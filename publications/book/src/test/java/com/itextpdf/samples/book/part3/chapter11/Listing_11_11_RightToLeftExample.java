/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV

*/

package com.itextpdf.samples.book.part3.chapter11;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.BaseDirection;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.licensing.base.LicenseKey;
import com.itextpdf.samples.GenericTest;
import com.itextpdf.test.annotations.type.SampleTest;
import org.junit.experimental.categories.Category;

import java.io.File;

@Category(SampleTest.class)
public class Listing_11_11_RightToLeftExample extends GenericTest {

    public static final String DEST = "./target/test/resources/book/part3/chapter11/Listing_11_11_RightToLeftExample.pdf";
    /** A movie title. */
    public static final String MOVIE
            = "\u05d4\u05d0\u05e1\u05d5\u05e0\u05d5\u05ea \u05e9\u05dc \u05e0\u05d9\u05e0\u05d4";
    private static final String FONT = "src/test/resources/font/FreeSans.ttf";

    public static void main(String[] agrs) throws Exception {
        new Listing_11_11_RightToLeftExample().manipulatePdf(DEST);
    }

    @Override
    protected void manipulatePdf(String dest) throws Exception {
        //Load the license file to use advanced typography features
        LicenseKey.loadLicenseFile(new File("./target/test-classes/license/2ab957fc9e2de841907f74b3407de4080b3fe9607ec5241c17b189c8889982f4.json"));
        //LicenseKey.loadLicenseFile(System.getenv("ITEXT7_LICENSEKEY") + "/itextkey-typography.xml");
        //Initialize document
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
        Document document = new Document(pdfDoc, PageSize.A4);

        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
        document.add(new Paragraph("Movie title: Nina's Tragedies"));
        document.add(new Paragraph("directed by Savi Gabizon"));
        document.add(new Paragraph(MOVIE).setFont(font).setFontSize(14).setTextAlignment(TextAlignment.RIGHT).setBaseDirection(BaseDirection.RIGHT_TO_LEFT));

        //Close document
        document.close();
    }
}
