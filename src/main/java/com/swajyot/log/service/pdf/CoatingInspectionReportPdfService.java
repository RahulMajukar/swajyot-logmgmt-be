package com.swajyot.log.service.pdf;

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
	private static final float BORDER_WIDTH = 0.5f;
	private static final Border SOLID_BORDER = new SolidBorder(ColorConstants.BLACK, BORDER_WIDTH);
	private static final DeviceRgb HEADER_BG_COLOR = new DeviceRgb(230, 230, 230);

	private static final float CONTENT_FONT_SIZE = 9f;
	private static final float SMALL_FONT_SIZE = 8f;

	public byte[] generatePdf(CoatingInspectionReport report, String username) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfWriter writer = new PdfWriter(baos);
		PdfDocument pdf = new PdfDocument(writer);
		Document document = new Document(pdf, PageSize.A4);
		document.setMargins(15, 36, 15, 36);
		PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
		PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

		PdfCommonComponents.addHeader(document, report, fontBold);
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

	private void addCoatingDetailsTable(Document document, CoatingInspectionReport report, PdfFont font, PdfFont fontBold) {
	    document.add(new Paragraph("\n").setFontSize(3));

	    Table table = new Table(UnitValue.createPercentArray(new float[]{5, 30, 20, 20, 25}))
	            .setWidth(UnitValue.createPercentValue(100))
	            .setFontSize(CONTENT_FONT_SIZE);

	    // Table headers
	    table.addHeaderCell(new Cell().add(new Paragraph("S.No.").setFont(fontBold))
	            .setBackgroundColor(HEADER_BG_COLOR).setBorder(SOLID_BORDER).setPadding(4).setTextAlignment(TextAlignment.CENTER));

	    table.addHeaderCell(new Cell().add(new Paragraph("Lacquer / Dye").setFont(fontBold))
	            .setBackgroundColor(HEADER_BG_COLOR).setBorder(SOLID_BORDER).setPadding(4));

	    table.addHeaderCell(new Cell().add(new Paragraph("Batch No.").setFont(fontBold))
	            .setBackgroundColor(HEADER_BG_COLOR).setBorder(SOLID_BORDER).setPadding(4));

	    table.addHeaderCell(new Cell().add(new Paragraph("Qty.").setFont(fontBold))
	            .setBackgroundColor(HEADER_BG_COLOR).setBorder(SOLID_BORDER).setPadding(4));

	    table.addHeaderCell(new Cell().add(new Paragraph("Expiry Date").setFont(fontBold))
	            .setBackgroundColor(HEADER_BG_COLOR).setBorder(SOLID_BORDER).setPadding(4));

	    // Add each coating detail
	    if (report.getCoatingDetails() != null && !report.getCoatingDetails().isEmpty()) {
	        for (Map<String, Object> detail : report.getCoatingDetails()) {
	            int id = ((Number) detail.getOrDefault("id", 0)).intValue();
	            String lacquerType = (String) detail.getOrDefault("lacquerType", "");
	            String batchNo = (String) detail.getOrDefault("batchNo", "");
	            String quantity = (String) detail.getOrDefault("quantity", "");
	            String expiryDate = (String) detail.getOrDefault("expiryDate", "");

	            table.addCell(new Cell().add(new Paragraph(String.valueOf(id)))
	                    .setTextAlignment(TextAlignment.CENTER).setBorder(SOLID_BORDER).setPadding(4));

	            table.addCell(new Cell().add(new Paragraph(lacquerType)).setBorder(SOLID_BORDER).setPadding(4));
	            table.addCell(new Cell().add(new Paragraph(batchNo)).setBorder(SOLID_BORDER).setPadding(4));
	            table.addCell(new Cell().add(new Paragraph(quantity)).setBorder(SOLID_BORDER).setPadding(4));
	            table.addCell(new Cell().add(new Paragraph(expiryDate)).setBorder(SOLID_BORDER).setPadding(4));
	        }
	    } else {
	        // Fill 5-column empty rows if no data
	        for (int i = 0; i < 5; i++) {
	            for (int j = 0; j < 5; j++) {
	                table.addCell(new Cell().add(new Paragraph("")).setBorder(SOLID_BORDER).setPadding(4));
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

		// Header row
		table.addHeaderCell(new Cell().add(new Paragraph("S.No.").setFont(fontBold)).setBackgroundColor(HEADER_BG_COLOR)
				.setBorder(SOLID_BORDER).setPadding(4).setTextAlignment(TextAlignment.CENTER));

		table.addHeaderCell(new Cell().add(new Paragraph("Characteristic").setFont(fontBold))
				.setBackgroundColor(HEADER_BG_COLOR).setBorder(SOLID_BORDER).setPadding(4));

		table.addHeaderCell(
				new Cell().add(new Paragraph("As per Reference sample no. X-211\nObservations").setFont(fontBold))
						.setBackgroundColor(HEADER_BG_COLOR).setBorder(SOLID_BORDER)
						.setTextAlignment(TextAlignment.CENTER).setPadding(4));

		table.addHeaderCell(new Cell().add(new Paragraph("Comments").setFont(fontBold))
				.setBackgroundColor(HEADER_BG_COLOR).setBorder(SOLID_BORDER).setPadding(4));

		// Predefined characteristics with their default values
		String[][] defaultCharacteristics = { { "1", "Colour Shade", "232", "2323" },
				{ "2", "Colour Height", "2332", "" }, 
				{ "3", "Any Visual defect", "", "" }, 
				{ "4", "MEK Test", "", "" },
				{ "5", "Cross Cut Test (Tape Test)", "", "" }, 
				{ "6", "Coating Thickness", "", "23" }, // Special row
				{ "7", "Temperature", "", "" }, 
				{ "8", "Viscosity", "", "" },
				{ "9", "Batch Composition", "Clear Extn 3 Red Dye 2", "" } };

		// Get actual characteristics from report
		List<Map<String, Object>> characteristics = report.getCharacteristics();

		int tableRowIndex = 0;

		for (int i = 0; i < defaultCharacteristics.length; i++) {
			String[] defaultRow = defaultCharacteristics[i];

			// Try to get actual data from report if available
			Map<String, Object> actualRow = null;
			if (characteristics != null && i < characteristics.size()) {
				actualRow = characteristics.get(i);
			}

			String sNo = defaultRow[0];
			String charName = actualRow != null ? (String) actualRow.getOrDefault("characteristic", defaultRow[1])
					: defaultRow[1];
			String observation = actualRow != null ? (String) actualRow.getOrDefault("observations", defaultRow[2])
					: defaultRow[2];
			String comment = actualRow != null ? (String) actualRow.getOrDefault("comments", defaultRow[3])
					: defaultRow[3];

			// Special handling for Coating Thickness row (row 6)
			if (i == 5) { // Index 5 = Row 6
				// Get body and bottom observation values
				String bodyObservation = "233";
				String bottomObservation = "23";

				if (actualRow != null) {
					if (actualRow.containsKey("bodyObservations")) {
						bodyObservation = (String) actualRow.get("bodyObservations");
					}
					if (actualRow.containsKey("bottomObservations")) {
						bottomObservation = (String) actualRow.get("bottomObservations");
					}
				}

				// First sub-row: S.No, Characteristic, Body observation, Comments
				table.addCell(new Cell(2, 1).add(new Paragraph(sNo)).setTextAlignment(TextAlignment.CENTER)
						.setBorder(SOLID_BORDER).setPadding(4).setVerticalAlignment(VerticalAlignment.MIDDLE));

				table.addCell(new Cell(2, 1).add(new Paragraph(charName)).setBorder(SOLID_BORDER).setPadding(4)
						.setVerticalAlignment(VerticalAlignment.MIDDLE));

				// Create a nested table for the observation column
				Table observationTable = new Table(UnitValue.createPercentArray(new float[] { 40, 60 }))
						.setWidth(UnitValue.createPercentValue(100)).setMargin(0).setPadding(0);

				// Body row in nested table
				observationTable.addCell(new Cell().add(new Paragraph("Body")).setBorder(SOLID_BORDER).setPadding(3)
						.setTextAlignment(TextAlignment.CENTER).setFontSize(CONTENT_FONT_SIZE));

				observationTable.addCell(new Cell().add(new Paragraph(bodyObservation)).setBorder(SOLID_BORDER)
						.setPadding(3).setFontSize(CONTENT_FONT_SIZE));

				// Bottom row in nested table
				observationTable.addCell(new Cell().add(new Paragraph("Bottom")).setBorder(SOLID_BORDER).setPadding(3)
						.setTextAlignment(TextAlignment.CENTER).setFontSize(CONTENT_FONT_SIZE));

				observationTable.addCell(new Cell().add(new Paragraph(bottomObservation)).setBorder(SOLID_BORDER)
						.setPadding(3).setFontSize(CONTENT_FONT_SIZE));

				// Add the nested table as a single cell
				table.addCell(new Cell().add(observationTable).setBorder(SOLID_BORDER).setPadding(0));

				// Comments cell with rowspan
				table.addCell(new Cell(2, 1).add(new Paragraph(comment)).setBorder(SOLID_BORDER).setPadding(4)
						.setVerticalAlignment(VerticalAlignment.MIDDLE));

			} else {
				// Regular rows - add all four cells normally
				table.addCell(new Cell().add(new Paragraph(sNo)).setTextAlignment(TextAlignment.CENTER)
						.setBorder(SOLID_BORDER).setPadding(4));

				table.addCell(new Cell().add(new Paragraph(charName)).setBorder(SOLID_BORDER).setPadding(4));

				table.addCell(new Cell().add(new Paragraph(observation)).setBorder(SOLID_BORDER).setPadding(4));

				table.addCell(new Cell().add(new Paragraph(comment)).setBorder(SOLID_BORDER).setPadding(4));
			}
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