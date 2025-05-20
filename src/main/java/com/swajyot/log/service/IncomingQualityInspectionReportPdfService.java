package com.swajyot.log.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
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
import com.swajyot.log.model.IncomingQualityInspectionReport;

@Service
public class IncomingQualityInspectionReportPdfService {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yy");
	private static final float BORDER_WIDTH = 0.5f;
	private static final Border SOLID_BORDER = new SolidBorder(ColorConstants.BLACK, BORDER_WIDTH);
	private static final DeviceRgb HEADER_BG_COLOR = new DeviceRgb(230, 230, 230);

	// Font sizes
	private static final float COMPANY_NAME_FONT_SIZE = 16f;
	private static final float TITLE_FONT_SIZE = 12f;
	private static final float SUBTITLE_FONT_SIZE = 10f;
	private static final float CONTENT_FONT_SIZE = 9f;
	private static final float SMALL_FONT_SIZE = 8f;

	public byte[] generatePdf(IncomingQualityInspectionReport report, String username) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfWriter writer = new PdfWriter(baos);
		PdfDocument pdf = new PdfDocument(writer);
		Document document = new Document(pdf, PageSize.A4);
		// Optimized margins for better content fit
		document.setMargins(15, 36, 15, 36); // top, right, bottom, left
		PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
		PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

		addHeader(document, report, fontBold);
		addProductInfo(document, report, font, fontBold);
		addAuditResultsTable(document, report, font, fontBold);
		addTestResultsTable(document, report, font, fontBold);
		addQualityDecisionSection(document, report, font, fontBold);

		// Add username at bottom of the PDF
		String generatedTime = java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

		Paragraph usernameFooter = new Paragraph("Downloaded by: " + username + " on " + generatedTime)
				.setFontSize(SMALL_FONT_SIZE).setTextAlignment(TextAlignment.RIGHT).setMarginTop(10);

		document.add(usernameFooter);

