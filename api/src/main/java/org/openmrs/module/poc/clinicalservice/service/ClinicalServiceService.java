package org.openmrs.module.poc.clinicalservice.service;

import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.poc.api.common.exception.POCBusinessException;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ClinicalServiceService extends OpenmrsService {
	
	void setObsService(ObsService obsService);
	
	void setConceptService(ConceptService conceptService);
	
	void setEncounterService(EncounterService encounterService);
	
	void deleteClinicalService(String encounterUuid, String clinicalServiceUuid) throws POCBusinessException;
	
}
