package com.itextpdf.samples.book.part2.chapter08;

/**
 * <h3>概要:</h3>
 *    TODO(请在此处填写概要)
 * <br>
 * <h3>功能:</h3>
 * <ol>
 * 		<li>TODO(这里用一句话描述功能点)</li>
 * </ol>
 * <h3>履历:</h3>
 * <ol>
 * 		<li>2024/6/17[suxh] 新建</li>
 * </ol>
 */
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.forms.form.element.TextArea;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//使用layout模块创建表单
//目前看只有itext8支持substitutionFont https://kb.itextpdf.com/itext/forms-in-itext-core-8-0-0
public class FormFromLayoutMechanism {

    private static final String DEST = "./target/test/resources/book/part2/chapter08/LayoutFormFields.pdf";

    private static final String DEST_FLATTEN = "./target/test/resources/book/part2/chapter08/LayoutFormFields_Flatten.pdf";

    public static void main(String[] args) throws IOException {
        //有问题：表单要点下去才有显示“福州”
        createTextArea(DEST,false);

        //直接显示福州
        createTextArea(DEST_FLATTEN, true);

        //使用layout直接填充
        fillinWithLayout(Listing_08_07_TextFields.DEST,"./target/test/resources/book/part2/chapter08/field_uncommon_text.pdf");
    }

    public static void createTextArea(String dest,boolean flatten){
        try (Document document = new Document(new PdfDocument(new PdfWriter(dest)))){
            TextArea flattenTextArea = new TextArea("EXAMPLE TEXT AREA");

            List<String> allFonts = new ArrayList<>();
            allFonts.add(StandardFonts.HELVETICA);
            allFonts.add("./src/test/resources/font/simsun.ttf");
            allFonts.add("./src/test/resources/font/NotoSerifCJKtc-Regular.ttf");
            FontProvider provider = new FontProvider();
            for (String font : allFonts) {
                provider.addFont(font);
            }
            // Adding fonts to the font provider
            document.setFontProvider(provider);

            flattenTextArea.setInteractive(!flatten);
            flattenTextArea.setProperty(FormProperty.FORM_FIELD_VALUE, "Flatten\ntext area\nwith height 生僻字：䶮/䲞/㙟/\uD84F\uDC97\uD847\uDC2A");
            flattenTextArea.setProperty(Property.HEIGHT, new UnitValue(UnitValue.POINT,100));
//            flattenTextArea.setProperty(Property.BORDER, new SolidBorder(2f));
            flattenTextArea.setFontFamily(allFonts);
            document.add(flattenTextArea);
            document.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void fillinWithLayout(String src,String dest) {
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(src), new PdfWriter(dest))) {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDocument, true);
            form.getField("textarea").setValue("Flatten\ntext area\nwith height 生僻字：䶮/䲞/<import>/\uD84F\uDC97\uD847\uDC2A");
            form.flattenFields();
        } catch (IOException e){
        }
    }
}
