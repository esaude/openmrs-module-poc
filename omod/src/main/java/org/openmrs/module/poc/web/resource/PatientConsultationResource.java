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

import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.poc.patientconsultation.model.PatientConsultation;
import org.openmrs.module.poc.patientconsultation.model.PatientConsultationSummary;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;

/**
 *
 */

@SubResource(parent = PatientConsultationSummaryResource.class, path = "patientConsultation", supportedClass = PatientConsultation.class, supportedOpenmrsVersions = {
        "1.8.*", "1.9.*", "1.10.*", "1.11.*", "1.12.*" })
public class PatientConsultationResource extends
        DelegatingSubResource<PatientConsultation, PatientConsultationSummary, PatientConsultationSummaryResource> {
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(final Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			final DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("encounter");
			description.addProperty("checkInOnConsultationDate");
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			final DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("encounter");
			description.addProperty("checkInOnConsultationDate");
			return description;
		}
		return null;
	}
	
	@Override
	public PatientConsultation newDelegate() {
		return new PatientConsultation();
	}
	
	@Override
	public PatientConsultation save(final PatientConsultation arg0) {
		
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public PageableResult doGetAll(final PatientConsultationSummary parent, final RequestContext context) {
		
		final List<PatientConsultation> items = new ArrayList<>();
		if (parent != null) {
			items.addAll(parent.getPatientConsultations());
		}
		return new NeedsPaging<>(items, context);
	}
	
	@Override
	public PatientConsultationSummary getParent(final PatientConsultation child) {
		
		return child.getParent();
	}
	
	@Override
	public void setParent(final PatientConsultation child, final PatientConsultationSummary parent) {
		
		if (child != null) {
			child.setParent(parent);
		}
	}
	
	@Override
	protected void delete(final PatientConsultation arg0, final String arg1, final RequestContext arg2) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public PatientConsultation getByUniqueId(final String arg0) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public void purge(final PatientConsultation arg0, final RequestContext arg1) {
		throw new ResourceDoesNotSupportOperationException();
	}
}
