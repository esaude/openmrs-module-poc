/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc.testorderresult.model;

import java.util.Date;
import java.util.List;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.module.poc.testorderresult.model.TestOrderResultItem.ITEM_STATUS;

public class TestOrderResult extends BaseOpenmrsData {
	
	private Patient patient;
	
	private Provider provider;
	
	private Location location;
	
	private Date dateCreation;
	
	private Encounter encounterRequest;
	
	private Encounter encounterResult;
	
	private List<TestOrderResultItem> items;
	
	private String codeSequence;
	
	public enum STATUS {
		PENDING, COMPLETE;
	}
	
	public List<TestOrderResultItem> getItems() {
		return this.items;
	}
	
	public void setItems(final List<TestOrderResultItem> items) {
		this.items = items;
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
	
	public Encounter getEncounterRequest() {
		return this.encounterRequest;
	}
	
	public void setEncounterRequest(final Encounter encounterRequest) {
		this.encounterRequest = encounterRequest;
	}
	
	public Encounter getEncounterResult() {
		return this.encounterResult;
	}
	
	public void setEncounterResult(final Encounter encounterResult) {
		this.encounterResult = encounterResult;
	}
	
	public STATUS getStatus() {
		
		if (this.items.isEmpty()) {
			return STATUS.PENDING;
		}
		for (final TestOrderResultItem item : this.items) {
			
			if (ITEM_STATUS.NEW.equals(item.getStatus())) {
				return STATUS.PENDING;
			}
		}
		return STATUS.COMPLETE;
	}
	
	public String getCodeSequence() {
		return this.codeSequence;
	}
	
	public void setCodeSequence(final String codeSequence) {
		this.codeSequence = codeSequence;
	}
	
	@Override
	public String toString() {
		return "TestOrderResult [patient=" + this.patient + ", provider=" + this.provider + ", location="
		        + this.location + ", dateCreation=" + this.dateCreation + ", encounterRequest=" + this.encounterRequest
		        + ", encounterResult=" + this.encounterResult + ", items=" + this.items + ", codeSequence="
		        + this.codeSequence + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = (prime * result) + ((this.encounterRequest == null) ? 0 : this.encounterRequest.hashCode());
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
		final TestOrderResult other = (TestOrderResult) obj;
		
		if (this.encounterRequest == null) {
			if (other.encounterRequest != null) {
				return false;
			}
		} else if (!this.encounterRequest.equals(other.encounterRequest)) {
			return false;
		}
		return true;
	}
	
	@Override
	public String getUuid() {
		
		if (this.encounterRequest != null) {
			return this.encounterRequest.getUuid();
		}
		return super.getUuid();
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
