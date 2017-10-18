/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
/**
 *
 */
package org.openmrs.module.poc.clinicalservice.validation;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.poc.api.common.exception.POCBusinessException;
import org.openmrs.module.poc.clinicalservice.util.ClinicalServiceKeys;
import org.openmrs.module.poc.clinicalservice.util.MappedClinicalServices;
import org.springframework.stereotype.Component;

@Component
public class ClinicalServiceMappingRule {
	
	public void validate(final String servicekey) throws POCBusinessException {
		
		try {
			final ClinicalServiceKeys clinicalServiceKey = ClinicalServiceKeys.getClinicalServiceByCode(servicekey);
			
			final List<String> clinicalServicesUuid = MappedClinicalServices.getClinicalServices(clinicalServiceKey);
			
			final List<Concept> clinicalServices = new ArrayList<>();
			for (final String clinicalServiceUuid : clinicalServicesUuid) {
				
				final Concept c = Context.getConceptService().getConceptByUuid(clinicalServiceUuid);
				if (c != null) {
					clinicalServices.add(c);
				}
			}
			if ((clinicalServices.size() != clinicalServicesUuid.size())) {
				
				throw new POCBusinessException("Some clinical services mapping value was not found");
			}
			
		}
		catch (final IllegalArgumentException e) {
			throw new POCBusinessException(e.getMessage());
		}
	}
}
