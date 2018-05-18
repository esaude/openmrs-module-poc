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
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.poc.api.common.util.OPENMRSUUIDs;
import org.openmrs.module.poc.testorderresult.dao.TestOrderRequestResultDAO;
import org.openmrs.module.poc.testorderresult.model.TestOrderRequestResult;

public class TestOrderRequestResultServiceImpl extends BaseOpenmrsService implements TestOrderRequestResultService {
	
	private TestOrderRequestResultDAO testOrderRequestResultDAO;
	
	private EncounterService encounterService;
	
	@Override
	public void setTestOrderRequestResultDAO(final TestOrderRequestResultDAO testOrderRequestResultDAO) {
		
		this.testOrderRequestResultDAO = testOrderRequestResultDAO;
	}
	
	@Override
	public void setEncounterService(final EncounterService encounterService) {
		this.encounterService = encounterService;
	}
	
	@Override
	public TestOrderRequestResult saveTestRequestResult(final TestOrderRequestResult testRequestResult,
	        final Date date) {
		
		final Encounter testRequest = testRequestResult.getTestOrderRequest();
		
		final TestOrderRequestResult testRequestResultFound = this.testOrderRequestResultDAO
		        .findByRequestEncounter(testRequest, false);
		
		if (testRequestResultFound != null) {
			
			return testRequestResultFound;
		}
		
		final Encounter testResult = this.generateTestResult(testRequest, date);
		this.encounterService.saveEncounter(testResult);
		
		testRequestResult.setTestOrderResult(testResult);
		testRequestResult.setPatient(testRequest.getPatient());
		
		this.testOrderRequestResultDAO.save(testRequestResult);
		
		return testRequestResult;
	}
	
	private Encounter generateTestResult(final Encounter testRequest, final Date date) {
		final Encounter encounter = new Encounter();
		
		encounter.setEncounterType(this.getEncounterType());
		encounter.setPatient(testRequest.getPatient());
		encounter.setProvider(this.getEncounterRole(),
		    testRequest.getEncounterProviders().iterator().next().getProvider());
		encounter.setLocation(testRequest.getLocation());
		encounter.setEncounterDatetime(date);
		encounter.setForm(this.getForm());
		
		return encounter;
	}
	
	private EncounterType getEncounterType() {
		
		return this.encounterService.getEncounterTypeByUuid(OPENMRSUUIDs.MISAU_LABORATORIO_ENCOUNTER_TYPE_UUID);
	}
	
	private EncounterRole getEncounterRole() {
		
		return this.encounterService.getEncounterRoleByUuid(OPENMRSUUIDs.DEFAULT_ENCONTER_ROLE_UUID);
	}
	
	private Form getForm() {
		
		return Context.getFormService().getFormByUuid(OPENMRSUUIDs.LABORATORIO_GERAL_FORM_UUID);
	}
	
	@Override
	public TestOrderRequestResult findTestRequestResultByResultEncounter(final Encounter result) {
		return this.testOrderRequestResultDAO.findByResultEncounter(result, false);
	}
	
	@Override
	public TestOrderRequestResult findTestRequestResultsByRequestEncounter(final Encounter request) {
		
		return this.testOrderRequestResultDAO.findByRequestEncounter(request, false);
	}
	
	@Override
	public List<TestOrderRequestResult> findTestRequestResultsByPatient(final Patient patient) {
		
		return this.testOrderRequestResultDAO.findByPatientUuid(patient, false);
	}
}
