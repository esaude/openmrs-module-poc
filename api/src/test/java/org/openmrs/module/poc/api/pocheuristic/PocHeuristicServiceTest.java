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
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
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
		
		final Patient patient = Context.getPatientService().getPatient(7);
		
		final EncounterType encounterType = Context.getService(PocHeuristicService.class)
		        .findSeguimentoPacienteEncounterTypeByPatientAge(patient);
		
		final List<Encounter> encounters = Context.getService(PocHeuristicService.class)
		        .findEncountersWithTestOrdersByPatient(patient, encounterType);
		
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
	public void shouldFindObsByOrderAndConceptAndEncounter() throws Exception {
		this.executeDataSet("pocheuristic/shouldFindObsByOrderAndConceptAndEncounter-dataset.xml");
		
		final Obs obs = Context.getService(PocHeuristicService.class)
		        .findObsByOrderAndConceptAndEncounter(new Order(1000), new Concept(60000), new Encounter(1000));
		
		Assert.assertNotNull(obs);
		Assert.assertEquals(Integer.valueOf(1000), obs.getEncounter().getEncounterId());
		Assert.assertEquals(Integer.valueOf(1000), obs.getOrder().getOrderId());
		Assert.assertEquals(Integer.valueOf(60000), obs.getConcept().getConceptId());
	}
	
	@Test
	public void shouldFindObsByEncounterAndConcept() throws Exception {
		this.executeDataSet("pocheuristic/shouldFindObsByEncounterAndConcept-dataset.xml");
		
		final Obs obs = Context.getService(PocHeuristicService.class).findObsByEncounterAndConcept(new Encounter(1000),
		    new Concept(60000));
		
		Assert.assertNotNull(obs);
		Assert.assertEquals(Integer.valueOf(1000), obs.getEncounter().getEncounterId());
		Assert.assertNull(obs.getOrder());
		Assert.assertEquals(Integer.valueOf(60000), obs.getConcept().getConceptId());
	}
	
	@Test
	public void shouldFindObsByGroup() throws Exception {
		this.executeDataSet("pocheuristic/shouldFindObsByGroup-dataset.xml");
		
		final List<Obs> observations = Context.getService(PocHeuristicService.class).findObsByGroup(new Obs(1000));
		
		Assert.assertNotNull(observations);
		Assert.assertEquals(3, observations.size());
	}
}
