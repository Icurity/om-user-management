package za.co.icurity.usermanagement.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import oracle.iam.identity.usermgmt.api.UserManager;
import oracle.iam.identity.usermgmt.vo.User;
import oracle.iam.platform.OIMClient;
import oracle.iam.platform.entitymgr.vo.SearchCriteria;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import za.co.icurity.usermanagement.vo.CheckExistingUserInVO;
import za.co.icurity.usermanagement.vo.FetchUsernameInVO;
import za.co.icurity.usermanagement.vo.FetchUsernameOutVO;
import za.co.icurity.usermanagement.vo.UserOutVO;


public class CheckUserExistUtil {
    public CheckUserExistUtil() {
        super();
    }
    private static final Logger LOG = LoggerFactory.getLogger(CheckUserExistUtil.class);

    public boolean checkExistingEmployeeNumber(OIMClient oimClient, String employeeNumber) {

        List<User> users = null;
        HashMap<String, Object> parameters = null;
        Set<String> attrNames = null;
        SearchCriteria criteria = new SearchCriteria("Employee Number", employeeNumber, SearchCriteria.Operator.EQUAL);
        attrNames = new HashSet<String>();
        attrNames.add("Last Name");
        attrNames.add("User Login");
        try {
            UserManager userManager_local = oimClient.getService(UserManager.class);
            users = userManager_local.search(criteria, attrNames, parameters);
            if (users != null && !users.isEmpty() && users.get(0) != null && users.get(0).getLastName() != null) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            LOG.error(this + " checkExistingEmployeeNumber Search error ");
        }
        return false;
    }

