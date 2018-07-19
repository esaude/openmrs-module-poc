/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.poc.testorder.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.CareSetting;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Order.Action;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.TestOrder;
import org.openmrs.api.APIException;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.poc.api.common.service.POCDbSessionManager;
import org.openmrs.module.poc.api.common.util.OPENMRSUUIDs;
import org.openmrs.module.poc.pocheuristic.service.PocHeuristicService;
import org.openmrs.module.poc.sequencegenerator.service.PocSequenceGeneratorService;
import org.openmrs.module.poc.sequencegenerator.util.SequenceNames;
import org.openmrs.module.poc.testorder.model.TestOrderItem;
import org.openmrs.module.poc.testorder.model.TestOrderPOC;
import org.openmrs.module.poc.testorder.util.TestOrderUtil;
import org.openmrs.module.poc.testorder.validation.TestOrderRequestValidator;
import org.openmrs.module.poc.testorderresult.model.TestOrderRequestResult;
import org.openmrs.module.poc.testorderresult.model.TestOrderResult;
import org.openmrs.module.poc.testorderresult.model.TestOrderResultItem;
import org.openmrs.module.poc.testorderresult.service.TestOrderRequestResultService;
import org.openmrs.module.poc.testorderresult.service.TestOrderResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional(noRollbackFor = APIException.class)
public class TestOrderServiceImpl extends BaseOpenmrsService implements TestOrderService {

	private PocHeuristicService pocHeuristicService;

	private OrderService orderService;

	private PatientService patientService;

	private EncounterService encounterService;

	private LocationService locationService;

	private ConceptService conceptService;

	private ProviderService providerService;

	private POCDbSessionManager pOCDbSessionManager;

	private TestOrderRequestResultService testOrderRequestResultService;

	private PocSequenceGeneratorService pocSequenceGeneratorService;

	@Autowired
	private TestOrderRequestValidator testOrderRequestValidator;

	@Autowired
	private TestOrderUtil testOrderUtil;

	@Override
	public void setPocHeuristicService(final PocHeuristicService pocHeuristicService) {
		this.pocHeuristicService = pocHeuristicService;
	}

	@Override
	public void setOrderService(final OrderService orderService) {
		this.orderService = orderService;
	}

	@Override
	public void setPatientService(final PatientService patientService) {
		this.patientService = patientService;
	}

	@Override
	public void setEncounterService(final EncounterService encounterService) {
		this.encounterService = encounterService;
	}

	@Override
	public void setLocationService(final LocationService locationService) {
		this.locationService = locationService;
	}

	@Override
	public void setConceptService(final ConceptService conceptService) {
		this.conceptService = conceptService;
	}

	@Override
	public void setProviderService(final ProviderService providerService) {
		this.providerService = providerService;
	}

	@Override
	public void setPOCDbSessionManager(final POCDbSessionManager pOCDbSessionManager) {
		this.pOCDbSessionManager = pOCDbSessionManager;
	}

	@Override
	public void setTestOrderRequestResultService(final TestOrderRequestResultService testOrderRequestResultService) {
		this.testOrderRequestResultService = testOrderRequestResultService;
	}

	@Override
	public void setPocSequenceGeneratorService(final PocSequenceGeneratorService pocSequenceGeneratorService) {

		this.pocSequenceGeneratorService = pocSequenceGeneratorService;
	}

	@Override
	public TestOrderPOC createTestOder(final TestOrderPOC testOrderPoc) {

		try {

			// workaround to controll the hibernate sessions commits
			this.pOCDbSessionManager.setManualFlushMode();

			if (testOrderPoc.getDateCreation() == null) {
				testOrderPoc.setDateCreation(new Date());
			}

			this.testOrderRequestValidator.validate(testOrderPoc);

			final Patient patient = this.patientService.getPatientByUuid(testOrderPoc.getPatient().getUuid());

			final Provider provider = this.providerService.getProviderByUuid(testOrderPoc.getProvider().getUuid());
			final CareSetting careSetting = this.orderService
					.getCareSettingByUuid(OPENMRSUUIDs.OUT_PUT_PATIENT_CARE_SETTINGS_CARESETTINGS_UUID);
			final Location location = this.locationService.getLocationByUuid(testOrderPoc.getLocation().getUuid());

			final Encounter encounter = this.getEncounterByRules(patient, provider, location,
					testOrderPoc.getDateCreation());

			for (final TestOrderItem orderItem : testOrderPoc.getTestOrderItems()) {

				final Concept concept = this.conceptService
						.getConceptByUuid(orderItem.getTestOrder().getConcept().getUuid());

				final TestOrder order = new TestOrder();
				order.setConcept(concept);
				order.setPatient(testOrderPoc.getPatient());
				order.setOrderer(provider);
				order.setCareSetting(careSetting);
				order.setEncounter(encounter);
				encounter.addOrder(order);
			}

			this.setSequenciaOrderNumber(testOrderPoc, encounter);
			this.encounterService.saveEncounter(encounter);
			testOrderPoc.setEncounter(encounter);
		} finally {
			this.pOCDbSessionManager.setAutoFlushMode();
		}

		return testOrderPoc;
	}

