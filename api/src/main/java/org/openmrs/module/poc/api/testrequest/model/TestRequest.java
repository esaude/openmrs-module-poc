/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc.api.testrequest.model;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Concept;

public class TestRequest extends BaseOpenmrsData {
	
	private Concept testOrder;
	
	private Concept category;
	
	public TestRequest(final Concept testOrder, final Concept category) {
		this.testOrder = testOrder;
		this.category = category;
	}
	
	public TestRequest() {
	}
	
	public Concept getTestOrder() {
		return this.testOrder;
	}
	
	public void setTestOrder(final Concept testOrder) {
		this.testOrder = testOrder;
	}
	
	public void setCategory(final Concept category) {
		this.category = category;
	}
	
	public Concept getCategory() {
		return this.category;
	}
	
	@Override
	public Integer getId() {
		return null;
	}
	
	@Override
	public void setId(final Integer arg0) {
	}
	
}