    public boolean checkExistingUserName(OIMClient oimClient, String username) {

        List<User> users = null;
        HashMap<String, Object> parameters = null;
        Set<String> attrNames = null;
        SearchCriteria criteria = new SearchCriteria("User Login", username, SearchCriteria.Operator.EQUAL);
        attrNames = new HashSet<String>();
        attrNames.add("Last Name");
        attrNames.add("User Login");
        try {
            UserManager userManager_local = oimClient.getService(UserManager.class);
            users = userManager_local.search(criteria, attrNames, parameters);
            if (users != null && !users.isEmpty() && users.get(0) != null && users.get(0).getLastName() != null) {
                return true;
            } else {
                criteria = new SearchCriteria("Employee Number", username, SearchCriteria.Operator.EQUAL);
                users = userManager_local.search(criteria, attrNames, parameters);
                if (users != null && !users.isEmpty() && users.get(0) != null && users.get(0).getLastName() != null) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception ex) {
            LOG.error(this + " checkExistingUserName Search error "+ex.getMessage());
        }
        return false;
    }

    public boolean checkExistingUser(OIMClient oimClient, CheckExistingUserInVO checkExistingUserInVO) {

        Set<String> attrNames = null;
        List<User> users = null;
        HashMap<String, Object> parameters = null;
        SearchCriteria criteriaFirstName = new SearchCriteria("First Name", checkExistingUserInVO.getFirstName(), SearchCriteria.Operator.EQUAL);
        SearchCriteria criteriaLastName = new SearchCriteria("Last Name", checkExistingUserInVO.getLastName(), SearchCriteria.Operator.EQUAL);
        SearchCriteria criteriaDateOfBirth = new SearchCriteria("birth_date", checkExistingUserInVO.getDateOfBirth(), SearchCriteria.Operator.EQUAL);
        SearchCriteria criteriaIDNumber = new SearchCriteria("id_number", checkExistingUserInVO.getIdNumber(), SearchCriteria.Operator.EQUAL);
        SearchCriteria criteriaFirstAndLast = new SearchCriteria(criteriaFirstName, criteriaLastName, SearchCriteria.Operator.AND);
        SearchCriteria criteriaDOBAndID = new SearchCriteria(criteriaDateOfBirth, criteriaIDNumber, SearchCriteria.Operator.AND);
        SearchCriteria criteria = new SearchCriteria(criteriaFirstAndLast, criteriaDOBAndID, SearchCriteria.Operator.AND);
        attrNames = new HashSet<String>();
        attrNames.add("First Name");
        attrNames.add("Last Name");
        attrNames.add("birth_date");
        attrNames.add("id_number");
        SearchCriteria searchCriteria = new SearchCriteria(criteria, attrNames, SearchCriteria.Operator.EQUAL);
        LOG.info(this + " checkExistingUser searchCriteria " + searchCriteria);
        try {
            UserManager userManager_local = oimClient.getService(UserManager.class);
            users = userManager_local.search(criteria, attrNames, parameters);
            if (users != null && !users.isEmpty() && users.get(0) != null && users.get(0).getLastName() != null) {
                LOG.info(this + " checkExistingUser : True for the Firstname " + checkExistingUserInVO.getFirstName());
                return true;
            }
        } catch (Exception ex) {
            LOG.error(this + " checkExistingUser Search error " + ex.getMessage());
        }
        return false;
    }

    public FetchUsernameOutVO checkUsername(OIMClient oimClient, FetchUsernameInVO fetchUsernameInVO) {

        Set<String> attrNames = null;
        List<User> users = null;
        HashMap<String, Object> parameters = null;
        FetchUsernameOutVO fetchUsernameOutVO = new FetchUsernameOutVO();
        SearchCriteria criteria1 = new SearchCriteria("Telephone Number", fetchUsernameInVO.getCellphone(), SearchCriteria.Operator.EQUAL);
        SearchCriteria criteria2 = new SearchCriteria("Last Name", fetchUsernameInVO.getLastName(), SearchCriteria.Operator.EQUAL);
        SearchCriteria criteria = new SearchCriteria(criteria1, criteria2, SearchCriteria.Operator.AND);
        attrNames = new HashSet<String>();
        attrNames.add("User Login");

        try {
            UserManager userManager_local = oimClient.getService(UserManager.class);
            users = userManager_local.search(criteria, attrNames, parameters);
        } catch (Exception ex) {
            LOG.error(this + " checkExistingUser Search error " + ex.getMessage());
            fetchUsernameOutVO.setStatus("Error");
            fetchUsernameOutVO.setErrorMessage(ex.getMessage());
            return fetchUsernameOutVO;
        }
        if (users != null && !users.isEmpty()) {
            if (users.size() == 1 && users.get(0).getAttribute("User Login") != null) {
                fetchUsernameOutVO.setUsername(users.get(0).getAttribute("User Login").toString());
                fetchUsernameOutVO.setStatus("Success");
            } else {
                
                fetchUsernameOutVO.setErrorMessage("More than one user found with Last Name: " + fetchUsernameInVO.getLastName() + " and Cellphone Number: " + fetchUsernameInVO.getCellphone());

            }
        } else {
            fetchUsernameOutVO.setErrorMessage("No user found with Last Name: " + fetchUsernameInVO.getLastName() + " and Cellphone Number: " + fetchUsernameInVO.getCellphone());

        }
        return fetchUsernameOutVO;
    }

    public UserOutVO getUserId(OIMClient oimClient, String username) {

        String userId = "";
        HashMap<String, Object> parameters = null;
        UserOutVO userOutVO = new UserOutVO();
        Set<String> attrNames = new HashSet<String>();
        attrNames.add("usr_key");
        UserManager userManager_local = oimClient.getService(UserManager.class);
        try {           
            User users = userManager_local.getDetails("User Login", username, attrNames);
            userId = users.getAttributes().get("usr_key").toString();
            userOutVO.setStatus("Success");
            userOutVO.setUserId(userId);
        } catch (Exception ex) {
            try {
                User users = userManager_local.getDetails("Employee Number", username, attrNames);
                userId = users.getAttributes().get("usr_key").toString();
                userOutVO.setStatus("Success");
                userOutVO.setUserId(userId);
            } catch (Exception exObj) {
                LOG.error(this + " getUserId Error: "+exObj.getMessage());
                userOutVO.setStatus("Error");
                userOutVO.setErrorMessage("No user found for username : " + username);
            }
        } finally {
            //Logout from OIMClient
            if (oimClient != null) {
                LOG.info(this + " getUserId", "logging out");
                oimClient.logout();
                oimClient = null;
            }
        }
        return userOutVO;
    }
}
