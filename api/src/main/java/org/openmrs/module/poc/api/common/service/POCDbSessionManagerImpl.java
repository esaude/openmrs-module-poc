/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
/*
 * Friends in Global Health - FGH © 2016
 */
package org.openmrs.module.poc.api.common.service;

import org.hibernate.FlushMode;
import org.openmrs.api.db.hibernate.DbSessionFactory;

public class POCDbSessionManagerImpl implements POCDbSessionManager {
	
	private DbSessionFactory sessionFactory;
	
	private FlushMode currentFlushMode;
	
	@Override
	public FlushMode getCurrentFlushMode() {
		return this.sessionFactory.getCurrentSession().getFlushMode();
	}
	
	@Override
	public void setManualFlushMode() {
		this.currentFlushMode = this.getCurrentFlushMode();
		this.sessionFactory.getCurrentSession().setFlushMode(FlushMode.MANUAL);
	}
	
	@Override
	public void setFlushMode(final FlushMode flushMode) {
		this.sessionFactory.getCurrentSession().setFlushMode(flushMode);
	}
	
	public void setSessionFactory(final DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Override
	public void setAutoFlushMode() {
		this.setFlushMode(this.currentFlushMode);
	}
}
