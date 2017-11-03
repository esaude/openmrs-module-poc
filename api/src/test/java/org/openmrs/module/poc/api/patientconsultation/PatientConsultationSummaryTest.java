/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc.api.patientconsultation;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.module.poc.patientconsultation.model.PatientConsultation;
import org.openmrs.module.poc.patientconsultation.model.PatientConsultationSummary;

public class PatientConsultationSummaryTest {
	
	@Test
	public void shouldCreateAnInstanceOfPatientConsultationSummary() {
		
		final PatientConsultationSummary summary = new PatientConsultationSummary();
		
		summary.setConsultationDate(new Date());
		summary.addPatientConsultation(new PatientConsultation());
		
		summary.setId(10);
		Assert.assertNotNull(summary);
		Assert.assertNotNull(summary.getConsultationDate());
		Assert.assertEquals(1, summary.getPatientConsultations().size());
		Assert.assertEquals(null, summary.getId());
	}
	
	public void shouldCreateAnInstanceOfPatientConsultation() {
		
		final PatientConsultation patientConsultation = new PatientConsultation();
		
		patientConsultation.setParent(new PatientConsultationSummary());
		patientConsultation.setEncounter(new Encounter());
		patientConsultation.setCheckInOnConsultationDate(Boolean.TRUE);
		patientConsultation.setId(10);
		
		Assert.assertNotNull(patientConsultation);
		Assert.assertNotNull(patientConsultation.getParent());
		Assert.assertNotNull(patientConsultation.getEncounter());
		Assert.assertNotNull(patientConsultation.getCheckInOnConsultationDate());
		Assert.assertNull(patientConsultation.getId());
	}
	
}
