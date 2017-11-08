/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc.patientconsultation.service;

import java.util.Date;
import java.util.List;

import org.openmrs.Location;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.poc.patientconsultation.dao.PatientConsultationSummaryDAO;
import org.openmrs.module.poc.patientconsultation.model.PatientConsultationSummary;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface PatientConsultationSummaryService extends OpenmrsService {
	
	public void setPatientConsultationSummaryDAO(final PatientConsultationSummaryDAO patientConsultationSummaryDAO);
	
	public List<PatientConsultationSummary> findPatientConsultationsByLocationAndDateInterval(Location location,
	        Date initialDate, Date finalDate);
	
}
