/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV

*/

package com.itextpdf.samples.book.part3.chapter12;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.samples.SignatureTest;
import com.itextpdf.signatures.*;
import com.itextpdf.test.annotations.type.SampleTest;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.*;

import static org.junit.Assert.fail;

@Category(SampleTest.class)
public class Listing_12_15_Signatures extends SignatureTest {
    public static String ORIGINAL = "./target/test/resources/book/part3/chapter12/Listing_12_15_Signatures_hello.pdf";
    public static String SIGNED1 = "./target/test/resources/book/part3/chapter12/Listing_12_15_Signatures_signature_1.pdf";
    public static String SIGNED2 = "./target/test/resources/book/part3/chapter12/Listing_12_15_Signatures_signature_2.pdf";
    public static String VERIFICATION = "./target/test/resources/book/part3/chapter12/Listing_12_15_Signatures_verify.txt";
    public static String REVISION = "./target/test/resources/book/part3/chapter12/Listing_12_15_Signatures_revision_1.pdf";

    public static String[] RESULT = new String[]{SIGNED1, SIGNED2, REVISION};
    public static String[] CMP_RESULT = new String[]{
            "./src/test/resources/book/part3/chapter12/cmp_Listing_12_15_Signatures_signature_1.pdf",
            "./src/test/resources/book/part3/chapter12/cmp_Listing_12_15_Signatures_signature_2.pdf",
            "./src/test/resources/book/part3/chapter12/cmp_Listing_12_15_Signatures_revision_1.pdf"
    };

    /**
     * A properties file that is PRIVATE.
     * You should make your own properties file and adapt this line.
     */
    public static String PATH
            = "./src/test/resources/encryption/key.properties";

    public static Properties properties = new Properties();
    public static final String RESOURCE = "./src/test/resources/img/logo.gif";

