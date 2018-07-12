/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc.clinicalservice.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.ConceptDatatype;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.APIException;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.poc.api.common.exception.POCBusinessException;
import org.openmrs.module.poc.clinicalservice.util.ClinicalServiceKeys;
import org.openmrs.module.poc.clinicalservice.util.ClinicalServiceUtil;
import org.openmrs.module.poc.clinicalservice.util.ConceptUUIDConstants;
import org.openmrs.module.poc.clinicalservice.util.MappedClinicalServices;
import org.openmrs.module.poc.clinicalservice.validation.ClinicalServiceValidator;
import org.springframework.beans.factory.annotation.Autowired;

public class ClinicalServiceServiceImpl extends BaseOpenmrsService implements ClinicalServiceService {
	
	private ObsService obsService;
	
	private EncounterService encounterService;
	
	@Autowired
	private ClinicalServiceValidator clinicalServiceValidator;
	
	@Autowired
	private ClinicalServiceUtil clinicalServiceUtil;
	
	private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm";
	
	private static final String DATE_PATTERN = "yyyy-MM-dd";
	
	private static final List<String> EXCLUDING_CONCEPTS = Arrays.asList(ConceptUUIDConstants.POC_MAPPING_WHO_DATE,
	    ConceptUUIDConstants.POC_MAPPING_ANAMNESE_DATE, ConceptUUIDConstants.POC_MAPPING_DIAGNOSIS_DATE,
	    ConceptUUIDConstants.POC_MAPPING_VITALS_DATE);
	
	@Override
	public void deleteClinicalService(final String encounterUuid, final String clinicalServiceKey)
	        throws POCBusinessException {
		
		final Encounter es = new Encounter();
		es.setUuid(encounterUuid);
		
		this.clinicalServiceValidator.validateDeletion(es, clinicalServiceKey);
		
		final Encounter encounter = this.encounterService.getEncounterByUuid(encounterUuid);
		final List<Concept> clinicalServices = this.clinicalServiceUtil.getNotVoidedConcepts(clinicalServiceKey);
		
		final Set<Obs> allObs = encounter.getAllObs();
		
		for (final Obs obs : allObs) {
			
			final Concept obsClinicalService = obs.getConcept();
			
			final String infoMessage = Context.getMessageSourceService().getMessage(
			    "poc.info.clinical.service.inactivated",
			    Arrays.asList(obsClinicalService.getDisplayString()).toArray(), Context.getLocale());
			
			if (clinicalServices.contains(obsClinicalService) && !obs.isVoided()) {
				this.obsService.voidObs(obs, infoMessage);
			}
		}
		
		final Set<Obs> remainActiveObss = encounter.getAllObs();
		if (remainActiveObss.isEmpty()) {
			
			this.encounterService.voidEncounter(encounter, Context.getMessageSourceService()
			        .getMessage("poc.info.encounter.inactivated.due.deleted.all.clinical.services"));
		} else {
			
			final List<Concept> allClinicalServices = this.getAllClinicalServices();
			for (final Obs obs : remainActiveObss) {
				
				final String infoMessage = Context.getMessageSourceService().getMessage(
				    "poc.info.clinical.service.inactivated",
				    Arrays.asList(obs.getConcept().getDisplayString()).toArray(), Context.getLocale());
				
				if (!allClinicalServices.contains(obs.getConcept()) && obs.getRelatedObservations().isEmpty()) {
					this.obsService.voidObs(obs, infoMessage);
				}
			}
			if (encounter.getAllObs().isEmpty()) {
				
				this.encounterService.voidEncounter(encounter, Context.getMessageSourceService()
				        .getMessage("poc.info.encounter.inactivated.due.deleted.all.clinical.services"));
			}
		}
	}
	
	private List<Concept> getAllClinicalServices() {
		
		final List<Concept> allClinicalServices = new ArrayList<>();
		
		for (final ClinicalServiceKeys clinicalServiceKey : ClinicalServiceKeys.values()) {
			
			allClinicalServices.addAll(this.clinicalServiceUtil.getNotVoidedConcepts(clinicalServiceKey.getCode()));
		}
		return allClinicalServices;
	}
	
