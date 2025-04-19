package com.swajyot.log.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.swajyot.log.model.IncomingQualityInspectionReport;
import com.swajyot.log.model.req.EmailRequest;
import com.swajyot.log.service.EmailService;
import com.swajyot.log.service.IncomingQualityInspectionReportPdfService;
import com.swajyot.log.service.IncomingQualityInspectionReportService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/incoming-quality-reports")
public class IncomingQualityInspectionReportController {

    private final IncomingQualityInspectionReportService reportService;
    
    @Autowired
    private IncomingQualityInspectionReportPdfService pdfService;
    
    @Autowired
    private EmailService emailService;

    @GetMapping
    public ResponseEntity<List<IncomingQualityInspectionReport>> getAllReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncomingQualityInspectionReport> getReportById(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.getReportById(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<IncomingQualityInspectionReport>> getReportsByStatus(@PathVariable String status) {
        try {
            IncomingQualityInspectionReport.ReportStatus reportStatus = 
                    IncomingQualityInspectionReport.ReportStatus.valueOf(status.toUpperCase());
            return ResponseEntity.ok(reportService.getReportsByStatus(reportStatus));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/submitter/{submitter}")
    public ResponseEntity<List<IncomingQualityInspectionReport>> getReportsBySubmitter(@PathVariable String submitter) {
        return ResponseEntity.ok(reportService.getReportsBySubmitter(submitter));
    }

    @GetMapping("/reviewer/{reviewer}")
    public ResponseEntity<List<IncomingQualityInspectionReport>> getReportsByReviewer(@PathVariable String reviewer) {
        return ResponseEntity.ok(reportService.getReportsByReviewer(reviewer));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<IncomingQualityInspectionReport>> getReportsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.getReportsByDateRange(startDate, endDate));
    }
    
    @GetMapping("/product/{productName}")
    public ResponseEntity<List<IncomingQualityInspectionReport>> getReportsByProductName(@PathVariable String productName) {
        return ResponseEntity.ok(reportService.getReportsByProductName(productName));
    }
    
    @GetMapping("/supplier/{supplier}")
    public ResponseEntity<List<IncomingQualityInspectionReport>> getReportsBySupplier(@PathVariable String supplier) {
        return ResponseEntity.ok(reportService.getReportsBySupplier(supplier));
    }
    
    @GetMapping("/batch/{batchNumber}")
    public ResponseEntity<List<IncomingQualityInspectionReport>> getReportsByBatchNumber(@PathVariable String batchNumber) {
        return ResponseEntity.ok(reportService.getReportsByBatchNumber(batchNumber));
    }
    
    @GetMapping("/quality-decision/{decision}")
    public ResponseEntity<List<IncomingQualityInspectionReport>> getReportsByQualityDecision(@PathVariable String decision) {
        return ResponseEntity.ok(reportService.getReportsByQualityDecision(decision));
    }
    
    @GetMapping("/received-date")
    public ResponseEntity<List<IncomingQualityInspectionReport>> getReportsByReceivedDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate receivedDate) {
        return ResponseEntity.ok(reportService.getReportsByReceivedDate(receivedDate));
    }

    @PostMapping
    public ResponseEntity<IncomingQualityInspectionReport> createReport(@RequestBody IncomingQualityInspectionReport report) {
        return new ResponseEntity<>(reportService.createReport(report), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IncomingQualityInspectionReport> updateReport(@PathVariable Long id, @RequestBody IncomingQualityInspectionReport report) {
        return ResponseEntity.ok(reportService.updateReport(id, report));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<IncomingQualityInspectionReport> submitReport(@PathVariable Long id, @RequestParam String submittedBy) {
        return ResponseEntity.ok(reportService.submitReport(id, submittedBy));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<IncomingQualityInspectionReport> approveReport(
            @PathVariable Long id,
            @RequestParam String reviewedBy,
            @RequestParam(required = false) String comments) {
        return ResponseEntity.ok(reportService.approveReport(id, reviewedBy, comments));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<IncomingQualityInspectionReport> rejectReport(
            @PathVariable Long id,
            @RequestParam String reviewedBy,
            @RequestParam String comments) {
        return ResponseEntity.ok(reportService.rejectReport(id, reviewedBy, comments));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Endpoint to generate a PDF of the incoming quality inspection report
     * @param id The ID of the report
     * @param userName Optional parameter for tracking who downloaded the PDF
     * @return The PDF as a byte array
     */
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generatePdf(
            @PathVariable Long id, 
            @RequestParam(required = false) String userName) {
        try {
            // Get the report by ID
            IncomingQualityInspectionReport report = reportService.getReportById(id);
            
            // Log the download activity if userName is provided
            if (userName != null && !userName.isEmpty()) {
                reportService.logPdfDownload(id, userName);
            }
            
            // Generate the PDF
            byte[] pdfBytes = pdfService.generatePdf(report, userName);
            
            // Set up response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "iqc_report_" + report.getDocumentNo() + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Endpoint to send an email with the report PDF attachment
     * @param id The ID of the report
     * @param emailRequest The email details (to, subject, body)
     * @return Success or error message
     */
    @PostMapping("/{id}/email-pdf")
    public ResponseEntity<Object> emailPdf(
            @PathVariable Long id,
            @RequestBody EmailRequest emailRequest) {
        
        try {
            // Get the report by ID
            IncomingQualityInspectionReport report = reportService.getReportById(id);
            
            // Generate the PDF
            byte[] pdfBytes = pdfService.generatePdf(report, "");
            
            // Send email with PDF attachment
            emailService.sendEmailWithAttachment(
                emailRequest.getTo(),
                emailRequest.getSubject(),
                emailRequest.getBody(),
                pdfBytes,
                "iqc_report_" + report.getDocumentNo() + ".pdf"
            );
            
            return ResponseEntity.ok(Map.of("message", "Email sent successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to send email: " + e.getMessage()));
        }
    }
    
    /**
     * Additional endpoint to generate a PDF of the report with userName in path
     * @param id The ID of the report
     * @param userName The name of the user downloading the PDF
     * @return The PDF as a byte array
     */
    @GetMapping("/{id}/pdf/{userName}")
    public ResponseEntity<byte[]> generatePdfWithUser(
            @PathVariable Long id, 
            @PathVariable String userName) {
        try {
            // Get the report by ID
            IncomingQualityInspectionReport report = reportService.getReportById(id);
            
            // Log the download activity with the userName
            if (userName != null && !userName.isEmpty()) {
                reportService.logPdfDownload(id, userName);
            }
            
            // Generate the PDF
            byte[] pdfBytes = pdfService.generatePdf(report, userName);
            
            // Set up response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "iqc_report_" + report.getDocumentNo() + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Endpoint to send an email with the report PDF attachment with userName in path
     * @param id The ID of the report
     * @param userName The name of the user sending the email
     * @param emailRequest The email details (to, subject, body)
     * @return Success or error message
     */
    @PostMapping("/{id}/email-pdf/{userName}")
    public ResponseEntity<Object> emailPdfWithUser(
            @PathVariable Long id,
            @PathVariable String userName,
            @RequestBody EmailRequest emailRequest) {
        
        try {
            // Get the report by ID
            IncomingQualityInspectionReport report = reportService.getReportById(id);
            
            // Generate the PDF with the userName
            byte[] pdfBytes = pdfService.generatePdf(report, userName);
            
            // Send email with PDF attachment
            emailService.sendEmailWithAttachment(
                emailRequest.getTo(),
                emailRequest.getSubject(),
                emailRequest.getBody(),
                pdfBytes,
                "iqc_report_" + report.getDocumentNo() + ".pdf"
            );
            
            return ResponseEntity.ok(Map.of("message", "Email sent successfully by " + userName));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to send email: " + e.getMessage()));
        }
    }
}