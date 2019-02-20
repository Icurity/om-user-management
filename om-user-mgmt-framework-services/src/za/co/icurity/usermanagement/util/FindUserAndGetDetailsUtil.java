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

import za.co.icurity.usermanagement.vo.CheckExistingUserInVO;
import za.co.icurity.usermanagement.vo.FindUserAndUserDetailsOutVO;

public class FindUserAndGetDetailsUtil {
    public FindUserAndGetDetailsUtil() {
        super();
    }
    private static final Logger LOG = LoggerFactory.getLogger(FindUserAndGetDetailsUtil.class);

    public FindUserAndUserDetailsOutVO findUserAndGetDetails(OIMClient oimClient,
                                                             CheckExistingUserInVO checkExistingUserInVO) {
        FindUserAndUserDetailsOutVO userDetailsVO = new FindUserAndUserDetailsOutVO();
        List<User> users = null;
        User user = null;
        HashMap<String, Object> parameters = null;
        Set<String> attrNames = null;

        SearchCriteria criteriaFirstName =
            new SearchCriteria("First Name", checkExistingUserInVO.getFirstName(), SearchCriteria.Operator.EQUAL);
        SearchCriteria criteriaLastName =
            new SearchCriteria("Last Name", checkExistingUserInVO.getLastName(), SearchCriteria.Operator.EQUAL);
        SearchCriteria criteriaDateOfBirth =
            new SearchCriteria("birth_date", checkExistingUserInVO.getDateOfBirth(), SearchCriteria.Operator.EQUAL);
        SearchCriteria criteriaIDNumber =
            new SearchCriteria("id_number", checkExistingUserInVO.getIdNumber(), SearchCriteria.Operator.EQUAL);
        SearchCriteria criteriaFirstAndLast =
            new SearchCriteria(criteriaFirstName, criteriaLastName, SearchCriteria.Operator.AND);
        SearchCriteria criteriaDOBAndID =
            new SearchCriteria(criteriaDateOfBirth, criteriaIDNumber, SearchCriteria.Operator.AND);
        SearchCriteria criteria =
            new SearchCriteria(criteriaFirstAndLast, criteriaDOBAndID, SearchCriteria.Operator.AND);
        LOG.info(this + " findUserAndGetDetails SearchCriteria " + criteria);

        attrNames = new HashSet<String>();
        attrNames.add("First Name");
        attrNames.add("Last Name");
        attrNames.add("Telephone Number");
        attrNames.add("birth_date");
        attrNames.add("id_number");
        attrNames.add("migr_firstLogin");
        attrNames.add("usr_locked");
        attrNames.add("id_type");
        attrNames.add("Employee Number");
        try {
            UserManager userManager_local = oimClient.getService(UserManager.class);
            users = userManager_local.search(criteria, attrNames, parameters);
            LOG.info(this + " findUserAndGetDetails users " + users);
            if (users != null && !users.isEmpty() && users.get(0) != null && users.get(0).getFirstName() != null) {
                userDetailsVO.setStatus("Success");
                userDetailsVO.setUserExists("true");
                user = users.get(0);
            } else {
                userDetailsVO.setStatus("Success");
                userDetailsVO.setUserExists("false");
            }
        } catch (Exception ex) {
            LOG.error(this + " findUserAndGetDetails", "Error: " + ex.getMessage());
            userDetailsVO.setStatus("Error");
            userDetailsVO.setErrorMessage("Error: " + ex.getMessage());
            return userDetailsVO;
        }

        try {

            if (user != null) {
                LOG.info(this + " findUserAndGetDetails user " + user);
                userDetailsVO.setFirstName(user.getFirstName());
                userDetailsVO.setLastName(user.getLastName());
                if (user.getAttribute("Telephone Number") != null) {
                    userDetailsVO.setCellPhoneNumber(user.getAttribute("Telephone Number").toString());
                }
                if (user.getAttribute("birth_date") != null) {

                    userDetailsVO.setDateOfBirth(user.getAttribute("birth_date").toString());
                }
                if (user.getAttribute("id_number") != null) {
                    userDetailsVO.setIdNumber(user.getAttribute("id_number").toString());
                }
                if (user.getAttribute("id_type") != null) {
                    userDetailsVO.setIdType(user.getAttribute("id_type").toString());
                }
                if (user.getAttribute("Employee Number") != null) {
                    userDetailsVO.setEmployeeNumber(user.getAttribute("Employee Number").toString());
                }
                if (user.getAttribute("migr_firstLogin") != null) {
                    userDetailsVO.setMigratedUserFirstLogin(user.getAttribute("migr_firstLogin").toString());
                } else {
                    userDetailsVO.setMigratedUserFirstLogin("false");
                }
                if (user.getAccountStatus() != null) {
                    if (user.getAccountStatus().equals("0")) {
                        userDetailsVO.setUserAccountLocked("false");
                    } else if (user.getAccountStatus().equals("1")) {
                        userDetailsVO.setUserAccountLocked("true");
                    }
                }
                userDetailsVO.setStatus("Success");

            }
        } catch (Exception ex) {
            LOG.error(this + " findUserAndGetDetails", "Error: " + ex.getMessage());
            userDetailsVO.setStatus("Error");
            userDetailsVO.setUserExists("No user found with specified details. Please provide valid input.");
            return userDetailsVO;
        } finally {
            //Logout from OIMClient
            if (oimClient != null) {
                LOG.info(this + " checkExistingIDNumber oimClient Logging out");
                oimClient.logout();
                oimClient = null;
            }
        }
        return userDetailsVO;
    }
}
