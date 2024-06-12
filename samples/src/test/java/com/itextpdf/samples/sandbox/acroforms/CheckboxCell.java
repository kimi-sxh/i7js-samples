/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV

*/

/**
 * Example written by Bruno Lowagie in answer to:
 * http://stackoverflow.com/questions/21342034/resizing-a-form-field-in-itextsharp
 * <p>
 * Given a cell, how do you add a check box with a specific size at a specific position.
 */
package com.itextpdf.samples.sandbox.acroforms;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.CheckBoxFormFieldBuilder;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.renderer.CellRenderer;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.samples.GenericTest;
import com.itextpdf.test.annotations.type.SampleTest;
import org.junit.experimental.categories.Category;

import java.io.File;

//参见：https://kb.itextpdf.com/itext/create-fields-in-a-table
@Category(SampleTest.class)
public class CheckboxCell extends GenericTest {
    public static final String DEST = "./target/test/resources/sandbox/acroforms/checkbox_cell.pdf";

    public static void main(String[] args) throws Exception {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new CheckboxCell().manipulatePdf(DEST);
    }

    @Override
    protected void manipulatePdf(String dest) throws Exception {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
        Document doc = new Document(pdfDoc);

        Table table = new Table(5);
        Cell cell;
        for (int i = 0; i < 5; i++) {
            cell = new Cell();
            cell.setNextRenderer(new CheckboxCellRenderer(cell, "cb" + i));
            cell.setHeight(50);
            table.addCell(cell);
        }

        doc.add(table);
        doc.close();
    }


    private class CheckboxCellRenderer extends CellRenderer {
        protected String name;

        public CheckboxCellRenderer(Cell modelElement, String name) {
            super(modelElement);
            this.name = name;
        }

        @Override
        public void draw(DrawContext drawContext) {
            PdfAcroForm form = PdfFormCreator.getAcroForm(drawContext.getDocument(), true);

            float x = (getOccupiedAreaBBox().getLeft() + getOccupiedAreaBBox().getRight()) / 2;
            float y = (getOccupiedAreaBBox().getTop() + getOccupiedAreaBBox().getBottom()) / 2;
            Rectangle rect = new Rectangle(x - 10, y - 10, 20, 20);
            // The 4th parameter is the initial value of checkbox: 'Yes' - checked, 'Off' - unchecked
            // By default, checkbox value type is cross.
            PdfButtonFormField checkBox = new CheckBoxFormFieldBuilder(drawContext.getDocument(), name)
                    .setWidgetRectangle(rect).createCheckBox();
            checkBox.setValue("Yes");
            form.addField(checkBox);
        }
    }
}
