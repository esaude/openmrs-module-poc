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
package org.openmrs.module.poc.api.testrequest.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.openmrs.Concept;
import org.openmrs.ConceptSet;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.poc.api.common.util.OPENMRSUUIDs;
import org.openmrs.module.poc.api.testrequest.dao.TestRequestDAO;
import org.openmrs.module.poc.api.testrequest.model.TestRequest;

public class TestRequestServiceImpl extends BaseOpenmrsService implements TestRequestService {
	
	private TestRequestDAO testRequestDAO;
	
	@Override
	public void setTestRequestDAO(final TestRequestDAO testRequestDAO) {
		this.testRequestDAO = testRequestDAO;
	}
	
	@Override
	public List<TestRequest> findAllTestRequests(final Locale locale) {
		
		final List<Concept> allConcepts = this.testRequestDAO.findAll(locale);
		
		final ConceptService conceptService = Context.getConceptService();
		
		final List<TestRequest> testOrderRequests = new ArrayList<>();
		for (final Concept concept : allConcepts) {
			
			final List<ConceptSet> conceptSets = conceptService.getSetsContainingConcept(concept);
			
			final List<Concept> categories = new ArrayList<>();
			for (final ConceptSet conceptSet : conceptSets) {
				categories.add(conceptSet.getConceptSet());
			}
			
			if (OPENMRSUUIDs.RAPID_PLASMA_REAGIN_CONCEP_UUID.equals(concept.getUuid()) && categories.isEmpty()) {
				categories.add(Context.getConceptService().getConceptByUuid(OPENMRSUUIDs.IMMUNOLOGIA_CONCEPT_SET_UUID));
			}
			
			if (OPENMRSUUIDs.DVRL_CONCEPT_UUID.equals(concept.getUuid()) && categories.isEmpty()) {
				categories.add(
				        Context.getConceptService().getConceptByUuid(OPENMRSUUIDs.HEMOGRAMA_KX21N_CONCEPT_SET_UUID));
			}
			
			if (!categories.isEmpty()) {
				testOrderRequests.add(new TestRequest(concept, categories.iterator().next()));
			}
		}
		return testOrderRequests;
	}
}
