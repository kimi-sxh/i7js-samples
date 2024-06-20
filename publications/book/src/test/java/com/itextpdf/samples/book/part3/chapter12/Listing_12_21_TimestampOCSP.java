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
import com.itextpdf.signatures.IOcspClient;
import com.itextpdf.signatures.ITSAClient;
import com.itextpdf.signatures.OcspClientBouncyCastle;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.TSAClientBouncyCastle;
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

@Ignore("Put property file with valid data")
@Category(SampleTest.class)
public class Listing_12_21_TimestampOCSP extends SignatureTest {
    public static String SRC = "./src/test/resources/book/part3/chapter12/cmp_Listing_12_15_Signatures_hello.pdf";
    public static String SIGNED0 = "./target/test/resources/book/part3/chapter12/Listing_12_21_TimestampOCSP_without.pdf";
    public static String SIGNED1 = "./target/test/resources/book/part3/chapter12/Listing_12_21_TimestampOCSP_ts.pdf";
    public static String SIGNED2 = "./target/test/resources/book/part3/chapter12/Listing_12_21_TimestampOCSP_ocsp.pdf";
    public static String SIGNED3 = "./target/test/resources/book/part3/chapter12/Listing_12_21_TimestampOCSP_ts_oscp.pdf";

    public static String SIGNED4 = "./target/test/resources/book/part3/chapter12/Listing_12_21_TimestampOCSP_ts_cert_invalid.pdf";

    public static String[] RESULT = {
            SIGNED0,
//            SIGNED1,
            SIGNED2
//            SIGNED3
    };
    public static String[] CMP_RESULT = {
            "./src/test/resources/book/part3/chapter12/cmp_Listing_12_21_TimestampOCSP_without.pdf",
//            SIGNED1,
            "./src/test/resources/book/part3/chapter12/cmp_Listing_12_21_TimestampOCSP_ocsp.pdf",
//            SIGNED3
    };

    /**
     * A properties file that is PRIVATE.
     * You should make your own properties file and adapt this line.
     */
    public static String PATH
            = "./src/test/resources/encryption/key.properties";

    public static Properties properties = new Properties();

    public void signPdf(String src, String dest, boolean withTS, boolean withOCSP)
            throws IOException, GeneralSecurityException {
        // Keystore and certificate chain
        String keystore = properties.getProperty("PRIVATE");
        String password = properties.getProperty("PASSWORD");

        this.signPdf(src, dest, withTS, withOCSP,keystore,password);
    }

    public void signExpirePdf(String src, String dest, boolean withTS, boolean withOCSP)
            throws IOException, GeneralSecurityException {
        // Keystore and certificate chain
        String expireKeystore = properties.getProperty("EXPIRE_PRIVATE");
        String expirePassword = properties.getProperty("EXPIRE_PASSWORD");

        this.signPdf(src, dest, withTS, withOCSP,expireKeystore,expirePassword);
    }

    public void signPdf(String src, String dest, boolean withTS, boolean withOCSP,String keystore,String password)
            throws IOException, GeneralSecurityException {
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(new FileInputStream(keystore), password.toCharArray());
        String alias = (String) ks.aliases().nextElement();
        PrivateKey pk = (PrivateKey) ks.getKey(alias, password.toCharArray());
        Certificate[] chain = ks.getCertificateChain(alias);
        // Creating the reader and the signer
        PdfReader reader = new PdfReader(src);
        StampingProperties stampingProperties = new StampingProperties();
        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), stampingProperties);
        // appearance
        PdfSignatureAppearance sap = signer.getSignatureAppearance();
        sap.setReason("I'm approving this.");
        sap.setLocation("Foobar");
        sap.setPageNumber(1);
        sap.setPageRect(new Rectangle(72, 632, 200, 100));
        signer.setFieldName("Signature");
        // preserve some space for the contents
        // digital signature
        IExternalSignature es = new PrivateKeySignature(pk, "SHA-256", "BC");
        // If we add a time stamp:
        ITSAClient tsc = null;
        if (withTS) {
            //时间戳地址使用正向代理
            String tsa_url = properties.getProperty("TSA");
            String tsa_login = properties.getProperty("TSA_LOGIN");
            String tsa_passw = properties.getProperty("TSA_PASSWORD");
            //正向代理
//            tsc = new TSAClientBouncyCastle(tsa_url,"ON","127.0.0.1","8889", tsa_login, tsa_passw);
            tsc = new TSAClientBouncyCastle(tsa_url,"OFF","","", tsa_login, tsa_passw);
        }
        // If we use OCSP:
        IOcspClient ocsp = null;
        if (withOCSP) {
            ocsp = new OcspClientBouncyCastle(null);
        }
        IExternalDigest digest = new BouncyCastleDigest();
        signer.signDetached(digest, es, chain, null, ocsp, tsc, 0, PdfSigner.CryptoStandard.CMS);
    }


    public static void main(String[] args) throws Exception {
        new Listing_12_21_TimestampOCSP().manipulatePdf(SRC);
    }

    protected void manipulatePdf(String src) throws IOException, GeneralSecurityException {
        Security.addProvider(new BouncyCastleProvider());
        properties.load(new FileInputStream(PATH));
        signPdf(src, SIGNED0, false, false);
        signPdf(src, SIGNED1, true, false);
        signPdf(src, SIGNED2, false, true);
        signPdf(src, SIGNED3, true, true);

        //过期证书签名 校验
        signExpirePdf(src, SIGNED4, true, false);
    }

    @Test
    public void runTest() throws Exception {
        Listing_12_21_TimestampOCSP.main(null);

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
