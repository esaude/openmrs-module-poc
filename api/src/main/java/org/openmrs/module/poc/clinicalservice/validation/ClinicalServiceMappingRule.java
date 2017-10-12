/**
 *
 */
package org.openmrs.module.poc.clinicalservice.validation;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.poc.api.common.exception.POCBusinessException;
import org.openmrs.module.poc.clinicalservice.util.ClinicalServiceKeys;
import org.openmrs.module.poc.clinicalservice.util.MappedClinicalServices;
import org.springframework.stereotype.Component;

@Component
public class ClinicalServiceMappingRule {
	
	public void validate(final String servicekey) throws POCBusinessException {
		
		try {
			final ClinicalServiceKeys clinicalServiceKey = ClinicalServiceKeys.getClinicalServiceByCode(servicekey);
			
			final List<String> clinicalServicesUuid = MappedClinicalServices.getClinicalServices(clinicalServiceKey);
			
			final List<Concept> clinicalServices = new ArrayList<Concept>();
			for (final String clinicalServiceUuid : clinicalServicesUuid) {
				
				final Concept c = Context.getConceptService().getConceptByUuid(clinicalServiceUuid);
				if (c != null) {
					clinicalServices.add(c);
				}
			}
			if ((clinicalServices.size() != clinicalServicesUuid.size())) {
				
				throw new POCBusinessException("Some clinical services mapping value was not found");
			}
			
		}
		catch (final IllegalArgumentException e) {
			throw new POCBusinessException(e.getMessage());
		}
	}
}
