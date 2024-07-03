package com.itextpdf.samples.book.part4.chapter15;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IPdfTextLocation;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;
import com.itextpdf.kernel.pdf.canvas.parser.listener.RegexBasedLocationExtractionStrategy;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

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
 * 		<li>2024/7/1[suxh] 新建</li>
 * </ol>
 */
public class Listing_15_23_SearchKeyword {

    public static void main(String[] args) throws IOException {
//        String keyword = "注师二";
//        String file = "C:\\Users\\suxh\\Desktop\\研究\\15.7-【关键字】特殊情况\\流读出来是倒着\\8e8416aae2954d4aa325b463d3853a0d.pdf";

        //1、旋转文档
//        String keyword = "左下角";
//        String file = "E:\\WXWork\\1688850438039587\\Cache\\File\\2024-06\\4-rotate180.pdf";

        //1、table里跨行的情况(按照排序就会找不到)
        String keyword = "Winter... and Spring";
        String file = "D:\\idea workspace\\i7js-samples\\publications\\book\\target\\test\\resources\\book\\part1\\chapter04\\Listing_04_23_ColumnTable.pdf";
        //返回内部pdf坐标(到时候给业务也是要按照视觉坐标)
        Map<Integer,List<Rectangle>> rectangles = getKeyword(file, keyword);

        //2、按照返回的坐标圈出关键字坐标位置
        pointToKeyword(file, rectangles, "D:\\idea\\old_local_base\\out\\artifacts\\interface_sign\\testfile\\633955_20230419141210_point.pdf");
    }

    private static void pointToKeyword(String src, Map<Integer, List<Rectangle>> rectangles, String dest) throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(dest));
        for(int pageNum : rectangles.keySet()) {
            PdfCanvas pdfCanvas = new PdfCanvas(pdfDoc.getPage(pageNum));
            List<Rectangle> rects = rectangles.get(pageNum);
            for(Rectangle rect : rects) {
                pdfCanvas.setStrokeColor(ColorConstants.RED);
                pdfCanvas.rectangle(rect);
                pdfCanvas.stroke();
            }
        }
        pdfDoc.close();
    }

    /**
     * <b>概要：</b>
     *  获取关键字位置信息
     * <b>作者：</b>suxh</br>
     * <b>日期：</b>2024/7/1 16:58</br>
     * @param src
     * @param keyword
     * @return
     **/
    public static Map<Integer,List<Rectangle>> getKeyword(String src, String keyword) throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(new ByteArrayOutputStream()));
        RegexBasedLocationExtractionStrategy strategy = new RegexBasedLocationExtractionStrategy(keyword);
        PdfCanvasProcessor parser = new PdfCanvasProcessor(strategy);
        PdfPage page = null;
        Map<Integer,List<Rectangle>> retval = new HashMap<>();
        List<Rectangle> eachPageResult = null;
        for (int i = 1; i <= pdfDoc.getNumberOfPages(); i++) {
            eachPageResult = new ArrayList<>();
            page = pdfDoc.getPage(i);
            parser.processPageContent(page);
            for(IPdfTextLocation l : strategy.getResultantLocations()) {
                eachPageResult.add(l.getRectangle());
            }
            if(eachPageResult.size() != 0) {
                retval.put(i,eachPageResult);
            }
        }
        pdfDoc.close();
        return retval;
    }
}
