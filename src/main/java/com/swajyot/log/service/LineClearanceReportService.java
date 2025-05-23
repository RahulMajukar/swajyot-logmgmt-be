package com.swajyot.log.service;

import com.swajyot.log.model.LineClearanceReport;
import com.swajyot.log.repository.LineClearanceReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LineClearanceReportService {

    private final LineClearanceReportRepository lineClearanceReportRepository;

    @Autowired
    public LineClearanceReportService(LineClearanceReportRepository lineClearanceReportRepository) {
        this.lineClearanceReportRepository = lineClearanceReportRepository;
    }

    public List<LineClearanceReport> getAllReports() {
        return lineClearanceReportRepository.findAll();
    }

    public LineClearanceReport getReportById(Long id) {
        return lineClearanceReportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Line Clearance Report not found with id: " + id));
    }

    public List<LineClearanceReport> getReportsByStatus(LineClearanceReport.ReportStatus status) {
        return lineClearanceReportRepository.findByStatus(status);
    }

    public List<LineClearanceReport> getReportsBySubmitter(String submitter) {
        return lineClearanceReportRepository.findBySubmittedBy(submitter);
    }

    public List<LineClearanceReport> getReportsByReviewer(String reviewer) {
        return lineClearanceReportRepository.findByReviewedBy(reviewer);
    }

    public List<LineClearanceReport> getReportsByDateRange(LocalDate startDate, LocalDate endDate) {
        return lineClearanceReportRepository.findByReportDateBetween(startDate, endDate);
    }
    
    public List<LineClearanceReport> getReportsByProductionArea(LineClearanceReport.ProductionArea productionArea) {
        return lineClearanceReportRepository.findByProductionArea(productionArea);
    }
    
    public List<LineClearanceReport> getReportsByLine(String line) {
        return lineClearanceReportRepository.findByLine(line);
    }
    
    public List<LineClearanceReport> getReportsByProductName(String productName) {
        return lineClearanceReportRepository.findByProductName(productName);
    }

    @Transactional
    public LineClearanceReport createReport(LineClearanceReport report) {
        // Set initial status to DRAFT
        if (report.getStatus() == null) {
            report.setStatus(LineClearanceReport.ReportStatus.DRAFT);
        }
        // Generate document number automatically if not provided
        if (report.getDocumentNo() == null || report.getDocumentNo().isEmpty()) {
            report.setDocumentNo(generateDocumentNumber());
        }

        return lineClearanceReportRepository.save(report);
    }

//    @Transactional
//    public LineClearanceReport updateReport(Long id, LineClearanceReport updatedReport) {
//        LineClearanceReport existingReport = getReportById(id);
//        
//        // Only allow updates for reports in DRAFT or REJECTED status
//        if (existingReport.getStatus() != LineClearanceReport.ReportStatus.DRAFT 
//                && existingReport.getStatus() != LineClearanceReport.ReportStatus.REJECTED) {
//            throw new IllegalStateException("Cannot update a report that is already submitted or approved");
//        }
//        
//        // Preserve the ID
//        updatedReport.setId(id);
//        
//        return lineClearanceReportRepository.save(updatedReport);
//    }
    @Transactional
    public LineClearanceReport updateReport(Long id, LineClearanceReport updatedReport) {
        LineClearanceReport existingReport = getReportById(id);
        
        // Remove the status check to allow updates regardless of status
        // Preserve the ID
        updatedReport.setId(id);
        
        // Preserve audit trail information if it exists in the original report
        if (existingReport.getSubmittedBy() != null) {
            updatedReport.setSubmittedBy(existingReport.getSubmittedBy());
            updatedReport.setSubmittedAt(existingReport.getSubmittedAt());
        }
        
        if (existingReport.getReviewedBy() != null) {
            updatedReport.setReviewedBy(existingReport.getReviewedBy());
            updatedReport.setReviewedAt(existingReport.getReviewedAt());
        }
        
        return lineClearanceReportRepository.save(updatedReport);
    }

    @Transactional
    public LineClearanceReport submitReport(Long id, String submittedBy) {
        LineClearanceReport report = getReportById(id);
        
        if (report.getStatus() != LineClearanceReport.ReportStatus.DRAFT 
                && report.getStatus() != LineClearanceReport.ReportStatus.REJECTED) {
            throw new IllegalStateException("Report is already submitted or approved");
        }
        
        report.setStatus(LineClearanceReport.ReportStatus.SUBMITTED);
        report.setSubmittedBy(submittedBy);
        report.setSubmittedAt(LocalDateTime.now());
        
        return lineClearanceReportRepository.save(report);
    }

    @Transactional
    public LineClearanceReport approveReport(Long id, String reviewedBy, String comments) {
        LineClearanceReport report = getReportById(id);
        
        if (report.getStatus() != LineClearanceReport.ReportStatus.SUBMITTED) {
            throw new IllegalStateException("Report must be in SUBMITTED status to approve");
        }
        
        report.setStatus(LineClearanceReport.ReportStatus.APPROVED);
        report.setReviewedBy(reviewedBy);
        report.setReviewedAt(LocalDateTime.now());
        
        if (comments != null) {
            report.setComments(comments);
        }
        
        return lineClearanceReportRepository.save(report);
    }

    @Transactional
    public LineClearanceReport rejectReport(Long id, String reviewedBy, String comments) {
        LineClearanceReport report = getReportById(id);
        
        if (report.getStatus() != LineClearanceReport.ReportStatus.SUBMITTED) {
            throw new IllegalStateException("Report must be in SUBMITTED status to reject");
        }
        
        report.setStatus(LineClearanceReport.ReportStatus.REJECTED);
        report.setReviewedBy(reviewedBy);
        report.setReviewedAt(LocalDateTime.now());
        report.setComments(comments);
        
        return lineClearanceReportRepository.save(report);
    }

    @Transactional
    public void deleteReport(Long id) {
        LineClearanceReport report = getReportById(id);
        
        // Only allow deletion of DRAFT or REJECTED reports
        if (report.getStatus() != LineClearanceReport.ReportStatus.DRAFT 
                && report.getStatus() != LineClearanceReport.ReportStatus.REJECTED) {
            throw new IllegalStateException("Cannot delete a report that is submitted or approved");
        }
        
        lineClearanceReportRepository.deleteById(id);
    }
    
    @Transactional
    public void logPdfDownload(Long id, String userName) {
        // Implement logging logic here if needed
        // This could write to a separate audit log table
        // For now, we'll just log to console
        System.out.println("PDF for Line Clearance Report ID: " + id + " downloaded by: " + userName);
    }
    
    /**     * Generates a unique document number in the format AGI-MS-<Month>-LCR-<id>
     */
//    private String generateUniqueDocumentNumber() {
//        // Get current month in three-letter format (e.g., JAN, FEB)
//        String month = java.time.LocalDate.now().getMonth().toString().substring(0, 3);
//        
//        // Find the highest existing ID for the current month
//        String prefix = "AGI-MS-" + month + "-LCR-";
//        
//        // Query to find max ID with this prefix
//        Integer maxId = lineClearanceReportRepository.findMaxIdForPrefix(prefix);
//        int nextId = (maxId != null) ? maxId + 1 : 1;
//        
//        return prefix + nextId;
//    }
    
    private String generateDocumentNumber() {
        String month = LocalDate.now().getMonth().toString(); // e.g., MAY
        String prefix = "AGI-LCR-" + month + "-";

        // Fetch all reports for current month to determine the next serial number
        List<LineClearanceReport> reportsThisMonth =
        		lineClearanceReportRepository.findByDocumentNoStartingWith(prefix);

        // Find the max serial number used this month
        int maxSerial = reportsThisMonth.stream()
                .map(r -> {
                    String[] parts = r.getDocumentNo().split("-");
                    try {
                        return Integer.parseInt(parts[parts.length - 1]);
                    } catch (Exception e) {
                        return 0; // fallback if parsing fails
                    }
                })
                .max(Integer::compare)
                .orElse(0);

        int nextSerial = maxSerial + 1;
        String paddedSerial = String.format("%03d", nextSerial); // 001, 002, etc.

        return prefix + paddedSerial;
    }
}