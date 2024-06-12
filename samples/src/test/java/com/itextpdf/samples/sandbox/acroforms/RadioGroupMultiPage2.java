/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV

*/

/**
 * This example was written by Bruno Lowagie in answer to the following question:
 * http://stackoverflow.com/questions/30895930/issue-with-itext-radiocheckfield-when-displayed-on-multiple-pages
 */
package com.itextpdf.samples.sandbox.acroforms;

import com.itextpdf.forms.fields.*;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.test.annotations.type.SampleTest;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.renderer.CellRenderer;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.samples.GenericTest;

import java.io.File;
import java.io.FileOutputStream;

import org.junit.experimental.categories.Category;

//参见：https://kb.itextpdf.com/itext/create-fields-in-a-table#Createfieldsinatable-radiogroupmultipage2
@Category(SampleTest.class)
public class RadioGroupMultiPage2 extends GenericTest {
    public static final String DEST = "./target/test/resources/sandbox/acroforms/radio_group_multi_page2.pdf";

    public static void main(String[] args) throws Exception {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new RadioGroupMultiPage2().manipulatePdf(DEST);
    }

    @Override
    protected void manipulatePdf(String dest) throws Exception {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
        Document doc = new Document(pdfDoc);
        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

        // Radio buttons will be added to this radio group
        RadioFormFieldBuilder builder = new RadioFormFieldBuilder(pdfDoc, "answer");
        PdfButtonFormField radioGroup = builder.createRadioGroup();
        radioGroup.setValue("answer 1");

        Table table = new Table(2);
        Cell cell;
        for (int i = 0; i < 25; i++) {
            cell = new Cell().add(new Paragraph("Question " + i));
            table.addCell(cell);
            cell = new Cell().add(new Paragraph("Answer " + i));
            table.addCell(cell);
        }
        for (int i = 0; i < 25; i++) {
            cell = new Cell().add(new Paragraph("Radio: " + i));
            table.addCell(cell);
            cell = new Cell();
            cell.setNextRenderer(new AddRadioButtonRenderer(cell, radioGroup, "answer " + i));
            table.addCell(cell);
        }
        doc.add(table);

        form.addField(radioGroup);

        pdfDoc.close();
    }


    private class AddRadioButtonRenderer extends CellRenderer {
        protected PdfButtonFormField radioGroup;
        protected String value;

        public AddRadioButtonRenderer(Cell modelElement, PdfButtonFormField radioGroup, String value) {
            super(modelElement);
            this.radioGroup = radioGroup;
            this.value = value;
        }

        // If a renderer overflows on the next area, iText uses #getNextRenderer() method to create a new renderer for the overflow part.
        // If #getNextRenderer() isn't overridden, the default method will be used and thus the default rather than the custom
        // renderer will be created
        @Override
        public IRenderer getNextRenderer() {
            return new AddRadioButtonRenderer((Cell) modelElement, radioGroup, value);
        }

        @Override
        public void draw(DrawContext drawContext) {
            PdfDocument document = drawContext.getDocument();
            PdfAcroForm form = PdfFormCreator.getAcroForm(document, true);

            // Create a radio button that is added to a radio group.
            PdfFormAnnotation field = new RadioFormFieldBuilder(document, null)
                    .createRadioButton( value, getOccupiedAreaBBox());
            radioGroup.addKid(field);
            // This method merges field with its annotation and place it on the given page.
            // This method won't work if the field has no or more than one widget annotations.
            form.addFieldAppearanceToPage(field.getParentField(), document.getPage(getOccupiedArea().getPageNumber()));
        }
    }
}
