/**
 *
 */
package org.openmrs.module.poc.api.common.validation;

import org.openmrs.OpenmrsObject;
import org.openmrs.module.poc.api.common.exception.POCBusinessException;

/**
 *
 */
public interface IValidationRule<T extends OpenmrsObject> {
	
	public void validate(T t) throws POCBusinessException;
	
}
