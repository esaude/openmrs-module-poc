/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc.api.testorder.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.CareSetting;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.TestOrder;
import org.openmrs.api.APIException;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.poc.api.common.service.POCDbSessionManager;
import org.openmrs.module.poc.api.common.util.OPENMRSUUIDs;
import org.openmrs.module.poc.api.pocheuristic.service.PocHeuristicService;
import org.openmrs.module.poc.api.testorder.model.TestOrderItem;
import org.openmrs.module.poc.api.testorder.model.TestOrderPOC;
import org.openmrs.module.poc.api.testorder.validation.TestOrderRequestValidator;
import org.openmrs.module.poc.api.testrequest.model.TestRequest;
import org.openmrs.module.poc.api.testrequest.service.TestRequestService;
import org.openmrs.module.poc.testresult.model.TestRequestResult;
import org.openmrs.module.poc.testresult.service.TestRequestResultService;
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
	
	private PersonService personService;
	
	private ProviderService providerService;
	
	private POCDbSessionManager pOCDbSessionManager;
	
	private TestRequestResultService testRequestResultService;
	
	@Autowired
	private TestOrderRequestValidator testOrderRequestValidator;
	
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
	public void setPersonService(final PersonService personService) {
		this.personService = personService;
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
			
			// final TestOrderPOC existingTestOrder =
			// this.buildTestOrder(encounter,
			// this.getMapCategoriesByTestConcept());
			//
			// if
			// (!TestOrderPOC.STATUS.NEW.equals(existingTestOrder.getStatus()))
			// {
			//
			// throw new APIException(Context.getMessageSourceService()
			// .getMessage("poc.error.testorderitem.cannot.be.created.due.stage.results"));
			// }
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
				
				// this.createObs(encounter, order, orderItem);
			}
			
			this.encounterService.saveEncounter(encounter);
			testOrderPOC.setEncounter(encounter);
		}
		finally {
			
			this.pOCDbSessionManager.setAutoFlushMode();
			// Context.flushSession();
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
		
		final Map<Concept, Concept> mapCategoriesByTestConcept = this.getMapCategoriesByTestConcept();
		final List<Encounter> encounters = this.pocHeuristicService.findEncountersWithTestOrdersByPatient(patientUUID);
		final List<TestOrderPOC> result = new ArrayList<>();
		
		for (final Encounter encounter : encounters) {
			
			final TestOrderPOC testOrder = this.buildTestOrder(encounter, mapCategoriesByTestConcept);
			if (testOrder != null) {
				result.add(testOrder);
			}
		}
		return result;
	}
	
	@Override
	public Encounter findLastEncounterByPatientAndEncounterTypeAndLocationAndDateAndStatus(final Patient patient,
	        final EncounterType encounterType, final Location location, final Date encounterDateTime)
	        throws APIException {
		
		return this.pocHeuristicService.findLastEncounterByPatientAndEncounterTypeAndLocationAndDateAndStatus(patient,
		    encounterType, location, encounterDateTime);
	}
	
	@Override
	public TestOrderPOC findTestOrderByEncounter(final Encounter encounter) {
		final Encounter encounterByUuid = this.encounterService.getEncounterByUuid(encounter.getUuid());
		final Map<Concept, Concept> mapCategories = this.getMapCategoriesByTestConcept();
		
		final TestOrderPOC testOrder = this.buildTestOrder(encounterByUuid, mapCategories);
		
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
			
			final Map<Concept, Concept> mapCategoriesByTestConcept = this.getMapCategoriesByTestConcept();
			
			final Concept category = mapCategoriesByTestConcept.get(order.getConcept());
			
			final TestOrderItem testOrderItem = new TestOrderItem((TestOrder) order, category);
			testOrderItem.setParent(this.findTestOrderByEncounter(order.getEncounter()));
			
			return testOrderItem;
			
		}
		
		throw new APIException(Context.getMessageSourceService()
		        .getMessage("poc.error.testorderitem.not.found.for.uuid", new String[] { uuid }, Context.getLocale()));
	}
	
	private TestOrderPOC buildTestOrder(final Encounter encounter,
	        final Map<Concept, Concept> mapCategoriesByTestConcept) {
		
		Encounter testResult = new Encounter();
		final TestRequestResult testRequestResult = this.testRequestResultService
		        .findTestRequestResultsByRequestEncounter(encounter);
		
		if (testRequestResult != null) {
			testResult = testRequestResult.getTestResult();
		}
		final Set<Order> orders = this.mergeOrders(encounter.getOrders(), testResult.getOrders());
		
		if ((orders != null) && !orders.isEmpty()) {
			
			final List<TestOrderItem> items = new ArrayList<>();
			for (final Order order : orders) {
				
				if (OrderType.TEST_ORDER_TYPE_UUID.equals(order.getOrderType().getUuid()) && !order.isVoided()) {
					items.add(new TestOrderItem((TestOrder) order, mapCategoriesByTestConcept.get(order.getConcept())));
				}
			}
			if (!items.isEmpty()) {
				
				final TestOrderPOC testOrder = new TestOrderPOC();
				testOrder.setPatient(encounter.getPatient());
				testOrder.setEncounter(encounter);
				testOrder.setProvider(encounter.getEncounterProviders().iterator().next().getProvider());
				testOrder.setLocation(encounter.getLocation());
				testOrder.setDateCreation(encounter.getEncounterDatetime());
				testOrder.setTestOrderItems(items);
				
				return testOrder;
			}
		}
		
		return null;
	}
	
	private Set<Order> mergeOrders(final Set<Order> requestOrders, final Set<Order> resultOrders) {
		
		final Set<Order> result = new LinkedHashSet<>();
		
		final Map<Concept, Order> mapRequests = new HashMap<>();
		final Map<Concept, Order> mapResults = new HashMap<>();
		
		for (final Order orderRequest : requestOrders) {
			mapRequests.put(orderRequest.getConcept(), orderRequest);
		}
		
		for (final Order orderResult : resultOrders) {
			mapResults.put(orderResult.getConcept(), orderResult);
		}
		
		for (final Concept order : mapRequests.keySet()) {
			
			if (mapResults.containsKey(order)) {
				
				result.add(mapResults.get(order));
			} else {
				
				result.add(mapRequests.get(order));
			}
		}
		
		return result;
	}
	
	private Map<Concept, Concept> getMapCategoriesByTestConcept() {
		
		final List<TestRequest> findAllTestOrderRequest = Context.getService(TestRequestService.class)
		        .findAllTestOrderRequest(Context.getLocale());
		
		final Map<Concept, Concept> map = new HashMap<>();
		
		for (final TestRequest testOrderRequest : findAllTestOrderRequest) {
			
			map.put(testOrderRequest.getTestOrder(), testOrderRequest.getCategory());
		}
		
		return map;
	}
	
	private Encounter getEncounterByRules(final Patient patient, final Provider provider, final Location location,
	        final Date encounterDate) {
		
		final EncounterType encounterType = this.findSeguimentoPacienteEncounterTypeByPatientAge(patient);
		
		try {
			return this.findLastEncounterByPatientAndEncounterTypeAndLocationAndDateAndStatus(patient, encounterType,
			    location, encounterDate);
		}
		catch (final APIException e) {
			final EncounterRole encounterRole = this.getEncounterRole();
			
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
	
	private EncounterRole getEncounterRole() {
		
		return this.encounterService.getEncounterRoleByUuid(OPENMRSUUIDs.DEFAULT_ENCONTER_ROLE_UUID);
	}
	
	@Override
	public void setTestRequestResultService(final TestRequestResultService testRequestResultService) {
		this.testRequestResultService = testRequestResultService;
	}
	
}
