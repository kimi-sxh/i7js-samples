/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV

*/

package com.itextpdf.samples.book.part2.chapter08;

import com.itextpdf.forms.fields.PdfFormAnnotation;
import com.itextpdf.forms.fields.TextFormFieldBuilder;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.font.FontSet;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.test.annotations.type.SampleTest;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfTextFormField;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.renderer.CellRenderer;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.samples.GenericTest;

import java.io.IOException;
import java.util.Map;

import org.junit.experimental.categories.Category;

@Category(SampleTest.class)
public class Listing_08_07_TextFields extends GenericTest {
    public static final String DEST = "./target/test/resources/book/part2/chapter08/Listing_08_07_TextFields.pdf";
    public static final String FILLED = "./target/test/resources/book/part2/chapter08/Listing_08_07_TextFields_filled.pdf";

    public static void main(String[] args) throws Exception {
        new Listing_08_07_TextFields().manipulatePdf(DEST);
    }

    @Override
    protected void manipulatePdf(String dest) throws Exception {
        createPdf(DEST);
        fillPdf(DEST, FILLED);
    }

    public void createPdf(String dest) throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
        Document doc = new Document(pdfDoc);

        Cell cell;
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2}));
        table.setWidth(UnitValue.createPercentValue(80));

        table.addCell(new Cell().add(new Paragraph("Name:")));
        cell = new Cell();
        cell.setNextRenderer(new TextFilledCellRenderer(cell, 1));
        table.addCell(cell);

        table.addCell(new Cell().add(new Paragraph("Loginname:")));

        cell = new Cell();
        cell.setNextRenderer(new TextFilledCellRenderer(cell, 2));
        table.addCell(cell);

        table.addCell(new Cell().add(new Paragraph("Password:")));

        cell = new Cell();
        cell.setNextRenderer(new TextFilledCellRenderer(cell, 3));

        table.addCell(cell);

        table.addCell(new Cell().add(new Paragraph("Reason:")));

        cell = new Cell();
        cell.setNextRenderer(new TextFilledCellRenderer(cell, 4));

        cell.setHeight(60);
        table.addCell(cell);

        doc.add(table);

        doc.close();
    }

    public void fillPdf(String src, String dest) throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(dest));
        Document document = new Document(pdfDoc);
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        Map<String, PdfFormField> fields = form.getAllFormFields();

        PdfFont msung = PdfFontFactory.createFont("MSung-Light", "UniCNS-UCS2-H", PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
        PdfFont notoSerifCJKtc = PdfFontFactory.createFont("./src/test/resources/font/NotoSerifCJKtc-Regular.ttf", PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
        pdfDoc.addFont(msung);
        pdfDoc.addFont(notoSerifCJKtc);

        FontSet set = new FontSet();
        set.addFont(msung.getFontProgram(),"UniCNS-UCS2-H");
        set.addFont(notoSerifCJKtc.getFontProgram(), PdfEncodings.IDENTITY_H);
        document.setFontProvider(new FontProvider(set));

        PdfFormField text1 = fields.get("text_1");
        text1.setValue("Bruno Lowagie 福州");
        text1.setFont(msung);
        text1.regenerateField();

        fields.get("text_2").setFieldFlags(0);
        fields.get("text_2").getFirstFormAnnotation().setBorderColor(ColorConstants.RED);
        fields.get("text_2").setValue("bruno");

        fields.get("text_3").setFieldFlag(PdfFormField.FF_PASSWORD, false);
        fields.get("text_3").getWidgets().get(0).setFlag(PdfAnnotation.PRINT);
        form.getField("text_3").setValue("12345678", "********");
        ((PdfTextFormField) form.getField("text_4")).setMaxLen(12);
        form.getField("text_4").regenerateField();

       // form.flattenFields();
        pdfDoc.close();
    }


    protected class TextFilledCellRenderer extends CellRenderer {
        /**
         * The text field index of a TextField that needs to be added to a cell.
         */
        protected int tf = 0;

        public TextFilledCellRenderer(Cell modelElement, int tf) {
            super(modelElement);
            this.tf = tf;
        }

        @Override
        public void draw(DrawContext drawContext) {
            super.draw(drawContext);

            PdfTextFormField text = new TextFormFieldBuilder(drawContext.getDocument(), String.format("text_%s", tf))
                    .setWidgetRectangle(getOccupiedAreaBBox()).createText();
            text.setValue("Enter your name here...");
            text.getFirstFormAnnotation().setBackgroundColor(new DeviceGray(0.75f));
            PdfDictionary borderStyleDict = new PdfDictionary();
            switch (tf) {
                case 1:
                    borderStyleDict.put(PdfName.S, PdfName.B);
                    text.getWidgets().get(0).setBorderStyle(borderStyleDict);
                    text.setFontSize(10);
                    text.setValue("Enter your name here...");
                    text.setRequired(true);
                    text.setJustification(TextAlignment.CENTER);
                    break;
                case 2:
                    borderStyleDict.put(PdfName.S, PdfName.S);
                    text.getWidgets().get(0).setBorderStyle(borderStyleDict);
                    text.setMaxLen(8);
                    text.setComb(true);
                    text.getFirstFormAnnotation().setBorderWidth(2);
                    break;
                case 3:
                    borderStyleDict.put(PdfName.S, PdfName.I);
                    text.getWidgets().get(0).setBorderStyle(borderStyleDict);
                    text.setPassword(true);
                    text.setValue("Choose a password");
                    text.getFirstFormAnnotation().setVisibility(PdfFormAnnotation.VISIBLE_BUT_DOES_NOT_PRINT);
                    break;
                case 4:
                    borderStyleDict.put(PdfName.S, PdfName.D);
                    text.getWidgets().get(0).setBorderStyle(borderStyleDict);
                    text.getFirstFormAnnotation().setBorderColor(ColorConstants.RED);
                    text.setFontSize(8);
                    text.setValue("Enter the reason why you want to win a free " +
                            "accreditation for the Foobar Film Festival aaa 多行文本  aaaa");
                    text.getFirstFormAnnotation().setBorderWidth(2);
                    text.setMultiline(true);
                    text.setRequired(true);
                    break;
            }
            PdfAcroForm.getAcroForm(drawContext.getDocument(), true).addField(text);
        }
    }
}
