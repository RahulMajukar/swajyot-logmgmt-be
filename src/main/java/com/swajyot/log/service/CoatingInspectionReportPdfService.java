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
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.swajyot.log.model.CoatingInspectionReport;

@Service
public class CoatingInspectionReportPdfService {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
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

	public byte[] generatePdf(CoatingInspectionReport report, String username) throws IOException {
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
		addCoatingDetailsTable(document, report, font, fontBold);
		addCharacteristicsTable(document, report, font, fontBold);
		addSignatureSection(document, report, font, fontBold);

		// Add username at bottom of the PDF
		String generatedTime = java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

		Paragraph usernameFooter = new Paragraph("Downloaded by: " + username + " on " + generatedTime)
				.setFontSize(SMALL_FONT_SIZE).setTextAlignment(TextAlignment.RIGHT).setMarginTop(10);

		document.add(usernameFooter);

		document.close();
		return baos.toByteArray();
	}

	private void addHeader(Document document, CoatingInspectionReport report, PdfFont fontBold) throws IOException {
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
					"iVBORw0KGgoAAAANSUhEUgAAAGQAAABkCAIAAAD/gAIDAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyJpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMy1jMDExIDY2LjE0NTY2MSwgMjAxMi8wMi8wNi0xNDo1NjoyNyAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENTNiAoV2luZG93cykiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6NTkzRTlDQkUyMEU5MTFFQTk4RkNBRkJDODVCRTZCODMiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6NTkzRTlDQkYyMEU5MTFFQTk4RkNBRkJDODVCRTZCODMiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDo1OTNFOUNCQzIwRTkxMUVBOThGQ0FGQkM4NUJFNkI4MyIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDo1OTNFOUNCRDIwRTkxMUVBOThGQ0FGQkM4NUJFNkI4MyIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/PpR4Hs4AAAgISURBVHja7JxrbBRVFMfPnd3Z3S7d0pWWbYEi5VGMWAPxAZgoBsLDGISoRIN8IcaYKJ+MSvxgCD7wQwkJMTHRRGIk8YMJGk0kBIwvSDRCRMFAIgYUaAHb0hf7nN0ZzwxndnZ3Znfb6ba7c+C07cydu3vv/57//5x7Z2dNQggMx8IyTGAYtjBsYdjCsIVhC8MWhi0MWxi2MGxh2MKwhWELw9Yw1m/O/Q8tCyYzRGmNDaFQMDyQSuA4Dviv1BobAMdR39d1xDl3HOq6jmVNcM1fsdpaIgQIQQD5f5ybpYfRUCXTnE6nwHEyU2/JskgTQizLEkIEQRAEQRRFnudr7TEfQUF8JEl0Y5qZCDAGRifBp8rGRgXgNJUa4ZKlGK4Mm2dnWf1Q30VFAZqYikSOKoqqquGwlP8eRcQDaiiKarooBIDU1NQtXBiBJ0pUgJd7dRtlZQVwoQQRCvE8D/C6LsU1Lpu6D3A6nYbfG24QQjRNczgc+eVcVKLJ9wJSoWoaQC5aDYWigXUcL8t6NBp2OADPcznAo7RFIpFYLOZwOEzWa+DLTIJlZCSJPP5YcLYdXVYUzYz5KQfTGhoa7rrrruXLl4dCIT6vB2nLqxcfPnzY4XDMnDmTZxaVp6xlyGxRIqyUWM37UPkMiouyzJZjwc5SAyGkqmogEFixYsXZs2e//vprmyfzgBgIBJLJZHt7O0+ZYEUd/Pju6JBVNZ9FZctR/Lnvn3PZKQRnwTxDsJlM5vHHH+/r6/v6669nzJhhcXIFYrt27XrooYduvvlmi0t46Rlf5aZ7TU1NLpfL5XK5XE6n0+l0u9yiKAqiwPN8JpPq7e21Xu/sHwwN9RA1BKPBT4lrGlJVVVGUTCaTSCQGBgb6+vquX7/e39//2GOP9fb2fvXVV1OnTrUYzOXLl7/88st9+/ZZXMJLr3rNQ6FQU1NTe3t7W1vblClTmpubXS7XwEBU06QTJ46LSqdlcjfbR2CUPXsufXju8kBU5ngfPcUYERFtMElJDdHfTU1Nd9xxx6pVq7xe75YtW15//fXe3t6vvvrK7/fbOhRCyJYtWzZs2DA4ONjS0mIN3lJ8M6yt7e3tXbZs2Z133mm4UT7uTgYG0q9/8HXw/OnQgFf3+yBeIIXfGCf6FI2mqaZpiURiYGCAYqS72Lx58zPPPNPR0XHo0KG5c+dajOaLL75YuHDha6+9Zt2DJcs1TnS5XAsWLJg+fTohJJVKaZrW19d38eJFnuc9Hk9zc7PP5+N5LpWSXn75vS++unH8ZPrqNYUQJxUZzLVYVBcZGJEkSZKkdDqdTCYlSRoaGlIUJZ1O9/T0NDY2iqLY2dnZ1NRk8SDPnj27cePGl156ieOsXUMr9TqkiGR0Op3zrwk6nU6fz+f3+1taWnw+HyFEVbWvvz31ye7rv/xGYrEoz1MwR3wDwJFEMjk0NBSJRPr7+3/77bdLly4dOXLkp59+2r9//+HDh48dO3bhwoXVq1fbOk44HH7xxRfXrVvX0dFRJnhLzUKCINBhNZvNZrPZbDZL3a6RSCSRSPh8Po/HI8syn0yqv1+JZLME+xxYP+RCoUVcEgQhEAjU1dX5/f729vahUEgURa/X29LS4vF4RFGMRCLP/vPi3r1777nnnvr6enunef/99+fNm9fa2lpO9pVWDiw0TUskEocOHfrhhx8OHDhw5syZeDweDodvv/32OXPmdHV1zZ8/f+bMme3t7YIgIISu/9GPEKqwABYuXLh79+6DBw8+8MADtk7T3d39zjvvbNu2rZyA5tDq0UfpvtFRo/0qPVAGYkYs3QIkSQoGgx6PR5ZlVVUDgUBXV1ddXd3DDz+8cOHCu+++e8aMGTSfvnz5w2eeebOvbwivbCgEcIvIQnSZInrHFFWmU4uyVF31OBRDqMbmfz737LPPvvnmm7bwOXfu3IoVK372iy/+Y7J9Q7VuM7bZ5KweFiCErHcZFoYtbGHYGpO/sQ17U1i2MGwNM9vCsDVsYdi6dU+djb3FGSPQmTaEEPiKgZ1xS3WZ3M3TYolWKpXq6ur66aefEokE5y+Hxd66kKqqtbW1W7duraurKzmLLOqWTrx1dHQcP358586dhw4dunbtWiQSoYcZxeUv9s0OwGqz2fZAINCazWZDodCePXs2bdp0/vz5wf8M/q3QEYvF7r///s8++2zNmjXlxbJoQmiOAo7jes8ddXV1dUvCGlP0VFJEl1dXV1dXV1d3e/eePXs2bdp0/vz5wf8M/q3QEYvF7r///s8++2zNmjXlxbJoQmiOAo7jes8ddXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXV1dXU=");
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

