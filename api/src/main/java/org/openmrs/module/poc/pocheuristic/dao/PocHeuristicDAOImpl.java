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
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.Visit;
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
	public List<Encounter> findEncountersWithTestOrdersByPatient(final Patient patient,
			final EncounterType encounterType, final boolean voided) {

		return this.sessionFactory.getCurrentSession()
				.createQuery(
						"select distinct o.encounter from TestOrder o where o.patient =:patient and o.encounter.encounterType = :encounterType and o.voided = :voided ")
				.setParameter("patient", patient).setParameter("voided", voided)
				.setParameter("encounterType", encounterType).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Encounter> findEncountersByPatientAndEncounterTypeAndOrderTypeUuid(final Patient patient,
			final EncounterType encounterType, final String orderTypeUuid, final boolean voided) {

		final Criteria searchCriteria = this.sessionFactory.getCurrentSession().createCriteria(Encounter.class, "enc");
		searchCriteria.add(Restrictions.eq("enc.patient", patient));
		searchCriteria.add(Restrictions.eq("enc.encounterType", encounterType));
		searchCriteria.add(Restrictions.eq("enc.voided", voided));
		searchCriteria.createAlias("enc.orders", "ord", CriteriaSpecification.LEFT_JOIN);
		searchCriteria.createAlias("ord.orderType", "oType");
		searchCriteria.add(Restrictions.eq("oType.uuid", orderTypeUuid));
		searchCriteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

		return searchCriteria.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Visit> findVisits(Patient patient, Boolean mostRecentOnly, Date startDate, Date endDate,
			Boolean voided) {
		final Criteria searchCriteria = this.sessionFactory.getCurrentSession().createCriteria(Visit.class, "visit");
		if (patient != null) {
			searchCriteria.add(Restrictions.eq("visit.patient", patient));
		}
		if (startDate != null) {
			searchCriteria.add(Restrictions.ge("visit.startDatetime", startDate));
		}
		if (endDate != null) {
			searchCriteria.add(Restrictions.lt("visit.stopDatetime", endDate));
		}
		if (voided != null) {
			searchCriteria.add(Restrictions.eq("visit.voided", voided));
		}
		if (mostRecentOnly != null && mostRecentOnly.booleanValue()) {
			searchCriteria.setMaxResults(1);
		}
		searchCriteria.addOrder(org.hibernate.criterion.Order.desc("visit.startDatetime"));
		return searchCriteria.list();
	}

	@Override
	public Obs findObsByOrderAndConceptAndEncounter(final Order order, final Concept concept, final Encounter encounter,
			final boolean voided) {

		final Criteria searchCriteria = this.sessionFactory.getCurrentSession().createCriteria(Obs.class, "obs");
		searchCriteria.add(Restrictions.eq("obs.order", order));
		searchCriteria.add(Restrictions.eq("obs.concept", concept));
		searchCriteria.add(Restrictions.eq("obs.encounter", encounter));
		searchCriteria.add(Restrictions.eq("obs.voided", voided));

		return (Obs) searchCriteria.uniqueResult();
	}

	@Override
	public Obs findObsByEncounterAndConcept(final Encounter encounter, final Concept concept, final boolean voided) {
		final Criteria searchCriteria = this.sessionFactory.getCurrentSession().createCriteria(Obs.class, "obs");
		searchCriteria.add(Restrictions.eq("obs.concept", concept));
		searchCriteria.add(Restrictions.eq("obs.encounter", encounter));
		searchCriteria.add(Restrictions.eq("obs.voided", voided));

		return (Obs) searchCriteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Obs> findObsByGroup(final Obs obsGroup, final boolean voided) {

		final Criteria searchCriteria = this.sessionFactory.getCurrentSession().createCriteria(Obs.class, "obs");
		searchCriteria.add(Restrictions.eq("obs.obsGroup", obsGroup));
		searchCriteria.add(Restrictions.eq("obs.voided", voided));

		return searchCriteria.list();
	}

}
