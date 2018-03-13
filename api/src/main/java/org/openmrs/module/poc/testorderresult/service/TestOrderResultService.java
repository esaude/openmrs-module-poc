/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc.testorderresult.service;

import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OpenmrsService;
import org.openmrs.api.OrderService;
import org.openmrs.api.ProviderService;
import org.openmrs.module.poc.pocheuristic.service.PocHeuristicService;
import org.openmrs.module.poc.testorderresult.model.TestOrderResult;
import org.openmrs.module.poc.testorderresult.model.TestOrderResultItem;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface TestOrderResultService extends OpenmrsService {
	
	void setPocHeuristicService(PocHeuristicService pocHeuristicService);
	
	void setOrderService(final OrderService orderService);
	
	void setEncounterService(final EncounterService encounterService);
	
	void setConceptService(final ConceptService conceptService);
	
	void setProviderService(ProviderService providerService);
	
	void setTestOrderRequestResultService(final TestOrderRequestResultService testOrderRequestResultService);
	
	TestOrderResult createTestOrderResult(TestOrderResult testOrderResult);
	
	void deleteTestOrderResultItem(TestOrderResultItem testOrderResultItem, String reason);
	
	TestOrderResult findTestOrderResultByTestRequest(Encounter request);
	
	List<TestOrderResult> findTestOrderResultsByPatient(String patientUUID);
	
	TestOrderResultItem findTestOrderResultItemByUuid(String uuid);
	
}
