package com.swajyot.log.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.swajyot.log.model.InspectionForm;
import com.swajyot.log.model.req.EmailRequest;
import com.swajyot.log.service.EmailService;
import com.swajyot.log.service.InspectionFormPdfService;
import com.swajyot.log.service.InspectionFormService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/inspection-forms")
public class InspectionFormController {

    private final InspectionFormService inspectionFormService;
    
    @Autowired
    private InspectionFormPdfService pdfService;
    
    @Autowired
    private EmailService emailService;

    @GetMapping
    public ResponseEntity<List<InspectionForm>> getAllForms() {
        return ResponseEntity.ok(inspectionFormService.getAllForms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InspectionForm> getFormById(@PathVariable Long id) {
        return ResponseEntity.ok(inspectionFormService.getFormById(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<InspectionForm>> getFormsByStatus(@PathVariable String status) {
        try {
            // Explicitly provide the enum class as the first parameter
            InspectionForm.FormStatus formStatus = Enum.valueOf(InspectionForm.FormStatus.class, status.toUpperCase());
            return ResponseEntity.ok(inspectionFormService.getFormsByStatus(formStatus));
        } catch (IllegalArgumentException e) {
            // Handle invalid status values
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/submitter/{submitter}")
    public ResponseEntity<List<InspectionForm>> getFormsBySubmitter(@PathVariable String submitter) {
        return ResponseEntity.ok(inspectionFormService.getFormsBySubmitter(submitter));
    }

    @GetMapping("/reviewer/{reviewer}")
    public ResponseEntity<List<InspectionForm>> getFormsByReviewer(@PathVariable String reviewer) {
        return ResponseEntity.ok(inspectionFormService.getFormsByReviewer(reviewer));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<InspectionForm>> getFormsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(inspectionFormService.getFormsByDateRange(startDate, endDate));
    }

    @PostMapping
    public ResponseEntity<InspectionForm> createForm(@RequestBody InspectionForm form) {
        return new ResponseEntity<>(inspectionFormService.createForm(form), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InspectionForm> updateForm(@PathVariable Long id, @RequestBody InspectionForm form) {
        return ResponseEntity.ok(inspectionFormService.updateForm(id, form));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<InspectionForm> submitForm(@PathVariable Long id, @RequestParam String submittedBy) {
        return ResponseEntity.ok(inspectionFormService.submitForm(id, submittedBy));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<InspectionForm> approveForm(
            @PathVariable Long id,
            @RequestParam String reviewedBy,
            @RequestParam(required = false) String comments) {
        return ResponseEntity.ok(inspectionFormService.approveForm(id, reviewedBy, comments));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<InspectionForm> rejectForm(
            @PathVariable Long id,
            @RequestParam String reviewedBy,
            @RequestParam String comments) {
        return ResponseEntity.ok(inspectionFormService.rejectForm(id, reviewedBy, comments));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteForm(@PathVariable Long id) {
        inspectionFormService.deleteForm(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Endpoint to generate a PDF of the inspection form
     * @param id The ID of the inspection form
     * @param userName Optional parameter for tracking who downloaded the PDF
     * @return The PDF as a byte array
     */
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generatePdf(
            @PathVariable Long id, 
            @RequestParam(required = false) String userName) {
        try {
            // Get the form by ID
            InspectionForm form = inspectionFormService.getFormById(id);
            
            // Log the download activity if userName is provided
            if (userName != null && !userName.isEmpty()) {
                inspectionFormService.logPdfDownload(id, userName);
            }
            
            // Generate the PDF
            byte[] pdfBytes = pdfService.generatePdf(form,userName);
            
            // Set up response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "inspection_form_" + form.getDocumentNo() + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Endpoint to send an email with the inspection form PDF attachment
     * @param id The ID of the inspection form
     * @param emailRequest The email details (to, subject, body)
     * @return Success or error message
     */
    @PostMapping("/{id}/email-pdf")
    public ResponseEntity<Object> emailPdf(
            @PathVariable Long id,
            @RequestBody EmailRequest emailRequest) {
        
        try {
            // Get the form by ID
            InspectionForm form = inspectionFormService.getFormById(id);
            
            // Generate the PDF
            byte[] pdfBytes = pdfService.generatePdf(form,"");
            
            // Send email with PDF attachment
            emailService.sendEmailWithAttachment(
                emailRequest.getTo(),
                emailRequest.getSubject(),
                emailRequest.getBody(),
                pdfBytes,
                "inspection_form_" + form.getDocumentNo() + ".pdf"
            );
            
            return ResponseEntity.ok(Map.of("message", "Email sent successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to send email: " + e.getMessage()));
        }
    }
    
    /**
     * New endpoint to send an email with the inspection form PDF attachment with userName in path
     * @param id The ID of the inspection form
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
            // Get the form by ID
            InspectionForm form = inspectionFormService.getFormById(id);
            
            // Generate the PDF with the userName
            byte[] pdfBytes = pdfService.generatePdf(form, userName);
            
            // Send email with PDF attachment
            emailService.sendEmailWithAttachment(
                emailRequest.getTo(),
                emailRequest.getSubject(),
                emailRequest.getBody(),
                pdfBytes,
                "inspection_form_" + form.getDocumentNo() + ".pdf"
            );
            
            return ResponseEntity.ok(Map.of("message", "Email sent successfully by " + userName));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to send email: " + e.getMessage()));
        }
    }
    
    /**
     * Additional endpoint to generate a PDF of the inspection form with userName in path
     * @param id The ID of the inspection form
     * @param userName The name of the user downloading the PDF
     * @return The PDF as a byte array
     */
    @GetMapping("/{id}/pdf/{userName}")
    public ResponseEntity<byte[]> generatePdfWithUser(
            @PathVariable Long id, 
            @PathVariable String userName) {
        try {
            // Get the form by ID
            InspectionForm form = inspectionFormService.getFormById(id);
            
            // Log the download activity with the userName
            if (userName != null && !userName.isEmpty()) {
                inspectionFormService.logPdfDownload(id, userName);
            }
            
            // Generate the PDF
            byte[] pdfBytes = pdfService.generatePdf(form, userName);
            
            // Set up response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "inspection_form_" + form.getDocumentNo() + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}