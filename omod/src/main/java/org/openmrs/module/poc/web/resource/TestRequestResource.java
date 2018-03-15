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

import org.openmrs.api.context.Context;
import org.openmrs.module.poc.testrequest.model.TestRequest;
import org.openmrs.module.poc.testrequest.service.TestRequestService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = RestConstants.VERSION_1
        + "/testrequest", order = 2, supportedClass = TestRequest.class, supportedOpenmrsVersions = { "1.11.*",
        "1.12.*" })
public class TestRequestResource extends DataDelegatingCrudResource<TestRequest> {
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(final Representation rep) {
		if ((rep instanceof DefaultRepresentation) || (rep instanceof RefRepresentation)) {
			final DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("display");
			description.addProperty("testOrder");
			description.addProperty("category");
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			final DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("display");
			description.addProperty("testOrder");
			description.addProperty("category");
			return description;
		}
		return null;
	}
	
	@Override
	public TestRequest newDelegate() {
		
		return new TestRequest();
	}
	
	@Override
	public TestRequest save(final TestRequest delegate) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public TestRequest getByUniqueId(final String uniqueId) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@PropertyGetter("display")
	public String getDisplayString(final TestRequest item) {
		return item.getTestOrder().getDisplayString();
	}
	
	@Override
	protected void delete(final TestRequest delegate, final String reason, final RequestContext context)
	        throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public void purge(final TestRequest delegate, final RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	protected NeedsPaging<TestRequest> doGetAll(final RequestContext context) {
		
		return new NeedsPaging<>(Context.getService(TestRequestService.class).findAllTestRequests(), context);
	}
}
