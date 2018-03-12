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
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.poc.api.common.util.OPENMRSUUIDs;
import org.openmrs.module.poc.api.pocheuristic.service.PocHeuristicService;
import org.openmrs.module.poc.testresult.dao.TestRequestResultDAO;
import org.openmrs.module.poc.testresult.model.TestRequestResult;

public class TestRequestResultServiceImpl extends BaseOpenmrsService implements TestRequestResultService {
	
	private TestRequestResultDAO testRequestResultDAO;
	
	private PocHeuristicService pocHeuristicService;
	
	private EncounterService encounterService;
	
	@Override
	public void setPocHeuristicService(final PocHeuristicService pocHeuristicService) {
		this.pocHeuristicService = pocHeuristicService;
	}
	
	@Override
	public void setTestRequestResultDAO(final TestRequestResultDAO testRequestResultDAO) {
		
		this.testRequestResultDAO = testRequestResultDAO;
	}
	
	@Override
	public void setEncounterService(final EncounterService encounterService) {
		this.encounterService = encounterService;
	}
	
	@Override
	public TestRequestResult saveTestRequestResult(final TestRequestResult testRequestResult, final Date date) {
		
		final Encounter testRequest = testRequestResult.getTestRequest();
		
		final TestRequestResult testRequestResultFound = this.testRequestResultDAO.findByRequestEncounter(testRequest,
		    false);
		
		if (testRequestResultFound != null) {
			
			return testRequestResultFound;
		}
		
		final Encounter testResult = this.generateTestResult(testRequest, date);
		Context.getEncounterService().saveEncounter(testResult);
		
		testRequestResult.setTestResult(testResult);
		testRequestResult.setPatient(testRequest.getPatient());
		
		this.testRequestResultDAO.save(testRequestResult);
		
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
		
		return Context.getEncounterService().getEncounterTypeByUuid(OPENMRSUUIDs.MISAU_LABORATORIO_ENCOUNTER_TYPE_UUID);
	}
	
	private EncounterRole getEncounterRole() {
		
		return Context.getEncounterService().getEncounterRoleByUuid(OPENMRSUUIDs.DEFAULT_ENCONTER_ROLE_UUID);
	}
	
	private Form getForm() {
		
		return Context.getFormService().getFormByUuid(OPENMRSUUIDs.LABORATORIO_GERAL_FORM_UUID);
	}
	
	@Override
	public void updateTestRequestResult(final TestRequestResult testRequestResult) {
	}
	
	@Override
	public void retireTestRequestResult(final TestRequestResult testRequestResult) {
	}
	
	@Override
	public TestRequestResult findTestRequestResultByUuid(final String uuid) {
		return null;
	}
	
	@Override
	public TestRequestResult findTestRequestResultByResultEncounter(final Encounter result) {
		return this.testRequestResultDAO.findByResultEncounter(result, false);
	}
	
	@Override
	public TestRequestResult findTestRequestResultsByRequestEncounter(final Encounter request) {
		
		return this.testRequestResultDAO.findByRequestEncounter(request, false);
	}
	
	@Override
	public List<TestRequestResult> findTestRequestResultsByPatientUuid(final String patientUuid) {
		
		return this.testRequestResultDAO.findByPatientUuid(patientUuid, false);
	}
}
