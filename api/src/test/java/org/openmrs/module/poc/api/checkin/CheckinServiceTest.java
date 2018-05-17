/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc.api.checkin;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.LocationDAO;
import org.openmrs.api.db.PatientDAO;
import org.openmrs.module.poc.api.POCBaseModuleContextSensitiveTest;
import org.openmrs.module.poc.api.common.model.helper.PocModule;
import org.openmrs.module.poc.api.common.util.OPENMRSUUIDs;
import org.openmrs.module.poc.checkin.model.helper.Checkin;
import org.openmrs.module.poc.checkin.service.CheckinService;
import org.openmrs.module.poc.pocheuristic.service.PocHeuristicService;
import org.springframework.beans.factory.annotation.Autowired;

public class CheckinServiceTest extends POCBaseModuleContextSensitiveTest {
	
	@Autowired
	private PatientDAO patientDAO;
	
	@Autowired
	private LocationDAO locationDAO;
	
	@Autowired
	private PocHeuristicService pocHeuristicService;
	
	@Test
	public void shouldCheckin() {
		Patient patient = patientDAO.getPatient(2);
		Assert.assertTrue(pocHeuristicService.findVisits(patient, null, new Date(), false).isEmpty());
		
		Checkin checkin = new Checkin();
		checkin.setPatient(patient);
		checkin.setLocation(locationDAO.getLocation(1));
		checkin.setModule(PocModule.PHARMACY);
		CheckinService checkinService = Context.getService(CheckinService.class);
		checkinService.checkin(checkin);
		
		Assert.assertFalse(pocHeuristicService.findVisits(patient, null, new Date(), false).isEmpty());
	}
	
	@Test
	public void shouldSelectPharmacyPickUpVisitType() {
		Patient patient = patientDAO.getPatient(2);
		Checkin checkin = new Checkin();
		checkin.setPatient(patient);
		checkin.setLocation(locationDAO.getLocation(1));
		checkin.setModule(PocModule.PHARMACY);
		CheckinService checkinService = Context.getService(CheckinService.class);
		checkinService.checkin(checkin);
		Visit visit = pocHeuristicService.findVisits(patient, null, new Date(), false).get(0);
		Assert.assertEquals(OPENMRSUUIDs.PHARMACY_PICKUP_VISIT_TYPE, visit.getVisitType().getUuid());
	}
	
	@Test
	public void shouldSelectFollowupConsultationVisitType() {
		Patient patient = patientDAO.getPatient(2);
		Checkin checkin = new Checkin();
		checkin.setPatient(patient);
		checkin.setLocation(locationDAO.getLocation(1));
		checkin.setModule(PocModule.REGISTRATION);
		CheckinService checkinService = Context.getService(CheckinService.class);
		checkinService.checkin(checkin);
		Visit visit = pocHeuristicService.findVisits(patient, null, new Date(), false).get(0);
		Assert.assertEquals(OPENMRSUUIDs.FOLLOW_UP_CONSULTATION_VISIT_TYPE, visit.getVisitType().getUuid());
	}
	
	@Test
	public void shouldSelectFirstConsultationVisitType() {
		Patient patient = patientDAO.getPatient(1000);
		Checkin checkin = new Checkin();
		checkin.setPatient(patient);
		checkin.setLocation(locationDAO.getLocation(1));
		checkin.setModule(PocModule.REGISTRATION);
		CheckinService checkinService = Context.getService(CheckinService.class);
		checkinService.checkin(checkin);
		Visit visit = pocHeuristicService.findVisits(patient, null, new Date(), false).get(0);
		Assert.assertEquals(OPENMRSUUIDs.FIRST_CONSULTATION_VISIT_TYPE, visit.getVisitType().getUuid());
	}
	
	@Test(expected = IllegalStateException.class)
	public void shouldNotCheckinTwiceOnSameDay() {
		Patient patient = patientDAO.getPatient(1000);
		Checkin checkin = new Checkin();
		checkin.setPatient(patient);
		checkin.setLocation(locationDAO.getLocation(1));
		checkin.setModule(PocModule.REGISTRATION);
		CheckinService checkinService = Context.getService(CheckinService.class);
		checkinService.checkin(checkin);
		checkinService.checkin(checkin);
	}
	
	@Before
	public void executeTestDataSet() throws Exception {
		this.executeDataSet("checkinservice/CheckinServiceTest-dataset.xml");
	}
	
}
