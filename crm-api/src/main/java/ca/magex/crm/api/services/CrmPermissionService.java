package ca.magex.crm.api.services;

import java.util.List;

import org.springframework.data.domain.Page;

import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PermissionsFilter;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.Permission;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;

public interface CrmPermissionService {
	
    List<Group> findGroups();
    
    Group findGroup(Identifier groupId);
    
	Group createGroup(Localized name);
	
	Group updateGroupName(Identifier groupId, Localized name);
	
    Group enableGroup(Identifier groupId);
    
    Group disableGroup(Identifier groupId);
    
    List<Role> findRoles(Identifier groupId);
    
    Role findRole(Identifier roleId);
    
    Role findRoleByCode(String code);
    
	Role createRole(Identifier groupId, String code, Localized name);
	
	Role updateRoleName(Identifier roleId, Localized name);

    Role enableRole(Identifier roleId);
    
    Role disableRole(Identifier roleId);
    
    long countPermissions(PermissionsFilter filter);
    
    Page<Permission> findPermissions(PermissionsFilter filter, Paging paging);
    
}