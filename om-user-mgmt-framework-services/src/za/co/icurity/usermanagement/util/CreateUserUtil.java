package za.co.icurity.usermanagement.util;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import oracle.iam.identity.rolemgmt.api.RoleManager;
import oracle.iam.identity.usermgmt.api.UserManager;
import oracle.iam.identity.usermgmt.vo.User;
import oracle.iam.identity.usermgmt.vo.UserManagerResult;
import oracle.iam.platform.OIMClient;
import oracle.iam.provisioning.api.ApplicationInstanceService;
import oracle.iam.provisioning.api.ProvisioningService;
import oracle.iam.provisioning.vo.Account;
import oracle.iam.provisioning.vo.AccountData;
import oracle.iam.provisioning.vo.ApplicationInstance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import za.co.icurity.usermanagement.vo.ProvisionUserAccountVO;
import za.co.icurity.usermanagement.vo.StatusOutVO;
import za.co.icurity.usermanagement.vo.UserInVO;
import za.co.icurity.usermanagement.vo.UserOutVO;


public class CreateUserUtil {
    public CreateUserUtil() {
        super();
    }
    private static final Logger LOG = LoggerFactory.getLogger(CreateUserUtil.class);
    public UserOutVO createUser(OIMClient oimClient, UserInVO userInVO) {
        UserOutVO userOutVO = new UserOutVO();
        HashMap<String, Object> mapAttrs = null;
        UserManagerResult result = null;
        User user = null;

        mapAttrs = new HashMap<String, Object>();
        if (userInVO.getFirstName() != null) {
            mapAttrs.put("First Name", userInVO.getFirstName());
        }
        if (userInVO.getLastName() != null) {
            mapAttrs.put("Last Name", userInVO.getLastName());
        }
        if (userInVO.getPassword() != null) {
            try {
                DecryptPasswordUtil descriptPassword = new DecryptPasswordUtil();
                userInVO.setPassword(descriptPassword.decrypt(userInVO.getPassword()));
            } catch (Exception e) {
                LOG.error(this + " createUser\", \"Error: " + e.getMessage());
                /*
                             * if (logger.isLoggable(Level.SEVERE)) { logger.logp(Level.SEVERE, CLASS_NAME,
                             * "createUser", "Error: " + e.getMessage()); }
                             */
                userOutVO.setStatus("Error");
                userOutVO.setErrorMessage("Exception while decrypting the password");
                return userOutVO;
            }
            mapAttrs.put("usr_password", userInVO.getPassword());
        }
        if (userInVO.getCountry() != null) {
            mapAttrs.put("Country", userInVO.getCountry());
        }
        if (userInVO.getCellPhoneNumber() != null) {
            mapAttrs.put("Telephone Number", userInVO.getCellPhoneNumber());
        }
        if (userInVO.getUsername() != null) {
            mapAttrs.put("User Login", userInVO.getUsername());
        }
        if (userInVO.getDateOfBirth() != null) {
            mapAttrs.put("birth_date", userInVO.getDateOfBirth());
        }
        if (userInVO.getIdNumber() != null) {
            mapAttrs.put("id_number", userInVO.getIdNumber());
        }
        if (userInVO.getSsaId() != null) {
            mapAttrs.put("Employee Number", userInVO.getSsaId());
            mapAttrs.put("Common Name", userInVO.getSsaId());
        }
        if (userInVO.getGender() != null) {
            mapAttrs.put("gender", userInVO.getGender());
        }
        if (userInVO.getIdType() != null) {
            if ("RSA-ID".equalsIgnoreCase(userInVO.getIdType()))
                mapAttrs.put("id_type", "RSA ID");
            else
                mapAttrs.put("id_type", userInVO.getIdType());
        }
        if (userInVO.getCountryCode() != null) {
            mapAttrs.put("country_code", userInVO.getCountryCode());
        }
        if (userInVO.getCountryName() != null) {
            mapAttrs.put("cellphone_country", userInVO.getCountryName());
        }

        mapAttrs.put("cell_validated", String.valueOf(userInVO.isCellPhoneValidated()));
        mapAttrs.put("act_key", 1L);
        mapAttrs.put("Role", "Full-Time");
        mapAttrs.put("usr_change_pwd_at_next_logon", "0");
        mapAttrs.put("pwd_flag", "false");
        mapAttrs.put("migr_firstLogin", "false");

        user = new User(userInVO.getUsername(), mapAttrs);

        /*
             * if (logger.isLoggable(Level.FINE)) { logger.logp(Level.FINE, CLASS_NAME,
             * "createUser", "User object created : " + userInVO.getUsername()); }
             */

        try {
            UserManager userManager = oimClient.getService(UserManager.class);
            result = userManager.create(user);
            userOutVO.setUserId(result.getEntityId());
            userOutVO.setStatus("Success");
            LOG.info(this + " User object created : " + userInVO.getUsername());
        } catch (Exception ex) {
            userOutVO.setStatus("Error");
            userOutVO.setErrorMessage("Error creating user on OIM for username " + userInVO.getUsername());
            LOG.error(this + " Error creating user on OIM for username " + userInVO.getUsername());
        }
        return userOutVO;
    }

