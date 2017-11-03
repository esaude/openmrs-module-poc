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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.openmrs.Location;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.poc.patientconsultation.model.PatientConsultationSummary;
import org.openmrs.module.poc.patientconsultation.service.PatientConsultationSummaryService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;

@Resource(name = RestConstants.VERSION_1
        + "/patientconsultationsummary", order = 1, supportedClass = PatientConsultationSummary.class, supportedOpenmrsVersions = {
        "1.8.*", "1.9.*", "1.10.*", "1.11.*", "1.12.*" })
public class PatientConsultationSummaryResource extends DataDelegatingCrudResource<PatientConsultationSummary> {
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(final Representation rep) {
		
		if (rep instanceof RefRepresentation) {
			final DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("consultationDate");
			description.addProperty("patientConsultations");
			description.addSelfLink();
			return description;
		} else if (rep instanceof DefaultRepresentation) {
			final DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("consultationDate");
			description.addProperty("patientConsultations");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			final DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addSelfLink();
			return description;
		} else {
			return null;
		}
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		
		final DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("patientConsultations");
		return description;
	}
	
	@Override
	public List<String> getPropertiesToExposeAsSubResources() {
		return Arrays.asList("patientConsultations");
	}
	
	@Override
	protected PageableResult doSearch(final RequestContext context) {
		try {
			final Location location = Context.getLocationService()
			        .getLocationByUuid(context.getRequest().getParameter("location"));
			final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
			final Date stardDate = formatter.parse(context.getRequest().getParameter("startDate"));
			final Date endDate = formatter.parse(context.getRequest().getParameter("endDate"));
			
			return new NeedsPaging<>(Context.getService(PatientConsultationSummaryService.class)
			        .findPatientConsultationsByLocationAndDateInterval(location, stardDate, endDate), context);
			
		}
		catch (final Exception e) {
			
			throw new APIException(e);
		}
	}
	
	@Override
	public PatientConsultationSummary newDelegate() {
		return new PatientConsultationSummary();
	}
	
	@Override
	public PatientConsultationSummary save(final PatientConsultationSummary dispensation) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public PatientConsultationSummary getByUniqueId(final String orderUuid) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	protected void delete(final PatientConsultationSummary summary, final String reason, final RequestContext context) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public void purge(final PatientConsultationSummary summary, final RequestContext context) {
		throw new ResourceDoesNotSupportOperationException();
	}
}
