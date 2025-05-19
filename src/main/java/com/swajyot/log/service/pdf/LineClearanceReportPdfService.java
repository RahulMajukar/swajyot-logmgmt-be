package com.swajyot.log.service.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.swajyot.log.model.LineClearanceReport;

@Service
public class LineClearanceReportPdfService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss");
    private static final float BORDER_WIDTH = 0.5f;
    private static final Border SOLID_BORDER = new SolidBorder(ColorConstants.BLACK, BORDER_WIDTH);
    private static final DeviceRgb HEADER_BG_COLOR = new DeviceRgb(230, 230, 230);

    // Font sizes
    private static final float COMPANY_NAME_FONT_SIZE = 16f;
    private static final float TITLE_FONT_SIZE = 12f;
    private static final float SUBTITLE_FONT_SIZE = 10f;
    private static final float CONTENT_FONT_SIZE = 9f;
    private static final float SMALL_FONT_SIZE = 8f;

    public byte[] generatePdf(LineClearanceReport report, String username) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        // Optimized margins for better content fit
        document.setMargins(15, 36, 15, 36); // top, right, bottom, left
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

//        addHeader(document, report, fontBold);
        // Use the common header component
        PdfCommonComponents.addHeader(document, report, fontBold);
        addProductInfo(document, report, font, fontBold);
        addCheckPointsTable(document, report, font, fontBold);
        addSignatureSection(document, report, font, fontBold);
        
        // Add username at bottom of the PDF
        String generatedTime = java.time.LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        Paragraph usernameFooter = new Paragraph("Downloaded by: " + username + " on " + generatedTime)
                .setFontSize(SMALL_FONT_SIZE)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginTop(10);

        document.add(usernameFooter);

        document.close();
        return baos.toByteArray();
    }

    private void addProductInfo(Document document, LineClearanceReport report, PdfFont font, PdfFont fontBold) {
        Table infoTable = new Table(UnitValue.createPercentArray(new float[]{55, 22, 23}))
                .setWidth(UnitValue.createPercentValue(100))
                .setFontSize(CONTENT_FONT_SIZE);

        // Date and Shift row
        Cell dateLabel = new Cell()
                .add(new Paragraph("DATE:").setFont(fontBold))
                .setBorder(SOLID_BORDER)
                .setPadding(3);
        
        String dateStr = report.getReportDate() != null ? report.getReportDate().format(DATE_FORMATTER) : "";
        Cell dateValue = new Cell()
                .add(new Paragraph(dateStr))
                .setBorder(SOLID_BORDER)
                .setPadding(3);
        
        Cell shiftLabel = new Cell()
                .add(new Paragraph("SHIFT: " + report.getShift()))
                .setBorder(SOLID_BORDER)
                .setPadding(3);
        
        infoTable.addCell(dateLabel);
        infoTable.addCell(dateValue);
        infoTable.addCell(shiftLabel);

        // Product details
        Cell nameOfProductLabel = new Cell()
                .add(new Paragraph("NAME OF PRODUCT:").setFont(fontBold))
                .setBorder(SOLID_BORDER)
                .setPadding(3);
        
        Cell productName = new Cell()
                .add(new Paragraph(report.getProductName() != null ? report.getProductName() : ""))
                .setBorder(SOLID_BORDER)
                .setPadding(3);
        
        Cell lineLabel = new Cell()
                .add(new Paragraph("LINE: " + report.getLine()))
                .setBorder(SOLID_BORDER)
                .setPadding(3);
        
        infoTable.addCell(nameOfProductLabel);
        infoTable.addCell(productName);
        infoTable.addCell(lineLabel);

        // Variant description
        Cell variantChangeLabel = new Cell()
                .add(new Paragraph("VARIANT / PRODUCT CHANGE DESCRIPTION:").setFont(fontBold))
                .setBorder(SOLID_BORDER)
                .setPadding(3);
        
        Cell existingVariantDesc = new Cell()
                .add(new Paragraph(report.getExistingVariantDescription() != null ? report.getExistingVariantDescription() : ""))
                .setBorder(SOLID_BORDER)
                .setPadding(3);
        
        Cell newVariantLabel = new Cell()
                .add(new Paragraph("NEW VARIANT / PRODUCT"))
                .setBorder(SOLID_BORDER)
                .setPadding(3);
        
        infoTable.addCell(variantChangeLabel);
        infoTable.addCell(existingVariantDesc);
        infoTable.addCell(newVariantLabel);

        // Variant/Product names
        Cell variantProductNameLabel = new Cell()
                .add(new Paragraph("VARIANT / PRODUCT NAME:").setFont(fontBold))
                .setBorder(SOLID_BORDER)
                .setPadding(3);
        
        Cell existingVariantName = new Cell()
                .add(new Paragraph(report.getExistingVariantName() != null ? report.getExistingVariantName() : ""))
                .setBorder(SOLID_BORDER)
                .setPadding(3);
        
        Cell newVariantName = new Cell()
                .add(new Paragraph(report.getNewVariantName() != null ? report.getNewVariantName() : ""))
                .setBorder(SOLID_BORDER)
                .setPadding(3);
        
        infoTable.addCell(variantProductNameLabel);
        infoTable.addCell(existingVariantName);
        infoTable.addCell(newVariantName);

        // Stop time
        Cell existingStopLabel = new Cell()
                .add(new Paragraph("EXISTING VARIANT / PRODUCT STOP TIME:").setFont(fontBold))
                .setBorder(SOLID_BORDER)
                .setPadding(3);
        
        String stopTime = "";
        if (report.getExistingVariantStopTime() != null) {
            stopTime = report.getExistingVariantStopTime().format(TIME_FORMATTER);
        }
        
        Cell stopTimeValue = new Cell()
                .add(new Paragraph(stopTime))
                .setBorder(SOLID_BORDER)
                .setPadding(3);
        
        Cell amPm1 = new Cell()
                .add(new Paragraph("AM / PM"))
                .setBorder(SOLID_BORDER)
                .setPadding(3);
        
        infoTable.addCell(existingStopLabel);
        infoTable.addCell(stopTimeValue);
        infoTable.addCell(amPm1);

        // Start time
        Cell newStartLabel = new Cell()
                .add(new Paragraph("NEW VARIANT / PRODUCT START TIME:").setFont(fontBold))
                .setBorder(SOLID_BORDER)
                .setPadding(3);
        
        String startTime = "";
        if (report.getNewVariantStartTime() != null) {
            startTime = report.getNewVariantStartTime().format(TIME_FORMATTER);
        }
        
        Cell startTimeValue = new Cell()
                .add(new Paragraph(startTime))
                .setBorder(SOLID_BORDER)
                .setPadding(3);
        
        Cell amPm2 = new Cell()
                .add(new Paragraph("AM / PM"))
                .setBorder(SOLID_BORDER)
                .setPadding(3);
        
        infoTable.addCell(newStartLabel);
        infoTable.addCell(startTimeValue);
        infoTable.addCell(amPm2);

        document.add(infoTable);
    }
    
    private void addCheckPointsTable(Document document, LineClearanceReport report, PdfFont font, PdfFont fontBold) {
        document.add(new Paragraph("\n").setFontSize(3));

        Table table = new Table(UnitValue.createPercentArray(new float[]{7, 48, 25, 20}))
                .setWidth(UnitValue.createPercentValue(100))
                .setFontSize(CONTENT_FONT_SIZE);

        // Table headers
        Cell slNoHeader = new Cell()
                .add(new Paragraph("Sl. No.").setFont(fontBold))
                .setBackgroundColor(HEADER_BG_COLOR)
                .setBorder(SOLID_BORDER)
                .setPadding(3);
        
        Cell checkPointHeader = new Cell()
                .add(new Paragraph("CHECK POINT").setFont(fontBold))
                .setBackgroundColor(HEADER_BG_COLOR)
                .setBorder(SOLID_BORDER)
                .setPadding(3);
        
        Cell responsibilityHeader = new Cell()
                .add(new Paragraph("RESPONSIBILITY").setFont(fontBold))
                .setBackgroundColor(HEADER_BG_COLOR)
                .setBorder(SOLID_BORDER)
                .setPadding(3);
        
        Cell remarksHeader = new Cell()
                .add(new Paragraph("REMARKS").setFont(fontBold))
                .setBackgroundColor(HEADER_BG_COLOR)
                .setBorder(SOLID_BORDER)
                .setPadding(3);
        
        table.addHeaderCell(slNoHeader);
        table.addHeaderCell(checkPointHeader);
        table.addHeaderCell(responsibilityHeader);
        table.addHeaderCell(remarksHeader);

        // Add each check point
        if (report.getCheckPoints() != null) {
            for (Map<String, Object> checkPoint : report.getCheckPoints()) {
                // S.No.
                Long id = ((Number) checkPoint.get("id")).longValue();
                table.addCell(new Cell()
                        .add(new Paragraph(String.valueOf(id)))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBorder(SOLID_BORDER)
                        .setPadding(3));

                // Check Point
                String checkPointText = (String) checkPoint.get("description");
                table.addCell(new Cell()
                        .add(new Paragraph(checkPointText != null ? checkPointText : ""))
                        .setBorder(SOLID_BORDER)
                        .setPadding(3));

                // Responsibility
                String responsibility = (String) checkPoint.get("responsibility");
                table.addCell(new Cell()
                        .add(new Paragraph(responsibility != null ? responsibility : ""))
                        .setBorder(SOLID_BORDER)
                        .setPadding(3));

                // Remarks
                String remarks = (String) checkPoint.get("remarks");
                table.addCell(new Cell()
                        .add(new Paragraph(remarks != null ? remarks : ""))
                        .setBorder(SOLID_BORDER)
                        .setPadding(3));
            }
        }

        document.add(table);
    }

    private void addSignatureSection(Document document, LineClearanceReport report, PdfFont font, PdfFont fontBold) {
        document.add(new Paragraph("\n").setFontSize(5));

        Table signatureTable = new Table(3)
                .setWidth(UnitValue.createPercentValue(100))
                .setBorder(SOLID_BORDER)
                .setFontSize(CONTENT_FONT_SIZE);

        // Header row for "RESPONSIBLE"
        Cell responsibleHeader = new Cell(1, 3)
                .add(new Paragraph("RESPONSIBLE").setFont(fontBold))
                .setTextAlignment(TextAlignment.CENTER)
                .setBorder(SOLID_BORDER)
                .setBackgroundColor(HEADER_BG_COLOR)
                .setPadding(3);
        signatureTable.addCell(responsibleHeader);

        // Header row for NAME and SIGN
        Cell nameHeader = new Cell(1, 2)
                .add(new Paragraph("NAME").setFont(fontBold))
                .setTextAlignment(TextAlignment.CENTER)
                .setBorder(SOLID_BORDER)
                .setBackgroundColor(HEADER_BG_COLOR)
                .setPadding(3);
        signatureTable.addCell(nameHeader);

        Cell signHeader = new Cell()
                .add(new Paragraph("SIGN").setFont(fontBold))
                .setTextAlignment(TextAlignment.CENTER)
                .setBorder(SOLID_BORDER)
                .setBackgroundColor(HEADER_BG_COLOR)
                .setPadding(3);
        signatureTable.addCell(signHeader);

        // PRODUCTION row
        Cell productionLabel = new Cell()
                .add(new Paragraph("PRODUCTION").setFont(fontBold))
                .setBorder(SOLID_BORDER)
                .setPadding(5);
        signatureTable.addCell(productionLabel);

        Cell productionName = new Cell()
                .add(new Paragraph(report.getProductionName() != null ? report.getProductionName() : ""))
                .setBorder(SOLID_BORDER)
                .setPadding(5);
        signatureTable.addCell(productionName);

        Cell productionSign = new Cell().setBorder(SOLID_BORDER).setPadding(5);
        // Add production signature if available
        if (report.getProductionSignature() != null && !report.getProductionSignature().isEmpty()) {
            try {
                ClassPathResource resource = new ClassPathResource("static/images/OperatorSign.png");
                Image productionSignImg = new Image(
                        ImageDataFactory.create(resource.getInputStream().readAllBytes()))
                        .scaleToFit(100, 30);
                productionSign.add(productionSignImg);
            } catch (Exception e) {
                productionSign.add(new Paragraph(report.getProductionName() != null ? report.getProductionName() + " (signed)" : ""));
            }
        }
        signatureTable.addCell(productionSign);

        // QUALITY row
        Cell qualityLabel = new Cell()
                .add(new Paragraph("QUALITY").setFont(fontBold))
                .setBorder(SOLID_BORDER)
                .setPadding(5);
        signatureTable.addCell(qualityLabel);

        Cell qualityName = new Cell()
                .add(new Paragraph(report.getQualityName() != null ? report.getQualityName() : ""))
                .setBorder(SOLID_BORDER)
                .setPadding(5);
        signatureTable.addCell(qualityName);

        Cell qualitySign = new Cell().setBorder(SOLID_BORDER).setPadding(5);
        // Add quality signature if available
        if (report.getQualitySignature() != null && !report.getQualitySignature().isEmpty()) {
            try {
                ClassPathResource resource = new ClassPathResource("static/images/QASign.png");
                Image qualitySignImg = new Image(
                        ImageDataFactory.create(resource.getInputStream().readAllBytes()))
                        .scaleToFit(100, 30);
                qualitySign.add(qualitySignImg);
            } catch (Exception e) {
                qualitySign.add(new Paragraph(report.getQualityName() != null ? report.getQualityName() + " (signed)" : ""));
            }
        }
        signatureTable.addCell(qualitySign);

        document.add(signatureTable);
    }
}