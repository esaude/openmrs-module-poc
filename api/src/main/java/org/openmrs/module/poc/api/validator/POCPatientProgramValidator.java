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

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.annotation.Handler;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Handler(supports = { PatientProgram.class }, order = 51)
public class POCPatientProgramValidator implements Validator {
	
	public static final String CCR_PROGRAM_UUID = "611f0a6b-68b7-4de7-bc7a-fd021330eef8";
	
	public static final String PTV_ETV_PROGRAM_UUID = "06057245-ca21-43ab-a02f-e861d7e54593";
	
	public static final int CCR_MAX_AGE = 18; // months
	
	public static final int PTV_MAX_AGE = 5; // years
	
	@Override
	public boolean supports(final Class<?> clazz) {
		return PatientProgram.class.isAssignableFrom(clazz);
	}
	
	/**
	 * @param target The CCR patient program to validate.
	 * @param errors Errors.
	 */
	@Override
	public void validate(final Object target, final Errors errors) {
		final PatientProgram patientProgram = (PatientProgram) target;
		
		if (POCPatientProgramValidator.CCR_PROGRAM_UUID.equals(patientProgram.getProgram().getUuid())) {
			this.validateCCR(target, errors);
		}
		
		if (POCPatientProgramValidator.PTV_ETV_PROGRAM_UUID.equals(patientProgram.getProgram().getUuid())) {
			this.validatePTV(target, errors);
		}
	}
	
	/**
	 * Validates the given CCR PatientProgram.
	 * 
	 * @param target The CCR patient program to validate.
	 * @param errors Errors.
	 */
	private void validateCCR(final Object target, final Errors errors) {
		final PatientProgram patientProgram = (PatientProgram) target;
		final Integer ageInMonths = this.patientAgeInMonths(patientProgram.getPatient());
		
		if (ageInMonths >= POCPatientProgramValidator.CCR_MAX_AGE) {
			errors.reject("poc.error.patientProgram.patientMustBeYoungerThan18Months");
		}
		
	}
	
	/**
	 * Validates the given PTV PatientProgram.
	 * 
	 * @param target The PTV patient program to validate.
	 * @param errors Errors.
	 */
	private void validatePTV(final Object target, final Errors errors) {
		final Patient patient = ((PatientProgram) target).getPatient();
		
		if ((patient.getAge() > POCPatientProgramValidator.PTV_MAX_AGE) && "M".equals(patient.getGender())) {
			errors.reject("poc.error.patientProgram.patientMustBeAged5OrYounger");
		}
		
	}
	
	private Integer patientAgeInMonths(final Patient patient) {
		
		if ((patient == null) || (patient.getBirthdate() == null)) {
			return null; // if there is error in patient's data return age as
			// null
		}
		final Date birthdate = patient.getBirthdate();
		final DateTime today = new DateTime();
		final DateTime dob = new DateTime(birthdate.getTime());
		return Months.monthsBetween(dob.toDateMidnight(), today.toDateMidnight()).getMonths();
	}
}
