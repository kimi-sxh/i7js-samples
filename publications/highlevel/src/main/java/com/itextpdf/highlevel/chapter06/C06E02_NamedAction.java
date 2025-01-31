/*
 * This example was written by Bruno Lowagie
 * in the context of the book: iText 7 building blocks
 */
package com.itextpdf.highlevel.chapter06;

import com.itextpdf.highlevel.util.CsvTo2DList;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.ListNumberingType;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Bruno Lowagie (iText Software)
 */
public class C06E02_NamedAction {
    
    public static final String SRC = "src/main/resources/data/jekyll_hyde.csv";
    public static final String DEST = "results/chapter06/jekyll_hyde_named.pdf";
       
    public static void main(String args[]) throws IOException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new C06E02_NamedAction().createPdf(DEST);
    }
    
    public void createPdf(String dest) throws IOException {
        PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
        Document document = new Document(pdf);
        Paragraph p = new Paragraph()
            .add("Go to last page")
            .setAction(PdfAction.createNamed(PdfName.LastPage));
        document.add(p);
        List<List<String>> resultSet = CsvTo2DList.convert(SRC, "|");
        resultSet.remove(0);
        com.itextpdf.layout.element.List list =
            new com.itextpdf.layout.element.List(ListNumberingType.DECIMAL);
        for (List<String> record : resultSet) {
            ListItem li = new ListItem();
            li.setKeepTogether(true);
            li.add(new Paragraph().setFontSize(14).add(record.get(2)))
                .add(new Paragraph(String.format(
                    "Directed by %s (%s, %s)",
                    record.get(3), record.get(4), record.get(1))));
            File file = new File(String.format(
                "src/main/resources/img/%s.jpg", record.get(0)));
            if (file.exists()) {
                Image img = new Image(ImageDataFactory.create(file.getPath()));
                img.scaleToFit(10000, 120);
                li.add(img);
            }
            list.add(li);
        }
        document.add(list);
        p = new Paragraph()
            .add("Go to first page")
            .setAction(PdfAction.createNamed(PdfName.FirstPage));
        document.add(p);
        document.close();
    }
    
}
