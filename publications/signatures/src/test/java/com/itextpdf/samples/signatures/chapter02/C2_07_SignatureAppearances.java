/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV

*/

/*
 * This class is part of the white paper entitled
 * "Digital Signatures for PDF documents"
 * written by Bruno Lowagie
 *
 * For more info, go to: http://itextpdf.com/learn
 */
package com.itextpdf.samples.signatures.chapter02;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.samples.SignatureTest;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalDigest;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.styledxmlparser.resolver.resource.ResourceResolver;
import com.itextpdf.svg.converter.SvgConverter;
import com.itextpdf.svg.element.SvgImage;
import com.itextpdf.svg.processors.ISvgProcessorResult;
import com.itextpdf.svg.processors.impl.DefaultSvgProcessor;
import com.itextpdf.svg.processors.impl.SvgConverterProperties;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.xobject.SvgImageXObject;
import com.itextpdf.test.annotations.type.SampleTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import static org.junit.Assert.fail;

//对已有的签名域做签名
// 文字和图片自定义外观怎么排布  #setRenderingMode
@Category(SampleTest.class)
public class C2_07_SignatureAppearances extends SignatureTest {
    public static final String KEYSTORE = "./src/test/resources/encryption/ks";
    public static final char[] PASSWORD = "password".toCharArray();
    public static final String SRC = "./src/test/resources/pdfs/hello_to_sign.pdf";
    public static final String DEST = "./target/test/resources/signatures/chapter02/signature_appearance_%s.pdf";
    public static final String IMG = "./src/test/resources/img/1t3xt.gif";
    private static final String FONT_DIR = "./src/test/resources/font/";

    /**
     * <b>概要：</b>
     *  对已有的签名域做签名
     * <b>作者：</b>suxh</br>
     * <b>日期：</b>2024/6/26 14:29</br>
     * @param src 源文件路径
     * @param name 已有签名域的域名
     * @param dest 生成目标文件路径
     * @param chain 证书信息
     * @param pk 私钥
     * @param digestAlgorithm 摘要算法
     * @param provider 算法提供者
     * @param subfilter 签名字典subfilter
     * @param reason
     * @param location
     * @param renderingMode 渲染模式
     * @param image 图片数据
     * @return
     **/
    public void sign2SignField(String src, String name, String dest,
                     Certificate[] chain, PrivateKey pk,
                     String digestAlgorithm, String provider,
                     PdfSigner.CryptoStandard subfilter,
                     String reason, String location, PdfSignatureAppearance.RenderingMode renderingMode,
                     ImageData image)
            throws GeneralSecurityException, IOException {
        // Creating the reader and the signer
        PdfReader reader = new PdfReader(src);
        StampingProperties stampingProperties = new StampingProperties().useAppendMode();
        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), stampingProperties);
        // Creating the appearance
        PdfSignatureAppearance appearance = signer.getSignatureAppearance();
        appearance.setReason(reason);
        appearance.setLocation(location);
        appearance.setReuseAppearance(false);
        signer.setFieldName(name);
        appearance.setLayer2Text("Signed on " + new Date().toString());
        appearance.setRenderingMode(renderingMode);
        appearance.setSignatureGraphic(image);
        // Creating the signature
        PrivateKeySignature pks = new PrivateKeySignature(pk, digestAlgorithm, provider);
        IExternalDigest digest = new BouncyCastleDigest();
        signer.signDetached(digest, pks, chain, null, null, null, 0, subfilter);
    }

    public void signSVG2SignField(String src, String name, String dest,
                     Certificate[] chain, PrivateKey pk,
                     String digestAlgorithm, String provider,
                     PdfSigner.CryptoStandard subfilter,
                     String reason, String location, PdfSignatureAppearance.RenderingMode renderingMode,
                     SvgImage svgImage)
            throws GeneralSecurityException, IOException {
        // Creating the reader and the signer
        PdfReader reader = new PdfReader(src);
        StampingProperties stampingProperties = new StampingProperties().useAppendMode();
        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), stampingProperties);
        signer.setFieldName(name);
        // Creating the appearance
        PdfSignatureAppearance appearance = signer.getSignatureAppearance();
        appearance.setReason(reason);
        appearance.setLocation(location);
        appearance.setReuseAppearance(false);
        appearance.setLayer2Text("Signed on " + new Date().toString());
        appearance.setRenderingMode(renderingMode);
        appearance.setSignatureGraphic(svgImage);
        // Creating the signature
        PrivateKeySignature pks = new PrivateKeySignature(pk, digestAlgorithm, provider);
        IExternalDigest digest = new BouncyCastleDigest();
        signer.signDetached(digest, pks, chain, null, null, null, 0, subfilter);
    }

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();

        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(new FileInputStream(KEYSTORE), PASSWORD);
        String alias = ks.aliases().nextElement();
        PrivateKey pk = (PrivateKey) ks.getKey(alias, PASSWORD);
        Certificate[] chain = ks.getCertificateChain(alias);
        ImageData image = ImageDataFactory.create(IMG);
        C2_07_SignatureAppearances app = new C2_07_SignatureAppearances();