	@Override
	public void deleteTestOrderItem(final TestOrderItem testOrderItem, final String reason) {

		final Concept examConcept = testOrderItem.getTestOrder().getConcept();
		final TestOrderPOC testOrderPOC = this.findTestOrderByEncounter(testOrderItem.getParent().getEncounter());

		if (TestOrderPOC.STATUS.COMPLETE.equals(testOrderPOC.getStatus())) {

			throw new APIException(Context.getMessageSourceService().getMessage(
					"poc.error.testorderitem.cannot.be.deleted.due.complete.status",
					new String[] { examConcept.getDisplayString() }, Context.getLocale()));
		}
		this.deleteExistingOrderResult(testOrderItem, reason);

		final Order orderByUuid = this.orderService.getOrderByUuid(testOrderItem.getUuid());
		this.orderService.voidOrder(orderByUuid, reason);
	}

	private void deleteExistingOrderResult(final TestOrderItem testOrderItem, final String reason) {

		if (TestOrderItem.ITEM_STATUS.REVISE.equals(testOrderItem.getStatus())) {

			final Concept examConcept = testOrderItem.getTestOrder().getConcept();

			final TestOrderResultService testOrderResultService = Context.getService(TestOrderResultService.class);

			final TestOrderResult testOrderResult = testOrderResultService
					.findTestOrderResultByTestRequest(testOrderItem.getTestOrder().getEncounter());

			for (final TestOrderResultItem resultItem : testOrderResult.getItems()) {

				if (examConcept.equals(resultItem.getTestOrder().getConcept())) {
					testOrderResultService.deleteTestOrderResultItem(resultItem, reason);

					final Encounter encounter = this.encounterService
							.getEncounter(testOrderItem.getTestOrder().getEncounter().getEncounterId());
					final Set<Order> orders = encounter.getOrders();

					for (final Order order : orders) {

						if (Action.NEW.equals(order.getAction()) && !order.isVoided()
								&& examConcept.equals(order.getConcept())
								&& !testOrderItem.getUuid().equals(order.getUuid())) {
							this.orderService.voidOrder(order, reason);
						}
					}
					break;
				}
			}
		}
	}

	@Override
	public List<TestOrderPOC> findTestOrdersByPatient(final String patientUUID) {

		final Patient patient = this.patientService.getPatientByUuid(patientUUID);
		final EncounterType encounterType = this.pocHeuristicService
				.findSeguimentoPacienteEncounterTypeByPatientAge(patient);
		final Map<Concept, Concept> mapCategoriesByTestConcept = this.testOrderUtil.getMapCategoriesByTestConcept();
		final List<Encounter> encounterRequests = this.pocHeuristicService
				.findEncountersWithTestOrdersByPatient(patient, encounterType);
		final List<TestOrderRequestResult> testRequestResults = this.testOrderRequestResultService
				.findTestRequestResultsByPatient(patient);

		return this.mergeTestRequestResult(encounterRequests, testRequestResults, mapCategoriesByTestConcept);
	}

	@Override
	public TestOrderPOC findTestOrderByEncounter(final Encounter encounter) {
		final Encounter encounterByUuid = this.encounterService.getEncounterByUuid(encounter.getUuid());
		final Map<Concept, Concept> mapCategories = this.testOrderUtil.getMapCategoriesByTestConcept();

		final TestOrderRequestResult testRequestResult = this.testOrderRequestResultService
				.findTestRequestResultsByRequestEncounter(encounterByUuid);

		Encounter resultEncounter = new Encounter();
		if (testRequestResult != null) {
			resultEncounter = testRequestResult.getTestOrderResult();
		}
		final TestOrderPOC testOrder = this.testOrderUtil.buildTestOrder(encounterByUuid, resultEncounter,
				mapCategories);

		if (testOrder != null) {
			return testOrder;
		}
		throw new APIException(
				Context.getMessageSourceService().getMessage("poc.error.testorder.not.found.for.encounter",
						new String[] { encounterByUuid.getEncounterDatetime().toString() }, Context.getLocale()));
	}

