package com.swajyot.log.config;

import com.swajyot.log.model.Characteristic;
import com.swajyot.log.model.InspectionForm;
import com.swajyot.log.model.Lacquer;
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
import java.util.ArrayList;
import java.util.List;

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
            createInspectionForms();
            
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
        // Create the first sample form
        InspectionForm form1 = new InspectionForm();
        form1.setDocumentNo("AGI-DEC-14-04");
        form1.setIssuanceNo("00");
        form1.setIssueDate(LocalDate.of(2024, 8, 1));
        form1.setReviewedDate(LocalDate.of(2027, 3, 1));
        form1.setPage("1 of 1");
        form1.setPreparedBy("QQM QC");
        form1.setApprovedBy("AVP-QA & SYS");
        form1.setIssued("AVP-QA & SYS");
        form1.setInspectionDate(LocalDate.of(2024, 11, 29));
        form1.setProduct("100 mL Bag Pke.");
        form1.setSizeNo("");
        form1.setShift("C");
        form1.setVariant("Pink matt");
        form1.setLineNo("02");
        form1.setCustomer("");
        form1.setSampleSize("08 Nos.");
        
        // Add lacquers
        List<Lacquer> lacquers1 = new ArrayList<>();
        lacquers1.add(new Lacquer(1L, "Clear Extn", "11.74", "2634", LocalDate.of(2025, 10, 24)));
        lacquers1.add(new Lacquer(2L, "Red Dye", "121g", "2137", LocalDate.of(2025, 10, 20)));
        lacquers1.add(new Lacquer(3L, "Black Dye", "46.7g", "1453", LocalDate.of(2025, 10, 21)));
        lacquers1.add(new Lacquer(4L, "Pink Dye", "26.5g", "1140", LocalDate.of(2025, 7, 10)));
        lacquers1.add(new Lacquer(5L, "Violet Dye", "18.7g", "1160", LocalDate.of(2025, 7, 11)));
        lacquers1.add(new Lacquer(6L, "Matt Bath", "300g", "1156", LocalDate.of(2025, 9, 12)));
        lacquers1.add(new Lacquer(7L, "Hardener", "60g", "114", LocalDate.of(2025, 11, 20)));
        lacquers1.add(new Lacquer(8L, "", "", "", null));
        form1.setLacquers(lacquers1);
        
        // Add characteristics
        List<Characteristic> characteristics1 = new ArrayList<>();
        characteristics1.add(new Characteristic(1L, "Colour Shade", "Shade 2 : OK", null, null, ""));
        characteristics1.add(new Characteristic(2L, "(Colour Height)", "Full", null, null, ""));
        characteristics1.add(new Characteristic(3L, "Any Visual defect", "No", null, null, ""));
        characteristics1.add(new Characteristic(4L, "MEK Test", "OK", null, null, ""));
        characteristics1.add(new Characteristic(5L, "Cross Cut Test (Tape Test)", "OK", null, null, ""));
        characteristics1.add(new Characteristic(6L, "Coating Thickness", null, "20 mic", "10.2 mic", ""));
        characteristics1.add(new Characteristic(7L, "Temperature", "117°c", null, null, ""));
        characteristics1.add(new Characteristic(8L, "Viscosity", "25.1s", null, null, ""));
        characteristics1.add(new Characteristic(9L, "Batch Composition", 
                "Clear Extn 11.74 Red Dye 121g Black Dye 46.7g\nPink Dye 26.5g Violet Dye 18.7g\nMatt Bath H-Agent 60g", 
                null, null, ""));
        form1.setCharacteristics(characteristics1);
        
        form1.setQaExecutive("Mike QA");
        form1.setQaSignature("signed_by_mike_qa");
        form1.setProductionOperator("John Operator");
        form1.setOperatorSignature("signed_by_john_operator");
        form1.setFinalApprovalTime("21:30 hrs");
        form1.setStatus(InspectionForm.FormStatus.APPROVED);
        form1.setSubmittedBy("John Operator");
        form1.setSubmittedAt(LocalDateTime.of(2024, 11, 29, 14, 30, 0));
        form1.setReviewedBy("Sarah AVP");
        form1.setReviewedAt(LocalDateTime.of(2024, 11, 29, 17, 45, 0));
        form1.setComments("");
        
        // Create a second sample form that's still in submitted state
        InspectionForm form2 = new InspectionForm();
        form2.setDocumentNo("AGI-DEC-14-05");
        form2.setIssuanceNo("00");
        form2.setIssueDate(LocalDate.of(2024, 8, 1));
        form2.setReviewedDate(LocalDate.of(2027, 3, 1));
        form2.setPage("1 of 1");
        form2.setPreparedBy("QQM QC");
        form2.setApprovedBy("AVP-QA & SYS");
        form2.setIssued("AVP-QA & SYS");
        form2.setInspectionDate(LocalDate.of(2024, 11, 30));
        form2.setProduct("200 mL Bottle");
        form2.setSizeNo("");
        form2.setShift("B");
        form2.setVariant("Blue matt");
        form2.setLineNo("01");
        form2.setCustomer("");
        form2.setSampleSize("08 Nos.");
        
        // Add lacquers
        List<Lacquer> lacquers2 = new ArrayList<>();
        lacquers2.add(new Lacquer(1L, "Clear Extn", "12.5", "2635", LocalDate.of(2025, 10, 30)));
        lacquers2.add(new Lacquer(2L, "Blue Dye", "95g", "2140", LocalDate.of(2025, 11, 15)));
        lacquers2.add(new Lacquer(3L, "Black Dye", "38.3g", "1455", LocalDate.of(2025, 10, 25)));
        lacquers2.add(new Lacquer(4L, "Matt Bath", "320g", "1157", LocalDate.of(2025, 9, 20)));
        lacquers2.add(new Lacquer(5L, "Hardener", "64g", "115", LocalDate.of(2025, 11, 25)));
        form2.setLacquers(lacquers2);
        
        // Add characteristics
        List<Characteristic> characteristics2 = new ArrayList<>();
        characteristics2.add(new Characteristic(1L, "Colour Shade", "Shade 1 : OK", null, null, ""));
        characteristics2.add(new Characteristic(2L, "(Colour Height)", "Full", null, null, ""));
        characteristics2.add(new Characteristic(3L, "Any Visual defect", "No", null, null, ""));
        characteristics2.add(new Characteristic(4L, "MEK Test", "OK", null, null, ""));
        characteristics2.add(new Characteristic(5L, "Cross Cut Test (Tape Test)", "OK", null, null, ""));
        characteristics2.add(new Characteristic(6L, "Coating Thickness", null, "18 mic", "9.8 mic", ""));
        characteristics2.add(new Characteristic(7L, "Temperature", "115°c", null, null, ""));
        characteristics2.add(new Characteristic(8L, "Viscosity", "24.5s", null, null, ""));
        characteristics2.add(new Characteristic(9L, "Batch Composition", 
                "Clear Extn 12.5 Blue Dye 95g Black Dye 38.3g\nMatt Bath 320g Hardener 64g", 
                null, null, ""));
        form2.setCharacteristics(characteristics2);
        
        form2.setQaExecutive("Mike QA");
        form2.setQaSignature("signed_by_mike_qa");
        form2.setProductionOperator("John Operator");
        form2.setOperatorSignature("signed_by_john_operator");
        form2.setFinalApprovalTime("18:45 hrs");
        form2.setStatus(InspectionForm.FormStatus.SUBMITTED);
        form2.setSubmittedBy("John Operator");
        form2.setSubmittedAt(LocalDateTime.of(2024, 11, 30, 15, 20, 0));
        
        // Save forms to the database
        inspectionFormRepository.save(form1);
        inspectionFormRepository.save(form2);
        
        System.out.println("Created 2 sample inspection forms");
    }
}