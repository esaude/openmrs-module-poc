/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc.api.common.model;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.openmrs.OpenmrsObject;

/**
 *
 *
 */
@MappedSuperclass
public abstract class BaseOpenmrsObjectWrapper implements Serializable, OpenmrsObject {
	
	private static final long serialVersionUID = 933059070810131693L;
	
	@Column(name = "uuid", unique = true, nullable = false, length = 38, updatable = false)
	private String uuid = UUID.randomUUID().toString();
	
	@Override
	public String getUuid() {
		return this.uuid;
	}
	
	@Override
	public void setUuid(final String uuid) {
		this.uuid = uuid;
	}
}
