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
package org.openmrs.module.poc.testrequest.service;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.poc.testrequest.model.TestRequest;
import org.openmrs.module.poc.testrequest.util.LaboratoryGeralSets;
import org.openmrs.module.poc.testrequest.util.MappedLaboratoryGeralSet;

public class TestRequestServiceImpl extends BaseOpenmrsService implements TestRequestService {
	
	private ConceptService conceptService;
	
	@Override
	public void setConceptService(final ConceptService conceptService) {
		this.conceptService = conceptService;
	}
	
	@Override
	public List<TestRequest> findAllTestRequests() {
		
		final List<TestRequest> testRequests = new ArrayList<>();
		
		for (final LaboratoryGeralSets laboratoryGeralSet : LaboratoryGeralSets.values()) {
			
			final List<String> conceptsUUids = MappedLaboratoryGeralSet.getLaboratoryConceptTests(laboratoryGeralSet);
			
			final Concept category = this.conceptService.getConceptByUuid(laboratoryGeralSet.getUuid());
			
			for (final String uuid : conceptsUUids) {
				
				final Concept concept = this.conceptService.getConceptByUuid(uuid);
				
				if ((concept != null) && (category != null) && category.isSet()) {
					testRequests.add(new TestRequest(concept, category));
					
				}
			}
		}
		return testRequests;
	}
}
