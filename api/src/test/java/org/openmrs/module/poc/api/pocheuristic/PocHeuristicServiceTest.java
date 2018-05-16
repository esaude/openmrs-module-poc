/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc.api.pocheuristic;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.poc.api.POCBaseModuleContextSensitiveTest;
import org.openmrs.module.poc.api.common.util.OPENMRSUUIDs;
import org.openmrs.module.poc.pocheuristic.service.PocHeuristicService;

public class PocHeuristicServiceTest extends POCBaseModuleContextSensitiveTest {
	
	@Test
	public void shouldFindSeguimentoPacienteEncounterTypeByPatientAgeForChildPatient() throws Exception {
		
		final Patient childPatient = new Patient();
		childPatient.setBirthdateFromAge(14, new Date());
		
		final EncounterType encounterType = Context.getService(PocHeuristicService.class)
		        .findSeguimentoPacienteEncounterTypeByPatientAge(childPatient);
		
		Assert.assertNotNull(encounterType);
		Assert.assertEquals(OPENMRSUUIDs.ARV_FOLLOW_UP_CHILD_ENCOUNTER_TYPE_UUID, encounterType.getUuid());
	}
	
	@Test
	public void shouldFindSeguimentoPacienteEncounterTypeByPatientAgeForAdultPatient() throws Exception {
		
		final Patient adultPatient = new Patient();
		adultPatient.setBirthdateFromAge(15, new Date());
		
		final EncounterType encounterType = Context.getService(PocHeuristicService.class)
		        .findSeguimentoPacienteEncounterTypeByPatientAge(adultPatient);
		
		Assert.assertNotNull(encounterType);
		Assert.assertEquals(OPENMRSUUIDs.ARV_FOLLOW_UP_ADULT_ENCOUNTER_TYPE_UUID, encounterType.getUuid());
	}
	
	@Test(expected = APIException.class)
	public void shouldThrowExceptionFindSeguimentoPacienteEncounterTypeByPatientAge() throws Exception {
		
		Context.getService(PocHeuristicService.class).findSeguimentoPacienteEncounterTypeByPatientAge(null);
	}
	
	@Test
	public void shouldFindLastEncounterByPatientAndEncounterTypeAndLocationAndDateAndStatus() throws Exception {
		this.executeDataSet(
		        "pocheuristic/shouldFindLastEncounterByPatientAndEncounterTypeAndLocationAndDateAndStatus-dataset.xml");
		
		final Patient patient = new Patient(7);
		final EncounterType encounterType = new EncounterType(3);
		final Location location = new Location(1);
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2018);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		final Date encounterDate = calendar.getTime();
		
		final Encounter encounter = Context.getService(PocHeuristicService.class)
		        .findLastEncounterByPatientAndEncounterTypeAndLocationAndDateAndStatus(patient, encounterType, location,
		            encounterDate);
		
		Assert.assertNotNull(encounter);
		Assert.assertEquals(Integer.valueOf(1000), encounter.getEncounterId());
	}
	
	@Test(expected = APIException.class)
	public void shouldThrowExceptionByFindLastEncounterByPatientAndEncounterTypeAndLocationAndDateAndStatus()
	        throws Exception {
		
		Context.getService(PocHeuristicService.class)
		        .findLastEncounterByPatientAndEncounterTypeAndLocationAndDateAndStatus(new Patient(7),
		            new EncounterType(7), new Location(1), new Date());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldFindEncountersWithTestOrdersByPatient() throws Exception {
		this.executeDataSet("pocheuristic/shouldFindEncountersWithTestOrdersByPatient-dataset.xml");
		
		final String patientUuid = "5946f880-b197-400b-9caa-a3c661d23041";
		
		final List<Encounter> encounters = Context.getService(PocHeuristicService.class)
		        .findEncountersWithTestOrdersByPatient(patientUuid);
		
		MatcherAssert.assertThat(encounters, CoreMatchers.notNullValue());
		MatcherAssert.assertThat(encounters, IsCollectionWithSize.hasSize(1));
		
		MatcherAssert.assertThat(encounters,
		    CoreMatchers.hasItems(Matchers.<Encounter> hasProperty("orders", IsCollectionWithSize.hasSize(2))));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldFindEncountersByPatientAndEncounterType() throws Exception {
		this.executeDataSet("pocheuristic/shouldFindEncountersByPatientAndEncounterType-dataset.xml");
		
		final Patient patient = new Patient(999);
		final EncounterType encounterType = new EncounterType(3);
		
		final List<Encounter> encounters = Context.getService(PocHeuristicService.class)
		        .findEncountersByPatientAndEncounterType(patient, encounterType);
		
		MatcherAssert.assertThat(encounters, CoreMatchers.notNullValue());
		MatcherAssert.assertThat(encounters, IsCollectionWithSize.hasSize(1));
		
		MatcherAssert.assertThat(encounters,
		    CoreMatchers.hasItems(Matchers.<Encounter> hasProperty("orders", IsCollectionWithSize.hasSize(2))));
	}
	
	@Test
	public void shouldFindVisitsOfThePatient() throws Exception {
		this.executeDataSet("pocheuristic/visits-dataset.xml");
		
		Patient patient = Context.getPatientService().getPatient(1);
		PocHeuristicService heuristicService = Context.getService(PocHeuristicService.class);
		List<Visit> visits = heuristicService.findVisits(patient, null, null, null);
		Assert.assertEquals(2, visits.size());
		Assert.assertEquals(Integer.valueOf(3), visits.get(0).getId());
		Assert.assertEquals(Integer.valueOf(1), visits.get(1).getId());
	}
	
	@Test
	public void shouldFindTheMostRecentOnly() throws Exception {
		this.executeDataSet("pocheuristic/visits-dataset.xml");
		
		Patient patient = Context.getPatientService().getPatient(1);
		PocHeuristicService heuristicService = Context.getService(PocHeuristicService.class);
		List<Visit> visits = heuristicService.findVisits(patient, true, null, null);
		Assert.assertEquals(1, visits.size());
		Assert.assertEquals(Integer.valueOf(3), visits.get(0).getId());
	}
	
	@Test
	public void shouldFindNonVoidedVisits() throws Exception {
		executeDataSet("pocheuristic/visits-dataset.xml");
		
		PocHeuristicService heuristicService = Context.getService(PocHeuristicService.class);
		List<Visit> visits = heuristicService.findVisits(null, null, null, false);
		Assert.assertEquals(4, visits.size());
	}
	
	@Test
	public void shouldFindOnlyFromSpecifiedDate() throws Exception {
		executeDataSet("pocheuristic/visits-dataset.xml");
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(2018, Calendar.MAY, 8);
		PocHeuristicService heuristicService = Context.getService(PocHeuristicService.class);
		List<Visit> visits = heuristicService.findVisits(null, null, calendar.getTime(), null);
		Assert.assertEquals(2, visits.size());
		Assert.assertEquals(Integer.valueOf(2), visits.get(0).getId());
		Assert.assertEquals(Integer.valueOf(1), visits.get(1).getId());
	}
}
