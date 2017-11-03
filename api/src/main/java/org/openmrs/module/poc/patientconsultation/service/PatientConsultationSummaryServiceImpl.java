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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.poc.clinicalservice.util.ConceptUUIDConstants;
import org.openmrs.module.poc.clinicalservice.util.EncounterTyeUUIDConstants;
import org.openmrs.module.poc.patientconsultation.dao.PatientConsultationSummaryDAO;
import org.openmrs.module.poc.patientconsultation.model.PatientConsultation;
import org.openmrs.module.poc.patientconsultation.model.PatientConsultationSummary;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PatientConsultationSummaryServiceImpl extends BaseOpenmrsService
        implements PatientConsultationSummaryService {
	
	private PatientConsultationSummaryDAO patientConsultationSummaryDAO;
	
	@Override
	public void setPatientConsultationSummaryDAO(final PatientConsultationSummaryDAO patientConsultationSummaryDAO) {
		this.patientConsultationSummaryDAO = patientConsultationSummaryDAO;
	}
	
	@Override
	public List<PatientConsultationSummary> findPatientConsultationsByLocationAndDateInterval(final Location location,
	        final Date startDate, final Date endDate) {
		
		final Concept concept = Context.getConceptService().getConceptByUuid(ConceptUUIDConstants.RETURN_VISIT_DATE);
		
		final EncounterType adultFollowUp = Context.getEncounterService()
		        .getEncounterTypeByUuid(EncounterTyeUUIDConstants.STARV_ADULT_FOLLOW_UP);
		final EncounterType pediatricFollowUp = Context.getEncounterService()
		        .getEncounterTypeByUuid(EncounterTyeUUIDConstants.STARV_PEDIATRIC_FOLLOW_UP);
		final EncounterType ccrFollowUp = Context.getEncounterService()
		        .getEncounterTypeByUuid(EncounterTyeUUIDConstants.CCR_FOLLOW_UP);
		
		final List<Obs> resultObs = this.patientConsultationSummaryDAO.findObsByLocationAndDateInterval(
		    Arrays.asList(adultFollowUp, pediatricFollowUp, ccrFollowUp), concept, location, startDate, endDate);
		
		return this.summarizeObjectsToPatientConsultation(this.groupObsByDateNextVisitDate(resultObs));
	}
	
	private Map<Date, List<Obs>> groupObsByDateNextVisitDate(final List<Obs> resultObs) {
		
		final Map<Date, List<Obs>> mapObsByNextVisitDate = new HashMap<>();
		for (final Obs obs : resultObs) {
			List<Obs> list = mapObsByNextVisitDate.get(obs.getValueDatetime());
			if (list == null) {
				mapObsByNextVisitDate.put(obs.getValueDatetime(), list = new ArrayList<>());
			}
			list.add(obs);
		}
		
		return mapObsByNextVisitDate;
	}
	
	private List<PatientConsultationSummary> summarizeObjectsToPatientConsultation(
	        final Map<Date, List<Obs>> mapObsByDateNextVisit) {
		
		final List<PatientConsultationSummary> listSummary = new ArrayList<>();
		
		for (final Entry<Date, List<Obs>> entry : mapObsByDateNextVisit.entrySet()) {
			
			final Date valueDateTime = entry.getKey();
			final List<Obs> listObs = entry.getValue();
			
			final PatientConsultationSummary patientConsultationSummay = new PatientConsultationSummary(valueDateTime);
			for (final Obs obs : listObs) {
				
				final boolean ckeckedInInExpectedNextVisitDate = this.patientConsultationSummaryDAO
				        .hasCheckinInExpectedNextVisitDate(new Patient(obs.getPerson().getId()), valueDateTime);
				
				final PatientConsultation patientConsultation = new PatientConsultation(obs.getEncounter(),
				        ckeckedInInExpectedNextVisitDate);
				
				patientConsultationSummay.addPatientConsultation(patientConsultation);
			}
			listSummary.add(patientConsultationSummay);
		}
		Collections.sort(listSummary);
		return listSummary;
	}
}
