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
		final int prime = 31;
		int result = super.hashCode();
		result = (prime * result) + ((this.category == null) ? 0 : this.category.hashCode());
		result = (prime * result) + ((this.testOrder == null) ? 0 : this.testOrder.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final TestRequest other = (TestRequest) obj;
		if (this.category == null) {
			if (other.category != null) {
				return false;
			}
		} else if (!this.category.equals(other.category)) {
			return false;
		}
		if (this.testOrder == null) {
			if (other.testOrder != null) {
				return false;
			}
		} else if (!this.testOrder.equals(other.testOrder)) {
			return false;
		}
		return true;
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
