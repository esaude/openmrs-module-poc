/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc.api.testorderresult;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Order;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.poc.api.POCBaseModuleContextSensitiveTest;
import org.openmrs.module.poc.api.common.util.OPENMRSUUIDs;
import org.openmrs.module.poc.testorderresult.model.TestOrderResult;
import org.openmrs.module.poc.testorderresult.model.TestOrderResultItem;
import org.openmrs.module.poc.testorderresult.service.TestOrderResultService;

public class TestOrderResultServiceTest extends POCBaseModuleContextSensitiveTest {
	
	@Test
	public void shouldCreateTestOrderResult() throws Exception {
		this.executeDataSet("testorderresult/shouldCreateTestOrderResult-dataset.xml");
		
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2018);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		
		final TestOrderResult testOrderResult = new TestOrderResult();
		testOrderResult.setDateCreation(calendar.getTime());
		
		final Encounter encounterRequest = new Encounter(1000);
		encounterRequest.setUuid("6519d653-393b-4118-9c83-a3715b82dnew");
		testOrderResult.setEncounterRequest(encounterRequest);
		
		final Provider provider = new Provider(1000);
		provider.setUuid("c2299800-cca9-11e0-9572-0800cprovider");
		testOrderResult.setProvider(provider);
		
		final Order order1 = Context.getOrderService().getOrder(1000);
		final Order order2 = Context.getOrderService().getOrder(1001);
		final Concept category = Context.getConceptService().getConcept(60000);
		
		final List<TestOrderResultItem> items = Arrays.asList(new TestOrderResultItem(order1, category, "10", ""),
		    new TestOrderResultItem(order2, category, "15", ""));
		testOrderResult.setItems(items);
		
		final TestOrderResult createdTestOrderResult = Context.getService(TestOrderResultService.class)
		        .createTestOrderResult(testOrderResult);
		
		Assert.assertNotNull(createdTestOrderResult);
		Assert.assertNotNull(createdTestOrderResult.getEncounterResult());
		
		Assert.assertEquals(OPENMRSUUIDs.MISAU_LABORATORIO_ENCOUNTER_TYPE_UUID,
		    createdTestOrderResult.getEncounterResult().getEncounterType().getUuid());
		
	}
	
}
