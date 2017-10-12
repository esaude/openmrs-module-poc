/**
 *
 */
package org.openmrs.module.poc.clinicalservice.util;

public enum ClinicalServiceKeys {
	
	SOCIAL_INFO_ADULT("001"),
	
	SOCIAL_INFO_CHILD("002"),
	
	VITALS_ADULT("003"),
	
	VITALS_CHILD("004");
	
	private String code;
	
	ClinicalServiceKeys(final String code) {
		this.code = code;
	}
	
	public String getCode() {
		return this.code;
	}
	
	public static ClinicalServiceKeys getClinicalServiceByCode(final String code) {
		
		for (final ClinicalServiceKeys clinicalService : ClinicalServiceKeys.values()) {
			
			if (clinicalService.getCode().equals(code)) {
				return clinicalService;
			}
		}
		throw new IllegalArgumentException("Clinical Service not found for given code " + code);
	}
}
