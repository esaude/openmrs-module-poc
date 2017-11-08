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
		    Arrays.asList(ConceptUUIDConstants.NAME, ConceptUUIDConstants.SURNAME,
		        ConceptUUIDConstants.TELEPHONE, ConceptUUIDConstants.OCCUPATION,
		        ConceptUUIDConstants.EDUCATION_LEVEL,
		        ConceptUUIDConstants.NUMBER_OF_COHABITANTS,
		        ConceptUUIDConstants.HIV_SEROLOGY_OF_THE_SPOUSE,
		        ConceptUUIDConstants.NUMBER_OF_CHILDREN,
		        ConceptUUIDConstants.NUMBER_OF_CHILDREN_TESTED,
		        ConceptUUIDConstants.NUMBER_OF_CHILDREN_HIV_POSETIVE,
		        ConceptUUIDConstants.GLACIER_AT_HOME,
		        ConceptUUIDConstants.ELECTRICITY_AT_HOME));
		
		// SOCIAL_INFO_CHILD
		MappedClinicalServices.clinicalServices.put(ClinicalServiceKeys.SOCIAL_INFO_PEDIATRICS, Arrays.asList(
		    ConceptUUIDConstants.NAME, ConceptUUIDConstants.SURNAME,
		    ConceptUUIDConstants.TELEPHONE, ConceptUUIDConstants.MOTHERS_NAME,
		    ConceptUUIDConstants.MOTHERS_AGE, ConceptUUIDConstants.MOTHER_IS_ALIVE,
		    ConceptUUIDConstants.MOTHER_IS_SICK,
		    ConceptUUIDConstants.MOTHERS_DESEASE,
		    ConceptUUIDConstants.MOTHER_HIV_RESULT,
		    ConceptUUIDConstants.MOTHER_TREATING_WITH_ANTIRETROVIRALS,
		    ConceptUUIDConstants.FATHERS_NAME, ConceptUUIDConstants.FATHERS_AGE,
		    ConceptUUIDConstants.FATHER_IS_ALIVE,
		    ConceptUUIDConstants.FATHER_IS_SICK,
		    ConceptUUIDConstants.FATHERS_DESEASE,
		    ConceptUUIDConstants.FATHER_HIV_RESULT,
		    ConceptUUIDConstants.FATHER_TREATING_WITH_ANTIRETROVIRALS));
		
		// VITALS_ADULT
		MappedClinicalServices.clinicalServices.put(ClinicalServiceKeys.VITALS_ADULT,
		    Arrays.asList(ConceptUUIDConstants.TEMPERATURE,
		        ConceptUUIDConstants.WEIGHT, ConceptUUIDConstants.HEIGHT,
		        ConceptUUIDConstants.SISTOLICA_ARTERIAL_BLOOD_PRESSURE,
		        ConceptUUIDConstants.DIALOSTIC_BLOOD_PRESSURE,
		        ConceptUUIDConstants.HEART_RATE,
		        ConceptUUIDConstants.RESPIRATORY_FREQUENCY,
		        ConceptUUIDConstants.POC_MAPPING_VITALS_DATE));
		
		// VITALS_PEDIATRICS
		MappedClinicalServices.clinicalServices.put(ClinicalServiceKeys.VITALS_PEDIATRICS,
		    Arrays.asList(ConceptUUIDConstants.TEMPERATURE,
		        ConceptUUIDConstants.WEIGHT, ConceptUUIDConstants.HEIGHT,
		        ConceptUUIDConstants.HEART_RATE,
		        ConceptUUIDConstants.RESPIRATORY_FREQUENCY,
		        ConceptUUIDConstants.POC_MAPPING_VITALS_DATE));
		
		// WHO_STAGE_ADULT
		MappedClinicalServices.clinicalServices.put(ClinicalServiceKeys.WHO_STAGE_ADULT,
		    Arrays.asList(ConceptUUIDConstants.POC_MAPPING_WHO_DATE,
		        ConceptUUIDConstants.WHO_STAGE_1_ADULT,
		        ConceptUUIDConstants.WHO_STAGE_2_ADULT,
		        ConceptUUIDConstants.WHO_STAGE_3_ADULT,
		        ConceptUUIDConstants.WHO_STAGE_4_ADULT,
		        ConceptUUIDConstants.CURRENT_WHO_HIV_STAGE));
		
		// WHO_STAGE_PEDIATRICS
		MappedClinicalServices.clinicalServices.put(ClinicalServiceKeys.WHO_STAGE_PEDIATRICS,
		    Arrays.asList(ConceptUUIDConstants.POC_MAPPING_WHO_DATE,
		        ConceptUUIDConstants.WHO_STAGE_1_ADULT,
		        ConceptUUIDConstants.WHO_STAGE_2_ADULT,
		        ConceptUUIDConstants.WHO_STAGE_3_ADULT,
		        ConceptUUIDConstants.WHO_STAGE_4_ADULT,
		        ConceptUUIDConstants.CURRENT_WHO_HIV_STAGE));
		
		// RELEVANT_ASPECTS
		MappedClinicalServices.clinicalServices.put(ClinicalServiceKeys.RELEVANT_ASPECTS,
		    Arrays.asList(ConceptUUIDConstants.OBSERVATION_STORY,
		        ConceptUUIDConstants.TYPE_OF_MESSAGE));
		
		// ADULT_ANAMNESE_EXAMS
		MappedClinicalServices.clinicalServices.put(ClinicalServiceKeys.ADULT_ANAMNESE_EXAMS,
		    Arrays.asList(ConceptUUIDConstants.POC_MAPPING_ANAMNESE_DATE,
		        ConceptUUIDConstants.PATIENT_HISTORY,
		        ConceptUUIDConstants.HEAD_AND_NECK_EXAMS,
		        ConceptUUIDConstants.CHEST_EXAMS,
		        ConceptUUIDConstants.ABDOMEN_EXAMS,
		        ConceptUUIDConstants.UPPER_AND_LOWER_LIMBS_EXAMS,
		        ConceptUUIDConstants.SCREENING_FOR_STI,
		        ConceptUUIDConstants.SCREENING_FOR_TB,
		        ConceptUUIDConstants.SCREENING_FOR_CRIPTO));
		
		// PEDIATRICS_ANAMNESE_EXAMS
		MappedClinicalServices.clinicalServices.put(ClinicalServiceKeys.PEDIATRICS_ANAMNESE_EXAMS,
		    Arrays.asList(ConceptUUIDConstants.POC_MAPPING_ANAMNESE_DATE,
		        ConceptUUIDConstants.PATIENT_HISTORY,
		        ConceptUUIDConstants.HEAD_AND_NECK_EXAMS,
		        ConceptUUIDConstants.CHEST_EXAMS,
		        ConceptUUIDConstants.ABDOMEN_EXAMS,
		        ConceptUUIDConstants.UPPER_AND_LOWER_LIMBS_EXAMS,
		        ConceptUUIDConstants.SCREENING_FOR_TB,
		        ConceptUUIDConstants.SCREENING_FOR_CRIPTO));
		
		// ADULT_DIAGNOSIS
		MappedClinicalServices.clinicalServices.put(ClinicalServiceKeys.ADULT_DIAGNOSIS,
		    Arrays.asList(ConceptUUIDConstants.POC_MAPPING_DIAGNOSIS_DATE,
		        ConceptUUIDConstants.TB_DRUG_START_DATE,
		        ConceptUUIDConstants.TB_DRUG_END_DATE,
		        ConceptUUIDConstants.DIAGNOSIS_ADDED,
		        ConceptUUIDConstants.NON_CODED_DIAGNOSIS,
		        ConceptUUIDConstants.RETURN_VISIT_DATE));
		
		// PEDIATRICS_DIAGNOSIS
		MappedClinicalServices.clinicalServices.put(ClinicalServiceKeys.PEDIATRICS_DIAGNOSIS,
		    Arrays.asList(ConceptUUIDConstants.POC_MAPPING_DIAGNOSIS_DATE,
		        ConceptUUIDConstants.TB_DRUG_START_DATE,
		        ConceptUUIDConstants.TB_DRUG_END_DATE,
		        ConceptUUIDConstants.DIAGNOSIS_ADDED,
		        ConceptUUIDConstants.NON_CODED_DIAGNOSIS,
		        ConceptUUIDConstants.RETURN_VISIT_DATE));
	}
	
	public static List<String> getClinicalServices(final ClinicalServiceKeys serviceKey) {
		return MappedClinicalServices.clinicalServices.get(serviceKey);
	}
}
