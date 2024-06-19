/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV

*/

package com.itextpdf.samples.book.part2.chapter08;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfFormAnnotation;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.RadioFormFieldBuilder;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.samples.GenericTest;
import com.itextpdf.test.annotations.type.SampleTest;
import org.junit.experimental.categories.Category;

@Category(SampleTest.class)
public class Listing_08_02_RadioButtons extends GenericTest {
    public static final String DEST = "./target/test/resources/book/part2/chapter08/Listing_08_02_RadioButtons.pdf";
    /**
     * Possible values of a Choice field.
     */
    public static final String[] LANGUAGES = {"English", "German", "French", "Spanish", "Dutch"};

    public static void main(String[] args) throws Exception {
        new Listing_08_02_RadioButtons().manipulatePdf(DEST);
    }

    @Override
    protected void manipulatePdf(String dest) throws Exception {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(DEST));
        Document doc = new Document(pdfDoc);
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA, PdfEncodings.WINANSI, PdfFontFactory.EmbeddingStrategy.PREFER_NOT_EMBEDDED);
        RadioFormFieldBuilder builder = new RadioFormFieldBuilder(pdfDoc,"language");
        PdfButtonFormField radioGroup = builder.createRadioGroup();
        radioGroup.setValue("");
        radioGroup.setFieldName("language");
        Rectangle rect = new Rectangle(40, 806, 60 - 40, 788 - 806);
        PdfFormAnnotation radio;
        for (int page = 1; page <= LANGUAGES.length; page++) {
            pdfDoc.addNewPage();
            radio = builder.createRadioButton( LANGUAGES[page - 1], rect);
            radio.setPage(page);
            doc.showTextAligned(new Paragraph(LANGUAGES[page - 1]).setFont(font).setFontSize(18),
                    70, 790, page, TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
            radio.getWidget().setColor(new DeviceGray(0.8f).getColorValue());
            radioGroup.addKid(radio);
        }
        PdfAcroForm.getAcroForm(pdfDoc, true).addField(radioGroup);
        doc.close();
    }
}
