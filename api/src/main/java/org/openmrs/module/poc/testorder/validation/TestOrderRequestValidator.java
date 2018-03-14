/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc.testorder.validation;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.poc.api.common.util.OPENMRSUUIDs;
import org.openmrs.module.poc.pocheuristic.service.PocHeuristicService;
import org.openmrs.module.poc.testorder.model.TestOrderItem;
import org.openmrs.module.poc.testorder.model.TestOrderPOC;
import org.springframework.stereotype.Component;

@Component
public class TestOrderRequestValidator {
	
	public void validate(final TestOrderPOC testOrder) {
		final PocHeuristicService pocHeuristicService = Context.getService(PocHeuristicService.class);
		
		this.validatePatient(testOrder);
		this.validateLocation(testOrder);
		this.validateProvider(testOrder);
		this.validateCreationDate(testOrder);
		this.validateTestOrderItems(testOrder);
		
		final Patient patient = Context.getPatientService().getPatientByUuid(testOrder.getPatient().getUuid());
		final Location location = Context.getLocationService().getLocationByUuid(testOrder.getLocation().getUuid());
		final EncounterType encounterType = pocHeuristicService
		        .findSeguimentoPacienteEncounterTypeByPatientAge(patient);
		
		this.validateDuplicateTest(pocHeuristicService, testOrder.getTestOrderItems(), patient, encounterType, location,
		    testOrder.getDateCreation());
		
	}
	
	private void validatePatient(final TestOrderPOC testOrder) {
		
		if (testOrder.getPatient() == null) {
			throw new APIException(Context.getMessageSourceService().getMessage("poc.error.location.required"));
		}
		final Location location = Context.getLocationService().getLocationByUuid(testOrder.getLocation().getUuid());
		
		if (location == null) {
			throw new APIException(Context.getMessageSourceService().getMessage("poc.error.location.not.found.for.uuid",
			    new String[] { testOrder.getLocation().getUuid() }, Context.getLocale()));
		}
	}
	
	private void validateProvider(final TestOrderPOC testOrder) {
		
		if (testOrder.getProvider() == null) {
			throw new APIException(Context.getMessageSourceService().getMessage("poc.error.provider.required"));
		}
		
		final Provider provider = Context.getProviderService().getProviderByUuid(testOrder.getProvider().getUuid());
		
		if (provider == null) {
			throw new APIException(Context.getMessageSourceService().getMessage("poc.error.provider.not.found.for.uuid",
			    new String[] { testOrder.getProvider().getUuid() }, Context.getLocale()));
		}
	}
	
	private void validateCreationDate(final TestOrderPOC testOrder) {
		
		if (testOrder.getDateCreation() == null) {
			throw new APIException(Context.getMessageSourceService().getMessage("poc.error.creationDate.required"));
		}
		if (testOrder.getDateCreation().after(new Date())) {
			
			throw new APIException(
			        Context.getMessageSourceService().getMessage("poc.error.testorder.creationDate.isAfterCurrentDate",
			            new String[] { testOrder.getDateCreation().toString() }, Context.getLocale()));
		}
	}
	
	private void validateTestOrderItems(final TestOrderPOC testOrder) {
		
		if (testOrder.getTestOrderItems().isEmpty()) {
			throw new APIException(Context.getMessageSourceService().getMessage("poc.error.testorder.items.required"));
		}
		
		final Map<Concept, List<String>> repeatedTests = new HashMap<>();
		for (final TestOrderItem item : testOrder.getTestOrderItems()) {
			
			if ((item == null) || (item.getTestOrder() == null) || (item.getTestOrder().getConcept() == null)) {
				throw new APIException(Context.getMessageSourceService().getMessage("poc.error.testConcept.required"));
			}
			final Concept testConcept = Context.getConceptService()
			        .getConceptByUuid(item.getTestOrder().getConcept().getUuid());
			
			if (testConcept == null) {
				
				throw new APIException(
				        Context.getMessageSourceService().getMessage("poc.error.testConcept.not.found.for.uuid",
				            new String[] { item.getTestOrder().getConcept().getUuid() }, Context.getLocale()));
			}
			
			if (!OPENMRSUUIDs.TEST_CONCEPT_CLASS_UUID.equals(testConcept.getConceptClass().getUuid())) {
				
				throw new APIException(Context.getMessageSourceService().getMessage(
				    "poc.error.testorder.item.notfor.classTest",
				    new String[] { item.getTestOrder().getConcept().getDisplayString() }, Context.getLocale()));
			}
			List<String> list = repeatedTests.get(testConcept);
			
			if (list == null) {
				list = new ArrayList<>();
				repeatedTests.put(testConcept, list);
			}
			list.add(testConcept.getUuid());
		}
		
		for (final Entry<Concept, List<String>> keyValue : repeatedTests.entrySet()) {
			
			if (keyValue.getValue().size() > 1) {
				
				throw new APIException(
				        Context.getMessageSourceService().getMessage("poc.error.testorder.not.allowed.repeated.items",
				            new String[] { keyValue.getKey().getDisplayString() }, Context.getLocale()));
			}
		}
	}
	
	private void validateLocation(final TestOrderPOC testOrder) {
		
		if (testOrder.getLocation() == null) {
			throw new APIException(Context.getMessageSourceService().getMessage("poc.error.patient.required"));
		}
		final Patient patient = Context.getPatientService().getPatientByUuid(testOrder.getPatient().getUuid());
		
		if (patient == null) {
			throw new APIException(Context.getMessageSourceService().getMessage("poc.error.patient.not.found.for.uuid",
			    new String[] { testOrder.getPatient().getUuid() }, Context.getLocale()));
		}
	}
	
	private void validateDuplicateTest(final PocHeuristicService pocHeuristicService, final List<TestOrderItem> items,
	        final Patient patient, final EncounterType encounterType, final Location location,
	        final Date creationDate) {
		
		final Encounter encounter;
		try {
			encounter = pocHeuristicService.findLastEncounterByPatientAndEncounterTypeAndLocationAndDateAndStatus(
			    patient, encounterType, location, creationDate);
			
		}
		catch (final APIException e) {
			
			return;
		}
		
		final List<String> alreadyCreatedTests = new ArrayList<>();
		for (final Order order : encounter.getOrders()) {
			
			if (!order.isVoided() && OrderType.TEST_ORDER_TYPE_UUID.equals(order.getOrderType().getUuid())) {
				alreadyCreatedTests.add(order.getConcept().getUuid());
			}
		}
		
		final Set<String> repeatedTests = new TreeSet<>();
		for (final TestOrderItem item : items) {
			final Concept testConcept = item.getTestOrder().getConcept();
			if (alreadyCreatedTests.contains(testConcept.getUuid())) {
				repeatedTests.add(testConcept.getDisplayString());
			}
		}
		if (!repeatedTests.isEmpty()) {
			
			throw new APIException(Context.getMessageSourceService().getMessage("poc.error.found.repeated.tests",
			    repeatedTests.toArray(), Context.getLocale()));
		}
	}
	
}
