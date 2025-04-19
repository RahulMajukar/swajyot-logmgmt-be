package com.swajyot.log.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.swajyot.log.model.LineClearanceReport;
import com.swajyot.log.model.req.EmailRequest;
import com.swajyot.log.service.EmailService;
import com.swajyot.log.service.LineClearanceReportPdfService;
import com.swajyot.log.service.LineClearanceReportService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/line-clearance-reports")
public class LineClearanceReportController {

    private final LineClearanceReportService lineClearanceReportService;
    
    @Autowired
    private LineClearanceReportPdfService pdfService;
    
    @Autowired
    private EmailService emailService;

    @GetMapping
    public ResponseEntity<List<LineClearanceReport>> getAllReports() {
        return ResponseEntity.ok(lineClearanceReportService.getAllReports());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineClearanceReport> getReportById(@PathVariable Long id) {
        return ResponseEntity.ok(lineClearanceReportService.getReportById(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<LineClearanceReport>> getReportsByStatus(@PathVariable String status) {
        try {
            LineClearanceReport.ReportStatus reportStatus = 
                    LineClearanceReport.ReportStatus.valueOf(status.toUpperCase());
            return ResponseEntity.ok(lineClearanceReportService.getReportsByStatus(reportStatus));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/submitter/{submitter}")
    public ResponseEntity<List<LineClearanceReport>> getReportsBySubmitter(@PathVariable String submitter) {
        return ResponseEntity.ok(lineClearanceReportService.getReportsBySubmitter(submitter));
    }

    @GetMapping("/reviewer/{reviewer}")
    public ResponseEntity<List<LineClearanceReport>> getReportsByReviewer(@PathVariable String reviewer) {
        return ResponseEntity.ok(lineClearanceReportService.getReportsByReviewer(reviewer));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<LineClearanceReport>> getReportsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(lineClearanceReportService.getReportsByDateRange(startDate, endDate));
    }
    
    @GetMapping("/production-area/{area}")
    public ResponseEntity<List<LineClearanceReport>> getReportsByProductionArea(@PathVariable String area) {
        try {
            LineClearanceReport.ProductionArea productionArea = 
                    LineClearanceReport.ProductionArea.valueOf(area.toUpperCase());
            return ResponseEntity.ok(lineClearanceReportService.getReportsByProductionArea(productionArea));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/line/{line}")
    public ResponseEntity<List<LineClearanceReport>> getReportsByLine(@PathVariable String line) {
        return ResponseEntity.ok(lineClearanceReportService.getReportsByLine(line));
    }
    
    @GetMapping("/product/{productName}")
    public ResponseEntity<List<LineClearanceReport>> getReportsByProductName(@PathVariable String productName) {
        return ResponseEntity.ok(lineClearanceReportService.getReportsByProductName(productName));
    }

    @PostMapping
    public ResponseEntity<LineClearanceReport> createReport(@RequestBody LineClearanceReport report) {
        return new ResponseEntity<>(lineClearanceReportService.createReport(report), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LineClearanceReport> updateReport(@PathVariable Long id, @RequestBody LineClearanceReport report) {
        return ResponseEntity.ok(lineClearanceReportService.updateReport(id, report));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<LineClearanceReport> submitReport(@PathVariable Long id, @RequestParam String submittedBy) {
        return ResponseEntity.ok(lineClearanceReportService.submitReport(id, submittedBy));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<LineClearanceReport> approveReport(
            @PathVariable Long id,
            @RequestParam String reviewedBy,
            @RequestParam(required = false) String comments) {
        return ResponseEntity.ok(lineClearanceReportService.approveReport(id, reviewedBy, comments));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<LineClearanceReport> rejectReport(
            @PathVariable Long id,
            @RequestParam String reviewedBy,
            @RequestParam String comments) {
        return ResponseEntity.ok(lineClearanceReportService.rejectReport(id, reviewedBy, comments));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        lineClearanceReportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Endpoint to generate a PDF of the line clearance report
     * @param id The ID of the line clearance report
     * @param userName Optional parameter for tracking who downloaded the PDF
     * @return The PDF as a byte array
     */
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generatePdf(
            @PathVariable Long id, 
            @RequestParam(required = false) String userName) {
        try {
            // Get the report by ID
            LineClearanceReport report = lineClearanceReportService.getReportById(id);
            
            // Log the download activity if userName is provided
            if (userName != null && !userName.isEmpty()) {
                lineClearanceReportService.logPdfDownload(id, userName);
            }
            
            // Generate the PDF
            byte[] pdfBytes = pdfService.generatePdf(report, userName);
            
            // Set up response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "line_clearance_report_" + report.getDocumentNo() + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Endpoint to send an email with the line clearance report PDF attachment
     * @param id The ID of the line clearance report
     * @param emailRequest The email details (to, subject, body)
     * @return Success or error message
     */
    @PostMapping("/{id}/email-pdf")
    public ResponseEntity<Object> emailPdf(
            @PathVariable Long id,
            @RequestBody EmailRequest emailRequest) {
        
        try {
            // Get the report by ID
            LineClearanceReport report = lineClearanceReportService.getReportById(id);
            
            // Generate the PDF
            byte[] pdfBytes = pdfService.generatePdf(report, "");
            
            // Send email with PDF attachment
            emailService.sendEmailWithAttachment(
                emailRequest.getTo(),
                emailRequest.getSubject(),
                emailRequest.getBody(),
                pdfBytes,
                "line_clearance_report_" + report.getDocumentNo() + ".pdf"
            );
            
            return ResponseEntity.ok(Map.of("message", "Email sent successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to send email: " + e.getMessage()));
        }
    }
    
    /**
     * Additional endpoint to generate a PDF of the line clearance report with userName in path
     * @param id The ID of the line clearance report
     * @param userName The name of the user downloading the PDF
     * @return The PDF as a byte array
     */
    @GetMapping("/{id}/pdf/{userName}")
    public ResponseEntity<byte[]> generatePdfWithUser(
            @PathVariable Long id, 
            @PathVariable String userName) {
        try {
            // Get the report by ID
            LineClearanceReport report = lineClearanceReportService.getReportById(id);
            
            // Log the download activity with the userName
            if (userName != null && !userName.isEmpty()) {
                lineClearanceReportService.logPdfDownload(id, userName);
            }
            
            // Generate the PDF
            byte[] pdfBytes = pdfService.generatePdf(report, userName);
            
            // Set up response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "line_clearance_report_" + report.getDocumentNo() + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Endpoint to send an email with the line clearance report PDF attachment with userName in path
     * @param id The ID of the line clearance report
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
            LineClearanceReport report = lineClearanceReportService.getReportById(id);
            
            // Generate the PDF with the userName
            byte[] pdfBytes = pdfService.generatePdf(report, userName);
            
            // Send email with PDF attachment
            emailService.sendEmailWithAttachment(
                emailRequest.getTo(),
                emailRequest.getSubject(),
                emailRequest.getBody(),
                pdfBytes,
                "line_clearance_report_" + report.getDocumentNo() + ".pdf"
            );
            
            return ResponseEntity.ok(Map.of("message", "Email sent successfully by " + userName));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to send email: " + e.getMessage()));
        }
    }
}