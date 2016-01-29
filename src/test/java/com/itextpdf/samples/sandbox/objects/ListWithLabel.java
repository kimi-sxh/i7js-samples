/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV

*/

/**
 * Example written by Bruno Lowagie in answer to:
 * http://stackoverflow.com/questions/21579204/how-to-set-label-to-itext-list
 *
 * Workaround to add a label to a list.
 */
package com.itextpdf.samples.sandbox.objects;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.annotations.type.SampleTest;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Property;
import com.itextpdf.layout.element.*;
import com.itextpdf.samples.GenericTest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.experimental.categories.Category;

@Category(SampleTest.class)
public class ListWithLabel extends GenericTest {
    public static final String DEST = "./target/test/resources/sandbox/objects/list_with_label.pdf";

    public static void main(String[] args) throws IOException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new ListWithLabel().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new FileOutputStream(DEST)));
        Document doc = new Document(pdfDoc);

        Table table = new Table(new float[]{1, 10});
        table.setWidth(200);
        table.setHorizontalAlignment(Property.HorizontalAlignment.LEFT);
        Cell cell;
        cell = new Cell();
        cell.setBorder(null);
        cell.add(new Paragraph("Label"));
        table.addCell(cell);
        cell = new Cell();
        cell.setBorder(null);
        List list = new List();
        list.add(new ListItem("Value 1"));
        list.add(new ListItem("Value 2"));
        list.add(new ListItem("Value 3"));
        cell.add(list);
        table.addCell(cell);
        doc.add(table);

        doc.close();
    }
}
