package za.co.icurity.usermanagement.util;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import oracle.iam.identity.usermgmt.api.UserManager;
import oracle.iam.identity.usermgmt.vo.User;
import oracle.iam.platform.OIMClient;
import oracle.iam.selfservice.exception.ChangePasswordException;
import oracle.iam.selfservice.self.selfmgmt.api.AuthenticatedSelfService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import za.co.icurity.usermanagement.vo.ChangePasswordForLoggedInUserVO;
import za.co.icurity.usermanagement.vo.StatusOutVO;

public class ChangePasswordForLoggedInUserUtil {
    private static final Logger LOG = LoggerFactory.getLogger(ChangePasswordForLoggedInUserUtil.class);

    public ChangePasswordForLoggedInUserUtil() {
        super();
    }

    public StatusOutVO changePasswordForLoggedInUser(OIMClient oimClient,
                                                     ChangePasswordForLoggedInUserVO changePasswordForLoggedInUserVO) {

        StatusOutVO statusOutVO = new StatusOutVO();


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
            user = userManager_local.getDetails("User Login", changePasswordForLoggedInUserVO.getUsername(), attrNames);
            if (user != null && user.getAttributes() != null && !user.getAttributes().isEmpty() &&
                user.getAttributes().get("User Login") != null) {
                username = user.getAttributes().get("User Login").toString();
            }
        } catch (Exception ex) {
            try {
                user = userManager_local.getDetails("Employee Number", changePasswordForLoggedInUserVO.getUsername(), attrNames);
                if (user != null && user.getAttributes() != null && !user.getAttributes().isEmpty() &&
                    user.getAttributes().get("User Login") != null) {
                    username = user.getAttributes().get("User Login").toString();
                }
            } catch (Exception exObj) {

                LOG.info(this + "changePasswordForLoggedInUser", "Error: " + exObj.getMessage());
                statusOutVO.setStatus("Error");
                statusOutVO.setErrorMessage("No user found for username : " +
                                            changePasswordForLoggedInUserVO.getUsername());
                return statusOutVO;
            }
        }

        if (username == null || username.equals("Error")) {
            statusOutVO.setStatus("Error");
            statusOutVO.setErrorMessage("No user found for username : " +
                                        changePasswordForLoggedInUserVO.getUsername());
            return statusOutVO;
        }


        //tcUnauthenticatedOperationsIntf authIntf = oimClient.getService(tcUnauthenticatedOperationsIntf.class);
        AuthenticatedSelfService authIntf = oimClient.getService(AuthenticatedSelfService.class);
        try {
            //  authIntf.changePasswordForUser(username, changePasswordForLoggedInUserVO.getOldPassword(), changePasswordForLoggedInUserVO.getNewPassword(), changePasswordForLoggedInUserVO.getNewPassword());
            authIntf.changePassword(changePasswordForLoggedInUserVO.getOldPassword().toCharArray(),
                                    changePasswordForLoggedInUserVO.getNewPassword().toCharArray(),
                                    changePasswordForLoggedInUserVO.getNewPassword().toCharArray());
            statusOutVO.setStatus("Success");
        } /* catch (tcPasswordIncorrectException ex) {
            if (logger.isLoggable(Level.SEVERE)) {
                logger.logp(Level.SEVERE, CLASS_NAME, "changePasswordForLoggedInUser", "Error: " + ex.getMessage());
            }
            statusOutVO.setStatus("Error");
            statusOutVO.setErrorMessage("Incorrect old password");
        } catch (tcPasswordPolicyException ex) {
            if (logger.isLoggable(Level.SEVERE)) {
                logger.logp(Level.SEVERE, CLASS_NAME, "changePasswordForLoggedInUser", "Error: " + ex.getMessage());
            }
            statusOutVO.setStatus("Error");
            statusOutVO.setErrorMessage(ex.getMessage());
        } catch (tcUserAccountInvalidException ex) {
            if (logger.isLoggable(Level.SEVERE)) {
                logger.logp(Level.SEVERE, CLASS_NAME, "changePasswordForLoggedInUser", "Error: " + ex.getMessage());
            }
            statusOutVO.setStatus("Error");
            statusOutVO.setErrorMessage(ex.getMessage());
        } catch (tcUserAccountDisabledException ex) {
            if (logger.isLoggable(Level.SEVERE)) {
                logger.logp(Level.SEVERE, CLASS_NAME, "changePasswordForLoggedInUser", "Error: " + ex.getMessage());
            }
            statusOutVO.setStatus("Error");
            statusOutVO.setErrorMessage(ex.getMessage());
        }*/

        catch (ChangePasswordException ex) {
            LOG.info(this + "changePasswordForLoggedInUser", "Error: " + ex.getMessage());
            statusOutVO.setStatus("Error");
            statusOutVO.setErrorMessage(ex.getMessage());

        } catch (Exception ex) {
            LOG.info(this + "changePasswordForLoggedInUser", "Error: " + ex.getMessage());
            statusOutVO.setStatus("Error");
            statusOutVO.setErrorMessage(ex.getMessage());
        } finally {
            // authIntf.close();
            //Logout from OIMClient
            if (oimClient != null) {
                LOG.info(this + "changePasswordLoggedInUser", "logging out");
                oimClient.logout();
                oimClient = null;
            }
        }
        return statusOutVO;

    }
}
