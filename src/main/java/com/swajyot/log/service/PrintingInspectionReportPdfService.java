package com.swajyot.log.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Map;

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
import com.swajyot.log.model.PrintingInspectionReport;

@Service
public class PrintingInspectionReportPdfService {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss");
	private static final float BORDER_WIDTH = 0.5f;
	private static final Border SOLID_BORDER = new SolidBorder(ColorConstants.BLACK, BORDER_WIDTH);
	private static final DeviceRgb HEADER_BG_COLOR = new DeviceRgb(230, 230, 230);

	// Variable font sizes based on content length and available space
	private static final float COMPANY_NAME_FONT_SIZE = 16f;
	private static final float TITLE_FONT_SIZE = 12f;
	private static final float SUBTITLE_FONT_SIZE = 10f;
	private static final float CONTENT_FONT_SIZE = 9f;
	private static final float SMALL_FONT_SIZE = 8f;

	public byte[] generatePdf(PrintingInspectionReport report, String username) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        // Optimized margins for better content fit
        document.setMargins(15, 36, 15, 36); // top, right, bottom, left
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

        addHeader(document, report, fontBold);
        addHeaderInfo(document, report, font, fontBold);
        addInkTable(document, report, font, fontBold);
        addCharacteristicsTable(document, report, font, fontBold);
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

	private void addTableHeader(Table table, String text, PdfFont fontBold) {
		table.addHeaderCell(new Cell().add(new Paragraph(text).setFont(fontBold)).setBackgroundColor(HEADER_BG_COLOR)
				.setBorder(SOLID_BORDER).setPadding(3));
	}


