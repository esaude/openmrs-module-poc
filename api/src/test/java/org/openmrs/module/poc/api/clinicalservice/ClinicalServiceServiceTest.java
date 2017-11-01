/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc.api.clinicalservice;

import java.util.List;
import java.util.Set;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.poc.api.BasePOCModuleContextSensitiveTest;
import org.openmrs.module.poc.api.common.exception.POCBusinessException;
import org.openmrs.module.poc.clinicalservice.service.ClinicalServiceService;
import org.openmrs.module.poc.clinicalservice.util.ClinicalServiceKeys;
import org.openmrs.module.poc.clinicalservice.util.ClinicalServicesConceptUUIDConstants;

public class ClinicalServiceServiceTest extends BasePOCModuleContextSensitiveTest {
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldDeletePediatricVitalsClinicalServices() throws Exception {
		this.executeDataSet("clinicalservice/shouldDeletePediatricVitalsClinicalServices-dataset.xml");
		
		final String encounterUuid = "eec646cb-c847-4ss-enc-vital-child";
		
		final List<Obs> notDeletedObs = Context.getObsService().getObservations("1000");
		
		MatcherAssert.assertThat(notDeletedObs, CoreMatchers.notNullValue());
		MatcherAssert.assertThat(notDeletedObs, IsCollectionWithSize.hasSize(5));
		MatcherAssert.assertThat(notDeletedObs,
		    CoreMatchers.hasItems(Matchers.<Obs> hasProperty("voided", CoreMatchers.is(false))));
		
		Context.getService(ClinicalServiceService.class).deleteClinicalService(encounterUuid,
		    ClinicalServiceKeys.VITALS_PEDIATRICS.getCode());
		final Encounter encounuter = Context.getEncounterService().getEncounterByUuid(encounterUuid);
		
		final Set<Obs> deletedObs = encounuter.getAllObs(true);
		MatcherAssert.assertThat(deletedObs, CoreMatchers.notNullValue());
		MatcherAssert.assertThat(deletedObs, IsCollectionWithSize.hasSize(5));
		MatcherAssert.assertThat(deletedObs,
		    CoreMatchers.hasItems(Matchers.<Obs> hasProperty("voided", CoreMatchers.is(true))));
		MatcherAssert.assertThat(encounuter, Matchers.hasProperty("voided", CoreMatchers.is(true)));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldDeleteAdultVitalsClinicalServices() throws Exception {
		this.executeDataSet("clinicalservice/shouldDeleteAdultVitalsClinicalServices-dataset.xml");
		
		final String encounterUuid = "eec646cb-c847-4ss-enc-vital-adult";
		
		final List<Obs> notDeletedObs = Context.getObsService().getObservations("1001");
		
		MatcherAssert.assertThat(notDeletedObs, CoreMatchers.notNullValue());
		MatcherAssert.assertThat(notDeletedObs, IsCollectionWithSize.hasSize(7));
		MatcherAssert.assertThat(notDeletedObs,
		    CoreMatchers.hasItems(Matchers.<Obs> hasProperty("voided", CoreMatchers.is(false))));
		
		Context.getService(ClinicalServiceService.class).deleteClinicalService(encounterUuid,
		    ClinicalServiceKeys.VITALS_ADULT.getCode());
		final Encounter encounuter = Context.getEncounterService().getEncounterByUuid(encounterUuid);
		
		final Set<Obs> deletedObs = encounuter.getAllObs(true);
		MatcherAssert.assertThat(deletedObs, CoreMatchers.notNullValue());
		MatcherAssert.assertThat(deletedObs, IsCollectionWithSize.hasSize(7));
		MatcherAssert.assertThat(deletedObs,
		    CoreMatchers.hasItems(Matchers.<Obs> hasProperty("voided", CoreMatchers.is(true))));
		MatcherAssert.assertThat(encounuter, Matchers.hasProperty("voided", CoreMatchers.is(true)));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldDeleteAdultSocialInfotClinicalServices() throws Exception {
		this.executeDataSet("clinicalservice/shouldDeleteAdultSocialInfotClinicalServices-dataset.xml");
		
		final String encounterUuid = "eec646cb-c8-enc-social-inf-adult";
		
		final List<Obs> notDeletedObs = Context.getObsService().getObservations("1002");
		
		MatcherAssert.assertThat(notDeletedObs, CoreMatchers.notNullValue());
		MatcherAssert.assertThat(notDeletedObs, IsCollectionWithSize.hasSize(12));
		MatcherAssert.assertThat(notDeletedObs,
		    CoreMatchers.hasItems(Matchers.<Obs> hasProperty("voided", CoreMatchers.is(false))));
		
		Context.getService(ClinicalServiceService.class).deleteClinicalService(encounterUuid,
		    ClinicalServiceKeys.SOCIAL_INFO_ADULT.getCode());
		final Encounter encounuter = Context.getEncounterService().getEncounterByUuid(encounterUuid);
		
		final Set<Obs> deletedObs = encounuter.getAllObs(true);
		
		MatcherAssert.assertThat(deletedObs, CoreMatchers.notNullValue());
		MatcherAssert.assertThat(deletedObs, IsCollectionWithSize.hasSize(12));
		MatcherAssert.assertThat(deletedObs,
		    CoreMatchers.hasItems(Matchers.<Obs> hasProperty("voided", CoreMatchers.is(true))));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldDeletePediatricSocialInfotClinicalServices() throws Exception {
		this.executeDataSet("clinicalservice/shouldDeletePediatricSocialInfotClinicalServices-dataset.xml");
		
		final String enconterUuid = "eec646cb-c8-enc-social-inf-pediatric";
		
		final List<Obs> notDeletedObs = Context.getObsService().getObservations("1002");
		
		MatcherAssert.assertThat(notDeletedObs, CoreMatchers.notNullValue());
		MatcherAssert.assertThat(notDeletedObs, IsCollectionWithSize.hasSize(17));
		MatcherAssert.assertThat(notDeletedObs,
		    CoreMatchers.hasItems(Matchers.<Obs> hasProperty("voided", CoreMatchers.is(false))));
		
		Context.getService(ClinicalServiceService.class).deleteClinicalService(enconterUuid,
		    ClinicalServiceKeys.SOCIAL_INFO_PEDIATRICS.getCode());
		final Encounter encounuter = Context.getEncounterService().getEncounterByUuid(enconterUuid);
		
		final Set<Obs> deletedObs = encounuter.getAllObs(true);
		
		MatcherAssert.assertThat(deletedObs, CoreMatchers.notNullValue());
		MatcherAssert.assertThat(deletedObs, IsCollectionWithSize.hasSize(17));
		MatcherAssert.assertThat(deletedObs,
		    CoreMatchers.hasItems(Matchers.<Obs> hasProperty("voided", CoreMatchers.is(true))));
	}
	
	@Test(expected = POCBusinessException.class)
	public void shouldNotFindClinicalServiceForGivenWrongCode() throws Exception {
		
		final String vitalsEncounterUuid = "6519d653-393b-4118-9c83-a3715b82d4ac";
		final String invalidCode = "invalid";
		
		Context.getService(ClinicalServiceService.class).deleteClinicalService(vitalsEncounterUuid, invalidCode);
	}
	
	@Test(expected = POCBusinessException.class)
	public void shouldNotFindClinicalServiceForGivenEncounterWithoutUuid() throws Exception {
		
		final String invalidEncounterUuid = null;
		final String validCode = ClinicalServiceKeys.VITALS_ADULT.getCode();
		
		Context.getService(ClinicalServiceService.class).deleteClinicalService(invalidEncounterUuid, validCode);
	}
	
	@Test(expected = POCBusinessException.class)
	public void shouldNotFindClinicalServiceForGivenEncounterWithInvalidUuid() throws Exception {
		
		final String invalidEncounterUuid = "invalid";
		final String validCode = ClinicalServiceKeys.VITALS_ADULT.getCode();
		
		Context.getService(ClinicalServiceService.class).deleteClinicalService(invalidEncounterUuid, validCode);
	}
	
	@Test(expected = POCBusinessException.class)
	public void shouldNotFindClinicalServiceForVoidedEncounter() throws Exception {
		this.executeDataSet("clinicalservice/shouldNotFindClinicalServiceForVoidedEncounter-dataset.xml");
		
		final String voidedEncounterUuid = "eec646cb-c847-4ss-enc-that-was-voided";
		final String validCode = ClinicalServiceKeys.VITALS_ADULT.getCode();
		
		Context.getService(ClinicalServiceService.class).deleteClinicalService(voidedEncounterUuid, validCode);
	}
	
	@Test(expected = POCBusinessException.class)
	public void shouldNotFindClinicalServiceForEncounterWithoutObservations() throws Exception {
		this.executeDataSet("clinicalservice/shouldNotFindClinicalServiceForEncounterWithoutObservations-dataset.xml");
		
		final String invalidEncounterUuid = "eec646cb-c847-4ss-active-enc-xxx-pptt";
		final String validCode = ClinicalServiceKeys.VITALS_ADULT.getCode();
		
		Context.getService(ClinicalServiceService.class).deleteClinicalService(invalidEncounterUuid, validCode);
	}
	
	@Test(expected = POCBusinessException.class)
	public void shouldNotDeleteClinicalServicesForMissingClinicalServicesConcept() throws Exception {
		
		// This must be mocked to not lost focus testing one and unique
		// behaviour
		final Concept temperature = Context.getConceptService()
		        .getConceptByUuid(ClinicalServicesConceptUUIDConstants.TEMPERATURE);
		Context.getConceptService().retireConcept(temperature, "reason");
		
		this.executeDataSet("clinicalservice/shouldDeletePediatricVitalsClinicalServices-dataset.xml");
		
		final String encounterUuid = "eec646cb-c847-4ss-enc-vital-child";
		
		Context.getService(ClinicalServiceService.class).deleteClinicalService(encounterUuid,
		    ClinicalServiceKeys.VITALS_PEDIATRICS.getCode());
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldDeleteWHOStageAdult() throws Exception {
		this.executeDataSet("clinicalservice/shouldDeleteWHOStageAdult-dataset.xml");
		
		final String whoAdultEncounter = "eec646cb-c847-4ss-enc-who-adult";
		
		final List<Obs> notDeletedObs = Context.getObsService().getObservations("1005");
		
		MatcherAssert.assertThat(notDeletedObs, CoreMatchers.notNullValue());
		MatcherAssert.assertThat(notDeletedObs, IsCollectionWithSize.hasSize(6));
		MatcherAssert.assertThat(notDeletedObs,
		    CoreMatchers.hasItems(Matchers.<Obs> hasProperty("voided", CoreMatchers.is(false))));
		
		Context.getService(ClinicalServiceService.class).deleteClinicalService(whoAdultEncounter,
		    ClinicalServiceKeys.WHO_STAGE_ADULT.getCode());
		final Encounter encounuter = Context.getEncounterService().getEncounterByUuid(whoAdultEncounter);
		
		final Set<Obs> deletedObs = encounuter.getAllObs(true);
		
		MatcherAssert.assertThat(deletedObs, CoreMatchers.notNullValue());
		MatcherAssert.assertThat(deletedObs, IsCollectionWithSize.hasSize(6));
		MatcherAssert.assertThat(deletedObs,
		    CoreMatchers.hasItems(Matchers.<Obs> hasProperty("voided", CoreMatchers.is(true))));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldDeleteWHOStagePediatrics() throws Exception {
		this.executeDataSet("clinicalservice/shouldDeleteWHOStagePediatrics-dataset.xml");
		
		final String whoAdultEncounter = "eec646cb-c847-4ss-enc-who-pediatrics";
		
		final List<Obs> notDeletedObs = Context.getObsService().getObservations("1005");
		
		MatcherAssert.assertThat(notDeletedObs, CoreMatchers.notNullValue());
		MatcherAssert.assertThat(notDeletedObs, IsCollectionWithSize.hasSize(6));
		MatcherAssert.assertThat(notDeletedObs,
		    CoreMatchers.hasItems(Matchers.<Obs> hasProperty("voided", CoreMatchers.is(false))));
		
		Context.getService(ClinicalServiceService.class).deleteClinicalService(whoAdultEncounter,
		    ClinicalServiceKeys.WHO_STAGE_PEDIATRICS.getCode());
		final Encounter encounuter = Context.getEncounterService().getEncounterByUuid(whoAdultEncounter);
		
		final Set<Obs> deletedObs = encounuter.getAllObs(true);
		
		MatcherAssert.assertThat(deletedObs, CoreMatchers.notNullValue());
		MatcherAssert.assertThat(deletedObs, IsCollectionWithSize.hasSize(6));
		MatcherAssert.assertThat(deletedObs,
		    CoreMatchers.hasItems(Matchers.<Obs> hasProperty("voided", CoreMatchers.is(true))));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldDeleteRelevantAspects() throws Exception {
		this.executeDataSet("clinicalservice/shouldDeleteRelevantAspects-dataset.xml");
		
		final String whoAdultEncounter = "eec646cb-c847-4ss-enc-relevant-aspects";
		
		final List<Obs> notDeletedObs = Context.getObsService().getObservations("1005");
		
		MatcherAssert.assertThat(notDeletedObs, CoreMatchers.notNullValue());
		MatcherAssert.assertThat(notDeletedObs, IsCollectionWithSize.hasSize(2));
		MatcherAssert.assertThat(notDeletedObs,
		    CoreMatchers.hasItems(Matchers.<Obs> hasProperty("voided", CoreMatchers.is(false))));
		
		Context.getService(ClinicalServiceService.class).deleteClinicalService(whoAdultEncounter,
		    ClinicalServiceKeys.RELEVANT_ASPECTS.getCode());
		final Encounter encounuter = Context.getEncounterService().getEncounterByUuid(whoAdultEncounter);
		
		final Set<Obs> deletedObs = encounuter.getAllObs(true);
		
		MatcherAssert.assertThat(deletedObs, CoreMatchers.notNullValue());
		MatcherAssert.assertThat(deletedObs, IsCollectionWithSize.hasSize(2));
		MatcherAssert.assertThat(deletedObs,
		    CoreMatchers.hasItems(Matchers.<Obs> hasProperty("voided", CoreMatchers.is(true))));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldDeleteAdultAnamneseExams() throws Exception {
		this.executeDataSet("clinicalservice/shouldDeleteAdultAnamneseExams-dataset.xml");
		
		final String whoAdultEncounter = "eec-c847-4ss-enc-adult-amnese-exams";
		
		final List<Obs> notDeletedObs = Context.getObsService().getObservations("1005");
		
		MatcherAssert.assertThat(notDeletedObs, CoreMatchers.notNullValue());
		MatcherAssert.assertThat(notDeletedObs, IsCollectionWithSize.hasSize(9));
		MatcherAssert.assertThat(notDeletedObs,
		    CoreMatchers.hasItems(Matchers.<Obs> hasProperty("voided", CoreMatchers.is(false))));
		
		Context.getService(ClinicalServiceService.class).deleteClinicalService(whoAdultEncounter,
		    ClinicalServiceKeys.ADULT_ANAMNESE_EXAMS.getCode());
		final Encounter encounuter = Context.getEncounterService().getEncounterByUuid(whoAdultEncounter);
		
		final Set<Obs> deletedObs = encounuter.getAllObs(true);
		
		MatcherAssert.assertThat(deletedObs, CoreMatchers.notNullValue());
		MatcherAssert.assertThat(deletedObs, IsCollectionWithSize.hasSize(9));
		MatcherAssert.assertThat(deletedObs,
		    CoreMatchers.hasItems(Matchers.<Obs> hasProperty("voided", CoreMatchers.is(true))));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldDeletePediatricsAnamneseExams() throws Exception {
		this.executeDataSet("clinicalservice/shouldDeletePediatricsAnamneseExams-dataset.xml");
		
		final String whoAdultEncounter = "c847-4ss-enc-pediatrics-amnese-exams";
		
		final List<Obs> notDeletedObs = Context.getObsService().getObservations("1005");
		
		MatcherAssert.assertThat(notDeletedObs, CoreMatchers.notNullValue());
		MatcherAssert.assertThat(notDeletedObs, IsCollectionWithSize.hasSize(8));
		MatcherAssert.assertThat(notDeletedObs,
		    CoreMatchers.hasItems(Matchers.<Obs> hasProperty("voided", CoreMatchers.is(false))));
		
		Context.getService(ClinicalServiceService.class).deleteClinicalService(whoAdultEncounter,
		    ClinicalServiceKeys.PEDIATRICS_ANAMNESE_EXAMS.getCode());
		final Encounter encounuter = Context.getEncounterService().getEncounterByUuid(whoAdultEncounter);
		
		final Set<Obs> deletedObs = encounuter.getAllObs(true);
		
		MatcherAssert.assertThat(deletedObs, CoreMatchers.notNullValue());
		MatcherAssert.assertThat(deletedObs, IsCollectionWithSize.hasSize(8));
		MatcherAssert.assertThat(deletedObs,
		    CoreMatchers.hasItems(Matchers.<Obs> hasProperty("voided", CoreMatchers.is(true))));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldDeleteAdultDiagnosis() throws Exception {
		this.executeDataSet("clinicalservice/shouldDeleteAdultDiagnosis-dataset.xml");
		
		final String whoAdultEncounter = "c847-4ss-enc-xpto-adult-diagnosis";
		
		final List<Obs> notDeletedObs = Context.getObsService().getObservations("1005");
		
		MatcherAssert.assertThat(notDeletedObs, CoreMatchers.notNullValue());
		MatcherAssert.assertThat(notDeletedObs, IsCollectionWithSize.hasSize(6));
		MatcherAssert.assertThat(notDeletedObs,
		    CoreMatchers.hasItems(Matchers.<Obs> hasProperty("voided", CoreMatchers.is(false))));
		
		Context.getService(ClinicalServiceService.class).deleteClinicalService(whoAdultEncounter,
		    ClinicalServiceKeys.ADULT_DIAGNOSIS.getCode());
		final Encounter encounuter = Context.getEncounterService().getEncounterByUuid(whoAdultEncounter);
		
		final Set<Obs> deletedObs = encounuter.getAllObs(true);
		
		MatcherAssert.assertThat(deletedObs, CoreMatchers.notNullValue());
		MatcherAssert.assertThat(deletedObs, IsCollectionWithSize.hasSize(6));
		MatcherAssert.assertThat(deletedObs,
		    CoreMatchers.hasItems(Matchers.<Obs> hasProperty("voided", CoreMatchers.is(true))));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldDeletePediatricDiagnosis() throws Exception {
		this.executeDataSet("clinicalservice/shouldDeletePediatricDiagnosis-dataset.xml");
		
		final String whoAdultEncounter = "c847-4ss-enc-xpto-pediatric-diagnosis";
		
		final List<Obs> notDeletedObs = Context.getObsService().getObservations("1005");
		
		MatcherAssert.assertThat(notDeletedObs, CoreMatchers.notNullValue());
		MatcherAssert.assertThat(notDeletedObs, IsCollectionWithSize.hasSize(6));
		MatcherAssert.assertThat(notDeletedObs,
		    CoreMatchers.hasItems(Matchers.<Obs> hasProperty("voided", CoreMatchers.is(false))));
		
		Context.getService(ClinicalServiceService.class).deleteClinicalService(whoAdultEncounter,
		    ClinicalServiceKeys.PEDIATRICS_DIAGNOSIS.getCode());
		final Encounter encounuter = Context.getEncounterService().getEncounterByUuid(whoAdultEncounter);
		
		final Set<Obs> deletedObs = encounuter.getAllObs(true);
		
		MatcherAssert.assertThat(deletedObs, CoreMatchers.notNullValue());
		MatcherAssert.assertThat(deletedObs, IsCollectionWithSize.hasSize(6));
		MatcherAssert.assertThat(deletedObs,
		    CoreMatchers.hasItems(Matchers.<Obs> hasProperty("voided", CoreMatchers.is(true))));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldDeleteAllObsIncludingObsGroupAndEnconterOfPediatricDiagnosis() throws Exception {
		this.executeDataSet(
		        "clinicalservice/shouldDeleteAllObsIncludingObsGroupAndEnconterOfPediatricDiagnosis-dataset.xml");
		
		final String whoAdultEncounter = "c847-4ss-enc-xpto-pediatric-diagnosis";
		
		final List<Obs> notDeletedObs = Context.getObsService().getObservations("1005");
		
		MatcherAssert.assertThat(notDeletedObs, CoreMatchers.notNullValue());
		MatcherAssert.assertThat(notDeletedObs, IsCollectionWithSize.hasSize(9));
		MatcherAssert.assertThat(notDeletedObs,
		    CoreMatchers.hasItems(Matchers.<Obs> hasProperty("voided", CoreMatchers.is(false))));
		
		Context.getService(ClinicalServiceService.class).deleteClinicalService(whoAdultEncounter,
		    ClinicalServiceKeys.PEDIATRICS_DIAGNOSIS.getCode());
		final Encounter encounuter = Context.getEncounterService().getEncounterByUuid(whoAdultEncounter);
		
		final Set<Obs> deletedObs = encounuter.getAllObs(true);
		
		MatcherAssert.assertThat(deletedObs, CoreMatchers.notNullValue());
		MatcherAssert.assertThat(deletedObs, IsCollectionWithSize.hasSize(9));
		MatcherAssert.assertThat(deletedObs,
		    CoreMatchers.hasItems(Matchers.<Obs> hasProperty("voided", CoreMatchers.is(true))));
		MatcherAssert.assertThat(encounuter, Matchers.hasProperty("voided", CoreMatchers.is(true)));
	}
	
	@Test
	public void testeMessageBundle() {
		
	}
}
