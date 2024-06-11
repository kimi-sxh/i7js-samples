/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV

*/
package com.itextpdf.samples.book.part3.chapter12;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.samples.SignatureTest;
import com.itextpdf.signatures.*;
import com.itextpdf.test.annotations.type.SampleTest;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.fail;

@Ignore
@Category(SampleTest.class)

public class Listing_12_13_SignatureField extends SignatureTest {
    public static String ORIGINAL = "./target/test/resources/book/part3/chapter12/Listing_12_13_SignatureField_unsigned.pdf";
    public static String SIGNED1 = "./target/test/resources/book/part3/chapter12/Listing_12_13_SignatureField_signed_1.pdf";
    public static String SIGNED2 = "./target/test/resources/book/part3/chapter12/Listing_12_13_SignatureField_signed_2.pdf";

    // aliases only for testing reasons
    public static String[] RESULT = new String[]{SIGNED1, SIGNED2};
    public static String[] CMP_RESULT = new String[]{
            "./src/test/resources/book/part3/chapter12/cmp_Listing_12_13_SignatureField_signed_1.pdf",
            "./src/test/resources/book/part3/chapter12/cmp_Listing_12_13_SignatureField_signed_2.pdf"
    };

    public static final String RESOURCE = "./src/test/resources/img/1t3xt.gif";

    /**
     * A properties file that is PRIVATE.
     * You should make your own properties file and adapt this line.
     */
    public static String PATH
            = "./src/test/resources/encryption/key.properties";

    public static Properties properties = new Properties();

    public void createPdf(String filename) throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));
        Document doc = new Document(pdfDoc);
        doc.add(new Paragraph("Hello World!"));
        PdfFormField field = PdfFormField.createSignature(pdfDoc, new Rectangle(72, 632, 200, 100));
        field.getWidgets().get(0)
                .setHighlightMode(PdfAnnotation.HIGHLIGHT_INVERT)
                .setFlags(PdfAnnotation.PRINT);
        field.setFieldName("mySig");
        field.setPage(1);

        PdfDictionary mkDictionary = field.getWidgets().get(0).getAppearanceCharacteristics();
        if (null == mkDictionary) {
            mkDictionary = new PdfDictionary();
        }
        PdfArray black = new PdfArray();
        black.add(new PdfNumber(ColorConstants.BLACK.getColorValue()[0]));
        black.add(new PdfNumber(ColorConstants.BLACK.getColorValue()[1]));
        black.add(new PdfNumber(ColorConstants.BLACK.getColorValue()[2]));
        mkDictionary.put(PdfName.BC, black);
        PdfArray white = new PdfArray();
        black.add(new PdfNumber(ColorConstants.WHITE.getColorValue()[0]));
        black.add(new PdfNumber(ColorConstants.WHITE.getColorValue()[1]));
        black.add(new PdfNumber(ColorConstants.WHITE.getColorValue()[2]));
        mkDictionary.put(PdfName.BG, white);
        field.getWidgets().get(0).setAppearanceCharacteristics(mkDictionary);

        PdfFormXObject xObject = new PdfFormXObject(new Rectangle(200, 100));
        PdfCanvas canvas = new PdfCanvas(xObject, pdfDoc);
        canvas
                .rectangle(0.5f, 0.5f, 199.5f, 99.5f)
                .stroke();
        // TODO Acrobat does not render new appearance (Foxit however does)
        field.getWidgets().get(0).setNormalAppearance(xObject.getPdfObject());
        PdfAcroForm.getAcroForm(pdfDoc, true).addField(field);
        doc.close();
    }

    public void signPdf(String src, String dest, boolean certified, boolean graphic) throws GeneralSecurityException, IOException {
        // private key and certificate
        String path = properties.getProperty("PRIVATE");
        String keystore_password = properties.getProperty("PASSWORD");
        String key_password = properties.getProperty("PASSWORD");
        KeyStore ks = KeyStore.getInstance("pkcs12", "BC");
        ks.load(new FileInputStream(path), keystore_password.toCharArray());
        String alias = ks.aliases().nextElement();
        PrivateKey pk = (PrivateKey) ks.getKey(alias, key_password.toCharArray());
        Certificate[] chain = ks.getCertificateChain(alias);
        // reader and signer
        PdfReader reader = new PdfReader(ORIGINAL);
        StampingProperties stampingProperties = new StampingProperties();
        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), stampingProperties);
        // appearance
        PdfSignatureAppearance appearance = signer.getSignatureAppearance();
        signer.setFieldName("mySig");
        appearance.setReason("It's personal.");
        appearance.setLocation("Foobar");
        if (graphic) {
            appearance.setSignatureGraphic(ImageDataFactory.create(RESOURCE));
            appearance.setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC);
        }
        if (certified) {
            // TODO DEVSIX-488
            signer.setCertificationLevel(PdfSigner.CERTIFIED_NO_CHANGES_ALLOWED);
        }
        // signature
        IExternalSignature es = new PrivateKeySignature(pk, "SHA-256", "BC");
        IExternalDigest digest = new BouncyCastleDigest();
        signer.signDetached(digest, es, chain, null, null, null, 0, PdfSigner.CryptoStandard.CMS);
    }

    public static void main(String[] args) throws Exception {
        new Listing_12_13_SignatureField().manipulatePdf();
    }

    public void manipulatePdf() throws IOException, GeneralSecurityException {
        Security.addProvider(new BouncyCastleProvider());
        properties.load(new FileInputStream(PATH));
        createPdf(ORIGINAL);
        signPdf(ORIGINAL, SIGNED1, false, false);
        signPdf(ORIGINAL, SIGNED2, true, true);
    }

    @Test
    public void runTest() throws Exception {
        Listing_12_13_SignatureField.main(null);

        String[] errors = new String[RESULT.length];
        boolean error = false;

        HashMap<Integer, List<Rectangle>> ignoredAreas = new HashMap<Integer, List<Rectangle>>() {
            {
                put(1, Arrays.asList(new Rectangle(72, 632, 200, 100)));
            }
        };

        for (int i = 0; i < RESULT.length; i++) {
            String fileErrors = checkForErrors(RESULT[i], CMP_RESULT[i], "./target/test/resources/book/part3/chapter12/", ignoredAreas);
            if (fileErrors != null) {
                errors[i] = fileErrors;
                error = true;
            }
        }

        if (error) {
            fail(accumulateErrors(errors));
        }
    }
}
