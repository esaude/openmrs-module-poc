/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc.testorder.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openmrs.CareSetting;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.TestOrder;
import org.openmrs.api.APIException;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.poc.api.common.service.POCDbSessionManager;
import org.openmrs.module.poc.api.common.util.OPENMRSUUIDs;
import org.openmrs.module.poc.pocheuristic.service.PocHeuristicService;
import org.openmrs.module.poc.testorder.model.TestOrderItem;
import org.openmrs.module.poc.testorder.model.TestOrderPOC;
import org.openmrs.module.poc.testorder.util.TestOrderUtil;
import org.openmrs.module.poc.testorder.validation.TestOrderRequestValidator;
import org.openmrs.module.poc.testorderresult.model.TestOrderRequestResult;
import org.openmrs.module.poc.testorderresult.service.TestOrderRequestResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional(noRollbackFor = APIException.class)
public class TestOrderServiceImpl extends BaseOpenmrsService implements TestOrderService {
	
	private PocHeuristicService pocHeuristicService;
	
	private OrderService orderService;
	
	private PatientService patientService;
	
	private EncounterService encounterService;
	
	private LocationService locationService;
	
	private ConceptService conceptService;
	
	private ProviderService providerService;
	
	private POCDbSessionManager pOCDbSessionManager;
	
	private TestOrderRequestResultService testOrderRequestResultService;
	
	@Autowired
	private TestOrderRequestValidator testOrderRequestValidator;
	
	@Autowired
	private TestOrderUtil testOrderUtil;
	
	@Override
	public void setPocHeuristicService(final PocHeuristicService pocHeuristicService) {
		this.pocHeuristicService = pocHeuristicService;
	}
	
	@Override
	public void setOrderService(final OrderService orderService) {
		this.orderService = orderService;
	}
	
	@Override
	public void setPatientService(final PatientService patientService) {
		this.patientService = patientService;
	}
	
	@Override
	public void setEncounterService(final EncounterService encounterService) {
		this.encounterService = encounterService;
	}
	
	@Override
	public void setLocationService(final LocationService locationService) {
		this.locationService = locationService;
	}
	
	@Override
	public void setConceptService(final ConceptService conceptService) {
		this.conceptService = conceptService;
	}
	
	@Override
	public void setProviderService(final ProviderService providerService) {
		this.providerService = providerService;
	}
	
	@Override
	public void setPOCDbSessionManager(final POCDbSessionManager pOCDbSessionManager) {
		this.pOCDbSessionManager = pOCDbSessionManager;
	}
	
	@Override
	public void setTestOrderRequestResultService(final TestOrderRequestResultService testOrderRequestResultService) {
		this.testOrderRequestResultService = testOrderRequestResultService;
	}
	
	@Override
	public TestOrderPOC createTestOder(final TestOrderPOC testOrderPOC) {
		
		try {
			
			// workaround to controll the hibernate sessions commits
			this.pOCDbSessionManager.setManualFlushMode();
			
			this.testOrderRequestValidator.validate(testOrderPOC);
			
			final Patient patient = this.patientService.getPatientByUuid(testOrderPOC.getPatient().getUuid());
			
			final Provider provider = this.providerService.getProviderByUuid(testOrderPOC.getProvider().getUuid());
			final CareSetting careSetting = this.orderService
			        .getCareSettingByUuid(OPENMRSUUIDs.OUT_PUT_PATIENT_CARE_SETTINGS_CARESETTINGS_UUID);
			final Location location = this.locationService.getLocationByUuid(testOrderPOC.getLocation().getUuid());
			
			final Encounter encounter = this.getEncounterByRules(patient, provider, location,
			    testOrderPOC.getDateCreation());
			
			for (final TestOrderItem orderItem : testOrderPOC.getTestOrderItems()) {
				
				final Concept concept = this.conceptService
				        .getConceptByUuid(orderItem.getTestOrder().getConcept().getUuid());
				
				final TestOrder order = new TestOrder();
				order.setConcept(concept);
				order.setPatient(testOrderPOC.getPatient());
				order.setOrderer(provider);
				order.setCareSetting(careSetting);
				order.setEncounter(encounter);
				encounter.addOrder(order);
			}
			
			this.encounterService.saveEncounter(encounter);
			testOrderPOC.setEncounter(encounter);
		}
		finally {
			// Context.flushSession();
			this.pOCDbSessionManager.setAutoFlushMode();
		}
		
		return testOrderPOC;
	}
	
	@Override
	public void deleteTestOrderItem(final TestOrderItem testOrder, final String reason) {
		
		final Order orderByUuid = this.orderService.getOrderByUuid(testOrder.getUuid());
		this.orderService.voidOrder(orderByUuid, reason);
	}
	
	@Override
	public EncounterType findSeguimentoPacienteEncounterTypeByPatientAge(final Patient patient) {
		if (patient != null) {
			return Context.getEncounterService()
			        .getEncounterTypeByUuid(patient.getAge() < 15 ? OPENMRSUUIDs.ARV_FOLLOW_UP_CHILD_ENCOUNTER_TYPE_UUID
			                : OPENMRSUUIDs.ARV_FOLLOW_UP_ADULT_ENCOUNTER_TYPE_UUID);
		}
		throw new APIException(
		        Context.getMessageSourceService().getMessage("poc.error.encountetype.notfound.for.non.given.patient"));
	}
	
