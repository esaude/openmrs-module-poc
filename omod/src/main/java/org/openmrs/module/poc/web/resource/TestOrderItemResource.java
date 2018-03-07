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
import org.openmrs.module.poc.api.testorder.model.TestOrderItem;
import org.openmrs.module.poc.api.testorder.model.TestOrderPOC;
import org.openmrs.module.poc.api.testorder.service.TestOrderService;
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

@SubResource(parent = TestOrderResource.class, path = "item", supportedClass = TestOrderItem.class, supportedOpenmrsVersions = {
        "1.8.*", "1.9.*", "1.10.*", "1.11.*", "1.12.*" })
public class TestOrderItemResource extends DelegatingSubResource<TestOrderItem, TestOrderPOC, TestOrderResource> {
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(final Representation rep) {
		if ((rep instanceof DefaultRepresentation) || (rep instanceof RefRepresentation)) {
			
			final DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("display");
			description.addProperty("testOrder", Representation.REF);
			description.addProperty("category", Representation.REF);
			description.addSelfLink();
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
	public TestOrderItem newDelegate() {
		return new TestOrderItem();
	}
	
	@Override
	public TestOrderItem save(final TestOrderItem delegate) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public TestOrderPOC getParent(final TestOrderItem instance) {
		return instance.getParent();
	}
	
	@PropertyGetter("display")
	public String getDisplayString(final TestOrderItem item) {
		return item.getTestOrder().getConcept().getDisplayString();
	}
	
	@Override
	public void setParent(final TestOrderItem instance, final TestOrderPOC parent) {
		instance.setParent(parent);
	}
	
	@Override
	public PageableResult doGetAll(final TestOrderPOC parent, final RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public TestOrderItem getByUniqueId(final String uniqueId) {
		
		return Context.getService(TestOrderService.class).findTestOrderItemByUuid(uniqueId);
	}
	
	@Override
	protected void delete(final TestOrderItem delegate, final String reason, final RequestContext context)
	        throws ResponseException {
		Context.getService(TestOrderService.class).deleteTestOrderItem(delegate, reason);
	}
	
	@Override
	public void purge(final TestOrderItem delegate, final RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		final DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("testOrder");
		description.addProperty("category");
		return description;
	}
	
}
