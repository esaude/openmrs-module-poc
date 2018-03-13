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

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.module.poc.testorderresult.model.TestOrderResult;
import org.openmrs.module.poc.testorderresult.service.TestOrderResultService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_11.RestConstants1_11;

@Resource(name = RestConstants.VERSION_1
        + "/testorderresult", supportedClass = TestOrderResult.class, supportedOpenmrsVersions = { "1.10.*", "1.11.*",
        "1.12.*", "2.0.*", "2.1.*" })
public class TestOrderResultResource extends DelegatingCrudResource<TestOrderResult> {
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(final Representation rep) {
		
		if (rep instanceof RefRepresentation) {
			
			final DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("encounterRequest", Representation.REF);
			description.addProperty("encounterResult", Representation.REF);
			description.addProperty("provider", Representation.REF);
			description.addProperty("patient", Representation.REF);
			description.addProperty("location", Representation.REF);
			description.addProperty("dateCreation");
			description.addProperty("status");
			description.addProperty("items");
			
			description.addSelfLink();
			
			return description;
		} else if (rep instanceof DefaultRepresentation) {
			
			final DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("encounterRequest", Representation.REF);
			description.addProperty("encounterResult", Representation.REF);
			description.addProperty("provider", Representation.REF);
			description.addProperty("patient", Representation.REF);
			description.addProperty("location", Representation.REF);
			description.addProperty("dateCreation");
			description.addProperty("status");
			description.addProperty("items");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			
			return description;
		} else if (rep instanceof FullRepresentation) {
			
			final DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("encounterRequest", Representation.REF);
			description.addProperty("encounterResult", Representation.REF);
			description.addProperty("provider");
			description.addProperty("patient");
			description.addProperty("location");
			description.addProperty("dateCreation");
			description.addProperty("status");
			description.addProperty("items");
			description.addSelfLink();
			
			return description;
		} else {
			return null;
		}
	}
	
	@Override
	public TestOrderResult newDelegate() {
		return new TestOrderResult();
	}
	
	@Override
	public TestOrderResult save(final TestOrderResult delegate) {
		
		return Context.getService(TestOrderResultService.class).createTestOrderResult(delegate);
	}
	
	@Override
	public TestOrderResult getByUniqueId(final String uniqueId) {
		
		final Encounter encounter = new Encounter();
		encounter.setUuid(uniqueId);
		return Context.getService(TestOrderResultService.class).findTestOrderResultByTestRequest(encounter);
	}
	
	@PropertyGetter("display")
	public String getDisplayString(final TestOrderResult item) {
		return item.getEncounterResult().getEncounterDatetime() != null
		        ? item.getEncounterResult().getEncounterDatetime().toString() : StringUtils.EMPTY;
	}
	
	@Override
	protected PageableResult doSearch(final RequestContext context) {
		final String patient = context.getParameter("patient");
		
		if (StringUtils.isNotBlank(patient)) {
			return new NeedsPaging<>(
			        Context.getService(TestOrderResultService.class).findTestOrderResultsByPatient(patient), context);
		}
		
		return new EmptySearchResult();
	}
	
	@Override
	protected void delete(final TestOrderResult delegate, final String reason, final RequestContext context)
	        throws ResponseException {
		
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		final DelegatingResourceDescription description = new DelegatingResourceDescription();
		
		description.addProperty("encounterRequest");
		description.addProperty("provider");
		description.addProperty("dateCreation");
		description.addProperty("items");
		
		return description;
	}
	
	@Override
	public List<String> getPropertiesToExposeAsSubResources() {
		return Arrays.asList("items");
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getUpdatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
		
		final DelegatingResourceDescription description = super.getUpdatableProperties();
		description.addProperty("items");
		
		return description;
	}
	
	@Override
	public String getResourceVersion() {
		return RestConstants1_11.RESOURCE_VERSION;
	}
	
	@Override
	public void purge(final TestOrderResult delegate, final RequestContext context) throws ResponseException {
	}
}
