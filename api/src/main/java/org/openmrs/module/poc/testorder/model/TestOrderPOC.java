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

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.module.poc.testorder.model.TestOrderItem.ITEM_STATUS;

public class TestOrderPOC extends BaseOpenmrsData implements Serializable {
	
	private static final long serialVersionUID = 6702921908078966554L;
	
	private Patient patient;
	
	private Provider provider;
	
	private Location location;
	
	private Date dateCreation;
	
	private Encounter encounter;
	
	private String provenance;
	
	private String codeSequence;
	
	private List<TestOrderItem> testOrderItems;
	
	public enum STATUS {
		NEW, PENDING, COMPLETE;
	}
	
	public List<TestOrderItem> getTestOrderItems() {
		return this.testOrderItems;
	}
	
	public void setTestOrderItems(final List<TestOrderItem> testOrderItems) {
		this.testOrderItems = testOrderItems;
	}
	
	public Patient getPatient() {
		return this.patient;
	}
	
	public void setPatient(final Patient patient) {
		this.patient = patient;
	}
	
	public Provider getProvider() {
		return this.provider;
	}
	
	public void setProvider(final Provider provider) {
		this.provider = provider;
	}
	
	public Date getDateCreation() {
		return this.dateCreation;
	}
	
	public void setDateCreation(final Date dateCreation) {
		this.dateCreation = dateCreation;
	}
	
	public Location getLocation() {
		return this.location;
	}
	
	public void setLocation(final Location location) {
		this.location = location;
	}
	
	public Encounter getEncounter() {
		return this.encounter;
	}
	
	public void setEncounter(final Encounter encounter) {
		this.encounter = encounter;
	}
	
	public String getProvenance() {
		return this.provenance;
	}
	
	public void setProvenance(final String provenance) {
		this.provenance = provenance;
	}
	
	public String getCodeSequence() {
		return this.codeSequence;
	}
	
	public void setCodeSequence(final String codeSequence) {
		this.codeSequence = codeSequence;
	}
	
	public STATUS getStatus() {
		
		if (this.testOrderItems.isEmpty()) {
			return STATUS.NEW;
		}
		
		int countRevise = 0;
		int countNew = 0;
		for (final TestOrderItem item : this.testOrderItems) {
			
			if (ITEM_STATUS.REVISE.equals(item.getStatus())) {
				countRevise++;
			} else if (ITEM_STATUS.NEW.equals(item.getStatus())) {
				countNew++;
			}
		}
		if (countRevise == this.testOrderItems.size()) {
			return STATUS.COMPLETE;
		}
		
		if (countNew == this.testOrderItems.size()) {
			return STATUS.NEW;
		}
		
		return STATUS.PENDING;
	}
	
	@Override
	public String toString() {
		return "TestOrderPOC [patient=" + this.patient + ", provider=" + this.provider + ", location=" + this.location
		        + ", dateCreation=" + this.dateCreation + ", encounter=" + this.encounter + ", provenance="
		        + this.provenance + ", codeSequence=" + this.codeSequence + ", testOrderItems=" + this.testOrderItems
		        + "]";
	}
	
	@Override
	public String getUuid() {
		
		if (this.encounter != null) {
			return this.encounter.getUuid();
		}
		return super.getUuid();
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	@Override
	public boolean equals(final Object obj) {
		return super.equals(obj);
	}
	
	@Override
	public Integer getId() {
		return null;
	}
	
	@Override
	public void setId(final Integer arg0) {
		// this is a wrapper Entity
	}
}
