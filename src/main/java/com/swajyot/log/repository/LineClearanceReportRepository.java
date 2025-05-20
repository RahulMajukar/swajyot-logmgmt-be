package com.swajyot.log.repository;

import com.swajyot.log.model.LineClearanceReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LineClearanceReportRepository extends JpaRepository<LineClearanceReport, Long> {
    
    List<LineClearanceReport> findByStatus(LineClearanceReport.ReportStatus status);
    
    List<LineClearanceReport> findBySubmittedBy(String submitter);
    
    List<LineClearanceReport> findByReviewedBy(String reviewer);
    
    List<LineClearanceReport> findByReportDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<LineClearanceReport> findByProductionArea(LineClearanceReport.ProductionArea productionArea);
    
    List<LineClearanceReport> findByLine(String line);
    
    List<LineClearanceReport> findByProductName(String productName);
    
    List<LineClearanceReport> findByDocumentNo(String documentNo);
    
    @Query(value = "SELECT COALESCE(MAX(CAST(SUBSTRING(document_no, LENGTH(?1) + 1) AS INTEGER)), 0) " +
            "FROM line_clearance_reports WHERE document_no LIKE CONCAT(?1, '%')", nativeQuery = true)
     Integer findMaxIdForPrefix(String prefix);
    
    List<LineClearanceReport> findByDocumentNoStartingWith(String prefix);
}