/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc.api.pocheuristic.service;

import java.util.Date;
import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.poc.api.common.util.OPENMRSUUIDs;
import org.openmrs.module.poc.api.pocheuristic.dao.PocHeuristicCAO;
import org.springframework.transaction.annotation.Transactional;

@Transactional(noRollbackFor = APIException.class)
public class PocHeuristicServiceImpl extends BaseOpenmrsService implements PocHeuristicService {
	
	private PocHeuristicCAO pocHeuristicCAO;
	
	@Override
	public void setPocHeuristicCAO(final PocHeuristicCAO pocHeuristicCAO) {
		this.pocHeuristicCAO = pocHeuristicCAO;
	}
	
	@Override
	public EncounterType findSeguimentoPacienteEncounterTypeByPatientAge(final Patient patient) {
		if (patient != null) {
			return Context.getEncounterService()
			        .getEncounterTypeByUuid(patient.getAge() < 15 ? OPENMRSUUIDs.ARV_FOLLOW_UP_CHILD_ENCOUNTER_TYPE_UUID
			                : OPENMRSUUIDs.ARV_FOLLOW_UP_ADULT_ENCOUNTER_TYPE_UUID);
		}
		throw new APIException(
		        Context.getMessageSourceService().getMessage("poc.error.encountetype.notfound.for.non.given.patient"));
	}
	
	@Override
	public Encounter findLastEncounterByPatientAndEncounterTypeAndLocationAndDateAndStatus(final Patient patient,
	        final EncounterType encounterType, final Location location, final Date encounterDateTime)
	        throws APIException {
		
		return this.pocHeuristicCAO.findLastEncounterByPatientAndEncounterTypeAndLocationAndDateAndStatus(patient,
		    encounterType, location, encounterDateTime, false);
	}
	
	@Override
	public List<Encounter> findEncountersWithTestOrdersByPatient(final String patientUUID) {
		return this.pocHeuristicCAO.findEncountersWithTestOrdersByPatient(patientUUID);
	}
}
