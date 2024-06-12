/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV

*/

/**
 * This example was written by Bruno Lowagie in answer to the following question:
 * http://stackoverflow.com/questions/30895930/issue-with-itext-radiocheckfield-when-displayed-on-multiple-pages
 */
package com.itextpdf.samples.sandbox.acroforms;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfFormAnnotation;
import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.forms.fields.RadioFormFieldBuilder;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
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

import java.io.File;

//参见：https://kb.itextpdf.com/itext/creating-form-fields#Creatingformfields-radiogroupmultipage1

@Category(SampleTest.class)
public class RadioGroupMultiPage1 extends GenericTest {
    public static final String DEST = "./target/test/resources/sandbox/acroforms/radio_group_multi_page1.pdf";
    public static final String[] LANGUAGES = {"English", "German", "French", "Spanish", "Dutch"};

    public static void main(String[] args) throws Exception {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new RadioGroupMultiPage1().manipulatePdf(DEST);
    }

    @Override
    protected void manipulatePdf(String dest) throws Exception {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
        Document doc = new Document(pdfDoc);
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        Rectangle rect = new Rectangle(40, 788, 20, 18);
        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

        String formFieldName = "Language";
        // Radio buttons will be added to this radio group
        PdfButtonFormField radioGroup = new RadioFormFieldBuilder(pdfDoc,formFieldName ).createRadioGroup();
        radioGroup.setValue("");

        for (int page = 1; page <= LANGUAGES.length; page++) {
            pdfDoc.addNewPage();

            // Create a radio button that is added to a radio group.
            PdfFormAnnotation field = new RadioFormFieldBuilder(pdfDoc, formFieldName)
                    .createRadioButton( LANGUAGES[page - 1], rect)
                    .setBorderWidth(1)
                    .setPage(page)
                    .setBorderColor(ColorConstants.BLACK);
            radioGroup.addKid(field);
            // Method specifies on which page the form field's widget must be shown.
            doc.showTextAligned(new Paragraph(LANGUAGES[page - 1]).setFont(font).setFontSize(18),
                    70, 786, page, TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
        }

        form.addField(radioGroup);

        doc.close();
    }
}