    public void createPdf(String filename) throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));
        Document doc = new Document(pdfDoc);
        doc.add(new Paragraph("Hello World!"));
        doc.close();
    }

    public void signPdfFirstTime(String src, String dest)
            throws IOException, GeneralSecurityException {
        String path = properties.getProperty("PRIVATE");
        String keystore_password = properties.getProperty("PASSWORD");
        String key_password = properties.getProperty("PASSWORD");
        KeyStore ks = KeyStore.getInstance("pkcs12", "BC");
        ks.load(new FileInputStream(path), keystore_password.toCharArray());
        String alias = ks.aliases().nextElement();
        PrivateKey pk = (PrivateKey) ks.getKey(alias, key_password.toCharArray());
        Certificate[] chain = ks.getCertificateChain(alias);
        // reader and signer
        PdfReader reader = new PdfReader(src);
        StampingProperties stampingProperties = new StampingProperties();
        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), stampingProperties);
        // appearance
        PdfSignatureAppearance appearance = signer.getSignatureAppearance();
        appearance.setImage(ImageDataFactory.create(RESOURCE));
        appearance.setReason("I've written this.");
        appearance.setLocation("Foobar");
        appearance.setPageRect(new Rectangle(72, 632, 150, 150));
        appearance.setPageNumber(1);
        signer.setFieldName("first");
        // digital signature
        IExternalSignature es = new PrivateKeySignature(pk, "SHA-256", "BC");
        IExternalDigest digest = new BouncyCastleDigest();
        signer.signDetached(digest, es, chain, null, null, null, 0, PdfSigner.CryptoStandard.CMS);
    }

    public void signPdfSecondTime(String src, String dest)
            throws IOException, GeneralSecurityException {
        String path = "./src/test/resources/encryption/.keystore";
        String keystore_password = "f00b4r";
        String key_password = "f1lmf3st";
        String alias = "foobar";
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(new FileInputStream(path), keystore_password.toCharArray());
        PrivateKey pk = (PrivateKey) ks.getKey(alias, key_password.toCharArray());
        Certificate[] chain = ks.getCertificateChain(alias);
        // reader and signer
        PdfReader reader = new PdfReader(src);
        StampingProperties stampingProperties = new StampingProperties().useAppendMode();
        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), stampingProperties);
        // appearance
        PdfSignatureAppearance appearance = signer.getSignatureAppearance();
        appearance.setReason("I'm approving this.");
        appearance.setLocation("Foobar");
        appearance.setPageRect(new Rectangle(240, 632, 150, 150));
        appearance.setPageNumber(1);
        signer.setFieldName("second");
        // digital signature
        IExternalSignature es = new PrivateKeySignature(pk, "SHA-256", "BC");
        IExternalDigest digest = new BouncyCastleDigest();
        signer.signDetached(digest, es, chain, null, null, null, 0, PdfSigner.CryptoStandard.CMS);
    }

    public void verifySignatures() throws GeneralSecurityException, IOException {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, null);
        CertificateFactory cf = CertificateFactory.getInstance("X509");
        FileInputStream is1 = new FileInputStream(properties.getProperty("ROOTCERT"));
        X509Certificate cert1 = (X509Certificate) cf.generateCertificate(is1);
        ks.setCertificateEntry("cacert", cert1);
        FileInputStream is2 = new FileInputStream("./src/test/resources/encryption/foobar.cer");
        X509Certificate cert2 = (X509Certificate) cf.generateCertificate(is2);
        ks.setCertificateEntry("foobar", cert2);

        PrintWriter out = new PrintWriter(new FileOutputStream(VERIFICATION));
        PdfReader reader = new PdfReader(SIGNED2);
        PdfDocument pdfDoc = new PdfDocument(reader);
        PdfAcroForm af = PdfAcroForm.getAcroForm(pdfDoc, false);
        SignatureUtil signUtil = new SignatureUtil(pdfDoc);
        List<String> names = signUtil.getSignatureNames();
        for (String name : names) {
            out.println("Signature name: " + name);
            out.println("Signature covers whole document: " + signUtil.signatureCoversWholeDocument(name));
            out.println("Document revision: " + signUtil.getRevision(name) + " of " + signUtil.getTotalRevisions());
            PdfPKCS7 pk = signUtil.readSignatureData(name);
            Calendar cal = pk.getSignDate();
            Certificate[] pkc = pk.getCertificates();
            out.println("Subject: " + CertificateInfo.getSubjectFields(pk.getSigningCertificate()));
            out.println("Revision modified: " + !pk.verifySignatureIntegrityAndAuthenticity());
            List<VerificationException> errors = CertificateVerification.verifyCertificates(pkc, ks, null, cal);
            if (errors.size() == 0) {
                out.println("Certificates verified against the KeyStore");
            } else {
                out.println(errors);
            }
        }
        out.flush();
        out.close();
    }

    public void extractFirstRevision() throws IOException {
        PdfReader reader = new PdfReader(SIGNED2);
        PdfDocument pdfDoc = new PdfDocument(reader);
        SignatureUtil signUtil = new SignatureUtil(pdfDoc);
        FileOutputStream os = new FileOutputStream(REVISION);
        byte bb[] = new byte[1028];
        InputStream ip = signUtil.extractRevision("first");
        int n = 0;
        while ((n = ip.read(bb)) > 0)
            os.write(bb, 0, n);
        os.close();
        ip.close();
    }

    public static void main(String[] args) throws Exception {
        new Listing_12_15_Signatures().manipulatePdf(ORIGINAL);
    }

    public void manipulatePdf(String src) throws IOException, GeneralSecurityException {
        Security.addProvider(new BouncyCastleProvider());
        properties.load(new FileInputStream(PATH));
        createPdf(src);
        signPdfFirstTime(src, SIGNED1);
        signPdfSecondTime(SIGNED1, SIGNED2);
        verifySignatures();
        extractFirstRevision();
    }

    @Test
    public void runTest() throws Exception {
        new File("./target/test/resources/book/part3/chapter12/").mkdirs();
        Listing_12_15_Signatures.main(null);

        String[] errors = new String[RESULT.length];
        boolean error = false;

        // Compare the documents only in signature data
        for (int i = 0; i < RESULT.length; i++) {
            compareSignatures(RESULT[i], CMP_RESULT[i]);
            if (getErrorMessage() != null) {
                errors[i] = getErrorMessage();
                error = true;
            }
        }

        if (error) {
            fail(accumulateErrors(errors));
        }
    }

    @Override
    protected void initKeyStoreForVerification(KeyStore ks) throws IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException {
        super.initKeyStoreForVerification(ks);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate foobarCert = cf.generateCertificate(new FileInputStream("./src/test/resources/encryption/foobar.cer"));
        ks.setCertificateEntry("foobar", foobarCert);
    }
}
