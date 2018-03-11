/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc.testresult.service;

import java.util.Date;
import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.poc.api.pocheuristic.service.PocHeuristicService;
import org.openmrs.module.poc.testresult.dao.TestRequestResultDAO;
import org.openmrs.module.poc.testresult.model.TestRequestResult;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface TestRequestResultService extends OpenmrsService {
	
	void setTestRequestResultDAO(TestRequestResultDAO testRequestResultDAO);
	
	void setPocHeuristicService(PocHeuristicService pocHeuristicService);
	
	void setEncounterService(final EncounterService encounterService);
	
	TestRequestResult saveTestRequestResult(TestRequestResult testRequestResult, Date date);
	
	void updateTestRequestResult(TestRequestResult testRequestResult);
	
	void retireTestRequestResult(TestRequestResult testRequestResult);
	
	TestRequestResult findTestRequestResultByUuid(String uuid);
	
	TestRequestResult findTestRequestResultByResultEncounter(Encounter result);
	
	TestRequestResult findTestRequestResultsByRequestEncounter(Encounter request);
	
	List<TestRequestResult> findTestRequestResultsByPatientUuid(String patientUuid);
	
}
