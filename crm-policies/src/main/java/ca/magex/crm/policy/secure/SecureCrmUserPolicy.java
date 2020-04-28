package ca.magex.crm.policy.secure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.common.User;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.policies.CrmUserPolicy;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.Identifier;

@Component
@Profile(value = {
		MagexCrmProfiles.CRM_AUTH_EMBEDDED,
		MagexCrmProfiles.CRM_AUTH_REMOTE
})
public class SecureCrmUserPolicy extends AbstractSecureCrmPolicy implements CrmUserPolicy {

	@Autowired private CrmUserService userService;
	@Autowired private CrmPersonService personService;

	@Override
	public boolean canCreateUserForPerson(Identifier personId) {
		User currentUser = getCurrentUser();
		/* if the user is a CRM Admin then return true */
		if (isCrmAdmin(currentUser)) {
			return true;
		}
		/*
		 * if the person belongs to the organization, then return true if they are an RE
		 * Admin
		 */
		PersonSummary person = personService.findPersonSummary(personId);
		if (currentUser.getOrganizationId().equals(person.getOrganizationId())) {
			return isReAdmin(currentUser);
		}
		/* not part of the organization that the personId belongs to */
		return false;
	}

	@Override
	public boolean canViewUser(Identifier userId) {
		User currentUser = getCurrentUser();
		/* if the user is a CRM Admin then return true */
		if (isCrmAdmin(currentUser)) {
			return true;
		}
		/* check if the target user belongs to the same organization */
		User user = userService.findUserById(userId);
		return currentUser.getOrganizationId().equals(user.getOrganizationId());
	}

	@Override
	public boolean canUpdateUserRole(Identifier userId) {
		User currentUser = getCurrentUser();
		/* if the user is a CRM Admin then return true */
		if (isCrmAdmin(currentUser)) {
			return true;
		}
		/*
		 * if the person belongs to the organization, then return true if they are an RE
		 * Admin
		 */
		User user = userService.findUserById(userId);
		if (currentUser.getOrganizationId().equals(user.getOrganizationId())) {
			return isReAdmin(currentUser);
		}
		/* not part of the organization that the personId belongs to */
		return false;
	}

	@Override
	public boolean canUpdateUserPassword(Identifier userId) {
		User currentUser = getCurrentUser();
		/* if the user is a CRM Admin then return true */
		if (isCrmAdmin(currentUser)) {
			return true;
		}
		/* current user can update their own password */
		if (currentUser.getUserId().equals(userId)) {
			return true;
		}
		
		/*
		 * if the person belongs to the organization, then return true if they are an RE
		 * Admin
		 */
		User user = userService.findUserById(userId);
		if (currentUser.getOrganizationId().equals(user.getOrganizationId())) {
			return isReAdmin(currentUser);
		}
		/* not part of the organization that the personId belongs to */
		return false;
	}
}
