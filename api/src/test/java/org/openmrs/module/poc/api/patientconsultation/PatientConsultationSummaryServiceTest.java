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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.poc.api.POCBaseModuleContextSensitiveTest;
import org.openmrs.module.poc.patientconsultation.model.PatientConsultation;
import org.openmrs.module.poc.patientconsultation.model.PatientConsultationSummary;
import org.openmrs.module.poc.patientconsultation.service.PatientConsultationSummaryService;

public class PatientConsultationSummaryServiceTest extends POCBaseModuleContextSensitiveTest {

	@Test
	public void shouldFindPatientConsultationsUpToEndDate() throws Exception {
		this.executeDataSet("patientconsultation/shouldFindPatientConsultationsByLocationAndDateInterval-dataset.xml");

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2017);
		calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
		calendar.set(Calendar.DAY_OF_MONTH, 3);
		final Date endDate = calendar.getTime();

		final List<PatientConsultationSummary> patientSummaries = Context
				.getService(PatientConsultationSummaryService.class)
				.findPatientConsultationsByLocationAndDateInterval(new Location(2), true, endDate);

		Assert.assertEquals(3, patientSummaries.size());
		{
			PatientConsultationSummary summary = patientSummaries.get(0);
			Assert.assertEquals("20/10/2017", dateFormat.format(summary.getConsultationDate()));
			Assert.assertEquals(1, summary.getPatientConsultations().size());
			PatientConsultation consultation = summary.getPatientConsultations().get(0);
			Assert.assertEquals(Integer.valueOf(1005), consultation.getEncounter().getId());
			Assert.assertTrue(consultation.getCheckInOnConsultationDate());
		}
		{
			PatientConsultationSummary summary = patientSummaries.get(1);
			Assert.assertEquals("30/10/2017", dateFormat.format(summary.getConsultationDate()));
			Assert.assertEquals(1, summary.getPatientConsultations().size());
			PatientConsultation consultation = summary.getPatientConsultations().get(0);
			Assert.assertEquals(Integer.valueOf(1006), consultation.getEncounter().getId());
			Assert.assertFalse(consultation.getCheckInOnConsultationDate());
		}
		{
			PatientConsultationSummary summary = patientSummaries.get(2);
			Assert.assertEquals("03/11/2017", dateFormat.format(summary.getConsultationDate()));
			Assert.assertEquals(1, summary.getPatientConsultations().size());
			PatientConsultation consultation = summary.getPatientConsultations().get(0);
			Assert.assertEquals(Integer.valueOf(1007), consultation.getEncounter().getId());
			Assert.assertTrue(consultation.getCheckInOnConsultationDate());
		}
	}

	@Test
	public void shouldFindPatientConsultationsWithinOneMonth() throws Exception {
		this.executeDataSet("patientconsultation/shouldFindPatientConsultationsByLocationAndDateInterval-dataset.xml");

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2017);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		calendar.set(Calendar.DAY_OF_MONTH, 3);
		final Date endDate = calendar.getTime();

		final List<PatientConsultationSummary> patientSummaries = Context
				.getService(PatientConsultationSummaryService.class)
				.findPatientConsultationsByLocationAndDateInterval(new Location(2), true, endDate);

		Assert.assertEquals(2, patientSummaries.size());
		Assert.assertEquals("03/11/2017", dateFormat.format(patientSummaries.get(0).getConsultationDate()));
		Assert.assertEquals("01/12/2017", dateFormat.format(patientSummaries.get(1).getConsultationDate()));
	}

	@Test
	public void shouldNotConsiderPatientConsultationsOusideOneMonth() throws Exception {
		this.executeDataSet("patientconsultation/shouldFindPatientConsultationsByLocationAndDateInterval-dataset.xml");

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2017);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		calendar.set(Calendar.DAY_OF_MONTH, 4);
		final Date endDate = calendar.getTime();

		final List<PatientConsultationSummary> patientSummaries = Context
				.getService(PatientConsultationSummaryService.class)
				.findPatientConsultationsByLocationAndDateInterval(new Location(2), true, endDate);

		Assert.assertEquals(1, patientSummaries.size());
		Assert.assertEquals("01/12/2017", dateFormat.format(patientSummaries.get(0).getConsultationDate()));
	}

	@Test
	public void shouldFindPatientConsultationsWithinOneWeek() throws Exception {
		this.executeDataSet("patientconsultation/shouldFindPatientConsultationsByLocationAndDateInterval-dataset.xml");

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2017);
		calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
		calendar.set(Calendar.DAY_OF_MONTH, 5);
		final Date endDate = calendar.getTime();

		final List<PatientConsultationSummary> patientSummaries = Context
				.getService(PatientConsultationSummaryService.class)
				.findPatientConsultationsByLocationAndDateInterval(new Location(2), false, endDate);

		Assert.assertEquals(2, patientSummaries.size());
		Assert.assertEquals("30/10/2017", dateFormat.format(patientSummaries.get(0).getConsultationDate()));
		Assert.assertEquals("03/11/2017", dateFormat.format(patientSummaries.get(1).getConsultationDate()));
	}

	@Test
	public void shouldNotConsiderPatientConsultationsOutsideOneWeek() throws Exception {
		this.executeDataSet("patientconsultation/shouldFindPatientConsultationsByLocationAndDateInterval-dataset.xml");

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2017);
		calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
		calendar.set(Calendar.DAY_OF_MONTH, 6);
		final Date endDate = calendar.getTime();

		final List<PatientConsultationSummary> patientSummaries = Context
				.getService(PatientConsultationSummaryService.class)
				.findPatientConsultationsByLocationAndDateInterval(new Location(2), false, endDate);

		Assert.assertEquals(1, patientSummaries.size());
		Assert.assertEquals("03/11/2017", dateFormat.format(patientSummaries.get(0).getConsultationDate()));
	}

	@Test
	public void shouldGroupConsultationsByDate() throws Exception {
		this.executeDataSet("patientconsultation/shouldFindPatientConsultationsByLocationAndDateInterval-dataset.xml");

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2017);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		final Date endDate = calendar.getTime();

		final List<PatientConsultationSummary> patientSummaries = Context
				.getService(PatientConsultationSummaryService.class)
				.findPatientConsultationsByLocationAndDateInterval(new Location(2), true, endDate);

		Assert.assertEquals(2, patientSummaries.size());
		{
			PatientConsultationSummary summary = patientSummaries.get(0);
			Assert.assertEquals("03/11/2017", dateFormat.format(summary.getConsultationDate()));
			Assert.assertEquals(1, summary.getPatientConsultations().size());
		}
		Assert.assertEquals(2, patientSummaries.size());
		{
			PatientConsultationSummary summary = patientSummaries.get(1);
			Assert.assertEquals("01/12/2017", dateFormat.format(summary.getConsultationDate()));
			Assert.assertEquals(2, summary.getPatientConsultations().size());
		}
	}
}
