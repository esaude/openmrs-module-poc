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

import java.util.Date;
import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.api.OpenmrsService;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProviderService;
import org.openmrs.module.poc.api.common.service.POCDbSessionManager;
import org.openmrs.module.poc.pocheuristic.service.PocHeuristicService;
import org.openmrs.module.poc.testorder.model.TestOrderItem;
import org.openmrs.module.poc.testorder.model.TestOrderPOC;
import org.openmrs.module.poc.testorderresult.service.TestOrderRequestResultService;
import org.springframework.transaction.annotation.Transactional;

@Transactional(noRollbackFor = APIException.class)
public interface TestOrderService extends OpenmrsService {
	
	void setPocHeuristicService(PocHeuristicService pocHeuristicService);
	
	void setOrderService(final OrderService orderService);
	
	void setPatientService(final PatientService patientService);
	
	void setEncounterService(final EncounterService encounterService);
	
	void setLocationService(final LocationService locationService);
	
	void setConceptService(final ConceptService conceptService);
	
	void setProviderService(ProviderService providerService);
	
	void setPOCDbSessionManager(POCDbSessionManager pOCDbSessionManager);
	
	void setTestOrderRequestResultService(TestOrderRequestResultService testOrderRequestResultService);
	
	TestOrderPOC createTestOder(TestOrderPOC testOrderPOC);
	
	void deleteTestOrderItem(TestOrderItem testOrder, String reason);
	
	TestOrderPOC findTestOrderByEncounter(Encounter encounter);
	
	List<TestOrderPOC> findTestOrdersByPatient(String patientUUID);
	
	TestOrderItem findTestOrderItemByUuid(String uuid);
	
	EncounterType findSeguimentoPacienteEncounterTypeByPatientAge(final Patient patient);
	
	Encounter findLastEncounterByPatientAndEncounterTypeAndLocationAndDateAndStatus(final Patient patient,
	        final EncounterType encounterType, final Location location, final Date encounterDateTime);
	
}
