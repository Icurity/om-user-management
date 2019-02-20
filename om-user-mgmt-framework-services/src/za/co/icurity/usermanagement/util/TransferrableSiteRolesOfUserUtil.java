package za.co.icurity.usermanagement.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import oracle.iam.identity.rolemgmt.api.RoleManager;
import oracle.iam.identity.rolemgmt.vo.Role;
import oracle.iam.identity.usermgmt.api.UserManager;
import oracle.iam.identity.usermgmt.vo.User;
import oracle.iam.platform.OIMClient;

import oracle.iam.platform.entitymgr.vo.SearchCriteria;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import za.co.icurity.usermanagement.vo.RoleListVO;
import za.co.icurity.usermanagement.vo.RoleVO;

public class TransferrableSiteRolesOfUserUtil {
    public TransferrableSiteRolesOfUserUtil() {
        super();
    }
    
    private static final Logger LOG = LoggerFactory.getLogger(TransferrableSiteRolesOfUserUtil.class);
    
    public RoleListVO getTransferrableSiteRolesOfUser(OIMClient oimClient,String inputUsername) {
        
        RoleListVO roleListVO = new RoleListVO();
        List<Role> roles = null;     
        
        
        if (inputUsername != null && !inputUsername.trim().isEmpty()) {
            // BEGIN ---- get Username from ssa id
            Set<String> attrNames = null;
            String username = null;
            User user = null;
            attrNames = new HashSet<String>();
            attrNames.add("First Name");
            attrNames.add("Last Name");
            attrNames.add("User Login");
            UserManager userManager_local = oimClient.getService(UserManager.class);
            try {
                user = userManager_local.getDetails("User Login", inputUsername, attrNames);
            } catch (Exception ex) {
                try {
                    user = userManager_local.getDetails("Employee Number", inputUsername, attrNames);
                } catch (Exception exObj) {

                    roleListVO.setStatus("Error");
                    roleListVO.setErrorMessage("Invalid username or usernumber");
                    return roleListVO;
                }
            }
            if (user != null && user.getAttribute("User Login") != null) {
                username = user.getAttribute("User Login").toString();
            } else {
                roleListVO.setStatus("Error");
                roleListVO.setErrorMessage("Invalid username or usernumber");
                return roleListVO;
            }
            // END ---- get Username from ssa id

            try {
                if (username != null) {
                    User users = userManager_local.getDetails("User Login", username, attrNames);

                    if (user != null && users.getAttributes() != null && !users.getAttributes().isEmpty() && users.getAttributes().get("usr_key") != null) {
                        //BEGIN ---- get user id from username
                        String userId = users.getAttributes().get("usr_key").toString();
                        //END ---- get user id from username

                        //BEGIN ---- get user memberships assigned to user
                        RoleManager roleManager_local = oimClient.getService(RoleManager.class);
                        roles = roleManager_local.getUserMemberships(userId, true);
                        List<RoleVO> roleVOList = new ArrayList<RoleVO>();

                        if (roles != null && !roles.isEmpty()) {
                            for (Role role : roles) {
                                RoleVO oimRoleVO = new RoleVO();
                                String roleDelegationAllowed = null;
                                String roleType = null;
                                String linkedDelegableRole = null;

                                if (null != role.getAttribute("RoleType")) {
                                    roleType = role.getAttribute("RoleType").toString();
                                } // role.getAttribute("Role Name");
                                if (null != role.getAttribute("RoleDelegationAllowed")) {
                                    roleDelegationAllowed = role.getAttribute("RoleDelegationAllowed").toString();
                                }

                                System.out.println("Role Name : " + role.getName() + " : RoleType: " + roleType + " : " + " : roleDelegationAllowed : " + roleDelegationAllowed);
                                if ("Site Role".equalsIgnoreCase(roleType) && "No".equalsIgnoreCase(roleDelegationAllowed)) {

                                    linkedDelegableRole = (String) role.getAttribute("LinkedDelegableRole"); //Pranati
                                    Set<String> resAttrs = new HashSet<String>();
                                    List<Role> delegableRole = null;

                                    SearchCriteria criteria1 = new SearchCriteria("Role Name", linkedDelegableRole, SearchCriteria.Operator.EQUAL);

                                    resAttrs = new HashSet<String>();

                                    resAttrs.add("Role Name");
                                    resAttrs.add("Role Display Name");
                                    resAttrs.add("Role Description");

                                    delegableRole = roleManager_local.search(criteria1, resAttrs, null);
                                    if (null != delegableRole && delegableRole.size() > 0) {

                                        if (null != delegableRole.get(0).getAttribute("Role Name"))
                                            oimRoleVO.setName(delegableRole.get(0).getAttribute("Role Name").toString());
                                        if (null != delegableRole.get(0).getAttribute("Role Display Name"))
                                            oimRoleVO.setDisplayName(delegableRole.get(0).getAttribute("Role Display Name").toString());
                                        if (null != delegableRole.get(0).getAttribute("Role Description"))
                                            oimRoleVO.setRoleDescription(delegableRole.get(0).getAttribute("Role Description").toString());

                                        roleVOList.add(oimRoleVO);
                                    }

                                }
                            }
                            if (null != roleVOList && roleVOList.size() > 0) {
                                roleListVO.setRoles(roleVOList);
                                roleListVO.setStatus("Success");
                            } else {
                                roleListVO.setStatus("Error");
                                roleListVO.setErrorMessage("No transferrable site roles are present for username : " + inputUsername);
                            }
                        } else {
                            roleListVO.setStatus("Error");
                            roleListVO.setErrorMessage("No roles present for username : " + inputUsername);
                        }
                        //END ---- get user memberships assigned to user
                    } else {
                        roleListVO.setStatus("Error");
                        roleListVO.setErrorMessage("No user found for username  : " + username);
                    }
                } else {
                    roleListVO.setStatus("Error");
                    roleListVO.setErrorMessage("No user found for username : " + username);
                }
            } catch (Exception ex) {

                roleListVO.setStatus("Error");
                roleListVO.setErrorMessage("Invalid username or usernumber: " + inputUsername);
            } finally {
                //Logout from OIMClient
                if (oimClient != null) {

                    oimClient.logout();
                    oimClient = null;
                }
            }


        }

        else {
            roleListVO.setStatus("Error");
            roleListVO.setErrorMessage("Please provide a username or usernumber");
        }
        
        return roleListVO;
    }
}
