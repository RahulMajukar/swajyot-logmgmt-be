package com.swajyot.log.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.Type;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;

@Entity
@Table(name = "inspection_forms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InspectionForm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String documentNo;
    
    private String issuanceNo;
    
    private LocalDate issueDate;
    
    private LocalDate reviewedDate;
    
    private String page;
    
    private String preparedBy;
    
    private String approvedBy;
    
    private String issued;
    
    private LocalDate inspectionDate;
    
    private String product;
    
    private String sizeNo;
    
    private String shift;
    
    private String variant;
    
    private String lineNo;
    
    private String customer;
    
    private String sampleSize;
    
    // Add MC No field for printing forms
    private String mcNo;
    
    // Add scope and title fields
    private String scope;
    private String title;
    
    // Generic JSON storage for table data
    // This can store both lacquers and inks arrays
    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private List<Map<String, Object>> tableData;
    
    // Generic JSON storage for characteristics
    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private List<Map<String, Object>> characteristics;
    
    private String qaExecutive;
    
    private String qaSignature;
    
    private String productionOperator;
    
    private String operatorSignature;
    
    private String finalApprovalTime;
    
    @Enumerated(EnumType.STRING)
    private FormStatus status;
    
    private String submittedBy;
    
    private LocalDateTime submittedAt;
    
    private String reviewedBy;
    
    private LocalDateTime reviewedAt;
    
    private String comments;
    
    // Field to indicate form type: COATING or PRINTING
    @Enumerated(EnumType.STRING)
    private FormType formType;
    
    public enum FormType {
        COATING, PRINTING
    }
    
    public enum FormStatus {
        DRAFT, SUBMITTED, APPROVED, REJECTED
    }
}