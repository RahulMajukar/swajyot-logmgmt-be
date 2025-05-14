package com.swajyot.log.service;

import com.swajyot.log.model.IncomingQualityInspectionReport;
import com.swajyot.log.repository.IncomingQualityInspectionReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class IncomingQualityInspectionReportService {

    private final IncomingQualityInspectionReportRepository reportRepository;

    @Autowired
    public IncomingQualityInspectionReportService(IncomingQualityInspectionReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public List<IncomingQualityInspectionReport> getAllReports() {
        return reportRepository.findAll();
    }

    public IncomingQualityInspectionReport getReportById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incoming Quality Inspection Report not found with id: " + id));
    }

    public List<IncomingQualityInspectionReport> getReportsByStatus(IncomingQualityInspectionReport.ReportStatus status) {
        return reportRepository.findByStatus(status);
    }

    public List<IncomingQualityInspectionReport> getReportsBySubmitter(String submitter) {
        return reportRepository.findBySubmittedBy(submitter);
    }

    public List<IncomingQualityInspectionReport> getReportsByReviewer(String reviewer) {
        return reportRepository.findByReviewedBy(reviewer);
    }

    public List<IncomingQualityInspectionReport> getReportsByDateRange(LocalDate startDate, LocalDate endDate) {
        return reportRepository.findByIqcDateBetween(startDate, endDate);
    }
    
    public List<IncomingQualityInspectionReport> getReportsByProductName(String productName) {
        return reportRepository.findByProductVariantName(productName);
    }
    
    public List<IncomingQualityInspectionReport> getReportsBySupplier(String supplier) {
        return reportRepository.findByProductReceivedFrom(supplier);
    }
    
    public List<IncomingQualityInspectionReport> getReportsByBatchNumber(String batchNumber) {
        return reportRepository.findByBatchNumber(batchNumber);
    }
    
    public List<IncomingQualityInspectionReport> getReportsByQualityDecision(String qualityDecision) {
        return reportRepository.findByQualityDecision(qualityDecision);
    }
    
    public List<IncomingQualityInspectionReport> getReportsByReceivedDate(LocalDate receivedDate) {
        return reportRepository.findByProductReceivedDate(receivedDate);
    }

    @Transactional
    public IncomingQualityInspectionReport createReport(IncomingQualityInspectionReport report) {
        // Set initial status to DRAFT
        if (report.getStatus() == null) {
            report.setStatus(IncomingQualityInspectionReport.ReportStatus.DRAFT);
        }
        return reportRepository.save(report);
    }

    @Transactional
    public IncomingQualityInspectionReport updateReport(Long id, IncomingQualityInspectionReport updatedReport) {
        IncomingQualityInspectionReport existingReport = getReportById(id);
        
        // Only allow updates for reports in DRAFT or REJECTED status
//        if (existingReport.getStatus() != IncomingQualityInspectionReport.ReportStatus.DRAFT 
//                && existingReport.getStatus() != IncomingQualityInspectionReport.ReportStatus.REJECTED) {
//            throw new IllegalStateException("Cannot update a report that is already submitted or approved");
//        }
        
        // Preserve the ID
        updatedReport.setId(id);
        
        return reportRepository.save(updatedReport);
    }

    @Transactional
    public IncomingQualityInspectionReport submitReport(Long id, String submittedBy) {
        IncomingQualityInspectionReport report = getReportById(id);
        
        if (report.getStatus() != IncomingQualityInspectionReport.ReportStatus.DRAFT 
                && report.getStatus() != IncomingQualityInspectionReport.ReportStatus.REJECTED) {
            throw new IllegalStateException("Report is already submitted or approved");
        }
        
        report.setStatus(IncomingQualityInspectionReport.ReportStatus.SUBMITTED);
        report.setSubmittedBy(submittedBy);
        report.setSubmittedAt(LocalDateTime.now());
        
        return reportRepository.save(report);
    }

    @Transactional
    public IncomingQualityInspectionReport approveReport(Long id, String reviewedBy, String comments) {
        IncomingQualityInspectionReport report = getReportById(id);
        
        if (report.getStatus() != IncomingQualityInspectionReport.ReportStatus.SUBMITTED) {
            throw new IllegalStateException("Report must be in SUBMITTED status to approve");
        }
        
        report.setStatus(IncomingQualityInspectionReport.ReportStatus.APPROVED);
        report.setReviewedBy(reviewedBy);
        report.setReviewedAt(LocalDateTime.now());
        
        if (comments != null) {
            report.setComments(comments);
        }
        
        return reportRepository.save(report);
    }

    @Transactional
    public IncomingQualityInspectionReport rejectReport(Long id, String reviewedBy, String comments) {
        IncomingQualityInspectionReport report = getReportById(id);
        
        if (report.getStatus() != IncomingQualityInspectionReport.ReportStatus.SUBMITTED) {
            throw new IllegalStateException("Report must be in SUBMITTED status to reject");
        }
        
        report.setStatus(IncomingQualityInspectionReport.ReportStatus.REJECTED);
        report.setReviewedBy(reviewedBy);
        report.setReviewedAt(LocalDateTime.now());
        report.setComments(comments);
        
        return reportRepository.save(report);
    }

    @Transactional
    public void deleteReport(Long id) {
        IncomingQualityInspectionReport report = getReportById(id);
        
        // Only allow deletion of DRAFT or REJECTED reports
        if (report.getStatus() != IncomingQualityInspectionReport.ReportStatus.DRAFT 
                && report.getStatus() != IncomingQualityInspectionReport.ReportStatus.REJECTED) {
            throw new IllegalStateException("Cannot delete a report that is submitted or approved");
        }
        
        reportRepository.deleteById(id);
    }
    
    @Transactional
    public void logPdfDownload(Long id, String userName) {
        // Implementation can be adjusted based on logging requirements
        System.out.println("PDF for Incoming Quality Inspection Report ID: " + id + " downloaded by: " + userName);
    }
    
    // Additional methods for business logic specific to Incoming Quality Inspection Reports
    
    /**
     * Gets reports by quality decision outcome (PASS, FAIL, CONDITIONAL_PASS)
     */
    public List<IncomingQualityInspectionReport> getReportsByQualityOutcome(String outcome) {
        return reportRepository.findByQualityDecision(outcome);
    }
    
    /**
     * Gets reports with critical defects found
     * This method would need additional repository methods or filtering logic
     */
    public List<IncomingQualityInspectionReport> getReportsWithCriticalDefects() {
        // This is a placeholder implementation
        // In a real implementation, you would query reports where audit results
        // contain entries with "CRITICAL" category and count > 0
        return reportRepository.findAll().stream()
                .filter(this::hasCriticalDefects)
                .toList();
    }
    
    /**
     * Helper method to check if a report has critical defects
     */
    private boolean hasCriticalDefects(IncomingQualityInspectionReport report) {
        if (report.getAuditResults() == null) {
            return false;
        }
        
        return report.getAuditResults().stream()
                .anyMatch(result -> {
                    String category = (String) result.get("category");
                    Object countObj = result.get("count");
                    Integer count = null;
                    
                    if (countObj instanceof Integer) {
                        count = (Integer) countObj;
                    } else if (countObj instanceof String) {
                        try {
                            count = Integer.parseInt((String) countObj);
                        } catch (NumberFormatException e) {
                            // Ignore parsing errors
                        }
                    }
                    
                    return "CRITICAL".equals(category) && count != null && count > 0;
                });
    }
    
    /**
     * Calculates batch acceptance rate for a specific time period
     */
    public double calculateBatchAcceptanceRate(LocalDate startDate, LocalDate endDate) {
        List<IncomingQualityInspectionReport> reports = reportRepository.findByIqcDateBetween(startDate, endDate);
        
        if (reports.isEmpty()) {
            return 0.0;
        }
        
        long passedReports = reports.stream()
                .filter(report -> "PASS".equals(report.getQualityDecision()) || 
                                  "CONDITIONAL_PASS".equals(report.getQualityDecision()))
                .count();
        
        return (double) passedReports / reports.size() * 100.0;
    }
}