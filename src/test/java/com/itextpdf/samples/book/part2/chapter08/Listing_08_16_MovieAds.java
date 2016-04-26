/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV

*/

package com.itextpdf.samples.book.part2.chapter08;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfTextFormField;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceGray;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.color.WebColors;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.IAccessibleElement;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.pdf.xobject.PdfXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import com.itextpdf.layout.renderer.AbstractRenderer;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.samples.GenericTest;
import com.itextpdf.test.annotations.type.SampleTest;
import com.lowagie.database.DatabaseConnection;
import com.lowagie.database.HsqldbConnection;
import com.lowagie.filmfestival.Director;
import com.lowagie.filmfestival.Movie;
import com.lowagie.filmfestival.PojoFactory;
import org.junit.Ignore;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Ignore
@Category(SampleTest.class)
public class Listing_08_16_MovieAds extends GenericTest {
    public static final String DEST
            = "./target/test/resources/book/part2/chapter08/Listing_08_16_MovieAds.pdf";
    public static final String TEMPLATE
            = "./target/test/resources/book/part2/chapter08/Listing_08_16_MovieAds_template.pdf";
    public static final String RESOURCE
            = "./src/test/resources/pdfs/movie_overview.pdf";
    public static final String IMAGE
            = "./src/test/resources/img/posters/%s.jpg";
    public static final String POSTER = "poster";
    public static final String TEXT = "text";
    public static final String YEAR = "year";

    public static void main(String[] args) throws Exception {
        new Listing_08_16_MovieAds().manipulatePdf(DEST);
    }

    public static float millimetersToPoints(float value) {
        return (value / 25.4f) * 72f;
    }

    /**
     * Create a small formXObject that will be used for an individual ad.
     *
     * @param filename the filename of the add
     * @throws IOException
     */
    public void createTemplate(String filename) throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));
        pdfDoc.setDefaultPageSize(new PageSize(millimetersToPoints(35), millimetersToPoints(50)));
        pdfDoc.addNewPage();
        pdfDoc.getCatalog().setPageLayout(PdfName.SinglePage);

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);

        PdfButtonFormField poster = PdfFormField.createPushButton(pdfDoc, new Rectangle(millimetersToPoints(0),
                millimetersToPoints(25), millimetersToPoints(35) - millimetersToPoints(0),
                millimetersToPoints(50) - millimetersToPoints(25)), POSTER, "");
        poster.setBackgroundColor(new DeviceGray(0.4f));
        form.addField(poster);

        PdfTextFormField movie = PdfFormField.createText(pdfDoc, new Rectangle(millimetersToPoints(0),
                millimetersToPoints(7), millimetersToPoints(35) - millimetersToPoints(0),
                millimetersToPoints(25) - millimetersToPoints(7)), TEXT, "");
        movie.setMultiline(true);
        form.addField(movie);

        PdfTextFormField screening = PdfFormField.createText(pdfDoc, new Rectangle(millimetersToPoints(0),
                millimetersToPoints(0), millimetersToPoints(35) - millimetersToPoints(0),
                millimetersToPoints(7) - millimetersToPoints(0)), YEAR, "");
        screening.setJustification(PdfFormField.ALIGN_CENTER);
        screening.setBackgroundColor(new DeviceGray(0.4f));
        screening.setColor(Color.LIGHT_GRAY);
        form.addField(screening);

        pdfDoc.close();
    }

    /**
     * Fill out the small formXObject with information about the movie.
     *
     * @param filename the formXObject for an individual ad
     * @param movie    the movie that needs to be in the ad
     * @return a byte[] containing an individual ad
     * @throws IOException
     */
    public byte[] fillTemplate(String filename, Movie movie) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfReader reader = new PdfReader(filename);
        PdfDocument pdfDoc = new PdfDocument(reader, new PdfWriter(baos));
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        // change the background color of the poster and add a new icon
        DeviceRgb color = WebColors.getRGBColor("#" + movie.getEntry().getCategory().getColor());
        PdfButtonFormField bt = (PdfButtonFormField) form.getField(POSTER);
        // TODO No setImage & setLayout & setProportionalIcon in PdfFormField
        // bt.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
        // bt.setProportionalIcon(true);
        bt.setImage(String.format(IMAGE, movie.getImdb()));
        bt.setBackgroundColor(color);
