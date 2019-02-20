package za.co.icurity.usermanagement.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import oracle.iam.api.OIMService;
import oracle.iam.identity.usermgmt.api.UserManager;
import oracle.iam.identity.usermgmt.vo.User;
import oracle.iam.platform.OIMClient;

import oracle.iam.platform.utils.vo.OIMType;
import oracle.iam.request.vo.RequestConstants;
import oracle.iam.request.vo.RequestData;
import oracle.iam.request.vo.RequestEntity;
import oracle.iam.vo.OperationResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import za.co.icurity.usermanagement.vo.DisableUserRequestInVO;
import za.co.icurity.usermanagement.vo.StatusOutVO;

public class DisableUserUtil {
    public DisableUserUtil() {
        super();
    }
    private static final Logger LOG = LoggerFactory.getLogger(DisableUserUtil.class);

    public StatusOutVO disableUser(OIMClient oimClient, DisableUserRequestInVO disableUserRequestInVO) {
        Set<String> attrNames = new HashSet<String>();
        StatusOutVO statusOutVO = new StatusOutVO();
        String userId = null;
        UserManager userManager_local = oimClient.getService(UserManager.class);
        try {
            User user = userManager_local.getDetails("User Login", disableUserRequestInVO.getUsername(), attrNames);
            if (user != null && user.getAttributes() != null && !user.getAttributes().isEmpty() &&
                user.getAttributes().get("usr_key") != null) {
                userId = user.getAttributes().get("usr_key").toString();
                LOG.info(this + " disableUser userId "+userId);
            }
        } catch (Exception ex) {
            try {
                User users =
                    userManager_local.getDetails("Employee Number", disableUserRequestInVO.getUsername(), attrNames);
                
                if (users != null && users.getAttributes() != null && !users.getAttributes().isEmpty() &&
                    users.getAttributes().get("usr_key") != null) {
                    userId = users.getAttributes().get("usr_key").toString();
                }
            } catch (Exception exObj) {
                LOG.error(this + " disableUser: Error: " + exObj.getMessage());
                statusOutVO.setStatus("Error");
                statusOutVO.setErrorMessage("No user found for username : " + disableUserRequestInVO.getUsername());
                return statusOutVO;
            }
        }
        if (userId == null || (statusOutVO.getStatus() != null && statusOutVO.getStatus().equalsIgnoreCase("Error"))) {
            LOG.error(this + " disableUser: No user found for username : " + disableUserRequestInVO.getUsername());
            statusOutVO.setStatus("Error");
            statusOutVO.setErrorMessage("No user found for username : " + disableUserRequestInVO.getUsername());
            return statusOutVO;
        } else {

            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            sdf.setLenient(false);
            Date effectiveDate = null;
            try {
                effectiveDate = sdf.parse(disableUserRequestInVO.getEffectiveDate());
                LOG.info(this + " disableUser effectiveDate"+effectiveDate);
            } catch (ParseException ex) {
                LOG.error(this + " disableUser: Error: " + ex.getMessage());
                statusOutVO.setStatus("Error");
                statusOutVO.setErrorMessage("Please enter date in format MM/dd/yyyy");
                return statusOutVO;
            }
            // Call disable user operation
            RequestData requestData = new RequestData();

            RequestEntity requestEntity = new RequestEntity();
            requestEntity.setRequestEntityType(OIMType.User);
            requestEntity.setEntityKey(userId);
            requestEntity.setOperation(RequestConstants.MODEL_DISABLE_OPERATION);
            List<RequestEntity> entities = new ArrayList<RequestEntity>();
            entities.add(requestEntity);

            requestData.setJustification(disableUserRequestInVO.getJustification());
            requestData.setExecutionDate(effectiveDate);
            requestData.setTargetEntities(entities);
            LOG.info(this + " disableUserUtil requestData: " + disableUserRequestInVO.getJustification() +
                     " Effective date " + effectiveDate + " entities " + entities);
            try {
                OIMService oimService_local = oimClient.getService(OIMService.class);
                OperationResult result = oimService_local.doOperation(requestData, OIMService.Intent.ANY);
                LOG.info(this + " disableUser getOperationStatus " + result.getOperationStatus().toString());
                statusOutVO.setStatus(result.getOperationStatus().toString());
                LOG.info(this + " disableUser end");
                return statusOutVO;
            } catch (Exception ex) {
                LOG.error(this + " disableUser", "Error: " + ex.getMessage());
                statusOutVO.setStatus("Error");
                statusOutVO.setErrorMessage("Error while disabling user");
                return statusOutVO;
            } finally {
                //Logout from OIMClient
                if (oimClient != null) {
                    LOG.error(this + " disableUser", " logging out from OIMClient");
                    oimClient.logout();
                    oimClient = null;

                }
            }
        }
    }
}
