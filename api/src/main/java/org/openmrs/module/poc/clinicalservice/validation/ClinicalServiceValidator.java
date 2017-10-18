/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
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
