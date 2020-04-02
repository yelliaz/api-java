package ca.magex.crm.api.services;

import org.springframework.data.domain.Page;

import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Role;

public interface CrmPersonService {
	
	PersonDetails createPerson(Identifier organizationId, PersonName name, MailingAddress address, Communication communication, BusinessPosition unit);
    PersonSummary enablePerson(Identifier personId);
    PersonSummary disablePerson(Identifier personId);
    PersonDetails updatePersonName(Identifier personId, PersonName name);
    PersonDetails updatePersonAddress(Identifier personId, MailingAddress address);
    PersonDetails updatePersonCommunication(Identifier personId, Communication communication);
    PersonDetails updatePersonBusinessUnit(Identifier personId, BusinessPosition unit);
    PersonDetails addUserRole(Identifier personId, Role role);
    PersonDetails removeUserRole(Identifier personId, Role role);
    PersonSummary findPersonSummary(Identifier personId);
    PersonDetails findPersonDetails(Identifier personId);
    long countPersons(PersonsFilter filter);
    Page<PersonDetails> findPersonDetails(PersonsFilter filter, Paging paging);
    Page<PersonSummary> findPersonSummaries(PersonsFilter filter, Paging paging);

}