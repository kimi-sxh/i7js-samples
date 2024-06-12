/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV

*/

package com.itextpdf.samples.sandbox.acroforms;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfTextFormField;
import com.itextpdf.forms.fields.TextFormFieldBuilder;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.ReaderProperties;
import com.itextpdf.samples.GenericTest;
import com.itextpdf.test.annotations.type.SampleTest;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.IOException;

@Category(SampleTest.class)
public class ReadOnlyField2 extends GenericTest {
    public static final String DEST = "./target/test/resources/sandbox/acroforms/read_only_field2.pdf";

    public static void main(String[] args) throws Exception {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new ReadOnlyField2().manipulatePdf(DEST);
    }

    @Override
    protected void manipulatePdf(String dest) throws Exception {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(new RandomAccessSourceFactory().createSource(createForm()),
                new ReaderProperties()), new PdfWriter(DEST));
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        form.getField("text1").setReadOnly(true).setValue("A B C D E F G H I J K L M N O P Q R S T U V W X Y Z");
        form.getField("text2").setReadOnly(true).setValue("A B C D E F G H I J K L M N O P Q R S T U V W X Y Z");
        form.getField("text3").setReadOnly(true).setValue("A B C D E F G H I J K L M N O P Q R S T U V W X Y Z");
        form.getField("text4").setReadOnly(true).setValue("A B C D E F G H I J K L M N O P Q R S T U V W X Y Z");
        pdfDoc.close();
    }

    public byte[] createForm() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos));
        PdfFont font = PdfFontFactory.createFont();
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);

        Rectangle rect = new Rectangle(36, 770, 108, 36);
        PdfTextFormField textField1 = new TextFormFieldBuilder(pdfDoc, "text1")
                .setWidgetRectangle(rect).createText();
        textField1.setValue("text1").setFont(font).setFontSize(18f);
        // Being set as true, the field can contain multiple lines of text;
        // if false, the field's text is restricted to a single line.
        textField1.setMultiline(true);
        form.addField(textField1);

        rect = new Rectangle(148, 770, 108, 36);
        PdfTextFormField textField2 = new TextFormFieldBuilder(pdfDoc, "text2")
                .setWidgetRectangle(rect).createText();
        textField2.setValue("text2").setFont(font).setFontSize(18f);
        textField2.setMultiline(true);
        form.addField(textField2);

        rect = new Rectangle(36, 724, 108, 36);
        PdfTextFormField textField3 = new TextFormFieldBuilder(pdfDoc, "text3")
                .setWidgetRectangle(rect).createText();
        textField3.setValue("text3").setFont(font).setFontSize(18f);
        textField3.setMultiline(true);
        form.addField(textField3);

        rect = new Rectangle(148, 727, 108, 33);
        PdfTextFormField textField4 = new TextFormFieldBuilder(pdfDoc, "text4")
                .setWidgetRectangle(rect).createText();
        textField4.setValue("text4").setFont(font).setFontSize(18f);
        textField4.setMultiline(true);
        form.addField(textField4);

        pdfDoc.close();
        return baos.toByteArray();
    }
}
