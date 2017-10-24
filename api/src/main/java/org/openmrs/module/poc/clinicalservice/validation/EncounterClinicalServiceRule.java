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

import java.util.Set;

import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.poc.api.common.exception.POCBusinessException;
import org.openmrs.module.poc.api.common.validation.IValidationRule;
import org.springframework.stereotype.Component;

@Component
public class EncounterClinicalServiceRule implements IValidationRule<Encounter> {
	
	@Override
	public void validate(final Encounter encounter) throws POCBusinessException {
		
		if ((encounter == null) || (encounter.getUuid() == null)) {
			throw new POCBusinessException("encounter not provided");
		}
		
		final Encounter encounterFound = Context.getEncounterService().getEncounterByUuid(encounter.getUuid());
		
		if (encounterFound == null) {
			throw new POCBusinessException("encounter not found for given uuid " + encounter.getUuid());
		}
		
		if (encounterFound.isVoided()) {
			throw new POCBusinessException("Cannot delete services for a voided encounter " + encounter.getUuid());
		}
		
		final Set<Obs> allNonVoidedEncounterObs = encounterFound.getAllObs();
		
		if (allNonVoidedEncounterObs.isEmpty()) {
			throw new POCBusinessException("Cannot delete services for encounter without non voided Obs "
			        + encounter.getUuid());
		}
	}
}
