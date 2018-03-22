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
import org.openmrs.module.poc.testorderresult.model.TestOrderResult;
import org.openmrs.module.poc.testorderresult.model.TestOrderResultItem;
import org.openmrs.module.poc.testorderresult.service.TestOrderResultService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@SubResource(parent = TestOrderResultResource.class, path = "item", supportedClass = TestOrderResultItem.class, supportedOpenmrsVersions = {
        "1.8.*", "1.9.*", "1.10.*", "1.11.*", "1.12.*" })
public class TestOrderResultItemResource
        extends DelegatingSubResource<TestOrderResultItem, TestOrderResult, TestOrderResultResource> {
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(final Representation rep) {
		if ((rep instanceof DefaultRepresentation) || (rep instanceof RefRepresentation)) {
			
			final DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("testOrder", Representation.REF);
			description.addProperty("category", Representation.REF);
			description.addProperty("value");
			description.addProperty("status");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			
			return description;
		} else if (rep instanceof FullRepresentation) {
			
			final DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("testOrder");
			description.addProperty("category");
			description.addProperty("value");
			description.addProperty("status");
			
			return description;
		}
		return null;
	}
	
	@Override
	public TestOrderResultItem newDelegate() {
		return new TestOrderResultItem();
	}
	
	@Override
	public TestOrderResultItem save(final TestOrderResultItem delegate) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public TestOrderResult getParent(final TestOrderResultItem instance) {
		return instance.getParent();
	}
	
	@PropertyGetter("display")
	public String getDisplayString(final TestOrderResultItem item) {
		return item.getTestOrder().getConcept().getDisplayString();
	}
	
	@Override
	public void setParent(final TestOrderResultItem instance, final TestOrderResult parent) {
		instance.setParent(parent);
	}
	
	@Override
	public PageableResult doGetAll(final TestOrderResult parent, final RequestContext context)
	        throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public TestOrderResultItem getByUniqueId(final String uniqueId) {
		
		return Context.getService(TestOrderResultService.class).findTestOrderResultItemByUuid(uniqueId);
	}
	
	@Override
	protected void delete(final TestOrderResultItem delegate, final String reason, final RequestContext context)
	        throws ResponseException {
		Context.getService(TestOrderResultService.class).deleteTestOrderResultItem(delegate, reason);
	}
	
	@Override
	public void purge(final TestOrderResultItem delegate, final RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		final DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("testOrder");
		description.addProperty("value");
		return description;
	}
}