//        app.sign2SignField(SRC, "Signature1", String.format(DEST, 1), chain, pk,
//                DigestAlgorithms.SHA256, provider.getName(), PdfSigner.CryptoStandard.CMS,
//                "Appearance 1", "Ghent", PdfSignatureAppearance.RenderingMode.DESCRIPTION, null);
//        app.sign2SignField(SRC, "Signature1", String.format(DEST, 2), chain, pk,
//                DigestAlgorithms.SHA256, provider.getName(), PdfSigner.CryptoStandard.CMS,
//                "Appearance 2", "Ghent", PdfSignatureAppearance.RenderingMode.NAME_AND_DESCRIPTION, null);
//        app.sign2SignField(SRC, "Signature1", String.format(DEST, 3), chain, pk,
//                DigestAlgorithms.SHA256, provider.getName(), PdfSigner.CryptoStandard.CMS,
//                "Appearance 3", "Ghent", PdfSignatureAppearance.RenderingMode.GRAPHIC_AND_DESCRIPTION, image);
//        app.sign2SignField(SRC, "Signature1", String.format(DEST, 4), chain, pk,
//                DigestAlgorithms.SHA256, provider.getName(), PdfSigner.CryptoStandard.CMS,
//                "Appearance 4", "Ghent", PdfSignatureAppearance.RenderingMode.GRAPHIC, image);

        //sign svg(svg图片的签名)
        /*
         * ①svg包含中文名字没法显示(已解决，后面再说)
         * ②目前只针对renderingMode为graphics做改造，其他渲染模式没有改造 改造参见PdfSignatureAppearance#setContent
         * ③针对svg图片切割问题需要研究
         **/
        //String svgFileName = "c:\\wechat.svg";//纯画笔画出来的svg
        String svgFileName = "c:\\测试.svg";//包含中文
        SvgConverterProperties svgConverterProperties = new SvgConverterProperties() ;
        FontProgramFactory.registerFont(FONT_DIR + "simsun.ttf", "simsun");
        PdfFont simsun = PdfFontFactory.createRegisteredFont("simsun");
        FontProvider fontProvider = new FontProvider();
        fontProvider.addFont(simsun.getFontProgram());
        svgConverterProperties.setFontProvider(fontProvider);
        INode parsedSvg = SvgConverter.parse(new FileInputStream(svgFileName));
        ISvgProcessorResult result = new DefaultSvgProcessor().process(parsedSvg, svgConverterProperties);
        ISvgNodeRenderer topSvgRenderer = result.getRootRenderer();
        float[] wh = SvgConverter.extractWidthAndHeight(topSvgRenderer);
        SvgImageXObject svgImageXObject = new SvgImageXObject(new Rectangle(0, 0, wh[0], wh[1]),
                result, new ResourceResolver("c:\\"));
        app.signSVG2SignField(SRC, "Signature1", String.format(DEST, "svg"), chain, pk,
                DigestAlgorithms.SHA256, provider.getName(), PdfSigner.CryptoStandard.CMS,
                "Appearance 4", "Ghent", PdfSignatureAppearance.RenderingMode.GRAPHIC, new SvgImage(svgImageXObject));
    }

    @Test
    public void runTest() throws IOException, InterruptedException, GeneralSecurityException {
        new File("./target/test/resources/signatures/chapter02/").mkdirs();
        C2_07_SignatureAppearances.main(null);

        String[] resultFiles = new String[]{"signature_appearance_1.pdf", "signature_appearance_2.pdf",
                "signature_appearance_3.pdf", "signature_appearance_4.pdf"};

        String destPath = String.format(outPath, "chapter02");
        String comparePath = String.format(cmpPath, "chapter02");

        String[] errors = new String[resultFiles.length];
        boolean error = false;

        HashMap<Integer, List<Rectangle>> ignoredAreas = new HashMap<Integer, List<Rectangle>>() {
            {
                put(1, Arrays.asList(new Rectangle(46, 472, 287, 255)));
            }
        };

        for (int i = 0; i < resultFiles.length; i++) {
            String resultFile = resultFiles[i];
            String fileErrors = checkForErrors(destPath + resultFile, comparePath + "cmp_" + resultFile, destPath, ignoredAreas);
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