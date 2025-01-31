/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV

*/

package com.itextpdf.samples.book.part1.chapter04;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.samples.GenericTest;
import com.itextpdf.test.annotations.type.SampleTest;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

//设置table宽度及列宽
@Category(SampleTest.class)
public class Listing_04_02_ColumnWidths extends GenericTest {
    public static final String DEST = "./target/test/resources/book/part1/chapter04/Listing_04_02_ColumnWidths.pdf";

    public static void main(String args[]) throws IOException, SQLException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();

        new Listing_04_02_ColumnWidths().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException, SQLException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
        Document doc = new Document(pdfDoc);

        Table table = createTable1();
        doc.add(table);
        table = createTable2();
        table.setMarginTop(5);
        table.setMarginBottom(5);
        doc.add(table);
        table = createTable3();
        doc.add(table);
        table = createTable4();
        table.setMarginTop(5);
        table.setMarginBottom(5);
        doc.add(table);
        table = createTable5();
        doc.add(table);

        doc.close();
    }

    //table设置百分比宽度
    public static Table createTable1() {
        Table table = new Table(new float[]{2, 1, 1});
        table.setWidth(UnitValue.createPercentValue(288/523f * 100));
        Cell cell;
        cell = new Cell(1, 3).add(new Paragraph("Table 1"));
        table.addCell(cell);
        cell = new Cell(2, 1).add(new Paragraph("Cell with rowspan 2"));
        table.addCell(cell);
        table.addCell(new Cell().add(new Paragraph("row 1; cell 1")));
        table.addCell(new Cell().add(new Paragraph("row 1; cell 2")));
        table.addCell(new Cell().add(new Paragraph("row 2; cell 1")));
        table.addCell(new Cell().add(new Paragraph("row 2; cell 2")));
        return table;
    }

    //设置绝对宽度
    public static Table createTable2() {
        Table table = new Table(new float[]{2, 1, 1});
        table.setWidth(UnitValue.createPointValue(288));
        Cell cell;
        cell = new Cell(1, 3).add(new Paragraph("Table 2"));
        table.addCell(cell);
        cell = new Cell(2, 1).add(new Paragraph("Cell with rowspan 2"));
        table.addCell(cell);
        table.addCell(new Cell().add(new Paragraph("row 1; cell 1")));
        table.addCell(new Cell().add(new Paragraph("row 1; cell 2")));
        table.addCell(new Cell().add(new Paragraph("row 2; cell 1")));
        table.addCell(new Cell().add(new Paragraph("row 2; cell 2")));
        return table;
    }

    public static Table createTable3() {
        Table table = new Table(new float[]{2, 1, 1});
        table.setWidth(UnitValue.createPercentValue(55.067f));
        Cell cell;
        cell = new Cell(1, 3).add(new Paragraph("Table 3"));
        table.addCell(cell);
        cell = new Cell(2, 1).add(new Paragraph("Cell with rowspan 2"));
        table.addCell(cell);
        table.addCell(new Cell().add(new Paragraph("row 1; cell 1")));
        table.addCell(new Cell().add(new Paragraph("row 1; cell 2")));
        table.addCell(new Cell().add(new Paragraph("row 2; cell 1")));
        table.addCell(new Cell().add(new Paragraph("row 2; cell 2")));
        return table;
    }

    public static Table createTable4() {
        Table table = new Table(new float[]{144, 72, 72});
        Rectangle rect = new Rectangle(523, 770);
        table.setWidth(UnitValue.createPercentValue((144 + 72 + 72) / rect.getWidth() * 100));
        Cell cell;
        cell = new Cell(1, 3).add(new Paragraph("Table 4"));
        table.addCell(cell);
        cell = new Cell(2, 1).add(new Paragraph("Cell with rowspan 2"));
        table.addCell(cell);
        table.addCell("row 1; cell 1");
        table.addCell("row 1; cell 2");
        table.addCell("row 2; cell 1");
        table.addCell("row 2; cell 2");
        return table;
    }

    public static Table createTable5() {
        Table table = new Table(new float[]{144, 72, 72});
        Cell cell;
        cell = new Cell(1, 3).add(new Paragraph("Table 5"));
        table.addCell(cell);
        cell = new Cell(2, 1).add(new Paragraph("Cell with rowspan 2"));
        table.addCell(cell);
        table.addCell("row 1; cell 1");
        table.addCell("row 1; cell 2");
        table.addCell("row 2; cell 1");
        table.addCell("row 2; cell 2");
        return table;
    }
}
