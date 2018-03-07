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

import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.poc.api.testorderrequest.model.TestOrderRequest;
import org.openmrs.module.poc.api.testorderrequest.service.TestOrderRequestService;
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
        + "/testorderrequest", order = 2, supportedClass = TestOrderRequest.class, supportedOpenmrsVersions = {
        "1.11.*", "1.12.*" })
public class TestOrderRequestResource extends DataDelegatingCrudResource<TestOrderRequest> {
	
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
	public TestOrderRequest newDelegate() {
		
		return new TestOrderRequest();
	}
	
	@Override
	public TestOrderRequest save(final TestOrderRequest delegate) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public TestOrderRequest getByUniqueId(final String uniqueId) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@PropertyGetter("display")
	public String getDisplayString(final TestOrderRequest item) {
		return item.getTestOrder().getDisplayString();
	}
	
	@Override
	protected void delete(final TestOrderRequest delegate, final String reason, final RequestContext context)
	        throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public void purge(final TestOrderRequest delegate, final RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	protected NeedsPaging<TestOrderRequest> doGetAll(final RequestContext context) {
		
		final TestOrderRequestService testOrderService = Context.getService(TestOrderRequestService.class);
		final List<TestOrderRequest> testOrderRequests = testOrderService.findAllTestOrderRequest(Context.getLocale());
		return new NeedsPaging<>(testOrderRequests, context);
	}
}
