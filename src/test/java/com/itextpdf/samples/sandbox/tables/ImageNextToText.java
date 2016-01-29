/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV

*/

package com.itextpdf.samples.sandbox.tables;

import com.itextpdf.io.image.ImageFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Property;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.samples.GenericTest;
import com.itextpdf.test.annotations.type.SampleTest;

import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;

import org.junit.experimental.categories.Category;

@Category(SampleTest.class)
public class ImageNextToText extends GenericTest {
    public static final String DEST = "./target/test/resources/sandbox/tables/image_next_to_text.pdf";
    public static final String IMG1 = "./src/test/resources/sandbox/tables/javaone2013.jpg";

    public static void main(String[] args) throws Exception {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new ImageNextToText().manipulatePdf(DEST);
    }

    public static Cell createImageCell(String path) throws MalformedURLException {
        Image img = new Image(ImageFactory.getImage(path));
        Cell cell = new Cell().add(img.setAutoScale(true));
        cell.setBorder(null);
        return cell;
    }

    public static Cell createTextCell(String text) {
        Cell cell = new Cell();
        Paragraph p = new Paragraph(text);
        p.setTextAlignment(Property.TextAlignment.RIGHT);
        cell.add(p).setVerticalAlignment(Property.VerticalAlignment.BOTTOM);
        cell.setBorder(Border.NO_BORDER);
        return cell;
    }

    @Override
    protected void manipulatePdf(String dest) throws Exception {
        FileOutputStream fos = new FileOutputStream(dest);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);
        Table table = new Table(new float[]{1, 2});
        table.setWidthPercent(100);
        table.addCell(createImageCell(IMG1));
        table.addCell(createTextCell("This picture was taken at Java One.\nIt shows the iText crew at Java One in 2013."));
        doc.add(table);

        doc.close();
    }
}
