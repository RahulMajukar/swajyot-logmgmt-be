package com.swajyot.log.config;

import com.swajyot.log.model.LineClearanceReport;
import com.swajyot.log.repository.LineClearanceReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class LineClearanceReportDataInitializer {

    private final LineClearanceReportRepository lineClearanceReportRepository;

    @Bean
//    @Profile("dev") // Only run in development environment
    public CommandLineRunner initLineClearanceReportData() {
        return args -> {
            // Check if data already exists
            if (lineClearanceReportRepository.count() > 0) {
                System.out.println("Line Clearance Report data already exists, skipping initialization");
                return;
            }

            System.out.println("Initializing Line Clearance Report sample data...");

            // Create sample reports
            createCoatingLineClearanceReport();
            createPrintingLineClearanceReport();
            createCombinedLineClearanceReport();

            System.out.println("Line Clearance Report sample data initialization complete");
        };
    }

    private void createCoatingLineClearanceReport() {
        LineClearanceReport report = new LineClearanceReport();
        
        // Document info
        report.setDocumentNo("AGI-IMS-DEC-L4-21");
        report.setRevision("02");
        report.setEffectiveDate(LocalDate.now().minusMonths(3));
        report.setReviewedOn(LocalDate.now().minusMonths(2));
        report.setPage("1/1");
        report.setPreparedBy("QCM-QC");
        report.setApprovedBy("AVP-QA & SYS");
        report.setIssued("AVP-QA & SYS");
        
        // Report details
        report.setReportDate(LocalDate.now());
        report.setShift("A / B / C");
        report.setLine("COATING");
        
        // Product details
        report.setProductName("100 ml L/F1");
        report.setExistingVariantDescription("100ml L/F1 (Red matt)");
        report.setNewVariantDescription("100ml L/F1 (Lemon matt)");
        report.setExistingVariantName("100ml L/F1 (Red matt)");
        report.setNewVariantName("100ml L/F1 (Lemon matt)");
        report.setExistingVariantStopTime(LocalDateTime.of(LocalDate.now(), LocalTime.of(14, 0)));
        report.setNewVariantStartTime(LocalDateTime.of(LocalDate.now(), LocalTime.of(14, 30)));
        
        // Check points
        List<Map<String, Object>> checkPoints = new ArrayList<>();
        
        // Checkpoint 1
        Map<String, Object> checkPoint1 = new HashMap<>();
        checkPoint1.put("id", 1);
        checkPoint1.put("checkPoint", "Ensure no Rejection / OK bottles of existing Job / Variant kept at Coating Loading / Unloading area.");
        checkPoint1.put("responsibility", "Quality Engineer");
        checkPoint1.put("remarks", "No bottle found");
        checkPoint1.put("isCompleted", true);
        checkPoint1.put("applicableArea", "COATING");
        checkPoints.add(checkPoint1);
        
        // Checkpoint 2
        Map<String, Object> checkPoint2 = new HashMap<>();
        checkPoint2.put("id", 2);
        checkPoint2.put("checkPoint", "Ensure no Rejection / Ok bottles of existing Job / Variant kept at Printing Production area (All M/c an lehr Loading end)");
        checkPoint2.put("responsibility", "Printing Operator");
        checkPoint2.put("remarks", "No bottle found");
        checkPoint2.put("isCompleted", true);
        checkPoint2.put("applicableArea", "PRINTING");
        checkPoints.add(checkPoint2);
        
        // Checkpoint 3
        Map<String, Object> checkPoint3 = new HashMap<>();
        checkPoint3.put("id", 3);
        checkPoint3.put("checkPoint", "Ensure no Rejection / Ok bottles of existing Job / Variant kept at Lehr end Inspection area and below Lehr Conveyor.");
        checkPoint3.put("responsibility", "Executive");
        checkPoint3.put("remarks", "No bottle found");
        checkPoint3.put("isCompleted", true);
        checkPoint3.put("applicableArea", "BOTH");
        checkPoints.add(checkPoint3);
        
        // Checkpoint 4
        Map<String, Object> checkPoint4 = new HashMap<>();
        checkPoint4.put("id", 4);
        checkPoint4.put("checkPoint", "Ensure no Rejection / Ok bottles of existing Job / Variant kept at QC laboratory");
        checkPoint4.put("responsibility", "Executive");
        checkPoint4.put("remarks", "No bottle found");
        checkPoint4.put("isCompleted", true);
        checkPoint4.put("applicableArea", "BOTH");
        checkPoints.add(checkPoint4);
        
        // Checkpoint 5
        Map<String, Object> checkPoint5 = new HashMap<>();
        checkPoint5.put("id", 5);
        checkPoint5.put("checkPoint", "Ensure all box Labels of existing Job / Variant are withdraw or Removed");
        checkPoint5.put("responsibility", "Executive");
        checkPoint5.put("remarks", "Removed");
        checkPoint5.put("isCompleted", true);
        checkPoint5.put("applicableArea", "BOTH");
        checkPoints.add(checkPoint5);
        
        report.setCheckPoints(checkPoints);
        
        // Signatures
        report.setResponsibleName("John Doe");
        report.setProductionName("Shiva Prasad");
        report.setQualityName("Sreejan");
        
        // Status
        report.setStatus(LineClearanceReport.ReportStatus.APPROVED);
        report.setSubmittedBy("operator1");
        report.setSubmittedAt(LocalDateTime.now().minusHours(2));
        report.setReviewedBy("supervisor1");
        report.setReviewedAt(LocalDateTime.now().minusHours(1));
        
        // Area
        report.setProductionArea(LineClearanceReport.ProductionArea.COATING);
        
        lineClearanceReportRepository.save(report);
    }
    
    private void createPrintingLineClearanceReport() {
        LineClearanceReport report = new LineClearanceReport();
        
        // Document info
        report.setDocumentNo("AGI-IMS-DEC-L4-22");
        report.setRevision("01");
        report.setEffectiveDate(LocalDate.now().minusMonths(2));
        report.setReviewedOn(LocalDate.now().minusMonths(1));
        report.setPage("1/1");
        report.setPreparedBy("QCM-QC");
        report.setApprovedBy("AVP-QA & SYS");
        report.setIssued("AVP-QA & SYS");
        
        // Report details
        report.setReportDate(LocalDate.now());
        report.setShift("B");
        report.setLine("PRINTING");
        
        // Product details
        report.setProductName("150 ml L/F1");
        report.setExistingVariantDescription("150ml L/F1 (Blue print)");
        report.setNewVariantDescription("150ml L/F1 (Gold print)");
        report.setExistingVariantName("150ml L/F1 (Blue print)");
        report.setNewVariantName("150ml L/F1 (Gold print)");
        report.setExistingVariantStopTime(LocalDateTime.of(LocalDate.now(), LocalTime.of(16, 0)));
        report.setNewVariantStartTime(LocalDateTime.of(LocalDate.now(), LocalTime.of(16, 30)));
        
        // Check points - similar to coating but with printing-specific checks
        List<Map<String, Object>> checkPoints = new ArrayList<>();
        
        // Similar checkpoint structure as in createCoatingLineClearanceReport
        // Checkpoint 1
        Map<String, Object> checkPoint1 = new HashMap<>();
        checkPoint1.put("id", 1);
        checkPoint1.put("checkPoint", "Ensure no Rejection / OK bottles of existing Job / Variant kept at Coating Loading / Unloading area.");
        checkPoint1.put("responsibility", "Quality Engineer");
        checkPoint1.put("remarks", "No bottle found");
        checkPoint1.put("isCompleted", true);
        checkPoint1.put("applicableArea", "COATING");
        checkPoints.add(checkPoint1);
        
        // Add more printing-specific checkpoints here
        // Checkpoint 6
        Map<String, Object> checkPoint6 = new HashMap<>();
        checkPoint6.put("id", 6);
        checkPoint6.put("checkPoint", "Ensure all empty boxes / Trays / Partitions of existing Job / Variant are withdraw or removed");
        checkPoint6.put("responsibility", "Production Engineer");
        checkPoint6.put("remarks", "Removed");
        checkPoint6.put("isCompleted", true);
        checkPoint6.put("applicableArea", "BOTH");
        checkPoints.add(checkPoint6);
        
        // Checkpoint 7
        Map<String, Object> checkPoint7 = new HashMap<>();
        checkPoint7.put("id", 7);
        checkPoint7.put("checkPoint", "Ensure all Final Pallet cards of existing Job / Variant are withdraw or removed");
        checkPoint7.put("responsibility", "Production Engineer");
        checkPoint7.put("remarks", "Removed");
        checkPoint7.put("isCompleted", true);
        checkPoint7.put("applicableArea", "BOTH");
        checkPoints.add(checkPoint7);
        
        // Checkpoint 8
        Map<String, Object> checkPoint8 = new HashMap<>();
        checkPoint8.put("id", 8);
        checkPoint8.put("checkPoint", "Ensure all existing Job / Variant are withdraw or Removed from Offline sorting Resorting area");
        checkPoint8.put("responsibility", "Production Engineer");
        checkPoint8.put("remarks", "Removed");
        checkPoint8.put("isCompleted", true);
        checkPoint8.put("applicableArea", "BOTH");
        checkPoints.add(checkPoint8);
        
        report.setCheckPoints(checkPoints);
        
        // Signatures
        report.setResponsibleName("Jane Smith");
        report.setProductionName("Amit Kumar");
        report.setQualityName("Priya Singh");
        
        // Status
        report.setStatus(LineClearanceReport.ReportStatus.SUBMITTED);
        report.setSubmittedBy("operator2");
        report.setSubmittedAt(LocalDateTime.now().minusHours(4));
        
        // Area
        report.setProductionArea(LineClearanceReport.ProductionArea.PRINTING);
        
        lineClearanceReportRepository.save(report);
    }
    
    private void createCombinedLineClearanceReport() {
        LineClearanceReport report = new LineClearanceReport();
        
        // Document info
        report.setDocumentNo("AGI-IMS-DEC-L4-23");
        report.setRevision("01");
        report.setEffectiveDate(LocalDate.now().minusMonths(1));
        report.setReviewedOn(LocalDate.now().minusWeeks(2));
        report.setPage("1/1");
        report.setPreparedBy("QCM-QC");
        report.setApprovedBy("AVP-QA & SYS");
        report.setIssued("AVP-QA & SYS");
        
        // Report details
        report.setReportDate(LocalDate.now().minusDays(2));
        report.setShift("C");
        report.setLine("COATING / PRINTING");
        
        // Product details
        report.setProductName("200 ml L/F1");
        report.setExistingVariantDescription("200ml L/F1 (Green matt & blue logo)");
        report.setNewVariantDescription("200ml L/F1 (Orange matt & black logo)");
        report.setExistingVariantName("200ml L/F1 (Green matt & blue logo)");
        report.setNewVariantName("200ml L/F1 (Orange matt & black logo)");
        report.setExistingVariantStopTime(LocalDateTime.of(LocalDate.now().minusDays(2), LocalTime.of(22, 0)));
        report.setNewVariantStartTime(LocalDateTime.of(LocalDate.now().minusDays(2), LocalTime.of(22, 45)));
        
        // Check points - combining all checks for both areas
        List<Map<String, Object>> checkPoints = new ArrayList<>();
        
        // Add all checkpoints from both areas
        // Checkpoint 1-8 similar to previous methods
        // Checkpoint 1
        Map<String, Object> checkPoint1 = new HashMap<>();
        checkPoint1.put("id", 1);
        checkPoint1.put("checkPoint", "Ensure no Rejection / OK bottles of existing Job / Variant kept at Coating Loading / Unloading area.");
        checkPoint1.put("responsibility", "Quality Engineer");
        checkPoint1.put("remarks", "No bottle found");
        checkPoint1.put("isCompleted", true);
        checkPoint1.put("applicableArea", "COATING");
        checkPoints.add(checkPoint1);
        
        // All checkpoints from previous methods...
        // For brevity, adding just a few more
        
        // Checkpoint 2
        Map<String, Object> checkPoint2 = new HashMap<>();
        checkPoint2.put("id", 2);
        checkPoint2.put("checkPoint", "Ensure no Rejection / Ok bottles of existing Job / Variant kept at Printing Production area (All M/c an lehr Loading end)");
        checkPoint2.put("responsibility", "Printing Operator");
        checkPoint2.put("remarks", "No bottle found");
        checkPoint2.put("isCompleted", true);
        checkPoint2.put("applicableArea", "PRINTING");
        checkPoints.add(checkPoint2);
        
        // Checkpoint 8
        Map<String, Object> checkPoint8 = new HashMap<>();
        checkPoint8.put("id", 8);
        checkPoint8.put("checkPoint", "Ensure all existing Job / Variant are withdraw or Removed from Offline sorting Resorting area");
        checkPoint8.put("responsibility", "Production Engineer");
        checkPoint8.put("remarks", "Removed");
        checkPoint8.put("isCompleted", true);
        checkPoint8.put("applicableArea", "BOTH");
        checkPoints.add(checkPoint8);
        
        report.setCheckPoints(checkPoints);
        
        // Signatures
        report.setResponsibleName("Rajesh Mehta");
        report.setProductionName("Deepak Kumar");
        report.setQualityName("Neha Sharma");
        
        // Status
        report.setStatus(LineClearanceReport.ReportStatus.DRAFT);
        
        // Area
        report.setProductionArea(LineClearanceReport.ProductionArea.BOTH);
        
        lineClearanceReportRepository.save(report);
    }
}