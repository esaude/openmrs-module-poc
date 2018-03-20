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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.openmrs.Provider;
import org.openmrs.TestOrder;
import org.openmrs.api.APIException;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.poc.api.common.util.OPENMRSUUIDs;
import org.openmrs.module.poc.pocheuristic.service.PocHeuristicService;
import org.openmrs.module.poc.testorderresult.model.TestOrderRequestResult;
import org.openmrs.module.poc.testorderresult.model.TestOrderResult;
import org.openmrs.module.poc.testorderresult.model.TestOrderResultItem;
import org.openmrs.module.poc.testorderresult.util.TestOrderResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class TestOrderResultServiceImpl extends BaseOpenmrsService implements TestOrderResultService {
	
	private PocHeuristicService pocHeuristicService;
	
	private OrderService orderService;
	
	private EncounterService encounterService;
	
	private ConceptService conceptService;
	
	private ProviderService providerService;
	
	@Autowired
	private TestOrderResultUtils testOrderResultUtils;
	
	private TestOrderRequestResultService testOrderRequestResultService;
	
	@Override
	public void setTestOrderRequestResultService(final TestOrderRequestResultService testOrderRequestResultService) {
		this.testOrderRequestResultService = testOrderRequestResultService;
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
	public void setEncounterService(final EncounterService encounterService) {
		this.encounterService = encounterService;
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
	public TestOrderResult createTestOrderResult(final TestOrderResult testOrderResult) {
		
		final Encounter testRequest = this.encounterService
		        .getEncounterByUuid(testOrderResult.getEncounterRequest().getUuid());
		
		final TestOrderRequestResult requestResult = new TestOrderRequestResult(testRequest.getPatient(), testRequest);
		
		final Provider provider = this.providerService.getProviderByUuid(testOrderResult.getProvider().getUuid());
		final TestOrderRequestResult testRequestResult = this.testOrderRequestResultService
		        .saveTestRequestResult(requestResult, testOrderResult.getDateCreation());
		
		final Encounter testResult = testRequestResult.getTestOrderResult();
		
		this.setObsCreationDate(testOrderResult, testResult);
		
		final Map<Concept, Concept> mapCategoriesByConcept = this.testOrderResultUtils.getMapCategoriesByTestConcept();
		final List<Order> toVoid = new ArrayList<>();
		for (final TestOrderResultItem resultItem : testOrderResult.getItems()) {
			final Order order = this.orderService.getOrderByUuid(resultItem.getTestOrder().getUuid());
			
			try {
				
				final Concept category = this.getCategoryByTestConcept(mapCategoriesByConcept, order.getConcept());
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
	
	private Concept getCategoryByTestConcept(final Map<Concept, Concept> mapCategoriesByConcepts,
	        final Concept testConcept) {
		
		final Concept category = mapCategoriesByConcepts.get(testConcept);
		
		if (category == null) {
			throw new APIException(Context.getMessageSourceService().getMessage(
			    "poc.error.testorderitemresult.category.notFound.for.testConcept",
			    new String[] { testConcept.getDisplayString(), testConcept.getUuid() }, Context.getLocale()));
		}
		return category;
	}
	
	@Override
	public void deleteTestOrderResultItem(final TestOrderResultItem testOrder, final String reason) {
		
		final Order order = this.orderService.getOrderByUuid(testOrder.getUuid());
		final TestOrderRequestResult testRequestResult = this.testOrderRequestResultService
		        .findTestRequestResultByResultEncounter(order.getEncounter());
		
		final Encounter encounter = testRequestResult.getTestOrderRequest();
		final Obs oldObs = this.testOrderResultUtils.getObsFromOrder(order);
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
		
		final Map<Concept, Concept> categoriesByTestConcept = this.testOrderResultUtils.getMapCategoriesByTestConcept();
		final List<TestOrderRequestResult> testRequestResults = this.testOrderRequestResultService
		        .findTestRequestResultsByPatientUuid(patientUUID);
		
		final Map<Encounter, Encounter> mapEncounters = this.mergeTestRequestResult(encounters, testRequestResults);
		
		final List<TestOrderResult> resutl = new ArrayList<>();
		for (final Entry<Encounter, Encounter> keyValue : mapEncounters.entrySet()) {
			
			final TestOrderResult testOrderResult = this.testOrderResultUtils.buildTestOrderResult(keyValue.getKey(),
			    keyValue.getValue(), categoriesByTestConcept);
			
			if (testOrderResult != null) {
				resutl.add(testOrderResult);
			}
		}
		return resutl;
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
	
	@Override
	public TestOrderResultItem findTestOrderResultItemByUuid(final String uuid) {
		final Order order = this.orderService.getOrderByUuid(uuid);
		
		if (order != null) {
			
			final Map<Concept, Concept> mapCategoriesByTestConcept = this.testOrderResultUtils
			        .getMapCategoriesByTestConcept();
			
			final Concept category = mapCategoriesByTestConcept.get(order.getConcept());
			
			final TestOrderRequestResult requestResult = this.testOrderRequestResultService
			        .findTestRequestResultByResultEncounter(order.getEncounter());
			
			final TestOrderResultItem testOrderResultItem = new TestOrderResultItem(order, category,
			        this.testOrderResultUtils.getObsValue(order));
			testOrderResultItem.setParent(this.findTestOrderResultByTestRequest(requestResult.getTestOrderRequest()));
			
			return testOrderResultItem;
		}
		
		throw new APIException(Context.getMessageSourceService().getMessage(
		    "poc.error.testorderitemresult.not.found.for.uuid", new String[] { uuid }, Context.getLocale()));
	}
	
	@Override
	public TestOrderResult findTestOrderResultByTestRequest(final Encounter encounter) {
		
		final Encounter requestEncounter = this.encounterService.getEncounterByUuid(encounter.getUuid());
		final TestOrderRequestResult resquestResult = this.testOrderRequestResultService
		        .findTestRequestResultsByRequestEncounter(requestEncounter);
		
		Encounter resultEncounter = new Encounter();
		if (resquestResult != null) {
			resultEncounter = resquestResult.getTestOrderResult();
		}
		final Map<Concept, Concept> mapCategoriesByTestConcept = this.testOrderResultUtils
		        .getMapCategoriesByTestConcept();
		
		return this.testOrderResultUtils.buildTestOrderResult(requestEncounter, resultEncounter,
		    mapCategoriesByTestConcept);
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
		
		final Obs obsGroup = this.getGroupObsByTestCategory(encounter, category);
		obsGroup.setConcept(category);
		
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
	
	private Obs getGroupObsByTestCategory(final Encounter encounter, final Concept category) {
		
		final Set<Obs> allObs = encounter.getAllObs(false);
		
		for (final Obs obs : allObs) {
			
			if (obs.isObsGrouping() && obs.getConcept().equals(category)) {
				return obs;
			}
		}
		final Obs obs = new Obs();
		obs.setConcept(category);
		return obs;
	}
	
	private void generateObs(final Encounter encounter, final Order order, final TestOrderResultItem resultItem,
	        final Concept category) throws ParseException {
		
		if (!Action.NEW.equals(order.getAction())) {
			
			final Obs oldObs = this.testOrderResultUtils.getObsFromOrder(order);
			Context.getObsService().voidObs(oldObs, "retired for update value");
			Context.getObsService().voidObs(oldObs.getObsGroup(), "retired for update value");
		}
		this.createObs(encounter, order, resultItem, category);
	}
}
