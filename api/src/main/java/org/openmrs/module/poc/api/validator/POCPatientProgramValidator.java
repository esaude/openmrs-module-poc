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

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.annotation.Handler;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Date;

@Handler(supports = { PatientProgram.class }, order = 51)
public class POCPatientProgramValidator implements Validator {
	
	public static final String CCR_PROGRAM_UUID = "611f0a6b-68b7-4de7-bc7a-fd021330eef8";
	
	public static final String PTV_ETV_PROGRAM_UUID = "06057245-ca21-43ab-a02f-e861d7e54593";
	
	public static final int CCR_MAX_AGE = 18; // months
	
	public static final int PTV_MAX_AGE = 5; // years
	
	@Override
	public boolean supports(Class<?> clazz) {
		return PatientProgram.class.isAssignableFrom(clazz);
	}
	
	/**
	 * @param target The CCR patient program to validate.
	 * @param errors Errors.
	 */
	@Override
	public void validate(Object target, Errors errors) {
		PatientProgram patientProgram = (PatientProgram) target;
		
		if (CCR_PROGRAM_UUID.equals(patientProgram.getProgram().getUuid())) {
			validateCCR(target, errors);
		}
		
		if (PTV_ETV_PROGRAM_UUID.equals(patientProgram.getProgram().getUuid())) {
			validatePTV(target, errors);
		}
	}
	
	/**
	 * Validates the given CCR PatientProgram.
	 * 
	 * @param target The CCR patient program to validate.
	 * @param errors Errors.
	 */
	private void validateCCR(Object target, Errors errors) {
		PatientProgram patientProgram = (PatientProgram) target;
		Integer ageInMonths = patientAgeInMonths(patientProgram.getPatient());
		
		if (ageInMonths >= CCR_MAX_AGE) {
			errors.reject("error.patientProgram.patientMustBeYoungerThan18Months");
		}
		
	}
	
	/**
	 * Validates the given PTV PatientProgram.
	 * 
	 * @param target The PTV patient program to validate.
	 * @param errors Errors.
	 */
	private void validatePTV(Object target, Errors errors) {
		Patient patient = ((PatientProgram) target).getPatient();
		
		if (patient.getAge() > PTV_MAX_AGE && "M".equals(patient.getGender())) {
			errors.reject("error.patientProgram.patientMustBeAged5OrYounger");
		}
		
	}
	
	private Integer patientAgeInMonths(Patient patient) {
		
		if (patient == null || patient.getBirthdate() == null) {
			return null; // if there is error in patient's data return age as null
		}
		Date birthdate = patient.getBirthdate();
		DateTime today = new DateTime();
		DateTime dob = new DateTime(birthdate.getTime());
		return Months.monthsBetween(dob.toDateMidnight(), today.toDateMidnight()).getMonths();
	}
}
