/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV

*/

package com.itextpdf.samples.book.part4.chapter15;

import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.SimpleTextExtractionStrategy;
import com.itextpdf.kernel.pdf.canvas.parser.listener.ITextExtractionStrategy;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.test.annotations.type.SampleTest;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import org.junit.experimental.categories.Category;
import org.xml.sax.SAXException;

//按照内容流读取
@Category(SampleTest.class)
public class Listing_15_25_ExtractPageContent {
    public static final String DEST
            = "./target/test/resources/book/part4/chapter15/Listing_15_25_ExtractPageContent.txt";
    public static final String PREFACE
            = "./src/test/resources/pdfs/preface.pdf";

    public static void main(String args[])
            throws IOException, SQLException, ParserConfigurationException, SAXException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();

        new Listing_15_25_ExtractPageContent().manipulatePdf(DEST);
    }

    public void manipulatePdf(String dest)
            throws IOException, SQLException, ParserConfigurationException, SAXException {
        parsePdf(PREFACE, dest);
    }

    public void parsePdf(String src, String txt) throws IOException {
        PrintWriter out = new PrintWriter(new FileOutputStream(txt));
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(src));
        ITextExtractionStrategy strategy = new SimpleTextExtractionStrategy();
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
