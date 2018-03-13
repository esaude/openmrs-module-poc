/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
/**
 *
 */
package org.openmrs.module.poc.testorderresult.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.module.poc.api.common.model.BaseOpenmrsMetadataWrapper;

@Entity
@Table(name = "poc_test_order_request_result")
public class TestOrderRequestResult extends BaseOpenmrsMetadataWrapper {
	
	private static final long serialVersionUID = 5970480895598422427L;
	
	@Id
	@GeneratedValue
	@Column(name = "test_order_request_result_id")
	private Integer testOrderRequestResultId;
	
	@ManyToOne
	@JoinColumn(name = "test_order_request_id")
	private Encounter testOrderRequest;
	
	@ManyToOne
	@JoinColumn(name = "test_order_result_id")
	private Encounter testOrderResult;
	
	@ManyToOne
	@JoinColumn(name = "patient_id")
	private Patient patient;
	
	public TestOrderRequestResult() {
	}
	
	public TestOrderRequestResult(final Patient patient, final Encounter testOrderRequest) {
		this.patient = patient;
		this.testOrderRequest = testOrderRequest;
	}
	
	@Override
	public Integer getId() {
		return this.testOrderRequestResultId;
	}
	
	@Override
	public void setId(final Integer id) {
		this.testOrderRequestResultId = id;
	}
	
	public Encounter getTestOrderRequest() {
		return this.testOrderRequest;
	}
	
	public void setTestOrderRequest(final Encounter testOrderRequest) {
		this.testOrderRequest = testOrderRequest;
	}
	
	public Encounter getTestOrderResult() {
		return this.testOrderResult;
	}
	
	public void setTestOrderResult(final Encounter testOrderResult) {
		this.testOrderResult = testOrderResult;
	}
	
	public Patient getPatient() {
		return this.patient;
	}
	
	public void setPatient(final Patient patient) {
		this.patient = patient;
	}
}
