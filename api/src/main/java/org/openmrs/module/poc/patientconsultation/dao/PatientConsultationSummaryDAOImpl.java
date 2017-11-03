/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc.patientconsultation.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.module.poc.api.common.util.DateUtils;

public class PatientConsultationSummaryDAOImpl implements PatientConsultationSummaryDAO {
	
	private SessionFactory sessionFactory;
	
	@Override
	public void setSessionFactory(final SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Obs> findObsByLocationAndDateInterval(final List<EncounterType> encounterTypes, final Concept concept,
	        final Location location, final Date startDate, final Date endDate) {
		
		final Criteria searchCriteria = this.sessionFactory.getCurrentSession().createCriteria(Obs.class, "obs");
		searchCriteria.createAlias("obs.encounter", "enc");
		searchCriteria.add(Restrictions.in("enc.encounterType", encounterTypes));
		searchCriteria.add(Restrictions.eq("obs.concept", concept));
		searchCriteria.add(Restrictions.eq("obs.voided", false));
		searchCriteria.add(Restrictions.eq("obs.location", location));
		searchCriteria.add(Restrictions.between("obs.valueDatetime", DateUtils.lowDateTime(startDate),
		    DateUtils.highDateTime(endDate)));
		searchCriteria.addOrder(Order.asc("obs.valueDatetime"));
		
		return searchCriteria.list();
	}
	
	@Override
	public boolean hasCheckinInExpectedNextVisitDate(final Patient patient, final Date dateForNextVisit) {
		
		final Criteria searchCriteria = this.sessionFactory.getCurrentSession().createCriteria(Visit.class, "visit");
		searchCriteria.add(Restrictions.eq("visit.patient", patient));
		searchCriteria.add(Restrictions.eq("visit.voided", false));
		searchCriteria.add(Restrictions.ge("startDatetime", DateUtils.lowDateTime(dateForNextVisit)));
		searchCriteria.add(Restrictions.le("startDatetime", DateUtils.highDateTime(dateForNextVisit)));
		
		return !searchCriteria.list().isEmpty();
	}
}
