package ca.magex.crm.hazelcast.service;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hazelcast.core.TransactionalMap;
import com.hazelcast.flakeidgen.FlakeIdGenerator;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.validation.StructureValidationService;
import ca.magex.crm.hazelcast.xa.XATransactionAwareHazelcastInstance;

@Service
@Primary
@Profile(MagexCrmProfiles.CRM_DATASTORE_DECENTRALIZED)
@Transactional(propagation = Propagation.REQUIRED, noRollbackFor = {
		ItemNotFoundException.class,
		BadRequestException.class
})
public class HazelcastPersonService implements CrmPersonService {

	public static String HZ_PERSON_KEY = "persons";

	private XATransactionAwareHazelcastInstance hzInstance;
	private CrmOrganizationService organizationService;
	private StructureValidationService validationService;
	
	public HazelcastPersonService(
			XATransactionAwareHazelcastInstance hzInstance,
			CrmOrganizationService organizationService,
			@Lazy StructureValidationService validationService) {
		this.hzInstance = hzInstance;
		this.organizationService = organizationService;
		this.validationService = validationService;
	}

	@Override
	public PersonDetails createPerson(Identifier organizationId, PersonName legalName, MailingAddress address, Communication communication, BusinessPosition position) {
		/* run a find on the organizationId to ensure it exists */
		organizationService.findOrganizationDetails(organizationId);
		/* create our new person for this organizationId */
		TransactionalMap<Identifier, PersonDetails> persons = hzInstance.getPersonsMap();
		FlakeIdGenerator idGenerator = hzInstance.getFlakeIdGenerator(HZ_PERSON_KEY);
		PersonDetails personDetails = new PersonDetails(
				new Identifier(Long.toHexString(idGenerator.newId())),
				organizationId,
				Status.ACTIVE,
				legalName.getDisplayName(),
				legalName,
				address,
				communication,
				position);
		persons.put(personDetails.getPersonId(), validationService.validate(personDetails));
		return SerializationUtils.clone(personDetails);
	}

	@Override
	public PersonDetails updatePersonName(Identifier personId, PersonName name) {
		TransactionalMap<Identifier, PersonDetails> persons = hzInstance.getPersonsMap();
		PersonDetails personDetails = persons.get(personId);
		if (personDetails == null) {
			throw new ItemNotFoundException("Person ID '" + personId + "'");
		}
		if (personDetails.getLegalName().equals(name)) {
			return SerializationUtils.clone(personDetails);
		}
		personDetails = personDetails.withLegalName(name).withDisplayName(name.getDisplayName());
		persons.put(personId, validationService.validate(personDetails));
		return SerializationUtils.clone(personDetails);
	}

	@Override
	public PersonDetails updatePersonAddress(Identifier personId, MailingAddress address) {
		TransactionalMap<Identifier, PersonDetails> persons = hzInstance.getPersonsMap();
		PersonDetails personDetails = persons.get(personId);
		if (personDetails == null) {
			throw new ItemNotFoundException("Person ID '" + personId + "'");
		}
		if (personDetails.getAddress().equals(address)) {
			return SerializationUtils.clone(personDetails);
		}
		personDetails = personDetails.withAddress(address);
		persons.put(personId, validationService.validate(personDetails));
		return SerializationUtils.clone(personDetails);
	}

	@Override
	public PersonDetails updatePersonCommunication(Identifier personId, Communication communication) {
		TransactionalMap<Identifier, PersonDetails> persons = hzInstance.getPersonsMap();
		PersonDetails personDetails = persons.get(personId);
		if (personDetails == null) {
			throw new ItemNotFoundException("Person ID '" + personId + "'");
		}
		if (personDetails.getCommunication().equals(communication)) {
			return SerializationUtils.clone(personDetails);
		}
		personDetails = personDetails.withCommunication(communication);
		persons.put(personId, validationService.validate(personDetails));
		return SerializationUtils.clone(personDetails);
	}

	@Override
	public PersonDetails updatePersonBusinessPosition(Identifier personId, BusinessPosition position) {
		TransactionalMap<Identifier, PersonDetails> persons = hzInstance.getPersonsMap();
		PersonDetails personDetails = persons.get(personId);
		if (personDetails == null) {
			throw new ItemNotFoundException("Person ID '" + personId + "'");
		}
		if (personDetails.getPosition().equals(position)) {
			return SerializationUtils.clone(personDetails);
		}
		personDetails = personDetails.withPosition(position);
		persons.put(personId, validationService.validate(personDetails));
		return SerializationUtils.clone(personDetails);
	}

