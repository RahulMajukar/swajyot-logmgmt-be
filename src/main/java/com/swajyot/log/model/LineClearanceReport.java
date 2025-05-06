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
@Table(name = "line_clearance_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LineClearanceReport  implements ReportHeader{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String unit = "AGI Speciality Glass Division";
    
    private String scope= "AGI/ DEC/ COATING & PRINTING";
    
    private String title = "LINE CLEARANCE REPORT";
    
    //AGI-IMS-DEC-L4-21			
    @Column(nullable = false, unique = true)
    private String documentNo;
   
    private String revision;
    
    private LocalDate effectiveDate;
    
    private LocalDate reviewedOn;
    
    private String page;
    
    private String preparedBy;
    
    private String approvedBy;
    
    private String issued;
    
    private LocalDate reportDate;
    
    private String shift;
    
    private String line;
    
    // Product details
    private String productName;
    private String existingVariantDescription;
    private String newVariantDescription;
    
    private String existingVariantName;
    private String newVariantName;
    
    private LocalDateTime existingVariantStopTime;
    private LocalDateTime newVariantStartTime;
    
    // Check points and their status
    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private List<Map<String, Object>> checkPoints;
    
    // Signatures
    private String responsibleName;
    private String responsibleSignature;
    
    private String productionName;
    private String productionSignature;
    
    private String qualityName;
    private String qualitySignature;
    
    @Enumerated(EnumType.STRING)
    private ReportStatus status;
    
    private String submittedBy;
    
    private LocalDateTime submittedAt;
    
    private String reviewedBy;
    
    private LocalDateTime reviewedAt;
    
    private String comments;
    
    @Enumerated(EnumType.STRING)
    private ProductionArea productionArea;
    
    public enum ProductionArea {
        COATING, PRINTING, BOTH
    }
    
    public enum ReportStatus {
        DRAFT, SUBMITTED, APPROVED, REJECTED
    }
}