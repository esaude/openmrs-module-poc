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

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.springframework.validation.BindException;

import java.util.Calendar;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class POCPatientProgramValidatorTest {
	
	private PatientProgram patientProgram;
	
	private POCPatientProgramValidator pocPatientProgramValidator;
	
	private BindException errors;
	
	@Before
	public void setUp() {
		Program program = new Program();
		Patient patient = new Patient();
		patientProgram = new PatientProgram();
		patientProgram.setProgram(program);
		patientProgram.setPatient(patient);
		
		pocPatientProgramValidator = new POCPatientProgramValidator();
		
		errors = new BindException(patientProgram, "patientProgram");
	}
	
	/**
	 * @see POCPatientProgramValidator#validatePTV(Object, org.springframework.validation.Errors)
	 */
	@Test
	public void shouldFailGivenTargetWithPTVProgramAndMalePatientIsAged6YearsOrOlder() throws Exception {
		patientProgram.getProgram().setUuid(POCPatientProgramValidator.PTV_ETV_PROGRAM_UUID);
		patientProgram.getPatient().setBirthdateFromAge(6, null);
		patientProgram.getPatient().setGender("M");
		
		pocPatientProgramValidator.validate(patientProgram, errors);
		
		assertThat(errors.getGlobalError().getCode(), is("error.patientProgram.patientMustBeAged5OrYounger"));
	}
	
	/**
	 * @see POCPatientProgramValidator#validatePTV(Object, org.springframework.validation.Errors)
	 */
	@Test
	public void shouldPassGiveTargetWithPTVProgramAndMalePatientAged5YearsOrYounger() throws Exception {
		patientProgram.getProgram().setUuid(POCPatientProgramValidator.PTV_ETV_PROGRAM_UUID);
		patientProgram.getPatient().setBirthdateFromAge(5, null);
		patientProgram.getPatient().setGender("M");
		
		pocPatientProgramValidator.validate(patientProgram, errors);
		
		assertThat(errors.hasGlobalErrors(), is(false));
	}
	
	/**
	 * @see POCPatientProgramValidator#validatePTV(Object, org.springframework.validation.Errors)
	 */
	@Test
	public void shouldPassGivenTargetWithPTVProgramAndFemalePatient() throws Exception {
		patientProgram.getProgram().setUuid(POCPatientProgramValidator.PTV_ETV_PROGRAM_UUID);
		patientProgram.getPatient().setBirthdateFromAge(6, null);
		patientProgram.getPatient().setGender("F");
		
		pocPatientProgramValidator.validate(patientProgram, errors);
		
		assertThat(errors.hasGlobalErrors(), is(false));
	}
	
	/**
	 * @see POCPatientProgramValidator#validateCCR(Object, org.springframework.validation.Errors)
	 */
	@Test
	public void shouldFailGivenTargetWithCCRProgramAndPatientAged18MonthsOrOlder() throws Exception {
		patientProgram.getProgram().setUuid(POCPatientProgramValidator.CCR_PROGRAM_UUID);
		Calendar monthsAgo = Calendar.getInstance();
		monthsAgo.add(Calendar.MONTH, -18);
		patientProgram.getPatient().setBirthdate(monthsAgo.getTime());
		
		pocPatientProgramValidator.validate(patientProgram, errors);
		
		assertThat(errors.getGlobalError().getCode(),
		    is("error.patientProgram.patientMustBeYoungerThan18Months"));
	}
	
	/**
	 * @see POCPatientProgramValidator#validateCCR(Object, org.springframework.validation.Errors)
	 */
	@Test
	public void shouldPassGivenTargetWithCCRProgramAndPatientAged17MonthsOrYounger() throws Exception {
		patientProgram.getProgram().setUuid(POCPatientProgramValidator.CCR_PROGRAM_UUID);
		Calendar monthsAgo = Calendar.getInstance();
		monthsAgo.add(Calendar.MONTH, -17);
		patientProgram.getPatient().setBirthdate(monthsAgo.getTime());
		
		pocPatientProgramValidator.validate(patientProgram, errors);
		
		assertThat(errors.hasGlobalErrors(), is(false));
	}
}
