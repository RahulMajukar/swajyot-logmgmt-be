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
@Table(name = "coating_inspection_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoatingInspectionReport implements ReportHeader{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String unit = "AGI Speciality Glass Division";
    
    private String scope = "AGI/ DEC/ COATING";
    
    private String title = "FIRST ARTICLE INSPECTION REPORT - COATING";
    
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
    
    private String lineNo;
    
    // Product details
    private String product;
    private String variant;
    private String sizeNo;
    private String customer;
    private String sampleSize;
    
    // Coating details
    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private List<Map<String, Object>> coatingDetails;
    
    // Characteristics
    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private List<Map<String, Object>> characteristics;
    
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
    
    // Batch information    
    private String batchNo;
    private LocalDate expiryDate;
    
    // Color details
    private String colorShade;
    private String colorHeight;
    
    // Test results
    private String visualDefect;
    private String mekTest;
    private String crossCutTest;
    
    // Coating thickness
    private String coatingThicknessBody;
    private String coatingThicknessBottom;
    
    // Environmental conditions
    private String temperature;
    private String viscosity;
    private String batchComposition;
    
    public enum ReportStatus {
        DRAFT, SUBMITTED, APPROVED, REJECTED
    }
}