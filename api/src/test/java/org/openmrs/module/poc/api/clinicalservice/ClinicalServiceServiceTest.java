package org.openmrs.module.poc.api.clinicalservice;

/*
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

 */

import java.util.List;
import java.util.Set;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.poc.api.BasePOCModuleContextSensitiveTest;
import org.openmrs.module.poc.clinicalservice.service.ClinicalServiceService;
import org.openmrs.module.poc.clinicalservice.util.ClinicalServiceKeys;

public class ClinicalServiceServiceTest extends BasePOCModuleContextSensitiveTest {
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldDeleteChildVitalsClinicalServices() throws Exception {
		this.executeDataSet("clinicalservice/shouldDeleteChildVitalsClinicalServices-dataset.xml");
		
		final String encounterUuid = "eec646cb-c847-4ss-enc-vital-child";
		
		final List<Obs> notDeletedObs = Context.getObsService().getObservations("1000");
		
		MatcherAssert.assertThat(notDeletedObs, CoreMatchers.notNullValue());
		MatcherAssert.assertThat(notDeletedObs, IsCollectionWithSize.hasSize(5));
		MatcherAssert.assertThat(notDeletedObs,
		    CoreMatchers.hasItems(Matchers.<Obs> hasProperty("voided", CoreMatchers.is(false))));
		
		Context.getService(ClinicalServiceService.class).deleteClinicalService(encounterUuid,
		    ClinicalServiceKeys.VITALS_CHILD.toString());
		final Encounter encounuter = Context.getEncounterService().getEncounterByUuid(encounterUuid);
		
		final Set<Obs> deletedObs = encounuter.getAllObs(true);
		MatcherAssert.assertThat(deletedObs, CoreMatchers.notNullValue());
		MatcherAssert.assertThat(deletedObs, IsCollectionWithSize.hasSize(5));
		MatcherAssert.assertThat(deletedObs,
		    CoreMatchers.hasItems(Matchers.<Obs> hasProperty("voided", CoreMatchers.is(true))));
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
		    ClinicalServiceKeys.VITALS_CHILD.toString());
		final Encounter encounuter = Context.getEncounterService().getEncounterByUuid(encounterUuid);
		
		final Set<Obs> deletedObs = encounuter.getAllObs(true);
		MatcherAssert.assertThat(deletedObs, CoreMatchers.notNullValue());
		MatcherAssert.assertThat(deletedObs, IsCollectionWithSize.hasSize(7));
		MatcherAssert.assertThat(deletedObs,
		    CoreMatchers.hasItems(Matchers.<Obs> hasProperty("voided", CoreMatchers.is(true))));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldDeleteAdultSocialInfotClinicalServices() throws Exception {
		this.executeDataSet("clinicalservice/shouldDeleteAdultSocialInfotClinicalServices-dataset.xml");
		
		final String vitalsEncounterUuid = "eec646cb-c8-enc-social-inf-adult";
		
		final List<Obs> notDeletedObs = Context.getObsService().getObservations("1002");
		
		MatcherAssert.assertThat(notDeletedObs, CoreMatchers.notNullValue());
		MatcherAssert.assertThat(notDeletedObs, IsCollectionWithSize.hasSize(12));
		MatcherAssert.assertThat(notDeletedObs,
		    CoreMatchers.hasItems(Matchers.<Obs> hasProperty("voided", CoreMatchers.is(false))));
		
		Context.getService(ClinicalServiceService.class).deleteClinicalService(vitalsEncounterUuid,
		    ClinicalServiceKeys.SOCIAL_INFO_ADULT.toString());
		final Encounter encounuter = Context.getEncounterService().getEncounterByUuid(vitalsEncounterUuid);
		
		final Set<Obs> deletedObs = encounuter.getAllObs(true);
		
		MatcherAssert.assertThat(deletedObs, CoreMatchers.notNullValue());
		MatcherAssert.assertThat(deletedObs, IsCollectionWithSize.hasSize(12));
		MatcherAssert.assertThat(deletedObs,
		    CoreMatchers.hasItems(Matchers.<Obs> hasProperty("voided", CoreMatchers.is(true))));
	}
}
