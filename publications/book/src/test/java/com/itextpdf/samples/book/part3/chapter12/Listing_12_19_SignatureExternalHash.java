/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV

*/

package com.itextpdf.samples.book.part3.chapter12;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.samples.SignatureTest;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.IExternalDigest;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.test.annotations.type.SampleTest;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
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

@Category(SampleTest.class)
public class Listing_12_19_SignatureExternalHash extends SignatureTest {
    public static String SRC = "./src/test/resources/book/part3/chapter12/cmp_Listing_12_15_Signatures_hello.pdf";

    /**
     * A properties file that is PRIVATE.
     * You should make your own properties file and adapt this line.
     */
    public static String PATH
            = "./src/test/resources/encryption/key.properties";
    public static Properties properties = new Properties();

    public static String SIGNED1 = "./target/test/resources/book/part3/chapter12/Listing_12_19_Signatures_externalhash_1.pdf";
    public static String SIGNED2 = "./target/test/resources/book/part3/chapter12/Listing_12_19_Signatures_externalhash_2.pdf";
    public static String SIGNED3 = "./target/test/resources/book/part3/chapter12/Listing_12_19_Signatures_externalhash_3.pdf";
    public static String CMP_SIGNED1 = "./src/test/resources/book/part3/chapter12/cmp_Listing_12_19_Signatures_externalhash_1.pdf";

    public void signPdfDetached(String src, String dest) throws GeneralSecurityException, IOException {
        // Private key and certificate
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
        appearance.setReason("External hash example");
        appearance.setLocation("Foobar");
        appearance.setPageNumber(1);
        appearance.setPageRect(new Rectangle(72, 632, 200, 100));
        signer.setFieldName("sig");

        // digital signature
        IExternalSignature es = new PrivateKeySignature(pk, "SHA-256", "BC");
        IExternalDigest digest = new BouncyCastleDigest();
        signer.signDetached(digest, es, chain, null, null, null, 0, PdfSigner.CryptoStandard.CMS);
    }

    public static void main(String[] args) throws Exception {
        new Listing_12_19_SignatureExternalHash().manipulatePdf(SRC);
    }

    public void manipulatePdf(String src) throws IOException, GeneralSecurityException {
        Security.addProvider(new BouncyCastleProvider());
        properties.load(new FileInputStream(PATH));
        signPdfDetached(src, SIGNED1);
    }

    @Test
    public void runTest() throws Exception {
        Listing_12_19_SignatureExternalHash.main(null);

        String[] errors = new String[1];
        boolean error = false;

        HashMap<Integer, List<Rectangle>> ignoredAreas = new HashMap<Integer, List<Rectangle>>() {
            {
                put(1, Arrays.asList(new Rectangle(72, 632, 200, 100)));
            }
        };

        for (int i = 0; i < 1; i++) {
            String fileErrors = checkForErrors(SIGNED1, CMP_SIGNED1, "./target/test/resources/book/part3/chapter12/", ignoredAreas);
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
