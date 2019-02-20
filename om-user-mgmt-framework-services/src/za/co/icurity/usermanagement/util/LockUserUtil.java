package za.co.icurity.usermanagement.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import oracle.iam.identity.usermgmt.api.UserManager;
import oracle.iam.identity.usermgmt.vo.User;
import oracle.iam.platform.OIMClient;

import oracle.iam.provisioning.api.ApplicationInstanceService;
import oracle.iam.provisioning.api.ProvisioningService;
import oracle.iam.provisioning.vo.Account;
import oracle.iam.provisioning.vo.AccountData;
import oracle.iam.provisioning.vo.ApplicationInstance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import za.co.icurity.usermanagement.vo.ADPropertiesVO;
import za.co.icurity.usermanagement.vo.StatusOutVO;
import za.co.icurity.usermanagement.vo.UserRoleGrantVO;

public class LockUserUtil {
    public LockUserUtil() {
        super();
    }
    private static final Logger LOG = LoggerFactory.getLogger(LockUserUtil.class);
    
    public StatusOutVO lockUser(OIMClient oimClient, ADPropertiesVO adPropertiesVO, String username) {
        StatusOutVO statusOutVO = new StatusOutVO();
        Set<String> attrNames = new HashSet<String>();
        String userId = null;
        attrNames.add("First Name");
        attrNames.add("Last Name");
        attrNames.add("User Login");
        attrNames.add("usr_key");
        try {
            UserManager userManager_local = oimClient.getService(UserManager.class);
            LOG.info(this + " lockUser >>>>>>>> userManager_local " + userManager_local);
            try {

                User user = userManager_local.getDetails("User Login", username, attrNames);
                LOG.info(this + " lockUser <>>>>>>userManager_local " + userManager_local);
                if (user != null && user.getAttributes() != null && !user.getAttributes().isEmpty() &&
                    user.getAttributes().get("usr_key") != null) {
                    userId = user.getAttributes().get("usr_key").toString();
                    LOG.info(this + " lockUser >>>>>> userId " + userId);
                }
            } catch (Exception ex) {
                try {
                    User users = userManager_local.getDetails("Employee Number", username, attrNames);
                    LOG.info(this + " lockUser >>>>>> users " + users);
                    if (users != null && users.getAttributes() != null && !users.getAttributes().isEmpty() &&
                        users.getAttributes().get("usr_key") != null) {
                        userId = users.getAttributes().get("usr_key").toString();
                        LOG.info(this + " lockUser in inside  try >>>>>> users " + users);
                    }

                } catch (Exception exObj) {
                    LOG.error(this + " lockUser  exObj>>>>>  Error: " + exObj.getMessage());
                    statusOutVO.setStatus("Error");
                    statusOutVO.setErrorMessage("No user found for username : " + username);
                    return statusOutVO;
                }
            }
            if (userId != null && !userId.trim().isEmpty()) {
                ApplicationInstanceService appInstService_local =
                    oimClient.getService(ApplicationInstanceService.class);
                LOG.info(this + " lockUser: ApplicationInstanceService " + appInstService_local);
                ProvisioningService provisioningService_local = oimClient.getService(ProvisioningService.class);
                LOG.info(this + " lockUser  ProvisioningService" + provisioningService_local);
                List accounts = provisioningService_local.getAccountsProvisionedToUser(userId, true);
                LOG.info(this + " lockUser  accounts list " + accounts);
                ApplicationInstance appInstance =
                    appInstService_local.findApplicationInstanceByName(adPropertiesVO.getAdInstanceName());
                LOG.info(this + " lockUser appInstance " + appInstance);
                if (accounts != null && accounts.size() > 0 && accounts.get(0) != null) {
                    Account account = (Account)accounts.get(0);
                    LOG.info(this + " lockUser  account " + account);
                    if (account != null && account.getAccountStatus() != null) {
                        AccountData data = account.getAccountData();
                        LOG.info(this + " lockUser  AccountData " + data);
                        if (data != null) {
                            String accountId = account.getAccountID();
                            LOG.info(this + " lockUser  accountId " + accountId);
                            String processFormInstanceKey = account.getProcessInstanceKey();
                            LOG.info(this + " lockUser  processFormInstanceKey " + processFormInstanceKey);
                            Account modifiedAccount = new Account(accountId, processFormInstanceKey, userId);
                            LOG.info(this + " lockUser  modifiedAccount " + modifiedAccount);
                            String formKey = data.getFormKey();
                            LOG.info(this + " lockUser  formKey " + formKey);
                            String udTablePrimaryKey = data.getUdTablePrimaryKey();
                            LOG.info(this + " lockUser  udTablePrimaryKey " + udTablePrimaryKey);
                            HashMap<String, Object> modParentData = new HashMap<String, Object>();
                            modParentData.put("UD_ADUSER_LOCKED", true);

                            AccountData accountData = new AccountData(formKey, udTablePrimaryKey, modParentData);
                            LOG.info(this + " lockUser  accountData " + accountData);
                            modifiedAccount.setAccountData(accountData);
                            modifiedAccount.setAppInstance(appInstance);
                            provisioningService_local.modify(modifiedAccount);
                            LOG.info(this +
                                     " lockUser   provisioningService_local.modify(modifiedAccount) "); //check with Master
                            LOG.info(this + " lockUser  SUCCESSFUL ");
                            statusOutVO.setStatus("SUCCESSFUL"); //Check with RK,  returns successfull but user not locked
                        } else {
                            LOG.error(this + " lockUser", " Account data not found for the username " + username);
                            statusOutVO.setErrorMessage("Account data not found.");
                            statusOutVO.setStatus("Error");
                        }
                    } else {
                        LOG.error(this + " lockUser", " Account is not provisioned yet for the username " + username);
                        statusOutVO.setErrorMessage("Account is not provisioned yet");
                        statusOutVO.setStatus("Error");
                    }
                } else {
                    LOG.error(this + " lockUser", " No account provisioned to user yet. " + username);
                    statusOutVO.setErrorMessage("No account provisioned to user yet.");
                    statusOutVO.setStatus("Error");
                }
            } else {
                LOG.error(this + " lockUser", " No user found for username : " + username);
                statusOutVO.setErrorMessage("No user found for username : " + username);
                statusOutVO.setStatus("Error");
            }
        } catch (Exception e) {
            LOG.error(this + " lockUser Error: " + e.getMessage());
            statusOutVO.setErrorMessage("Error while modifying provisioned account.");
            statusOutVO.setStatus("Error");
            return statusOutVO;
        } finally {
            //Logout from OIMClient
            if (oimClient != null) {
                LOG.info(this + " lockUser ", "oimClient logging out");
                oimClient.logout();
                oimClient = null;
            }
            LOG.info(this + " lockUser", " lockUser end");
        }
        return statusOutVO;
    }
}
