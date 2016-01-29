/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV

*/

package com.itextpdf.samples.book.part2.chapter06;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.test.annotations.type.SampleTest;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.renderer.CellRenderer;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.samples.GenericTest;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.junit.experimental.categories.Category;

@Category(SampleTest.class)
public class Listing_06_05_ImportingPages2 extends GenericTest {
    public static final String DEST
            = "./target/test/resources/book/part2/chapter06/Listing_06_05_ImportingPages2.pdf";
    public static final String SOURCE
            = "./src/test/resources/source.pdf";
    public static final String MOVIE_TEMPLATES
            = "./src/test/resources/book/part1/chapter03/cmp_Listing_03_29_MovieTemplates.pdf";

    public static void main(String args[]) throws IOException, SQLException {
        new Listing_06_05_ImportingPages2().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest) throws IOException, SQLException {
        //Initialize destination document
        FileOutputStream fos = new FileOutputStream(dest);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument resultDoc = new PdfDocument(writer);
        Document doc = new Document(resultDoc);

        Table table = new Table(2);

        PdfReader reader = new PdfReader(MOVIE_TEMPLATES);
        PdfDocument srcDoc = new PdfDocument(reader);

        for (int i = 1; i <= srcDoc.getNumberOfPages(); i++) {
            PdfPage curPage = srcDoc.getPage(i);
            PdfFormXObject header = curPage.copyAsFormXObject(resultDoc);
            Cell cell = new Cell()
                    .add(new Image(header).setAutoScaleWidth(true))
                    .setBorder(new SolidBorder(1));
            cell.setNextRenderer(new MyCellRenderer(cell));
            table.addCell(cell);
        }

        doc.add(table);

        resultDoc.close();
        srcDoc.close();
    }


    class MyCellRenderer extends CellRenderer {
        public MyCellRenderer(Cell modelElement) {
            super(modelElement);
        }

        @Override
        public void draw(DrawContext drawContext) {
            super.draw(drawContext);
            Rectangle rect = getOccupiedAreaBBox();
            System.out.println(rect.getX());
            System.out.println(rect.getY());
            System.out.println(rect.getWidth());
            System.out.println(rect.getHeight());
            System.out.println();
        }
    }
}
