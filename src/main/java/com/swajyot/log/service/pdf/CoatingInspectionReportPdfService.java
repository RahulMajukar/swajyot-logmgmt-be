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