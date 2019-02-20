package za.co.icurity.usermanagement.util;

import java.util.HashMap;
import java.util.List;

import java.util.Set;

import oracle.iam.identity.rolemgmt.api.RoleManager;
import oracle.iam.platform.OIMClient;

import oracle.iam.provisioning.api.ApplicationInstanceService;
import oracle.iam.provisioning.api.ProvisioningService;
import oracle.iam.provisioning.vo.Account;
import oracle.iam.provisioning.vo.AccountData;
import oracle.iam.provisioning.vo.ApplicationInstance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import za.co.icurity.usermanagement.proxy.OIMLoginProxy;
import za.co.icurity.usermanagement.vo.ADPropertiesVO;
import za.co.icurity.usermanagement.vo.ProvisionUserAccountVO;
import za.co.icurity.usermanagement.vo.StatusOutVO;
import za.co.icurity.usermanagement.vo.UserInVO;


public class ProvisionUserUtil {
    public ProvisionUserUtil() {
        super();
    }
    private static final Logger LOG = LoggerFactory.getLogger(ProvisionUserUtil.class);
    // private static final Logger LOG = LoggerFactory.getLogger(ProvisionUserUtil.class);

    public StatusOutVO provisionUser(OIMClient oimClient, ProvisionUserAccountVO provisionUserAccountVO) {
        StatusOutVO statusOutVO = new StatusOutVO();

        Set<String> returnAttrRole = null;
        HashMap configParam = null;
        String roleName = null;
        String proviosioningStatus = "";
        // OIMLoginProxy oimLoginProxy = new OIMLoginProxy();

        try {
            PropertiesUtil propertiesUtil = new PropertiesUtil();
            ADPropertiesVO adPropertiesVO = propertiesUtil.getADProperties();

            ApplicationInstanceService appInstService_local = oimClient.getService(ApplicationInstanceService.class);
        
            ProvisioningService provisioningService_local = oimClient.getService(ProvisioningService.class);
            LOG.info(this+" provisionUser ProvisioningService executed");
            RoleManager rolemanager = oimClient.getService(RoleManager.class);
            LOG.info(this+" provisionUser rolemanager "+rolemanager);
            ApplicationInstance appInstance =
                appInstService_local.findApplicationInstanceByName(adPropertiesVO.getAdInstanceName());

            HashMap<String, Object> parentData = new HashMap<String, Object>();
            parentData.put("UD_ADUSER_SERVER", adPropertiesVO.getAdServerKey());
            parentData.put("UD_ADUSER_ORGNAME", adPropertiesVO.getAdOrganizationName());
    
            AccountData accountData =
                new AccountData(String.valueOf(appInstance.getAccountForm().getFormKey()), null, parentData);

            Account account = new Account(appInstance, accountData);
            LOG.info(this+" provisionUser account "+account);
            account.setAccountType(Account.ACCOUNT_TYPE.Primary);
            provisioningService_local.provision(provisionUserAccountVO.getUserKey(), account);

            List accounts =
                provisioningService_local.getAccountsProvisionedToUser(provisionUserAccountVO.getUserKey(), true);

            proviosioningStatus = ((Account)accounts.get(accounts.size() - 1)).getAccountStatus().toString();
            LOG.info(this+" provisionUser proviosioningStatus "+proviosioningStatus);
            
            //Check with Mocx or Master: OIMClientservice checking with Provisioned, but here we getting Provisioning
            if ("Provisioning".equalsIgnoreCase(proviosioningStatus)) {
                LOG.info(this+" provisionUser suceess for username "+provisionUserAccountVO.getUsername());
                statusOutVO.setStatus("Success");
            } else {
                statusOutVO.setStatus("Error");
                statusOutVO.setErrorMessage("Error while creating user account in AD");
                LOG.error(this+" provisionUser Error while creating user account in AD for username "+provisionUserAccountVO.getUsername());
            }
        } catch (Exception ex) {
            statusOutVO.setStatus("Error");
            statusOutVO.setErrorMessage("Error while provisioning resource to AD");
            LOG.error(this + " provisionUser: ERROR: " + ex.getMessage());
            ex.printStackTrace();
            return statusOutVO;
        } finally {
            //Logout from OIMClient
            if (oimClient != null) {
                LOG.info(this + " provisionUser", "logging out");
                oimClient.logout();
                oimClient = null;
            }
            LOG.info(this + " provisionUser", "provisionUser End");
        }
        return statusOutVO;
    }
}