	@Override
	public void setObsService(final ObsService obsService) {
		this.obsService = obsService;
	}
	
	@Override
	public void setEncounterService(final EncounterService encounterService) {
		this.encounterService = encounterService;
	}
	
	@Override
	public Encounter updateClinicalService(final String serviceCode, final Encounter encounter,
	        final List<Map<String, Object>> lstMapNewObs) {
		
		final Encounter fetchedEncounter = this.encounterService.getEncounterByUuid(encounter.getUuid());
		
		if ((serviceCode != null) && (fetchedEncounter != null)) {
			this.deleteOldObsOfClinicalService(fetchedEncounter, serviceCode);
			this.createNewObsForClinicalService(fetchedEncounter, lstMapNewObs);
			return this.encounterService.saveEncounter(fetchedEncounter);
		}
		throw new APIException(Context.getMessageSourceService().getMessage(
		    "poc.error.cannot.update.details.forgiven.service.and.encounter",
		    new String[] { serviceCode, (fetchedEncounter != null) ? fetchedEncounter.getUuid() : null },
		    Context.getLocale()));
	}
	
	private void deleteOldObsOfClinicalService(final Encounter fetchedEncounter, final String serviceCode) {
		
		final List<String> clinicalServices = MappedClinicalServices
		        .getClinicalServices(ClinicalServiceKeys.getClinicalServiceByCode(serviceCode));
		
		final Set<Obs> allOldObs = fetchedEncounter.getAllObs(false);
		for (final Obs oldItemObs : allOldObs) {
			
			final String conceptUuid = oldItemObs.getConcept().getUuid();
			if ((oldItemObs.getObsId() != null) && clinicalServices.contains(conceptUuid)
			        && !ClinicalServiceServiceImpl.EXCLUDING_CONCEPTS.contains(conceptUuid)) {
				this.obsService.voidObs(oldItemObs,
				    Context.getMessageSourceService().getMessage("poc.global.info.voided.forupdate"));
			}
		}
	}
	
	private void createNewObsForClinicalService(final Encounter fetchedEncounter,
	        final List<Map<String, Object>> lstMapNewObs) {
		
		try {
			for (final Map<String, Object> obsMap : lstMapNewObs) {
				
				final Concept concept = Context.getConceptService().getConceptByUuid((String) obsMap.get("concept"));
				final Obs obs = new Obs();
				obs.setConcept(concept);
				this.setObsValue(obs, concept, obsMap.get("value"));
				obs.setPerson(fetchedEncounter.getPatient());
				fetchedEncounter.addObs(obs);
			}
		}
		catch (final ParseException e) {
			throw new APIException(e);
		}
	}
	
	private void setObsValue(final Obs obs, final Concept concept, final Object value) throws ParseException {
		
		final ConceptDatatype datatype = concept.getDatatype();
		if (datatype.isNumeric()) {
			obs.setValueNumeric(Double.valueOf(value.toString()));
		} else if (datatype.isBoolean()) {
			obs.setValueBoolean(Boolean.valueOf(value.toString()));
		} else if (datatype.isDate()) {
			final DateFormat dateFormat = new SimpleDateFormat(ClinicalServiceServiceImpl.DATE_PATTERN);
			obs.setValueDate(dateFormat.parse(value.toString()));
		} else if (datatype.isDateTime()) {
			final DateFormat dateFormat = new SimpleDateFormat(ClinicalServiceServiceImpl.DATE_TIME_PATTERN);
			obs.setValueDatetime(dateFormat.parse(value.toString()));
		} else if (datatype.isCoded()) {
			obs.setValueCoded(Context.getConceptService().getConceptByUuid(value.toString()));
		} else if (datatype.isText()) {
			obs.setValueText(value.toString());
		} else {
			throw new RuntimeException("Don't know how to handle " + datatype);
		}
	}
}
