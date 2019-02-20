package za.co.icurity.usermanagement.util;

import java.util.HashMap;

import oracle.iam.identity.usermgmt.api.UserManager;
import oracle.iam.identity.usermgmt.vo.User;
import oracle.iam.identity.usermgmt.vo.UserManagerResult;
import oracle.iam.platform.OIMClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import za.co.icurity.usermanagement.vo.ProvisionUserAccountVO;
import za.co.icurity.usermanagement.vo.StatusOutVO;
import za.co.icurity.usermanagement.vo.UpdateUserInVO;
import za.co.icurity.usermanagement.vo.UserOutVO;

public class UpdateUserUtil {
    public UpdateUserUtil() {
        super();
    }
    private static final Logger LOG = LoggerFactory.getLogger(UpdateUserUtil.class);

    public UserOutVO updateUser(OIMClient oimClient, UpdateUserInVO updateUserInVO) {
        UserOutVO userOutVO = new UserOutVO();
        HashMap<String, Object> mapAttrs = new HashMap<String, Object>();
        UserManagerResult result = null;
        User user = null;
        UserManager userManager_local;
        try {
            userManager_local = oimClient.getService(UserManager.class);

            if (updateUserInVO.getUsername() != null) {
                mapAttrs.put("User Login", updateUserInVO.getUsername());
            }
            if (updateUserInVO.getFirstName() != null) {
                mapAttrs.put("First Name", updateUserInVO.getFirstName());
            }
            if (updateUserInVO.getLastName() != null) {
                mapAttrs.put("Last Name", updateUserInVO.getLastName());
            }
            if (updateUserInVO.getCountry() != null) {
                mapAttrs.put("Country", updateUserInVO.getCountry());
            }
            if (updateUserInVO.getCellPhoneNumber() != null) {
                mapAttrs.put("Telephone Number", updateUserInVO.getCellPhoneNumber());
            }
            mapAttrs.put("migr_firstLogin", String.valueOf(updateUserInVO.isMigratedUserFirstLogin()));

            if (updateUserInVO.getDateOfBirth() != null) {
                if (updateUserInVO.getDateOfBirth() == null ||
                    !updateUserInVO.getDateOfBirth().matches("^(3[01]|[12][0-9]|0[1-9])/(1[0-2]|0[1-9])/[0-9]{4}$")) {
                    userOutVO.setStatus("Error");
                    userOutVO.setErrorMessage("Please enter date in format dd/MM/yyyy" +
                                              System.getProperty("line.separator"));
                    return userOutVO;
                }
                mapAttrs.put("birth_date", updateUserInVO.getDateOfBirth());
            }
            if (updateUserInVO.getIdNumber() != null) {
                mapAttrs.put("id_number", updateUserInVO.getIdNumber());
            }
            if (updateUserInVO.getGender() != null) {
                mapAttrs.put("gender", updateUserInVO.getGender());
            }
            if (updateUserInVO.getIdType() != null) {
                if ("RSA-ID".equalsIgnoreCase(updateUserInVO.getIdType()))
                    mapAttrs.put("id_type", "RSA ID");
                else
                    mapAttrs.put("id_type", updateUserInVO.getIdType());
            }
            if (updateUserInVO.getIdNumber() != null) {
                mapAttrs.put("id_number", updateUserInVO.getIdNumber());
            }
            //GCS Change Start
            if (updateUserInVO.getCountryCode() != null) {
                mapAttrs.put("country_code", updateUserInVO.getCountryCode());
            }
            if (updateUserInVO.getCountryName() != null) {
                mapAttrs.put("cellphone_country", updateUserInVO.getCountryName());
            }
            //GCS Change End
            mapAttrs.put("cell_validated", String.valueOf(updateUserInVO.isCellPhoneValidated()));
            if (updateUserInVO.getPassword() != null) {
                try {
                    //Added for Encrypted password to Decrypt Start
                    DecryptPasswordUtil decryptPasswordUtil = new DecryptPasswordUtil();
                    updateUserInVO.setPassword(decryptPasswordUtil.decrypt(updateUserInVO.getPassword()));
                    //Added for Encrypted password to Decrypt End
                    LOG.info(this + "updateUser At changepassword ");
                    userManager_local.changePassword("Employee Number", updateUserInVO.getEmployeeNumber(),
                                                     updateUserInVO.getPassword().toCharArray(), false);
                } catch (Exception ex) {
                    LOG.error(this + " updateUser:  ERROR: " + ex.getMessage());
                    userOutVO.setStatus("Error");
                    ex.printStackTrace();
                    ////Added for Encrypted password to Decrypt Start
                    userOutVO.setErrorMessage("Exception while changing the password");
                    // outVO.setErrorMessage(ex.getMessage());
                    //Added for Encrypted password to Decrypt End
                    return userOutVO;
                }
                mapAttrs.put("usr_change_pwd_at_next_logon", "0");
                mapAttrs.put("pwd_flag", "false");
            }
            LOG.info(this + "updateUser mapAttrs " + mapAttrs);
            user = new User(updateUserInVO.getEmployeeNumber(), mapAttrs);
            LOG.info(this + "updateUser user " + user);
        } catch (Exception ex) {
            LOG.error(this + "updateUser", "Error: " + ex.getMessage());
            userOutVO.setStatus("Error");
            ex.printStackTrace();
            userOutVO.setErrorMessage("Exception while changing the password");
            return userOutVO;
        }
        try {
            userManager_local = oimClient.getService(UserManager.class);
            LOG.info(this + "updateUser userManager_local " + userManager_local);
            result = userManager_local.modify("Employee Number", updateUserInVO.getEmployeeNumber(), user);
            LOG.info(this + "updateUser success. result: " +result);
            userOutVO.setStatus("Success");
            userOutVO.setUserId(result.getEntityId());
            userOutVO.setErrorMessage(null);
            return userOutVO;
        } catch (Exception ex) {
            LOG.error(this + "updateUser", "Error: " + ex.getMessage());
            userOutVO.setStatus("Error");
            userOutVO.setErrorMessage(ex.getMessage());
            return userOutVO;
        } finally {
            //Logout from OIMClient
            if (oimClient != null) {
                LOG.info(this + "updateUser", "logging out");
                oimClient.logout();
                oimClient = null;
            }
        }
        
    }
}
