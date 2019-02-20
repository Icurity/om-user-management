package za.co.icurity.usermanagement.util;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import oracle.iam.identity.exception.NoSuchRoleException;
import oracle.iam.identity.exception.NoSuchUserException;
import oracle.iam.identity.exception.RoleGrantRevokeException;
import oracle.iam.identity.exception.SearchKeyNotUniqueException;
import oracle.iam.identity.exception.ValidationFailedException;
import oracle.iam.identity.rolemgmt.api.RoleManager;
import oracle.iam.identity.rolemgmt.vo.RoleManagerResult;
import oracle.iam.identity.usermgmt.api.UserManager;
import oracle.iam.identity.usermgmt.vo.User;
import oracle.iam.platform.OIMClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import za.co.icurity.usermanagement.vo.StatusOutVO;
import za.co.icurity.usermanagement.vo.UserRoleListAssignVO;

public class AssignRolesUtil {
    public AssignRolesUtil() {
        super();
    }
    private static final Logger LOG = LoggerFactory.getLogger(AssignRolesUtil.class);

    public StatusOutVO assignRoles(OIMClient oimClient,
                                   UserRoleListAssignVO userRolegrantVO) throws ValidationFailedException,
                                                                                RoleGrantRevokeException,
                                                                                SearchKeyNotUniqueException,
                                                                                NoSuchRoleException,
                                                                                NoSuchUserException {

        RoleManagerResult result = null;
        User user = null;
        String userKey = null;
        StatusOutVO statusOutVO = new StatusOutVO();
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

        // SearchCriteria search1=new SearchCriteria("User Login",userRolegrantVO.getUserEntityId(),SearchCriteria.Operator.EQUAL);

        if (null != userRolegrantVO.getUserEntityId() && !"".equalsIgnoreCase(userRolegrantVO.getUserEntityId())) {
            try {
                user = usrMgr.getDetails("User Login", userRolegrantVO.getUserEntityId(), resultVal);
                if (user != null) {
                    if (user.getAttribute("usr_key") != null) {
                        userKey = user.getAttribute("usr_key").toString();
                    }
                } else {
                    statusOutVO.setStatus("Error");
                    statusOutVO.setErrorMessage("Invalid username or usernumber");
                }

            } catch (Exception ex) {
                try {
                    user = usrMgr.getDetails("Employee Number", userRolegrantVO.getUserEntityId(), resultVal);
                    if (user != null) {
                        userKey = user.getAttribute("usr_key").toString();
                    }
                } catch (Exception exObj) {

                    LOG.info(this + "assignRoles", "Error: " + exObj.getMessage());
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

            if (null != userRolegrantVO.getRoleName() && !"".equalsIgnoreCase(userRolegrantVO.getRoleName().get(0))) {
                for (int i = 0; i < userRolegrantVO.getRoleName().size(); i++) {
                    try {
                        result =
                                roleManager_local.grantRole("Role Name", userRolegrantVO.getRoleName().get(i), "usr_key",
                                                            userKey);
                        statusOutVO.setStatus(result.getStatus());
                    } catch (Exception e) {
                        //statusOutVO.setErrorMessage(e.getMessage());
                        String errorMessage = e.getMessage();
                        sb = sb.append(errorMessage);
                        sb = sb.append("\n");

                        statusOutVO.setErrorMessage(sb.toString());
                    }

                }
            } else {
                statusOutVO.setStatus("Error");
                statusOutVO.setErrorMessage("Please provide a rolename");
            }
        }

        finally {
            //Logout from OIMClient
            if (oimClient != null) {

                LOG.info(this + "assignRoles", "logging out");
                oimClient.logout();
                oimClient = null;
            }
        }

        return statusOutVO;
    }
}
