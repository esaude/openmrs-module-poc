/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc.clinicalservice.util;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

public class MappedClinicalServices {
	
	private static EnumMap<ClinicalServiceKeys, List<String>> clinicalServices = new EnumMap<>(
	        ClinicalServiceKeys.class);
	static {
		
		// SOCIAL_INFO_ADULT
		MappedClinicalServices.clinicalServices.put(ClinicalServiceKeys.SOCIAL_INFO_ADULT,
		    Arrays.asList(ClinicalServicesConceptUUIDConstants.NAME, ClinicalServicesConceptUUIDConstants.SURNAME,
		        ClinicalServicesConceptUUIDConstants.TELEPHONE, ClinicalServicesConceptUUIDConstants.OCCUPATION,
		        ClinicalServicesConceptUUIDConstants.EDUCATION_LEVEL,
		        ClinicalServicesConceptUUIDConstants.NUMBER_OF_COHABITANTS,
		        ClinicalServicesConceptUUIDConstants.HIV_SEROLOGY_OF_THE_SPOUSE,
		        ClinicalServicesConceptUUIDConstants.NUMBER_OF_CHILDREN,
		        ClinicalServicesConceptUUIDConstants.NUMBER_OF_CHILDREN_TESTED,
		        ClinicalServicesConceptUUIDConstants.NUMBER_OF_CHILDREN_HIV_POSETIVE,
		        ClinicalServicesConceptUUIDConstants.GLACIER_AT_HOME,
		        ClinicalServicesConceptUUIDConstants.ELECTRICITY_AT_HOME));
		
		// SOCIAL_INFO_CHILD
		MappedClinicalServices.clinicalServices.put(ClinicalServiceKeys.SOCIAL_INFO_PEDIATRICS, Arrays.asList(
		    ClinicalServicesConceptUUIDConstants.NAME, ClinicalServicesConceptUUIDConstants.SURNAME,
		    ClinicalServicesConceptUUIDConstants.TELEPHONE, ClinicalServicesConceptUUIDConstants.MOTHERS_NAME,
		    ClinicalServicesConceptUUIDConstants.MOTHERS_AGE, ClinicalServicesConceptUUIDConstants.MOTHER_IS_ALIVE,
		    ClinicalServicesConceptUUIDConstants.MOTHER_IS_SICK,
		    ClinicalServicesConceptUUIDConstants.MOTHERS_DESEASE,
		    ClinicalServicesConceptUUIDConstants.MOTHER_HIV_RESULT,
		    ClinicalServicesConceptUUIDConstants.MOTHER_TREATING_WITH_ANTIRETROVIRALS,
		    ClinicalServicesConceptUUIDConstants.FATHERS_NAME, ClinicalServicesConceptUUIDConstants.FATHERS_AGE,
		    ClinicalServicesConceptUUIDConstants.FATHER_IS_ALIVE,
		    ClinicalServicesConceptUUIDConstants.FATHER_IS_SICK,
		    ClinicalServicesConceptUUIDConstants.FATHERS_DESEASE,
		    ClinicalServicesConceptUUIDConstants.FATHER_HIV_RESULT,
		    ClinicalServicesConceptUUIDConstants.FATHER_TREATING_WITH_ANTIRETROVIRALS));
		
		// VITALS_ADULT
		MappedClinicalServices.clinicalServices.put(ClinicalServiceKeys.VITALS_ADULT,
		    Arrays.asList(ClinicalServicesConceptUUIDConstants.TEMPERATURE,
		        ClinicalServicesConceptUUIDConstants.WEIGHT, ClinicalServicesConceptUUIDConstants.HEIGHT,
		        ClinicalServicesConceptUUIDConstants.SISTOLICA_ARTERIAL_BLOOD_PRESSURE,
		        ClinicalServicesConceptUUIDConstants.DIALOSTIC_BLOOD_PRESSURE,
		        ClinicalServicesConceptUUIDConstants.HEART_RATE,
		        ClinicalServicesConceptUUIDConstants.RESPIRATORY_FREQUENCY,
		        ClinicalServicesConceptUUIDConstants.POC_MAPPING_VITALS_DATE));
		
		// VITALS_PEDIATRICS
		MappedClinicalServices.clinicalServices.put(ClinicalServiceKeys.VITALS_PEDIATRICS,
		    Arrays.asList(ClinicalServicesConceptUUIDConstants.TEMPERATURE,
		        ClinicalServicesConceptUUIDConstants.WEIGHT, ClinicalServicesConceptUUIDConstants.HEIGHT,
		        ClinicalServicesConceptUUIDConstants.HEART_RATE,
		        ClinicalServicesConceptUUIDConstants.RESPIRATORY_FREQUENCY,
		        ClinicalServicesConceptUUIDConstants.POC_MAPPING_VITALS_DATE));
		
		// WHO_STAGE_ADULT
		MappedClinicalServices.clinicalServices.put(ClinicalServiceKeys.WHO_STAGE_ADULT,
		    Arrays.asList(ClinicalServicesConceptUUIDConstants.POC_MAPPING_WHO_DATE,
		        ClinicalServicesConceptUUIDConstants.WHO_STAGE_1_ADULT,
		        ClinicalServicesConceptUUIDConstants.WHO_STAGE_2_ADULT,
		        ClinicalServicesConceptUUIDConstants.WHO_STAGE_3_ADULT,
		        ClinicalServicesConceptUUIDConstants.WHO_STAGE_4_ADULT,
		        ClinicalServicesConceptUUIDConstants.CURRENT_WHO_HIV_STAGE));
		
		// WHO_STAGE_PEDIATRICS
		MappedClinicalServices.clinicalServices.put(ClinicalServiceKeys.WHO_STAGE_PEDIATRICS,
		    Arrays.asList(ClinicalServicesConceptUUIDConstants.POC_MAPPING_WHO_DATE,
		        ClinicalServicesConceptUUIDConstants.WHO_STAGE_1_ADULT,
		        ClinicalServicesConceptUUIDConstants.WHO_STAGE_2_ADULT,
		        ClinicalServicesConceptUUIDConstants.WHO_STAGE_3_ADULT,
		        ClinicalServicesConceptUUIDConstants.WHO_STAGE_4_ADULT,
		        ClinicalServicesConceptUUIDConstants.CURRENT_WHO_HIV_STAGE));
		
		// RELEVANT_ASPECTS
		MappedClinicalServices.clinicalServices.put(ClinicalServiceKeys.RELEVANT_ASPECTS,
		    Arrays.asList(ClinicalServicesConceptUUIDConstants.OBSERVATION_STORY,
		        ClinicalServicesConceptUUIDConstants.TYPE_OF_MESSAGE));
		
		// ADULT_ANAMNESE_EXAMS
		MappedClinicalServices.clinicalServices.put(ClinicalServiceKeys.ADULT_ANAMNESE_EXAMS,
		    Arrays.asList(ClinicalServicesConceptUUIDConstants.POC_MAPPING_ANAMNESE_DATE,
		        ClinicalServicesConceptUUIDConstants.PATIENT_HISTORY,
		        ClinicalServicesConceptUUIDConstants.HEAD_AND_NECK_EXAMS,
		        ClinicalServicesConceptUUIDConstants.CHEST_EXAMS,
		        ClinicalServicesConceptUUIDConstants.ABDOMEN_EXAMS,
		        ClinicalServicesConceptUUIDConstants.UPPER_AND_LOWER_LIMBS_EXAMS,
		        ClinicalServicesConceptUUIDConstants.SCREENING_FOR_STI,
		        ClinicalServicesConceptUUIDConstants.SCREENING_FOR_TB,
		        ClinicalServicesConceptUUIDConstants.SCREENING_FOR_CRIPTO));
		
		// PEDIATRICS_ANAMNESE_EXAMS
		MappedClinicalServices.clinicalServices.put(ClinicalServiceKeys.PEDIATRICS_ANAMNESE_EXAMS,
		    Arrays.asList(ClinicalServicesConceptUUIDConstants.POC_MAPPING_ANAMNESE_DATE,
		        ClinicalServicesConceptUUIDConstants.PATIENT_HISTORY,
		        ClinicalServicesConceptUUIDConstants.HEAD_AND_NECK_EXAMS,
		        ClinicalServicesConceptUUIDConstants.CHEST_EXAMS,
		        ClinicalServicesConceptUUIDConstants.ABDOMEN_EXAMS,
		        ClinicalServicesConceptUUIDConstants.UPPER_AND_LOWER_LIMBS_EXAMS,
		        ClinicalServicesConceptUUIDConstants.SCREENING_FOR_TB,
		        ClinicalServicesConceptUUIDConstants.SCREENING_FOR_CRIPTO));
		
		// ADULT_DIAGNOSIS
		MappedClinicalServices.clinicalServices.put(ClinicalServiceKeys.ADULT_DIAGNOSIS,
		    Arrays.asList(ClinicalServicesConceptUUIDConstants.POC_MAPPING_DIAGNOSIS_DATE,
		        ClinicalServicesConceptUUIDConstants.TB_DRUG_START_DATE,
		        ClinicalServicesConceptUUIDConstants.TB_DRUG_END_DATE,
		        ClinicalServicesConceptUUIDConstants.DIAGNOSIS_ADDED,
		        ClinicalServicesConceptUUIDConstants.NON_CODED_DIAGNOSIS,
		        ClinicalServicesConceptUUIDConstants.RETURN_VISIT_DATE));
		
		// PEDIATRICS_DIAGNOSIS
		MappedClinicalServices.clinicalServices.put(ClinicalServiceKeys.PEDIATRICS_DIAGNOSIS,
		    Arrays.asList(ClinicalServicesConceptUUIDConstants.POC_MAPPING_DIAGNOSIS_DATE,
		        ClinicalServicesConceptUUIDConstants.TB_DRUG_START_DATE,
		        ClinicalServicesConceptUUIDConstants.TB_DRUG_END_DATE,
		        ClinicalServicesConceptUUIDConstants.DIAGNOSIS_ADDED,
		        ClinicalServicesConceptUUIDConstants.NON_CODED_DIAGNOSIS,
		        ClinicalServicesConceptUUIDConstants.RETURN_VISIT_DATE));
	}
	
	public static List<String> getClinicalServices(final ClinicalServiceKeys serviceKey) {
		return MappedClinicalServices.clinicalServices.get(serviceKey);
	}
}
