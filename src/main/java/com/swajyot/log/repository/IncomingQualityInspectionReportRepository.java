package com.swajyot.log.repository;

import com.swajyot.log.model.IncomingQualityInspectionReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IncomingQualityInspectionReportRepository extends JpaRepository<IncomingQualityInspectionReport, Long> {
    
    List<IncomingQualityInspectionReport> findByStatus(IncomingQualityInspectionReport.ReportStatus status);
    
    List<IncomingQualityInspectionReport> findBySubmittedBy(String submitter);
    
    List<IncomingQualityInspectionReport> findByReviewedBy(String reviewer);
    
    List<IncomingQualityInspectionReport> findByIqcDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<IncomingQualityInspectionReport> findByProductVariantName(String productVariantName);
    
    List<IncomingQualityInspectionReport> findByProductReceivedFrom(String supplier);
    
    List<IncomingQualityInspectionReport> findByBatchNumber(String batchNumber);
    
    List<IncomingQualityInspectionReport> findByQualityDecision(String qualityDecision);
    
    List<IncomingQualityInspectionReport> findByProductReceivedDate(LocalDate receivedDate);
    
    List<IncomingQualityInspectionReport> findByDocumentNo(String documentNo);
}