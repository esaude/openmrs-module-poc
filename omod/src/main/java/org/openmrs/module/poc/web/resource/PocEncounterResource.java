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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.module.poc.clinicalservice.util.ConceptUUIDConstants;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9.EncounterResource1_9;

/**
 * Dados Vitais
 * 
 * @author edrisse
 */
@Resource(name = RestConstants.VERSION_1
		+ "/poc-encounter", supportedClass = Encounter.class, supportedOpenmrsVersions = { "1.9.*", "1.10.*", "1.11.*",
				"1.12.*", "2.0.*", "2.1.*" })
public class PocEncounterResource extends EncounterResource1_9 {

	private static final Collection<String> MARKED_ON_UUIDS = new ArrayList<String>() {

		private static final long serialVersionUID = 1620224949111571964L;
		{
			add(ConceptUUIDConstants.POC_MAPPING_VITALS_DATE);
			add(ConceptUUIDConstants.POC_MAPPING_WHO_DATE);
			add(ConceptUUIDConstants.POC_MAPPING_ANAMNESE_DATE);
			add(ConceptUUIDConstants.POC_MAPPING_DIAGNOSIS_DATE);
		}
	};

	@Override
	public Encounter save(Encounter delegate) {
		setCurrentDateOnMarkedOnObs(delegate);
		return super.save(delegate);
	}

	private void setCurrentDateOnMarkedOnObs(Encounter delegate) {
		Date now = new Date();
		for (Obs obs : delegate.getObs()) {
			Concept concept = obs.getConcept();
			if (MARKED_ON_UUIDS.contains(concept.getUuid())) {
				obs.setValueDatetime(now);
			}
		}
	}
}
