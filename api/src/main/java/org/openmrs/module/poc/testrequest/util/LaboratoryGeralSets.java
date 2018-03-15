/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc.testrequest.util;

import org.openmrs.module.poc.api.common.util.OPENMRSUUIDs;

public enum LaboratoryGeralSets {
	
	HEMOGRAMA_KX21N(OPENMRSUUIDs.HEMOGRAMA_KX21N_CONCEPT_SET_UUID),
	
	IMMUNOLOGIA(OPENMRSUUIDs.IMMUNOLOGIA_CONCEPT_SET_UUID),
	
	TESTAGEM_VIROLOGIA(OPENMRSUUIDs.TESTAGEM_VIROLOGIA_CONCEPT_SET_UUID),
	
	BIOQUIMICA_LABORATORIO(OPENMRSUUIDs.BIOQUIMICA_LABORATORIO_CONCEPT_SET_UUID),
	
	PCR(OPENMRSUUIDs.PCR_CONCEPT_SET_UUID),
	
	TUBERCULOSIS_EXAMS_SET(OPENMRSUUIDs.TUBERCULOSIS_EXAMS_SET_CONCEPT_SET_UUID);
	
	private String uuid;
	
	LaboratoryGeralSets(final String uuid) {
		this.uuid = uuid;
	}
	
	public String getUuid() {
		return this.uuid;
	}
	
	public static LaboratoryGeralSets getLaboratoryConvSetByCode(final String code) {
		
		for (final LaboratoryGeralSets labSet : LaboratoryGeralSets.values()) {
			
			if (labSet.getUuid().equals(code)) {
				return labSet;
			}
		}
		throw new IllegalArgumentException("poc.error.clinical.service.not.found.for.given.code");
	}
}
