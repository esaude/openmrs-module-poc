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
import java.util.Set;

import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.TestOrder;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.poc.pocheuristic.service.PocHeuristicService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.resource.impl.ServiceSearcher;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.PatientResource1_8;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9.EncounterResource1_9;

@Resource(name = RestConstants.VERSION_1
        + "/encounterwrapper", supportedClass = Encounter.class, supportedOpenmrsVersions = { "1.9.*", "1.10.*",
        "1.11.*", "1.12.*", "2.0.*" })
public class EncounterWrapperResource extends EncounterResource1_9 {
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(final Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			final DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("encounterDatetime");
			description.addProperty("patient", Representation.REF);
			description.addProperty("location", Representation.REF);
			description.addProperty("form", Representation.REF);
			description.addProperty("encounterType", Representation.REF);
			description.addProperty("provider", Representation.REF);
			description.addProperty("obs", Representation.REF);
			description.addProperty("orders", Representation.REF);
			description.addProperty("voided");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			final DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("encounterDatetime");
			description.addProperty("patient", Representation.REF);
			description.addProperty("location");
			description.addProperty("form");
			description.addProperty("encounterType");
			description.addProperty("provider");
			description.addProperty("obs");
			description.addProperty("orders");
			description.addProperty("voided");
			description.addProperty("auditInfo");
			description.addSelfLink();
			return description;
		}
		return null;
	}
	
	@PropertySetter("orders")
	public static void setOrders(final Encounter instance, final Set<Order> orders) {
		
		System.out.println("Executando o wrapper....");
		for (final Order o : orders) {
			
			if (OrderType.TEST_ORDER_TYPE_UUID.equals(o.getOrderType().getUuid())) {
				
				final TestOrder order = (TestOrder) Context.getOrderService().getOrder(o.getId());
				instance.addOrder(order);
				
			} else if (OrderType.DRUG_ORDER_TYPE_UUID.equals(o.getOrderType().getUuid())) {
				final DrugOrder order = (DrugOrder) Context.getOrderService().getOrder(o.getId());
				instance.addOrder(order);
			}
		}
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		final DelegatingResourceDescription description = new DelegatingResourceDescription();
		
		description.addProperty("encounterDatetime"); // has a default value
		// set, hence not
		// required here
		description.addRequiredProperty("patient");
		description.addRequiredProperty("encounterType");
		
		description.addProperty("location");
		description.addProperty("form");
		description.addProperty("provider");
		description.addProperty("orders");
		description.addProperty("obs");
		
		return description;
	}
	
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
		if (patientUuid != null) {
			if (patient == null) {
				return new EmptySearchResult();
			}
			final List<Encounter> encs = Context.getEncounterService().getEncountersByPatient(patient);
			return new NeedsPaging<>(encs, context);
		}
		
		return new ServiceSearcher<Encounter>(EncounterService.class, "getEncounters", "getCountOfEncounters")
		        .search(context.getParameter("q"), context);
	}
}
