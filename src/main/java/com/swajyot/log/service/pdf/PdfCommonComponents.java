package com.swajyot.log.service.pdf;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

import org.springframework.core.io.ClassPathResource;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.swajyot.log.model.ReportHeader;

public class PdfCommonComponents {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final float BORDER_WIDTH = 0.5f;
    private static final Border SOLID_BORDER = new SolidBorder(ColorConstants.BLACK, BORDER_WIDTH);
    private static final DeviceRgb HEADER_BG_COLOR = new DeviceRgb(230, 230, 230);

    // Font sizes
    private static final float COMPANY_NAME_FONT_SIZE = 16f;
    private static final float SUBTITLE_FONT_SIZE = 10f;
    private static final float CONTENT_FONT_SIZE = 9f;
    private static final float SMALL_FONT_SIZE = 8f;
    
    /**
     * Adds a standard header to any PDF document that implements ReportHeader
     * 
     * @param document The PDF document
     * @param report The report data implementing ReportHeader interface
     * @param fontBold Bold font to use
     * @throws IOException If image loading fails
     */
    public static void addHeader(Document document, ReportHeader report, PdfFont fontBold) throws IOException {
        // Create a 3-column table for the header with collapsed borders
        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{30, 40, 30}))
                .setWidth(UnitValue.createPercentValue(100))
                .setBorder(SOLID_BORDER)
                .setPadding(0)
                .setMargin(0);

        // Column 1: Document Info
        Table docInfoTable = createDocumentInfoTable(report, fontBold);
        Cell docInfoCell = new Cell()
                .add(docInfoTable)
                .setBorder(Border.NO_BORDER)
                .setBorderRight(SOLID_BORDER)
                .setPadding(0);
        headerTable.addCell(docInfoCell);

        // Column 2: Title
        Table titleTable = createTitleTable(report, fontBold);
        Cell titleCell = new Cell()
                .add(titleTable)
                .setBorder(Border.NO_BORDER)
                .setBorderRight(SOLID_BORDER)
                .setPadding(0);
        headerTable.addCell(titleCell);

        // Column 3: Logo
        Image logo;
        try {
            ClassPathResource resource = new ClassPathResource("static/images/agilogo.png");
            logo = new Image(ImageDataFactory.create(resource.getInputStream().readAllBytes()));
        } catch (Exception e) {
            byte[] placeholder = Base64.getDecoder().decode(
                    "iVBORw0KGgoAAAANSUhEUgAAAGQAAABkCAIAAAD/gAIDAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyJpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMy1jMDExIDY2LjE0NTY2MSwgMjAxMi8wMi8wNi0xNDo1NjoyNyAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENTNiAoV2luZG93cykiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6NTkzRTlDQkUyMEU5MTFFQTk4RkNBRkJDODVCRTZCODMiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6NTkzRTlDQkYyMEU5MTFFQTk4RkNBRkJDODVCRTZCODMiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDo1OTNFOUNCQzIwRTkxMUVBOThGQ0FGQkM4NUJFNkI4MyIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDo1OTNFOUNCRDIwRTkxMUVBOThGQ0FGQkM4NUJFNkI4MyIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/PpR4Hs4AAAgISURBVHja7JxrbBRVFMfPnd3Z3S7d0pWWbYEi5VGMWAPxAZgoBsLDGISoRIN8IcaYKJ+MSvxgCD7wQwkJMTHRRGIk8YMJGk0kBIwvSDRCRMFAIgYUaAHb0hf7nN0ZzwxndnZ3Znfb6ba7c+C07cydu3vv/57//5x7Z2dNQggMx8IyTGAYtjBsYdjCsIVhC8MWhi0MWxi2MGxh2MKwhWELw9Yw1m/O/Q8tCyYzRGmNDaFQMDyQSuA4Dviv1BobAMdR39d1xDl3HOq6jmVNcM1fsdpaIgQIQQD5f5ybpYfRUCXTnE6nwHEyU2/JskgTQizLEkIEQRAEQRRFnudr7TEfQUF8JEl0Y5qZCDAGRifBp8rGRgXgNJUa4ZKlGK4Mm2dnWf1Q30VFAZqYikSOKoqqquGwlP8eRcQDaiiKarooBIDU1NQtXBiBJ0pUgJd7dRtlZQVwoQQRCvE8D/C6LsU1Lpu6D3A6nYbfG24QQjRNczgc+eVcVKLJ9wJSoWoaQC5aDYWigXUcL8t6NBp2OADPcznAo7RFIpFYLOZwOEzWa+DLTIJlZCSJPP5YcLYdXVYUzYz5KQfTGhoa7rrrruXLl4dCIT6vB2nLqxcfPnzY4XDMnDmTZxaVp6xlyGxRIqyUWM37UPkMiouyzJZjwc5SAyGkqmogEFixYsXZs2e//vprmyfzgBgIBJLJZHt7O0+ZYEUd/Pju6JBVNZ9FZctR/Lnvn3PZKQRnwTxDsJlM5vHHH+/r6/v6669nzJhhcXIFYrt27XrooYduvvlmi0t46Rlf5aZ7TU1NLpfL5XK5XE6n0+l0u9yiKAqiwPN8JpPq7e21Xu/sHwwN9RA1BKPBT4lrGlJVVVGUTCaTSCQGBgb6+vquX7/e39//2GOP9fb2fvXVV1OnTrUYzOXLl7/88st9+/ZZXMJLr3rNQ6FQU1NTe3t7W1vblClTmpubXS7XwEBU06QTJ46LSqdlcjfbR2CUPXsufXju8kBU5ngfPcUYERFtMElJDdHfTU1Nd9xxx6pVq7xe75YtW15//fXe3t6vvvrK7/fbOhRCyJYtWzZs2DA4ONjS0mIN3lJ8M6yt7e3tXbZs2Z133mm4UT7uTgYG0q9/8HXw/OnQgFf3+yBeIIXfGCf6FI2mqaZpiURiYGCAYqS72Lx58zPPPNPR0XHo0KG5c+dajOaLL75YuHDha6+9Zt2DJcs1TnS5XAsWLJg+fTohJJVKaZrW19d38eJFnuc9Hk9zc7PP5+N5LpWSXn75vS++unH8ZPrqNYUQJxUZzLVYVBcZGJEkSZKkdDqdTCYlSRoaGlIUJZ1O9/T0NDY2iqLY2dnZ1NRk8SDPnj27cePGl156ieOsXUMr9TqkiGR0Op3zrwk6nU6fz+f3+1taWnw+HyFEVbWvvz31ye7rv/xGYrEoz1MwR3wDwJFEMjk0NBSJRPr7+3/77bdLly4dOXLkp59+2r9//+HDh48dO3bhwoXVq1fbOk44HH7xxRfXrVvX0dFRJnhLzUKCINBhNZvNZrPZbDZL3a6RSCSRSPh8Po/HI8syn0yqv1+JZLME+xxYP+RCoUVcEgQhEAjU1dX5/f729vahUEgURa/X29LS4vF4RFGMRCLP/vPi3r1777nnnvr6enunef/99+fNm9fa2lpO9pVWDiw0TUskEocOHfrhhx8OHDhw5syZeDweDodvv/32OXPmdHV1zZ8/f+bMme3t7YIgIISu/9GPEKqwABYuXLh79+6DBw8+8MADtk7T3d39zjvvbNu2rZyA5tDq0UfpvtFRo/0qPVAGYkYs3QIkSQoGgx6PR5ZlVVUDgUBXV1ddXd3DDz+8cOHCu+++e8aMGTSfvnz5w2eeebOvbwivbCgEcIvIQnSZInrHFFWmU4uyVF31OBRDqMbmfz737LPPvvnmm7bwOXfu3IoVK372iy/+Y7J9Q7VuM7bZ5KweFiCErHcZFoYtbGHYGpO/sQ17U1i2MGwNM9vCsDVsYdi6dU+djb3FGSPQmTaEEPiKgZ1xS3WZ3M3TYolWKpXq6ur66aefEokE5y+Hxd66kKqqtbW1W7duraurKzmLLOqWTrx1dHQcP358586dhw4dunbtWiQSoYcZxeUv9s0OwGqz2fZAINCazWZDodCePXs2bdp0/vz5wf8M/q3QEYvF7r///s8++2zNmjXlxbJoQmiOAo7jes8ddXV1dUvCGlP0VFJEl1dXV1dXV1d3e/eePXs2bdp0/vz5wf8M/q3QEYvF7r///s8++2zNmjXlxbJoQmiOAo7jes8ddXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXU=");
            logo = new Image(ImageDataFactory.create(placeholder));
        }

        // Optimized logo size and positioning
        logo.setWidth(UnitValue.createPercentValue(80));
        logo.setAutoScale(true);
        logo.setMargins(20,20,20,20);

        Cell logoCell = new Cell()
                .add(logo)
                .setBorder(Border.NO_BORDER)
                .setPadding(5)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setHorizontalAlignment(HorizontalAlignment.CENTER);

        headerTable.addCell(logoCell);
        document.add(headerTable);
    }

    public static Table createDocumentInfoTable(ReportHeader report, PdfFont fontBold) {
        Table docInfoTable = new Table(UnitValue.createPercentArray(new float[]{40, 60}))
                .setWidth(UnitValue.createPercentValue(100));
        docInfoTable.setFontSize(SMALL_FONT_SIZE);
        docInfoTable.setMargin(0);
        docInfoTable.setPadding(0);

        addDocInfoRow(docInfoTable, "Document No. :", report.getDocumentNo(), fontBold);
        addDocInfoRow(docInfoTable, "Revision :", report.getRevision(), fontBold);
        String effectiveDate = report.getEffectiveDate() != null ? report.getEffectiveDate().format(DATE_FORMATTER) : "";
        addDocInfoRow(docInfoTable, "Effective Date :", effectiveDate, fontBold);
        String reviewedOn = report.getReviewedOn() != null ? report.getReviewedOn().format(DATE_FORMATTER) : "";
        addDocInfoRow(docInfoTable, "Reviewed on :", reviewedOn, fontBold);
        addDocInfoRow(docInfoTable, "Page :", report.getPage(), fontBold);
        addDocInfoRow(docInfoTable, "Prepared By :", report.getPreparedBy(), fontBold);
        addDocInfoRow(docInfoTable, "Approved by :", report.getApprovedBy(), fontBold);
        addDocInfoRow(docInfoTable, "Issued :", report.getIssued(), fontBold);

        return docInfoTable;
    }

    public static void addDocInfoRow(Table table, String label, String value, PdfFont fontBold) {
        Cell labelCell = new Cell()
                .add(new Paragraph(label).setFont(fontBold))
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(SOLID_BORDER)
                .setBorderRight(SOLID_BORDER)
                .setPadding(3)
                .setHeight(14);

        Cell valueCell = new Cell()
                .add(new Paragraph(value != null ? value : ""))
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(SOLID_BORDER)
                .setPadding(3)
                .setHeight(14);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    public static Table createTitleTable(ReportHeader report, PdfFont fontBold) {
        Table titleTable = new Table(1)
                .setWidth(UnitValue.createPercentValue(100))
                .setMargin(0);

        // Company name with variable font size
        Paragraph companyName = new Paragraph("AGI Greenpac Limited")
                .setFont(fontBold)
                .setFontSize(COMPANY_NAME_FONT_SIZE)
                .setMarginTop(20)
                .setTextAlignment(TextAlignment.CENTER);

        Cell companyCell = new Cell()
                .add(companyName)
                .setBorder(Border.NO_BORDER)
                .setPadding(3)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);

        titleTable.addCell(companyCell);

        // Unit name
        Paragraph unitName = new Paragraph("Unit :- " + report.getUnit())
                .setFontSize(SUBTITLE_FONT_SIZE)
                .setMarginBottom(20)
                .setTextAlignment(TextAlignment.CENTER);

        Cell unitCell = new Cell()
                .add(unitName)
                .setBorder(Border.NO_BORDER)
                .setPadding(3)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);

        titleTable.addCell(unitCell);

        // Add spacing
        Cell spaceCell = new Cell()
                .setHeight(10)
                .setBorder(Border.NO_BORDER);

        titleTable.addCell(spaceCell);

        // Create a table for SCOPE with proper borders and sizing
        Table scopeTable = new Table(UnitValue.createPercentArray(new float[]{25, 75}))
                .setWidth(UnitValue.createPercentValue(100));

        Cell scopeLabelCell = new Cell()
                .add(new Paragraph("SCOPE:").setFont(fontBold).setFontSize(CONTENT_FONT_SIZE))
                .setBorder(SOLID_BORDER)
                .setPadding(4)
                .setTextAlignment(TextAlignment.LEFT);
        
        Cell scopeValueCell = new Cell()
                .add(new Paragraph(report.getScope()).setFontSize(CONTENT_FONT_SIZE))
                .setBorder(SOLID_BORDER)
                .setPadding(4)
                .setTextAlignment(TextAlignment.CENTER);

        scopeTable.addCell(scopeLabelCell);
        scopeTable.addCell(scopeValueCell);

        // Add the SCOPE table to the main title table
        Cell scopeTableCell = new Cell()
                .add(scopeTable)
                .setBorder(Border.NO_BORDER)
                .setPadding(0);

        titleTable.addCell(scopeTableCell);

        Table titleContentTable = new Table(UnitValue.createPercentArray(new float[]{25, 75}))
                .setWidth(UnitValue.createPercentValue(100));

        Cell titleLabelCell = new Cell()
                .add(new Paragraph("TITLE :").setFont(fontBold).setFontSize(CONTENT_FONT_SIZE))
                .setBorder(SOLID_BORDER)
                .setPadding(4)
                .setTextAlignment(TextAlignment.LEFT);
        
        Cell titleValueCell = new Cell()
                .add(new Paragraph(report.getTitle())
                        .setFontSize(CONTENT_FONT_SIZE)
                .setPadding(4)
                .setTextAlignment(TextAlignment.CENTER));

        titleContentTable.addCell(titleLabelCell);
        titleContentTable.addCell(titleValueCell);

        // Add the TITLE table to the main title table
        Cell titleTableCell = new Cell()
                .add(titleContentTable)
                .setBorder(Border.NO_BORDER)
                .setPadding(0);

        titleTable.addCell(titleTableCell);

        return titleTable;
    }
}