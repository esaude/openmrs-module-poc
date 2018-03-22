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

import java.util.Date;
import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.poc.testorderresult.dao.TestOrderRequestResultDAO;
import org.openmrs.module.poc.testorderresult.model.TestOrderRequestResult;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface TestOrderRequestResultService extends OpenmrsService {
	
	void setTestOrderRequestResultDAO(TestOrderRequestResultDAO testOrderRequestResultDAO);
	
	void setEncounterService(final EncounterService encounterService);
	
	TestOrderRequestResult saveTestRequestResult(TestOrderRequestResult testRequestResult, Date date);
	
	TestOrderRequestResult findTestRequestResultByResultEncounter(Encounter result);
	
	TestOrderRequestResult findTestRequestResultsByRequestEncounter(Encounter request);
	
	List<TestOrderRequestResult> findTestRequestResultsByPatientUuid(String patientUuid);
}
