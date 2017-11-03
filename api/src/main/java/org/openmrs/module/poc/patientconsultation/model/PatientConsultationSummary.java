/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc.patientconsultation.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openmrs.BaseOpenmrsData;

public class PatientConsultationSummary extends BaseOpenmrsData
        implements Comparable<PatientConsultationSummary>, Serializable {
	
	private static final long serialVersionUID = -3409006199467109819L;
	
	private final List<PatientConsultation> patientConsultations = new ArrayList<>();
	
	private Date consultationDate;
	
	public PatientConsultationSummary() {
	}
	
	public PatientConsultationSummary(final Date consultationDate) {
		this.consultationDate = consultationDate;
	}
	
	public List<PatientConsultation> getPatientConsultations() {
		return this.patientConsultations;
	}
	
	public void addPatientConsultation(final PatientConsultation patientConsultation) {
		this.patientConsultations.add(patientConsultation);
	}
	
	public Date getConsultationDate() {
		return this.consultationDate;
	}
	
	public void setConsultationDate(final Date consultationDate) {
		this.consultationDate = consultationDate;
	}
	
	@Override
	public Integer getId() {
		return null;
	}
	
	@Override
	public void setId(final Integer arg0) {
	}
	
	@Override
	public int compareTo(final PatientConsultationSummary o) {
		return this.consultationDate.compareTo(o.getConsultationDate());
	}
}
