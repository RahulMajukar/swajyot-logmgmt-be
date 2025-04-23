package com.swajyot.log.controller;

import com.swajyot.log.model.PrintingInspectionReport;
import com.swajyot.log.service.PrintingInspectionReportPdfService;
import com.swajyot.log.service.PrintingInspectionReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/printing-inspection")
public class PrintingInspectionReportController {

    @Autowired
    private PrintingInspectionReportService service;

    @Autowired
    private PrintingInspectionReportPdfService pdfService;

    @GetMapping
    public ResponseEntity<List<PrintingInspectionReport>> getAllReports() {
        return ResponseEntity.ok(service.getAllReports());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrintingInspectionReport> getReportById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.getReportById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/status")
    public ResponseEntity<List<PrintingInspectionReport>> getByStatus(@RequestParam String status) {
        try {
            return ResponseEntity.ok(service.getReportsByStatus(PrintingInspectionReport.ReportStatus.valueOf(status.toUpperCase())));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<PrintingInspectionReport> createReport(@RequestBody PrintingInspectionReport report) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createReport(report));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PrintingInspectionReport> updateReport(@PathVariable Long id, @RequestBody PrintingInspectionReport updatedReport) throws IllegalStateException {
        try {
            return ResponseEntity.ok(service.updateReport(id, updatedReport));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/submit/{id}")
    public ResponseEntity<PrintingInspectionReport> submitReport(@PathVariable Long id) throws IllegalStateException {
        try {
            return ResponseEntity.ok(service.submitReport(id, "system"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<PrintingInspectionReport> approveReport(@PathVariable Long id, @RequestParam(required = false) String comments) throws IllegalStateException {
        try {
            return ResponseEntity.ok(service.approveReport(id, "system", comments));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity<PrintingInspectionReport> rejectReport(@PathVariable Long id, @RequestParam(required = false) String comments) throws IllegalStateException {
        try {
            return ResponseEntity.ok(service.rejectReport(id, "system", comments));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) throws IllegalStateException {
        try {
            service.deleteReport(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/pdf/{id}")
    public ResponseEntity<byte[]> generatePdf(@PathVariable Long id) {
        try {
            PrintingInspectionReport report = service.getReportById(id);
            byte[] pdfBytes = pdfService.generatePdf(report, "system");
            service.logPdfDownload(id, "system");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "inspection-report-" + id + ".pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (IOException | RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Long>> getReportSummary() {
        Map<String, Long> summary = new HashMap<>();
        for (PrintingInspectionReport.ReportStatus status : PrintingInspectionReport.ReportStatus.values()) {
            summary.put(status.name(), (long) service.getReportsByStatus(status).size());
        }
        summary.put("TOTAL", (long) service.getAllReports().size());
        return ResponseEntity.ok(summary);
    }
}
