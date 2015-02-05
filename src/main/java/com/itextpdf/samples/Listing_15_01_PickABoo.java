package com.itextpdf.samples;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.pdf.layer.PdfLayer;
import com.itextpdf.basics.PdfException;
import com.itextpdf.core.fonts.PdfStandardFont;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;

public class Listing_15_01_PickABoo {

    static private final String RESULT = "./result.pdf";

    public static void main(String args[]) throws IOException, PdfException {

        //Initialize writer
        FileOutputStream fos = new FileOutputStream(RESULT);
        PdfWriter writer = new PdfWriter(fos);

        //Initialize document
        PdfDocument pdfDoc = new PdfDocument(writer);

        //Write to canvas
        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        PdfLayer layer = new PdfLayer("Do you see me?", pdfDoc);
        layer.setOn(true);
        canvas.beginText().setFontAndSize(new PdfStandardFont(pdfDoc, PdfStandardFont.Helvetica), 18).
                moveText(50, 760).showText("Do you see me?").
                beginLayer(layer).moveText(0, -30).showText("Peek-A-Boo!!!").endLayer().
                endText();
        canvas.release();

        //Close document
        pdfDoc.close();

    }


}