	private void addHeader(Document document, PrintingInspectionReport report, PdfFont fontBold) throws IOException {
		// Create a 3-column table for the header with collapsed borders
		Table headerTable = new Table(UnitValue.createPercentArray(new float[] { 30, 40, 30 }))
				.setWidth(UnitValue.createPercentValue(100)).setBorder(SOLID_BORDER).setPadding(0).setMargin(0);

		// Column 1: Document Info
		Table docInfoTable = createDocumentInfoTable(report, fontBold);
		Cell docInfoCell = new Cell().add(docInfoTable).setBorder(Border.NO_BORDER).setBorderRight(SOLID_BORDER)
				.setPadding(0);
		headerTable.addCell(docInfoCell);

		// Column 2: Title
		Table titleTable = createTitleTable(report, fontBold);
		Cell titleCell = new Cell().add(titleTable).setBorder(Border.NO_BORDER).setBorderRight(SOLID_BORDER)
				.setPadding(0);
		headerTable.addCell(titleCell);

		// Column 3: Logo
		Image logo;
		try {
			ClassPathResource resource = new ClassPathResource("static/images/agilogo.png");
			logo = new Image(ImageDataFactory.create(resource.getInputStream().readAllBytes()));
		} catch (Exception e) {
			byte[] placeholder = Base64.getDecoder().decode(
					"iVBORw0KGgoAAAANSUhEUgAAAGQAAABkCAIAAAD/gAIDAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyJpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMy1jMDExIDY2LjE0NTY2MSwgMjAxMi8wMi8wNi0xNDo1NjoyNyAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENTNiAoV2luZG93cykiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6NTkzRTlDQkUyMEU5MTFFQTk4RkNBRkJDODVCRTZCODMiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6NTkzRTlDQkYyMEU5MTFFQTk4RkNBRkJDODVCRTZCODMiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDo1OTNFOUNCQzIwRTkxMUVBOThGQ0FGQkM4NUJFNkI4MyIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDo1OTNFOUNCRDIwRTkxMUVBOThGQ0FGQkM4NUJFNkI4MyIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/PpR4Hs4AAAgISURBVHja7JxrbBRVFMfPnd3Z3S7d0pWWbYEi5VGMWAPxAJgoBsLDGISoRIN8IcaYKJ+MSvxgCD7wQwkJMTHRRGIk8YMJGk0kBIwvSDRCRMFAIgYUaAHb0hf7nN0ZzwxndnZ3Znfb6ba7c+C07cydu3vv/57//5x7Z2dNQggMx8IyTGAYtjBsYdjCsIVhC8MWhi0MWxi2MGxh2MKwhWELw9Yw1m/O/Q8tCyYzRGmNDaFQMDyQSuA4Dviv1BobAMdR39d1xDl3HOq6jmVNcM1fsdpaIgQIQQD5f5ybpYfRUCXTnE6nwHEyU2/JskgTQizLEkIEQRAEQRRFnudr7TEfQUF8JEl0Y5qZCDAGRifBp8rGRgXgNJUa4ZKlGK4Mm2dnWf1Q30VFAZqYikSOKoqqquGwlP8eRcQDaiiKarooBIDU1NQtXBiBJ0pUgJd7dRtlZQVwoQQRCvE8D/C6LsU1Lpu6D3A6nYbfG24QQjRNczgc+eVcVKLJ9wJSoWoaQC5aDYWigXUcL8t6NBp2OADPcznAo7RFIpFYLOZwOEzWa+DLTIJlZCSJPP5YcLYdXVYUzYz5KQfTGhoa7rrrruXLl4dCIT6vB2nLqxcfPnzY4XDMnDmTZxaVp6xlyGxRIqyUWM37UPkMiouyzJZjwc5SAyGkqmogEFixYsXZs2e//vprmyfzgBgIBJLJZHt7O0+ZYEUd/Pju6JBVNZ9FZctR/Lnvn3PZKQRnwTxDsJlM5vHHH+/r6/v6669nzJhhcXIFYrt27XrooYduvvlmi0t46Rlf5aZ7TU1NLpfL5XK5XE6n0+l0u9yiKAqiwPN8JpPq7e21Xu/sHwwN9RA1BKPBT4lrGlJVVVGUTCaTSCQGBgb6+vquX7/e39//2GOP9fb2fvXVV1OnTrUYzOXLl7/88st9+/ZZXMJLr3rNQ6FQU1NTe3t7W1vblClTmpubXS7XwEBU06QTJ46LSqdlcjfbR2CUPXsufXju8kBU5ngfPcUYERFtMElJDdHfTU1Nd9xxx6pVq7xe75YtW1577bXe3t6vvvrK7/fbOhRCyJYtWzZs2DA4ONjS0mIN3lJ8M6yt7e3tXbZs2Z133mm4UT7uTgYG0q9/8HXw/OnQgFf3+yBeIIXfGCf6FI2mqaZpiURiYGCAYqS72Lx58zPPPNPR0XHo0KG5c+dajOaLL75YuHDha6+9Zt2DJcs1TnS5XAsWLJg+fTohJJVKaZrW19d38eJFnuc9Hk9zc7PP5+N5LpWSXn75vS++unH8ZPrqNYUQJxUZzLVYVBcZGJEkSZKkdDqdTCYlSRoaGlIUJZ1O9/T0NDY2iqLY2dnZ1NRk8SDPnj27cePGl156ieOsXUMr9TqkiGR0Op3zrwk6nU6fz+f3+1taWnw+HyFEVbWvvz31ye7rv/xGYrEoz1MwR3wDwJFEMjk0NBSJRPr7+3/77bdLly4dOXLkp59+2r9//+HDh48dO3bhwoXVq1fbOk44HH7xxRfXrVvX0dFRJnhLzUKCINBhNZvNZrPZbDZL3a6RSCSRSPh8Po/HI8syn0yqv1+JZLME+xxYP+RCoUVcEgQhEAjU1dX5/f729vahUEgURa/X29LS4vF4RFGMRCLP/vPi3r1777nnnvr6enunef/99+fNm9fa2lpO9pVWDiw0TUskEocOHfrhhx8OHDhw5syZeDweDodvv/32OXPmdHV1zZ8/f+bMme3t7YIgIISu/9GPEKqwABYuXLh79+6DBw8+8MADtk7T3d39zjvvbNu2rZyA5tDq0UfpvtFRo/0qPVAGYkYs3QIkSQoGgx6PR5ZlVVUDgUBXV1ddXd3DDz+8cOHCu+++e8aMGTSfvnz5w2eeebOvbwivbCgEcIvIQnSZInrHFFWmU4uyVF31OBRDqMbmfz737LPPvvnmm7bwOXfu3IoVK372iy/+Y7J9Q7VuM7bZ5KweFiCErHcZFoYtbGHYGpO/sQ17U1i2MGwNM9vCsDVsYdi6dU+djb3FGSPQmTaEEPiKgZ1xS3WZ3M3TYolWKpXq6ur66aefEokE5y+Hxd66kKqqtbW1W7duraurKzmLLOqWTrx1dHQcP358586dhw4dunbtWiQSoYcZxeUv9s0OwGqz2fZAINCazWZDodCePXs2bdp0/vz5wf8M/q3QEYvF7r///s8++2zNmjXlxbJoQmiOAo7jes8ddXV1dUvCGlP0VFJEl1dXV1dXV1d3e/eePXs2bdp0/vz5wf8M/q3QEYvF7r///s8++2zNmjXlxbJoQmiOAo7jes8ddXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXU=");
			logo = new Image(ImageDataFactory.create(placeholder));
		}

		// Optimized logo size and positioning for better display
		logo.setWidth(UnitValue.createPercentValue(80));
		logo.setAutoScale(true);
		logo.setMargins(20, 20, 20, 20);

		Cell logoCell = new Cell().add(logo).setBorder(Border.NO_BORDER).setPadding(5)
				.setVerticalAlignment(VerticalAlignment.MIDDLE).setHorizontalAlignment(HorizontalAlignment.CENTER);

		headerTable.addCell(logoCell);
		document.add(headerTable);
	}

