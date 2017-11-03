/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc.api.patientconsultation;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.poc.api.POCBaseModuleContextSensitiveTest;
import org.openmrs.module.poc.patientconsultation.model.PatientConsultationSummary;
import org.openmrs.module.poc.patientconsultation.service.PatientConsultationSummaryService;

public class PatientConsultationSummaryServiceTest extends POCBaseModuleContextSensitiveTest {
	
	@Test
	public void shouldFindPatientConsultationsByLocationAndDateInterval() throws Exception {
		this.executeDataSet("patientconsultation/shouldFindPatientConsultationsByLocationAndDateInterval-dataset.xml");
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2017);
		calendar.set(Calendar.MONTH, 9);
		calendar.set(Calendar.DAY_OF_MONTH, 20);
		final Date startDate = calendar.getTime();
		
		calendar.set(Calendar.YEAR, 2017);
		calendar.set(Calendar.MONTH, 10);
		calendar.set(Calendar.DAY_OF_MONTH, 3);
		final Date endDate = calendar.getTime();
		
		final List<PatientConsultationSummary> patientSummaries = Context
		        .getService(PatientConsultationSummaryService.class)
		        .findPatientConsultationsByLocationAndDateInterval(new Location(2), startDate, endDate);
		
		MatcherAssert.assertThat(patientSummaries, CoreMatchers.notNullValue());
		MatcherAssert.assertThat(patientSummaries, IsCollectionWithSize.hasSize(3));
		MatcherAssert.assertThat(patientSummaries, CoreMatchers.hasItems(Matchers
		        .<PatientConsultationSummary> hasProperty("patientConsultations", IsCollectionWithSize.hasSize(1))));
		
	}
}
