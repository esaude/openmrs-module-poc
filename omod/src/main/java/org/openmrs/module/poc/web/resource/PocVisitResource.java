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

import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.poc.pocheuristic.service.PocHeuristicService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9.VisitResource1_9;

/**
 * @author Edrisse Mussá
 */
@Resource(name = RestConstants.VERSION_1 + "/pocvisit", supportedClass = Visit.class, supportedOpenmrsVersions = {
        "1.10.*", "1.11.*", "1.12.*", "2.0.*" })
public class PocVisitResource extends VisitResource1_9 {
	
	@Override
	public SimpleObject search(RequestContext context) throws ResponseException {
		String patientParameter = context.getRequest().getParameter("patient");
		Boolean mostRecentOnly = getBooleanParameter(context.getRequest().getParameter("mostRecentOnly"));
		Boolean currentDateOnly = getBooleanParameter(context.getRequest().getParameter("currentDateOnly"));
		Boolean voided = getBooleanParameter(context.getRequest().getParameter("voided"));
		Patient patient = null;
		if (patientParameter != null) {
			patient = Context.getPatientService().getPatientByUuid(patientParameter);
		}
		Date date = null;
		if (currentDateOnly != null && currentDateOnly.booleanValue()) {
			date = new Date();
		}
		PocHeuristicService heuristicService = Context.getService(PocHeuristicService.class);
		List<Visit> visits = heuristicService.findVisits(patient, mostRecentOnly, date, voided);
		return new NeedsPaging<Visit>(visits, context).toSimpleObject(this);
	}
	
	private Boolean getBooleanParameter(String parameter) {
		if (parameter != null) {
			return Boolean.valueOf(parameter);
		}
		return null;
	}
	
}
