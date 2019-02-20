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

public class RevokeRoleUtil {
    public RevokeRoleUtil() {
        super();
    }
    private static final Logger LOG = LoggerFactory.getLogger(RevokeRoleUtil.class);


    public StatusOutVO revokeRole(OIMClient oimClient,
                                  UserRoleGrantVO userRolerevokeVO) throws ValidationFailedException,
                                                                           RoleGrantRevokeException,
                                                                           SearchKeyNotUniqueException,
                                                                           NoSuchRoleException, NoSuchUserException {
        User user = null;
        String userKey = null;
        RoleManagerResult result = null;
        StatusOutVO statusOutVO = new StatusOutVO();

        if ((null == userRolerevokeVO.getUserEntityId() || "".equalsIgnoreCase(userRolerevokeVO.getUserEntityId())) &&
            (null == userRolerevokeVO.getRoleName() || "".equalsIgnoreCase(userRolerevokeVO.getRoleName()))) {
            statusOutVO.setStatus("Error");
            statusOutVO.setErrorMessage("Please Provide Username and RoleName");
            return statusOutVO;
        }


        Set resultVal = new HashSet();
        resultVal.add("usr_key");


        UserManager usrMgr = oimClient.getService(UserManager.class);


        if (null != userRolerevokeVO.getUserEntityId() && !"".equalsIgnoreCase(userRolerevokeVO.getUserEntityId())) {
            try {

                user = usrMgr.getDetails("User Login", userRolerevokeVO.getUserEntityId(), resultVal);
                if (user != null) {
                    if (user.getAttribute("usr_key") != null) {
                        userKey = user.getAttribute("usr_key").toString();
                    }
                }
            } catch (Exception ex) {
                try {
                    user = usrMgr.getDetails("Employee Number", userRolerevokeVO.getUserEntityId(), resultVal);
                    if (user != null) {
                        userKey = user.getAttribute("usr_key").toString();
                    }
                } catch (Exception exObj) {
                    LOG.info(this + " revokeRole: Role Revoke Status : " + statusOutVO.getStatus());
                    LOG.info(this + "revokeRole", "Error: " + exObj.getMessage());
                    //status="User not found";
                    statusOutVO.setStatus("Error");
                    statusOutVO.setErrorMessage("Invalid username or usernumber");
                    return statusOutVO;
                }
            }
        } else {
            statusOutVO.setStatus("Error");
            statusOutVO.setErrorMessage("Please provide a username or usernumber");
            return statusOutVO;
        }

        try {

            RoleManager roleManager_local = oimClient.getService(RoleManager.class);

            if (null != userRolerevokeVO.getRoleName() && !"".equalsIgnoreCase(userRolerevokeVO.getRoleName())) {

                // Get all children of role incase a auth role is passed in and remove the user as a member
                // of each role
                revokeAuthRoleChildMembership(roleManager_local, userKey, userRolerevokeVO.getRoleName(), statusOutVO);

                result =
                        roleManager_local.revokeRoleGrant("Role Name", userRolerevokeVO.getRoleName(), "usr_key", userKey);
                statusOutVO.setStatus(result.getStatus());
            } else {
                statusOutVO.setStatus("Error");
                statusOutVO.setErrorMessage("Please provide a rolename");
            }
        } catch (Exception ex) {
            LOG.info(this + "revokeRole", "Error: " + ex.getMessage());
            // status = ex.getMessage();
            statusOutVO.setErrorMessage(ex.getMessage());
        } finally {
            //Logout from OIMClient
            if (oimClient != null) {
                LOG.info(this + "revokeRole", "logging out");
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
