/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc.checkin.service;

import java.util.Date;
import java.util.List;

import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.APIException;
import org.openmrs.api.VisitService;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.poc.api.common.model.helper.PocModule;
import org.openmrs.module.poc.api.common.util.DateUtils;
import org.openmrs.module.poc.api.common.util.OPENMRSUUIDs;
import org.openmrs.module.poc.checkin.model.helper.Checkin;
import org.openmrs.module.poc.pocheuristic.service.PocHeuristicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional(noRollbackFor = APIException.class)
public class CheckinServiceImpl extends BaseOpenmrsService implements CheckinService {
	
	@Autowired
	private VisitService visitService;
	
	@Autowired
	private PocHeuristicService pocHeuristicService;
	
	@Override
	public void checkin(Checkin checkin) {
		Date now = new Date();
		List<Visit> todaysVisits = pocHeuristicService.findVisits(checkin.getPatient(), null, now, false);
		if (todaysVisits.isEmpty()) {
			VisitType visitType = getAppropriateVisitType(checkin);
			Visit visit = new Visit();
			visit.setPatient(checkin.getPatient());
			visit.setLocation(checkin.getLocation());
			visit.setVisitType(visitType);
			visit.setStartDatetime(now);
			visit.setDateCreated(now);
			visit.setStopDatetime(DateUtils.highDateTime(now));
			visitService.saveVisit(visit);
		} else {
			throw new IllegalStateException("Paciente " + checkin.getPatient().getUuid() + " já fez check-in");
		}
	}
	
	private VisitType getAppropriateVisitType(Checkin checkin) {
		String visitTypeUuid = OPENMRSUUIDs.FOLLOW_UP_CONSULTATION_VISIT_TYPE;
		if (checkin.getModule() == PocModule.PHARMACY) {
			visitTypeUuid = OPENMRSUUIDs.PHARMACY_PICKUP_VISIT_TYPE;
		} else {
			List<Visit> previousVisits = pocHeuristicService.findVisits(checkin.getPatient(), null, null, false);
			if (previousVisits.isEmpty()) {
				visitTypeUuid = OPENMRSUUIDs.FIRST_CONSULTATION_VISIT_TYPE;
			}
		}
		return visitService.getVisitTypeByUuid(visitTypeUuid);
	}
}
