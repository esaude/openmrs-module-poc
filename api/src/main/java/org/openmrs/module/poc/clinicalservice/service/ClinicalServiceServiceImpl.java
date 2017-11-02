/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc.clinicalservice.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.poc.api.common.exception.POCBusinessException;
import org.openmrs.module.poc.clinicalservice.util.ClinicalServiceKeys;
import org.openmrs.module.poc.clinicalservice.util.ClinicalServiceUtil;
import org.openmrs.module.poc.clinicalservice.validation.ClinicalServiceValidator;
import org.springframework.beans.factory.annotation.Autowired;

public class ClinicalServiceServiceImpl extends BaseOpenmrsService implements ClinicalServiceService {
	
	private ObsService obsService;
	
	private EncounterService encounterService;
	
	@Autowired
	private ClinicalServiceValidator clinicalServiceValidator;
	
	@Autowired
	private ClinicalServiceUtil clinicalServiceUtil;
	
	@Override
	public void deleteClinicalService(final String encounterUuid, final String clinicalServiceKey)
	        throws POCBusinessException {
		
		final Encounter es = new Encounter();
		es.setUuid(encounterUuid);
		
		this.clinicalServiceValidator.validateDeletion(es, clinicalServiceKey);
		
		final Encounter encounter = this.encounterService.getEncounterByUuid(encounterUuid);
		final List<Concept> clinicalServices = this.clinicalServiceUtil.getNotVoidedConcepts(clinicalServiceKey);
		
		final Set<Obs> allObs = encounter.getAllObs();
		
		for (final Obs obs : allObs) {
			
			final Concept obsClinicalService = obs.getConcept();
			
			final String infoMessage = Context.getMessageSourceService().getMessage(
			    "poc.info.clinical.service.inactivated",
			    Arrays.asList(obsClinicalService.getDisplayString()).toArray(), Context.getLocale());
			
			if (clinicalServices.contains(obsClinicalService) && !obs.isVoided()) {
				this.obsService.voidObs(obs, infoMessage);
			}
		}
		
		final Set<Obs> remainActiveObss = encounter.getAllObs();
		if (remainActiveObss.isEmpty()) {
			
			this.encounterService.voidEncounter(encounter, Context.getMessageSourceService()
			        .getMessage("poc.info.encounter.inactivated.due.deleted.all.clinical.services"));
		} else {
			
			final List<Concept> allClinicalServices = this.getAllClinicalServices();
			for (final Obs obs : remainActiveObss) {
				
				final String infoMessage = Context.getMessageSourceService().getMessage(
				    "poc.info.clinical.service.inactivated",
				    Arrays.asList(obs.getConcept().getDisplayString()).toArray(), Context.getLocale());
				
				if (!allClinicalServices.contains(obs.getConcept()) && obs.getRelatedObservations().isEmpty()) {
					this.obsService.voidObs(obs, infoMessage);
				}
			}
			if (encounter.getAllObs().isEmpty()) {
				
				this.encounterService.voidEncounter(encounter, Context.getMessageSourceService()
				        .getMessage("poc.info.encounter.inactivated.due.deleted.all.clinical.services"));
			}
		}
	}
	
	private List<Concept> getAllClinicalServices() {
		
		final List<Concept> allClinicalServices = new ArrayList<>();
		
		for (final ClinicalServiceKeys clinicalServiceKey : ClinicalServiceKeys.values()) {
			
			allClinicalServices.addAll(this.clinicalServiceUtil.getNotVoidedConcepts(clinicalServiceKey.getCode()));
		}
		return allClinicalServices;
	}
	
	@Override
	public void setObsService(final ObsService obsService) {
		this.obsService = obsService;
	}
	
	@Override
	public void setEncounterService(final EncounterService encounterService) {
		this.encounterService = encounterService;
	}
}
