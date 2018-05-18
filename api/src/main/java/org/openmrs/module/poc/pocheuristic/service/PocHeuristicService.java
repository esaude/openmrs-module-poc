/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc.pocheuristic.service;

import java.util.Date;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.poc.pocheuristic.dao.PocHeuristicCAO;
import org.springframework.transaction.annotation.Transactional;

@Transactional(noRollbackFor = APIException.class)
public interface PocHeuristicService extends OpenmrsService {
	
	void setPocHeuristicCAO(PocHeuristicCAO pocHeuristicCAO);
	
	EncounterType findSeguimentoPacienteEncounterTypeByPatientAge(final Patient patient);
	
	Encounter findLastEncounterByPatientAndEncounterTypeAndLocationAndDateAndStatus(final Patient patient,
	        final EncounterType encounterType, final Location location, final Date encounterDateTime);
	
	List<Encounter> findEncountersWithTestOrdersByPatient(Patient patient, EncounterType encounterType);
	
	List<Encounter> findEncountersByPatientAndEncounterType(Patient patient, EncounterType encounterType);
	
	List<Visit> findVisits(Patient patient, Boolean mostRecentOnly, Date date, Boolean voided);
	
	Obs findObsByOrderAndConceptAndEncounter(Order order, Concept concept, Encounter encounter);
	
	Obs findObsByEncounterAndConcept(Encounter encounter, Concept concept);
	
	List<Obs> findObsByGroup(Obs obsGroup);
}
