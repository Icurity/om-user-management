package za.co.icurity.usermanagement.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import oracle.iam.identity.usermgmt.api.UserManager;
import oracle.iam.identity.usermgmt.vo.User;
import oracle.iam.platform.OIMClient;

import oracle.iam.platform.entitymgr.vo.SearchCriteria;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import za.co.icurity.usermanagement.vo.CheckExistingIDNumberInVO;
import za.co.icurity.usermanagement.vo.CheckExistingIDNumberOutVO;
import za.co.icurity.usermanagement.vo.UserOutVO;

public class CheckExistingIDNumberUtil {
    public CheckExistingIDNumberUtil() {
        super();
    }

    private static final Logger LOG = LoggerFactory.getLogger(CheckExistingIDNumberUtil.class);

    public CheckExistingIDNumberOutVO checkExistingIDNumber(OIMClient oimClient,
                                                            CheckExistingIDNumberInVO checkExistingIDNumberInVO) {
        CheckExistingIDNumberOutVO checkExistingIDNumberOutVO = new CheckExistingIDNumberOutVO();
        Set<String> attrNames = null;
        List<User> users = null;
        HashMap<String, Object> parameters = null;

        if ("RSA-ID".equalsIgnoreCase(checkExistingIDNumberInVO.getIdType())) {
            checkExistingIDNumberInVO.setIdType("RSA ID");
        }

        SearchCriteria criteria =
            new SearchCriteria("id_number", checkExistingIDNumberInVO.getIdNumber(), SearchCriteria.Operator.EQUAL);
        LOG.info(this + " checkExistingIDNumber SearchCriteria " + criteria);
        attrNames = new HashSet<String>();
        attrNames.add("First Name");
        attrNames.add("Last Name");
        attrNames.add("id_number");
        attrNames.add("id_type");
        attrNames.add("Country");
        try {
            UserManager userManager_local = oimClient.getService(UserManager.class);
            users = userManager_local.search(criteria, attrNames, parameters);

            checkExistingIDNumberOutVO.setStatus("Success");
            checkExistingIDNumberOutVO.setIdNumberExists("false");

            for (User user : users) {

                String idNumber = (String)user.getAttribute("id_number");
                String idType = (String)user.getAttribute("id_type");
                // Match to what is sent in from the ESB
                if ("RSA-ID".equalsIgnoreCase(idType)) {
                    LOG.info(this + " checkExistingIDNumber idType is RSA-ID for idNumber " +
                             checkExistingIDNumberInVO.getIdNumber());
                    idType = "RSA ID";
                }
                if ("Passport Number".equalsIgnoreCase(idType)) {
                    LOG.info(this + " checkExistingIDNumber idType is Passport Number for idNumber " +
                             checkExistingIDNumberInVO.getIdNumber());
                    idType = "PASSPORT";
                }
                if ("Non RSA ID".equalsIgnoreCase(idType)) {
                    LOG.info(this + " checkExistingIDNumber idType is Non-RSA-ID for idNumber " +
                             checkExistingIDNumberInVO.getIdNumber());
                    idType = "Non-RSA-ID";
                }

                String countryOfIssue = user.getCountry();

                if (countryOfIssue == null) { // SSA User
                    if (checkExistingIDNumberInVO.getIdNumber().equalsIgnoreCase(idNumber) &&
                        checkExistingIDNumberInVO.getIdType().equalsIgnoreCase(idType)) {
                        checkExistingIDNumberOutVO.setStatus("Success");
                        checkExistingIDNumberOutVO.setIdNumberExists("true");
                        LOG.info(this + " checkExistingIDNumber Success for  id number " +
                                 checkExistingIDNumberInVO.getIdNumber());
                        break;
                    }
                } else {
                    if (checkExistingIDNumberInVO.getIdNumber().equalsIgnoreCase(idNumber) &&
                        checkExistingIDNumberInVO.getIdType().equalsIgnoreCase(idType) &&
                        checkExistingIDNumberInVO.getCountryOfIssue().equalsIgnoreCase(countryOfIssue)) {
                        checkExistingIDNumberOutVO.setStatus("Success");
                        LOG.info(this + " checkExistingIDNumber Success for  id number " +
                                 checkExistingIDNumberInVO.getIdNumber());
                        checkExistingIDNumberOutVO.setIdNumberExists("true");
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            LOG.error(this + " checkExistingIDNumber", "Error: " + ex.getMessage());
            checkExistingIDNumberOutVO.setStatus("Error");
            checkExistingIDNumberOutVO.setErrorMessage("Error: " + ex.getMessage());
            return checkExistingIDNumberOutVO;
        } finally {
            if (oimClient != null) {
                LOG.info(this + " checkExistingIDNumber oimClient Logging out");
                oimClient.logout();
                oimClient = null;
            }
        }
        return checkExistingIDNumberOutVO;
    }
}
