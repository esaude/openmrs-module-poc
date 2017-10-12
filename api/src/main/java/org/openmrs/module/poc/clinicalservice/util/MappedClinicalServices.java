package org.openmrs.module.poc.clinicalservice.util;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

public class MappedClinicalServices {
	
	private static EnumMap<ClinicalServiceKeys, List<String>> clinicalServices = new EnumMap<ClinicalServiceKeys, List<String>>(
	        ClinicalServiceKeys.class);
	static {
		
		// SOCIAL_INFO_ADULT
		MappedClinicalServices.clinicalServices.put(ClinicalServiceKeys.SOCIAL_INFO_ADULT, Arrays.asList(
		    ConceptUUIDConstants.NAME, ConceptUUIDConstants.SURNAME, ConceptUUIDConstants.TELEPHONE,
		    ConceptUUIDConstants.OCCUPATION, ConceptUUIDConstants.EDUCATION_LEVEL,
		    ConceptUUIDConstants.NUMBER_OF_COHABITANTS, ConceptUUIDConstants.HIV_SEROLOGY_OF_THE_SPOUSE,
		    ConceptUUIDConstants.NUMBER_OF_CHILDREN, ConceptUUIDConstants.NUMBER_OF_CHILDREN_TESTED,
		    ConceptUUIDConstants.NUMBER_OF_CHILDREN_HIV_POSETIVE, ConceptUUIDConstants.GLACIER_AT_HOME,
		    ConceptUUIDConstants.ELECTRICITY_AT_HOME));
		
		// SOCIAL_INFO_CHILD
		MappedClinicalServices.clinicalServices.put(ClinicalServiceKeys.SOCIAL_INFO_CHILD, Arrays.asList(
		    ConceptUUIDConstants.NAME, ConceptUUIDConstants.SURNAME, ConceptUUIDConstants.TELEPHONE,
		    ConceptUUIDConstants.MOTHERS_NAME, ConceptUUIDConstants.MOTHERS_AGE, ConceptUUIDConstants.MOTHER_IS_ALIVE,
		    ConceptUUIDConstants.MOTHER_IS_SICK, ConceptUUIDConstants.MOTHERS_DESEASE,
		    ConceptUUIDConstants.MOTHER_HIV_RESULT, ConceptUUIDConstants.MOTHER_TREATING_WITH_ANTIRETROVIRALS,
		    ConceptUUIDConstants.FATHERS_NAME, ConceptUUIDConstants.FATHERS_AGE, ConceptUUIDConstants.FATHER_IS_ALIVE,
		    ConceptUUIDConstants.FATHER_IS_SICK, ConceptUUIDConstants.FATHERS_DESEASE,
		    ConceptUUIDConstants.FATHER_HIV_RESULT, ConceptUUIDConstants.FATHER_TREATING_WITH_ANTIRETROVIRALS));
		
		// VITALS_ADULT
		MappedClinicalServices.clinicalServices.put(ClinicalServiceKeys.VITALS_ADULT, Arrays.asList(
		    ConceptUUIDConstants.TEMPERATURE, ConceptUUIDConstants.WEIGHT, ConceptUUIDConstants.HEIGHT,
		    ConceptUUIDConstants.SISTOLICA_ARTERIAL_BLOOD_PRESSURE, ConceptUUIDConstants.DIALOSTIC_BLOOD_PRESSURE,
		    ConceptUUIDConstants.HEART_RATE, ConceptUUIDConstants.RESPIRATORY_FREQUENCY));
		// VITALS_CHILD
		MappedClinicalServices.clinicalServices.put(ClinicalServiceKeys.VITALS_CHILD, Arrays.asList(
		    ConceptUUIDConstants.TEMPERATURE, ConceptUUIDConstants.WEIGHT, ConceptUUIDConstants.HEIGHT,
		    ConceptUUIDConstants.HEART_RATE, ConceptUUIDConstants.RESPIRATORY_FREQUENCY));
	}
	
	private MappedClinicalServices() {
	}
	
	public static List<String> getClinicalServices(final ClinicalServiceKeys serviceKey) {
		return MappedClinicalServices.clinicalServices.get(serviceKey);
	}
}
