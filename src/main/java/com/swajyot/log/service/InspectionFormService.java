package com.swajyot.log.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swajyot.log.model.InspectionForm;
import com.swajyot.log.repository.InspectionFormRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j  // This adds the logger
public class InspectionFormService {

    private final InspectionFormRepository inspectionFormRepository;

    // Constants for document number format
    private static final String DOC_PREFIX = "AGI-APR-";
    private static final Pattern DOC_NUMBER_PATTERN = Pattern.compile(DOC_PREFIX + "(\\d+)-(\\d+)");

    public List<InspectionForm> getAllForms() {
        return inspectionFormRepository.findAll();
    }

    public InspectionForm getFormById(Long id) {
        return inspectionFormRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inspection Form not found with id: " + id));
    }

    public List<InspectionForm> getFormsByStatus(InspectionForm.FormStatus status) {
        return inspectionFormRepository.findByStatus(status);
    }

    public List<InspectionForm> getFormsBySubmitter(String submittedBy) {
        return inspectionFormRepository.findBySubmittedBy(submittedBy);
    }

    public List<InspectionForm> getFormsByReviewer(String reviewedBy) {
        return inspectionFormRepository.findByReviewedBy(reviewedBy);
    }

    public List<InspectionForm> getFormsByDateRange(LocalDate startDate, LocalDate endDate) {
        return inspectionFormRepository.findByInspectionDateBetween(startDate, endDate);
    }
    
    /**
     * Get forms by form type (COATING or PRINTING)
     */
    public List<InspectionForm> getFormsByType(InspectionForm.FormType formType) {
        return inspectionFormRepository.findByFormType(formType);
    }

    /**
     * Generates a document number in the format AGI-APR-YY-X
     * where YY is the last two digits of the current year
     * and X is a sequential number that increments for each document in the current year
     */
    private String generateDocumentNumber() {
        // Get current year's last two digits
        int currentYear = LocalDate.now().getYear();
        String yearSuffix = String.valueOf(currentYear).substring(2);

        // Find the latest document number for the current year
        String yearPrefix = DOC_PREFIX + yearSuffix + "-";

        // Find all documents matching the pattern for this year
        List<InspectionForm> formsThisYear = inspectionFormRepository.findByDocumentNoStartingWith(yearPrefix);

        int maxSequence = 0;

        // Find the highest sequence number
        for (InspectionForm form : formsThisYear) {
            Matcher matcher = DOC_NUMBER_PATTERN.matcher(form.getDocumentNo());
            if (matcher.matches()) {
                String yearPart = matcher.group(1);
                String sequencePart = matcher.group(2);

                // Only process if the year matches
                if (yearPart.equals(yearSuffix)) {
                    try {
                        int sequence = Integer.parseInt(sequencePart);
                        if (sequence > maxSequence) {
                            maxSequence = sequence;
                        }
                    } catch (NumberFormatException e) {
                        // Skip invalid format
                    }
                }
            }
        }

        // Increment sequence for new document
        int newSequence = maxSequence + 1;

        // Format the document number
        return DOC_PREFIX + yearSuffix + "-" + newSequence;
    }

    @Transactional
    public InspectionForm createForm(InspectionForm form) {
        // Set default status if not provided
        if (form.getStatus() == null) {
            form.setStatus(InspectionForm.FormStatus.DRAFT);
        }

        // Generate document number if not provided or empty
        if (form.getDocumentNo() == null || form.getDocumentNo().isEmpty()) {
            form.setDocumentNo(generateDocumentNumber());
        }

        // Set default values for dates if not provided
        if (form.getIssueDate() == null) {
            form.setIssueDate(LocalDate.now());
        }

        if (form.getInspectionDate() == null) {
            form.setInspectionDate(LocalDate.now());
        }

        // Default issuance number
        if (form.getIssuanceNo() == null || form.getIssuanceNo().isEmpty()) {
            form.setIssuanceNo("00");
        }
        
        // Set default scope and title based on form type if not provided
        if (form.getFormType() != null && (form.getScope() == null || form.getScope().isEmpty())) {
            switch (form.getFormType()) {
                case COATING:
                    form.setScope("AGI / DEC / COATING");
                    form.setTitle("FIRST ARTICLE INSPECTION REPORT - COATING");
                    break;
                case PRINTING:
                    form.setScope("AGI / DEC / PRINTING");
                    form.setTitle("FIRST ARTICLE INSPECTION REPORT - PRINTING");
                    break;
            }
        }

        return inspectionFormRepository.save(form);
    }

