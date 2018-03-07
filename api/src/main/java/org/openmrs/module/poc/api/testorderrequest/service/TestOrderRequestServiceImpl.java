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
package org.openmrs.module.poc.api.testorderrequest.service;

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
import org.openmrs.module.poc.api.testorderrequest.dao.TestOrderRequestDAO;
import org.openmrs.module.poc.api.testorderrequest.model.TestOrderRequest;
import org.openmrs.module.poc.api.testorderrequest.util.TestOrderRequestUtil;

public class TestOrderRequestServiceImpl extends BaseOpenmrsService implements TestOrderRequestService {
	
	private TestOrderRequestDAO testOrderRequestDAO;
	
	private final Collection<String> CATEGORIES_UUID = Arrays.asList(new String[] {
	        TestOrderRequestUtil.BIOQUIMICA_LABORATORIO_CONCEPT_SET_UUID,
	        TestOrderRequestUtil.HEMOGRAMA_KX21N_CONCEPT_SET_UUID, TestOrderRequestUtil.IMMUNOLOGIA_CONCEPT_SET_UUID,
	        TestOrderRequestUtil.TESTAGEM_VIROLOGIA_CONCEPT_SET_UUID, TestOrderRequestUtil.PCR_CONCEPT_SET_UUID });
	
	@Override
	public List<TestOrderRequest> findAllTestOrderRequest(final Locale locale) {
		
		final List<Concept> allConcepts = this.testOrderRequestDAO.findAll(locale);
		
		final ConceptService conceptService = Context.getConceptService();
		
		final List<TestOrderRequest> testOrderRequests = new ArrayList<>();
		for (final Concept concept : allConcepts) {
			
			final List<ConceptSet> ConceptSets = conceptService.getSetsContainingConcept(concept);
			
			final List<Concept> categories = new ArrayList<>();
			for (final ConceptSet conceptSet : ConceptSets) {
				if (this.CATEGORIES_UUID.contains(conceptSet.getConceptSet().getUuid())
				        && !(TestOrderRequestUtil.GAMA_GLUTAMIL_TRANSFERASE.equals(concept.getUuid())
				        && TestOrderRequestUtil.HEMOGRAMA_KX21N_CONCEPT_SET_UUID
				                .equals(conceptSet.getConceptSet().getUuid()))) {
					categories.add(conceptSet.getConceptSet());
				}
			}
			if (!categories.isEmpty()) {
				testOrderRequests.add(new TestOrderRequest(concept, categories.iterator().next()));
			}
		}
		return testOrderRequests;
	}
	
	@Override
	public void setTestOrderRequestDAO(final TestOrderRequestDAO testOrderRequestDAO) {
		this.testOrderRequestDAO = testOrderRequestDAO;
	}
}
