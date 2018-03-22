/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc.sequencegenerator.dao;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.module.poc.sequencegenerator.model.PocSequenceGenerator;
import org.openmrs.module.poc.sequencegenerator.util.SequenceNames;

public class PocSequenceGeneratorDAOImpl implements PocSequenceGeneratorDAO {
	
	private SessionFactory sessionFactory;
	
	@Override
	public void setSessionFactory(final SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Override
	public PocSequenceGenerator findBySequenceName(final SequenceNames sequenceName) {
		
		return (PocSequenceGenerator) this.sessionFactory.getCurrentSession()
		        .createCriteria(PocSequenceGenerator.class, "seq")
		        .add(Restrictions.eq("seq.sequenceName", sequenceName)).uniqueResult();
	}
	
	@Override
	public PocSequenceGenerator update(final PocSequenceGenerator pocSequenceGenerator) {
		
		this.sessionFactory.getCurrentSession().save(pocSequenceGenerator);
		return pocSequenceGenerator;
	}
	
	@Override
	public PocSequenceGenerator create(final SequenceNames sequenceName) {
		
		final PocSequenceGenerator sequence = new PocSequenceGenerator();
		sequence.setSequenceName(sequenceName);
		sequence.setSequenceValue(0);
		
		this.sessionFactory.getCurrentSession().save(sequence);
		return sequence;
	}
	
}