	@Override
	public TestOrderItem findTestOrderItemByUuid(final String uuid) {

		final Order order = this.orderService.getOrderByUuid(uuid);

		if (order != null) {

			final TestOrderRequestResult requestResult = this.testOrderRequestResultService
					.findTestRequestResultsByRequestEncounter(order.getEncounter());

			final Map<Concept, Concept> mapCategoriesByTestConcept = this.testOrderUtil.getMapCategoriesByTestConcept();

			final List<Encounter> encounterRequests = new ArrayList<>();
			encounterRequests.add(order.getEncounter());
			final List<TestOrderRequestResult> requestResults = new ArrayList<>();
			if (requestResult != null) {
				requestResults.add(requestResult);
			}

			final TestOrderPOC testOrderPOC = this
					.mergeTestRequestResult(encounterRequests, requestResults, mapCategoriesByTestConcept).iterator()
					.next();

			for (final TestOrderItem item : testOrderPOC.getTestOrderItems()) {

				if (uuid.equals(item.getTestOrder().getUuid())) {

					testOrderPOC.setTestOrderItems(new ArrayList<TestOrderItem>());
					item.setParent(testOrderPOC);
					return item;
				}
			}
		}

		throw new APIException(Context.getMessageSourceService()
				.getMessage("poc.error.testorderitem.not.found.for.uuid", new String[] { uuid }, Context.getLocale()));
	}

	private void setSequenciaOrderNumber(final TestOrderPOC testOrderPoc, final Encounter encounter) {

		final Concept sequencialConcept = this.conceptService.getConceptByUuid(OPENMRSUUIDs.REFERENCE_TYPE);

		final boolean hasSequence = StringUtils.isNotBlank(testOrderPoc.getCodeSequence()) ? true : false;

		final Set<Obs> allObs = encounter.getAllObs(false);
		boolean needNewSequencial = true;

		for (final Obs obs : allObs) {

			if (sequencialConcept.equals(obs.getConcept())) {

				if (hasSequence && !testOrderPoc.getCodeSequence().trim().equalsIgnoreCase(obs.getValueText().trim())) {
					Context.getObsService().voidObs(obs, "voided due change of sequencial number");
				} else {
					needNewSequencial = false;
				}
				break;
			}
		}

		if (needNewSequencial) {

			final Integer nextSequenceNumber = this.pocSequenceGeneratorService
					.getNextSequenceNumber(SequenceNames.TEST_ORDER);
			final String newSequencialCode = hasSequence ? testOrderPoc.getCodeSequence()
					: "ORD-" + StringUtils.leftPad(String.valueOf(nextSequenceNumber), 6, "0");

			final Obs obs = new Obs();
			obs.setConcept(sequencialConcept);
			obs.setValueText(newSequencialCode);
			encounter.addObs(obs);
		}
	}

	private List<TestOrderPOC> mergeTestRequestResult(final List<Encounter> encounterRequests,
			final List<TestOrderRequestResult> testRequestResults,
			final Map<Concept, Concept> mapCategoriesByTestConcept) {

		final Map<Encounter, Encounter> mapEncounters = new HashMap<>();

		final List<Encounter> encountersFound = new ArrayList<>();

		for (final TestOrderRequestResult testRequestResult : testRequestResults) {

			mapEncounters.put(testRequestResult.getTestOrderRequest(), testRequestResult.getTestOrderResult());
			encountersFound.add(testRequestResult.getTestOrderRequest());
		}
		encounterRequests.removeAll(encountersFound);

		for (final Encounter request : encounterRequests) {
			mapEncounters.put(request, new Encounter());
		}

		final List<TestOrderPOC> result = new ArrayList<>();

		for (final Entry<Encounter, Encounter> keyValue : mapEncounters.entrySet()) {

			final TestOrderPOC testOrder = this.testOrderUtil.buildTestOrder(keyValue.getKey(), keyValue.getValue(),
					mapCategoriesByTestConcept);

			if (testOrder != null) {
				result.add(testOrder);
			}
		}

		return result;
	}

	private Encounter getEncounterByRules(final Patient patient, final Provider provider, final Location location,
			final Date encounterDate) {

		final EncounterType encounterType = this.pocHeuristicService
				.findSeguimentoPacienteEncounterTypeByPatientAge(patient);

		try {
			return this.pocHeuristicService.findLastEncounterByPatientAndEncounterTypeAndLocationAndDateAndStatus(
					patient, encounterType, location, encounterDate);
		} catch (final APIException e) {
			final EncounterRole encounterRole = this.encounterService
					.getEncounterRoleByUuid(OPENMRSUUIDs.DEFAULT_ENCONTER_ROLE_UUID);

			return this.createEncounter(patient, provider, location, encounterDate, encounterType, encounterRole);
		}
	}

	private Encounter createEncounter(final Patient patient, final Provider provider, final Location location,
			final Date encounterDateTime, final EncounterType encounterType, final EncounterRole encounterRole) {
		final Encounter encounter = new Encounter();

		encounter.setEncounterType(encounterType);
		encounter.setPatient(patient);
		encounter.setProvider(encounterRole, provider);
		encounter.setLocation(location);
		encounter.setEncounterDatetime(encounterDateTime);

		return encounter;
	}
}
