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

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.poc.testresult.model.TestRequestResult;

public class TestRequestResultDAOImpl implements TestRequestResultDAO {
	
	private SessionFactory sessionFactory;
	
	@Override
	public void setSessionFactory(final SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Override
	public TestRequestResult save(final TestRequestResult testRequestResult) {
		
		this.sessionFactory.getCurrentSession().save(testRequestResult);
		return testRequestResult;
	}
	
	@Override
	public void update(final TestRequestResult testRequestResult) {
		
	}
	
	@Override
	public void retire(final TestRequestResult testRequestResult) {
	}
	
	@Override
	public TestRequestResult findByUuid(final String uuid) {
		return null;
	}
	
	@Override
	public TestRequestResult findByResultEncounter(final Encounter result) {
		return null;
	}
	
	@Override
	public TestRequestResult findByRequestEncounter(final Encounter request, final boolean voided) {
		
		final Criteria searchCriteria = this.sessionFactory.getCurrentSession().createCriteria(TestRequestResult.class,
		    "testRR");
		searchCriteria.add(Restrictions.eq("testRR.testRequest", request));
		searchCriteria.add(Restrictions.eq("testRR.retired", voided));
		
		return (TestRequestResult) searchCriteria.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<TestRequestResult> findByPatientUuid(final String patientUuid, final boolean voided) {
		
		final Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
		
		final Criteria searchCriteria = this.sessionFactory.getCurrentSession().createCriteria(TestRequestResult.class,
		    "testRR");
		searchCriteria.add(Restrictions.eq("testRR.patient", patient));
		searchCriteria.add(Restrictions.eq("testRR.retired", voided));
		
		return searchCriteria.list();
	}
}
