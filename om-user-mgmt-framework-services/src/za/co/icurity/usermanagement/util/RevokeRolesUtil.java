package za.co.icurity.usermanagement.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import oracle.iam.identity.exception.NoSuchRoleException;
import oracle.iam.identity.exception.NoSuchUserException;
import oracle.iam.identity.exception.RoleGrantRevokeException;
import oracle.iam.identity.exception.RoleMemberException;
import oracle.iam.identity.exception.RoleSearchException;
import oracle.iam.identity.exception.SearchKeyNotUniqueException;
import oracle.iam.identity.exception.ValidationFailedException;
import oracle.iam.identity.rolemgmt.api.RoleManager;
import oracle.iam.identity.rolemgmt.vo.Role;
import oracle.iam.identity.rolemgmt.vo.RoleManagerResult;
import oracle.iam.identity.usermgmt.api.UserManager;
import oracle.iam.identity.usermgmt.vo.User;
import oracle.iam.platform.OIMClient;

import oracle.iam.platform.entitymgr.vo.SearchCriteria;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import za.co.icurity.usermanagement.vo.StatusOutVO;
import za.co.icurity.usermanagement.vo.UserRoleGrantVO;
import za.co.icurity.usermanagement.vo.UserRoleListAssignVO;

public class RevokeRolesUtil {
    public RevokeRolesUtil() {
        super();
    }
    private static final Logger LOG = LoggerFactory.getLogger(RevokeRolesUtil.class);


    public StatusOutVO revokeRoles(OIMClient oimClient,
                                   UserRoleListAssignVO userRolegrantVO) throws ValidationFailedException,
                                                                                RoleGrantRevokeException,
                                                                                SearchKeyNotUniqueException,
                                                                                NoSuchRoleException,
                                                                                NoSuchUserException {
        StatusOutVO statusOutVO = new StatusOutVO();
        User user = null;
        String userKey = null;
        RoleManagerResult result = null;
        StringBuffer sb = new StringBuffer();

        if ((null == userRolegrantVO.getUserEntityId() || "".equalsIgnoreCase(userRolegrantVO.getUserEntityId())) &&
            (null == userRolegrantVO.getRoleName() || "".equalsIgnoreCase(userRolegrantVO.getRoleName().get(0)))) {
            statusOutVO.setStatus("Error");
            statusOutVO.setErrorMessage("Please Provide Username and RoleName");
            return statusOutVO;
        }


        Set resultVal = new HashSet();
        resultVal.add("usr_key");


        UserManager usrMgr = oimClient.getService(UserManager.class);


        if (null != userRolegrantVO.getUserEntityId() && !"".equalsIgnoreCase(userRolegrantVO.getUserEntityId())) {
            try {

                user = usrMgr.getDetails("User Login", userRolegrantVO.getUserEntityId(), resultVal);
                if (user != null) {
                    if (user.getAttribute("usr_key") != null) {
                        userKey = user.getAttribute("usr_key").toString();
                    }
                }
            } catch (Exception ex) {
                try {
                    user = usrMgr.getDetails("Employee Number", userRolegrantVO.getUserEntityId(), resultVal);
                    if (user != null) {
                        userKey = user.getAttribute("usr_key").toString();
                    }
                } catch (Exception exObj) {

                    LOG.info(this + " revokeRoles: Role Revoke Status : " + statusOutVO.getStatus());
                    statusOutVO.setStatus("Error");
                    statusOutVO.setErrorMessage("Invalid username or usernumber");
                    return statusOutVO;
                }

            }

        }

        else {
            statusOutVO.setStatus("Error");
            statusOutVO.setErrorMessage("Please provide a username or usernumber");
            return statusOutVO;
        }

        try {

            RoleManager roleManager_local = oimClient.getService(RoleManager.class);
            if (null != userRolegrantVO.getRoleName() && !"".equalsIgnoreCase(userRolegrantVO.getRoleName().get(0))) {

                for (int i = 0; i < userRolegrantVO.getRoleName().size(); i++) {
                    try {

                        // Get all children of role incase a auth role is passed in and remove the user as a member
                        // of each role
                        revokeAuthRoleChildMembership(roleManager_local, userKey, userRolegrantVO.getRoleName().get(i),
                                                      statusOutVO);

                        result =
                                roleManager_local.revokeRoleGrant("Role Name", userRolegrantVO.getRoleName().get(i), "usr_key",
                                                                  userKey);
                        statusOutVO.setStatus(result.getStatus());


                    } catch (Exception ex) {
                        LOG.info(this + "revokeRoles", "Error: " + ex.getMessage());
                        LOG.info(this + " revokeRoles: Role Revoke Status : " + statusOutVO.getStatus());
                        String errorMessage = ex.getMessage();
                        sb = sb.append(errorMessage);
                        sb = sb.append("\n");

                        statusOutVO.setErrorMessage(sb.toString());
                        //statusOutVO.setErrorMessage(ex.getMessage());
                    }

                }
            } else {
                statusOutVO.setStatus("Error");
                statusOutVO.setErrorMessage("Please provide a rolename");
            }
        } finally {
            //Logout from OIMClient
            if (oimClient != null) {

                LOG.info(this + "revokeRoles", "logging out");
                oimClient.logout();
                oimClient = null;
            }
        }
        return statusOutVO;

    }


    /**
     * Get all children of role incase a auth role is passed in and remove the user as a member
     * of each role
     *
     * @param roleManager
     * @param userKey
     * @param roleName
     * @return void
     */
    private void revokeAuthRoleChildMembership(RoleManager roleManager, String userKey, String roleName,
                                               StatusOutVO statusOutVO) throws RoleSearchException,
                                                                               RoleMemberException,
                                                                               ValidationFailedException,
                                                                               RoleGrantRevokeException,
                                                                               SearchKeyNotUniqueException,
                                                                               NoSuchRoleException,
                                                                               NoSuchUserException {

        RoleManagerResult result = null;
        Set<String> resAttrs = new HashSet<String>();
        List<Role> roles = null;
        SearchCriteria criteria1 = new SearchCriteria("Role Name", roleName, SearchCriteria.Operator.EQUAL);

        resAttrs = new HashSet<String>();
        resAttrs.add("Role Key");
        resAttrs.add("Role Name");

        roles = roleManager.search(criteria1, resAttrs, null);
        if (null != roles && roles.size() > 0) {
            Role role = roles.get(0);
            String roleKey = (String)role.getAttribute("Role Key");
            //List<Role> children = roleManager_local.getRoleChildren(roleKey, true);
            List<Role> parents = roleManager.getRoleParents(roleKey, true);
            for (Role parent : parents) {
                result = roleManager.revokeRoleGrant("Role Name", parent.getName(), "usr_key", userKey);
                statusOutVO.setStatus(result.getStatus());
            }
        }
    }
}
