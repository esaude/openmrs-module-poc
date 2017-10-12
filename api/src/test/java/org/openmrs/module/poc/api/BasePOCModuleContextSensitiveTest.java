package org.openmrs.module.poc.api;

import org.junit.Before;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public abstract class BasePOCModuleContextSensitiveTest extends BaseModuleContextSensitiveTest {
	
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
		
		this.executeDataSet(BasePOCModuleContextSensitiveTest.EXAMPLE_XML_DATASET_PACKAGE_PATH);
		
		// Commit so that it is not rolled back after a test.
		this.getConnection().commit();
		
		this.updateSearchIndex();
		this.authenticate();
		
		Context.clearSession();
		
	}
	
}
