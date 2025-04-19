package com.swajyot.log.repository;

import com.swajyot.log.model.LineClearanceReport;
import org.springframework.data.jpa.repository.JpaRepository;
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
}