    @Transactional
    public InspectionForm updateForm(Long id, InspectionForm updatedForm) {
        InspectionForm existingForm = getFormById(id);

        // Update the existing form fields with the new values
        existingForm.setDocumentNo(updatedForm.getDocumentNo());
        existingForm.setIssuanceNo(updatedForm.getIssuanceNo());
        existingForm.setIssueDate(updatedForm.getIssueDate());
        existingForm.setReviewedDate(updatedForm.getReviewedDate());
        existingForm.setPage(updatedForm.getPage());
        existingForm.setPreparedBy(updatedForm.getPreparedBy());
        existingForm.setApprovedBy(updatedForm.getApprovedBy());
        existingForm.setIssued(updatedForm.getIssued());
        existingForm.setInspectionDate(updatedForm.getInspectionDate());
        existingForm.setProduct(updatedForm.getProduct());
        existingForm.setSizeNo(updatedForm.getSizeNo());
        existingForm.setShift(updatedForm.getShift());
        existingForm.setVariant(updatedForm.getVariant());
        existingForm.setLineNo(updatedForm.getLineNo());
        existingForm.setCustomer(updatedForm.getCustomer());
        existingForm.setSampleSize(updatedForm.getSampleSize());
        existingForm.setQaExecutive(updatedForm.getQaExecutive());
        existingForm.setQaSignature(updatedForm.getQaSignature());
        existingForm.setProductionOperator(updatedForm.getProductionOperator());
        existingForm.setOperatorSignature(updatedForm.getOperatorSignature());
        existingForm.setFinalApprovalTime(updatedForm.getFinalApprovalTime());
        existingForm.setComments(updatedForm.getComments());
        existingForm.setStatus(updatedForm.getStatus());
        existingForm.setReviewedAt(updatedForm.getReviewedAt());
        existingForm.setReviewedBy(updatedForm.getReviewedBy());
        
        // Update fields specific to the new model
        existingForm.setMcNo(updatedForm.getMcNo());
        existingForm.setScope(updatedForm.getScope());
        existingForm.setTitle(updatedForm.getTitle());
        existingForm.setFormType(updatedForm.getFormType());
        
        // Update the tableData based on form type
        if (updatedForm.getFormType() == InspectionForm.FormType.COATING) {
            // Handle lacquers data if present
            if (updatedForm.getTableData() != null) {
                existingForm.setTableData(updatedForm.getTableData());
            }
        } else if (updatedForm.getFormType() == InspectionForm.FormType.PRINTING) {
            // Handle inks data if present
            if (updatedForm.getTableData() != null) {
                existingForm.setTableData(updatedForm.getTableData());
            }
        }
        
        // Update characteristics
        if (updatedForm.getCharacteristics() != null) {
            existingForm.setCharacteristics(updatedForm.getCharacteristics());
        }

        return inspectionFormRepository.save(existingForm);
    }

    @Transactional
    public InspectionForm submitForm(Long id, String submittedBy) {
        InspectionForm form = getFormById(id);
        form.setStatus(InspectionForm.FormStatus.SUBMITTED);
        form.setSubmittedBy(submittedBy);
        form.setSubmittedAt(LocalDateTime.now());
        return inspectionFormRepository.save(form);
    }

    @Transactional
    public InspectionForm approveForm(Long id, String reviewedBy, String comments) {
        InspectionForm form = getFormById(id);
        form.setStatus(InspectionForm.FormStatus.APPROVED);
        form.setReviewedBy(reviewedBy);
        form.setReviewedAt(LocalDateTime.now());
        form.setComments(comments);
        return inspectionFormRepository.save(form);
    }

    @Transactional
    public InspectionForm rejectForm(Long id, String reviewedBy, String comments) {
        InspectionForm form = getFormById(id);
        form.setStatus(InspectionForm.FormStatus.REJECTED);
        form.setReviewedBy(reviewedBy);
        form.setReviewedAt(LocalDateTime.now());
        form.setComments(comments);
        return inspectionFormRepository.save(form);
    }

    @Transactional
    public void deleteForm(Long id) {
        inspectionFormRepository.deleteById(id);
    }
    
    /**
     * Track PDF download without creating a separate table
     * Simply logs the download action
     *
     * @param formId ID of the form being downloaded
     * @param userName User who is downloading the form
     */
    public void logPdfDownload(Long formId, String userName) {
        log.info("PDF downloaded - Form ID: {}, User: {}, Time: {}", 
                formId, userName, LocalDateTime.now());
                
        // Store download information in the form's audit history
        try {
            InspectionForm form = getFormById(formId);
            
            // Create formatted download record with timestamp
            LocalDateTime downloadTime = LocalDateTime.now();
            String formattedTime = downloadTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String downloadInfo = "PDF downloaded by " + userName + " at " + formattedTime;
            
            // Append to existing comments or create new
            String updatedComments = form.getComments() != null && !form.getComments().isEmpty() ? 
                form.getComments() + "\n" + downloadInfo : downloadInfo;
            
            // Update the form with the download information
            form.setComments(updatedComments);
            inspectionFormRepository.save(form);
            
            log.debug("Successfully recorded PDF download in form comments");
        } catch (Exception e) {
            log.error("Failed to update form with download information: {}", e.getMessage());
        }
    }
}