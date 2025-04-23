package com.swajyot.log.repository;

import com.swajyot.log.model.CoatingInspectionReport;
import org.springframework.data.jpa.repository.JpaRepository;
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
    
    // Additional query methods as needed
}