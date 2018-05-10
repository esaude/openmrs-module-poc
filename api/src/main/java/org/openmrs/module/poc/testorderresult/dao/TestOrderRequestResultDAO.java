/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc.testorderresult.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.module.poc.testorderresult.model.TestOrderRequestResult;

public interface TestOrderRequestResultDAO {
	
	void setSessionFactory(SessionFactory sessionFactory);
	
	TestOrderRequestResult save(TestOrderRequestResult testRequestResult);
	
	TestOrderRequestResult findByResultEncounter(Encounter result, boolean voided);
	
	TestOrderRequestResult findByRequestEncounter(Encounter request, boolean voided);
	
	List<TestOrderRequestResult> findByPatientUuid(Patient patient, boolean voided);
	
}
