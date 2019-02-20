package za.co.icurity.usermanagement.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import oracle.iam.identity.rolemgmt.api.RoleManager;
import oracle.iam.identity.rolemgmt.vo.Role;
import oracle.iam.identity.usermgmt.api.UserManager;
import oracle.iam.identity.usermgmt.vo.User;

import oracle.iam.platform.OIMClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import za.co.icurity.usermanagement.vo.AdminRoleListVO;
import za.co.icurity.usermanagement.vo.FetchUserRolesInVO;
import za.co.icurity.usermanagement.vo.RoleDetailsVO;
import za.co.icurity.usermanagement.vo.RoleListVO;

public class DelegatedAuthAdminRoleUtil {
    public DelegatedAuthAdminRoleUtil() {
        super();
    }
    private static final Logger LOG = LoggerFactory.getLogger(DelegatedAuthAdminRoleUtil.class);


    public AdminRoleListVO getDelegatedAuthAdminRole(OIMClient oimClient,
                                                            String inputUsername) {

       // AdminRoleListVO roleListVO = new AdminRoleListVO();
        AdminRoleListVO roleListVO = new AdminRoleListVO();
        List<Role> roles = null;
        String roletype = null;
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
                LOG.info(this + "getRolesAssigned", "User Attributes>>>>>>>>>>");
            } catch (Exception ex) {
                try {
                    user = userManager_local.getDetails("Employee Number", inputUsername, attrNames);
                } catch (Exception exObj) {
                    LOG.info(this + "getRolesAssigned", "Error: " + exObj.getMessage());
                    roleListVO.setStatus("Error");
                    roleListVO.setErrorMessage("Invalid username or usernumber");
                    return roleListVO;
                }
            }
            if (user != null && user.getAttribute("User Login") != null) {
                username = user.getAttribute("User Login").toString();
            } else {
                roleListVO.setStatus("Error");
                roleListVO.setErrorMessage("Invalid username or usernumber ");
                return roleListVO;
            }
            // END ---- get Username from ssa id

            try {
                if (username != null) {
                    User users = userManager_local.getDetails("User Login", username, attrNames);

                    if (user != null && users.getAttributes() != null && !users.getAttributes().isEmpty() &&
                        users.getAttributes().get("usr_key") != null) {
                        //BEGIN ---- get user id from username
                        String userId = users.getAttributes().get("usr_key").toString();
                        LOG.info(this + "getRolesAssigned", "Fetching The user ID>>" + userId);
                        //END ---- get user id from username

                        //BEGIN ---- get user memberships assigned to user
                        RoleManager roleManager_local = oimClient.getService(RoleManager.class);
                        roles = roleManager_local.getUserMemberships(userId, true);
                        List<RoleDetailsVO> roleVOList = new ArrayList<RoleDetailsVO>();
                        LOG.info(this + "getRolesAssigned", "11111111111111111>>");

                        if (roles != null && !roles.isEmpty()) {
                            for (Role role : roles) {
                                roletype = null;
                                if (null != role.getAttribute("RoleType")) {
                                    roletype = role.getAttribute("RoleType").toString();
                                }
                                if (null != roletype && roletype.equalsIgnoreCase("Site Role")) {
                                    RoleDetailsVO roleDetailsVO = new RoleDetailsVO();
                                    if (null != role.getAttribute("RoleRiskCategory"))
                                        roleDetailsVO.setRoleRiskCategory(role.getAttribute("RoleRiskCategory").toString());
                                    if (null != role.getAttribute("RoleType"))
                                        roleDetailsVO.setRoleType(role.getAttribute("RoleType").toString());
                                    if (null != role.getAttribute("Role Name"))
                                        roleDetailsVO.setName(role.getAttribute("Role Name").toString());
                                    if (null != role.getAttribute("Role Display Name"))
                                        roleDetailsVO.setDisplayName(role.getAttribute("Role Display Name").toString());
                                    if (null != role.getAttribute("RoleDelegationAllowed"))
                                        roleDetailsVO.setRoleDelegationAllowed(role.getAttribute("RoleDelegationAllowed").toString());
                                    if (null != role.getAttribute("LinkedDelegableRole"))
                                        roleDetailsVO.setLinkedDelegableRole(role.getAttribute("LinkedDelegableRole").toString());
                                    if (null != role.getAttribute("SiteUrl"))
                                        roleDetailsVO.setSiteUrl(role.getAttribute("SiteUrl").toString());
                                    LOG.info(this + "getRolesAssigned", "222222222222222222");
                                    roleVOList.add(roleDetailsVO);
                                }

                                LOG.info(this + "getRolesAssigned",
                                         "Name: " + role.getName() + ", Id: " + role.getDisplayName() +
                                         ", Description: " + role.getDescription());
                                LOG.info(this + "getRolesAssigned", "ADDDDDDIIIIINNNNNGGGG");
                            }
                            roleListVO.setRole(roleVOList);
                            if (roleVOList != null && roleVOList.size() > 0) {
                                roleListVO.setStatus("Success");
                            } else {
                                roleListVO.setStatus("Success");
                                roleListVO.setErrorMessage("No Site Role Found For The User");
                            }
                            LOG.info(this + "getRolesAssigned", "Show Successsssss");
                        } else {
                            roleListVO.setStatus("Error");
                            roleListVO.setErrorMessage("No roles granted for username : " +
                                                      inputUsername);
                            LOG.info(this + "getRolesAssigned", "Errorrrrr11111111111111111111111");
                        }
                        //END ---- get user memberships assigned to user
                    } else {
                        roleListVO.setStatus("Error");
                        roleListVO.setErrorMessage("No user found for username : " + username);
                        LOG.info(this + "getRolesAssigned", "No User Found");
                    }
                } else {
                    roleListVO.setStatus("Error");
                    roleListVO.setErrorMessage("No user found for username : " + username);
                }
            } catch (Exception ex) {
                LOG.info(this + "getRolesAssigned", "Error: " + ex.getMessage());
                roleListVO.setStatus("Error");
                roleListVO.setErrorMessage("No user found for username");
                return roleListVO;
            } finally {
                //Logout from OIMClient
                if (oimClient != null) {
                    LOG.info(this + "getRolesAssigned", "logging out");
                    oimClient.logout();
                    oimClient = null;
                }
            }
            LOG.info(this + "getRolesAssigned", "getRolesAssigned End__");
        } else {
            roleListVO.setStatus("Error");
            roleListVO.setErrorMessage("Please provide a username or usernumber");
        }

        return roleListVO;

    }
}
