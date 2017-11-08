/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc.api;

import org.junit.Before;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public abstract class POCBaseModuleContextSensitiveTest extends BaseModuleContextSensitiveTest {
	
	private static final String EXAMPLE_XML_DATASET_PACKAGE_PATH = "standardTestDataset.xml";
	
	@Override
	@Before
	public void baseSetupWithStandardDataAndAuthentication() throws Exception {
		
		if (!Context.isSessionOpen()) {
			Context.openSession();
		}
		this.initializeInMemoryDatabase();
		
		this.deleteAllData();
		
		if (this.useInMemoryDatabase()) {
			this.initializeInMemoryDatabase();
		} else {
			this.executeDataSet(BaseContextSensitiveTest.INITIAL_XML_DATASET_PACKAGE_PATH);
		}
		
		this.executeDataSet(POCBaseModuleContextSensitiveTest.EXAMPLE_XML_DATASET_PACKAGE_PATH);
		
		// Commit so that it is not rolled back after a test.
		this.getConnection().commit();
		
		this.updateSearchIndex();
		this.authenticate();
		
		Context.clearSession();
		
	}
	
}
