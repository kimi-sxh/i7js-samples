/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV

*/

/**
 * Example written by Bruno Lowagie in answer to:
 * http://stackoverflow.com/questions/29016333/pushbuttonfield-with-double-byte-character
 */
package com.itextpdf.samples.sandbox.acroforms;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PushButtonFormFieldBuilder;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.samples.GenericTest;
import com.itextpdf.test.annotations.type.SampleTest;

import java.io.File;

import org.junit.experimental.categories.Category;

@Category(SampleTest.class)
public class CreateJapaneseButton extends GenericTest {
    public static final String DEST = "./target/test/resources/sandbox/acroforms/create_japanese_button.pdf";
    public static final String FONT = "./src/test/resources/font/FreeSans.ttf";
    // あ き ら characters
    public static final String JAPANESE_TEXT = "\u3042\u304d\u3089";

    public static void main(String[] args) throws Exception {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new CreateJapaneseButton().manipulatePdf(DEST);
    }

    @Override
    protected void manipulatePdf(String dest) throws Exception {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.IDENTITY_H);
        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

        // Define the position of a button that measures 108 by 26
        Rectangle rect = new Rectangle(36, 780, 108, 26);
        PdfButtonFormField pushButton = new PushButtonFormFieldBuilder(pdfDoc, "japanese")
                .setWidgetRectangle(rect).setCaption(JAPANESE_TEXT)
                .createPushButton();
        pushButton.setFont(font);
        pushButton.setFontSize(12f);
        form.addField(pushButton);
        pdfDoc.close();
    }
}