//        form.removeField(POSTER);
        //form.addField(bt);
        // write the text using the appropriate font size
        PdfCanvas canvas = new PdfCanvas(pdfDoc.getFirstPage());
        Rectangle rect = form.getField(TEXT).getWidgets().get(0).getRectangle().toRectangle();
        float size = 100;
        while (!addParagraph(pdfDoc, createMovieParagraph(movie, size), canvas, rect) && size > 6) {
            size -= 0.2;
        }
        addParagraph(pdfDoc, createMovieParagraph(movie, size), canvas, rect);
        // fill out the year and change the background color
        form.getField(YEAR).setBackgroundColor(color);
        form.getField(YEAR).setValue(String.valueOf(movie.getYear()));
        form.flattenFields();
        // flatten the form and close the stamper
        pdfDoc.close();
        return baos.toByteArray();
    }

    /**
     * Add a paragraph at an absolute position.
     *
     * @param p      the paragraph that needs to be added
     * @param canvas the canvas on which the paragraph needs to be drawn
     * @param rect   the field position
     * @return true if the paragraph didn't fit the rectangle
     */
    public boolean addParagraph(PdfDocument pdfDoc, Paragraph p, PdfCanvas canvas, Rectangle rect) {
        try {
            new Canvas(canvas, pdfDoc, rect).add(p);
        } catch (PdfException e) {
            if (PdfException.ElementCannotFitAnyArea.equals(e.getMessage())) {
                return false;
            } else {
                throw e;
            }
        }
        return true;
    }

    /**
     * Creates a paragraph containing info about a movie
     *
     * @param movie    the Movie pojo
     * @param fontsize the font size
     * @return a Paragraph object
     */
    public Paragraph createMovieParagraph(Movie movie, float fontsize) {
        PdfFont normal = null;
        PdfFont bold = null;
        PdfFont italic = null;
        try {
            normal = PdfFontFactory.createFont(FontConstants.HELVETICA);
            bold = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
            italic = PdfFontFactory.createFont(FontConstants.HELVETICA_OBLIQUE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Paragraph p = new Paragraph().setFixedLeading(fontsize * 1.2f);
        p.setFont(normal).setFontSize(fontsize).setTextAlignment(TextAlignment.JUSTIFIED);
        p.add(new Text(movie.getMovieTitle()).setFont(bold));
        if (movie.getOriginalTitle() != null) {
            p.add(" ");
            p.add(new Text(movie.getOriginalTitle()).setFont(italic));
        }
        p.add(new Text(String.format("; run length: %s", movie.getDuration())).setFont(normal));
        p.add(new Text("; directed by:").setFont(normal));
        for (Director director : movie.getDirectors()) {
            p.add(" ");
            p.add(director.getGivenName());
            p.add(", ");
            p.add(director.getName());
        }
        return p;
    }

    protected void manipulatePdf(String dest) throws Exception {
        createTemplate(TEMPLATE);
        // open the connection to the database
        DatabaseConnection connection = new HsqldbConnection("filmfestival");
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));

        PdfDocument stamper = null;
        ByteArrayOutputStream baos = null;
        PdfAcroForm form = null;
        int count = 0;
        for (Movie movie : PojoFactory.getMovies(connection)) {
            if (count == 0) {
                baos = new ByteArrayOutputStream();
                stamper = new PdfDocument(new PdfReader(RESOURCE), new PdfWriter(baos));
                form = PdfAcroForm.getAcroForm(stamper, true);
                // form.flattenFields();
            }
            count++;
            PdfReader ad = new PdfReader(new ByteArrayInputStream(fillTemplate(TEMPLATE, movie)));
            PdfDocument srcDoc = new PdfDocument(ad);
            PdfPage curPage = srcDoc.getFirstPage();
            PdfFormXObject xObject = curPage.copyAsFormXObject(stamper);
            PdfButtonFormField bt = (PdfButtonFormField) form.getField("movie_" + count);

            CustomButton button = new CustomButton(bt);
            button.setFormXObject(xObject);

            // Listing_08_06_ReplaceIcon.CustomButton button = new Listing_08_06_ReplaceIcon.CustomButton(bt);

            // TODO No setLayout & setProportionalIcon & setFormXObject on PdfFormField
            // bt.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
            // bt.setProportionalIcon(true);
            // bt.setFormXObject(page);
            //form = PdfAcroForm.getAcroForm(stamper, true);
            //form.removeField("movie_" + count);
            //form.addField(bt);
            if (count == 16) {
                stamper.close();
                stamper = new PdfDocument(new PdfReader(new ByteArrayInputStream(baos.toByteArray())));
                stamper.copyPagesTo(1, 1, pdfDoc);
                stamper.close();
                count = 0;
            }
        }
        if (count > 0) {
            stamper.close();
            stamper = new PdfDocument(new PdfReader(new ByteArrayInputStream(baos.toByteArray())));
            stamper.copyPagesTo(1, 1, pdfDoc);
            stamper.close();
        }
        pdfDoc.close();
        // close the database connection
        connection.close();
    }

    class CustomButton extends AbstractElement<Listing_08_06_ReplaceIcon.CustomButton> implements ILeafElement, IAccessibleElement {

        protected PdfName role = PdfName.Figure;
        protected PdfButtonFormField button;
        protected String caption;
        protected ImageData image;
        protected PdfFormXObject formXObject;

        protected Rectangle rect;
        protected Color borderColor = Color.BLACK;
        protected Color buttonBackgroundColor = Color.WHITE;

        public CustomButton(PdfButtonFormField button) {
            this.button = button;
        }

        public PdfFormXObject getFormXObject() {
            return formXObject;
        }

        public void setFormXObject(PdfFormXObject formXObject) {
            this.formXObject = formXObject;
        }

        @Override
        protected IRenderer makeNewRenderer() {
            return new CustomButtonRenderer(this);
        }

        @Override
        public PdfName getRole() {
            return role;
        }

        @Override
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
            return caption == null ? "" : caption;
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

    class CustomButtonRenderer extends AbstractRenderer {

        public CustomButtonRenderer(CustomButton button) {
            super(button);
        }

        @Override
        public LayoutResult layout(LayoutContext layoutContext) {
            LayoutArea area = layoutContext.getArea().clone();
            Rectangle layoutBox = area.getBBox();
            applyMargins(layoutBox, false);
            Listing_08_06_ReplaceIcon.CustomButton modelButton = (Listing_08_06_ReplaceIcon.CustomButton) modelElement;
            occupiedArea = new LayoutArea(area.getPageNumber(), new Rectangle(modelButton.button.getWidgets().get(0).getRectangle().toRectangle()));
            PdfButtonFormField button = ((Listing_08_06_ReplaceIcon.CustomButton) getModelElement()).getButton();
            button.getWidgets().get(0).setRectangle(new PdfArray(occupiedArea.getBBox()));

            return new LayoutResult(LayoutResult.FULL, occupiedArea, null, null);
        }

        @Override
        public void draw(DrawContext drawContext) {
            CustomButton modelButton = (CustomButton) modelElement;
            Rectangle rect = modelButton.button.getWidgets().get(0).getRectangle().toRectangle();
            occupiedArea.setBBox(rect);

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

            new Canvas(canvas, drawContext.getDocument(), new Rectangle(0, 0, width, height)).
                    showTextAligned(paragraph, 1, 1, TextAlignment.LEFT, VerticalAlignment.BOTTOM);

            if (null == modelButton.getFormXObject()) {
                PdfImageXObject imageXObject = new PdfImageXObject(modelButton.getImage());
                float imageWidth = imageXObject.getWidth();
                if (imageXObject.getWidth() > rect.getWidth()) {
                    imageWidth = rect.getWidth();
                } else if (imageXObject.getHeight() > rect.getHeight()) {
                    imageWidth = imageWidth * (rect.getHeight() / imageXObject.getHeight());
                }

                canvas.addXObject(imageXObject, 0.5f, 0.5f, imageWidth - 1);


                PdfButtonFormField button = modelButton.getButton();
                button.getWidgets().get(0).setNormalAppearance(xObject.getPdfObject());
                xObject.getPdfObject().getOutputStream().writeBytes(str.getBytes());
                xObject.getResources().addImage(imageXObject);

                PdfAcroForm.getAcroForm(drawContext.getDocument(), true).addField(button, drawContext.getDocument().getPage(1));
            } else {
                PdfXObject formXObject = modelButton.getFormXObject();
                float imageWidth = formXObject.getWidth();
                if (formXObject.getWidth() > rect.getWidth()) {
                    imageWidth = rect.getWidth();
                } else if (formXObject.getHeight() > rect.getHeight()) {
                    imageWidth = imageWidth * (rect.getHeight() / formXObject.getHeight());
                }

                canvas.addXObject(formXObject, 0.5f, 0.5f, imageWidth - 1);


                PdfButtonFormField button = modelButton.getButton();
                button.getWidgets().get(0).setNormalAppearance(xObject.getPdfObject());
                xObject.getPdfObject().getOutputStream().writeBytes(str.getBytes());
                xObject.getResources().addImage(formXObject.getPdfObject());

                PdfAcroForm.getAcroForm(drawContext.getDocument(), true).addField(button, drawContext.getDocument().getPage(1));

            }
        }

        @Override
        public IRenderer getNextRenderer() {
            return null;
        }
    }

}
