package ca.magex.crm.api.services;

import java.util.ArrayList;
import java.util.List;

import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.services.OrganizationPolicy;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.Role;

public class OrganizationPolicyBasicImpl implements OrganizationPolicy {
	
	public boolean canCreateOrganization() {
		return true;
	}

	public boolean canViewOrganization(Identifier organizationId) {
		return true;
	}

	public boolean canUpdateOrganization(Identifier organizationId) {
		return true;
	}

	public boolean canEnableOrganization(Identifier organizationId) {
		return true;
	}

	public boolean canDisableOrganization(Identifier organizationId) {
		return true;
	}

	public boolean canCreateLocationForOrganization(Identifier organizationId) {
		return true;
	}

	public boolean canViewLocation(Identifier locationId) {
		return true;
	}

	public boolean canUpdateLocation(Identifier locationId) {
		return true;
	}

	public boolean canEnableLocation(Identifier locationId) {
		return true;
	}

	public boolean canDisableLocation(Identifier locationId) {
		return true;
	}

	public boolean canCreatePersonForOrganization(Identifier organizationId) {
		return true;
	}

	public boolean canViewPerson(Identifier personId) {
		return true;
	}

	public boolean canUpdatePerson(Identifier personId) {
		return true;
	}

	public boolean canEnablePerson(Identifier personId) {
		return true;
	}

	public boolean canDisablePerson(Identifier personId) {
		return true;
	}

	public boolean canUpdateUserRole(Identifier personId) {
		return true;
	}

}
