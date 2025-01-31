/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV

*/

package com.itextpdf.samples.book.part2.chapter08;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.*;
import com.itextpdf.forms.fields.properties.CheckBoxType;
import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.pdf.xobject.PdfXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AbstractElement;
import com.itextpdf.layout.element.ILeafElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.renderer.AbstractRenderer;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.tagging.IAccessibleElement;
import com.itextpdf.samples.GenericTest;
import com.itextpdf.test.annotations.type.SampleTest;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Category(SampleTest.class)
public class Listing_08_01_Buttons extends GenericTest {
    public static final String DEST = "./target/test/resources/book/part2/chapter08/Listing_08_01_Buttons.pdf";
    /**
     * The resulting PDF.
     */
    public static final String FILLED = "./target/test/resources/book/part2/chapter08/Listing_08_01_Buttons_filled.pdf";
    /**
     * Path to a JavaScript resource.
     */
    public static final String RESOURCE = "./src/test/resources/js/buttons.js";
    /**
     * Path to an image used as button icon.
     */
    public static final String IMAGE = "./src/test/resources/img/info.png";
    /**
     * Possible values of a radio field / checkboxes
     */
    public static final String[] LANGUAGES = {"English", "German", "French", "Spanish", "Dutch"};

    public static void main(String[] args) throws Exception {
        new Listing_08_01_Buttons().manipulatePdf(DEST);
    }

    @Override
    protected void manipulatePdf(String dest) throws Exception {
        createPdf(DEST);
        fillPdf(DEST, FILLED);
    }

    protected static String readFileToString(String path) throws IOException {
        File file = new File(path);
        byte[] jsBytes = new byte[(int) file.length()];
        FileInputStream f = new FileInputStream(file);
        f.read(jsBytes);
        return new String(jsBytes);
    }

