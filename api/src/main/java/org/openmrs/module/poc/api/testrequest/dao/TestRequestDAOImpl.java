/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
/**
 *
 */
package org.openmrs.module.poc.api.testrequest.dao;

import java.util.List;
import java.util.Locale;

import org.hibernate.SessionFactory;
import org.openmrs.Concept;

public class TestRequestDAOImpl implements TestRequestDAO {
	
	private SessionFactory sessionFactory;
	
	private final String LABORATORIO_GERAL_FORM_UUID = "e28b23a6-1d5f-11e0-b929-000c29ad1d07";
	
	private final String CONCEPT_CLASS_TEST_UUID = "8d4907b2-c2cc-11de-8d13-0010c6dffd0f";
	
	private final String findAll = "select distinct c from Concept c join c.names cn  where c.conceptId in ( "
	        + "  select fieldConcept.conceptId from FormField ff join ff.field f join f.concept fieldConcept join fieldConcept.names cname "
	        + "  where ff.form.uuid = :formUUID and c.conceptClass.uuid = :testConcepClasstUUID and cname.locale = :locale and fieldConcept.retired = :retired )";
	
	@Override
	public void setSessionFactory(final SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Concept> findAll(final Locale locale) {
		
		return this.sessionFactory.getCurrentSession().createQuery(this.findAll)
		        .setParameter("formUUID", this.LABORATORIO_GERAL_FORM_UUID)
		        .setParameter("testConcepClasstUUID", this.CONCEPT_CLASS_TEST_UUID).setParameter("retired", false)
		        .setParameter("locale", locale).list();
	}
	
}