		document.close();
		return baos.toByteArray();
	}

	private void addHeader(Document document, IncomingQualityInspectionReport report, PdfFont fontBold)
			throws IOException {
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
					"iVBORw0KGgoAAAANSUhEUgAAAGQAAABkCAIAAAD/gAIDAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyJpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMy1jMDExIDY2LjE0NTY2MSwgMjAxMi8wMi8wNi0xNDo1NjoyNyAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENTNiAoV2luZG93cykiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6NTkzRTlDQkUyMEU5MTFFQTk4RkNBRkJDODVCRTZCODMiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6NTkzRTlDQkYyMEU5MTFFQTk4RkNBRkJDODVCRTZCODMiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDo1OTNFOUNCQzIwRTkxMUVBOThGQ0FGQkM4NUJFNkI4MyIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDo1OTNFOUNCRDIwRTkxMUVBOThGQ0FGQkM4NUJFNkI4MyIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/PpR4Hs4AAAgISURBVHja7JxrbBRVFMfPnd3Z3S7d0pWWbYEi5VGMWAPxAZgoBsLDGISoRIN8IcaYKJ+MSvxgCD7wQwkJMTHRRGIk8YMJGk0kBIwvSDRCRMFAIgYUaAHb0hf7nN0ZzwxndnZ3Znfb6ba7c+C07cydu3vv/57//5x7Z2dNQggMx8IyTGAYtjBsYdjCsIVhC8MWhi0MWxi2MGxh2MKwhWELw9Yw1m/O/Q8tCyYzRGmNDaFQMDyQSuA4Dviv1BobAMdR39d1xDl3HOq6jmVNcM1fsdpaIgQIQQD5f5ybpYfRUCXTnE6nwHEyU2/JskgTQizLEkIEQRAEQRRFnudr7TEfQUF8JEl0Y5qZCDAGRifBp8rGRgXgNJUa4ZKlGK4Mm2dnWf1Q30VFAZqYikSOKoqqquGwlP8eRcQDaiiKarooBIDU1NQtXBiBJ0pUgJd7dRtlZQVwoQQRCvE8D/C6LsU1Lpu6D3A6nYbfG24QQjRNczgc+eVcVKLJ9wJSoWoaQC5aDYWigXUcL8t6NBp2OADPcznAo7RFIpFYLOZwOEzWa+DLTIJlZCSJPP5YcLYdXVYUzYz5KQfTGhoa7rrrruXLl4dCIT6vB2nLqxcfPnzY4XDMnDmTZxaVp6xlyGxRIqyUWM37UPkMiouyzJZjwc5SAyGkqmogEFixYsXZs2e//vprmyfzgBgIBJLJZHt7O0+ZYEUd/Pju6JBVNZ9FZctR/Lnvn3PZKQRnwTxDsJlM5vHHH+/r6/v6669nzJhhcXIFYrt27XrooYduvvlmi0t46Rlf5aZ7TU1NLpfL5XK5XE6n0+l0u9yiKAqiwPN8JpPq7e21Xu/sHwwN9RA1BKPBT4lrGlJVVVGUTCaTSCQGBgb6+vquX7/e39//2GOP9fb2fvXVV1OnTrUYzOXLl7/88st9+/ZZXMJLr3rNQ6FQU1NTe3t7W1vblClTmpubXS7XwEBU06QTJ46LSqdlcjfbR2CUPXsufXju8kBU5ngfPcUYERFtMElJDdHfTU1Nd9xxx6pVq7xe75YtW15//fXe3t6vvvrK7/fbOhRCyJYtWzZs2DA4ONjS0mIN3lJ8M6yt7e3tXbZs2Z133mm4UT7uTgYG0q9/8HXw/OnQgFf3+yBeIIXfGCf6FI2mqaZpiURiYGCAYqS72Lx58zPPPNPR0XHo0KG5c+dajOaLL75YuHDha6+9Zt2DJcs1TnS5XAsWLJg+fTohJJVKaZrW19d38eJFnuc9Hk9zc7PP5+N5LpWSXn75vS++unH8ZPrqNYUQJxUZzLVYVBcZGJEkSZKkdDqdTCYlSRoaGlIUJZ1O9/T0NDY2iqLY2dnZ1NRk8SDPnj27cePGl156ieOsXUMr9TqkiGR0Op3zrwk6nU6fz+f3+1taWnw+HyFEVbWvvz31ye7rv/xGYrEoz1MwR3wDwJFEMjk0NBSJRPr7+3/77bdLly4dOXLkp59+2r9//+HDh48dO3bhwoXVq1fbOk44HH7xxRfXrVvX0dFRJnhLzUKCINBhNZvNZrPZbDZL3a6RSCSRSPh8Po/HI8syn0yqv1+JZLME+xxYP+RCoUVcEgQhEAjU1dX5/f729vahUEgURa/X29LS4vF4RFGMRCLP/vPi3r1777nnnvr6enunef/99+fNm9fa2lpO9pVWDiw0TUskEocOHfrhhx8OHDhw5syZeDweDodvv/32OXPmdHV1zZ8/f+bMme3t7YIgIISu/9GPEKqwABYuXLh79+6DBw8+8MADtk7T3d39zjvvbNu2rZyA5tDq0UfpvtFRo/0qPVAGYkYs3QIkSQoGgx6PR5ZlVVUDgUBXV1ddXd3DDz+8cOHCu+++e8aMGTSfvnz5w2eeebOvbwivbCgEcIvIQnSZInrHFFWmU4uyVF31OBRDqMbmfz737LPPvvnmm7bwOXfu3IoVK372iy/+Y7J9Q7VuM7bZ5KweFiCErHcZFoYtbGHYGpO/sQ17U1i2MGwNM9vCsDVsYdi6dU+djb3FGSPQmTaEEPiKgZ1xS3WZ3M3TYolWKpXq6ur66aefEokE5y+Hxd66kKqqtbW1W7duraurKzmLLOqWTrx1dHQcP358586dhw4dunbtWiQSoYcZxeUv9s0OwGqz2fZAINCazWZDodCePXs2bdp0/vz5wf8M/q3QEYvF7r///s8++2zNmjXlxbJoQmiOAo7jes8ddXV1dUvCGlP0VFJEl1dXV1dXV1d3e/eePXs2bdp0/vz5wf8M/q3QEYvF7r///s8++2zNmjXlxbJoQmiOAo7jes8ddXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXU=");
			logo = new Image(ImageDataFactory.create(placeholder));
		}

		// Optimized logo size and positioning
		logo.setWidth(UnitValue.createPercentValue(80));
		logo.setAutoScale(true);
		logo.setMargins(20, 20, 20, 20);

		Cell logoCell = new Cell().add(logo).setBorder(Border.NO_BORDER).setPadding(5)
				.setVerticalAlignment(VerticalAlignment.MIDDLE).setHorizontalAlignment(HorizontalAlignment.CENTER);

		headerTable.addCell(logoCell);
		document.add(headerTable);
	}

	private Table createDocumentInfoTable(IncomingQualityInspectionReport report, PdfFont fontBold) {
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

	private Table createTitleTable(IncomingQualityInspectionReport report, PdfFont fontBold) {
		Table titleTable = new Table(1).setWidth(UnitValue.createPercentValue(100)).setMargin(0);

		// Company name with variable font size
		Paragraph companyName = new Paragraph("AGI Greenpac Limited").setFont(fontBold)
				.setFontSize(COMPANY_NAME_FONT_SIZE).setMarginTop(20).setTextAlignment(TextAlignment.CENTER);

		Cell companyCell = new Cell().add(companyName).setBorder(Border.NO_BORDER).setPadding(3)
				.setVerticalAlignment(VerticalAlignment.MIDDLE);

		titleTable.addCell(companyCell);

		// Unit name
		Paragraph unitName = new Paragraph("Unit :- " + report.getUnit()).setFontSize(SUBTITLE_FONT_SIZE)
				.setMarginBottom(20).setTextAlignment(TextAlignment.CENTER);

		Cell unitCell = new Cell().add(unitName).setBorder(Border.NO_BORDER).setPadding(3)
				.setVerticalAlignment(VerticalAlignment.MIDDLE);

		titleTable.addCell(unitCell);

		// Add spacing
		Cell spaceCell = new Cell().setHeight(10).setBorder(Border.NO_BORDER);

		titleTable.addCell(spaceCell);

		// Create a table for SCOPE with proper borders and sizing
		Table scopeTable = new Table(UnitValue.createPercentArray(new float[] { 25, 75 }))
				.setWidth(UnitValue.createPercentValue(100));

		Cell scopeLabelCell = new Cell().add(new Paragraph("SCOPE:").setFont(fontBold).setFontSize(CONTENT_FONT_SIZE))
				.setBorder(SOLID_BORDER).setPadding(4).setTextAlignment(TextAlignment.LEFT);

		Cell scopeValueCell = new Cell().add(new Paragraph(report.getScope()).setFontSize(CONTENT_FONT_SIZE))
				.setBorder(SOLID_BORDER).setPadding(4).setTextAlignment(TextAlignment.CENTER);

		scopeTable.addCell(scopeLabelCell);
		scopeTable.addCell(scopeValueCell);

		// Add the SCOPE table to the main title table
		Cell scopeTableCell = new Cell().add(scopeTable).setBorder(Border.NO_BORDER).setPadding(0);

		titleTable.addCell(scopeTableCell);

		Table titleContentTable = new Table(UnitValue.createPercentArray(new float[] { 25, 75 }))
				.setWidth(UnitValue.createPercentValue(100));

		Cell titleLabelCell = new Cell().add(new Paragraph("TITLE :").setFont(fontBold).setFontSize(CONTENT_FONT_SIZE))
				.setBorder(SOLID_BORDER).setPadding(4).setTextAlignment(TextAlignment.LEFT);

		Cell titleValueCell = new Cell().add(new Paragraph(report.getTitle()).setFontSize(CONTENT_FONT_SIZE))
				.setBorder(SOLID_BORDER).setPadding(4).setTextAlignment(TextAlignment.CENTER);

		titleContentTable.addCell(titleLabelCell);
		titleContentTable.addCell(titleValueCell);

		// Add the TITLE table to the main title table
		Cell titleTableCell = new Cell().add(titleContentTable).setBorder(Border.NO_BORDER).setPadding(0);

		titleTable.addCell(titleTableCell);

		return titleTable;
	}

	private void addProductInfo(Document document, IncomingQualityInspectionReport report, PdfFont font,
			PdfFont fontBold) {
		Table infoTable = new Table(UnitValue.createPercentArray(new float[] { 33, 33, 34 }))
				.setWidth(UnitValue.createPercentValue(100)).setFontSize(CONTENT_FONT_SIZE);

		// Row 1: IQC Date | Shift | Product Received Quantity
		infoTable.addCell(createLabelCell("IQC Date:", fontBold));
		infoTable.addCell(
				createValueCell(report.getIqcDate() != null ? report.getIqcDate().format(DATE_FORMATTER) : ""));
		infoTable.addCell(createLabelCell("Shift: " + nullSafe(report.getShift()), font));

		// Row 2: Product / Variant | Supplier Shift | Quantity Audited
		infoTable.addCell(createLabelCell("Name of Product / Variant:", fontBold));
		infoTable.addCell(createValueCell(nullSafe(report.getProductVariantName())));
		infoTable.addCell(createLabelCell("Supplier Shift: " + nullSafe(report.getSupplierShift()), font));

		// Row 3: Product Received From | Product Received Date | Batch Number
		infoTable.addCell(createLabelCell("Product Received From:", fontBold));
		infoTable.addCell(createValueCell(nullSafe(report.getProductReceivedFrom())));
		infoTable.addCell(createLabelCell("Product Received Date: "
				+ (report.getProductReceivedDate() != null ? report.getProductReceivedDate().format(DATE_FORMATTER)
						: ""),
				font));

		// Row 4: Product Received Quantity | Quantity Audited | Batch Number
		infoTable.addCell(createLabelCell("Product Received Quantity:", fontBold));
		infoTable.addCell(createValueCell(
				report.getProductReceivedQuantity() != null ? report.getProductReceivedQuantity().toString() : ""));
		infoTable.addCell(createLabelCell("Quantity Audited: "
				+ (report.getQuantityAudited() != null ? report.getQuantityAudited().toString() : ""), font));

		// Row 5: Batch Number (spanning)
		infoTable.addCell(createLabelCell("Batch Number:", fontBold));
		infoTable.addCell(new Cell(1, 2).add(new Paragraph(nullSafe(report.getBatchNumber()))).setBorder(SOLID_BORDER)
				.setPadding(4));

		document.add(infoTable);
	}

//	private Cell createLabelCell(String text, PdfFont font) {
//		return new Cell().add(new Paragraph(text).setFont(font)).setBorder(SOLID_BORDER).setPadding(4);
//	}
	
	private Cell createLabelCell(String labelWithValue, PdfFont fontBold) {
	    String[] parts = labelWithValue.split(":", 2);
	    Paragraph paragraph = new Paragraph();

	    if (parts.length == 2) {
	        paragraph.add(new Text(parts[0] + ": ").setFont(fontBold));
	        paragraph.add(new Text(parts[1].trim()));
	    } else {
	        paragraph.add(new Text(labelWithValue).setFont(fontBold));
	    }

	    return new Cell().add(paragraph).setBorder(SOLID_BORDER).setPadding(4);
	}

	private Cell createValueCell(String text) {
		return new Cell().add(new Paragraph(text)).setBorder(SOLID_BORDER).setPadding(4);
	}

	private String nullSafe(String input) {
		return input != null ? input : "";
	}

	private void addAuditResultsTable(Document document, IncomingQualityInspectionReport report, PdfFont font,
			PdfFont fontBold) {
		Table table = new Table(UnitValue.createPercentArray(new float[] { 25, 15, 60 }))
				.setWidth(UnitValue.createPercentValue(100)).setFontSize(CONTENT_FONT_SIZE);

		// Table headers
		Cell categoryHeader = new Cell().add(new Paragraph("CATEGORY").setFont(fontBold))
				.setBackgroundColor(HEADER_BG_COLOR).setBorder(SOLID_BORDER).setPadding(3);

		Cell nosHeader = new Cell().add(new Paragraph("Nos").setFont(fontBold)).setBackgroundColor(HEADER_BG_COLOR)
				.setBorder(SOLID_BORDER).setPadding(3);

		Cell defectsHeader = new Cell().add(new Paragraph("DEFECT(S) NAME").setFont(fontBold))
				.setBackgroundColor(HEADER_BG_COLOR).setBorder(SOLID_BORDER).setPadding(3);

		table.addHeaderCell(categoryHeader);
		table.addHeaderCell(nosHeader);
		table.addHeaderCell(defectsHeader);

		// Add each audit result
		int totalCount = 0;
		if (report.getAuditResults() != null) {
			for (Map<String, Object> auditResult : report.getAuditResults()) {
				// Category
				String category = (String) auditResult.get("category");
				table.addCell(new Cell().add(new Paragraph(category != null ? category : "")).setBorder(SOLID_BORDER)
						.setPadding(3));

				// Count
				Integer count = null;
				Object countObj = auditResult.get("count");
				if (countObj instanceof Integer) {
					count = (Integer) countObj;
				} else if (countObj instanceof String) {
					try {
						count = Integer.parseInt((String) countObj);
					} catch (NumberFormatException e) {
						// Keep count as null
					}
				}

				if (count != null) {
					totalCount += count;
				}

				table.addCell(new Cell().add(new Paragraph(count != null ? count.toString() : ""))
						.setTextAlignment(TextAlignment.CENTER).setBorder(SOLID_BORDER).setPadding(3));

				// Defect Name
				String defectName = (String) auditResult.get("defectName");
				table.addCell(new Cell().add(new Paragraph(defectName != null ? defectName : ""))
						.setBorder(SOLID_BORDER).setPadding(3));
			}
		}

		// Add TOTAL row
		table.addCell(new Cell().add(new Paragraph("TOTAL").setFont(fontBold)).setBorder(SOLID_BORDER).setPadding(3));

		table.addCell(new Cell().add(new Paragraph(String.valueOf(totalCount))).setTextAlignment(TextAlignment.CENTER)
				.setBorder(SOLID_BORDER).setPadding(3));

		table.addCell(new Cell().add(new Paragraph("")).setBorder(SOLID_BORDER).setPadding(3));

		document.add(table);
	}

	private void addTestResultsTable(Document document, IncomingQualityInspectionReport report, PdfFont font,
			PdfFont fontBold) {
		document.add(new Paragraph("\n").setFontSize(5));

		Table table = new Table(UnitValue.createPercentArray(new float[] { 40, 30, 30 }))
				.setWidth(UnitValue.createPercentValue(100)).setFontSize(CONTENT_FONT_SIZE);

		// Loop through test results and add them to the table
		if (report.getTestResults() != null) {
			for (Map<String, Object> testResult : report.getTestResults()) {
				String testName = (String) testResult.get("testName");
				String specification = (String) testResult.get("specification");

				// First column: Test name and specification
				Cell testCell = new Cell();
				Paragraph testPara = new Paragraph();
				if (testName != null) {
					testPara.add(new Text(testName).setFont(fontBold)).add("\n");
				}
				if (specification != null) {
					testPara.add(new Text("(Specification :- " + specification + ")"));
				}
				testCell.add(testPara).setBorder(SOLID_BORDER).setPadding(3);
				table.addCell(testCell);

				// Second column: Result
				String result = (String) testResult.get("result");
				Cell resultCell = new Cell().add(new Paragraph(result != null ? result : "")).setBorder(SOLID_BORDER)
						.setPadding(3);
				table.addCell(resultCell);

				// Third column: Checked by
				String checkedBy = (String) testResult.get("checkedBy");
				Cell checkedByCell = new Cell();
				Paragraph checkedByPara = new Paragraph();
				checkedByPara.add(new Text("Checked by :").setFont(fontBold)).add("\n");
				checkedByPara.add(new Text(checkedBy != null ? checkedBy : ""));
				checkedByCell.add(checkedByPara).setBorder(SOLID_BORDER).setPadding(3);
				table.addCell(checkedByCell);
			}
		}

		document.add(table);
	}

	private void addQualityDecisionSection(Document document, IncomingQualityInspectionReport report, PdfFont font,
			PdfFont fontBold) {
		document.add(new Paragraph("\n").setFontSize(5));

		Table decisionTable = new Table(2).setWidth(UnitValue.createPercentValue(100)).setFontSize(CONTENT_FONT_SIZE);

		// Quality decision row
		Cell decisionLabel = new Cell().add(new Paragraph("QUALITY DECISION FOR LOT").setFont(fontBold))
				.setBorder(SOLID_BORDER).setPadding(5).setWidth(UnitValue.createPercentValue(40));

		Cell decisionValue = new Cell()
				.add(new Paragraph(report.getQualityDecision() != null ? report.getQualityDecision() : ""))
				.setBorder(SOLID_BORDER).setPadding(5);

		decisionTable.addCell(decisionLabel);
		decisionTable.addCell(decisionValue);

		document.add(decisionTable);

		// Add signature box
		addSignature(document, report, fontBold);
	}

	private void addSignature(Document document, IncomingQualityInspectionReport report, PdfFont fontBold) {
		document.add(new Paragraph("\n").setFontSize(5));

		Table signatureTable = new Table(UnitValue.createPercentArray(new float[] { 100 }))
				.setWidth(UnitValue.createPercentValue(30)).setHorizontalAlignment(HorizontalAlignment.RIGHT)
				.setFontSize(CONTENT_FONT_SIZE);

		Cell signatureCell = new Cell().setBorder(Border.NO_BORDER).setHeight(60)
				.setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER);

		Paragraph signInfo = new Paragraph();

		try {
			if (report.getQualityManagerSignature() != null && !report.getQualityManagerSignature().isEmpty()) {
				ClassPathResource resource = new ClassPathResource("static/images/QASign.png");
				Image signatureImg = new Image(ImageDataFactory.create(resource.getInputStream().readAllBytes()))
						.scaleToFit(100, 30).setAutoScale(true);

				signInfo.add(signatureImg);
			} else if (report.getQualityManagerName() != null) {
				signInfo.add(new Paragraph(report.getQualityManagerName() + " (signed)").setFont(fontBold));
			} else {
				signInfo.add(new Paragraph("Signed by QA").setFont(fontBold));
			}
		} catch (Exception e) {
			signInfo.add(new Paragraph("Signature Error").setFont(fontBold));
		}

		String signatureDate = report.getSignatureDate() != null ? report.getSignatureDate().format(DATE_FORMATTER)
				: "";

		signInfo.add(new Paragraph(signatureDate).setFontSize(SMALL_FONT_SIZE));
		signatureCell.add(signInfo);

		signatureTable.addCell(signatureCell);
		document.add(signatureTable);
	}

}