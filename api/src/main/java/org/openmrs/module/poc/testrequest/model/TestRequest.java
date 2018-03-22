/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc.testrequest.model;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
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
	public int hashCode() {
		
		return new HashCodeBuilder().append(this.category.hashCode()).append(this.testOrder.hashCode()).toHashCode();
	}
	
	@Override
	public boolean equals(final Object obj) {
		
		if (obj == null) {
			return false;
		}
		
		if (obj instanceof TestRequest) {
			final TestRequest other = (TestRequest) obj;
			return new EqualsBuilder().append(this.getTestOrder(), other.getTestOrder())
			        .append(this.getCategory(), other.getCategory()).isEquals();
		}
		return false;
	}
	
	@Override
	public Integer getId() {
		return null;
	}
	
	@Override
	public void setId(final Integer arg0) {
		// Its is a wrapper entity
	}
}
