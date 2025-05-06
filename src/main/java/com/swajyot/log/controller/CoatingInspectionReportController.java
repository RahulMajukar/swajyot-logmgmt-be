package com.swajyot.log.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.swajyot.log.model.CoatingInspectionReport;
import com.swajyot.log.model.req.EmailRequest;
import com.swajyot.log.service.EmailService;
import com.swajyot.log.service.pdf.CoatingInspectionReportPdfService;
import com.swajyot.log.service.CoatingInspectionReportService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/coating-inspection-reports")
@RequiredArgsConstructor
public class CoatingInspectionReportController {

    private final CoatingInspectionReportService coatingInspectionReportService;
    
    @Autowired
    private CoatingInspectionReportPdfService pdfService;
    
    @Autowired
    private EmailService emailService;

    @GetMapping
    public ResponseEntity<List<CoatingInspectionReport>> getAllReports() {
        return ResponseEntity.ok(coatingInspectionReportService.getAllReports());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CoatingInspectionReport> getReportById(@PathVariable Long id) {
        return ResponseEntity.ok(coatingInspectionReportService.getReportById(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<CoatingInspectionReport>> getReportsByStatus(@PathVariable String status) {
        try {
            CoatingInspectionReport.ReportStatus reportStatus = 
                    CoatingInspectionReport.ReportStatus.valueOf(status.toUpperCase());
            return ResponseEntity.ok(coatingInspectionReportService.getReportsByStatus(reportStatus));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/submitter/{submitter}")
    public ResponseEntity<List<CoatingInspectionReport>> getReportsBySubmitter(@PathVariable String submitter) {
        return ResponseEntity.ok(coatingInspectionReportService.getReportsBySubmitter(submitter));
    }

    @GetMapping("/reviewer/{reviewer}")
    public ResponseEntity<List<CoatingInspectionReport>> getReportsByReviewer(@PathVariable String reviewer) {
        return ResponseEntity.ok(coatingInspectionReportService.getReportsByReviewer(reviewer));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<CoatingInspectionReport>> getReportsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(coatingInspectionReportService.getReportsByDateRange(startDate, endDate));
    }
    
    @GetMapping("/product/{product}")
    public ResponseEntity<List<CoatingInspectionReport>> getReportsByProduct(@PathVariable String product) {
        return ResponseEntity.ok(coatingInspectionReportService.getReportsByProduct(product));
    }
    
    @GetMapping("/variant/{variant}")
    public ResponseEntity<List<CoatingInspectionReport>> getReportsByVariant(@PathVariable String variant) {
        return ResponseEntity.ok(coatingInspectionReportService.getReportsByVariant(variant));
    }
    
    @GetMapping("/line/{lineNo}")
    public ResponseEntity<List<CoatingInspectionReport>> getReportsByLineNo(@PathVariable String lineNo) {
        return ResponseEntity.ok(coatingInspectionReportService.getReportsByLineNo(lineNo));
    }

    @PostMapping
    public ResponseEntity<CoatingInspectionReport> createReport(@RequestBody CoatingInspectionReport report) {
        return new ResponseEntity<>(coatingInspectionReportService.createReport(report), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CoatingInspectionReport> updateReport(@PathVariable Long id, @RequestBody CoatingInspectionReport report) {
        return ResponseEntity.ok(coatingInspectionReportService.updateReport(id, report));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<CoatingInspectionReport> submitReport(@PathVariable Long id, @RequestParam String submittedBy) {
        return ResponseEntity.ok(coatingInspectionReportService.submitReport(id, submittedBy));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<CoatingInspectionReport> approveReport(
            @PathVariable Long id,
            @RequestParam String reviewedBy,
            @RequestParam(required = false) String comments) {
        return ResponseEntity.ok(coatingInspectionReportService.approveReport(id, reviewedBy, comments));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<CoatingInspectionReport> rejectReport(
            @PathVariable Long id,
            @RequestParam String reviewedBy,
            @RequestParam String comments) {
        return ResponseEntity.ok(coatingInspectionReportService.rejectReport(id, reviewedBy, comments));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        coatingInspectionReportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Endpoint to generate a PDF of the coating inspection report
     * @param id The ID of the coating inspection report
     * @param userName Optional parameter for tracking who downloaded the PDF
     * @return The PDF as a byte array
     */
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generatePdf(
            @PathVariable Long id, 
            @RequestParam(required = false) String userName) {
        try {
            // Get the report by ID
            CoatingInspectionReport report = coatingInspectionReportService.getReportById(id);
            
            // Log the download activity if userName is provided
            if (userName != null && !userName.isEmpty()) {
                coatingInspectionReportService.logPdfDownload(id, userName);
            }
            
            // Generate the PDF
            byte[] pdfBytes = pdfService.generatePdf(report, userName);
            
            // Set up response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "coating_inspection_report_" + report.getDocumentNo() + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Endpoint to send an email with the coating inspection report PDF attachment
     * @param id The ID of the coating inspection report
     * @param emailRequest The email details (to, subject, body)
     * @return Success or error message
     */
    @PostMapping("/{id}/email-pdf")
    public ResponseEntity<Object> emailPdf(
            @PathVariable Long id,
            @RequestBody EmailRequest emailRequest) {
        
        try {
            // Get the report by ID
            CoatingInspectionReport report = coatingInspectionReportService.getReportById(id);
            
            // Generate the PDF
            byte[] pdfBytes = pdfService.generatePdf(report, "");
            
            // Send email with PDF attachment
            emailService.sendEmailWithAttachment(
                emailRequest.getTo(),
                emailRequest.getSubject(),
                emailRequest.getBody(),
                pdfBytes,
                "coating_inspection_report_" + report.getDocumentNo() + ".pdf"
            );
            
            return ResponseEntity.ok(Map.of("message", "Email sent successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to send email: " + e.getMessage()));
        }
    }
    
    /**
     * Additional endpoint to generate a PDF of the coating inspection report with userName in path
     * @param id The ID of the coating inspection report
     * @param userName The name of the user downloading the PDF
     * @return The PDF as a byte array
     */
    @GetMapping("/{id}/pdf/{userName}")
    public ResponseEntity<byte[]> generatePdfWithUser(
            @PathVariable Long id, 
            @PathVariable String userName) {
        try {
            // Get the report by ID
            CoatingInspectionReport report = coatingInspectionReportService.getReportById(id);
            
            // Log the download activity with the userName
            if (userName != null && !userName.isEmpty()) {
                coatingInspectionReportService.logPdfDownload(id, userName);
            }
            
            // Generate the PDF
            byte[] pdfBytes = pdfService.generatePdf(report, userName);
            
            // Set up response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "coating_inspection_report_" + report.getDocumentNo() + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Endpoint to send an email with the coating inspection report PDF attachment with userName in path
     * @param id The ID of the coating inspection report
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
            CoatingInspectionReport report = coatingInspectionReportService.getReportById(id);
            
            // Generate the PDF with the userName
            byte[] pdfBytes = pdfService.generatePdf(report, userName);
            
            // Send email with PDF attachment
            emailService.sendEmailWithAttachment(
                emailRequest.getTo(),
                emailRequest.getSubject(),
                emailRequest.getBody(),
                pdfBytes,
                "coating_inspection_report_" + report.getDocumentNo() + ".pdf"
            );
            
            return ResponseEntity.ok(Map.of("message", "Email sent successfully by " + userName));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to send email: " + e.getMessage()));
        }
    }
}