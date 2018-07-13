/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc.web.search;

import java.util.Arrays;

import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.v1_0.search.openmrs1_8.EncounterSearchHandler1_8;
import org.springframework.stereotype.Component;

/**
 * Read https://wiki.openmrs.org/display/docs/Adding+Web+Service+Search+Handler
 * 
 * @author edrisse
 */
@Component
public class PocEncounterSearchHandler extends EncounterSearchHandler1_8 {
	
	private static final String DATE_FROM = "fromdate";
	
	private static final String DATE_TO = "todate";
	
	@Override
	public SearchConfig getSearchConfig() {
		return new SearchConfig(
		        "default",
		        RestConstants.VERSION_1 + "/poc-encounter",
		        Arrays.asList("1.8.*", "1.9.*", "1.10.*", "1.11.*", "1.12.*"),
		        Arrays.asList(new SearchQuery.Builder(
		                "Allows you to find Encounter by patient and encounterType (and optionally by from and to date range)")
		                .withRequiredParameters("patient")
		                .withOptionalParameters("encounterType", DATE_FROM, DATE_TO, "order").build()));
	}
	
}
