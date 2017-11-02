/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc.api.validator;

import java.util.Calendar;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.springframework.validation.BindException;

public class POCPatientProgramValidatorTest {
	
	private PatientProgram patientProgram;
	
	private POCPatientProgramValidator pocPatientProgramValidator;
	
	private BindException errors;
	
	@Before
	public void setUp() {
		final Program program = new Program();
		final Patient patient = new Patient();
		this.patientProgram = new PatientProgram();
		this.patientProgram.setProgram(program);
		this.patientProgram.setPatient(patient);
		
		this.pocPatientProgramValidator = new POCPatientProgramValidator();
		
		this.errors = new BindException(this.patientProgram, "patientProgram");
	}
	
	/**
	 * @see POCPatientProgramValidator#validatePTV(Object, org.springframework.validation.Errors)
	 */
	@Test
	public void shouldFailGivenTargetWithPTVProgramAndMalePatientIsAged6YearsOrOlder() throws Exception {
		this.patientProgram.getProgram().setUuid(POCPatientProgramValidator.PTV_ETV_PROGRAM_UUID);
		this.patientProgram.getPatient().setBirthdateFromAge(6, null);
		this.patientProgram.getPatient().setGender("M");
		
		this.pocPatientProgramValidator.validate(this.patientProgram, this.errors);
		
		MatcherAssert.assertThat(this.errors.getGlobalError().getCode(),
		    CoreMatchers.is("poc.error.patientProgram.patientMustBeAged5OrYounger"));
	}
	
	/**
	 * @see POCPatientProgramValidator#validatePTV(Object, org.springframework.validation.Errors)
	 */
	@Test
	public void shouldPassGiveTargetWithPTVProgramAndMalePatientAged5YearsOrYounger() throws Exception {
		this.patientProgram.getProgram().setUuid(POCPatientProgramValidator.PTV_ETV_PROGRAM_UUID);
		this.patientProgram.getPatient().setBirthdateFromAge(5, null);
		this.patientProgram.getPatient().setGender("M");
		
		this.pocPatientProgramValidator.validate(this.patientProgram, this.errors);
		
		MatcherAssert.assertThat(this.errors.hasGlobalErrors(), CoreMatchers.is(false));
	}
	
	/**
	 * @see POCPatientProgramValidator#validatePTV(Object, org.springframework.validation.Errors)
	 */
	@Test
	public void shouldPassGivenTargetWithPTVProgramAndFemalePatient() throws Exception {
		this.patientProgram.getProgram().setUuid(POCPatientProgramValidator.PTV_ETV_PROGRAM_UUID);
		this.patientProgram.getPatient().setBirthdateFromAge(6, null);
		this.patientProgram.getPatient().setGender("F");
		
		this.pocPatientProgramValidator.validate(this.patientProgram, this.errors);
		
		MatcherAssert.assertThat(this.errors.hasGlobalErrors(), CoreMatchers.is(false));
	}
	
	/**
	 * @see POCPatientProgramValidator#validateCCR(Object, org.springframework.validation.Errors)
	 */
	@Test
	public void shouldFailGivenTargetWithCCRProgramAndPatientAged18MonthsOrOlder() throws Exception {
		this.patientProgram.getProgram().setUuid(POCPatientProgramValidator.CCR_PROGRAM_UUID);
		final Calendar monthsAgo = Calendar.getInstance();
		monthsAgo.add(Calendar.MONTH, -18);
		this.patientProgram.getPatient().setBirthdate(monthsAgo.getTime());
		
		this.pocPatientProgramValidator.validate(this.patientProgram, this.errors);
		
		MatcherAssert.assertThat(this.errors.getGlobalError().getCode(),
		    CoreMatchers.is("poc.error.patientProgram.patientMustBeYoungerThan18Months"));
	}
	
	/**
	 * @see POCPatientProgramValidator#validateCCR(Object, org.springframework.validation.Errors)
	 */
	@Test
	public void shouldPassGivenTargetWithCCRProgramAndPatientAged17MonthsOrYounger() throws Exception {
		this.patientProgram.getProgram().setUuid(POCPatientProgramValidator.CCR_PROGRAM_UUID);
		final Calendar monthsAgo = Calendar.getInstance();
		monthsAgo.add(Calendar.MONTH, -17);
		this.patientProgram.getPatient().setBirthdate(monthsAgo.getTime());
		
		this.pocPatientProgramValidator.validate(this.patientProgram, this.errors);
		
		MatcherAssert.assertThat(this.errors.hasGlobalErrors(), CoreMatchers.is(false));
	}
}
