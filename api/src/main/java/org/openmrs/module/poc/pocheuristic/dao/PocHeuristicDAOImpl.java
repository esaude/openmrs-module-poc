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

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.module.poc.api.common.util.DateUtils;

public class PocHeuristicDAOImpl implements PocHeuristicCAO {
	
	private SessionFactory sessionFactory;
	
	@Override
	public void setSessionFactory(final SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Encounter findLastEncounterByPatientAndEncounterTypeAndLocationAndDateAndStatus(final Patient patient,
	        final EncounterType encounterType, final Location location, final Date encounterDateTime,
	        final boolean status) {
		
		final Criteria searchCriteria = this.sessionFactory.getCurrentSession().createCriteria(Encounter.class, "enc");
		searchCriteria.add(Restrictions.eq("enc.patient", patient));
		searchCriteria.add(Restrictions.eq("enc.encounterType", encounterType));
		searchCriteria.add(Restrictions.eq("enc.location", location));
		searchCriteria.add(Restrictions.ge("enc.encounterDatetime", DateUtils.lowDateTime(encounterDateTime)));
		searchCriteria.add(Restrictions.le("enc.encounterDatetime", DateUtils.highDateTime(encounterDateTime)));
		searchCriteria.add(Restrictions.eq("enc.voided", status));
		searchCriteria.addOrder(org.hibernate.criterion.Order.desc("encounterDatetime"));
		searchCriteria.addOrder(org.hibernate.criterion.Order.desc("encounterId"));
		
		final List<Encounter> result = searchCriteria.list();
		if (result.isEmpty()) {
			throw new APIException("encounter not found for given patient " + patient.getGivenName());
		}
		return result.get(0);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Encounter> findEncountersWithTestOrdersByPatient(final String patientUUID, final boolean voided) {
		
		return this.sessionFactory
		        .getCurrentSession()
		        .createQuery(
		            "select distinct o.encounter from TestOrder o where o.patient.uuid =:patientUUID and o.voided = :voided ")
		        .setParameter("patientUUID", patientUUID).setParameter("voided", voided).list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Encounter> findEncountersByPatientAndEncounterTypeAndOrderTypeUuid(final Patient patient,
	        final EncounterType encounterType, final String orderTypeUuid) {
		
		return this.sessionFactory
		        .getCurrentSession()
		        .createQuery(
		            "select distinct enc from Encounter enc left join fetch enc.orders ord left join fetch ord.drug where enc.encounterType = :encounterType and enc.patient = :patient and ord.orderType.uuid =:orderTypeUuid")
		        .setParameter("patient", patient).setParameter("encounterType", encounterType)
		        .setParameter("orderTypeUuid", orderTypeUuid).list();
	}
	
}
