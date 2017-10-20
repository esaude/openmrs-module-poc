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
import java.util.List;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.poc.api.common.exception.POCBusinessException;
import org.openmrs.module.poc.clinicalservice.util.ClinicalServiceKeys;
import org.openmrs.module.poc.clinicalservice.util.MappedClinicalServices;
import org.openmrs.module.poc.clinicalservice.validation.ClinicalServiceValidator;
import org.springframework.beans.factory.annotation.Autowired;

public class ClinicalServiceServiceImpl extends BaseOpenmrsService implements ClinicalServiceService {
	
	private ObsService obsService;
	
	private ConceptService conceptService;
	
	private EncounterService encounterService;
	
	@Autowired
	private ClinicalServiceValidator clinicalServiceValidator;
	
	@Override
	public void deleteClinicalService(final String encounterUuid, final String clinicalServiceKey)
	        throws POCBusinessException {
		
		final Encounter es = new Encounter();
		es.setUuid(encounterUuid);
		
		this.clinicalServiceValidator.validateDeletion(es, clinicalServiceKey);
		
		final Encounter encounter = this.encounterService.getEncounterByUuid(encounterUuid);
		final List<Concept> clinicalServices = this.getClinicalServices(clinicalServiceKey);
		
		final Set<Obs> allObs = encounter.getAllObs();
		
		for (final Obs obs : allObs) {
			
			final Concept obsClinicalService = obs.getConcept();
			
			if (clinicalServices.contains(obsClinicalService)) {
				if (!obs.isVoided()) {
					this.obsService.voidObs(obs, "delete clinical service +" + obsClinicalService.getDisplayString());
				}
				clinicalServices.remove(obsClinicalService);
			}
		}
		
		final Set<Obs> remainActiveObss = encounter.getAllObs();
		if (remainActiveObss.isEmpty()) {
			this.encounterService.voidEncounter(encounter, "deleted All clinical Services");
		} else {
			
			final List<Concept> allClinicalServices = this.getAllClinicalServices();
			for (final Obs obs : remainActiveObss) {
				
				if (!allClinicalServices.contains(obs.getConcept())) {
					this.obsService.voidObs(obs, "delete clinical service +" + obs.getConcept().getDisplayString());
				}
			}
			
			if (encounter.getAllObs().isEmpty()) {
				
				this.encounterService.voidEncounter(encounter, "deleted All clinical Services");
			}
		}
	}
	
	private List<Concept> getClinicalServices(final String clinicalServiceKey) {
		
		final List<String> clinicalServiceUuids = MappedClinicalServices
		        .getClinicalServices(ClinicalServiceKeys.getClinicalServiceByCode(clinicalServiceKey));
		
		final List<Concept> clinicalServices = new ArrayList<>();
		
		for (final String clinicalServiceUuid : clinicalServiceUuids) {
			
			clinicalServices.add(this.conceptService.getConceptByUuid(clinicalServiceUuid));
		}
		return clinicalServices;
	}
	
	private List<Concept> getAllClinicalServices() {
		
		final List<Concept> allClinicalServices = new ArrayList<>();
		
		for (final ClinicalServiceKeys clinicalServiceKey : ClinicalServiceKeys.values()) {
			
			allClinicalServices.addAll(this.getClinicalServices(clinicalServiceKey.getCode()));
		}
		return allClinicalServices;
	}
	
	@Override
	public void setObsService(final ObsService obsService) {
		this.obsService = obsService;
	}
	
	@Override
	public void setConceptService(final ConceptService conceptService) {
		this.conceptService = conceptService;
	}
	
	@Override
	public void setEncounterService(final EncounterService encounterService) {
		this.encounterService = encounterService;
	}
}
