/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV

*/

/**
 * Example written by Bruno Lowagie in answer to:
 * http://stackoverflow.com/questions/29378407/how-can-you-eliminate-white-space-in-multiple-columns-using-itextsharp
 */
package com.itextpdf.samples.sandbox.objects;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.annotations.type.SampleTest;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.renderer.DocumentRenderer;
import com.itextpdf.samples.GenericTest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.experimental.categories.Category;

@Ignore
@Category(SampleTest.class)
public class ColumnTextParagraphs2 extends GenericTest {
    public static final String DEST = "./target/test/resources/sandbox/objects/column_text_paragraphs2.pdf";
    public static final String TEXT = "This is some long paragraph " +
            "that will be added over and over again to prove a point.";
    public static final float COLUMN_WIDTH = 254;
    public static final float ERROR_MARGIN = 16;
    public static final Rectangle[] COLUMNS = {
            new Rectangle(36, 36, 36 + COLUMN_WIDTH, 806),
            new Rectangle(305, 36, 305 + COLUMN_WIDTH, 806)
    };

    public static void main(String[] args) throws Exception {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new ColumnTextParagraphs2().manipulatePdf(DEST);
    }

    // TODO Smth like ColumnText simulation is needed here. See stackoverflow link provided above to find what is this sample exactly about
    public void manipulatePdf(String dest) throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new FileOutputStream(dest)));
        final Document doc = new Document(pdfDoc);

        doc.setRenderer(new DocumentRenderer(doc) {
            int nextAreaNumber = 0;
            int currentPageNumber;

            @Override
            public LayoutArea updateCurrentArea(LayoutResult overflowResult) {
                if (nextAreaNumber % 2 == 0) {
                    currentPageNumber = super.updateCurrentArea(overflowResult).getPageNumber();
                } else {
                    new PdfCanvas(document.getPdfDocument(), document.getPdfDocument().getNumberOfPages())
                            .moveTo(297.5f, 36)
                            .lineTo(297.5f, 806)
                            .stroke();
                }
                return (currentArea = new LayoutArea(currentPageNumber, COLUMNS[nextAreaNumber++ % 2].clone()));
            }
        });

        pdfDoc.addNewPage();
        int paragraphs = 0;
        while (paragraphs < 30) {
            doc.add(new Paragraph(String.format("Paragraph %s: %s", ++paragraphs, TEXT)));
        }
        doc.close();
    }
}
