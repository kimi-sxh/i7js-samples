/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV

*/

package com.itextpdf.samples.book.part3.chapter11;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.font.FontFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.annotations.type.SampleTest;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.samples.GenericTest;

import org.junit.Ignore;
import org.junit.experimental.categories.Category;

@Ignore
@Category(SampleTest.class)
public class Listing_11_19_FontFactory extends GenericTest {
    public static final String DEST
            = "./target/test/resources/book/part3/chapter11/Listing_11_19_FontFactory.pdf";

    public static void main(String[] args) throws Exception {
        new Listing_11_19_FontFactory().manipulatePdf(DEST);
    }

    @Override
    protected void manipulatePdf(String dest) throws Exception {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
        Document doc = new Document(pdfDoc);
        PdfFont font =  PdfFontFactory.createStandardFont(FontConstants.TIMES_ROMAN);
        doc.add(new Paragraph("Times-Roman").setFont(font));
        PdfFont fontBold = PdfFontFactory.createStandardFont(FontConstants.TIMES_BOLD);
        doc.add(new Paragraph("Times-Roman, Bold").setFont(fontBold));
        doc.add(new Paragraph("\n"));

        FontFactory.register("c:/windows/fonts/GARABD.TTF", "my_bold_font");
        PdfFont myBoldFont = PdfFontFactory.createRegisteredFont("my_bold_font");
        doc.add(new Paragraph(font.getFontProgram().getFontNames().getFontName()).setFont(myBoldFont));
        String[][] name = myBoldFont.getFontProgram().getFontNames().getFullName();
        for (int i = 0; i < name.length; i++) {
            doc.add(new Paragraph(name[i][3] + " (" + name[i][0]
                    + "; " + name[i][1] + "; " + name[i][2] + ")"));
        }

        PdfFont myBoldFont2 = PdfFontFactory.createRegisteredFont("Garamond Vet");
        doc.add(new Paragraph("Garamond Vet").setFont(myBoldFont2));
        doc.add(new Paragraph("\n"));

        doc.add(new Paragraph("Registered fonts:"));
        FontFactory.registerDirectory("resources/fonts");
        for (String f : FontFactory.getRegisteredFonts()) {
            doc.add(new Paragraph(f).setFont(PdfFontFactory.createRegisteredFont(f, "", true)));
        }
        doc.add(new Paragraph("\n"));

        // TODO CMR font issue
        // PdfFont cmr10 = PdfFont.createRegisteredFont(pdfDoc, "cmr10");
        // cmr10.getFontProgram().getFontNames().setFontName("Computer Modern Regular");
        // PdfFont computerModern = PdfFont.createRegisteredFont(pdfDoc,"Computer Modern Regular", "", true);
        // doc.add(new Paragraph("Computer Modern").setFont(computerModern));
        // doc.add(new Paragraph("\n"));

        FontFactory.registerSystemDirectories();
        for (String f : FontFactory.getRegisteredFamilies()) {
            doc.add(new Paragraph(f));
        }
        doc.add(new Paragraph("\n"));

        PdfFont garamond = PdfFontFactory.createRegisteredFont("garamond", PdfEncodings.WINANSI, true);
        doc.add(new Paragraph("Garamond").setFont(garamond));
        PdfFont garamondItalic = PdfFontFactory.createRegisteredFont("Garamond", PdfEncodings.WINANSI, true, FontConstants.ITALIC);
        // PdfFont garamondItalic
        //         = FontFactory.getFont("Garamond", BaseFont.WINANSI, BaseFont.EMBEDDED, 12, Font.ITALIC);
        doc.add(new Paragraph("Garamond-Italic").setFont(garamondItalic).setFontSize(12));

        doc.close();
    }
}
