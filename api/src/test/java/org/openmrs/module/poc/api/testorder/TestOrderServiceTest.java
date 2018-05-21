/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc.api.testorder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.TestOrder;
import org.openmrs.api.context.Context;
import org.openmrs.module.poc.api.POCBaseModuleContextSensitiveTest;
import org.openmrs.module.poc.api.common.util.OPENMRSUUIDs;
import org.openmrs.module.poc.testorder.model.TestOrderItem;
import org.openmrs.module.poc.testorder.model.TestOrderPOC;
import org.openmrs.module.poc.testorder.service.TestOrderService;

public class TestOrderServiceTest extends POCBaseModuleContextSensitiveTest {

	@SuppressWarnings("unchecked")
	@Test
	public void shouldFindTestOrdersByPatient() throws Exception {
		this.executeDataSet("testorder/shouldFindTestOrdersByPatient-dataset.xml");

		final String patientUUID = "5946f880-b197-400b-9caa-a3c661d23041";
		final List<TestOrderPOC> testOrdes = Context.getService(TestOrderService.class)
				.findTestOrdersByPatient(patientUUID);

		MatcherAssert.assertThat(testOrdes, CoreMatchers.notNullValue());
		MatcherAssert.assertThat(testOrdes, IsCollectionWithSize.hasSize(1));
		MatcherAssert.assertThat(testOrdes, CoreMatchers
				.hasItems(Matchers.<TestOrderPOC>hasProperty("testOrderItems", IsCollectionWithSize.hasSize(2))));
	}

	@Test
	public void shouldFindTestOrderByEncounter() throws Exception {
		this.executeDataSet("testorder/shouldFindTestOrderByEncounter-dataset.xml");

		final Encounter encounter = new Encounter();
		encounter.setUuid("6519d653-find-test-orders-by-encounter");
		final TestOrderPOC testOrder = Context.getService(TestOrderService.class).findTestOrderByEncounter(encounter);

		MatcherAssert.assertThat(testOrder, CoreMatchers.notNullValue());
		MatcherAssert.assertThat(testOrder, Matchers.hasProperty("testOrderItems", IsCollectionWithSize.hasSize(2)));
	}

	@Test
	public void shouldFindTestOrderItemByUuid() throws Exception {
		this.executeDataSet("testorder/shouldFindTestOrderItemByUuid-dataset.xml");

		final String testOrderItemUuid = "1c96f25-find-test-order-item-by-uuid";

		final TestOrderItem testOrderItem = Context.getService(TestOrderService.class)
				.findTestOrderItemByUuid(testOrderItemUuid);

		Assert.assertNotNull(testOrderItem);
		Assert.assertEquals(OrderType.TEST_ORDER_TYPE_UUID, testOrderItem.getTestOrder().getOrderType().getUuid());
		Assert.assertEquals(Integer.valueOf(1000), testOrderItem.getTestOrder().getConcept().getId());
	}

	@Test
	public void shouldCreateTestOder() throws Exception {
		this.executeDataSet("testorder/shouldCreateTestOder-dataset.xml");

		final TestOrderPOC testOrder = this.generateTestOrderPOC(OPENMRSUUIDs.VDRL_CONCEPT_UUID,
				OPENMRSUUIDs.RAPID_PLASMA_REAGIN_CONCEP_UUID);
		TestOrderPOC createTestOder = Context.getService(TestOrderService.class).createTestOder(testOrder);
		createTestOder = Context.getService(TestOrderService.class)
				.findTestOrderByEncounter(createTestOder.getEncounter());

		Assert.assertNotNull(createTestOder);
		Assert.assertNotNull(createTestOder.getEncounter());
		Assert.assertEquals(OPENMRSUUIDs.ARV_FOLLOW_UP_ADULT_ENCOUNTER_TYPE_UUID,
				createTestOder.getEncounter().getEncounterType().getUuid());
		Assert.assertEquals(TestOrderPOC.STATUS.NEW, createTestOder.getStatus());
		MatcherAssert.assertThat(createTestOder.getTestOrderItems(), CoreMatchers.notNullValue());
		MatcherAssert.assertThat(createTestOder.getTestOrderItems(), IsCollectionWithSize.hasSize(2));
	}

	@Test
	public void shouldCreateTestOderWithNoCreationDate() throws Exception {
		this.executeDataSet("testorder/shouldCreateTestOder-dataset.xml");
		final TestOrderPOC testOrder = this.generateTestOrderPOC(OPENMRSUUIDs.VDRL_CONCEPT_UUID,
				OPENMRSUUIDs.RAPID_PLASMA_REAGIN_CONCEP_UUID);
		testOrder.setDateCreation(null);
		TestOrderPOC createTestOder = Context.getService(TestOrderService.class).createTestOder(testOrder);
		createTestOder = Context.getService(TestOrderService.class)
				.findTestOrderByEncounter(createTestOder.getEncounter());
		Assert.assertNotNull(createTestOder);
		Assert.assertNotNull(createTestOder.getDateCreation());
	}

	@Test
	@Ignore
	public void shouldDeleteTestOrderItem() throws Exception {
		this.executeDataSet("testorder/shouldDeleteTestOrderItem-dataset.xml");

		final TestOrderItem testOrderItem = new TestOrderItem();
		testOrderItem.setUuid("1c96f25c-4949ww-order-item-to-delete");

		Context.getService(TestOrderService.class).deleteTestOrderItem(testOrderItem, "cancel the order");

		final Encounter encounter = new Encounter();
		encounter.setUuid("6519d653-find-test-orders-by-encounter");

		final TestOrderPOC testOrderPOC = Context.getService(TestOrderService.class)
				.findTestOrderByEncounter(encounter);
		final TestOrderItem afterDelete = testOrderPOC.getTestOrderItems().iterator().next();

		Assert.assertNotNull(afterDelete);
		Assert.assertTrue(afterDelete.isVoided());

	}

	private TestOrderPOC generateTestOrderPOC(final String... uuids) {

		final TestOrderPOC testOrder = new TestOrderPOC();
		final Patient patient = new Patient(7);
		patient.setUuid("5946f880-b197-400b-9caa-a3c661d23041");
		testOrder.setPatient(patient);

		final Provider provider = new Provider(1);
		provider.setUuid("c2299800-cca9-11e0-9572-0800200c9a66");
		testOrder.setProvider(provider);

		final Location location = new Location(1);
		location.setUuid("8d6c993e-c2cc-11de-8d13-0010c6dffd0f");
		testOrder.setLocation(location);
		testOrder.setDateCreation(new Date());
		testOrder.setCodeSequence("001");

		final List<TestOrderItem> items = new ArrayList<>();
		for (final String uuid : uuids) {

			items.add(this.generateTestOrderItem(uuid));
		}
		testOrder.setTestOrderItems(items);

		return testOrder;
	}

	private TestOrderItem generateTestOrderItem(final String uuid) {

		final TestOrderItem item = new TestOrderItem();
		final TestOrder order1 = new TestOrder();
		final Concept concept1 = new Concept();
		concept1.setUuid(uuid);
		order1.setConcept(concept1);
		item.setTestOrder(order1);
		return item;
	}
}