    /**
     * <b>概要：</b>
     *  创建pdf表单
     * <b>作者：</b>suxh</br>
     * <b>日期：</b>2024/6/13 9:51</br>
     * @param filename
     * @return
     **/
    public void createPdf(String filename) throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename.replace(" ","%20")));
        Document doc = new Document(pdfDoc);
        pdfDoc.getCatalog().setOpenAction(PdfAction.createJavaScript(readFileToString(RESOURCE).replace("\r\n", "\n")));
        PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        Rectangle rect;

        RadioFormFieldBuilder builder = new RadioFormFieldBuilder(pdfDoc,"language");
        PdfButtonFormField radioGroup = builder.createRadioGroup();
        radioGroup.setValue("English");
        radioGroup.setCheckType(CheckBoxType.DIAMOND);

        //单选框
        PdfFormAnnotation radio;
        for (int i = 0; i < LANGUAGES.length; i++) {
            rect = new Rectangle(40, 806 - i * 40, 60 - 40, 806 - 788);
            radio = builder.createRadioButton( LANGUAGES[i], rect)
                    .setBorderColor(ColorConstants.DARK_GRAY)
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY);
            //radio.setCheckType(CheckBoxType.CIRCLE);
            radioGroup.addKid(radio);
            canvas
                    .beginText()
                    .setFontAndSize(font, 18)
                    .moveText(70, 790 - i * 40 + 20)
                    .showText(LANGUAGES[i])
                    .endText();
        }
        PdfAcroForm.getAcroForm(pdfDoc, true).addField(radioGroup);

        //自定义画复选框
        PdfButtonFormField checkBox;
        for (int i = 0; i < LANGUAGES.length; i++) {
            PdfFormXObject xObjectApp1 = new PdfFormXObject(new Rectangle(0, 0, 200 - 180, 806 - 788));
            PdfCanvas canvasApp1 = new PdfCanvas(xObjectApp1, pdfDoc);
            canvasApp1
                    .saveState()
                    .rectangle(1, 1, 18, 18)
                    .stroke()
                    .restoreState();
            PdfFormXObject xObjectApp2 = new PdfFormXObject(new Rectangle(0, 0, 200 - 180, 806 - 788));
            PdfCanvas canvasApp2 = new PdfCanvas(xObjectApp2, pdfDoc);
            canvasApp2
                    .saveState()
                    .setFillColorRgb(255, 128, 128)
                    .rectangle(1, 1, 18, 18)
                    .rectangle(180, 806 - i * 40, 200 - 180, 806 - 788)
                    .fillStroke()
                    .moveTo(1, 1)
                    .lineTo(19, 19)
                    .moveTo(1, 19)
                    .lineTo(19, 1)
                    .stroke()
                    .restoreState();

            rect = new Rectangle(180, 806 - i * 40, 200 - 180, 806 - 788);
            checkBox = new CheckBoxFormFieldBuilder(pdfDoc, LANGUAGES[i]).setWidgetRectangle(rect).createCheckBox();
            checkBox.setValue("Off");
            checkBox.getWidgets().get(0).getNormalAppearanceObject().put(new PdfName("Off"),
                    xObjectApp1.getPdfObject());
            checkBox.getWidgets().get(0).getNormalAppearanceObject().put(new PdfName("Yes"),
                    xObjectApp2.getPdfObject());
            // Write text
            canvas
                    .beginText()
                    .setFontAndSize(font, 18)
                    .moveText(210, 790 - i * 40 + 20)
                    .showText(LANGUAGES[i])
                    .endText();
            PdfAcroForm.getAcroForm(pdfDoc, true).addField(checkBox);
        }

        // Add the push button
        rect = new Rectangle(300, 806, 370 - 300, 806 - 788);

        Button button = new Button("Buttons", "Push me", pdfDoc, rect);
        button.setImage(ImageDataFactory.create(IMAGE));
        button.setButtonBackgroundColor(new DeviceGray(0.75f));
        button.setBorderColor(ColorConstants.DARK_GRAY);
        button.setFontSize(12);

        doc.add(new Paragraph().add(button));

        PdfWidgetAnnotation ann = button.getButton().getWidgets().get(0);
        ann.setAction(PdfAction.createJavaScript("this.showButtonState()"));

        doc.close();
    }

    /**
     * Manipulates a PDF file src with the file dest as result
     *  填充pdf
     * @param src  the original PDF
     * @param dest the resulting PDF
     * @throws IOException
     */
    public void fillPdf(String src, String dest) throws IOException {
        //填充单选框
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(dest));
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        String[] radiostates = form.getField("language").getAppearanceStates();
        form.getField("language").setValue(radiostates[4]);

        //填充复选框
        for (int i = 0; i < LANGUAGES.length; i++) {
            String[] checkboxStates = form.getField("English").getAppearanceStates();
            form.getField(LANGUAGES[i]).setValue(checkboxStates[i % 2 == 0 ? 1 : 0], false);
        }
        pdfDoc.close();
    }

    private class Button extends AbstractElement<Button> implements ILeafElement, IAccessibleElement {

        protected PdfName role = PdfName.Figure;
        protected PdfButtonFormField button;
        protected String caption;
        protected ImageData image;
        protected Rectangle rect;
        protected Color borderColor = ColorConstants.BLACK;
        protected Color buttonBackgroundColor = ColorConstants.WHITE;

        public Button(String name, String caption, PdfDocument document, Rectangle rect) {
            button = new PushButtonFormFieldBuilder(document, "name")
                    .setWidgetRectangle(rect).createPushButton();
            button.setPushButton(true);

            this.caption = caption;
            this.rect = rect;
        }

        @Override
        protected IRenderer makeNewRenderer() {
            return new ButtonRenderer(this);
        }

        public PdfName getRole() {
            return role;
        }

        public void setRole(PdfName role) {
            this.role = role;
        }

        @Override
        public AccessibilityProperties getAccessibilityProperties() {
            return null;
        }

        public PdfButtonFormField getButton() {
            return button;
        }

        public String getCaption() {
            return caption;
        }

        public void setImage(ImageData image) {
            this.image = image;
        }

        public ImageData getImage() {
            return image;
        }

        public Color getBorderColor() {
            return borderColor;
        }

        public void setBorderColor(Color borderColor) {
            this.borderColor = borderColor;
        }

        public void setButtonBackgroundColor(Color buttonBackgroundColor) {
            this.buttonBackgroundColor = buttonBackgroundColor;
        }
    }

    class ButtonRenderer extends AbstractRenderer {

        public ButtonRenderer(Button button) {
            super(button);
        }

        @Override
        public LayoutResult layout(LayoutContext layoutContext) {
            LayoutArea area = layoutContext.getArea().clone();
            Rectangle layoutBox = area.getBBox();
            applyMargins(layoutBox, false);
            Button modelButton = (Button) modelElement;
            occupiedArea = new LayoutArea(area.getPageNumber(), new Rectangle(modelButton.rect));
            PdfButtonFormField button = ((Button) getModelElement()).getButton();
            button.getWidgets().get(0).setRectangle(new PdfArray(occupiedArea.getBBox()));

            return new LayoutResult(LayoutResult.FULL, occupiedArea, null, null);
        }

        @Override
        public void draw(DrawContext drawContext) {
            Button modelButton = (Button) modelElement;
            occupiedArea.setBBox(modelButton.rect);

            super.draw(drawContext);
            float width = occupiedArea.getBBox().getWidth();
            float height = occupiedArea.getBBox().getHeight();

            PdfStream str = new PdfStream();
            PdfCanvas canvas = new PdfCanvas(str, new PdfResources(), drawContext.getDocument());
            PdfFormXObject xObject = new PdfFormXObject(new Rectangle(0, 0, width, height));

            canvas.
                    saveState().
                    setStrokeColor(modelButton.getBorderColor()).
                    setLineWidth(1).
                    rectangle(0, 0, occupiedArea.getBBox().getWidth(), occupiedArea.getBBox().getHeight()).
                    stroke().
                    setFillColor(modelButton.buttonBackgroundColor).
                    rectangle(0.5f, 0.5f, occupiedArea.getBBox().getWidth() - 1, occupiedArea.getBBox().getHeight() - 1).
                    fill().
                    restoreState();

            Paragraph paragraph = new Paragraph(modelButton.getCaption()).setFontSize(10).setMargin(0).setMultipliedLeading(1);

            new Canvas(canvas, new Rectangle(0, 0, width, height)).
                    showTextAligned(paragraph, 20, 3, TextAlignment.LEFT, VerticalAlignment.BOTTOM);

            ImageData image = modelButton.getImage();
            if (image != null) {
                PdfImageXObject imageXObject = new PdfImageXObject(image);
                float imageWidth = image.getWidth();

                if (image.getWidth() > modelButton.rect.getWidth() * 2/3) {
                    imageWidth = modelButton.rect.getWidth() * 2/3;
                }
                if (image.getHeight() > modelButton.rect.getHeight()) {
                    imageWidth = image.getWidth() * (modelButton.rect.getHeight() / image.getHeight()) * 2/3;
                }
                Rectangle rect = PdfXObject.calculateProportionallyFitRectangleWithWidth(imageXObject, 3, 3, imageWidth);
                canvas.addXObjectFittedIntoRectangle(imageXObject, rect);

                xObject.getResources().addImage(imageXObject);
            }

            PdfButtonFormField button = modelButton.getButton();
            button.getWidgets().get(0).setNormalAppearance(xObject.getPdfObject());
            xObject.getPdfObject().getOutputStream().writeBytes(str.getBytes());

            PdfAcroForm.getAcroForm(drawContext.getDocument(), true).addField(button, drawContext.getDocument().getPage(1));
        }

        @Override
        public IRenderer getNextRenderer() {
            return null;
        }
    }
}
