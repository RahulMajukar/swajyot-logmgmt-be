package com.swajyot.log.model;

import java.time.LocalDate;

public interface ReportHeader {
	 String getDocumentNo();
	 String getRevision();
	 LocalDate getEffectiveDate();
	 LocalDate getReviewedOn();
	 String getPage();
	 String getPreparedBy();
	 String getApprovedBy();
	 String getIssued();
	 String getScope();
	 String getTitle();
	 String getUnit();
	}
