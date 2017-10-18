/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.ModuleActivator;

/**
 * This class contains the logic that is run every time this module is either started or stopped.
 */
public class OpenMRSPOCModuleActivator implements ModuleActivator {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @see ModuleActivator#willRefreshContext()
	 */
	@Override
	public void willRefreshContext() {
		this.log.info("Refreshing OpenMRS POC Module Module");
	}
	
	/**
	 * @see ModuleActivator#contextRefreshed()
	 */
	@Override
	public void contextRefreshed() {
		this.log.info("OpenMRS POC Module Module refreshed");
	}
	
	/**
	 * @see ModuleActivator#willStart()
	 */
	@Override
	public void willStart() {
		this.log.info("Starting OpenMRS POC Module Module");
	}
	
	/**
	 * @see ModuleActivator#started()
	 */
	@Override
	public void started() {
		this.log.info("OpenMRS POC Module Module started");
	}
	
	/**
	 * @see ModuleActivator#willStop()
	 */
	@Override
	public void willStop() {
		this.log.info("Stopping OpenMRS POC Module Module");
	}
	
	/**
	 * @see ModuleActivator#stopped()
	 */
	@Override
	public void stopped() {
		this.log.info("OpenMRS POC Module Module stopped");
	}
	
}
