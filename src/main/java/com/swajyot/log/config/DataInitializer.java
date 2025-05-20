package com.swajyot.log.config;

import com.swajyot.log.model.InspectionForm;
import com.swajyot.log.model.User;
import com.swajyot.log.repository.InspectionFormRepository;
import com.swajyot.log.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final InspectionFormRepository inspectionFormRepository;

    @Bean
    @Profile("!prod") // Only run in non-production environments
    public CommandLineRunner initData() {
        return args -> {
            // Check if data already exists
            if (userRepository.count() > 0) {
                System.out.println("Database already has data, skipping initialization");
                return;
            }

            System.out.println("Initializing sample data...");
            
            // Create sample users
            createUsers();
            
            // Create sample inspection forms
//            createInspectionForms();
            
            System.out.println("Sample data initialization complete!");
        };
    }
    
    private void createUsers() {
        List<User> users = new ArrayList<>();
        
        users.add(new User(null, "operator", "operator123", "John Operator", 
                User.Role.OPERATOR, true, LocalDateTime.now()));
        
        users.add(new User(null, "qa", "qa123", "Mike QA", 
                User.Role.QA, true, LocalDateTime.now()));
        
        users.add(new User(null, "avp", "avp123", "Sarah AVP", 
                User.Role.AVP, true, LocalDateTime.now()));
        
        users.add(new User(null, "master", "master123", "Admin Master", 
                User.Role.MASTER, true, LocalDateTime.now()));
        
        userRepository.saveAll(users);
        System.out.println("Created " + users.size() + " sample users");
    }
    
    private void createInspectionForms() {
        // Create sample coating form
        createCoatingForm();
        
        // Create sample printing form
        createPrintingForm();
        
        System.out.println("Created 2 sample inspection forms");
    }
    
    private void createCoatingForm() {
        // Create the coating form
        InspectionForm form = new InspectionForm();
        form.setDocumentNo("AGI-DEC-14-04");
        form.setIssuanceNo("00");
        form.setIssueDate(LocalDate.of(2024, 8, 1));
        form.setReviewedDate(LocalDate.of(2027, 3, 1));
        form.setPage("1 of 1");
        form.setPreparedBy("QQM QC");
        form.setApprovedBy("AVP-QA & SYS");
        form.setIssued("AVP-QA & SYS");
        form.setInspectionDate(LocalDate.of(2024, 11, 29));
        form.setProduct("100 mL Bag Pke.");
        form.setSizeNo("");
        form.setShift("C");
        form.setVariant("Pink matt");
        form.setLineNo("02");
        form.setCustomer("");
        form.setSampleSize("08 Nos.");
        form.setScope("AGI / DEC / COATING");
        form.setTitle("FIRST ARTICLE INSPECTION REPORT - COATING");
        form.setFormType(InspectionForm.FormType.COATING);
        
        // Add lacquers as tableData
        List<Map<String, Object>> lacquers = new ArrayList<>();
        lacquers.add(createLacquer(1L, "Clear Extn", "11.74", "2634", LocalDate.of(2025, 10, 24)));
        lacquers.add(createLacquer(2L, "Red Dye", "121g", "2137", LocalDate.of(2025, 10, 20)));
        lacquers.add(createLacquer(3L, "Black Dye", "46.7g", "1453", LocalDate.of(2025, 10, 21)));
        lacquers.add(createLacquer(4L, "Pink Dye", "26.5g", "1140", LocalDate.of(2025, 7, 10)));
        lacquers.add(createLacquer(5L, "Violet Dye", "18.7g", "1160", LocalDate.of(2025, 7, 11)));
        lacquers.add(createLacquer(6L, "Matt Bath", "300g", "1156", LocalDate.of(2025, 9, 12)));
        lacquers.add(createLacquer(7L, "Hardener", "60g", "114", LocalDate.of(2025, 11, 20)));
        lacquers.add(createLacquer(8L, "", "", "", null));
        form.setTableData(lacquers);
        
        // Add characteristics
        List<Map<String, Object>> characteristics = new ArrayList<>();
        characteristics.add(createCharacteristic(1L, "Colour Shade", "Shade 2 : OK", null, null, ""));
        characteristics.add(createCharacteristic(2L, "(Colour Height)", "Full", null, null, ""));
        characteristics.add(createCharacteristic(3L, "Any Visual defect", "No", null, null, ""));
        characteristics.add(createCharacteristic(4L, "MEK Test", "OK", null, null, ""));
        characteristics.add(createCharacteristic(5L, "Cross Cut Test (Tape Test)", "OK", null, null, ""));
        characteristics.add(createCharacteristic(6L, "Coating Thickness", null, "20 mic", "10.2 mic", ""));
        characteristics.add(createCharacteristic(7L, "Temperature", "117°c", null, null, ""));
        characteristics.add(createCharacteristic(8L, "Viscosity", "25.1s", null, null, ""));
        characteristics.add(createCharacteristic(9L, "Batch Composition", 
                "Clear Extn 11.74 Red Dye 121g Black Dye 46.7g\nPink Dye 26.5g Violet Dye 18.7g\nMatt Bath H-Agent 60g", 
                null, null, ""));
        form.setCharacteristics(characteristics);
        
        form.setQaExecutive("Mike QA");
        form.setQaSignature("signed_by_mike_qa");
        form.setProductionOperator("John Operator");
        form.setOperatorSignature("signed_by_john_operator");
        form.setFinalApprovalTime("21:30 hrs");
        form.setStatus(InspectionForm.FormStatus.APPROVED);
        form.setSubmittedBy("John Operator");
        form.setSubmittedAt(LocalDateTime.of(2024, 11, 29, 14, 30, 0));
        form.setReviewedBy("Sarah AVP");
        form.setReviewedAt(LocalDateTime.of(2024, 11, 29, 17, 45, 0));
        form.setComments("");
        
        inspectionFormRepository.save(form);
    }
    
    private void createPrintingForm() {
        // Create the printing form
        InspectionForm form = new InspectionForm();
        form.setDocumentNo("AGI-DEC-14-05");
        form.setIssuanceNo("00");
        form.setIssueDate(LocalDate.of(2024, 8, 1));
        form.setReviewedDate(LocalDate.of(2027, 3, 1));
        form.setPage("1 of 1");
        form.setPreparedBy("QQM QC");
        form.setApprovedBy("AVP-QA & SYS");
        form.setIssued("AVP-QA & SYS");
        form.setInspectionDate(LocalDate.of(2024, 11, 30));
        form.setProduct("100 mL Jar");
        form.setSizeNo("R-001");
        form.setShift("B");
        form.setVariant("BLUE HORSE AND TANK");
        form.setLineNo("03");
        form.setMcNo("CNC 03");
        form.setCustomer("Premium Packaging");
        form.setSampleSize("08 Nos.");
        form.setScope("AGI / DEC / PRINTING");
        form.setTitle("FIRST ARTICLE INSPECTION REPORT - PRINTING");
        form.setFormType(InspectionForm.FormType.PRINTING);
        
        // Add inks as tableData
        List<Map<String, Object>> inks = new ArrayList<>();
        inks.add(createInk(1L, "Black Ink", "2635", LocalDate.of(2025, 10, 30)));
        inks.add(createInk(2L, "Blue Ink", "2140", LocalDate.of(2025, 11, 15)));
        inks.add(createInk(3L, "Red Ink", "1455", LocalDate.of(2025, 10, 25)));
        inks.add(createInk(4L, "White Ink", "1157", LocalDate.of(2025, 9, 20)));
        inks.add(createInk(5L, "Yellow Ink", "115", LocalDate.of(2025, 11, 25)));
        inks.add(createInk(6L, "Green Ink", "118", LocalDate.of(2025, 12, 5)));
        inks.add(createInk(7L, "", "", null));
        inks.add(createInk(8L, "", "", null));
        form.setTableData(inks);
        
        // Add characteristics specific to printing
        List<Map<String, Object>> characteristics = new ArrayList<>();
        characteristics.add(createCharacteristic(1L, "Colour Shade", "Shade 1 : OK", null, null, ""));
        
        // Use vertical and horizontal fields for printing position
        Map<String, Object> printingPos = new HashMap<>();
        printingPos.put("id", 2L);
        printingPos.put("name", "Printing Position");
        printingPos.put("vertical", "0.8mm");
        printingPos.put("horizontal", "0.5mm");
        printingPos.put("comments", "Within tolerance");
        characteristics.add(printingPos);
        
        characteristics.add(createCharacteristic(3L, "Deposition of ink", "Excellent", null, null, ""));
        characteristics.add(createCharacteristic(4L, "Marking Sample", "Checked against standard", null, null, ""));
        characteristics.add(createCharacteristic(5L, "Art work / Positive", "Matches specification", null, null, ""));
        characteristics.add(createCharacteristic(6L, "Run File", "RUN-BH-203", null, null, ""));
        characteristics.add(createCharacteristic(7L, "Printing Ink (Type)", "UV Curable", null, null, ""));
        characteristics.add(createCharacteristic(8L, "Any Visual Defect", "None", null, null, ""));
        characteristics.add(createCharacteristic(9L, "Batch Composition", 
                "Black Ink Blue Ink Red Ink White Ink Yellow Ink Green Ink", 
                null, null, ""));
        form.setCharacteristics(characteristics);
        
        form.setQaExecutive("Mike QA");
        form.setQaSignature("signed_by_mike_qa");
        form.setProductionOperator("John Operator");
        form.setOperatorSignature("signed_by_john_operator");
        form.setFinalApprovalTime("18:45 hrs");
        form.setStatus(InspectionForm.FormStatus.SUBMITTED);
        form.setSubmittedBy("John Operator");
        form.setSubmittedAt(LocalDateTime.of(2024, 11, 30, 15, 20, 0));
        form.setComments("");
        
        inspectionFormRepository.save(form);
    }
    
    // Helper methods to create data structures
    
    private Map<String, Object> createLacquer(Long id, String name, String weight, String batchNo, LocalDate expiryDate) {
        Map<String, Object> lacquer = new HashMap<>();
        lacquer.put("id", id);
        lacquer.put("name", name);
        lacquer.put("weight", weight);
        lacquer.put("batchNo", batchNo);
        lacquer.put("expiryDate", expiryDate);
        return lacquer;
    }
    
    private Map<String, Object> createInk(Long id, String name, String batchNo, LocalDate expiryDate) {
        Map<String, Object> ink = new HashMap<>();
        ink.put("id", id);
        ink.put("name", name);
        ink.put("batchNo", batchNo);
        ink.put("expiryDate", expiryDate);
        return ink;
    }
    
    private Map<String, Object> createCharacteristic(Long id, String name, String observation, 
                                              String bodyThickness, String bottomThickness, String comments) {
        Map<String, Object> characteristic = new HashMap<>();
        characteristic.put("id", id);
        characteristic.put("name", name);
        
        if (observation != null) {
            characteristic.put("observation", observation);
        }
        
        if (bodyThickness != null) {
            characteristic.put("bodyThickness", bodyThickness);
        }
        
        if (bottomThickness != null) {
            characteristic.put("bottomThickness", bottomThickness);
        }
        
        characteristic.put("comments", comments);
        return characteristic;
    }
}