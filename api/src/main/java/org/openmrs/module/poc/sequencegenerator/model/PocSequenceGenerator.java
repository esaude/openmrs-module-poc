/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc.sequencegenerator.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.openmrs.module.poc.api.common.model.BaseOpenmrsObjectWrapper;
import org.openmrs.module.poc.sequencegenerator.util.SequenceNames;

@SuppressWarnings("serial")
@Entity
@Table(name = "poc_sequence_generator")
public class PocSequenceGenerator extends BaseOpenmrsObjectWrapper {
	
	@Id
	@GeneratedValue
	@Column(name = "sequence_generator_id")
	private Integer sequenceGeneratorId;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "sequence_name")
	private SequenceNames sequenceName;
	
	@Column(name = "sequence_value")
	private Integer sequenceValue;
	
	public SequenceNames getSequenceName() {
		return this.sequenceName;
	}
	
	public void setSequenceName(final SequenceNames sequenceName) {
		this.sequenceName = sequenceName;
	}
	
	public Integer getSequenceValue() {
		return this.sequenceValue;
	}
	
	public void setSequenceValue(final Integer sequenceValue) {
		this.sequenceValue = sequenceValue;
	}
	
	@Override
	public Integer getId() {
		return this.sequenceGeneratorId;
	}
	
	@Override
	public void setId(final Integer sequenceGeneratorId) {
		this.sequenceGeneratorId = sequenceGeneratorId;
	}
}