	private Table createDocumentInfoTable(CoatingInspectionReport report, PdfFont fontBold) {
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

	private Table createTitleTable(CoatingInspectionReport report, PdfFont fontBold) {
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

		Cell titleValueCell = new Cell().add(new Paragraph(report.getTitle()).setFontSize(CONTENT_FONT_SIZE)
				.setPadding(4).setTextAlignment(TextAlignment.CENTER));

		titleContentTable.addCell(titleLabelCell);
		titleContentTable.addCell(titleValueCell);

		// Add the TITLE table to the main title table
		Cell titleTableCell = new Cell().add(titleContentTable).setBorder(Border.NO_BORDER).setPadding(0);

		titleTable.addCell(titleTableCell);

		return titleTable;
	}

	private void addProductInfo(Document document, CoatingInspectionReport report, PdfFont font, PdfFont fontBold) {
		Table infoTable = new Table(UnitValue.createPercentArray(new float[] { 25, 25, 25, 25 }))
				.setWidth(UnitValue.createPercentValue(100)).setFontSize(CONTENT_FONT_SIZE);

		// Date and Shift row
		Cell dateLabel = new Cell().add(new Paragraph("DATE:").setFont(fontBold)).setBorder(SOLID_BORDER).setPadding(3);

		String dateStr = report.getInspectionDate() != null ? report.getInspectionDate().format(DATE_FORMATTER) : "";
		Cell dateValue = new Cell().add(new Paragraph(dateStr)).setBorder(SOLID_BORDER).setPadding(3);

		Cell shiftLabel = new Cell().add(new Paragraph("SHIFT:").setFont(fontBold)).setBorder(SOLID_BORDER)
				.setPadding(3);

		Cell shiftValue = new Cell().add(new Paragraph(report.getShift() != null ? report.getShift() : ""))
				.setBorder(SOLID_BORDER).setPadding(3);

		infoTable.addCell(dateLabel);
		infoTable.addCell(dateValue);
		infoTable.addCell(shiftLabel);
		infoTable.addCell(shiftValue);

		// Product, Variant, Line row
		Cell productLabel = new Cell().add(new Paragraph("PRODUCT:").setFont(fontBold)).setBorder(SOLID_BORDER)
				.setPadding(3);

		Cell productValue = new Cell().add(new Paragraph(report.getProduct() != null ? report.getProduct() : ""))
				.setBorder(SOLID_BORDER).setPadding(3);

		Cell variantLabel = new Cell().add(new Paragraph("VARIANT:").setFont(fontBold)).setBorder(SOLID_BORDER)
				.setPadding(3);

		Cell variantValue = new Cell().add(new Paragraph(report.getVariant() != null ? report.getVariant() : ""))
				.setBorder(SOLID_BORDER).setPadding(3);

		infoTable.addCell(productLabel);
		infoTable.addCell(productValue);
		infoTable.addCell(variantLabel);
		infoTable.addCell(variantValue);

		// Size No, Customer row
		Cell sizeNoLabel = new Cell().add(new Paragraph("SIZE NO:").setFont(fontBold)).setBorder(SOLID_BORDER)
				.setPadding(3);

		Cell sizeNoValue = new Cell().add(new Paragraph(report.getSizeNo() != null ? report.getSizeNo() : ""))
				.setBorder(SOLID_BORDER).setPadding(3);

		Cell lineNoLabel = new Cell().add(new Paragraph("LINE NO:").setFont(fontBold)).setBorder(SOLID_BORDER)
				.setPadding(3);

		Cell lineNoValue = new Cell().add(new Paragraph(report.getLineNo() != null ? report.getLineNo() : ""))
				.setBorder(SOLID_BORDER).setPadding(3);

		infoTable.addCell(sizeNoLabel);
		infoTable.addCell(sizeNoValue);
		infoTable.addCell(lineNoLabel);
		infoTable.addCell(lineNoValue);

		// Customer and Sample Size
		Cell customerLabel = new Cell().add(new Paragraph("CUSTOMER:").setFont(fontBold)).setBorder(SOLID_BORDER)
				.setPadding(3);

		Cell customerValue = new Cell().add(new Paragraph(report.getCustomer() != null ? report.getCustomer() : ""))
				.setBorder(SOLID_BORDER).setPadding(3);

		Cell sampleSizeLabel = new Cell().add(new Paragraph("SAMPLE SIZE:").setFont(fontBold)).setBorder(SOLID_BORDER)
				.setPadding(3);

		Cell sampleSizeValue = new Cell()
				.add(new Paragraph(report.getSampleSize() != null ? report.getSampleSize() : ""))
				.setBorder(SOLID_BORDER).setPadding(3);

		infoTable.addCell(customerLabel);
		infoTable.addCell(customerValue);
		infoTable.addCell(sampleSizeLabel);
		infoTable.addCell(sampleSizeValue);

		document.add(infoTable);
	}

	private void addCoatingDetailsTable(Document document, CoatingInspectionReport report, PdfFont font,
			PdfFont fontBold) {
		document.add(new Paragraph("\n").setFontSize(3));

		Table table = new Table(UnitValue.createPercentArray(new float[] { 5, 25, 15, 15, 15, 25 }))
				.setWidth(UnitValue.createPercentValue(100)).setFontSize(CONTENT_FONT_SIZE);

		// Table headers
		Cell slNoHeader = new Cell().add(new Paragraph("S.No.").setFont(fontBold)).setBackgroundColor(HEADER_BG_COLOR)
				.setBorder(SOLID_BORDER).setPadding(3);

		Cell lacquerHeader = new Cell().add(new Paragraph("Lacquer / Type").setFont(fontBold))
				.setBackgroundColor(HEADER_BG_COLOR).setBorder(SOLID_BORDER).setPadding(3);

		Cell batchNoHeader = new Cell().add(new Paragraph("Batch No").setFont(fontBold))
				.setBackgroundColor(HEADER_BG_COLOR).setBorder(SOLID_BORDER).setPadding(3);

		Cell qtyHeader = new Cell().add(new Paragraph("Qty.").setFont(fontBold)).setBackgroundColor(HEADER_BG_COLOR)
				.setBorder(SOLID_BORDER).setPadding(3);

		Cell noOfPiecesHeader = new Cell().add(new Paragraph("No of Pieces").setFont(fontBold))
				.setBackgroundColor(HEADER_BG_COLOR).setBorder(SOLID_BORDER).setPadding(3);

		Cell expiryDateHeader = new Cell().add(new Paragraph("Expiry Date").setFont(fontBold))
				.setBackgroundColor(HEADER_BG_COLOR).setBorder(SOLID_BORDER).setPadding(3);

		table.addHeaderCell(slNoHeader);
		table.addHeaderCell(lacquerHeader);
		table.addHeaderCell(batchNoHeader);
		table.addHeaderCell(qtyHeader);
		table.addHeaderCell(noOfPiecesHeader);
		table.addHeaderCell(expiryDateHeader);

		// Add each coating detail
		if (report.getCoatingDetails() != null && !report.getCoatingDetails().isEmpty()) {
			for (Map<String, Object> detail : report.getCoatingDetails()) {
				// S.No.
				int id = ((Number) detail.getOrDefault("id", 0)).intValue();
				table.addCell(new Cell().add(new Paragraph(String.valueOf(id))).setTextAlignment(TextAlignment.CENTER)
						.setBorder(SOLID_BORDER).setPadding(3));

				// Lacquer/Type
				String lacquerType = (String) detail.getOrDefault("lacquerType", "");
				table.addCell(new Cell().add(new Paragraph(lacquerType)).setBorder(SOLID_BORDER).setPadding(3));

				// Batch No
				String batchNo = (String) detail.getOrDefault("batchNo", "");
				table.addCell(new Cell().add(new Paragraph(batchNo)).setBorder(SOLID_BORDER).setPadding(3));

				// Quantity
				String quantity = (String) detail.getOrDefault("quantity", "");
				table.addCell(new Cell().add(new Paragraph(quantity)).setBorder(SOLID_BORDER).setPadding(3));

				// Number of Pieces
				String pieces = (String) detail.getOrDefault("numberOfPieces", "");
				table.addCell(new Cell().add(new Paragraph(pieces)).setBorder(SOLID_BORDER).setPadding(3));

				// Expiry Date
				String expiryDate = (String) detail.getOrDefault("expiryDate", "");
				table.addCell(new Cell().add(new Paragraph(expiryDate)).setBorder(SOLID_BORDER).setPadding(3));
			}
		} else {
			// Add empty rows if no details are provided
			for (int i = 0; i < 7; i++) {
				for (int j = 0; j < 6; j++) {
					table.addCell(new Cell().add(new Paragraph("")).setBorder(SOLID_BORDER).setPadding(5));
				}
			}
		}

		document.add(table);
	}

	private void addCharacteristicsTable(Document document, CoatingInspectionReport report, PdfFont font,
			PdfFont fontBold) {
		document.add(new Paragraph("\n").setFontSize(5));

		Table table = new Table(UnitValue.createPercentArray(new float[] { 5, 30, 35, 30 }))
				.setWidth(UnitValue.createPercentValue(100)).setFontSize(CONTENT_FONT_SIZE);

		// Table headers
		Cell slNoHeader = new Cell().add(new Paragraph("S.No.").setFont(fontBold)).setBackgroundColor(HEADER_BG_COLOR)
				.setBorder(SOLID_BORDER).setPadding(3);

		Cell characteristicHeader = new Cell().add(new Paragraph("Characteristic").setFont(fontBold))
				.setBackgroundColor(HEADER_BG_COLOR).setBorder(SOLID_BORDER).setPadding(3);

		Cell observationsHeader = new Cell()
				.add(new Paragraph("As per Reference/Specification/Observations").setFont(fontBold))
				.setBackgroundColor(HEADER_BG_COLOR).setBorder(SOLID_BORDER).setPadding(3)
				.setTextAlignment(TextAlignment.CENTER);

		Cell commentsHeader = new Cell().add(new Paragraph("Comments").setFont(fontBold))
				.setBackgroundColor(HEADER_BG_COLOR).setBorder(SOLID_BORDER).setPadding(3);

		table.addHeaderCell(slNoHeader);
		table.addHeaderCell(characteristicHeader);
		table.addHeaderCell(observationsHeader);
		table.addHeaderCell(commentsHeader);

		// Add characteristics data
		if (report.getCharacteristics() != null && !report.getCharacteristics().isEmpty()) {
			for (Map<String, Object> characteristic : report.getCharacteristics()) {
				// S.No.
				int id = ((Number) characteristic.getOrDefault("id", 0)).intValue();
				table.addCell(new Cell().add(new Paragraph(String.valueOf(id))).setTextAlignment(TextAlignment.CENTER)
						.setBorder(SOLID_BORDER).setPadding(3));

				// Characteristic
				String characteristicName = (String) characteristic.getOrDefault("characteristic", "");
				table.addCell(new Cell().add(new Paragraph(characteristicName)).setBorder(SOLID_BORDER).setPadding(3));

				// Observations
				String observations = (String) characteristic.getOrDefault("observations", "");
				table.addCell(new Cell().add(new Paragraph(observations)).setBorder(SOLID_BORDER).setPadding(3));

				// Comments
				String comments = (String) characteristic.getOrDefault("comments", "");
				table.addCell(new Cell().add(new Paragraph(comments)).setBorder(SOLID_BORDER).setPadding(3));
			}
		} else {
			// Add standard characteristics rows if none provided
			// Color Shade
			table.addCell(
					new Cell().add(new Paragraph("1")).setTextAlignment(TextAlignment.CENTER).setBorder(SOLID_BORDER));
			table.addCell(new Cell().add(new Paragraph("Color Shade")).setBorder(SOLID_BORDER));

			Cell colorShadeCell = new Cell().setBorder(SOLID_BORDER).setPadding(3);

			Table shadeTable = new Table(UnitValue.createPercentArray(new float[] { 50, 50 }))
					.setWidth(UnitValue.createPercentValue(100));

			shadeTable.addCell(new Cell().add(new Paragraph("Grade 1 :")).setBorder(SOLID_BORDER));
			shadeTable
					.addCell(new Cell().add(new Paragraph(report.getColorShade() != null ? report.getColorShade() : ""))
							.setBorder(SOLID_BORDER));

			colorShadeCell.add(shadeTable);
			table.addCell(colorShadeCell);
			table.addCell(new Cell().setBorder(SOLID_BORDER));

			// Color Height
			table.addCell(
					new Cell().add(new Paragraph("2")).setTextAlignment(TextAlignment.CENTER).setBorder(SOLID_BORDER));
			table.addCell(new Cell().add(new Paragraph("Color Height")).setBorder(SOLID_BORDER));
			table.addCell(new Cell().add(new Paragraph(report.getColorHeight() != null ? report.getColorHeight() : ""))
					.setBorder(SOLID_BORDER));
			table.addCell(new Cell().setBorder(SOLID_BORDER));

			// Visual Defect
			table.addCell(
					new Cell().add(new Paragraph("3")).setTextAlignment(TextAlignment.CENTER).setBorder(SOLID_BORDER));
			table.addCell(new Cell().add(new Paragraph("Any Visual defect")).setBorder(SOLID_BORDER));
			table.addCell(
					new Cell().add(new Paragraph(report.getVisualDefect() != null ? report.getVisualDefect() : ""))
							.setBorder(SOLID_BORDER));
			table.addCell(new Cell().setBorder(SOLID_BORDER));

			// MEK Test
			table.addCell(
					new Cell().add(new Paragraph("4")).setTextAlignment(TextAlignment.CENTER).setBorder(SOLID_BORDER));
			table.addCell(new Cell().add(new Paragraph("MEK Test")).setBorder(SOLID_BORDER));
			table.addCell(new Cell().add(new Paragraph(report.getMekTest() != null ? report.getMekTest() : ""))
					.setBorder(SOLID_BORDER));
			table.addCell(new Cell().setBorder(SOLID_BORDER));

			// Cross Cut Test
			table.addCell(
					new Cell().add(new Paragraph("5")).setTextAlignment(TextAlignment.CENTER).setBorder(SOLID_BORDER));
			table.addCell(new Cell().add(new Paragraph("Cross Cut Test (Tape Test)")).setBorder(SOLID_BORDER));
			table.addCell(
					new Cell().add(new Paragraph(report.getCrossCutTest() != null ? report.getCrossCutTest() : ""))
							.setBorder(SOLID_BORDER));
			table.addCell(new Cell().setBorder(SOLID_BORDER));

			// Coating Thickness
			table.addCell(
					new Cell().add(new Paragraph("6")).setTextAlignment(TextAlignment.CENTER).setBorder(SOLID_BORDER));
			table.addCell(new Cell().add(new Paragraph("Coating Thickness")).setBorder(SOLID_BORDER));

			Cell thicknessCell = new Cell().setBorder(SOLID_BORDER).setPadding(3);

			Table thicknessTable = new Table(UnitValue.createPercentArray(new float[] { 30, 70 }))
					.setWidth(UnitValue.createPercentValue(100));

			thicknessTable.addCell(new Cell().add(new Paragraph("Body :")).setBorder(SOLID_BORDER));
			thicknessTable.addCell(new Cell()
					.add(new Paragraph(
							report.getCoatingThicknessBody() != null ? report.getCoatingThicknessBody() : ""))
					.setBorder(SOLID_BORDER));

			thicknessTable.addCell(new Cell().add(new Paragraph("Bottom :")).setBorder(SOLID_BORDER));
			thicknessTable.addCell(new Cell()
					.add(new Paragraph(
							report.getCoatingThicknessBottom() != null ? report.getCoatingThicknessBottom() : ""))
					.setBorder(SOLID_BORDER));

			thicknessCell.add(thicknessTable);
			table.addCell(thicknessCell);
			table.addCell(new Cell().setBorder(SOLID_BORDER));

			// Temperature
			table.addCell(
					new Cell().add(new Paragraph("7")).setTextAlignment(TextAlignment.CENTER).setBorder(SOLID_BORDER));
			table.addCell(new Cell().add(new Paragraph("Temperature")).setBorder(SOLID_BORDER));
			table.addCell(new Cell().add(new Paragraph(report.getTemperature() != null ? report.getTemperature() : ""))
					.setBorder(SOLID_BORDER));
			table.addCell(new Cell().setBorder(SOLID_BORDER));

			// Viscosity
			table.addCell(
					new Cell().add(new Paragraph("8")).setTextAlignment(TextAlignment.CENTER).setBorder(SOLID_BORDER));
			table.addCell(new Cell().add(new Paragraph("Viscosity")).setBorder(SOLID_BORDER));
			table.addCell(new Cell().add(new Paragraph(report.getViscosity() != null ? report.getViscosity() : ""))
					.setBorder(SOLID_BORDER));
			table.addCell(new Cell().setBorder(SOLID_BORDER));

			// Batch Composition
			table.addCell(
					new Cell().add(new Paragraph("9")).setTextAlignment(TextAlignment.CENTER).setBorder(SOLID_BORDER));
			table.addCell(new Cell().add(new Paragraph("Batch Composition")).setBorder(SOLID_BORDER));
			table.addCell(new Cell()
					.add(new Paragraph(report.getBatchComposition() != null ? report.getBatchComposition() : ""))
					.setBorder(SOLID_BORDER));
			table.addCell(new Cell().setBorder(SOLID_BORDER));
		}

		document.add(table);
	}

	private void addSignatureSection(Document document, CoatingInspectionReport report, PdfFont font,
			PdfFont fontBold) {
		document.add(new Paragraph("\n").setFontSize(5));

		Table signatureTable = new Table(2).setWidth(UnitValue.createPercentValue(100)).setBorder(SOLID_BORDER)
				.setFontSize(CONTENT_FONT_SIZE);

		// QA Dept Row
		Cell qaTitle = new Cell().add(new Paragraph("QA Exit").setFont(fontBold)).setBorder(SOLID_BORDER).setPadding(5);

		Cell qaSign = new Cell().setBorder(SOLID_BORDER).setPadding(5);
		// Add QA signature if available
		if (report.getQaSignature() != null && !report.getQaSignature().isEmpty()) {
			try {
				ClassPathResource resource = new ClassPathResource("static/images/QASign.png");
				Image qaSignImg = new Image(ImageDataFactory.create(resource.getInputStream().readAllBytes()))
						.scaleToFit(100, 30);
				qaSign.add(qaSignImg);
			} catch (Exception e) {
				qaSign.add(new Paragraph(report.getQaName() != null ? report.getQaName() + " (signed)" : ""));
			}
		} else if (report.getQaName() != null && !report.getQaName().isEmpty()) {
			qaSign.add(new Paragraph(report.getQaName()));
		}

		signatureTable.addCell(qaTitle);
		signatureTable.addCell(qaSign);

		// Production Dept Row
		Cell operatorTitle = new Cell().add(new Paragraph("Production/Operator Name").setFont(fontBold))
				.setBorder(SOLID_BORDER).setPadding(5);

		Cell operatorSign = new Cell().setBorder(SOLID_BORDER).setPadding(5);
		// Add operator signature if available
		if (report.getOperatorSignature() != null && !report.getOperatorSignature().isEmpty()) {
			try {
				ClassPathResource resource = new ClassPathResource("static/images/OperatorSign.png");
				Image operatorSignImg = new Image(ImageDataFactory.create(resource.getInputStream().readAllBytes()))
						.scaleToFit(100, 30);
				operatorSign.add(operatorSignImg);
			} catch (Exception e) {
				operatorSign.add(
						new Paragraph(report.getOperatorName() != null ? report.getOperatorName() + " (signed)" : ""));
			}
		} else if (report.getOperatorName() != null && !report.getOperatorName().isEmpty()) {
			operatorSign.add(new Paragraph(report.getOperatorName()));
		}

		signatureTable.addCell(operatorTitle);
		signatureTable.addCell(operatorSign);

		// Approval time
		Cell timeTitle = new Cell().add(new Paragraph("Time (Final Approval) :").setFont(fontBold))
				.setBorder(SOLID_BORDER).setPadding(5);

		Cell timeValue = new Cell().add(new Paragraph(report.getApprovalTime() != null ? report.getApprovalTime() : ""))
				.setBorder(SOLID_BORDER).setPadding(5);

		signatureTable.addCell(timeTitle);
		signatureTable.addCell(timeValue);

		document.add(signatureTable);
	}
}