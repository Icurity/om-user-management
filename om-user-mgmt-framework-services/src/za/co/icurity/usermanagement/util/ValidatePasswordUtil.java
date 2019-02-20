package za.co.icurity.usermanagement.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import oracle.iam.identity.usermgmt.api.UserManager;
import oracle.iam.identity.usermgmt.vo.User;
import oracle.iam.passwordmgmt.api.PasswordMgmtService;
import oracle.iam.passwordmgmt.vo.ValidationResult;
import oracle.iam.passwordmgmt.vo.rules.PasswordRuleDescription;
import oracle.iam.platform.OIMClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import za.co.icurity.usermanagement.vo.DisableUserRequestInVO;
import za.co.icurity.usermanagement.vo.StatusOutVO;
import za.co.icurity.usermanagement.vo.ValidatePasswordVO;

public class ValidatePasswordUtil {
    public ValidatePasswordUtil() {
        super();
    }
    private static final Logger LOG = LoggerFactory.getLogger(ValidatePasswordUtil.class);

    public StatusOutVO validatePassword(OIMClient oimClient, ValidatePasswordVO passwordVO) {
        StatusOutVO statusOutVO = new StatusOutVO();
        ValidationResult result = null;
        StringBuffer buf = new StringBuffer();
        HashMap<String, Object> mapAttrs = null;
        User user = null;
        mapAttrs = new HashMap<String, Object>();

        if (passwordVO.getFirstName() != null) {
            mapAttrs.put("First Name", passwordVO.getFirstName());
        }
        if (passwordVO.getLastName() != null) {
            mapAttrs.put("Last Name", passwordVO.getLastName());
        }
        if (passwordVO.getUsername() != null) {
            mapAttrs.put("User Login", passwordVO.getUsername());
        }

        if (passwordVO.getUsername() != null && passwordVO.getUsername().trim().length() > 0 &&
            passwordVO.getFirstName() == null && passwordVO.getLastName() == null) {
            // BEGIN ---- get Username from ssa id
            Set<String> attrNames = null;
            String username = null;
            User userObj = null;
            attrNames = new HashSet<String>();
            attrNames.add("First Name");
            attrNames.add("Last Name");
            attrNames.add("User Login");
            UserManager userManager_local = oimClient.getService(UserManager.class);
            LOG.info(this + " validatePassword: UserManager " + userManager_local);
            try {
                userObj = userManager_local.getDetails("User Login", passwordVO.getUsername(), attrNames);
                LOG.info(this + " validatePassword:userObj " + userObj);
            } catch (Exception ex) {
                try {
                    user = userManager_local.getDetails("Employee Number", passwordVO.getUsername(), attrNames);
                } catch (Exception exObj) {
                    mapAttrs.put("User Login", username);
                }
            }
            if (user != null && user.getAttribute("User Login") != null) {
                username = user.getAttribute("User Login").toString();
                LOG.info(this + " validatePassword: username " + username);
            } else {
                mapAttrs.put("User Login", username);
            }
            // END ---- get Username from ssa id

            //BEGIN ---- get user id from username
            try {
                User users = userManager_local.getDetails("User Login", username, attrNames);
                if (user != null && users.getAttributes() != null && !users.getAttributes().isEmpty() &&
                    users.getAttributes().get("usr_key") != null) {
                    String userId = users.getAttributes().get("usr_key").toString();
                    LOG.info(this + " validatePassword:userId " + userId);
                    //END ---- get user id from username
                    user = new User(userId, mapAttrs);
                } else {
                    user = new User(null, mapAttrs);
                }
            } catch (Exception ex) {
                user = new User(null, mapAttrs);
            }
        } else {
            user = new User(null, mapAttrs);
        }
        try {
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
            PasswordMgmtService pswdManagementService_local = oimClient.getService(PasswordMgmtService.class);
            result =
                    pswdManagementService_local.validatePasswordAgainstPolicy(passwordVO.getPassword().toCharArray(), user,
                                                                              Locale.ENGLISH);
            LOG.info(this + " validatePassword: validatePasswordAgainstPolicy result " + result);
            if (!result.isPasswordValid() && result.getPolicyViolationsDescription() != null &&
                result.getPolicyViolationsDescription().getPasswordRulesDescription() != null &&
                !result.getPolicyViolationsDescription().getPasswordRulesDescription().isEmpty()) {
                buf.append(result.getPasswordPolicyDescription().getHeaderDisplayValue() +
                           System.getProperty("line.separator"));
                LOG.info(this + " validatePassword:buf " + buf);
                for (PasswordRuleDescription rule :
                     result.getPolicyViolationsDescription().getPasswordRulesDescription()) {
                    if (rule != null && rule.getDisplayValue() != null) {
                        buf.append(rule.getDisplayValue() + System.getProperty("line.separator"));
                    }
                }
                statusOutVO.setStatus("false");
                statusOutVO.setErrorMessage(buf.toString());
            } else {
                statusOutVO.setStatus("true");
            }
        } catch (Exception ex) {
            LOG.error(this + " validatePassword", "Error: " + ex.getMessage());
            statusOutVO.setStatus("false");
            statusOutVO.setErrorMessage("Error while validating password. Please provide valid input");
            return statusOutVO;
        } finally {
            //Logout from OIMClient
            if (oimClient != null) {
                LOG.info(this + " validatePassword", "logging out from oimclient");
                oimClient.logout();
                oimClient = null;
            }
        }
        return statusOutVO;
    }
}
