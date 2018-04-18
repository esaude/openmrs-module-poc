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

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.poc.pocheuristic.service.PocHeuristicService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.PatientResource1_8;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9.EncounterResource1_9;

@Resource(name = RestConstants.VERSION_1
        + "/encounterwrap", supportedClass = Encounter.class, supportedOpenmrsVersions = { "1.9.*", "1.10.*", "1.11.*",
        "1.12.*", "2.0.*" })
public class EncounterWrapResource extends EncounterResource1_9 {
	
	@Override
	protected PageableResult doSearch(final RequestContext context) {
		
		final String patientUuid = context.getRequest().getParameter("patient");
		final String encounterTypeUuid = context.getParameter("encounterType");
		
		final Patient patient = ((PatientResource1_8) Context.getService(RestService.class)
		        .getResourceBySupportedClass(Patient.class)).getByUniqueId(patientUuid);
		final EncounterType encounterType = Context.getEncounterService().getEncounterTypeByUuid(encounterTypeUuid);
		
		if ((patient != null) && (encounterType != null)) {
			
			return new NeedsPaging<>(Context.getService(PocHeuristicService.class)
			        .findEncountersByPatientAndEncounterType(patient, encounterType), context);
		}
		return new EmptySearchResult();
	}
}
