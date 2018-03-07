/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc.web.validation;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;

public class TestOrderConceptValidator {
	
	public void validate(final RequestContext context) {
		
		this.validateParamGroup(context.getRequest().getParameter("group"));
		this.validateParamName(context.getRequest().getParameter("name"));
	}
	
	private void validateParamGroup(final String group) {
		
		if (StringUtils.isNotBlank(group)) {
			
			final String message = Context.getMessageSourceService().getMessage("poc.error.invalid.param.value",
			    new Object[] { group, "group" }, Context.getLocale());
			throw new APIException(message);
		}
	}
	
	private void validateParamName(final String name) {
		
		if (StringUtils.isNotBlank(name)) {
			
			final String message = Context.getMessageSourceService().getMessage("poc.error.invalid.param.value",
			    new Object[] { name, "name" }, Context.getLocale());
			throw new APIException(message);
		}
	}
}
