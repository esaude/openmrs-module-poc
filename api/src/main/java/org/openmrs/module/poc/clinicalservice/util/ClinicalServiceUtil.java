/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc.clinicalservice.util;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Component;

@Component
public class ClinicalServiceUtil {
	
	public List<Concept> getNotVoidedConcepts(final String servicekey) {
		final List<Concept> result = new ArrayList<>();
		
		final ClinicalServiceKeys clinicalServiceKey = ClinicalServiceKeys.getClinicalServiceByCode(servicekey);
		
		final List<String> clinicalServicesUuid = MappedClinicalServices.getClinicalServices(clinicalServiceKey);
		
		for (final String clinicalServiceUuid : clinicalServicesUuid) {
			
			final Concept c = Context.getConceptService().getConceptByUuid(clinicalServiceUuid);
			if ((c != null) && !c.isRetired()) {
				result.add(c);
			}
		}
		
		if ((result.size() != clinicalServicesUuid.size())) {
			
			throw new IllegalArgumentException("error.some.clinical.services.were.not.found.for.given.code");
		}
		return result;
	}
}
