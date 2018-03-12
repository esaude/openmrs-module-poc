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
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.time.DateUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptDatatype;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Order.Action;
import org.openmrs.OrderType;
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
		
		final Map<Concept, Concept> mapCategoriesByConcept = this.getMapCategoriesByTestConcept();
		final List<Order> toVoid = new ArrayList<>();
		for (final TestOrderResultItem resultItem : testOrderResult.getItems()) {
			final Order order = this.orderService.getOrderByUuid(resultItem.getTestOrder().getUuid());
			
			try {
				
				final Concept category = mapCategoriesByConcept.get(order.getConcept());
				this.generateObs(testResult, order, resultItem, category);
				
			}
			catch (final ParseException e) {
				
				throw new APIException("Error creating Obs for Order with uuid " + resultItem.getTestOrder().getUuid());
			}
			
			if (Action.NEW.equals(order.getAction())) {
				
				final Order revisedOrder = order.cloneForRevision();
				revisedOrder.setOrderer(provider);
				resultItem.setTestOrder(revisedOrder);
				testResult.addOrder(revisedOrder);
				toVoid.add(order);
			}
		}
		
		this.encounterService.saveEncounter(testResult);
		
		for (final Order order : toVoid) {
			
			this.orderService.voidOrder(order, "voided due order revision");
		}
		testOrderResult.setEncounterRequest(testRequest);
		testOrderResult.setEncounterResult(testResult);
		
		return testOrderResult;
	}
	
	@Override
	public void deleteTestOrderResultItem(final TestOrderResultItem testOrder, final String reason) {
		
		final Order order = this.orderService.getOrderByUuid(testOrder.getUuid());
		final TestRequestResult testRequestResult = this.testRequestResultService
		        .findTestRequestResultByResultEncounter(order.getEncounter());
		
		final Encounter encounter = testRequestResult.getTestRequest();
		final Obs oldObs = this.getObsFromOrder(order);
		Context.getObsService().voidObs(oldObs, reason);
		Context.getObsService().voidObs(oldObs.getObsGroup(), reason);
		
		final TestOrder newTestOrder = new TestOrder();
		newTestOrder.setConcept(order.getConcept());
		newTestOrder.setPatient(order.getPatient());
		newTestOrder.setOrderer(order.getOrderer());
		newTestOrder.setCareSetting(order.getCareSetting());
		newTestOrder.setEncounter(encounter);
		encounter.addOrder(newTestOrder);
		
		this.encounterService.saveEncounter(encounter);
		this.orderService.voidOrder(order, "voided due order revision");
	}
	
	@Override
	public List<TestOrderResult> findTestOrderResultsByPatient(final String patientUUID) {
		
		final List<Encounter> encounters = this.pocHeuristicService.findEncountersWithTestOrdersByPatient(patientUUID);
		
		final Map<Concept, Concept> categoriesByTestConcept = this.getMapCategoriesByTestConcept();
		final List<TestRequestResult> TestRequestResults = this.testRequestResultService
		        .findTestRequestResultsByPatientUuid(patientUUID);
		
		final Map<Encounter, Encounter> mapEncounters = this.mergeTestRequestResult(encounters, TestRequestResults);
		
		final List<TestOrderResult> resutl = new ArrayList<>();
		for (final Entry<Encounter, Encounter> keyValue : mapEncounters.entrySet()) {
			
			resutl.add(this.buildTestOrderResult(keyValue.getKey(), keyValue.getValue(), categoriesByTestConcept));
		}
		return resutl;
	}
	
	private Map<Encounter, Encounter> mergeTestRequestResult(final List<Encounter> encounters,
	        final List<TestRequestResult> testRequestResults) {
		
		final Map<Encounter, Encounter> result = new HashMap<>();
		
		for (final TestRequestResult testRequestResult : testRequestResults) {
			
			final Encounter encounterRequest = testRequestResult.getTestRequest();
			final Encounter encounterResult = testRequestResult.getTestResult();
			
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
	
	@Override
	public TestOrderResultItem findTestOrderResultItemByUuid(final String uuid) {
		final Order order = this.orderService.getOrderByUuid(uuid);
		
		if (order != null) {
			
			final Map<Concept, Concept> mapCategoriesByTestConcept = this.getMapCategoriesByTestConcept();
			
			final Concept category = mapCategoriesByTestConcept.get(order.getConcept());
			
			final TestRequestResult requestResult = this.testRequestResultService
			        .findTestRequestResultByResultEncounter(order.getEncounter());
			
			final TestOrderResultItem testOrderResultItem = new TestOrderResultItem(order, category,
			        this.getObsValue(order));
			testOrderResultItem.setParent(this.findTestOrderResultByTestRequest(requestResult.getTestRequest()));
			
			return testOrderResultItem;
		}
		
		throw new APIException(Context.getMessageSourceService().getMessage(
		    "poc.error.testorderitemresult.not.found.for.uuid", new String[] { uuid }, Context.getLocale()));
	}
	
	@Override
	public TestOrderResult findTestOrderResultByTestRequest(final Encounter encounter) {
		
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
		final Concept conceptCreationExamDate = this.conceptService
		        .getConceptByUuid(OPENMRSUUIDs.DATA_PEDIDO_EXAMES_LABORATORIAIS_FORM_UUID);
		final Set<Obs> allObs = encounter.getAllObs(false);
		
		for (final Obs obs : allObs) {
			
			if (conceptCreationExamDate.equals(obs.getConcept())) {
				
				if (!DateUtils.isSameDay(obs.getValueDatetime(), testOrderResult.getDateCreation())) {
					Context.getObsService().voidObs(obs, "voided due update of the date of creation Exams");
				}
				break;
			}
		}
		final Obs obsCreationDate = new Obs();
		obsCreationDate.setConcept(conceptCreationExamDate);
		obsCreationDate.setValueDatetime(testOrderResult.getDateCreation());
		encounter.addObs(obsCreationDate);
	}
	
	private void createObs(final Encounter encounter, final Order order, final TestOrderResultItem resultItem,
	        final Concept category) throws ParseException {
		
		final Obs obsGroup = new Obs();
		obsGroup.setConcept(category);
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
	
	private void generateObs(final Encounter encounter, final Order order, final TestOrderResultItem resultItem,
	        final Concept category) throws ParseException {
		
		if (!Action.NEW.equals(order.getAction())) {
			
			final Obs oldObs = this.getObsFromOrder(order);
			Context.getObsService().voidObs(oldObs, "retired for update value");
			Context.getObsService().voidObs(oldObs.getObsGroup(), "retired for update value");
		}
		this.createObs(encounter, order, resultItem, category);
	}
	
	private TestOrderResult buildTestOrderResult(final Encounter requestEncounter, final Encounter resultEncounter,
	        final Map<Concept, Concept> mapCategoriesByTestConcept) {
		
		final Set<Order> orders = this.mergeOrders(requestEncounter.getOrders(), resultEncounter.getOrders());
		
		final List<TestOrderResultItem> items = new ArrayList<>();
		for (final Order order : orders) {
			
			if (OrderType.TEST_ORDER_TYPE_UUID.equals(order.getOrderType().getUuid()) && !order.isVoided()) {
				
				String value = "";
				if (!Action.NEW.equals(order.getAction()) && OPENMRSUUIDs.MISAU_LABORATORIO_ENCOUNTER_TYPE_UUID
				        .equals(order.getEncounter().getEncounterType().getUuid())) {
					
					value = this.getObsValue(order);
				}
				
				items.add(new TestOrderResultItem(order, mapCategoriesByTestConcept.get(order.getConcept()), value));
			}
		}
		if (!items.isEmpty()) {
			
			Encounter encounterSeguimentoPaciente;
			if (OPENMRSUUIDs.MISAU_LABORATORIO_ENCOUNTER_TYPE_UUID
			        .equals(requestEncounter.getEncounterType().getUuid())) {
				final TestRequestResult testRequestResult = this.testRequestResultService
				        .findTestRequestResultByResultEncounter(requestEncounter);
				encounterSeguimentoPaciente = testRequestResult.getTestRequest();
			} else {
				encounterSeguimentoPaciente = requestEncounter;
			}
			
			final TestOrderResult testOrder = new TestOrderResult();
			testOrder.setPatient(requestEncounter.getPatient());
			testOrder.setEncounterRequest(encounterSeguimentoPaciente);
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
		final Map<Concept, Order> map = new HashMap<>();
		
		for (final Order orderRequest : requestOrders) {
			if (!orderRequest.isVoided()) {
				map.put(orderRequest.getConcept(), orderRequest);
			}
		}
		
		for (final Order orderResult : resultOrders) {
			if (!orderResult.isVoided()) {
				map.put(orderResult.getConcept(), orderResult);
			}
		}
		
		for (final Entry<Concept, Order> mapConceptOrder : map.entrySet()) {
			
			result.add(mapConceptOrder.getValue());
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
