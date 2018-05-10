/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc.testorder.model;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Concept;
import org.openmrs.Order;
import org.openmrs.Order.Action;

public class TestOrderItem extends BaseOpenmrsData {
	
	private Order testOrder;
	
	private Concept category;
	
	private TestOrderPOC parent;
	
	private ITEM_STATUS status;
	
	private Result result;
	
	public enum ITEM_STATUS {
		NEW, REVISE;
	}
	
	public TestOrderItem(final Order testOrder, final Concept category, final Action action, final String value,
	    final String unit) {
		
		this.testOrder = testOrder;
		this.category = category;
		this.status = Action.NEW.equals(action) ? ITEM_STATUS.NEW : ITEM_STATUS.REVISE;
		this.result = new Result(value, unit);
		super.setUuid(testOrder.getUuid());
	}
	
	public TestOrderItem() {
		
	}
	
	public Order getTestOrder() {
		return this.testOrder;
	}
	
	public void setTestOrder(final Order testOrder) {
		this.testOrder = testOrder;
	}
	
	public Concept getCategory() {
		return this.category;
	}
	
	public void setCategory(final Concept category) {
		this.category = category;
	}
	
	public TestOrderPOC getParent() {
		return this.parent;
	}
	
	public void setParent(final TestOrderPOC parent) {
		this.parent = parent;
	}
	
	public ITEM_STATUS getStatus() {
		return this.status;
	}
	
	public void setStatus(final ITEM_STATUS status) {
		this.status = status;
	}
	
	public Result getResult() {
		return this.result;
	}
	
	public void setResult(final Result result) {
		this.result = result;
	}
	
	@Override
	public String toString() {
		return "TestOrderItem [testOrder=" + this.testOrder + ", category=" + this.category + ", parent=" + this.parent
		        + ", status=" + this.status + "]";
	}
	
	@Override
	public String getUuid() {
		
		if (this.testOrder != null) {
			return this.testOrder.getUuid();
		}
		return super.getUuid();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
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
		final TestOrderItem other = (TestOrderItem) obj;
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
		// this is a wrapper entity
	}
	
	private class Result {
		
		private String value;
		
		private String unit;
		
		public Result(final String value, final String unit) {
			this.unit = unit;
			this.value = value;
		}
		
		public String getValue() {
			return this.value;
		}
		
		public void setValue(final String value) {
			this.value = value;
		}
		
		public String getUnit() {
			return this.unit;
		}
		
		public void setUnit(final String unit) {
			this.unit = unit;
		}
		
	}
}
