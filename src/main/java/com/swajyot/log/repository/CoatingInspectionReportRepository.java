package com.swajyot.log.repository;

import com.swajyot.log.model.CoatingInspectionReport;
import com.swajyot.log.model.IncomingQualityInspectionReport;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CoatingInspectionReportRepository extends JpaRepository<CoatingInspectionReport, Long> {
    
    List<CoatingInspectionReport> findByStatus(CoatingInspectionReport.ReportStatus status);
    
    List<CoatingInspectionReport> findBySubmittedBy(String submittedBy);
    
    List<CoatingInspectionReport> findByReviewedBy(String reviewedBy);
    
    List<CoatingInspectionReport> findByInspectionDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<CoatingInspectionReport> findByProduct(String product);
    
    List<CoatingInspectionReport> findByVariant(String variant);
    
    List<CoatingInspectionReport> findByLineNo(String lineNo);
    
//    @Query(value = "SELECT COALESCE(MAX(CAST(SUBSTRING(document_no, LENGTH(?1) + 1) AS INTEGER)), 0) " +
//            "FROM coating_inspection_reports WHERE document_no LIKE CONCAT(?1, '%')", nativeQuery = true)
//     Integer findMaxIdForPrefix(String prefix);
    @Query(value = "SELECT COALESCE(MAX(CAST(SUBSTRING(document_no, LENGTH(?1) + 1) AS INTEGER)), 0) " +
            "FROM coating_inspection_reports " +
            "WHERE document_no LIKE CONCAT(?1, '%') " +
            "AND SUBSTRING(document_no, LENGTH(?1) + 1) ~ '^[0-9]+$'", nativeQuery = true)
Integer findMaxIdForPrefix(String prefix);
    
    List<CoatingInspectionReport> findByDocumentNoStartingWith(String prefix);

}