/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV

*/

/**
 * Example written by Bruno Lowagie in answer to:
 * http://stackoverflow.com/questions/28418555/remove-page-reference-from-annotation0
 */
package com.itextpdf.samples.sandbox.stamper;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.NonTerminalFormFieldBuilder;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfTextFormField;
import com.itextpdf.forms.fields.TextFormFieldBuilder;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.samples.GenericTest;
import com.itextpdf.test.annotations.type.SampleTest;

import org.junit.experimental.categories.Category;

import java.io.File;

//参见：https://kb.itextpdf.com/itext/adding-fields-to-an-existing-form#Addingfieldstoanexistingform-addfieldandkids
//添加表单域（包括子域名）
@Category(SampleTest.class)
public class AddFieldAndKids extends GenericTest {
    public static final String DEST = "./target/test/resources/sandbox/stamper/add_field_and_kids.pdf";
    public static final String SRC = "./src/test/resources/pdfs/hello.pdf";

    public static void main(String[] args) throws Exception {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new AddExtraMargin().manipulatePdf(DEST);
    }

    @Override
    protected void manipulatePdf(String dest) throws Exception {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(SRC), new PdfWriter(DEST));

        PdfFormField personal =
                new NonTerminalFormFieldBuilder(pdfDoc, "personal").createNonTerminalFormField();
        PdfTextFormField name = new TextFormFieldBuilder(pdfDoc, "name")
                .setWidgetRectangle(new Rectangle(36, 760, 108, 30)).createText();
        name.setValue("");
        personal.addKid(name);
        PdfTextFormField password = new TextFormFieldBuilder(pdfDoc, "password")
                .setWidgetRectangle(new Rectangle(150, 760, 300, 30)).createText();
        password.setValue("");
        personal.addKid(password);

        PdfAcroForm.getAcroForm(pdfDoc, true).addField(personal, pdfDoc.getFirstPage());
        pdfDoc.close();
    }
}
