/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV

*/

package com.itextpdf.samples.book.part3.chapter11;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.font.FontSelector;
import com.itextpdf.samples.GenericTest;
import com.itextpdf.test.annotations.type.SampleTest;
import org.junit.Ignore;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

@Ignore("Font selector not implemented")
@Category(SampleTest.class)
public class Listing_11_20_FontSelectionExample extends GenericTest {
    public static final String DEST
            = "./target/test/resources/book/part3/chapter11/Listing_11_20_FontSelectionExample.pdf";
    public static final String TEXT
            = "These are the protagonists in 'Hero', a movie by Zhang Yimou:\n"
            + "\u7121\u540d (Nameless), \u6b98\u528d (Broken Sword), "
            + "\u98db\u96ea (Flying Snow), \u5982\u6708 (Moon), "
            + "\u79e6\u738b (the King), and \u9577\u7a7a (Sky).";

    public static void main(String[] agrs) throws Exception {
        new Listing_11_20_FontSelectionExample().manipulatePdf(DEST);
    }

    @Override
    protected void manipulatePdf(String dest) throws Exception {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
        Document doc = new Document(pdfDoc);
        FontProgram font1 = FontProgramFactory.createFont(StandardFonts.TIMES_ROMAN);
        //doc.setFont();
        List<PdfFont> substitutionFonts = new ArrayList<>();
        PdfFont msung = PdfFontFactory.createFont("MSung-Light", "UniCNS-UCS2-H", PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
        PdfFont roman = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
        substitutionFonts.add(roman);
        substitutionFonts.add(msung);

        char[] cc = TEXT.toCharArray();
        Paragraph paragraph = new Paragraph().setMarginTop(0.0f).setMarginBottom(0.0f);
        for(int i=0;i<cc.length;i++){
            for(int j=0;j< substitutionFonts.size();j++) {
                if('\n' == cc[i]) {//换行
                    doc.add(paragraph);
                    paragraph = new Paragraph().setMarginTop(0.0f).setMarginBottom(0.0f);
                }
                if(substitutionFonts.get(j).containsGlyph(cc[i])) {
                    paragraph.add(new Text(String.valueOf(cc[i]))
                            .setFont(substitutionFonts.get(j))
                            .setFontColor(j==0?ColorConstants.RED:ColorConstants.BLUE));
                    break;
                }
            }
        }
         doc.add(paragraph);
        // step 5: we close the document
        doc.close();
    }
}
