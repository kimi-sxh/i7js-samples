/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV

*/

/**
 * Example written by Bruno Lowagie in answer to:
 * http://stackoverflow.com/questions/24665167/table-keeprowstogether-in-itext-5-5-1-doesnt-seem-to-work-correctly
 */
package com.itextpdf.samples.sandbox.tables;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.samples.GenericTest;
import com.itextpdf.test.annotations.type.SampleTest;

import org.junit.experimental.categories.Category;

import java.io.File;

@Category(SampleTest.class)
public class SplitRowAtSpecificRow extends GenericTest {
    public static final String DEST = "./target/test/resources/sandbox/tables/split_row_at_specific_row.pdf";

    public static void main(String[] args) throws Exception {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new SplitRowAtSpecificRow().manipulatePdf(DEST);
    }

    @Override
    protected void manipulatePdf(String dest) throws Exception {
        Table table = new Table(1);
        table.setWidth(550);
        // the number of iterations has been changed in order to provide the same as in itext5 example
        for (int i = 0; i < 6; i++) {
            Cell cell;
            if (i == 5) {
                cell = new Cell().add(new Paragraph("Three\nLines\nHere"));
            } else {
                cell = new Cell().add(new Paragraph(Integer.toString(i)));
            }
            table.addCell(cell);
        }

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
        Document doc = new Document(pdfDoc, new PageSize(612, 237));
        doc.add(table);
        doc.close();
    }
}