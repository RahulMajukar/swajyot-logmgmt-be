package com.swajyot.log.config;

import com.swajyot.log.model.IncomingQualityInspectionReport;
import com.swajyot.log.repository.IncomingQualityInspectionReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class IncomingQualityInspectionReportDataInitializer {

    private final IncomingQualityInspectionReportRepository reportRepository;

    @Bean
//    @Profile("dev") // Only run in development environment
    public CommandLineRunner initIncomingQualityInspectionReportData() {
        return args -> {
            // Check if data already exists
            if (reportRepository.count() > 0) {
                System.out.println("Incoming Quality Inspection Report data already exists, skipping initialization");
                return;
            }

            System.out.println("Initializing Incoming Quality Inspection Report sample data...");

            // Create sample reports
            createPassReport();
            createFailReport();
            createDraftReport();

            System.out.println("Incoming Quality Inspection Report sample data initialization complete");
        };
    }

    private void createPassReport() {
        IncomingQualityInspectionReport report = new IncomingQualityInspectionReport();
        
        // Document info
        report.setDocumentNo("AGI-IMS-DEC-L4-11");
        report.setRevision("00");
        report.setEffectiveDate(LocalDate.of(2024, 1, 6));
        report.setReviewedOn(LocalDate.of(2027, 3, 31));
        report.setPage("1 of 2");
        report.setPreparedBy("DGM QC");
        report.setApprovedBy("AVP-QA & SYS");
        report.setIssued("AVP-QA & SYS");
        
        // Report details
        report.setIqcDate(LocalDate.now().minusDays(5));
        report.setShift("B/S");
        
        // Product details
        report.setProductVariantName("200 ml FLAT Shoulder only Frosted");
        report.setProductReceivedFrom("Himshikha Traders");
        report.setSupplierShift("B/S");
        report.setProductReceivedDate(LocalDate.now().minusDays(6));
        report.setProductReceivedQuantity(75900);
        report.setQuantityAudited(500);
        report.setBatchNumber("0622248");
        
        // Audit results
        List<Map<String, Object>> auditResults = new ArrayList<>();
        
        Map<String, Object> okResult = new HashMap<>();
        okResult.put("category", "OK");
        okResult.put("count", 61);
        okResult.put("defectName", "Inside dust, Neck crack, Neck chipping, Body crack");
        auditResults.add(okResult);
        
        Map<String, Object> criticalResult = new HashMap<>();
        criticalResult.put("category", "CRITICAL");
        criticalResult.put("count", 429);
        criticalResult.put("defectName", "Inside dust, Neck crack, chipping, body mark, inside water");
        auditResults.add(criticalResult);
        
        Map<String, Object> majorAResult = new HashMap<>();
        majorAResult.put("category", "MAJOR-A");
        majorAResult.put("count", 0);
        majorAResult.put("defectName", "");
        auditResults.add(majorAResult);
        
        Map<String, Object> majorBResult = new HashMap<>();
        majorBResult.put("category", "MAJOR-B");
        majorBResult.put("count", 0);
        majorBResult.put("defectName", "");
        auditResults.add(majorBResult);
        
        Map<String, Object> minorResult = new HashMap<>();
        minorResult.put("category", "MINOR");
        minorResult.put("count", 10);
        minorResult.put("defectName", "Outside dust - Powder");
        auditResults.add(minorResult);
        
        report.setAuditResults(auditResults);
        
        // Test results
        List<Map<String, Object>> testResults = new ArrayList<>();
        
        Map<String, Object> surfacePh = new HashMap<>();
        surfacePh.put("testName", "SURFACE pH");
        surfacePh.put("specification", "MIN - 6.0 and MAX - 7.5");
        surfacePh.put("result", "7.1 pH");
        surfacePh.put("checkedBy", "Srikant M");
        surfacePh.put("isPass", true);
        testResults.add(surfacePh);
        
        Map<String, Object> surfaceTension = new HashMap<>();
        surfaceTension.put("testName", "SURFACE TENSION");
        surfaceTension.put("specification", "MIN - 34 mN/m");
        surfaceTension.put("result", "43 mN/m");
        surfaceTension.put("checkedBy", "Srikant");
        surfaceTension.put("isPass", true);
        testResults.add(surfaceTension);
        
        Map<String, Object> printingPosition = new HashMap<>();
        printingPosition.put("testName", "PRINTING POSITION");
        printingPosition.put("specification", "-");
        printingPosition.put("result", "N/A");
        printingPosition.put("checkedBy", "");
        printingPosition.put("isPass", true);
        testResults.add(printingPosition);
        
        Map<String, Object> positiveMatch = new HashMap<>();
        positiveMatch.put("testName", "POSITIVE MATCH");
        positiveMatch.put("specification", "OK / NOT OK");
        positiveMatch.put("result", "N/A");
        positiveMatch.put("checkedBy", "");
        positiveMatch.put("isPass", true);
        testResults.add(positiveMatch);
        
        Map<String, Object> nailTest = new HashMap<>();
        nailTest.put("testName", "NAIL TEST");
        nailTest.put("specification", "OK / NOT OK");
        nailTest.put("result", "N/A");
        nailTest.put("checkedBy", "");
        nailTest.put("isPass", true);
        testResults.add(nailTest);
        
        Map<String, Object> scotchTapeTest = new HashMap<>();
        scotchTapeTest.put("testName", "SCOTCH TAPE TEST");
        scotchTapeTest.put("specification", "OK / NOT OK");
        scotchTapeTest.put("result", "N/A");
        scotchTapeTest.put("checkedBy", "");
        scotchTapeTest.put("isPass", true);
        testResults.add(scotchTapeTest);
        
        Map<String, Object> mekTest = new HashMap<>();
        mekTest.put("testName", "MEK TEST");
        mekTest.put("specification", "OK / NOT OK");
        mekTest.put("result", "N/A");
        mekTest.put("checkedBy", "");
        mekTest.put("isPass", true);
        testResults.add(mekTest);
        
        Map<String, Object> compatibilityTest = new HashMap<>();
        compatibilityTest.put("testName", "COMPATIBILITY TEST");
        compatibilityTest.put("specification", "OK / NOT OK");
        compatibilityTest.put("result", "N/A");
        compatibilityTest.put("checkedBy", "");
        compatibilityTest.put("isPass", true);
        testResults.add(compatibilityTest);
        
        Map<String, Object> crossCutTest = new HashMap<>();
        crossCutTest.put("testName", "CROSS CUT TEST");
        crossCutTest.put("specification", "Level 2 - Max");
        crossCutTest.put("result", "N/A");
        crossCutTest.put("checkedBy", "");
        crossCutTest.put("isPass", true);
        testResults.add(crossCutTest);
        
        report.setTestResults(testResults);
        
        // Quality decision
        report.setQualityDecision("PASS");
        
        // Signature
        report.setQualityManagerName("Quality Manager");
        report.setQualityManagerSignature("QualityManagerSign");
        report.setSignatureDate(LocalDate.now());
        
        // Status
        report.setStatus(IncomingQualityInspectionReport.ReportStatus.APPROVED);
        report.setSubmittedBy("quality.inspector");
        report.setSubmittedAt(LocalDateTime.now().minusDays(1));
        report.setReviewedBy("quality.manager");
        report.setReviewedAt(LocalDateTime.now().minusHours(2));
        report.setComments("Approved. All tests passed.");
        
        reportRepository.save(report);
    }
    
    private void createFailReport() {
        IncomingQualityInspectionReport report = new IncomingQualityInspectionReport();
        
        // Document info
        report.setDocumentNo("AGI-IMS-DEC-L4-12");
        report.setRevision("00");
        report.setEffectiveDate(LocalDate.of(2024, 1, 6));
        report.setReviewedOn(LocalDate.of(2027, 3, 31));
        report.setPage("1 of 2");
        report.setPreparedBy("DGM QC");
        report.setApprovedBy("AVP-QA & SYS");
        report.setIssued("AVP-QA & SYS");
        
        // Report details
        report.setIqcDate(LocalDate.now().minusDays(2));
        report.setShift("A/S");
        
        // Product details
        report.setProductVariantName("300 ml Round Only Frosted");
        report.setProductReceivedFrom("Ganesh Enterprises");
        report.setSupplierShift("A/S");
        report.setProductReceivedDate(LocalDate.now().minusDays(3));
        report.setProductReceivedQuantity(82000);
        report.setQuantityAudited(500);
        report.setBatchNumber("0622250");
        
        // Audit results
        List<Map<String, Object>> auditResults = new ArrayList<>();
        
        Map<String, Object> okResult = new HashMap<>();
        okResult.put("category", "OK");
        okResult.put("count", 12);
        okResult.put("defectName", "Clean bottles");
        auditResults.add(okResult);
        
        Map<String, Object> criticalResult = new HashMap<>();
        criticalResult.put("category", "CRITICAL");
        criticalResult.put("count", 356);
        criticalResult.put("defectName", "Inside dust, Neck crack, chipping, body mark");
        auditResults.add(criticalResult);
        
        Map<String, Object> majorAResult = new HashMap<>();
        majorAResult.put("category", "MAJOR-A");
        majorAResult.put("count", 98);
        majorAResult.put("defectName", "Foreign particles, Stones in glass");
        auditResults.add(majorAResult);
        
        Map<String, Object> majorBResult = new HashMap<>();
        majorBResult.put("category", "MAJOR-B");
        majorBResult.put("count", 22);
        majorBResult.put("defectName", "Inconsistent finish");
        auditResults.add(majorBResult);
        
        Map<String, Object> minorResult = new HashMap<>();
        minorResult.put("category", "MINOR");
        minorResult.put("count", 12);
        minorResult.put("defectName", "Outside dust - Powder");
        auditResults.add(minorResult);
        
        report.setAuditResults(auditResults);
        
        // Test results
        List<Map<String, Object>> testResults = new ArrayList<>();
        
        Map<String, Object> surfacePh = new HashMap<>();
        surfacePh.put("testName", "SURFACE pH");
        surfacePh.put("specification", "MIN - 6.0 and MAX - 7.5");
        surfacePh.put("result", "5.8 pH");
        surfacePh.put("checkedBy", "Srikant M");
        surfacePh.put("isPass", false);
        testResults.add(surfacePh);
        
        Map<String, Object> surfaceTension = new HashMap<>();
        surfaceTension.put("testName", "SURFACE TENSION");
        surfaceTension.put("specification", "MIN - 34 mN/m");
        surfaceTension.put("result", "30 mN/m");
        surfaceTension.put("checkedBy", "Srikant");
        surfaceTension.put("isPass", false);
        testResults.add(surfaceTension);
        
        report.setTestResults(testResults);
        
        // Quality decision
        report.setQualityDecision("FAIL");
        
        // Signature
        report.setQualityManagerName("Quality Manager");
        report.setQualityManagerSignature("QualityManagerSign");
        report.setSignatureDate(LocalDate.now());
        
        // Status
        report.setStatus(IncomingQualityInspectionReport.ReportStatus.APPROVED);
        report.setSubmittedBy("quality.inspector");
        report.setSubmittedAt(LocalDateTime.now().minusDays(1));
        report.setReviewedBy("quality.manager");
        report.setReviewedAt(LocalDateTime.now().minusHours(2));
        report.setComments("Rejected due to high critical defect count and failed pH/surface tension tests.");
        
        reportRepository.save(report);
    }
    
    private void createDraftReport() {
        IncomingQualityInspectionReport report = new IncomingQualityInspectionReport();
        
        // Document info
        report.setDocumentNo("AGI-IMS-DEC-L4-13");
        report.setRevision("00");
        report.setEffectiveDate(LocalDate.of(2024, 1, 6));
        report.setReviewedOn(LocalDate.of(2027, 3, 31));
        report.setPage("1 of 2");
        report.setPreparedBy("DGM QC");
        report.setApprovedBy("AVP-QA & SYS");
        report.setIssued("AVP-QA & SYS");
        
        // Report details
        report.setIqcDate(LocalDate.now());
        report.setShift("C/S");
        
        // Product details
        report.setProductVariantName("150 ml Oval Clear");
        report.setProductReceivedFrom("PQR Supplies");
        report.setSupplierShift("A/S");
        report.setProductReceivedDate(LocalDate.now());
        report.setProductReceivedQuantity(45000);
        report.setQuantityAudited(250);
        report.setBatchNumber("0622255");
        
        // Audit results - partial data
        List<Map<String, Object>> auditResults = new ArrayList<>();
        
        Map<String, Object> okResult = new HashMap<>();
        okResult.put("category", "OK");
        okResult.put("count", 180);
        okResult.put("defectName", "Clean bottles");
        auditResults.add(okResult);
        
        Map<String, Object> criticalResult = new HashMap<>();
        criticalResult.put("category", "CRITICAL");
        criticalResult.put("count", 45);
        criticalResult.put("defectName", "Neck crack, chipping");
        auditResults.add(criticalResult);
        
        report.setAuditResults(auditResults);
        
        // Test results - partial data
        List<Map<String, Object>> testResults = new ArrayList<>();
        
        Map<String, Object> surfacePh = new HashMap<>();
        surfacePh.put("testName", "SURFACE pH");
        surfacePh.put("specification", "MIN - 6.0 and MAX - 7.5");
        surfacePh.put("result", "6.8 pH");
        surfacePh.put("checkedBy", "Srikant M");
        surfacePh.put("isPass", true);
        testResults.add(surfacePh);
        
        report.setTestResults(testResults);
        
        // Quality decision - not yet decided
        report.setQualityDecision(null);
        
        // Status
        report.setStatus(IncomingQualityInspectionReport.ReportStatus.DRAFT);
        
        reportRepository.save(report);
    }
}