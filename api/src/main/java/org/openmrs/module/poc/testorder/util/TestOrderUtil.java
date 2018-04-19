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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.api.context.Context;
import org.openmrs.module.poc.api.common.util.OPENMRSUUIDs;
import org.openmrs.module.poc.testorder.model.TestOrderItem;
import org.openmrs.module.poc.testorder.model.TestOrderPOC;
import org.openmrs.module.poc.testrequest.model.TestRequest;
import org.openmrs.module.poc.testrequest.service.TestRequestService;
import org.springframework.stereotype.Component;

@Component
public class TestOrderUtil {
	
	public TestOrderPOC buildTestOrder(final Encounter requestEncounter, final Encounter resultEncounter,
	        final Map<Concept, Concept> mapCategoriesByTestConcept) {
		
		final Set<Order> orders = this.mergeOrders(requestEncounter.getOrders(), resultEncounter.getOrders());
		
		if (!orders.isEmpty()) {
			
			final List<TestOrderItem> items = new ArrayList<>();
			for (final Order order : orders) {
				
				if (OrderType.TEST_ORDER_TYPE_UUID.equals(order.getOrderType().getUuid()) && !order.isVoided()) {
					items.add(new TestOrderItem(order, mapCategoriesByTestConcept.get(order.getConcept())));
				}
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
	
}
