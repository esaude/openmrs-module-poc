/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc.pocheuristic.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Patient;

public interface PocHeuristicCAO {
	
	void setSessionFactory(final SessionFactory sessionFactory);
	
	public Encounter findLastEncounterByPatientAndEncounterTypeAndLocationAndDateAndStatus(final Patient patient,
	        final EncounterType encounterType, final Location location, final Date encounterDateTime,
	        final boolean status);
	
	List<Encounter> findEncountersWithTestOrdersByPatient(String patientUUID, boolean voided);
	
	List<Encounter> findEncountersByPatientAndEncounterTypeAndOrderTypeUuid(final Patient patient,
	        final EncounterType encounterType, String orderTypeUuid);
}
