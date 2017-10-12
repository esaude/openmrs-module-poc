package org.openmrs.module.poc.clinicalservice.validation;

import org.openmrs.Encounter;
import org.openmrs.module.poc.api.common.exception.POCBusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClinicalServiceValidator {
	
	@Autowired
	private EncounterClinicalServiceRule encounterClinicalServiceRule;
	
	@Autowired
	private ClinicalServiceMappingRule clinicalServiceMappingRule;
	
	public void validateDeletion(final Encounter encounterService, final String serviceKey) throws POCBusinessException {
		
		this.encounterClinicalServiceRule.validate(encounterService);
		this.clinicalServiceMappingRule.validate(serviceKey);
	}
}
