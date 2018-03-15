/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc.sequencegenerator.service;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.poc.sequencegenerator.dao.PocSequenceGeneratorDAO;
import org.openmrs.module.poc.sequencegenerator.util.SequenceNames;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface PocSequenceGeneratorService extends OpenmrsService {
	
	void setPocSequenceGeneratorDAO(PocSequenceGeneratorDAO pocSequenceGeneratorDAO);
	
	Integer getNextSequenceNumber(SequenceNames sequenceName);
	
	Integer getCurrentSequenceNumber(SequenceNames sequenceName);
}
