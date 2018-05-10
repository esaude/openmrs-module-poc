/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc.testorder.util;

import java.util.ArrayList;
import java.util.HashMap;
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
import org.openmrs.api.context.Context;
import org.openmrs.module.poc.api.common.util.OPENMRSUUIDs;
import org.openmrs.module.poc.pocheuristic.service.PocHeuristicService;
import org.openmrs.module.poc.testorder.model.TestOrderItem;
import org.openmrs.module.poc.testorder.model.TestOrderPOC;
import org.openmrs.module.poc.testrequest.model.TestRequest;
import org.openmrs.module.poc.testrequest.service.TestRequestService;
import org.springframework.stereotype.Component;

@Component
public class TestOrderUtil {
	
	public TestOrderPOC buildTestOrder(final Encounter requestEncounter, final Encounter resultEncounter,
	        final Map<Concept, Concept> mapCategoriesByTestConcept) {
		
		final Map<Order, Order> requestResultMap = this.mergeOrders(requestEncounter.getOrders(),
		    resultEncounter.getOrders());
		
		if (!requestResultMap.isEmpty()) {
			
			final List<TestOrderItem> items = new ArrayList<>();
			
			final PocHeuristicService pocHeuristicService = Context.getService(PocHeuristicService.class);
			
			for (final Entry<Order, Order> requestResult : requestResultMap.entrySet()) {
				
				final Order request = requestResult.getKey();
				final Order result = requestResult.getValue();
				
				String value = null;
				String unit = null;
				if (!Action.NEW.equals(result.getAction())) {
					
					final Obs obs = pocHeuristicService.findObsByOrderAndConceptAndEncounter(result,
					    result.getConcept(), result.getEncounter());
					value = obs.getValueAsString(Context.getLocale());
					
					final ConceptDatatype datatype = obs.getConcept().getDatatype();
					
					if (datatype.isNumeric()) {
						unit = Context.getConceptService().getConceptNumeric(obs.getConcept().getConceptId())
						        .getUnits();
					}
				}
				
				items.add(new TestOrderItem(request, mapCategoriesByTestConcept.get(request.getConcept()),
				        result.getAction(), value, unit));
			}
			
			if (!items.isEmpty()) {
				
				final TestOrderPOC testOrder = new TestOrderPOC();
				testOrder.setPatient(requestEncounter.getPatient());
				testOrder.setEncounter(requestEncounter);
				testOrder.setProvider(requestEncounter.getEncounterProviders().iterator().next().getProvider());
				testOrder.setLocation(requestEncounter.getLocation());
				testOrder.setDateCreation(requestEncounter.getEncounterDatetime());
				testOrder.setCodeSequence(this.getCodeSequence(requestEncounter));
				testOrder.setTestOrderItems(items);
				testOrder.setUuid(requestEncounter.getUuid());
				
				return testOrder;
			}
		}
		
		return null;
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
	
	private String getCodeSequence(final Encounter encounter) {
		
		final Concept codeSequenceConcept = Context.getConceptService().getConceptByUuid(OPENMRSUUIDs.REFERENCE_TYPE);
		
		final Set<Obs> allObs = encounter.getAllObs(false);
		for (final Obs obs : allObs) {
			
			if (codeSequenceConcept.equals(obs.getConcept())) {
				return obs.getValueText();
				
			}
		}
		return StringUtils.EMPTY;
	}
	
	public Map<Order, Order> mergeOrders(final Set<Order> requestOrders, final Set<Order> resultOrders) {
		
		final Map<Order, Order> result = new HashMap<>();
		
		for (final Order requestOrder : requestOrders) {
			
			for (final Order resultOrder : resultOrders) {
				if (requestOrder.getConcept().equals(resultOrder.getConcept())) {
					if (!requestOrder.isVoided() && !resultOrder.isVoided()) {
						result.put(requestOrder, resultOrder);
					}
				}
			}
		}
		for (final Order requestOrder : requestOrders) {
			
			if (!result.containsKey(requestOrder) && !requestOrder.isVoided()) {
				
				result.put(requestOrder, new Order());
			}
		}
		return result;
	}
}
