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

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.module.poc.testorderresult.model.TestOrderRequestResult;

public class TestOrderRequestResultDAOImpl implements TestOrderRequestResultDAO {
	
	private SessionFactory sessionFactory;
	
	@Override
	public void setSessionFactory(final SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Override
	public TestOrderRequestResult save(final TestOrderRequestResult testRequestResult) {
		
		this.sessionFactory.getCurrentSession().save(testRequestResult);
		return testRequestResult;
	}
	
	@Override
	public TestOrderRequestResult findByResultEncounter(final Encounter result, final boolean voided) {
		final Criteria searchCriteria = this.sessionFactory.getCurrentSession()
		        .createCriteria(TestOrderRequestResult.class, "testRR");
		searchCriteria.add(Restrictions.eq("testRR.testOrderResult", result));
		searchCriteria.add(Restrictions.eq("testRR.retired", voided));
		
		return (TestOrderRequestResult) searchCriteria.uniqueResult();
	}
	
	@Override
	public TestOrderRequestResult findByRequestEncounter(final Encounter request, final boolean voided) {
		
		final Criteria searchCriteria = this.sessionFactory.getCurrentSession()
		        .createCriteria(TestOrderRequestResult.class, "testRR");
		searchCriteria.add(Restrictions.eq("testRR.testOrderRequest", request));
		searchCriteria.add(Restrictions.eq("testRR.retired", voided));
		
		return (TestOrderRequestResult) searchCriteria.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<TestOrderRequestResult> findByPatientUuid(final Patient patient, final boolean voided) {
		
		final Criteria searchCriteria = this.sessionFactory.getCurrentSession()
		        .createCriteria(TestOrderRequestResult.class, "testRR");
		searchCriteria.add(Restrictions.eq("testRR.patient", patient));
		searchCriteria.add(Restrictions.eq("testRR.retired", voided));
		
		return searchCriteria.list();
	}
}