	@Override
	public List<TestOrderPOC> findTestOrdersByPatient(final String patientUUID) {
		
		final Map<Concept, Concept> mapCategoriesByTestConcept = this.testOrderUtil.getMapCategoriesByTestConcept();
		final List<Encounter> encounters = this.pocHeuristicService.findEncountersWithTestOrdersByPatient(patientUUID);
		
		final List<TestOrderRequestResult> testRequestResults = this.testOrderRequestResultService
		        .findTestRequestResultsByPatientUuid(patientUUID);
		
		final List<TestOrderPOC> result = new ArrayList<>();
		
		final Map<Encounter, Encounter> mapEncounters = this.mergeTestRequestResult(encounters, testRequestResults);
		
		for (final Entry<Encounter, Encounter> keyValue : mapEncounters.entrySet()) {
			
			final TestOrderPOC testOrder = this.testOrderUtil.buildTestOrder(keyValue.getKey(), keyValue.getValue(),
			    mapCategoriesByTestConcept);
			
			if (testOrder != null) {
				result.add(testOrder);
			}
		}
		
		return result;
	}
	
	@Override
	public Encounter findLastEncounterByPatientAndEncounterTypeAndLocationAndDateAndStatus(final Patient patient,
	        final EncounterType encounterType, final Location location, final Date encounterDateTime) {
		
		return this.pocHeuristicService.findLastEncounterByPatientAndEncounterTypeAndLocationAndDateAndStatus(patient,
		    encounterType, location, encounterDateTime);
	}
	
	@Override
	public TestOrderPOC findTestOrderByEncounter(final Encounter encounter) {
		final Encounter encounterByUuid = this.encounterService.getEncounterByUuid(encounter.getUuid());
		final Map<Concept, Concept> mapCategories = this.testOrderUtil.getMapCategoriesByTestConcept();
		
		final TestOrderRequestResult testRequestResult = this.testOrderRequestResultService
		        .findTestRequestResultsByRequestEncounter(encounterByUuid);
		
		Encounter resultEncounter = new Encounter();
		if (testRequestResult != null) {
			resultEncounter = testRequestResult.getTestOrderResult();
		}
		final TestOrderPOC testOrder = this.testOrderUtil.buildTestOrder(encounterByUuid, resultEncounter,
		    mapCategories);
		
		if (testOrder != null) {
			return testOrder;
		}
		throw new APIException(
		        Context.getMessageSourceService().getMessage("poc.error.testorder.not.found.for.encounter",
		            new String[] { encounterByUuid.getEncounterDatetime().toString() }, Context.getLocale()));
	}
	
	@Override
	public TestOrderItem findTestOrderItemByUuid(final String uuid) {
		
		final Order order = this.orderService.getOrderByUuid(uuid);
		
		if (order != null) {
			
			final Map<Concept, Concept> mapCategoriesByTestConcept = this.testOrderUtil.getMapCategoriesByTestConcept();
			
			final Concept category = mapCategoriesByTestConcept.get(order.getConcept());
			
			final TestOrderItem testOrderItem = new TestOrderItem((TestOrder) order, category);
			testOrderItem.setParent(this.findTestOrderByEncounter(order.getEncounter()));
			
			return testOrderItem;
		}
		
		throw new APIException(Context.getMessageSourceService()
		        .getMessage("poc.error.testorderitem.not.found.for.uuid", new String[] { uuid }, Context.getLocale()));
	}
	
	private Map<Encounter, Encounter> mergeTestRequestResult(final List<Encounter> encounters,
	        final List<TestOrderRequestResult> testRequestResults) {
		
		final Map<Encounter, Encounter> result = new HashMap<>();
		
		for (final TestOrderRequestResult testRequestResult : testRequestResults) {
			
			final Encounter encounterRequest = testRequestResult.getTestOrderRequest();
			final Encounter encounterResult = testRequestResult.getTestOrderResult();
			
			if (encounters.contains(encounterRequest) && encounters.contains(encounterResult)) {
				result.put(encounterRequest, encounterResult);
				encounters.remove(encounterRequest);
				encounters.remove(encounterResult);
				continue;
			} else if (encounters.contains(encounterRequest)) {
				result.put(encounterRequest, new Encounter());
				encounters.remove(encounterRequest);
				continue;
			} else if (encounters.contains(encounterResult)) {
				result.put(encounterResult, new Encounter());
				result.remove(encounterResult);
				continue;
			}
		}
		for (final Encounter request : encounters) {
			
			result.put(request, new Encounter());
		}
		return result;
	}
	
	private Encounter getEncounterByRules(final Patient patient, final Provider provider, final Location location,
	        final Date encounterDate) {
		
		final EncounterType encounterType = this.findSeguimentoPacienteEncounterTypeByPatientAge(patient);
		
		try {
			return this.findLastEncounterByPatientAndEncounterTypeAndLocationAndDateAndStatus(patient, encounterType,
			    location, encounterDate);
		}
		catch (final APIException e) {
			final EncounterRole encounterRole = this.encounterService
			        .getEncounterRoleByUuid(OPENMRSUUIDs.DEFAULT_ENCONTER_ROLE_UUID);
			
			return this.createEncounter(patient, provider, location, encounterDate, encounterType, encounterRole);
		}
	}
	
	private Encounter createEncounter(final Patient patient, final Provider provider, final Location location,
	        final Date encounterDateTime, final EncounterType encounterType, final EncounterRole encounterRole) {
		final Encounter encounter = new Encounter();
		
		encounter.setEncounterType(encounterType);
		encounter.setPatient(patient);
		encounter.setProvider(encounterRole, provider);
		encounter.setLocation(location);
		encounter.setEncounterDatetime(encounterDateTime);
		
		return encounter;
	}
	
}
