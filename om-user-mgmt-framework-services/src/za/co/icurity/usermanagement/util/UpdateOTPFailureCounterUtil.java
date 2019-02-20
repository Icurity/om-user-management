package za.co.icurity.usermanagement.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import oracle.iam.identity.usermgmt.api.UserManager;
import oracle.iam.identity.usermgmt.vo.User;
import oracle.iam.identity.usermgmt.vo.UserManagerResult;
import oracle.iam.platform.OIMClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import za.co.icurity.usermanagement.vo.OTPFailCounterVO;
import za.co.icurity.usermanagement.vo.OTPOutVO;

public class UpdateOTPFailureCounterUtil {
    public UpdateOTPFailureCounterUtil() {
        super();
    }
    private static final Logger LOG = LoggerFactory.getLogger(UpdateNotitifcationPreferenceUtil.class);

    public OTPOutVO updateOTPFailureCounter(OIMClient oimClient, OTPFailCounterVO otpFailCounterVO) {
        OTPOutVO otpOutVO = new OTPOutVO();

        // BEGIN ---- get Username from ssa id
        Set<String> attrNames = null;
        String username = null;
        int otpFailCounter = 0;
        String otpValidFlag = null;
        User user = null;
        attrNames = new HashSet<String>();
        attrNames.add("OTPFailedCounter"); //TBD the name fail counter
        attrNames.add("User Login");

        try {
            UserManager userManager_local = oimClient.getService(UserManager.class);
            try {
                user = userManager_local.getDetails("User Login", otpFailCounterVO.getUsername(), attrNames);
            } catch (Exception ex) {
                try {
                    user = userManager_local.getDetails("Employee Number", otpFailCounterVO.getUsername(), attrNames);
                } catch (Exception exObj) {
                    otpOutVO.setStatus("Error");
                    otpOutVO.setErrorMessage("No user found for username : " + otpFailCounterVO.getUsername());
                    LOG.info(this + "updateOTPFailCounter", "Error: " + exObj.getMessage());
                    return otpOutVO;
                }
            }
            LOG.info(this + "updateOTPFailCounter", "got the user details");
            if (user != null && user.getAttributes() != null && !user.getAttributes().isEmpty() &&
                user.getAttributes().get("User Login") != null) {
                username = user.getAttributes().get("User Login").toString();
            }
            if (user != null && user.getAttributes() != null && !user.getAttributes().isEmpty() &&
                user.getAttributes().get("OTPFailedCounter") != null) {
                otpFailCounter = Integer.parseInt(user.getAttributes().get("OTPFailedCounter").toString());
            }
            LOG.info(this + "updateOTPFailCounter", "otpFailCounter before update:::" + otpFailCounter);
            otpValidFlag = otpFailCounterVO.getOtpValidFlag();
            LOG.info(this + "updateOTPFailCounter", "otpValidFlag:::" + otpValidFlag);
            if ("false".equalsIgnoreCase(otpValidFlag)) {
                LOG.info(this + "updateOTPFailCounter", "Entered to IF");
                if (otpFailCounter == 0) {
                    otpFailCounter = 1;
                } else {
                    LOG.info(this + "updateOTPFailCounter", "Entered to else");
                    otpFailCounter = otpFailCounter + 1;
                }
            } else {
                otpFailCounter = 0;
            }
            LOG.info(this + "updateOTPFailCounter", "otpFailCounter after increament:::" + otpFailCounter);
            User users = null;
            HashMap<String, Object> mapAttrs = new HashMap<String, Object>();
            UserManagerResult result = null;
            mapAttrs.put("OTPFailedCounter", new Long(otpFailCounter));
            users = new User(username, mapAttrs);
            try {
                result = userManager_local.modify("User Login", username, users);
            } catch (Exception ex) {
                LOG.info(this + "updateOTPFailCounter", "Error: " + ex.getMessage());
                ex.printStackTrace();
            }
            otpOutVO.setOtpFailCounter(otpFailCounter);
            otpOutVO.setStatus("Success");
            return otpOutVO;
        } catch (Exception ex) {
            LOG.info(this + "updateOTPFailCounter", "Error: " + ex.getMessage());
            otpOutVO.setStatus("Error");
            otpOutVO.setErrorMessage("Error in updateOTPFailCounter.");
        } finally {
            //Logout from OIMClient
            if (oimClient != null) {
                LOG.info(this + "updateOTPFailCounter", "logging out");
                oimClient.logout();
                oimClient = null;
            }
        }
        LOG.info(this + "updateOTPFailCounter", "__updateOTPFailCounter End__");
        return otpOutVO;
    }
}
