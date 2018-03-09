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
package org.openmrs.module.poc.testresult.model;

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
@Table(name = "poc_test_request_result")
public class TestRequestResult extends BaseOpenmrsMetadataWrapper {
	
	private static final long serialVersionUID = 5970480895598422427L;
	
	@Id
	@GeneratedValue
	@Column(name = "test_request_result_id")
	private Integer testRequestResultId;
	
	@ManyToOne
	@JoinColumn(name = "test_request_id")
	private Encounter testRequest;
	
	@ManyToOne
	@JoinColumn(name = "test_result_id")
	private Encounter testResult;
	
	@ManyToOne
	@JoinColumn(name = "patient_id")
	private Patient patient;
	
	public TestRequestResult() {
	}
	
	public TestRequestResult(final Patient patient, final Encounter testRequest) {
		this.patient = patient;
		this.testRequest = testRequest;
	}
	
	@Override
	public Integer getId() {
		return this.testRequestResultId;
	}
	
	@Override
	public void setId(final Integer id) {
		this.testRequestResultId = id;
	}
	
	public Encounter getTestRequest() {
		return this.testRequest;
	}
	
	public void setTestRequest(final Encounter testRequest) {
		this.testRequest = testRequest;
	}
	
	public Encounter getTestResult() {
		return this.testResult;
	}
	
	public void setTestResult(final Encounter testResult) {
		this.testResult = testResult;
	}
	
	public Patient getPatient() {
		return this.patient;
	}
	
	public void setPatient(final Patient patient) {
		this.patient = patient;
	}
}
