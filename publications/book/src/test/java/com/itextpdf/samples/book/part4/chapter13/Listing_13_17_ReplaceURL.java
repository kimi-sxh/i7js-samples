/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV

*/

package com.itextpdf.samples.book.part4.chapter13;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.*;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.samples.GenericTest;
import com.itextpdf.samples.book.part2.chapter08.Listing_08_14_ChildFieldEvent;
import com.itextpdf.test.annotations.type.SampleTest;
import org.junit.Assert;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

@Category(SampleTest.class)
public class Listing_13_17_ReplaceURL extends GenericTest {
    // DEST is alias for RESULT2
    public static final String DEST = "./target/test/resources/book/part4/chapter13/Listing_13_17_ReplaceURL.pdf";
    public static final String[] RESULT = {
            "./target/test/resources/book/part4/chapter13/Listing_13_17_ReplaceURL_submit1.pdf",
            "./target/test/resources/book/part4/chapter13/Listing_13_17_ReplaceURL_submit2.pdf"
    };
    public static final String[] CMP_RESULT = {
            "./src/test/resources/book/part4/chapter13/cmp_Listing_13_17_ReplaceURL_submit1.pdf",
            "./src/test/resources/book/part4/chapter13/cmp_Listing_13_17_ReplaceURL_submit2.pdf"
    };

    public void createPdf(String dest) throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
        Document doc = new Document(pdfDoc);

        PdfFormField personal =
                new NonTerminalFormFieldBuilder(pdfDoc, "personal").createNonTerminalFormField();
        personal.setFieldName("personal");
        Table table = new Table(3);
        Cell cell;

        table.addCell("Your name:");
        cell = new Cell(1, 2);
        PdfTextFormField field = new TextFormFieldBuilder(pdfDoc, "name").setWidgetRectangle(new Rectangle(0, 0))
                .createText();
        field.setFontSize(12);
        personal.addKid(field);
        cell.setNextRenderer(new Listing_08_14_ChildFieldEvent(field, 1, cell));
        table.addCell(cell);
        table.addCell("Login:");
        cell = new Cell();
        field = new TextFormFieldBuilder(pdfDoc, "loginname")
                .setWidgetRectangle(new Rectangle(0, 0)).createText();
        field.setFontSize(12);
        personal.addKid(field);
        cell.setNextRenderer(new Listing_08_14_ChildFieldEvent(field, 1, cell));
        table.addCell(cell);
        cell = new Cell();
        field = new TextFormFieldBuilder(pdfDoc, "password")
                .setWidgetRectangle(new Rectangle(0, 0)).createText();
        field.setFieldFlag(PdfFormField.FF_PASSWORD);
        field.setFontSize(12);
        personal.addKid(field);
        cell.setNextRenderer(new Listing_08_14_ChildFieldEvent(field, 1, cell));
        table.addCell(cell);
        table.addCell("Your motivation:");
        cell = new Cell(1, 2);
        cell.setHeight(60);
        field = new TextFormFieldBuilder(pdfDoc, "reason")
                .setWidgetRectangle(new Rectangle(0, 0)).createText();
        field.setMultiline(true);
        field.setFontSize(12);
        personal.addKid(field);
        cell.setNextRenderer(new Listing_08_14_ChildFieldEvent(field, 1, cell));
        table.addCell(cell);
        doc.add(table);
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        form.addField(personal);

        PdfButtonFormField button1 = new PushButtonFormFieldBuilder(pdfDoc, "post")
                .setWidgetRectangle(new Rectangle(90, 660, 50, 30)).setCaption("POST").createPushButton();
        button1.getFirstFormAnnotation().setBackgroundColor(new DeviceGray(0.7f));
        button1.setValue("POST");
        button1.getFirstFormAnnotation().setVisibility(PdfFormAnnotation.VISIBLE_BUT_DOES_NOT_PRINT);
        button1.getFirstFormAnnotation().setAction(PdfAction.createSubmitForm("/book/request", null,
                PdfAction.SUBMIT_HTML_FORMAT | PdfAction.SUBMIT_COORDINATES));
        form.addField(button1);

        doc.close();
    }

    public void changePdf(String src, String dest) throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(dest));
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        PdfFormField field = form.getField("post");
        PdfDictionary action = field.getPdfObject().getAsDictionary(PdfName.A);
        PdfDictionary f = action.getAsDictionary(PdfName.F);
        f.put(PdfName.F, new PdfString("http://itextpdf.com:8080/book/request"));
        pdfDoc.close();
    }

    public static void main(String args[]) throws IOException, SQLException, XMPException {
        new Listing_13_17_ReplaceURL().manipulatePdf(DEST);
    }

    @Override
    protected void manipulatePdf(String dest) throws IOException, SQLException, XMPException {
        createPdf(RESULT[0]);
        changePdf(RESULT[0], RESULT[1]);
    }

    @Override
    protected void comparePdf(String dest, String cmp) throws Exception {
        CompareTool compareTool = new CompareTool();
        String outPath;
        for (int i = 0; i < RESULT.length; i++) {
            outPath = new File(RESULT[i]).getParent();
            if (compareXml) {
                if (!compareTool.compareXmls(RESULT[i], CMP_RESULT[i])) {
                    addError("The XML structures are different.");
                }
            } else {
                if (compareRenders) {
                    addError(compareTool.compareVisually(RESULT[i], CMP_RESULT[i], outPath, differenceImagePrefix));
                    addError(compareTool.compareLinkAnnotations(dest, cmp));
                } else {
                    addError(compareTool.compareByContent(RESULT[i], CMP_RESULT[i], outPath, differenceImagePrefix));
                }
                addError(compareTool.compareDocumentInfo(RESULT[i], CMP_RESULT[i]));
            }
        }

        if (errorMessage != null) Assert.fail(errorMessage);
    }
}
