/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV

*/

package com.itextpdf.samples.book.part4.chapter15;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.annotations.type.SampleTest;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import org.junit.experimental.categories.Category;
import org.xml.sax.SAXException;

//按照位置排序读取
@Category(SampleTest.class)
public class Listing_15_26_ExtractPageContentSorted1 {
    public static final String DEST
            = "./target/test/resources/book/part4/chapter15/Listing_15_26_ExtractPageContentSorted1.txt";
    public static final String PREFACE
            = "./src/test/resources/pdfs/preface.pdf";

    public static void main(String args[])
            throws IOException, SQLException, ParserConfigurationException, SAXException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();

        new Listing_15_26_ExtractPageContentSorted1().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest)
            throws IOException, SQLException, ParserConfigurationException, SAXException {
        //parsePdf(PREFACE, dest);

        parsePdf("D:\\idea\\old_local_base\\out\\artifacts\\interface_sign\\testfile\\633955_20230419141210.pdf",
                "./target/test/resources/book/part4/chapter15/Listing_15_26_ExtractPage_633955_20230419141210.txt");
    }

    public void parsePdf(String src, String txt) throws IOException {
        PrintWriter out = new PrintWriter(new FileOutputStream(txt,false));
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(new ByteArrayOutputStream()));
        LocationTextExtractionStrategy strategy = new LocationTextExtractionStrategy();

        PdfCanvasProcessor parser = new PdfCanvasProcessor(strategy);
        for (int i = 1; i <= pdfDoc.getNumberOfPages(); i++) {
             parser.processPageContent(pdfDoc.getPage(i));
             out.println(strategy.getResultantText());
        }
        out.flush();
        out.close();
        pdfDoc.close();
    }
}
