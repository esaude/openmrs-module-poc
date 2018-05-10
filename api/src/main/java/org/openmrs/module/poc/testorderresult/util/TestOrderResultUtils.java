/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc.testorderresult.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptDatatype;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Order.Action;
import org.openmrs.Provider;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.poc.api.common.util.OPENMRSUUIDs;
import org.openmrs.module.poc.pocheuristic.service.PocHeuristicService;
import org.openmrs.module.poc.testorder.util.TestOrderUtil;
import org.openmrs.module.poc.testorderresult.model.TestOrderResult;
import org.openmrs.module.poc.testorderresult.model.TestOrderResultItem;
import org.openmrs.module.poc.testrequest.model.TestRequest;
import org.openmrs.module.poc.testrequest.service.TestRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestOrderResultUtils {
	
	@Autowired
	private TestOrderUtil testOrderUtil;
	
	public TestOrderResult buildTestOrderResult(final Encounter requestEncounter, final Encounter resultEncounter,
	        final Map<Concept, Concept> mapCategoriesByTestConcept) {
		
		final Map<Order, Order> requestResultOrders = this.testOrderUtil.mergeOrders(requestEncounter.getOrders(),
		    resultEncounter.getOrders());
		
		final List<TestOrderResultItem> items = new ArrayList<>();
		if (requestResultOrders != null) {
			final PocHeuristicService pocHeuristicService = Context.getService(PocHeuristicService.class);
			
			for (final Entry<Order, Order> requestResult : requestResultOrders.entrySet()) {
				
				final Order request = requestResult.getKey();
				final Order result = requestResult.getValue();
				String value = StringUtils.EMPTY;
				String unit = StringUtils.EMPTY;
				if (!Action.NEW.equals(result.getAction())) {
					
					final Obs obs = pocHeuristicService.findObsByOrderAndConceptAndEncounter(result,
					    result.getConcept(), result.getEncounter());
					value = this.getObsValue(result);
					
					final ConceptDatatype datatype = obs.getConcept().getDatatype();
					
					if (datatype.isNumeric()) {
						unit = Context.getConceptService().getConceptNumeric(obs.getConcept().getConceptId())
						        .getUnits();
					}
				}
				items.add(new TestOrderResultItem(Action.NEW.equals(result.getAction()) ? request : result,
				        mapCategoriesByTestConcept.get(request.getConcept()), value, unit));
			}
		}
		
		if (!items.isEmpty()) {
			
			final TestOrderResultItem item = items.iterator().next();
			final Encounter encounterSeguimentoPaciente = item.getTestOrder().getEncounter();
			
			final TestOrderResult testOrderResult = new TestOrderResult();
			testOrderResult.setPatient(requestEncounter.getPatient());
			testOrderResult.setEncounterRequest(encounterSeguimentoPaciente);
			testOrderResult.setEncounterResult(resultEncounter);
			testOrderResult.setLocation(requestEncounter.getLocation());
			testOrderResult.setDateCreation(requestEncounter.getEncounterDatetime());
			testOrderResult.setCodeSequence(this.getCodeSequence(encounterSeguimentoPaciente));
			testOrderResult.setItems(items);
			testOrderResult.setUuid(encounterSeguimentoPaciente.getUuid());
			
			Provider provider = requestEncounter.getEncounterProviders().iterator().next().getProvider();
			if ((resultEncounter.getEncounterProviders() != null)
			        && !resultEncounter.getEncounterProviders().isEmpty()) {
				provider = resultEncounter.getEncounterProviders().iterator().next().getProvider();
			}
			testOrderResult.setProvider(provider);
			
			return testOrderResult;
		}
		
		return null;
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
	
	public Obs getObsFromOrder(final Order order) {
		
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
	
	private String getCodeSequence(final Encounter encounter) {
		
		final Concept sequencialConcept = Context.getConceptService().getConceptByUuid(OPENMRSUUIDs.REFERENCE_TYPE);
		final Set<Obs> allObs = encounter.getAllObs(false);
		
		for (final Obs obs : allObs) {
			
			if (sequencialConcept.equals(obs.getConcept())) {
				return obs.getValueText();
			}
		}
		return StringUtils.EMPTY;
	}
	
	public Map<Concept, Concept> getMapCategoriesByTestConcept() {
		
		final List<TestRequest> findAllTestOrderRequest = Context.getService(TestRequestService.class)
		        .findAllTestRequests();
		
		final Map<Concept, Concept> map = new HashMap<>();
		
		for (final TestRequest testOrderRequest : findAllTestOrderRequest) {
			
			map.put(testOrderRequest.getTestOrder(), testOrderRequest.getCategory());
		}
		return map;
	}
	
}
