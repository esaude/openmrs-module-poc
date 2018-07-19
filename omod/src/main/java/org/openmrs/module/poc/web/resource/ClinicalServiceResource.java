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
package org.openmrs.module.poc.web.resource;

import java.util.List;
import java.util.Map;

import org.openmrs.Encounter;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.poc.api.common.exception.POCBusinessException;
import org.openmrs.module.poc.clinicalservice.service.ClinicalServiceService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9.EncounterResource1_9;

/**
 *
 */

@Resource(name = RestConstants.VERSION_1
        + "/clinicalservice", order = 2, supportedClass = Encounter.class, supportedOpenmrsVersions = { "1.11.*",
        "1.12.*" })
public class ClinicalServiceResource extends EncounterResource1_9 {
	
	@Override
	protected PageableResult doSearch(final RequestContext context) {
		try {
			Context.getService(ClinicalServiceService.class).deleteClinicalService(context.getParameter("encounter"),
			    context.getParameter("clinicalservice"));
			return new EmptySearchResult();
		}
		catch (final POCBusinessException e) {
			throw new APIException(e);
		}
	}
	
	@Override
	public Encounter update(final String uuid, final SimpleObject propertiesToUpdate, final RequestContext context)
	        throws ResponseException {
		
		final Encounter delegate = this.getByUniqueId(uuid);
		final String serviceCode = (String) propertiesToUpdate.remove("serviceCode");
		final List<Map<String, Object>> lstMapObs = propertiesToUpdate.get("obs");
		return Context.getService(ClinicalServiceService.class).updateClinicalService(serviceCode, delegate, lstMapObs);
	}
}
