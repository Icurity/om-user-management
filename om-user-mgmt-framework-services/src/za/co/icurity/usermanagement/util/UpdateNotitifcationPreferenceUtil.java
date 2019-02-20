package za.co.icurity.usermanagement.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import oracle.iam.identity.exception.NoSuchRoleException;
import oracle.iam.identity.exception.NoSuchUserException;
import oracle.iam.identity.exception.RoleGrantRevokeException;
import oracle.iam.identity.exception.SearchKeyNotUniqueException;
import oracle.iam.identity.exception.ValidationFailedException;
import oracle.iam.identity.usermgmt.api.UserManager;
import oracle.iam.identity.usermgmt.vo.User;
import oracle.iam.identity.usermgmt.vo.UserManagerResult;
import oracle.iam.platform.OIMClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import za.co.icurity.usermanagement.vo.NotitifcationPreferenceVO;
import za.co.icurity.usermanagement.vo.StatusOutVO;
import za.co.icurity.usermanagement.vo.UserRoleListAssignVO;

public class UpdateNotitifcationPreferenceUtil {
    public UpdateNotitifcationPreferenceUtil() {
        super();
    }
    private static final Logger LOG = LoggerFactory.getLogger(UpdateNotitifcationPreferenceUtil.class);

    public StatusOutVO updateNotitifcationPreference(OIMClient oimClient,
                                                     NotitifcationPreferenceVO notitifcationPreferenceVO) throws ValidationFailedException,
                                                                                                                 RoleGrantRevokeException,
                                                                                                                 SearchKeyNotUniqueException,
                                                                                                                 NoSuchRoleException,
                                                                                                                 NoSuchUserException {
        StatusOutVO statusOutVO = new StatusOutVO();
        // BEGIN ---- get Username from ssa id
        Set<String> attrNames = null;
        String username = null;
        int otpFailCounter = 0;
        User user = null;
        attrNames = new HashSet<String>();
        attrNames.add("User Login");
        try {
            UserManager userManager_local = oimClient.getService(UserManager.class);
            try {
                user = userManager_local.getDetails("User Login", notitifcationPreferenceVO.getUsername(), attrNames);
            } catch (Exception ex) {
                try {
                    user = userManager_local.getDetails("Employee Number", notitifcationPreferenceVO.getUsername(), attrNames);
                } catch (Exception exObj) {
                    statusOutVO.setStatus("Error");
                    statusOutVO.setErrorMessage("No user found for username : " + notitifcationPreferenceVO.getUsername());
                    LOG.info(this + "updateNotitifcationPreference","Error: " + exObj.getMessage());                 
                    return statusOutVO;
                }
            }          
                LOG.info(this + "updateNotitifcationPreference", "got the user details");
            if (user != null && user.getAttributes() != null && !user.getAttributes().isEmpty() &&
                user.getAttributes().get("User Login") != null) {
                username = user.getAttributes().get("User Login").toString();
            }
            User users = null;
            HashMap<String, Object> mapAttrs = new HashMap<String, Object>();
            UserManagerResult result = null;
            mapAttrs.put("PreferredMode", notitifcationPreferenceVO.getPreferredModeNotification());
            mapAttrs.put("OptOut", notitifcationPreferenceVO.getOptOutPreferrence());
            users = new User(username, mapAttrs);
            try {
                result = userManager_local.modify("User Login", username, users);
            } catch (Exception ex) {
                statusOutVO.setStatus(result.getStatus());
                statusOutVO.setErrorMessage(ex.getMessage());
                LOG.info(this + "updateNotitifcationPreference","Error: " + ex.getMessage());            
                ex.printStackTrace();
            }
            statusOutVO.setStatus("Success");

        } catch (Exception ex) {
            LOG.info(this +  "updateNotitifcationPreference", "Error: " + ex.getMessage());
            statusOutVO.setStatus("Error");
            statusOutVO.setErrorMessage("Error in updateNotitifcationPreference.");
        } finally {
            //Logout from OIMClient
            if (oimClient != null) {               
                LOG.info(this + "updateNotitifcationPreference", "logging out");             
                oimClient.logout();
                oimClient = null;
            }
        }
        return statusOutVO;
    }
}


