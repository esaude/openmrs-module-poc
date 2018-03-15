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

import org.openmrs.api.APIException;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.poc.sequencegenerator.dao.PocSequenceGeneratorDAO;
import org.openmrs.module.poc.sequencegenerator.model.PocSequenceGenerator;
import org.openmrs.module.poc.sequencegenerator.util.SequenceNames;

public class PocSequenceGeneratorServiceImpl extends BaseOpenmrsService implements PocSequenceGeneratorService {
	
	private PocSequenceGeneratorDAO pocSequenceGeneratorDAO;
	
	@Override
	public void setPocSequenceGeneratorDAO(final PocSequenceGeneratorDAO pocSequenceGeneratorDAO) {
		this.pocSequenceGeneratorDAO = pocSequenceGeneratorDAO;
	}
	
	@Override
	public Integer getNextSequenceNumber(final SequenceNames sequenceName) {
		
		PocSequenceGenerator pocSequence = this.pocSequenceGeneratorDAO.findBySequenceName(sequenceName);
		
		if (pocSequence == null) {
			pocSequence = this.pocSequenceGeneratorDAO.create(sequenceName);
		}
		pocSequence.setSequenceValue(pocSequence.getSequenceValue() + 1);
		
		this.pocSequenceGeneratorDAO.update(pocSequence);
		
		return pocSequence.getSequenceValue();
	}
	
	@Override
	public Integer getCurrentSequenceNumber(final SequenceNames sequenceName) {
		
		final PocSequenceGenerator sequenceGen = this.pocSequenceGeneratorDAO.findBySequenceName(sequenceName);
		if (sequenceGen == null) {
			throw new APIException("Sequence not Found for Sequence Name " + sequenceName);
		}
		return sequenceGen.getSequenceValue();
	}
}