	private Table createDocumentInfoTable(PrintingInspectionReport report, PdfFont fontBold) {
		Table docInfoTable = new Table(UnitValue.createPercentArray(new float[] { 40, 60 }))
				.setWidth(UnitValue.createPercentValue(100));
		docInfoTable.setFontSize(SMALL_FONT_SIZE);
		docInfoTable.setMargin(0);
		docInfoTable.setPadding(0);

		addDocInfoRow(docInfoTable, "Document No. :", report.getDocumentNo(), fontBold);
		addDocInfoRow(docInfoTable, "Revision :", report.getRevision(), fontBold);
		String effectiveDate = report.getEffectiveDate() != null ? report.getEffectiveDate().format(DATE_FORMATTER)
				: "";
		addDocInfoRow(docInfoTable, "Effective Date :", effectiveDate, fontBold);
		String reviewedOn = report.getReviewedOn() != null ? report.getReviewedOn().format(DATE_FORMATTER) : "";
		addDocInfoRow(docInfoTable, "Reviewed on :", reviewedOn, fontBold);
		addDocInfoRow(docInfoTable, "Page :", report.getPage(), fontBold);
		addDocInfoRow(docInfoTable, "Prepared By :", report.getPreparedBy(), fontBold);
		addDocInfoRow(docInfoTable, "Approved by :", report.getApprovedBy(), fontBold);
		addDocInfoRow(docInfoTable, "Issued :", report.getIssued(), fontBold);

		return docInfoTable;
	}

	private void addDocInfoRow(Table table, String label, String value, PdfFont fontBold) {
		Cell labelCell = new Cell().add(new Paragraph(label).setFont(fontBold)).setBorder(Border.NO_BORDER)
				.setBorderBottom(SOLID_BORDER).setBorderRight(SOLID_BORDER).setPadding(3).setHeight(14);

		Cell valueCell = new Cell().add(new Paragraph(value != null ? value : "")).setBorder(Border.NO_BORDER)
				.setBorderBottom(SOLID_BORDER).setPadding(3).setHeight(14);

		table.addCell(labelCell);
		table.addCell(valueCell);
	}

