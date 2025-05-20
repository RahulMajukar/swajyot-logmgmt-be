package com.swajyot.log.repository;

import com.swajyot.log.model.PrintingInspectionReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PrintingInspectionReportRepository extends JpaRepository<PrintingInspectionReport, Long> {
    
    List<PrintingInspectionReport> findByStatus(PrintingInspectionReport.ReportStatus status);
    
    List<PrintingInspectionReport> findBySubmittedBy(String submittedBy);
    
    List<PrintingInspectionReport> findByReviewedBy(String reviewedBy);
    
    List<PrintingInspectionReport> findByInspectionDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<PrintingInspectionReport> findByProduct(String product);
    
    List<PrintingInspectionReport> findByVariant(String variant);
    
    List<PrintingInspectionReport> findByLineNo(String lineNo);
    
    List<PrintingInspectionReport> findByMachineNo(String machineNo);
    
    List<PrintingInspectionReport> findByDocumentNoStartingWith(String prefix);
}