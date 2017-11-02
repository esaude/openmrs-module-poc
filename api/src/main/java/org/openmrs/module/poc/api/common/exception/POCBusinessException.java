/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
/**
 *
 */
package org.openmrs.module.poc.api.common.exception;

import org.openmrs.api.context.Context;

public class POCBusinessException extends Exception {
	
	/**
	 *
	 */
	private static final long serialVersionUID = -1163889206769856060L;
	
	/**
	 * Constructor used to simply chain a parent exception cause to an APIException. Preference
	 * should be given to the {@link #POCBusinessException(String, Throwable)} constructor if at all
	 * possible instead of this one.
	 * 
	 * @param cause the parent exception cause that this APIException is wrapping around
	 */
	public POCBusinessException(final Throwable cause) {
		super(cause);
	}
	
	/**
	 * Constructor to give the end user a helpful message that relates to why this error occurred.
	 * 
	 * @param messageKey message code to retrieve
	 * @param parameters message parameters
	 */
	public POCBusinessException(final String messageKey, final Object... parameters) {
		super(Context.getMessageSourceService().getMessage(messageKey, parameters, Context.getLocale()));
	}
	
	/**
	 * Constructor to give the end user a helpful message and to also propagate the parent error
	 * exception message..
	 * 
	 * @param messageKey message code to retrieve
	 * @param parameters message parameters
	 * @param cause the parent exception cause that this APIException is wrapping around
	 */
	public POCBusinessException(final String messageKey, final Throwable cause, final Object... parameters) {
		super(Context.getMessageSourceService().getMessage(messageKey, parameters, Context.getLocale()), cause);
	}
}