    public StatusOutVO provisionUser(OIMClient oimClient, ProvisionUserAccountVO accountVO) {

        StatusOutVO statusOutVO = new StatusOutVO();
        StringBuffer buffer = new StringBuffer();
        String appInstanceName = null;
        String adServerKey = null;
        String adOrganizationName = null;
        Set<String> returnAttrRole = null;
        HashMap configParam;
        configParam = null;
        String roleName = null;
        String proviosioningStatus = "";

        try {
            Properties properties = PropertiesUtil.getProperties();
            appInstanceName = properties.getProperty("AD_application_instance_name");
            adServerKey = properties.getProperty("AD_server_key");
            adOrganizationName = properties.getProperty("AD_organization_name");
            roleName = properties.getProperty("role_name");
        } catch (Exception e) {
            statusOutVO.setStatus("Error");
            statusOutVO.setErrorMessage("Exception on Provisioning the user");
            LOG.error(this + " Exception while setting the properties " + e.getMessage());
            return statusOutVO;
        }

        try {
            ApplicationInstanceService appInstService_local = oimClient.getService(ApplicationInstanceService.class);
            ProvisioningService provisioningService_local = oimClient.getService(ProvisioningService.class);
            RoleManager rolemanager = oimClient.getService(RoleManager.class);

            ApplicationInstance appInstance = appInstService_local.findApplicationInstanceByName(appInstanceName);

            HashMap<String, Object> parentData = new HashMap<String, Object>();
            parentData.put("UD_ADUSER_SERVER", adServerKey);
            parentData.put("UD_ADUSER_ORGNAME", adOrganizationName);

            AccountData accountData = new AccountData(String.valueOf(appInstance.getAccountForm().getFormKey()), null, parentData);

            Account account = new Account(appInstance, accountData);
            account.setAccountType(Account.ACCOUNT_TYPE.Primary);

            provisioningService_local.provision(accountVO.getUserKey(), account);

            List accounts;
            accounts = provisioningService_local.getAccountsProvisionedToUser(accountVO.getUserKey(), true);

            proviosioningStatus = ((Account)accounts.get(accounts.size() - 1)).getAccountStatus().toString();

            if ("Provisioned".equalsIgnoreCase(proviosioningStatus)) {
                statusOutVO.setStatus("Success");
            } else {
                statusOutVO.setStatus("Error");
                statusOutVO.setErrorMessage("Exception on Provisioning the user");
                LOG.error(this + " Error on provisionUser ");
            }
        } catch (Exception ex) {
            statusOutVO.setStatus("Error");
            statusOutVO.setErrorMessage("Exception on Provisioning the user");
            LOG.error(this + " Error on provisionUser " + ex.getMessage());
        }
        return statusOutVO;
    }
    
    public static String getSSAID() {

            String ssaid = "";
            String pchar = "123456789";
            int pLen = pchar.length();

            for (int i = 1; i <= 8; ++i) {
                    int index = (int) (Math.random() * pLen);
                    ssaid = ssaid + pchar.substring(index, 1 + index);
            }

            return ssaid;
    }
}
