package com.swajyot.log.service;

import com.swajyot.log.model.CoatingInspectionReport;
import com.swajyot.log.repository.CoatingInspectionReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CoatingInspectionReportService {

    private final CoatingInspectionReportRepository coatingInspectionReportRepository;

    @Autowired
    public CoatingInspectionReportService(CoatingInspectionReportRepository coatingInspectionReportRepository) {
        this.coatingInspectionReportRepository = coatingInspectionReportRepository;
    }

    public List<CoatingInspectionReport> getAllReports() {
        return coatingInspectionReportRepository.findAll();
    }

    public CoatingInspectionReport getReportById(Long id) {
        return coatingInspectionReportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coating Inspection Report not found with id: " + id));
    }

    public List<CoatingInspectionReport> getReportsByStatus(CoatingInspectionReport.ReportStatus status) {
        return coatingInspectionReportRepository.findByStatus(status);
    }

    public List<CoatingInspectionReport> getReportsBySubmitter(String submitter) {
        return coatingInspectionReportRepository.findBySubmittedBy(submitter);
    }

    public List<CoatingInspectionReport> getReportsByReviewer(String reviewer) {
        return coatingInspectionReportRepository.findByReviewedBy(reviewer);
    }

    public List<CoatingInspectionReport> getReportsByDateRange(LocalDate startDate, LocalDate endDate) {
        return coatingInspectionReportRepository.findByInspectionDateBetween(startDate, endDate);
    }
    
    public List<CoatingInspectionReport> getReportsByProduct(String product) {
        return coatingInspectionReportRepository.findByProduct(product);
    }
    
    public List<CoatingInspectionReport> getReportsByVariant(String variant) {
        return coatingInspectionReportRepository.findByVariant(variant);
    }
    
    public List<CoatingInspectionReport> getReportsByLineNo(String lineNo) {
        return coatingInspectionReportRepository.findByLineNo(lineNo);
    }

    @Transactional
    public CoatingInspectionReport createReport(CoatingInspectionReport report) {
        // Set initial status to DRAFT
        if (report.getStatus() == null) {
            report.setStatus(CoatingInspectionReport.ReportStatus.DRAFT);
        }
        return coatingInspectionReportRepository.save(report);
    }

    @Transactional
    public CoatingInspectionReport updateReport(Long id, CoatingInspectionReport updatedReport) {
        CoatingInspectionReport existingReport = getReportById(id);
        
        // Only allow updates for reports in DRAFT or REJECTED status
        if (existingReport.getStatus() != CoatingInspectionReport.ReportStatus.DRAFT 
                && existingReport.getStatus() != CoatingInspectionReport.ReportStatus.REJECTED) {
            throw new IllegalStateException("Cannot update a report that is already submitted or approved");
        }
        
        // Preserve the ID
        updatedReport.setId(id);
        
        return coatingInspectionReportRepository.save(updatedReport);
    }

    @Transactional
    public CoatingInspectionReport submitReport(Long id, String submittedBy) {
        CoatingInspectionReport report = getReportById(id);
        
        if (report.getStatus() != CoatingInspectionReport.ReportStatus.DRAFT 
                && report.getStatus() != CoatingInspectionReport.ReportStatus.REJECTED) {
            throw new IllegalStateException("Report is already submitted or approved");
        }
        
        report.setStatus(CoatingInspectionReport.ReportStatus.SUBMITTED);
        report.setSubmittedBy(submittedBy);
        report.setSubmittedAt(LocalDateTime.now());
        
        return coatingInspectionReportRepository.save(report);
    }

    @Transactional
    public CoatingInspectionReport approveReport(Long id, String reviewedBy, String comments) {
        CoatingInspectionReport report = getReportById(id);
        
        if (report.getStatus() != CoatingInspectionReport.ReportStatus.SUBMITTED) {
            throw new IllegalStateException("Report must be in SUBMITTED status to approve");
        }
        
        report.setStatus(CoatingInspectionReport.ReportStatus.APPROVED);
        report.setReviewedBy(reviewedBy);
        report.setReviewedAt(LocalDateTime.now());
        
        if (comments != null) {
            report.setComments(comments);
        }
        
        return coatingInspectionReportRepository.save(report);
    }

    @Transactional
    public CoatingInspectionReport rejectReport(Long id, String reviewedBy, String comments) {
        CoatingInspectionReport report = getReportById(id);
        
        if (report.getStatus() != CoatingInspectionReport.ReportStatus.SUBMITTED) {
            throw new IllegalStateException("Report must be in SUBMITTED status to reject");
        }
        
        report.setStatus(CoatingInspectionReport.ReportStatus.REJECTED);
        report.setReviewedBy(reviewedBy);
        report.setReviewedAt(LocalDateTime.now());
        report.setComments(comments);
        
        return coatingInspectionReportRepository.save(report);
    }

    @Transactional
    public void deleteReport(Long id) {
        CoatingInspectionReport report = getReportById(id);
        
        // Only allow deletion of DRAFT or REJECTED reports
        if (report.getStatus() != CoatingInspectionReport.ReportStatus.DRAFT 
                && report.getStatus() != CoatingInspectionReport.ReportStatus.REJECTED) {
            throw new IllegalStateException("Cannot delete a report that is submitted or approved");
        }
        
        coatingInspectionReportRepository.deleteById(id);
    }
    
    @Transactional
    public void logPdfDownload(Long id, String userName) {
        // Implement logging logic here if needed
        // This could write to a separate audit log table
        // For now, we'll just log to console
        System.out.println("PDF for Coating Inspection Report ID: " + id + " downloaded by: " + userName);
    }
}