	@Override
	public PersonSummary enablePerson(Identifier personId) {
		TransactionalMap<Identifier, PersonDetails> persons = hzInstance.getPersonsMap();
		PersonDetails personDetails = persons.get(personId);
		if (personDetails == null) {
			throw new ItemNotFoundException("Person ID '" + personId + "'");
		}
		if (personDetails.getStatus() == Status.ACTIVE) {
			return SerializationUtils.clone(personDetails);
		}
		personDetails = personDetails.withStatus(Status.ACTIVE);
		persons.put(personId, validationService.validate(personDetails));
		return SerializationUtils.clone(personDetails);
	}

	@Override
	public PersonSummary disablePerson(Identifier personId) {
		TransactionalMap<Identifier, PersonDetails> persons = hzInstance.getPersonsMap();
		PersonDetails personDetails = persons.get(personId);
		if (personDetails == null) {
			throw new ItemNotFoundException("Person ID '" + personId + "'");
		}
		if (personDetails.getStatus() == Status.INACTIVE) {
			return SerializationUtils.clone(personDetails);
		}
		personDetails = personDetails.withStatus(Status.INACTIVE);
		persons.put(personId, validationService.validate(personDetails));
		return SerializationUtils.clone(personDetails);
	}

	@Override
	public PersonSummary findPersonSummary(Identifier personId) {
		return findPersonDetails(personId);
	}

	@Override
	public PersonDetails findPersonDetails(Identifier personId) {
		TransactionalMap<Identifier, PersonDetails> persons = hzInstance.getPersonsMap();
		PersonDetails personDetails = persons.get(personId);
		if (personDetails == null) {
			throw new ItemNotFoundException("Person ID '" + personId + "'");
		}
		return SerializationUtils.clone(personDetails);
	}

	@Override
	public long countPersons(PersonsFilter filter) {
		TransactionalMap<Identifier, PersonDetails> persons = hzInstance.getPersonsMap();
		return persons.values()
				.stream()
				.filter(p -> StringUtils.isNotBlank(filter.getDisplayName()) ? p.getDisplayName().contains(filter.getDisplayName()) : true)
				.filter(i -> filter.getStatus() != null ? i.getStatus().equals(filter.getStatus()) : true)
				.filter(j -> filter.getOrganizationId() != null ? j.getOrganizationId().equals(filter.getOrganizationId()) : true)
				.count();
	}

	@Override
	public FilteredPage<PersonDetails> findPersonDetails(PersonsFilter filter, Paging paging) {
		TransactionalMap<Identifier, PersonDetails> persons = hzInstance.getPersonsMap();
		List<PersonDetails> allMatchingPersons = persons.values()
				.stream()
				.filter(p -> StringUtils.isNotBlank(filter.getDisplayName()) ? p.getDisplayName().contains(filter.getDisplayName()) : true)
				.filter(i -> filter.getStatus() != null ? i.getStatus().equals(filter.getStatus()) : true)
				.filter(j -> filter.getOrganizationId() != null ? j.getOrganizationId().equals(filter.getOrganizationId()) : true)
				.map(i -> SerializationUtils.clone(i))
				.sorted(filter.getComparator(paging))
				.collect(Collectors.toList());
		return PageBuilder.buildPageFor(filter, allMatchingPersons, paging);
	}

	@Override
	public FilteredPage<PersonSummary> findPersonSummaries(PersonsFilter filter, Paging paging) {
		TransactionalMap<Identifier, PersonDetails> persons = hzInstance.getPersonsMap();
		List<PersonSummary> allMatchingPersons = persons.values()
				.stream()
				.filter(p -> StringUtils.isNotBlank(filter.getDisplayName()) ? p.getDisplayName().contains(filter.getDisplayName()) : true)
				.filter(i -> filter.getStatus() != null ? i.getStatus().equals(filter.getStatus()) : true)
				.filter(j -> filter.getOrganizationId() != null ? j.getOrganizationId().equals(filter.getOrganizationId()) : true)
				.map(i -> SerializationUtils.clone(i))
				.sorted(filter.getComparator(paging))
				.collect(Collectors.toList());
		return PageBuilder.buildPageFor(filter, allMatchingPersons, paging);
	}
	
	@Override
	public Page<PersonSummary> findActivePersonSummariesForOrg(Identifier organizationId) {
		return CrmPersonService.super.findActivePersonSummariesForOrg(organizationId);
	}
	
	@Override
	public Page<PersonDetails> findPersonDetails(PersonsFilter filter) {
		return CrmPersonService.super.findPersonDetails(filter);
	}
	
	@Override
	public Page<PersonSummary> findPersonSummaries(PersonsFilter filter) {
		return CrmPersonService.super.findPersonSummaries(filter);
	}
}