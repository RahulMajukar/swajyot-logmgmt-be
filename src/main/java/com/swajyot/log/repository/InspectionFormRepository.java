package com.swajyot.log.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.swajyot.log.model.InspectionForm;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InspectionFormRepository extends JpaRepository<InspectionForm, Long> {

    List<InspectionForm> findByStatus(InspectionForm.FormStatus status);
    
    List<InspectionForm> findBySubmittedBy(String submittedBy);
    
    List<InspectionForm> findByReviewedBy(String reviewedBy);
    
    @Query("SELECT f FROM InspectionForm f WHERE f.documentNo = :documentNo")
    InspectionForm findByDocumentNo(@Param("documentNo") String documentNo);
    
    List<InspectionForm> findByProductContainingIgnoreCase(String product);
    
    List<InspectionForm> findByVariant(String variant);
    
    List<InspectionForm> findByInspectionDateBetween(LocalDate startDate, LocalDate endDate);

    List<InspectionForm> findByDocumentNoStartingWith(String documentNoPrefix);
}