	private Table createTitleTable(PrintingInspectionReport report, PdfFont fontBold) {
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
        Paragraph unitName = new Paragraph("Unit :- AGI Speciality Glas Division")
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

        // Always use the same scope for printing reports
        String scopeValue = "AGI / DEC / PRINTING";
        
        Cell scopeValueCell = new Cell()
                .add(new Paragraph(scopeValue).setFontSize(CONTENT_FONT_SIZE))
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

        // Fixed title for printing reports
        String titleValue = "FIRST ARTICLE INSPECTION REPORT - PRINTING";
        
        Cell titleValueCell = new Cell()
                .add(new Paragraph(titleValue)
                        .setFontSize(SMALL_FONT_SIZE)
                        .setTextAlignment(TextAlignment.CENTER));
        titleValueCell.setBorder(SOLID_BORDER);
        titleValueCell.setPadding(4);

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
	
	private void addHeaderInfo(Document document, PrintingInspectionReport report, PdfFont font, PdfFont fontBold) {
	    // Create a table with 3 columns for header info
	    Table infoTable = new Table(UnitValue.createPercentArray(new float[]{34f, 32f, 34f}))
	            .setWidth(UnitValue.createPercentValue(100))
	            .setFontSize(CONTENT_FONT_SIZE);

	    // Column 1
	    Table col1 = new Table(2).setWidth(UnitValue.createPercentValue(100));
	    col1.addCell(new Cell().add(new Paragraph("Date:").setFont(fontBold))
	            .setBorder(SOLID_BORDER).setPadding(3));
	    col1.addCell(new Cell().add(new Paragraph(
	                    report.getInspectionDate() != null ? report.getInspectionDate().format(DATE_FORMATTER) : ""))
	            .setBorder(SOLID_BORDER).setPadding(3));
	    col1.addCell(new Cell().add(new Paragraph("Product:").setFont(fontBold))
	            .setBorder(SOLID_BORDER).setPadding(3));
	    col1.addCell(new Cell().add(new Paragraph(report.getProduct()))
	            .setBorder(SOLID_BORDER).setPadding(3));
	    col1.addCell(new Cell().add(new Paragraph("Customer:").setFont(fontBold))
	            .setBorder(SOLID_BORDER).setPadding(3));
	    col1.addCell(new Cell().add(new Paragraph(report.getCustomer()))
	            .setBorder(SOLID_BORDER).setPadding(3));

	    infoTable.addCell(new Cell().add(col1).setBorder(SOLID_BORDER).setPadding(0));

	    // Column 2
	    Table col2 = new Table(2).setWidth(UnitValue.createPercentValue(100));
	    col2.addCell(new Cell().add(new Paragraph("Shift:").setFont(fontBold))
	            .setBorder(SOLID_BORDER).setPadding(3));
	    col2.addCell(new Cell().add(new Paragraph(report.getShift()))
	            .setBorder(SOLID_BORDER).setPadding(3));
	    col2.addCell(new Cell().add(new Paragraph("Variant:").setFont(fontBold))
	            .setBorder(SOLID_BORDER).setPadding(3));
	    col2.addCell(new Cell().add(new Paragraph(report.getVariant()))
	            .setBorder(SOLID_BORDER).setPadding(3));
	    col2.addCell(new Cell().add(new Paragraph("Size No.:").setFont(fontBold))
	            .setBorder(SOLID_BORDER).setPadding(3));
	    col2.addCell(new Cell().add(new Paragraph(report.getSizeNo()))
	            .setBorder(SOLID_BORDER).setPadding(3));

	    infoTable.addCell(new Cell().add(col2).setBorder(SOLID_BORDER).setPadding(0));

	    // Column 3
	    Table col3 = new Table(2).setWidth(UnitValue.createPercentValue(100));
	    col3.addCell(new Cell().add(new Paragraph("MC No.:").setFont(fontBold))
	            .setBorder(SOLID_BORDER).setPadding(3));
	    col3.addCell(new Cell().add(new Paragraph(report.getMachineNo()))
	            .setBorder(SOLID_BORDER).setPadding(3));
	    col3.addCell(new Cell().add(new Paragraph("Line No.:").setFont(fontBold))
	            .setBorder(SOLID_BORDER).setPadding(3));
	    col3.addCell(new Cell().add(new Paragraph(report.getLineNo()))
	            .setBorder(SOLID_BORDER).setPadding(3));
	    col3.addCell(new Cell().add(new Paragraph("Sample Size:").setFont(fontBold))
	            .setBorder(SOLID_BORDER).setPadding(3));
	    col3.addCell(new Cell().add(new Paragraph(report.getSampleSize()))
	            .setBorder(SOLID_BORDER).setPadding(3));

	    infoTable.addCell(new Cell().add(col3).setBorder(SOLID_BORDER).setPadding(0));

	    document.add(infoTable);
	}

	private void addInkTable(Document document, PrintingInspectionReport report, PdfFont font, PdfFont fontBold) {
	    document.add(new Paragraph("\n").setFontSize(3));

	    Table table = new Table(UnitValue.createPercentArray(new float[]{8, 30, 30, 32}))
	            .setWidth(UnitValue.createPercentValue(100))
	            .setFontSize(CONTENT_FONT_SIZE);

	    addTableHeader(table, "S.No.", fontBold);
	    addTableHeader(table, "Ink / Dye", fontBold);
	    addTableHeader(table, "Batch No.", fontBold);
	    addTableHeader(table, "Expiry Date", fontBold);

	    if (report.getPrintingDetails() != null) {
	        int rowNum = 1;
	        for (Map<String, Object> ink : report.getPrintingDetails()) {
	            // S.No.
	            table.addCell(new Cell().add(new Paragraph(String.valueOf(rowNum++)))
	                    .setTextAlignment(TextAlignment.CENTER)
	                    .setBorder(SOLID_BORDER)
	                    .setPadding(3));

	            // Ink/Dye
	            String inkType = (String) ink.get("inkType");
	            table.addCell(new Cell().add(new Paragraph(inkType != null ? inkType : ""))
	                    .setBorder(SOLID_BORDER)
	                    .setPadding(3));

	            // Batch No.
	            String batchNo = (String) ink.get("batchNo");
	            table.addCell(new Cell().add(new Paragraph(batchNo != null ? batchNo : ""))
	                    .setTextAlignment(TextAlignment.CENTER)
	                    .setBorder(SOLID_BORDER)
	                    .setPadding(3));

	            // Expiry Date
	            String expiryDate = (String) ink.get("expiryDate");
	            table.addCell(new Cell().add(new Paragraph(expiryDate != null ? expiryDate : ""))
	                    .setTextAlignment(TextAlignment.CENTER)
	                    .setBorder(SOLID_BORDER)
	                    .setPadding(3));
	        }
	    }
	    
	    // Add empty rows to complete table (assuming typical inspection report has at least 7 rows)
	    int rowsToAdd = 7;
	    if (report.getPrintingDetails() != null) {
	        rowsToAdd = Math.max(0, rowsToAdd - report.getPrintingDetails().size());
	    }
	    
	    for (int i = 0; i < rowsToAdd; i++) {
	        table.addCell(new Cell().add(new Paragraph(String.valueOf(i + (report.getPrintingDetails() != null ? report.getPrintingDetails().size() : 0) + 1)))
	                .setTextAlignment(TextAlignment.CENTER)
	                .setBorder(SOLID_BORDER)
	                .setPadding(3));
	        
	        table.addCell(new Cell().add(new Paragraph(""))
	                .setBorder(SOLID_BORDER)
	                .setPadding(3));
	                
	        table.addCell(new Cell().add(new Paragraph(""))
	                .setBorder(SOLID_BORDER)
	                .setPadding(3));
	                
	        table.addCell(new Cell().add(new Paragraph(""))
	                .setBorder(SOLID_BORDER)
	                .setPadding(3));
	    }
	    
	    document.add(table);
	}

	private void addCharacteristicsTable(Document document, PrintingInspectionReport report, PdfFont font, PdfFont fontBold) {
	    document.add(new Paragraph("\n").setFontSize(3));

	    Table table = new Table(UnitValue.createPercentArray(new float[]{8, 25, 42, 25}))
	            .setWidth(UnitValue.createPercentValue(100))
	            .setFontSize(CONTENT_FONT_SIZE);

	    addTableHeader(table, "S.No.", fontBold);
	    addTableHeader(table, "Characteristic", fontBold);

	    Cell obsHeader = new Cell(1, 1)
	            .setBorder(SOLID_BORDER)
	            .setBackgroundColor(HEADER_BG_COLOR)
	            .setPadding(3);

	    Paragraph obsHeaderText = new Paragraph()
	            .add(new Text("As per reference sample no. X-100\n").setFont(fontBold))
	            .add(new Text("Observations").setFont(fontBold));

	    obsHeader.add(obsHeaderText);
	    table.addHeaderCell(obsHeader);
	    addTableHeader(table, "Comments", fontBold);

	    // Row 1: Color Shade
	    addCharacteristicRow(table, 1, "Colour Shade", report.getColorShade(), "");

	    // Row 2: Printing Position
	    // Create a sub-table for printing position with vertical and horizontal positions
	    Table positionTable = new Table(UnitValue.createPercentArray(new float[]{30, 70}))
	            .setWidth(UnitValue.createPercentValue(100));

	    // Vertical position
	    Cell verticalLabelCell = new Cell()
	            .add(new Paragraph("Vertical ± 1.0mm").setFont(fontBold))
	            .setTextAlignment(TextAlignment.CENTER)
	            .setBorderRight(SOLID_BORDER)
	            .setBorderBottom(SOLID_BORDER)
	            .setPadding(3)
	            .setHeight(15);
	    positionTable.addCell(verticalLabelCell);

	    Cell verticalValueCell = new Cell()
	            .add(new Paragraph(report.getVerticalPosition() != null ? report.getVerticalPosition() : ""))
	            .setTextAlignment(TextAlignment.CENTER)
	            .setBorderBottom(SOLID_BORDER)
	            .setPadding(3)
	            .setHeight(15);
	    positionTable.addCell(verticalValueCell);

	    // Horizontal position
	    Cell horizontalLabelCell = new Cell()
	            .add(new Paragraph("Horizontal ± 1.0mm").setFont(fontBold))
	            .setTextAlignment(TextAlignment.CENTER)
	            .setBorderRight(SOLID_BORDER)
	            .setPadding(3)
	            .setHeight(15);
	    positionTable.addCell(horizontalLabelCell);

	    Cell horizontalValueCell = new Cell()
	            .add(new Paragraph(report.getHorizontalPosition() != null ? report.getHorizontalPosition() : ""))
	            .setTextAlignment(TextAlignment.CENTER)
	            .setPadding(3)
	            .setHeight(15);
	    positionTable.addCell(horizontalValueCell);

	    // Add position table to main table
	    table.addCell(new Cell().add(new Paragraph("2")).setTextAlignment(TextAlignment.CENTER).setBorder(SOLID_BORDER).setPadding(3));
	    table.addCell(new Cell().add(new Paragraph("Printing Position")).setBorder(SOLID_BORDER).setPadding(3));
	    table.addCell(new Cell().add(positionTable).setBorder(SOLID_BORDER).setPadding(0));
	    table.addCell(new Cell().add(new Paragraph("")).setBorder(SOLID_BORDER).setPadding(3));

	    // Other rows
	    addCharacteristicRow(table, 3, "Deposition of Ink", report.getDepositionOfInk(), "");
	    addCharacteristicRow(table, 4, "Marking Sample", report.getMarkingSample(), "");
	    addCharacteristicRow(table, 5, "Art work / Positive", report.getArtworkPositive(), "");
	    addCharacteristicRow(table, 6, "Bar File", report.getBarFile(), "");
	    addCharacteristicRow(table, 7, "Printing Ink (Type)", report.getPrintingInkType(), "");
	    addCharacteristicRow(table, 8, "Any Visual Defect", report.getVisualDefect(), "");
	    addCharacteristicRow(table, 9, "Batch Composition", report.getBatchComposition(), "");

	    document.add(table);
	}

	private void addSignatureSection(Document document, PrintingInspectionReport report, PdfFont font, PdfFont fontBold)
	        throws IOException {
	    document.add(new Paragraph("\n").setFontSize(3));

	    Table signatureTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
	            .setWidth(UnitValue.createPercentValue(100))
	            .setBorder(SOLID_BORDER)
	            .setFontSize(CONTENT_FONT_SIZE);

	    // QA Executive signature
	    Table qaInnerTable = new Table(UnitValue.createPercentArray(new float[]{30, 70}))
	            .setWidth(UnitValue.createPercentValue(100))
	            .setBorder(Border.NO_BORDER);

	    Cell qaLabelCell = new Cell()
	            .add(new Paragraph("QA Exe.:").setFont(fontBold))
	            .setBorder(Border.NO_BORDER)
	            .setVerticalAlignment(VerticalAlignment.MIDDLE);

	    qaInnerTable.addCell(qaLabelCell);

	    Cell qaSignCell = new Cell().setBorder(Border.NO_BORDER);

	    // Try to load QA signature if available
	    if (report.getQaName() != null && report.getReviewedAt() != null) {
	        try {
	            ClassPathResource resource = new ClassPathResource("static/images/QASign.png");
	            Image qaSignatureImg = new Image(
	                    ImageDataFactory.create(resource.getInputStream().readAllBytes()))
	                    .scaleToFit(40, 20);
	            qaSignCell.add(qaSignatureImg);
	        } catch (Exception e) {
	            qaSignCell.add(new Paragraph(report.getQaName() + " (signed)"));
	        }
	    } else {
	        qaSignCell.add(new Paragraph("______________________"));
	    }

	    qaInnerTable.addCell(qaSignCell);
	    Cell qaCell = new Cell().add(qaInnerTable).setPadding(3);
	    signatureTable.addCell(qaCell);

	    // Operator signature
	    Table opInnerTable = new Table(UnitValue.createPercentArray(new float[]{60, 40}))
	            .setWidth(UnitValue.createPercentValue(100))
	            .setBorder(Border.NO_BORDER);

	    Cell opLabelCell = new Cell()
	            .add(new Paragraph("Production Sup. / Operator:").setFont(fontBold))
	            .setBorder(Border.NO_BORDER)
	            .setVerticalAlignment(VerticalAlignment.MIDDLE);

	    opInnerTable.addCell(opLabelCell);

	    Cell opSignCell = new Cell().setBorder(Border.NO_BORDER);

	    // Try to load operator signature if available
	    if (report.getOperatorName() != null && report.getOperatorSignature() != null) {
	        try {
	            ClassPathResource resource = new ClassPathResource("static/images/OperatorSign.png");
	            Image opSignatureImg = new Image(
	                    ImageDataFactory.create(resource.getInputStream().readAllBytes()))
	                    .scaleToFit(40, 20);
	            opSignCell.add(opSignatureImg);
	        } catch (Exception e) {
	            opSignCell.add(new Paragraph(report.getOperatorName() + " (signed)"));
	        }
	    } else {
	        opSignCell.add(new Paragraph("______________________"));
	    }

	    opInnerTable.addCell(opSignCell);
	    Cell opCell = new Cell().add(opInnerTable).setPadding(3);
	    signatureTable.addCell(opCell);

	    // Time (Final Approval)
	    String approvalTime = report.getApprovalTime() != null ? report.getApprovalTime() : 
	                         java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")) + " Hrs";

	    Paragraph finalApproval = new Paragraph()
	            .add(new Text("Time (Final Approval) : ").setFont(fontBold))
	            .add(new Text(approvalTime));

	    Cell finalApprovalCell = new Cell(1, 2)
	            .add(finalApproval)
	            .setBorderTop(SOLID_BORDER)
	            .setPadding(3);
	    signatureTable.addCell(finalApprovalCell);

	    document.add(signatureTable);
	}

	// Helper method to add a characteristic row to the table
	private void addCharacteristicRow(Table table, int rowNum, String characteristic, String observation, String comments) {
	    table.addCell(new Cell().add(new Paragraph(String.valueOf(rowNum)))
	            .setTextAlignment(TextAlignment.CENTER)
	            .setBorder(SOLID_BORDER)
	            .setPadding(3));
	    
	    table.addCell(new Cell().add(new Paragraph(characteristic))
	            .setBorder(SOLID_BORDER)
	            .setPadding(3));
	    
	    table.addCell(new Cell().add(new Paragraph(observation != null ? observation : ""))
	            .setBorder(SOLID_BORDER)
	            .setPadding(3));
	    
	    table.addCell(new Cell().add(new Paragraph(comments != null ? comments : ""))
	            .setBorder(SOLID_BORDER)
	            .setPadding(3));
	}

	// Helper method to add table headers
//	private void addTableHeader(Table table, String text, PdfFont fontBold) {
//	    table.addHeaderCell(new Cell()
//	            .add(new Paragraph(text).setFont(fontBold))
//	            .setBackgroundColor(HEADER_BG_COLOR)
//	            .setBorder(SOLID_BORDER)
//	            .setPadding(3));
//	}
}