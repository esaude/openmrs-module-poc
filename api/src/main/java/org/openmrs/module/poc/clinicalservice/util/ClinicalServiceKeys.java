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
package org.openmrs.module.poc.clinicalservice.util;

public enum ClinicalServiceKeys {
	
	SOCIAL_INFO_ADULT("001"),
	
	SOCIAL_INFO_PEDIATRICS("002"),
	
	VITALS_ADULT("003"),
	
	VITALS_PEDIATRICS("004"),
	
	WHO_STAGE_ADULT("006"),
	
	WHO_STAGE_PEDIATRICS("012"),
	
	RELEVANT_ASPECTS("007"),
	
	ADULT_ANAMNESE_EXAMS("008"),
	
	PEDIATRICS_ANAMNESE_EXAMS("010"),
	
	ADULT_DIAGNOSIS("009"),
	
	PEDIATRICS_DIAGNOSIS("011");
	
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
		throw new IllegalArgumentException("poc.error.clinical.service.not.found.for.given.code");
	}
}
