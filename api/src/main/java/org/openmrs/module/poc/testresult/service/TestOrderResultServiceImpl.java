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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.ConceptDatatype;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Order.Action;
import org.openmrs.OrderType;
import org.openmrs.Provider;
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
import org.openmrs.module.poc.api.testrequest.model.TestRequest;
import org.openmrs.module.poc.api.testrequest.service.TestRequestService;
import org.openmrs.module.poc.testresult.model.TestOrderResult;
import org.openmrs.module.poc.testresult.model.TestOrderResultItem;
import org.openmrs.module.poc.testresult.model.TestRequestResult;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class TestOrderResultServiceImpl extends BaseOpenmrsService implements TestOrderResultService {
	
	private PocHeuristicService pocHeuristicService;
	
	private OrderService orderService;
	
	private PatientService patientService;
	
	private EncounterService encounterService;
	
	private LocationService locationService;
	
	private ConceptService conceptService;
	
	private PersonService personService;
	
	private ProviderService providerService;
	
	private TestRequestResultService testRequestResultService;
	
	private POCDbSessionManager pOCDbSessionManager;
	
	@Override
	public void setTestRequestResultService(final TestRequestResultService testRequestResultService) {
		this.testRequestResultService = testRequestResultService;
	}
	
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
	public TestOrderResult createTestOrderResult(final TestOrderResult testOrderResult) {
		
		final Encounter testRequest = this.encounterService
		        .getEncounterByUuid(testOrderResult.getEncounterRequest().getUuid());
		
		final TestRequestResult requestResult = new TestRequestResult(testRequest.getPatient(), testRequest);
		
		final Provider provider = this.providerService.getProviderByUuid(testOrderResult.getProvider().getUuid());
		final TestRequestResult testRequestResult = this.testRequestResultService.saveTestRequestResult(requestResult,
		    testOrderResult.getDateCreation());
		
		final Encounter testResult = testRequestResult.getTestResult();
		
		this.setObsCreationDate(testOrderResult, testResult);
		
		for (final TestOrderResultItem resultItem : testOrderResult.getItems()) {
			final Order order = this.orderService.getOrderByUuid(resultItem.getTestOrder().getUuid());
			
			try {
				
				this.generateObs(testResult, order, resultItem);
				
			}
			catch (final ParseException e) {
				
				throw new APIException("Error creating Obs for Order with uuid " + resultItem.getTestOrder().getUuid());
			}
			
			if (Action.NEW.equals(order.getAction())) {
				
				final Order revisedOrder = order.cloneForRevision();
				revisedOrder.setOrderer(provider);
				resultItem.setTestOrder(revisedOrder);
				testResult.addOrder(revisedOrder);
			}
		}
		
		this.encounterService.saveEncounter(testResult);
		
		testOrderResult.setEncounterRequest(testRequest);
		testOrderResult.setEncounterResult(testResult);
		
		return testOrderResult;
	}
	
	@Override
	public void deleteTestOrderResultItem(final TestOrderResultItem testOrder, final String reason) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public List<TestOrderResult> findTestOrderResultsByPatient(final String patientUUID) {
		
		final Map<Concept, Concept> categoriesByTestConcept = this.getMapCategoriesByTestConcept();
		
		final List<TestRequestResult> TestRequestResults = this.testRequestResultService
		        .findTestRequestResultsByPatientUuid(patientUUID);
		
		final List<TestOrderResult> resutl = new ArrayList<>();
		for (final TestRequestResult testRequestResult : TestRequestResults) {
			
			resutl.add(this.buildTestOrderResult(testRequestResult.getTestRequest(), testRequestResult.getTestResult(),
			    categoriesByTestConcept));
		}
		return resutl;
	}
	
	@Override
	public TestOrderResultItem findTestOrderResultItemByUuid(final String uuid) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public TestOrderResult findTestOrderByTestRequest(final Encounter encounter) {
		
		final Encounter requestEncounter = this.encounterService.getEncounterByUuid(encounter.getUuid());
		final TestRequestResult resquestResult = this.testRequestResultService
		        .findTestRequestResultsByRequestEncounter(requestEncounter);
		
		Encounter resultEncounter = new Encounter();
		if (resquestResult != null) {
			resultEncounter = resquestResult.getTestResult();
		}
		final Map<Concept, Concept> mapCategoriesByTestConcept = this.getMapCategoriesByTestConcept();
		
		return this.buildTestOrderResult(requestEncounter, resultEncounter, mapCategoriesByTestConcept);
	}
	
	private void setObsCreationDate(final TestOrderResult testOrderResult, final Encounter encounter) {
		final Obs obsCreationDate = new Obs();
		obsCreationDate.setConcept(
		        this.conceptService.getConceptByUuid(OPENMRSUUIDs.DATA_PEDIDO_EXAMES_LABORATORIAIS_FORM_UUID));
		obsCreationDate.setValueDatetime(testOrderResult.getDateCreation());
		encounter.addObs(obsCreationDate);
	}
	
	private void createObs(final Encounter encounter, final Order order, final TestOrderResultItem resultItem)
	        throws ParseException {
		
		final Obs obsGroup = new Obs();
		obsGroup.setConcept(this.conceptService.getConceptByUuid(resultItem.getCategory().getUuid()));
		obsGroup.setOrder(order);
		
		final Obs obsTest = new Obs();
		obsTest.setConcept(order.getConcept());
		obsTest.setOrder(order);
		
		final ConceptDatatype datatype = order.getConcept().getDatatype();
		if (datatype.isCoded()) {
			obsTest.setValueCoded(this.conceptService.getConceptByUuid(resultItem.getValue()));
		} else {
			obsTest.setValueAsString(resultItem.getValue());
		}
		obsGroup.addGroupMember(obsTest);
		encounter.addObs(obsGroup);
	}
	
	private void generateObs(final Encounter encounter, final Order order, final TestOrderResultItem resultItem)
	        throws ParseException {
		
		if (!Action.NEW.equals(order.getAction())) {
			
			final Obs oldObs = this.getObsFromOrder(order);
			Context.getObsService().voidObs(oldObs, "retired for update value");
			Context.getObsService().voidObs(oldObs.getObsGroup(), "retired for update value");
		}
		this.createObs(encounter, order, resultItem);
	}
	
	private TestOrderResult buildTestOrderResult(final Encounter requestEncounter, final Encounter resultEncounter,
	        final Map<Concept, Concept> mapCategoriesByTestConcept) {
		
		final Set<Order> orders = this.mergeOrders(requestEncounter.getOrders(), resultEncounter.getOrders());
		
		final List<TestOrderResultItem> items = new ArrayList<>();
		for (final Order order : orders) {
			
			if (OrderType.TEST_ORDER_TYPE_UUID.equals(order.getOrderType().getUuid()) && order.isActive()) {
				
				String value = "";
				if (!Action.NEW.equals(order.getAction())) {
					
					value = this.getObsValue(order);
				}
				
				items.add(new TestOrderResultItem(order, mapCategoriesByTestConcept.get(order.getConcept()), value));
			}
		}
		if (!items.isEmpty()) {
			
			final TestOrderResult testOrder = new TestOrderResult();
			testOrder.setPatient(requestEncounter.getPatient());
			testOrder.setEncounterRequest(requestEncounter);
			testOrder.setEncounterResult(resultEncounter);
			testOrder.setLocation(requestEncounter.getLocation());
			testOrder.setDateCreation(requestEncounter.getEncounterDatetime());
			testOrder.setItems(items);
			
			Provider provider = requestEncounter.getEncounterProviders().iterator().next().getProvider();
			if ((resultEncounter.getEncounterProviders() != null)
			        && !resultEncounter.getEncounterProviders().isEmpty()) {
				provider = resultEncounter.getEncounterProviders().iterator().next().getProvider();
			}
			testOrder.setProvider(provider);
			
			return testOrder;
		}
		
		return null;
	}
	
	private Obs getObsFromOrder(final Order order) {
		
		final Concept concept = order.getConcept();
		final Encounter encounter = order.getEncounter();
		final Set<Obs> allObs = encounter.getAllObs(false);
		
		for (final Obs obs : allObs) {
			
			if (obs.getConcept().equals(concept)) {
				return obs;
			}
		}
		throw new APIException(" No Obs found for the Exame with order uuid " + order.getUuid() + " and concept uuid "
		        + concept.getUuid());
	}
	
	private String getObsValue(final Order order) {
		
		final Concept concept = order.getConcept();
		final ConceptDatatype datatype = concept.getDatatype();
		
		final Obs obs = this.getObsFromOrder(order);
		if (obs.getConcept().equals(concept)) {
			
			if (datatype.isBoolean()) {
				return String.valueOf(obs.getValueAsBoolean());
			}
			
			if (datatype.isCoded()) {
				
				return obs.getValueCoded().getUuid();
			}
			
			if (datatype.isNumeric()) {
				
				return String.valueOf(obs.getValueNumeric());
			}
			if (datatype.isDateTime()) {
				
				return String.valueOf(obs.getValueDatetime().toString());
			}
			
			if (datatype.isText()) {
				return obs.getValueText();
			}
		}
		throw new APIException(" No result value found for the Exame with order uuid " + order.getUuid()
		        + " and concept uuid " + concept.getUuid());
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
}
