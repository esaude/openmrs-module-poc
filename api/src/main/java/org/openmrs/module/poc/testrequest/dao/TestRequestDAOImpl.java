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
package org.openmrs.module.poc.testrequest.dao;

import java.util.List;
import java.util.Locale;

import org.hibernate.SessionFactory;
import org.openmrs.Concept;
import org.openmrs.module.poc.api.common.util.OPENMRSUUIDs;

public class TestRequestDAOImpl implements TestRequestDAO {
	
	private SessionFactory sessionFactory;
	
	private static final String FINDALL = "select distinct c from Concept c join c.names cn  where c.conceptId in ( "
	        + "  select fieldConcept.conceptId from FormField ff join ff.field f join f.concept fieldConcept join fieldConcept.names cname "
	        + "  where ff.form.uuid = :formUUID and c.conceptClass.uuid = :testConcepClasstUUID and cname.locale = :locale and fieldConcept.retired = :retired )";
	
	@Override
	public void setSessionFactory(final SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Concept> findAll(final Locale locale) {
		
		return this.sessionFactory.getCurrentSession().createQuery(TestRequestDAOImpl.FINDALL)
		        .setParameter("formUUID", OPENMRSUUIDs.LABORATORIO_GERAL_META_FORM_UUID)
		        .setParameter("testConcepClasstUUID", OPENMRSUUIDs.TEST_CONCEPT_CLASS_UUID)
		        .setParameter("retired", false).setParameter("locale", locale).list();
	}
}
