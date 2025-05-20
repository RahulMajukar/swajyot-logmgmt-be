//package com.swajyot.log.config;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
////import org.springframework.context.annotation.Profile;
//
//import com.swajyot.log.model.CoatingInspectionReport;
//import com.swajyot.log.repository.CoatingInspectionReportRepository;
//
//@Configuration
////@Profile("dev") // Only run in dev profile
//public class CoatingInspectionReportDataInitializer {
//
//    @Autowired
//    private CoatingInspectionReportRepository repository;
//
//    @Bean
//    public CommandLineRunner initCoatingInspectionData() {
//        return args -> {
//            // Skip if we already have data
//            if (repository.count() > 0) {
//                System.out.println("Coating inspection data already exists, skipping initialization");
//                return;
//            }
//
//            System.out.println("Initializing default coating inspection reports data...");
//            
//            // Create a few sample reports
//            createSampleCoatingReports();
//            
//            System.out.println("Coating inspection data initialization completed");
//        };
//    }
//    
//    private void createSampleCoatingReports() {
//        // Sample 1 - APPROVED report
//        CoatingInspectionReport report1 = new CoatingInspectionReport();
//        report1.setDocumentNo("AGI-IMS-CTF-Apr-21");
//        report1.setRevision("A");
//        report1.setEffectiveDate(LocalDate.of(2025, 3, 15));
//        report1.setReviewedOn(LocalDate.of(2025, 3, 10));
//        report1.setPage("1 of 1");
//        report1.setPreparedBy("John Doe");
//        report1.setApprovedBy("Jane Smith");
//        report1.setIssued("General Issue");
//        report1.setInspectionDate(LocalDate.of(2025, 4, 21));
//        report1.setShift("A");
//        report1.setLineNo("02");
//        report1.setProduct("100 mL Bag Pke.");
//        report1.setVariant("Pink matt");
//        report1.setSizeNo("100 mL");
//        report1.setCustomer("ABC Pharmaceuticals");
//        report1.setSampleSize("05 Nos");
//        report1.setStatus(CoatingInspectionReport.ReportStatus.APPROVED);
//        report1.setSubmittedBy("John Operator");
//        report1.setSubmittedAt(LocalDateTime.now().minusDays(2));
//        report1.setReviewedBy("Sarah QA");
//        report1.setReviewedAt(LocalDateTime.now().minusDays(1));
//        report1.setOperatorName("John Operator");
//        report1.setQaName("Sarah QA");
//        report1.setOperatorSignature("signature.png");
//        report1.setApprovalTime("21:30 Hrs");
//        
//        // Add coating details
//        List<Map<String, Object>> coatingDetails = new ArrayList<>();
//        
//        Map<String, Object> coating1 = new HashMap<>();
//        coating1.put("id", 1);
//        coating1.put("lacquerType", "Clear Matt");
//        coating1.put("batchNo", "11343");
//        coating1.put("quantity", "3050");
//        coating1.put("numberOfPieces", "2034");
//        coating1.put("expiryDate", "24/10/25");
//        coatingDetails.add(coating1);
//        
//        Map<String, Object> coating2 = new HashMap<>();
//        coating2.put("id", 2);
//        coating2.put("lacquerType", "Blue Matt");
//        coating2.put("batchNo", "12170");
//        coating2.put("quantity", "2350");
//        coating2.put("numberOfPieces", "2433");
//        coating2.put("expiryDate", "20/11/25");
//        coatingDetails.add(coating2);
//        
//        Map<String, Object> coating3 = new HashMap<>();
//        coating3.put("id", 3);
//        coating3.put("lacquerType", "Black UV");
//        coating3.put("batchNo", "46790");
//        coating3.put("quantity", "1550");
//        coating3.put("numberOfPieces", "1853");
//        coating3.put("expiryDate", "01/12/25");
//        coatingDetails.add(coating3);
//        
//        report1.setCoatingDetails(coatingDetails);
//        
//        // Add test values
//        report1.setColorShade("Full");
//        report1.setColorHeight("4mm");
//        report1.setVisualDefect("No");
//        report1.setMekTest("OK");
//        report1.setCrossCutTest("OK");
//        report1.setCoatingThicknessBody("25 μm");
//        report1.setCoatingThicknessBottom("10 μm");
//        report1.setTemperature("117°");
//        report1.setViscosity("25.16");
//        report1.setBatchComposition("Ethyl 10% Butyl 30% Methyl 40% Matt plate & Mould Release 20%");
//        
//        repository.save(report1);
//        
//        // Sample 2 - SUBMITTED report
//        CoatingInspectionReport report2 = new CoatingInspectionReport();
//        report2.setDocumentNo("AGI-IMS-CTF-Apr-22");
//        report2.setRevision("A");
//        report2.setEffectiveDate(LocalDate.of(2025, 3, 15));
//        report2.setReviewedOn(LocalDate.of(2025, 3, 10));
//        report2.setPage("1 of 1");
//        report2.setPreparedBy("John Doe");
//        report2.setApprovedBy("Jane Smith");
//        report2.setIssued("General Issue");
//        report2.setInspectionDate(LocalDate.of(2025, 4, 22));
//        report2.setShift("B");
//        report2.setLineNo("01");
//        report2.setProduct("100 mL Jar");
//        report2.setVariant("BLUE HORSE AND TANK");
//        report2.setSizeNo("100 mL");
//        report2.setCustomer("XYZ Pharma");
//        report2.setSampleSize("08 Nos");
//        report2.setStatus(CoatingInspectionReport.ReportStatus.SUBMITTED);
//        report2.setSubmittedBy("Mike Operator");
//        report2.setSubmittedAt(LocalDateTime.now().minusHours(6));
//        report2.setOperatorName("Mike Operator");
//        
//        // Add coating details
//        List<Map<String, Object>> coatingDetails2 = new ArrayList<>();
//        
//        Map<String, Object> coating2_1 = new HashMap<>();
//        coating2_1.put("id", 1);
//        coating2_1.put("lacquerType", "Blue UV");
//        coating2_1.put("batchNo", "22451");
//        coating2_1.put("quantity", "3250");
//        coating2_1.put("numberOfPieces", "3144");
//        coating2_1.put("expiryDate", "15/11/25");
//        coatingDetails2.add(coating2_1);
//        
//        Map<String, Object> coating2_2 = new HashMap<>();
//        coating2_2.put("id", 2);
//        coating2_2.put("lacquerType", "White Base");
//        coating2_2.put("batchNo", "18766");
//        coating2_2.put("quantity", "1800");
//        coating2_2.put("numberOfPieces", "2211");
//        coating2_2.put("expiryDate", "08/10/25");
//        coatingDetails2.add(coating2_2);
//        
//        report2.setCoatingDetails(coatingDetails2);
//        
//        // Add test values
//        report2.setColorShade("Medium");
//        report2.setColorHeight("3.5mm");
//        report2.setVisualDefect("No");
//        report2.setMekTest("OK");
//        report2.setCrossCutTest("OK");
//        report2.setCoatingThicknessBody("22 μm");
//        report2.setCoatingThicknessBottom("9 μm");
//        report2.setTemperature("115°");
//        report2.setViscosity("24.5");
//        report2.setBatchComposition("Ethyl 15% Butyl 25% Methyl 45% Matt plate & Mould Release 15%");
//        
//        repository.save(report2);
//        
//        // Sample 3 - DRAFT report
//        CoatingInspectionReport report3 = new CoatingInspectionReport();
//        report3.setDocumentNo("AGI-IMS-CTF-Apr-23");
//        report3.setRevision("A");
//        report3.setEffectiveDate(LocalDate.of(2025, 3, 15));
//        report3.setReviewedOn(LocalDate.of(2025, 3, 10));
//        report3.setPage("1 of 1");
//        report3.setPreparedBy("John Doe");
//        report3.setApprovedBy("Jane Smith");
//        report3.setIssued("General Issue");
//        report3.setInspectionDate(LocalDate.of(2025, 4, 23));
//        report3.setShift("C");
//        report3.setLineNo("03");
//        report3.setProduct("250 mL Jar");
//        report3.setVariant("Green Matt");
//        report3.setSizeNo("250 mL");
//        report3.setCustomer("PQR Industries");
//        report3.setSampleSize("06 Nos");
//        report3.setStatus(CoatingInspectionReport.ReportStatus.DRAFT);
//        report3.setOperatorName("Lisa Operator");
//        
//        // Add coating details
//        List<Map<String, Object>> coatingDetails3 = new ArrayList<>();
//        
//        Map<String, Object> coating3_1 = new HashMap<>();
//        coating3_1.put("id", 1);
//        coating3_1.put("lacquerType", "Green Matt");
//        coating3_1.put("batchNo", "32178");
//        coating3_1.put("quantity", "2850");
//        coating3_1.put("numberOfPieces", "1980");
//        coating3_1.put("expiryDate", "12/12/25");
//        coatingDetails3.add(coating3_1);
//        
//        report3.setCoatingDetails(coatingDetails3);
//        
//        // Add test values
//        report3.setColorShade("Dark");
//        report3.setColorHeight("4.2mm");
//        report3.setVisualDefect("No");
//        report3.setMekTest("OK");
//        report3.setCrossCutTest("OK");
//        report3.setCoatingThicknessBody("28 μm");
//        report3.setCoatingThicknessBottom("11 μm");
//        report3.setTemperature("118°");
//        report3.setViscosity("26.2");
//        report3.setBatchComposition("Ethyl 12% Butyl 28% Methyl 42% Matt plate & Mould Release 18%");
//        
//        repository.save(report3);
//    }
//}