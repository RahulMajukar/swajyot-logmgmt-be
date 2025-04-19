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
@Table(name = "incoming_quality_inspection_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncomingQualityInspectionReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String unit = "AGI Speciality Glas Division";
    
    private String scope = "AGI / DEC / IQC";
    
    private String title = "INCOMING QUALITY INSPECTION REPORT";
    
    @Column(nullable = false, unique = true)
    private String documentNo;
    
    private String revision;
    
    private LocalDate effectiveDate;
    
    private LocalDate reviewedOn;
    
    private String page;
    
    private String preparedBy;
    
    private String approvedBy;
    
    private String issued;
    
    private LocalDate iqcDate;
    
    private String shift;
    
    // Product details
    private String productVariantName;
    
    private String productReceivedFrom;
    
    private String supplierShift;
    
    private LocalDate productReceivedDate;
    
    private Integer productReceivedQuantity;
    
    private Integer quantityAudited;
    
    private String batchNumber;
    
    // Audit results
    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private List<Map<String, Object>> auditResults;
    
    // Test results
    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private List<Map<String, Object>> testResults;
    
    // Quality decision
    private String qualityDecision;
    
    // Signature
    private String qualityManagerName;
    private String qualityManagerSignature;
    private LocalDate signatureDate;
    
    // Status tracking
    @Enumerated(EnumType.STRING)
    private ReportStatus status;
    
    private String submittedBy;
    
    private LocalDateTime submittedAt;
    
    private String reviewedBy;
    
    private LocalDateTime reviewedAt;
    
    private String comments;
    
    public enum ReportStatus {
        DRAFT, SUBMITTED, APPROVED, REJECTED
    }
    
    // Helper enums for categorization
    public enum DefectCategory {
        OK, CRITICAL, MAJOR_A, MAJOR_B, MINOR
    }
    
    public enum QualityDecision {
        PASS, FAIL, CONDITIONAL_PASS
    }
    
    /**
     * Structure for an audit result item:
     * {
     *   "category": "OK",
     *   "count": 61,
     *   "defectName": "Inside dust, Neck crack, Neck chipping, Body crack"
     * }
     */
    
    /**
     * Structure for a test result item:
     * {
     *   "testName": "SURFACE pH",
     *   "specification": "MIN - 6.0 and MAX - 7.5",
     *   "result": "7.1 pH",
     *   "checkedBy": "Srikant M",
     *   "isPass": true
     * }
     */
}