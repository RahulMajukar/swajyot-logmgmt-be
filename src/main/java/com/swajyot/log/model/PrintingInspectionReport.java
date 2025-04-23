package com.swajyot.log.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.Type;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "printing_inspection_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrintingInspectionReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String unit = "AGI Speciality Glass Division";
    
    private String scope = "AGI/ DEC/ PRINTING";
    
    private String title = "FIRST ARTICLE INSPECTION REPORT - PRINTING";
    
    @Column(nullable = false, unique = true)
    private String documentNo;
   
    private String revision;
    
    private LocalDate effectiveDate;
    
    private LocalDate reviewedOn;
    
    private String page;
    
    private String preparedBy;
    
    private String approvedBy;
    
    private String issued;
    
    private LocalDate inspectionDate;
    
    private String shift;
    
    private String machineNo;
    
    // Product details
    private String product;
    private String variant;
    private String lineNo;
    private String customer;
    private String sizeNo;
    private String sampleSize;
    
    // Printing details (inks)
    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private List<Map<String, Object>> printingDetails;
    
    // Characteristics/test results
    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private List<Map<String, Object>> characteristics;
    
    // Printing position measurements
    private String verticalPosition;
    private String horizontalPosition;
    
    // Approval information
    private String operatorName;
    private String operatorSignature;
    
    private String qaName;
    private String qaSignature;
    
    private String approvalTime;
    
    @Enumerated(EnumType.STRING)
    private ReportStatus status;
    
    private String submittedBy;
    
    private LocalDateTime submittedAt;
    
    private String reviewedBy;
    
    private LocalDateTime reviewedAt;
    
    private String comments;
    
    // Test results
    private String colorShade;
    private String depositionOfInk;
    private String markingSample;
    private String artworkPositive;
    private String barFile;
    private String printingInkType;
    private String visualDefect;
    private String batchComposition;
    
    public enum ReportStatus {
        DRAFT, SUBMITTED, APPROVED, REJECTED
    }
}