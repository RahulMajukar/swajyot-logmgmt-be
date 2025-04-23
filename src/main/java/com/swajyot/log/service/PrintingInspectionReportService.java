package com.swajyot.log.service;

import com.swajyot.log.model.PrintingInspectionReport;
import com.swajyot.log.repository.PrintingInspectionReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PrintingInspectionReportService {

    private final PrintingInspectionReportRepository printingInspectionReportRepository;

    @Autowired
    public PrintingInspectionReportService(PrintingInspectionReportRepository printingInspectionReportRepository) {
        this.printingInspectionReportRepository = printingInspectionReportRepository;
    }

    public List<PrintingInspectionReport> getAllReports() {
        return printingInspectionReportRepository.findAll();
    }

    public PrintingInspectionReport getReportById(Long id) {
        return printingInspectionReportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Printing Inspection Report not found with id: " + id));
    }

    public List<PrintingInspectionReport> getReportsByStatus(PrintingInspectionReport.ReportStatus status) {
        return printingInspectionReportRepository.findByStatus(status);
    }

    public List<PrintingInspectionReport> getReportsBySubmitter(String submitter) {
        return printingInspectionReportRepository.findBySubmittedBy(submitter);
    }

    public List<PrintingInspectionReport> getReportsByReviewer(String reviewer) {
        return printingInspectionReportRepository.findByReviewedBy(reviewer);
    }

    public List<PrintingInspectionReport> getReportsByDateRange(LocalDate startDate, LocalDate endDate) {
        return printingInspectionReportRepository.findByInspectionDateBetween(startDate, endDate);
    }
    
    public List<PrintingInspectionReport> getReportsByProduct(String product) {
        return printingInspectionReportRepository.findByProduct(product);
    }
    
    public List<PrintingInspectionReport> getReportsByVariant(String variant) {
        return printingInspectionReportRepository.findByVariant(variant);
    }
    
    public List<PrintingInspectionReport> getReportsByLineNo(String lineNo) {
        return printingInspectionReportRepository.findByLineNo(lineNo);
    }
    
    public List<PrintingInspectionReport> getReportsByMachineNo(String machineNo) {
        return printingInspectionReportRepository.findByMachineNo(machineNo);
    }

    @Transactional
    public PrintingInspectionReport createReport(PrintingInspectionReport report) {
        // Set initial status to DRAFT
        if (report.getStatus() == null) {
            report.setStatus(PrintingInspectionReport.ReportStatus.DRAFT);
        }
        return printingInspectionReportRepository.save(report);
    }

    @Transactional
    public PrintingInspectionReport updateReport(Long id, PrintingInspectionReport updatedReport) {
        PrintingInspectionReport existingReport = getReportById(id);
        
        // Only allow updates for reports in DRAFT or REJECTED status
        if (existingReport.getStatus() != PrintingInspectionReport.ReportStatus.DRAFT 
                && existingReport.getStatus() != PrintingInspectionReport.ReportStatus.REJECTED) {
            throw new IllegalStateException("Cannot update a report that is already submitted or approved");
        }
        
        // Preserve the ID
        updatedReport.setId(id);
        
        return printingInspectionReportRepository.save(updatedReport);
    }

    @Transactional
    public PrintingInspectionReport submitReport(Long id, String submittedBy) {
        PrintingInspectionReport report = getReportById(id);
        
        if (report.getStatus() != PrintingInspectionReport.ReportStatus.DRAFT 
                && report.getStatus() != PrintingInspectionReport.ReportStatus.REJECTED) {
            throw new IllegalStateException("Report is already submitted or approved");
        }
        
        report.setStatus(PrintingInspectionReport.ReportStatus.SUBMITTED);
        report.setSubmittedBy(submittedBy);
        report.setSubmittedAt(LocalDateTime.now());
        
        return printingInspectionReportRepository.save(report);
    }

    @Transactional
    public PrintingInspectionReport approveReport(Long id, String reviewedBy, String comments) {
        PrintingInspectionReport report = getReportById(id);
        
        if (report.getStatus() != PrintingInspectionReport.ReportStatus.SUBMITTED) {
            throw new IllegalStateException("Report must be in SUBMITTED status to approve");
        }
        
        report.setStatus(PrintingInspectionReport.ReportStatus.APPROVED);
        report.setReviewedBy(reviewedBy);
        report.setReviewedAt(LocalDateTime.now());
        
        if (comments != null) {
            report.setComments(comments);
        }
        
        return printingInspectionReportRepository.save(report);
    }

    @Transactional
    public PrintingInspectionReport rejectReport(Long id, String reviewedBy, String comments) {
        PrintingInspectionReport report = getReportById(id);
        
        if (report.getStatus() != PrintingInspectionReport.ReportStatus.SUBMITTED) {
            throw new IllegalStateException("Report must be in SUBMITTED status to reject");
        }
        
        report.setStatus(PrintingInspectionReport.ReportStatus.REJECTED);
        report.setReviewedBy(reviewedBy);
        report.setReviewedAt(LocalDateTime.now());
        report.setComments(comments);
        
        return printingInspectionReportRepository.save(report);
    }

    @Transactional
    public void deleteReport(Long id) {
        PrintingInspectionReport report = getReportById(id);
        
        // Only allow deletion of DRAFT or REJECTED reports
        if (report.getStatus() != PrintingInspectionReport.ReportStatus.DRAFT 
                && report.getStatus() != PrintingInspectionReport.ReportStatus.REJECTED) {
            throw new IllegalStateException("Cannot delete a report that is submitted or approved");
        }
        
        printingInspectionReportRepository.deleteById(id);
    }
    
    @Transactional
    public void logPdfDownload(Long id, String userName) {
        // Implement logging logic here if needed
        System.out.println("PDF for Printing Inspection Report ID: " + id + " downloaded by: " + userName);
    }
}