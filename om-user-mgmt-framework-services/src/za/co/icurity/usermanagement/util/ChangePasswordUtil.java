package za.co.icurity.usermanagement.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import oracle.iam.identity.exception.NoSuchUserException;
import oracle.iam.identity.exception.UserManagerException;
import oracle.iam.identity.usermgmt.api.UserManager;
import oracle.iam.identity.usermgmt.vo.User;
import oracle.iam.identity.usermgmt.vo.UserManagerResult;
import oracle.iam.platform.OIMClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import za.co.icurity.usermanagement.vo.ChangePasswordVO;
import za.co.icurity.usermanagement.vo.ProvisionUserAccountVO;
import za.co.icurity.usermanagement.vo.StatusOutVO;

public class ChangePasswordUtil {
    private static final Logger LOG = LoggerFactory.getLogger(ChangePasswordUtil.class);

    public StatusOutVO changePassword(OIMClient oimClient, ChangePasswordVO passwordVO) {
        StatusOutVO statusOutVO = new StatusOutVO();


        // BEGIN ---- get Username from ssa id
        Set<String> attrNames = null;
        String username = null;
        User user = null;
        attrNames = new HashSet<String>();
        attrNames.add("First Name");
        attrNames.add("Last Name");
        attrNames.add("User Login");

        try {
            UserManager userManager_local = oimClient.getService(UserManager.class);
            try {
                user = userManager_local.getDetails("User Login", passwordVO.getUsername(), attrNames);
            } catch (Exception ex) {
                try {
                    user = userManager_local.getDetails("Employee Number", passwordVO.getUsername(), attrNames);
                } catch (Exception exObj) {
                    statusOutVO.setStatus("Error");
                    statusOutVO.setErrorMessage("No user found for username : " + passwordVO.getUsername());
                    LOG.error(this + " changePassword: Error " + exObj.getMessage());
                    return statusOutVO;
                }
            }
            //Added for Encrypted password to Decrypt Start
            if (null != passwordVO.getPassword()) {
                String passwordReceived = passwordVO.getPassword();
                String[] randomPassword = passwordReceived.split("::");
                int length = randomPassword.length;
                if (length > 1) {
                    if ("true".equalsIgnoreCase(randomPassword[1])) {
                        passwordVO.setPassword((randomPassword[0]));

                    } 
                }else if (length == 1) {
                    passwordVO.setPassword(passwordReceived);
                } else {
                    DecryptPasswordUtil decryptPasswordUtil = new DecryptPasswordUtil();
                    passwordVO.setPassword(decryptPasswordUtil.decrypt(passwordReceived));
                }
            }
            //Added for Encrypted password to Decrypt End
            if (user != null && user.getAttributes() != null && !user.getAttributes().isEmpty() &&
                user.getAttributes().get("User Login") != null) {
                username = user.getAttributes().get("User Login").toString();
                if (passwordVO.getChangePasswordAtNextLogin().equalsIgnoreCase("Yes")) {
                    userManager_local.changePassword(username, passwordVO.getPassword().toCharArray(), true,
                                                     Locale.ENGLISH, true, false);

                    //                   BEGIN -- Change custom password flag
                    User users = null;
                    HashMap<String, Object> mapAttrs = new HashMap<String, Object>();
                    UserManagerResult result = null;
                    mapAttrs.put("pwd_flag", "true");
                    users = new User(username, mapAttrs);
                    try {
                        result = userManager_local.modify("User Login", username, users);
                    } catch (Exception ex) {
                        LOG.error(this + " changePassword: Error " + ex.getMessage());
                    }
                    //                   END -- Change custom password flag
                } else {
                    userManager_local.changePassword(username, passwordVO.getPassword().toCharArray(), true,
                                                     Locale.ENGLISH, false, false);
                    //                   BEGIN -- Change custom password flag
                    User users = null;
                    HashMap<String, Object> mapAttrs = new HashMap<String, Object>();
                    UserManagerResult result = null;
                    mapAttrs.put("pwd_flag", "true");
                    users = new User(username, mapAttrs);
                    try {
                        result = userManager_local.modify("User Login", username, users);
                    } catch (Exception ex) {
                        LOG.error(this + " changePassword: Error " + ex.getMessage());
                    }
                    //                   END -- Change custom password flag
                }
                statusOutVO.setStatus("Success");
            } else {
                LOG.error(this + " changePassword: No user found for username : " + passwordVO.getUsername());
                statusOutVO.setStatus("Error");
                statusOutVO.setErrorMessage("No user found for username : " + passwordVO.getUsername());
                return statusOutVO;
            }
        } catch (NoSuchUserException ex) {
            LOG.error(this + " changePassword: Error " + ex.getMessage());
            statusOutVO.setStatus("Error");
            statusOutVO.setErrorMessage("No user found for username : " + passwordVO.getUsername());
        } catch (UserManagerException ex) {
            LOG.error(this + " changePassword: Error " + ex.getMessage());
            statusOutVO.setStatus("Error");
            statusOutVO.setErrorMessage("Error while changing user password. Please provide valid inputs.");
        } catch (Exception ex) {
            LOG.error(this + " changePassword: Error " + ex.getMessage());
            statusOutVO.setStatus("Error");
            statusOutVO.setErrorMessage("Error while changing user password.");
        } finally {
            //Logout from OIMClient
            if (oimClient != null) {

                LOG.info(this + " changePassword", "logging out from oimclient");
                oimClient.logout();
                oimClient = null;
            }
        }
        return statusOutVO;

    }
}
