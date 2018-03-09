/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc.testresult.dao;

import org.hibernate.SessionFactory;
import org.openmrs.Encounter;
import org.openmrs.module.poc.testresult.model.TestRequestResult;

public interface TestRequestResultDAO {
	
	void setSessionFactory(SessionFactory sessionFactory);
	
	TestRequestResult save(TestRequestResult testRequestResult);
	
	void update(TestRequestResult testRequestResult);
	
	void retire(TestRequestResult testRequestResult);
	
	TestRequestResult findByUuid(String uuid);
	
	TestRequestResult findByResultEncounter(Encounter result);
	
	TestRequestResult findByRequestEncounter(Encounter request, boolean voided);
	
}
