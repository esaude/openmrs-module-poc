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
import java.util.Arrays;
import java.util.Collection;
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
	
	private final Collection<String> CATEGORIES_UUID = Arrays
	        .asList(new String[] { OPENMRSUUIDs.BIOQUIMICA_LABORATORIO_CONCEPT_SET_UUID,
	                OPENMRSUUIDs.HEMOGRAMA_KX21N_CONCEPT_SET_UUID, OPENMRSUUIDs.IMMUNOLOGIA_CONCEPT_SET_UUID,
	                OPENMRSUUIDs.TESTAGEM_VIROLOGIA_CONCEPT_SET_UUID, OPENMRSUUIDs.PCR_CONCEPT_SET_UUID });
	
	@Override
	public List<TestRequest> findAllTestOrderRequest(final Locale locale) {
		
		final List<Concept> allConcepts = this.testRequestDAO.findAll(locale);
		
		final ConceptService conceptService = Context.getConceptService();
		
		final List<TestRequest> testOrderRequests = new ArrayList<>();
		for (final Concept concept : allConcepts) {
			
			final List<ConceptSet> ConceptSets = conceptService.getSetsContainingConcept(concept);
			
			final List<Concept> categories = new ArrayList<>();
			for (final ConceptSet conceptSet : ConceptSets) {
				if (this.CATEGORIES_UUID.contains(conceptSet.getConceptSet().getUuid())
				        && !(OPENMRSUUIDs.GAMA_GLUTAMIL_TRANSFERASE.equals(concept.getUuid())
				        && OPENMRSUUIDs.HEMOGRAMA_KX21N_CONCEPT_SET_UUID
				                .equals(conceptSet.getConceptSet().getUuid()))) {
					categories.add(conceptSet.getConceptSet());
				}
			}
			if (!categories.isEmpty()) {
				testOrderRequests.add(new TestRequest(concept, categories.iterator().next()));
			}
		}
		return testOrderRequests;
	}
	
	@Override
	public void setTestRequestDAO(final TestRequestDAO testRequestDAO) {
		this.testRequestDAO = testRequestDAO;
	}
}
