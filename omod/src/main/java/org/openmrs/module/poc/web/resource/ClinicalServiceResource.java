/**
 *
 */
package org.openmrs.module.poc.web.resource;

import org.openmrs.Encounter;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.poc.api.common.exception.POCBusinessException;
import org.openmrs.module.poc.clinicalservice.service.ClinicalServiceService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9.EncounterResource1_9;

/**
 *
 */

@Resource(name = RestConstants.VERSION_1 + "/clinicalservice", order = 2, supportedClass = Encounter.class, supportedOpenmrsVersions = {
        "1.11.*", "1.12.*" })
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
}
