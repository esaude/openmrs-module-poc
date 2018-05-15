/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc.web.resource;

import java.util.Date;
import java.util.List;

import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.poc.api.common.util.DateUtils;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9.VisitResource1_9;

/**
 * @author Edrisse Mussá
 */
@Resource(name = RestConstants.VERSION_1 + "/pocvisit", supportedClass = Visit.class, supportedOpenmrsVersions = {
        "1.10.*", "1.11.*", "1.12.*", "2.0.*" })
public class PocVisitResource extends VisitResource1_9 {
	
	private static final String FIRST_CONSULTATION_VISIT_TYPE = "64a510a1-fbf4-465f-acd2-cd37bc321cee";
	
	private static final String FOLLOW_UP_CONSULTATION_VISIT_TYPE_UUID = "3866891d-09c5-4d98-98de-6ae7916110fa";
	
	@Override
	public Visit save(Visit visit) {
		VisitService visitService = Context.getVisitService();
		List<Visit> existingVisits = visitService.getVisitsByPatient(visit.getPatient(), true, false);
		if (!existingVisits.isEmpty()) {
			VisitType visitType = visitService.getVisitTypeByUuid(FOLLOW_UP_CONSULTATION_VISIT_TYPE_UUID);
			visit.setVisitType(visitType);
		}
		Date startTime = visit.getStartDatetime();
		visit.setStopDatetime(DateUtils.highDateTime(startTime));
		visit.setDateCreated(startTime);
		return visitService.saveVisit(visit);
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addRequiredProperty("patient");
		description.addRequiredProperty("location");
		
		// setado pela superclasse
		description.addProperty("startDatetime");
		
		// setado pelo metodo create
		description.addProperty("visitType");
		return description;
	}
	
	@Override
	public Object create(SimpleObject propertiesToCreate, RequestContext context) throws ResponseException {
		// precisamos setar visitType com algum valor para passar pela validação
		// do VisitValidador. Para já vamos sempre setar como primeira consulta
		// e mudar mais adiante se for necessário
		propertiesToCreate.add("visitType", FIRST_CONSULTATION_VISIT_TYPE);
		return super.create(propertiesToCreate, context);
	}
}
