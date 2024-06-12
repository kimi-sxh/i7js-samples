/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV

*/

/**
 * Example written by Bruno Lowagie in answer to:
 * http://stackoverflow.com/questions/24301578/align-acrofields-in-java
 */
package com.itextpdf.samples.sandbox.acroforms;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.samples.GenericTest;
import com.itextpdf.test.annotations.type.SampleTest;

import org.junit.experimental.categories.Category;

import java.io.File;
import java.util.Map;

//参见：https://kb.itextpdf.com/itext/changing-the-alignment-of-a-field
//怎么改变field的对齐方式
@Category(SampleTest.class)
public class AlignField extends GenericTest {
    public static final String DEST = "./target/test/resources/sandbox/acroforms/align_field.pdf";
    public static final String SRC = "./src/test/resources/pdfs/subscribe.pdf";

    public static void main(String[] args) throws Exception {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new AlignField().manipulatePdf(DEST);
    }

    @Override
    protected void manipulatePdf(String dest) throws Exception {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SRC), new PdfWriter(DEST));
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);

        Map<String, PdfFormField> fields = form.getAllFormFields();
        PdfFormField field;

        field = fields.get("personal.name");
        field.setJustification(TextAlignment.LEFT);
        field.setValue("Test");

        field = fields.get("personal.loginname");
        field.setJustification(TextAlignment.CENTER);
        field.setValue("Test");

        field = fields.get("personal.password");
        field.setJustification(TextAlignment.RIGHT);
        field.setValue("Test");

        field = fields.get("personal.reason");
        field.setValue("Test");

        pdfDoc.close();
    }
}
