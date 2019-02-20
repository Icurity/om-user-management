package za.co.icurity.usermanagement.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import za.co.icurity.usermanagement.vo.ADPropertiesVO;
import za.co.icurity.usermanagement.vo.StatusOutVO;

public class UnlockAccountUtil {
    public UnlockAccountUtil() {
        super();
    }
    private static final Logger LOG = LoggerFactory.getLogger(UnlockAccountUtil.class);
    
    public StatusOutVO unLockUser(OIMClient oimClient, ADPropertiesVO adPropertiesVO, String username) {
        StatusOutVO statusOutVO = new StatusOutVO();
        Set<String> attrNames = new HashSet<String>();
        User usersLocked = null;
        UserManagerResult resultLocked = null;
        User usersActive = null;
        UserManagerResult resultActive = null;
        String badPasswordCount = null;
        String retryCount = null;
        String userId = null;
        attrNames.add("First Name");
        attrNames.add("Last Name");
        attrNames.add("User Login");
        attrNames.add("usr_key");
        try {
            UserManager userManager_local = oimClient.getService(UserManager.class);
            try {
                User user = userManager_local.getDetails("User Login", username, attrNames);
                if (user != null && user.getAttributes() != null && !user.getAttributes().isEmpty() &&
                    user.getAttributes().get("usr_key") != null) {
                    userId = user.getAttributes().get("usr_key").toString();
                }
            } catch (Exception ex) {
                try {
                    User users = userManager_local.getDetails("Employee Number", username, attrNames);
                    if (users != null && users.getAttributes() != null && !users.getAttributes().isEmpty() &&
                        users.getAttributes().get("usr_key") != null) {
                        userId = users.getAttributes().get("usr_key").toString();
                    }

                } catch (Exception exObj) {
                    LOG.error(this + " unlockAccount", "Error: " + exObj.getMessage());
                    statusOutVO.setStatus("Error");
                    statusOutVO.setErrorMessage("No user found for username : " + username);
                    return statusOutVO;
                }
            }
            if (userId != null && !userId.trim().isEmpty()) {
                ApplicationInstanceService appInstService_local =
                    oimClient.getService(ApplicationInstanceService.class);
                ProvisioningService provisioningService_local = oimClient.getService(ProvisioningService.class);


                //Locking and Unlocking the account

                List accountsUL = provisioningService_local.getAccountsProvisionedToUser(userId, true);
                ApplicationInstance appInstanceUL =
                    appInstService_local.findApplicationInstanceByName(adPropertiesVO.getAdInstanceName());

                if (accountsUL != null && accountsUL.size() > 0 && accountsUL.get(0) != null) {
                    Account account = (Account)accountsUL.get(0);
                    if (account != null && account.getAccountStatus() != null) {
                        AccountData data = account.getAccountData();
                        if (data != null) {
                            String accountId = account.getAccountID();
                            LOG.info(this + " unlockAccount >>>>>>>>>>>>>>>>>>>>>> accountId " + accountId);
                            String processFormInstanceKey = account.getProcessInstanceKey();
                            LOG.info(this + " unlockAccount >>.......> processFormInstanceKey " +
                                     processFormInstanceKey);
                            Account modifiedAccount = new Account(accountId, processFormInstanceKey, userId);
                            LOG.info(this + " unlockAccount >>>>>>>>>>>>>>>>>>>>>> modifiedAccount " +
                                     modifiedAccount);
                            String formKey = data.getFormKey();
                            LOG.info(this + " unlockAccount >>>>>>>>>>>>>>>>>>>>>> formKey " + formKey);
                            String udTablePrimaryKey = data.getUdTablePrimaryKey();
                            LOG.info(this + " unlockAccount >>>>>>>>>>>>>>>>>>>>>> udTablePrimaryKey " +
                                     udTablePrimaryKey);
                            HashMap<String, Object> modParentData = new HashMap<String, Object>();
                            badPasswordCount = data.getData().get("UD_ADUSER_BAD_PWD_COUNT").toString();
                            LOG.info(this + " unlockAccount >>>>>>> badPasswordCount " + badPasswordCount);
                            retryCount = data.getData().get("UD_ADUSER_OBTRYCOUNT").toString();
                            LOG.info(this + " unlockAccount >>>>> retryCount " + retryCount);
                            HashMap<String, Object> userAttributeValueMapLocked = new HashMap<String, Object>();
                            userAttributeValueMapLocked.put("account_status", "Locked");
                            HashMap<String, Object> userAttributeValueMapActive = new HashMap<String, Object>();
                            userAttributeValueMapActive.put("account_status", "Active");
                            if (userId != null && !userId.trim().isEmpty()) {
                                LOG.info(this + " unlockAccount >>>>> userAttributeValueMapLocked " +
                                         userAttributeValueMapLocked);
                                usersLocked = new User(userId, userAttributeValueMapLocked);
                                LOG.info(this + " unlockAccount >>>>> usersLocked " + usersLocked);
                                usersActive = new User(userId, userAttributeValueMapActive);
                                LOG.info(this + " unlockAccount >>>>> usersActive " + usersActive);
                            }

                            resultLocked = userManager_local.modify("usr_key", userId, usersLocked);
                            LOG.info(this + " unlockAccount >>>>> resultLocked " + resultLocked);
                            if ((null == badPasswordCount || "".equalsIgnoreCase(badPasswordCount)) &&
                                ((null == retryCount) || ("".equalsIgnoreCase(retryCount)))) {
                                modParentData.put("UD_ADUSER_BAD_PWD_COUNT", "0");
                                modParentData.put("UD_ADUSER_OBTRYCOUNT", "0");
                            } else {
                                modParentData.put("UD_ADUSER_BAD_PWD_COUNT", "");
                                modParentData.put("UD_ADUSER_OBTRYCOUNT", "");
                            }
                            AccountData accountData = new AccountData(formKey, udTablePrimaryKey, modParentData);
                            LOG.info(this + " unlockAccount >>>>> accountData " + accountData);
                            modifiedAccount.setAccountData(accountData);
                            modifiedAccount.setAppInstance(appInstanceUL);

                            provisioningService_local.modify(modifiedAccount);
                            LOG.info(this + " unlockAccount >>>>> provisioningService_local.modify ");
                            resultActive = userManager_local.modify("usr_key", userId, usersActive);
                            LOG.info(this + " unlockAccount >>>>> resultActive " + resultActive);
                            LOG.info(this + " unlockAccount", " SUCCESSFUL ");
                            statusOutVO.setStatus("SUCCESSFUL"); //Check with RK,  returns successfull but user not locked
                        } else {
                            LOG.info(this + " unlockAccount", " Account data not found. ");
                            statusOutVO.setErrorMessage("Account data not found.");
                            statusOutVO.setStatus("Error");
                        }
                    } else {
                        LOG.info(this + " unlockAccount", " Account is not provisioned yet ");
                        statusOutVO.setErrorMessage("Account is not provisioned yet");
                        statusOutVO.setStatus("Error");
                    }
                } else {
                    LOG.info(this + " unlockAccount", " No account provisioned to user yet. ");
                    statusOutVO.setErrorMessage("No account provisioned to user yet.");
                    statusOutVO.setStatus("Error");
                }
            } else {
                LOG.error(this + " unlockAccount", " No user found for username : " + username);
                statusOutVO.setErrorMessage("No user found for username : " + username);
                statusOutVO.setStatus("Error");
            }
        } catch (Exception e) {
            LOG.error(this + " unlockAccount", "Error: " + e.getMessage());
            statusOutVO.setErrorMessage("Error while modifying provisioned account.");
            statusOutVO.setStatus("Error");
            return statusOutVO;
        } finally {
            //Logout from OIMClient
            if (oimClient != null) {
                LOG.info(this + " unlockAccount", " oimClient logging out");
                oimClient.logout();
                oimClient = null;
            }
        }
        LOG.info(this + " unlockAccount", " unlockAccount end");
        return statusOutVO;
    }
}
