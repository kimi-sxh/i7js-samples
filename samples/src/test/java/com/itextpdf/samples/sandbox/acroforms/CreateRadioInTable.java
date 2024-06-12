/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV

*/

/**
 * Example written by Bruno Lowagie in answer to:
 * http://stackoverflow.com/questions/29393050/itext-radiogroup-radiobuttons-across-multiple-pdfpcells
 */
package com.itextpdf.samples.sandbox.acroforms;

import com.itextpdf.forms.fields.*;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.CellRenderer;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.samples.GenericTest;
import com.itextpdf.test.annotations.type.SampleTest;

import java.io.File;
import java.io.FileOutputStream;

import org.junit.experimental.categories.Category;

//参见：https://kb.itextpdf.com/itext/create-fields-in-a-table#Createfieldsinatable-createradiointable
@Category(SampleTest.class)
public class CreateRadioInTable extends GenericTest {
    public static final String DEST = "./target/test/resources/sandbox/acroforms/create_radio_in_table.pdf";

    public static void main(String[] args) throws Exception {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new CreateRadioInTable().manipulatePdf(DEST);
    }

    @Override
    protected void manipulatePdf(String dest) throws Exception {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
        Document doc = new Document(pdfDoc);
        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

// Radio buttons will be added to this radio group
        PdfButtonFormField radioGroup = new RadioFormFieldBuilder(pdfDoc, "Language").createRadioGroup();
        radioGroup.setValue("");

        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        Cell cell = new Cell().add(new Paragraph("English"));
        table.addCell(cell);

        cell = new Cell();

        // The renderer creates radio button for the current radio group in the current cell
        cell.setNextRenderer(new AddRadioButtonRenderer(cell, radioGroup, "english"));
        table.addCell(cell);

        cell = new Cell().add(new Paragraph("French"));
        table.addCell(cell);

        cell = new Cell();
        cell.setNextRenderer(new AddRadioButtonRenderer(cell, radioGroup, "french"));
        table.addCell(cell);

        cell = new Cell().add(new Paragraph("Dutch"));
        table.addCell(cell);

        cell = new Cell();
        cell.setNextRenderer(new AddRadioButtonRenderer(cell, radioGroup, "dutch"));
        table.addCell(cell);

        doc.add(table);

        form.addField(radioGroup);

        doc.close();
    }


    private class AddRadioButtonRenderer extends CellRenderer {
        protected String value;
        protected PdfButtonFormField radioGroup;

        public AddRadioButtonRenderer(Cell modelElement, PdfButtonFormField radioGroup, String fieldName) {
            super(modelElement);
            this.radioGroup = radioGroup;
            this.value = fieldName;
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

            // Create a radio button that is added to a radio group.
            PdfFormAnnotation radio = new RadioFormFieldBuilder(drawContext.getDocument(), this.value)
                    .createRadioButton(value,getOccupiedAreaBBox());
            this.radioGroup.addKid(radio);
        }
    }
}
