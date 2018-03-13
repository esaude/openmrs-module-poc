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

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Encounter;

public class PatientConsultation extends BaseOpenmrsData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Encounter encounter;
	
	private Boolean checkInOnConsultationDate;
	
	private PatientConsultationSummary parent;
	
	public PatientConsultation() {
	}
	
	public PatientConsultation(final Encounter encounter, final Boolean checkInOnConsultationDate) {
		this.encounter = encounter;
		this.checkInOnConsultationDate = checkInOnConsultationDate;
	}
	
	public Encounter getEncounter() {
		return this.encounter;
	}
	
	public void setEncounter(final Encounter encounter) {
		this.encounter = encounter;
	}
	
	public Boolean getCheckInOnConsultationDate() {
		return this.checkInOnConsultationDate;
	}
	
	public void setCheckInOnConsultationDate(final Boolean checkInOnConsultationDate) {
		this.checkInOnConsultationDate = checkInOnConsultationDate;
	}
	
	@Override
	public Integer getId() {
		return null;
	}
	
	@Override
	public void setId(final Integer arg0) {
		// this is a wrapper entity
	}
	
	public PatientConsultationSummary getParent() {
		return this.parent;
	}
	
	public void setParent(final PatientConsultationSummary parent) {
		this.parent = parent;
	}
}
