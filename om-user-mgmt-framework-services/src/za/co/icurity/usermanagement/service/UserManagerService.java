package za.co.icurity.usermanagement.service;

import java.util.ArrayList;
import java.util.Enumeration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchResult;

import javax.security.auth.login.LoginException;

import oracle.iam.identity.rolemgmt.vo.Role;
import oracle.iam.identity.usermgmt.api.UserManager;
import oracle.iam.identity.usermgmt.vo.User;
import oracle.iam.platform.OIMClient;

import oracle.iam.platform.entitymgr.vo.SearchCriteria;

import za.co.icurity.usermanagement.proxy.OIMLoginProxy;
import za.co.icurity.usermanagement.proxy.OVDLoginProxy;
import za.co.icurity.usermanagement.util.ADUsernameAttributes;
import za.co.icurity.usermanagement.util.AssignRoleUtil;
import za.co.icurity.usermanagement.util.AssignRolesUtil;
import za.co.icurity.usermanagement.util.AttributesUtil;
import za.co.icurity.usermanagement.util.CellNumberExistsUtil;
import za.co.icurity.usermanagement.util.ChangePasswordForLoggedInUserUtil;
import za.co.icurity.usermanagement.util.ChangePasswordUtil;
import za.co.icurity.usermanagement.util.CheckExistingIDNumberUtil;
import za.co.icurity.usermanagement.util.CheckUserExistUtil;
import za.co.icurity.usermanagement.util.CreateUserUtil;
import za.co.icurity.usermanagement.util.DelegatedAuthAdminRoleUtil;
import za.co.icurity.usermanagement.util.DisableUserUtil;
import za.co.icurity.usermanagement.util.FetchUserRolesUtil;
import za.co.icurity.usermanagement.util.FindUserAndGetDetailsUtil;
import za.co.icurity.usermanagement.util.GenerateTempPasswordUtil;
import za.co.icurity.usermanagement.util.LockUserUtil;
import za.co.icurity.usermanagement.util.PropertiesUtil;
import za.co.icurity.usermanagement.util.ProvisionUserUtil;
import za.co.icurity.usermanagement.util.RevokeRoleUtil;
import za.co.icurity.usermanagement.util.RevokeRolesUtil;
import za.co.icurity.usermanagement.util.RoleDetailsUtil;
import za.co.icurity.usermanagement.util.StringUtil;
import za.co.icurity.usermanagement.util.TransferrableSiteRolesOfUserUtil;
import za.co.icurity.usermanagement.util.UnlockAccountUtil;
import za.co.icurity.usermanagement.util.UpdateNotitifcationPreferenceUtil;
import za.co.icurity.usermanagement.util.UpdateOTPFailureCounterUtil;
import za.co.icurity.usermanagement.util.UpdateUserUtil;
import za.co.icurity.usermanagement.util.UserPreferenceDetailsVOUtil;
import za.co.icurity.usermanagement.util.UserStatusUtil;
import za.co.icurity.usermanagement.util.ValidateADSecurityAnswersUtil;
import za.co.icurity.usermanagement.util.ValidateInVOUtil;
import za.co.icurity.usermanagement.util.ValidatePasswordUtil;
import za.co.icurity.usermanagement.vo.ADPropertiesVO;
import za.co.icurity.usermanagement.vo.ADSecurityAnswersOutVO;
import za.co.icurity.usermanagement.vo.AdminRoleListVO;
import za.co.icurity.usermanagement.vo.ChangePasswordForLoggedInUserVO;
import za.co.icurity.usermanagement.vo.ChangePasswordVO;
import za.co.icurity.usermanagement.vo.CheckExistingCellphoneOutVO;
import za.co.icurity.usermanagement.vo.CheckExistingEmployeeNoOutVO;
import za.co.icurity.usermanagement.vo.CheckExistingIDNumberInVO;
import za.co.icurity.usermanagement.vo.CheckExistingIDNumberOutVO;
import za.co.icurity.usermanagement.vo.CheckExistingSSAIDOutVO;
import za.co.icurity.usermanagement.vo.CheckExistingUserInVO;
import za.co.icurity.usermanagement.vo.CheckExistingUserOutVO;
import za.co.icurity.usermanagement.vo.CheckExistingUsernameOutVO;
import za.co.icurity.usermanagement.vo.DisableUserRequestInVO;
import za.co.icurity.usermanagement.vo.FetchUserRolesInVO;
import za.co.icurity.usermanagement.vo.FetchUsernameInVO;
import za.co.icurity.usermanagement.vo.FetchUsernameOutVO;
import za.co.icurity.usermanagement.vo.FindUserAndUserDetailsOutVO;
import za.co.icurity.usermanagement.vo.GenerateTempPasswordInVO;
import za.co.icurity.usermanagement.vo.GenerateTempPasswordOutVO;
import za.co.icurity.usermanagement.vo.GetADUsernameInVO;
import za.co.icurity.usermanagement.vo.GetADUsernameOutVO;
import za.co.icurity.usermanagement.vo.GetUserAccountLockedOutVO;
import za.co.icurity.usermanagement.vo.NotitifcationPreferenceVO;
import za.co.icurity.usermanagement.vo.OTPFailCounterVO;
import za.co.icurity.usermanagement.vo.OTPOutVO;
import za.co.icurity.usermanagement.vo.ProvisionUserAccountVO;
import za.co.icurity.usermanagement.vo.RoleDetailsVO;
import za.co.icurity.usermanagement.vo.RoleListVO;
import za.co.icurity.usermanagement.vo.RoleVO;
import za.co.icurity.usermanagement.vo.StatusOutVO;
import za.co.icurity.usermanagement.vo.UpdateUserInVO;
import za.co.icurity.usermanagement.vo.UserDetailsVO;
import za.co.icurity.usermanagement.vo.UserInVO;
import za.co.icurity.usermanagement.vo.UserOutVO;
import za.co.icurity.usermanagement.vo.UserPreferenceDetailsVO;
import za.co.icurity.usermanagement.vo.UserRoleGrantVO;
import za.co.icurity.usermanagement.vo.UserRoleListAssignVO;
import za.co.icurity.usermanagement.vo.UserStatusVO;
import za.co.icurity.usermanagement.vo.UsernameVO;
import za.co.icurity.usermanagement.vo.ValidatePasswordVO;


public class UserManagerService {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(UserManagerService.class);
    static final String searchBase = "cn=users";
    static final StringUtil stringUtil = null;

    /**
     * Gets the status of the user
     *
     * @param userInVO
     * @return password change status and user first login status
     * @throws Exception
     */
    public UserStatusVO userstatus(String username) throws Exception {

        UserStatusVO userStatusVO = new UserStatusVO();
        userStatusVO.setErrorMessage(null);
        userStatusVO.setStatus(null);
        userStatusVO.setMigratedUserFirstLogin(null);
        userStatusVO.setChangePassword(null);
        OVDLoginProxy ovdLoginProxy = new OVDLoginProxy();
        if (username == null || username.isEmpty()) {
            userStatusVO.setStatus("Error");
            userStatusVO.setErrorMessage("Mandatory field : Username");
            LOG.error(" userstatus: Mandatory field : Username " + username);
            return userStatusVO;
        }
        DirContext dirContext;
        try {
            dirContext = ovdLoginProxy.connect();
            LOG.info(this + " Entering into userstatus dirContext" + dirContext);
            System.out.println("dirContext " + dirContext);
        } catch (NamingException ne) {
            userStatusVO.setStatus("Error");
            userStatusVO.setErrorMessage("System unavailable at the moment, please try again");
            LOG.error(this + " userstatus: Failed login to OVD (dirContext is null)  for the user" + username);
            ne.printStackTrace();
            return userStatusVO;
        } catch (Exception e) {
            userStatusVO.setStatus("Error");
            userStatusVO.setErrorMessage("System unavailable at the moment, please try again");
            LOG.error(this + " userstatus: Failed login to OVD (dirContext is null)  for the user" + username);
            return userStatusVO;
        }

        if (dirContext == null) {
            userStatusVO.setStatus("Error");
            userStatusVO.setErrorMessage("System unavailable at the moment, please try again");
            LOG.error(this + " userstatus: Failed login to OVD (dirContext is null)  for the user " + username);
            return userStatusVO;
        }
        UserStatusUtil userStatusUtil = new UserStatusUtil();
        try {
            return userStatusUtil.userStatus(dirContext, username);
        } catch (Exception ex) {
            return userStatusVO;
        } finally {
            if (dirContext != null) {
                dirContext.close();
            }
        }
    }

    public UserOutVO createUser(UserInVO userInVO) throws Exception {
        UserOutVO userOutVO = new UserOutVO();
        ValidateInVOUtil validateInVOUtil = new ValidateInVOUtil();
        CreateUserUtil createUserUtil = new CreateUserUtil();
        OIMClient oimClient = null;
        userOutVO.setUserId(null);
        userOutVO.setErrorMessage(null);
        userOutVO.setUserId(null);
        userOutVO.setErrorCode(null);
        String ssaid = null;
        userOutVO = validateInVOUtil.validateUserInVO(userInVO);
        // validate input fields - returns userOutVO with status
        if (userOutVO.getStatus().equalsIgnoreCase("Error")) {
            LOG.error(this + " createUser " + userOutVO.getErrorMessage());
            userOutVO.setStatus("Error");
            userOutVO.setErrorMessage(userOutVO.getErrorMessage());
            return userOutVO;
        }

        OIMLoginProxy oimLoginProxy = new OIMLoginProxy();
        oimClient = oimLoginProxy.userLogin();
        if (oimClient == null) {
            userOutVO.setStatus("Error");
            userOutVO.setErrorMessage("System unavailable at the moment, please try again");
            return userOutVO;
        }

        CheckUserExistUtil checkUserExistUtil = new CheckUserExistUtil();
        while (true) {
            ssaid = createUserUtil.getSSAID();
            if (!checkUserExistUtil.checkExistingEmployeeNumber(oimClient, ssaid)) { // confirm with Mocx
                userInVO.setSsaId(ssaid);
                break;
            }
        }
        // create user

        userOutVO = createUserUtil.createUser(oimClient, userInVO);
        if (userOutVO.getStatus().equalsIgnoreCase("Error")) {
            userOutVO.setStatus("Error");
            userOutVO.setErrorMessage(userOutVO.getErrorMessage());
            return userOutVO;
        }
        try {
            // provision user
            ProvisionUserAccountVO provisionUserAccountVO = new ProvisionUserAccountVO();
            if (userOutVO.getUserId() != null) {
                provisionUserAccountVO.setEmployeeNumber(ssaid);
                provisionUserAccountVO.setUserKey(userOutVO.getUserId());
                provisionUserAccountVO.setUsername(userInVO.getUsername());
                StatusOutVO statusOutVO = createUserUtil.provisionUser(oimClient, provisionUserAccountVO);
                if (statusOutVO.getStatus().equalsIgnoreCase("Error")) {
                    userOutVO.setStatus("Error");
                    userOutVO.setErrorMessage(userOutVO.getErrorMessage());
                    return userOutVO;
                } else {
                   // userOutVO.setStatus("Success");
                    userOutVO.setStatus("COMPLETED");
                    //userOutVO.setErrorMessage("User " + userInVO.getUsername() + " successfully created ");
                    return userOutVO;
                }
            } else {
                userOutVO.setStatus("Error");
                userOutVO.setErrorMessage(userOutVO.getErrorMessage());
                return userOutVO;
            }
        } catch (Exception e) {
            userOutVO.setStatus("Error");
            userOutVO.setErrorMessage(userOutVO.getErrorMessage());
            return userOutVO;
        } finally {
            if (oimClient != null) {
                oimClient.logout();
                oimClient = null;
            }
        }
    }

    public RoleListVO fetchUserRoles(String username) throws Exception {
        DirContext dirContext = null;
        OVDLoginProxy ovdLoginProxy = new OVDLoginProxy();
        RoleListVO roleListVO;
        roleListVO = new RoleListVO();
        roleListVO.setErrorMessage(null);
        roleListVO.setStatus(null);
        roleListVO.setRoles(null);
        LOG.info(this + " Entering into fetchUserRoles " + username);
        if (username == null || username.isEmpty()) {
            roleListVO.setStatus("Error");
            roleListVO.setErrorMessage("Mandatory field : Username");
            LOG.error(this + " fetchUserRoles: Mandatory field : Username " + username);
            return roleListVO;
        }

        try {
            dirContext = ovdLoginProxy.connect();
            LOG.info(this + " fetchUserRoles: dirContext " + dirContext);
            if (dirContext == null) {
                for (int i = 0; i < 3; i++) {
                    Thread.sleep(1000);
                    dirContext = ovdLoginProxy.connect();
                    if (dirContext != null) {
                        break;
                    }
                }
            }
        } catch (NamingException ne) {
            roleListVO.setStatus("Error");
            roleListVO.setErrorMessage("System unavailable at the moment, please try again");
            LOG.error(this + " fetchUserRoles: Failed login to OVD (dirContext is null)  for the user" + username);
            ne.printStackTrace();
            return roleListVO;
        } catch (Exception e) {
            roleListVO.setStatus("Error");
            roleListVO.setErrorMessage("System unavailable at the moment, please try again");
            LOG.error(this + " fetchUserRoles: Failed login to OVD (dirContext is null)  for the user" + username);
            return roleListVO;
        }

        if (dirContext == null) {
            roleListVO.setStatus("Error");
            roleListVO.setErrorMessage("System unavailable at the moment, please try again");
            LOG.error(this + " fetchUserRoles: Failed login to OVD (dirContext is null)  for the user" + username);
            return roleListVO;
        }
        FetchUserRolesUtil fetchUserRolesUtil = new FetchUserRolesUtil();
        try {
            return fetchUserRolesUtil.fetchUserRoles(dirContext, username);
        } catch (Exception ex) {
            return roleListVO;
        } finally {
            if (dirContext != null) {
                dirContext.close();
            }
        }
    }

    public CheckExistingUsernameOutVO checkExistingUserName(String username) {
        OIMClient oimClient = null;
        OIMLoginProxy oimLoginProxy = new OIMLoginProxy();
        CheckExistingUsernameOutVO checkExistingUsernameOutVO = new CheckExistingUsernameOutVO();
        checkExistingUsernameOutVO.setErrorMessage(null);
        checkExistingUsernameOutVO.setStatus(null);
        checkExistingUsernameOutVO.setUsernameExists(null);

        if (username == null || username.isEmpty()) {
            checkExistingUsernameOutVO.setStatus("Error");
            checkExistingUsernameOutVO.setErrorMessage("Mandatory field : Username");
            return checkExistingUsernameOutVO;
        }
        try {
            oimClient = oimLoginProxy.userLogin();
            if (oimClient == null) {
                LOG.error(this + " checkExistingUserName: Failed  to OIMCLient login");
                checkExistingUsernameOutVO.setStatus("Error");
                checkExistingUsernameOutVO.setErrorMessage("System unavailable at the moment, please try again");
                return checkExistingUsernameOutVO;
            }
            LOG.info(this + " checkExistingUserName Logged into OIMClient  ");
            CheckUserExistUtil checkUserExistUtil = new CheckUserExistUtil();
            if (checkUserExistUtil.checkExistingUserName(oimClient, username)) {
                checkExistingUsernameOutVO.setStatus("Success");
                checkExistingUsernameOutVO.setUsernameExists("true");
                return checkExistingUsernameOutVO;
            } else {
                LOG.error(this + " checkExistingUserName: No user found for username: " + username);
                checkExistingUsernameOutVO.setStatus("Error");
                checkExistingUsernameOutVO.setErrorMessage("No user found for username: " + username);
                //checkExistingUsernameOutVO.setUsernameExists("false");
                return checkExistingUsernameOutVO;
            }
        } catch (Exception e) {
            LOG.error(this + " Error on checkExistingUserName " + e.getMessage());
            checkExistingUsernameOutVO.setStatus("Error");
            checkExistingUsernameOutVO.setErrorMessage("System unavailable at the moment, please try again");
            return checkExistingUsernameOutVO;
        } finally {
            if (oimClient != null) {
                oimClient.logout();
                oimClient = null;
            }
        }
    }

    public CheckExistingEmployeeNoOutVO checkExistingEmployeeNumber(String employeeNumber) {
        OIMClient oimClient = null;
        OIMLoginProxy oimLoginProxy = new OIMLoginProxy();
        CheckExistingEmployeeNoOutVO checkExistingEmployeeNoOutVO = new CheckExistingEmployeeNoOutVO();
        checkExistingEmployeeNoOutVO.setErrorMessage(null);
        checkExistingEmployeeNoOutVO.setStatus(null);
        checkExistingEmployeeNoOutVO.setEmployeeNoExists(null);

        if (employeeNumber == null || employeeNumber.isEmpty()) {
            checkExistingEmployeeNoOutVO.setStatus("Error");
            checkExistingEmployeeNoOutVO.setErrorMessage("Mandatory field : employeeNumber");
            return checkExistingEmployeeNoOutVO;
        }
        try {
            oimClient = oimLoginProxy.userLogin();
            if (oimClient == null) {
                LOG.error(this + " checkExistingEmployeeNumber: Failed  to OIMCLient login");
                checkExistingEmployeeNoOutVO.setStatus("Error");
                checkExistingEmployeeNoOutVO.setErrorMessage("System unavailable at the moment, please try again");
                return checkExistingEmployeeNoOutVO;
            }
            LOG.info(this + " checkExistingEmployeeNumber Logged into OIMClient  ");
            CheckUserExistUtil checkUserExistUtil = new CheckUserExistUtil();
            if (checkUserExistUtil.checkExistingEmployeeNumber(oimClient, employeeNumber)) {
                checkExistingEmployeeNoOutVO.setStatus("Success");
                checkExistingEmployeeNoOutVO.setEmployeeNoExists("true");
                return checkExistingEmployeeNoOutVO;
            } else {
                LOG.error(this + " checkExistingEmployeeNumber: No user found for username: " + employeeNumber);
                //checkExistingEmployeeNoOutVO.setStatus("Error");
                checkExistingEmployeeNoOutVO.setStatus("Success"); // added by prakash
                checkExistingEmployeeNoOutVO.setEmployeeNoExists("false");
                return checkExistingEmployeeNoOutVO;
            }
        } catch (Exception e) {
            LOG.error(this + " Error on checkExistingEmployeeNumber " + e.getMessage());
            checkExistingEmployeeNoOutVO.setStatus("Error");
            checkExistingEmployeeNoOutVO.setErrorMessage("System unavailable at the moment, please try again");
            return checkExistingEmployeeNoOutVO;
        } finally {
            if (oimClient != null) {
                oimClient.logout();
                oimClient = null;
            }
        }
    }

    public CheckExistingIDNumberOutVO checkExistingIDNumber(CheckExistingIDNumberInVO checkExistingIDNumberInVO) {
        CheckExistingIDNumberOutVO checkExistingIDNumberOutVO = new CheckExistingIDNumberOutVO();
        CheckExistingIDNumberUtil checkExistingIDNumberUtil = new CheckExistingIDNumberUtil();
        ValidateInVOUtil validateInVOUtil = new ValidateInVOUtil();
        OIMClient oimClient = null;
        OIMLoginProxy oimLoginProxy = new OIMLoginProxy();
        oimClient = oimLoginProxy.userLogin();


        checkExistingIDNumberOutVO = validateInVOUtil.validateCheckExistingIDNumberInVO(checkExistingIDNumberInVO);
        // validate input fields - returns userOutVO with status
        if (checkExistingIDNumberOutVO.getStatus().equalsIgnoreCase("Error")) {
            LOG.error(this + " checkExistingIDNumber " + checkExistingIDNumberOutVO.getErrorMessage());
            checkExistingIDNumberOutVO.setStatus("Error");
            checkExistingIDNumberOutVO.setErrorMessage(checkExistingIDNumberOutVO.getErrorMessage());
            return checkExistingIDNumberOutVO;
        }

        if (oimClient == null) {
            checkExistingIDNumberOutVO.setStatus("Error");
            checkExistingIDNumberOutVO.setErrorMessage("System unavailable at the moment, please try again");
            LOG.error(this + " Error on checkExistingIDNumber oimClient login for id number " +
                      checkExistingIDNumberInVO.getIdNumber());
            return checkExistingIDNumberOutVO;
        }
        try {
            return checkExistingIDNumberUtil.checkExistingIDNumber(oimClient, checkExistingIDNumberInVO);
        } catch (Exception ex) {
            return checkExistingIDNumberOutVO;
        } finally {
            if (oimClient != null) {
                oimClient.logout();
                oimClient = null;
            }
            LOG.info(this + " checkExistingIDNumber End for idNumber " + checkExistingIDNumberInVO.getIdNumber());
        }

    }

    public FindUserAndUserDetailsOutVO findUserAndGetDetails(CheckExistingUserInVO checkExistingUserInVO) {

        FindUserAndUserDetailsOutVO findUserAndUserDetailsOutVO = new FindUserAndUserDetailsOutVO();
        ValidateInVOUtil validateInVOUtil = new ValidateInVOUtil();
        OIMClient oimClient = null;
        OIMLoginProxy oimLoginProxy = new OIMLoginProxy();
        oimClient = oimLoginProxy.userLogin();

        findUserAndUserDetailsOutVO =
                validateInVOUtil.validateCheckExistingUserAndUserDetailsInVO(checkExistingUserInVO);

        // validate input fields - returns userOutVO with status
        if (findUserAndUserDetailsOutVO.getStatus().equalsIgnoreCase("Error")) {
            LOG.error(this + " findUserAndGetDetails " + findUserAndUserDetailsOutVO.getErrorMessage());
            findUserAndUserDetailsOutVO.setStatus("Error");
            findUserAndUserDetailsOutVO.setErrorMessage(findUserAndUserDetailsOutVO.getErrorMessage());
            return findUserAndUserDetailsOutVO;
        }

        if (oimClient == null) {
            findUserAndUserDetailsOutVO.setStatus("Error");
            findUserAndUserDetailsOutVO.setErrorMessage("System unavailable at the moment, please try again");
            LOG.error(this + " Error on findUserAndGetDetails oimClient login for id number " +
                      findUserAndUserDetailsOutVO.getIdNumber());
            return findUserAndUserDetailsOutVO;
        }

        FindUserAndGetDetailsUtil findUserAndGetDetailsUtil = new FindUserAndGetDetailsUtil();
        try {
            return findUserAndGetDetailsUtil.findUserAndGetDetails(oimClient, checkExistingUserInVO);
        } catch (Exception ex) {
            return findUserAndUserDetailsOutVO;
        } finally {
            if (oimClient != null) {
                oimClient.logout();
                oimClient = null;
            }
            LOG.info(this + " findUserAndUserDetailsOutVO End for idNumber " + checkExistingUserInVO.getIdNumber());
        }
    }

    public UserOutVO getUserId(String username) {
        OIMClient oimClient = null;
        UserOutVO userOutVO = new UserOutVO();
        userOutVO.setUserId(null);
        userOutVO.setStatus(null);
        CheckUserExistUtil checkUserExistUtil = new CheckUserExistUtil();

        if (username == null || username.trim().isEmpty()) {
            userOutVO.setStatus("Error");
            userOutVO.setErrorMessage("Field mandatory : Username");
            return userOutVO;
        }

        try {
            OIMLoginProxy oimLoginProxy = new OIMLoginProxy();
            oimClient = oimLoginProxy.userLogin();
            if (oimClient == null) {
                userOutVO.setStatus("Error");
                userOutVO.setErrorMessage("System unavailable at the moment, please try again");
                LOG.error(this + " getUserId: Error on Login to oimClient " + userOutVO.getErrorMessage());
                return userOutVO;
            }
            LOG.info(this + " getUserId Logged into OIMClient  ");
            return checkUserExistUtil.getUserId(oimClient, username);
        } catch (Exception e) {
            LOG.error(this + " Error on getUsername " + e.getMessage());
            userOutVO.setStatus("Error");
            userOutVO.setErrorMessage("System unavailable at the moment, please try again");
            return userOutVO;
        } finally {
            if (oimClient != null) {
                oimClient.logout();
                oimClient = null;
            }
        }
    }

    public CheckExistingCellphoneOutVO checkExistingCellphone(String cellNumber) {
        OIMClient oimClient = null;
        CheckExistingCellphoneOutVO checkExistingCellphoneOutVO = new CheckExistingCellphoneOutVO();
        OIMLoginProxy oimLoginProxy = new OIMLoginProxy();
        checkExistingCellphoneOutVO.setErrorMessage(null);
        checkExistingCellphoneOutVO.setStatus(null);
        checkExistingCellphoneOutVO.setCellphoneExists(null);

        if (cellNumber == null || cellNumber.isEmpty()) {
            checkExistingCellphoneOutVO.setStatus("Error");
            checkExistingCellphoneOutVO.setErrorMessage("Mandatory field : Cellphone Number");
            return checkExistingCellphoneOutVO;
        }
        try {
            oimClient = oimLoginProxy.userLogin();
            if (oimClient == null) {
                LOG.error(this + " checkExistingCellphone: Failed  to OIMCLient login");
                checkExistingCellphoneOutVO.setStatus("Error");
                checkExistingCellphoneOutVO.setErrorMessage("System unavailable at the moment, please try again");
                LOG.error(this + " checkExistingCellphone: Error on Login to oimClient ");
                return checkExistingCellphoneOutVO;
            }
            LOG.info(this + " checkExistingCellphone Logged into OIMClient  ");
            CellNumberExistsUtil cellNumberExistsUtil = new CellNumberExistsUtil();
            LOG.info(this + " cellNumberExistsUtil into cellNumberExistsUtil  ");
            if (cellNumberExistsUtil.checkExistingCellphoneAttribute(oimClient, cellNumber)) {
                checkExistingCellphoneOutVO.setStatus("Success");
                checkExistingCellphoneOutVO.setCellphoneExists("true");
                LOG.info(this + " checkExistingCellphone success for cellNumber  " + cellNumber);
                return checkExistingCellphoneOutVO;
            } else {
                LOG.error(this + " checkExistingCellphone: No cell number found : " + cellNumber);
                //checkExistingCellphoneOutVO.setStatus("Error");
                checkExistingCellphoneOutVO.setStatus("Success"); /// changed by prakash
                checkExistingCellphoneOutVO.setCellphoneExists("false");
                return checkExistingCellphoneOutVO;
            }
        } catch (Exception e) {
            LOG.error(this + " Error on checkExistingCellphone " + e.getMessage());
            checkExistingCellphoneOutVO.setStatus("Error");
            checkExistingCellphoneOutVO.setErrorMessage("System unavailable at the moment, please try again");
            return checkExistingCellphoneOutVO;
        } finally {
            if (oimClient != null) {
                oimClient.logout();
                oimClient = null;
            }
        }
    }

    public CheckExistingUserOutVO checkExistingUser(CheckExistingUserInVO checkExistingUserInVO) {
        OIMClient oimClient = null;
        CheckExistingUserOutVO checkExistingUserOutVO = new CheckExistingUserOutVO();
        ValidateInVOUtil validateInVOUtil = new ValidateInVOUtil();
        checkExistingUserOutVO.setErrorMessage(null);
        checkExistingUserOutVO.setStatus(null);
        checkExistingUserOutVO.setUserExists(null);
        UserOutVO userOutVO = validateInVOUtil.validateCheckExistingUserInVO(checkExistingUserInVO);

        if (userOutVO.getStatus().equalsIgnoreCase("Error")) { //vijay check this method
            LOG.error("checkExistingUser " + checkExistingUserOutVO.getErrorMessage());
            checkExistingUserOutVO.setStatus("Error");
            checkExistingUserOutVO.setErrorMessage(userOutVO.getErrorMessage());
            return checkExistingUserOutVO;
        }

        try {
            OIMLoginProxy oimLoginProxy = new OIMLoginProxy();
            oimClient = oimLoginProxy.userLogin();
            if (oimClient == null) {
                checkExistingUserOutVO.setStatus("Error");
                checkExistingUserOutVO.setErrorMessage("System unavailable at the moment, please try again");
                LOG.error(this + " checkExistingUser: Failed  to OIMCLient login");
                return checkExistingUserOutVO;
            }
            LOG.info(this + " checkExistingUser Logged into OIMClient  ");
            CheckUserExistUtil checkUserExistUtil = new CheckUserExistUtil();
            if (checkUserExistUtil.checkExistingUser(oimClient, checkExistingUserInVO)) {
                checkExistingUserOutVO.setStatus("Success");
                checkExistingUserOutVO.setUserExists("true");
                return checkExistingUserOutVO;
            } else {
                LOG.error(this +
                          " checkExistingUser: No user found with specified details. Please provide valid input. " +
                          checkExistingUserInVO.getLastName());
                checkExistingUserOutVO.setStatus("Error");
                //checkExistingUserOutVO.setStatus("Success");
                checkExistingUserOutVO.setErrorMessage("No user found with specified details. Please provide valid input.");
                //checkExistingUserOutVO.setErrorMessage("false");
                return checkExistingUserOutVO;
            }
        } catch (Exception e) {
            LOG.error(this + " Error on checkExistingUser " + e.getMessage());
            checkExistingUserOutVO.setStatus("Error");
            checkExistingUserOutVO.setErrorMessage("System unavailable at the moment, please try again");
            return checkExistingUserOutVO;
        } finally {
            if (oimClient != null) {
                oimClient.logout();
                oimClient = null;
            }
        }
    }

    /**
     * @param fetchUsernameInVO
     * @return
     */
    public FetchUsernameOutVO getUsername(FetchUsernameInVO fetchUsernameInVO) {
        OIMClient oimClient = null;
        FetchUsernameOutVO fetchUsernameOutVO = new FetchUsernameOutVO();
        ValidateInVOUtil validateInVOUtil = new ValidateInVOUtil();
        fetchUsernameOutVO.setErrorMessage(null);
        fetchUsernameOutVO.setUsername(null);
        fetchUsernameOutVO = validateInVOUtil.validateFetchUsernameInVO(fetchUsernameInVO);

        if (fetchUsernameOutVO.getStatus().equalsIgnoreCase("Error")) {
            LOG.error("fetchUsernameOutVO " + fetchUsernameOutVO.getErrorMessage());
            fetchUsernameOutVO.setStatus("Error");
            fetchUsernameOutVO.setErrorMessage(fetchUsernameOutVO.getErrorMessage());
            return fetchUsernameOutVO;
        }

        try {
            OIMLoginProxy oimLoginProxy = new OIMLoginProxy();
            oimClient = oimLoginProxy.userLogin();
            if (oimClient == null) {
                fetchUsernameOutVO.setStatus("Error");
                fetchUsernameOutVO.setErrorMessage("System unavailable at the moment, please try again");
                LOG.error(this + " getUsername: Failed  to OIMCLient login");
                return fetchUsernameOutVO;
            }
            LOG.info(this + " getUsername Logged into OIMClient  ");
            CheckUserExistUtil checkUserExistUtil = new CheckUserExistUtil();
            return checkUserExistUtil.checkUsername(oimClient, fetchUsernameInVO);
        } catch (Exception e) {
            LOG.error(this + " Error on getUsername " + e.getMessage());
            fetchUsernameOutVO.setStatus("Error");
            fetchUsernameOutVO.setErrorMessage("System unavailable at the moment, please try again");
            return fetchUsernameOutVO;
        } finally {
            if (oimClient != null) {
                oimClient.logout();
                oimClient = null;
            }
        }
    }

    /**
     * @param ssaid
     * @return
     */
    public CheckExistingSSAIDOutVO checkExistingSSAID(String ssaid) {
        OIMClient oimClient = null;
        CheckExistingSSAIDOutVO checkExistingSSAIDOutVO = new CheckExistingSSAIDOutVO();
        OIMLoginProxy oimLoginProxy = new OIMLoginProxy();
        checkExistingSSAIDOutVO.setErrorMessage(null);
        checkExistingSSAIDOutVO.setStatus(null);
        checkExistingSSAIDOutVO.setSsaid(null);

        if (ssaid == null || ssaid.isEmpty()) {
            checkExistingSSAIDOutVO.setStatus("Error");
            checkExistingSSAIDOutVO.setErrorMessage("Mandatory field : Username");
            return checkExistingSSAIDOutVO;
        }
        try {
            oimClient = oimLoginProxy.userLogin();
            if (oimClient == null) {
                checkExistingSSAIDOutVO.setStatus("Error");
                checkExistingSSAIDOutVO.setErrorMessage("System unavailable at the moment, please try again");
                LOG.error(this + " getUsername: Failed  to OIMCLient login");
                return checkExistingSSAIDOutVO;
            }
            CheckUserExistUtil checkUserExistUtil = new CheckUserExistUtil();
            if (checkUserExistUtil.checkExistingEmployeeNumber(oimClient,
                                                               ssaid)) { // confirm with Mocx and RK about checking with employee number
                checkExistingSSAIDOutVO.setStatus("Success");
                checkExistingSSAIDOutVO.setSsaid("true");
                return checkExistingSSAIDOutVO;
            } else {
                checkExistingSSAIDOutVO.setStatus("Error");
                checkExistingSSAIDOutVO.setErrorMessage("No user found for ssaid " + ssaid);
                return checkExistingSSAIDOutVO;
            }
        } catch (Exception e) {
            LOG.error(this + " Error on checkExistingSSAID " + e.getMessage());
            checkExistingSSAIDOutVO.setStatus("Error");
            checkExistingSSAIDOutVO.setErrorMessage("System unavailable at the moment, please try again");
            return checkExistingSSAIDOutVO;
        } finally {
            if (oimClient != null) {
                oimClient.logout();
                oimClient = null;
            }
        }
    }

    /**
     * @param username
     * @return
     * @throws Exception
     */
    public UserDetailsVO getUserDetails(String username) throws Exception {
        OVDLoginProxy ovdLoginProxy = new OVDLoginProxy();
        DirContext dirContext = null;
        OIMClient oimClient = null;
        UserDetailsVO userDetailsVO = new UserDetailsVO();
        userDetailsVO.setErrorMessage(null);
        userDetailsVO.setStatus(null);
        userDetailsVO.setStatus(null);
        userDetailsVO.setFirstName(null);
        userDetailsVO.setLastName(null);
        userDetailsVO.setCellPhoneNumber(null);
        userDetailsVO.setDateOfBirth(null);
        userDetailsVO.setIdNumber(null);
        userDetailsVO.setMigratedUserFirstLogin(null);
        userDetailsVO.setUserAccountLocked(null);
        userDetailsVO.setIdType(null);
        userDetailsVO.setEmployeeNumber(null);
        LOG.info(this + " In getuserdetails ");

        if (username == null || username.isEmpty()) {
            userDetailsVO.setStatus("Error");
            userDetailsVO.setErrorMessage("Mandatory field : Username");
            return userDetailsVO;
        }

        try {

            OIMLoginProxy oimLoginProxy = new OIMLoginProxy();
            oimClient = oimLoginProxy.userLogin();
            if (oimClient == null) {
                userDetailsVO.setStatus("Error");
                userDetailsVO.setErrorMessage("System unavailable at the moment, please try again");
                LOG.error(this + " getUserDetails: Error on Login to oimClient " + userDetailsVO.getErrorMessage());
                return userDetailsVO;
            }
            LOG.info(this + " getUserId Logged into OIMClient  ");

            AttributesUtil attributesUtil = new AttributesUtil();
            //userDetailsVO = attributesUtil.getUserDetailsAttributes(oimClient, username);
            return attributesUtil.getUserDetailsAttributes(oimClient, username);
        } catch (Exception e) {
            LOG.error(this + " Error on getUsername " + e.getMessage());
            userDetailsVO.setStatus("Error");
            userDetailsVO.setErrorMessage("System unavailable at the moment, please try again");
            return userDetailsVO;
        } finally {
            if (oimClient != null) {
                oimClient.logout();
                oimClient = null;
            }
        }

        /*  try {
            dirContext = ovdLoginProxy.connect();
            AttributesUtil attributesUtil = new AttributesUtil();
            userDetailsVO = attributesUtil.getUserDetailsAttributes(dirContext, username);


            if (userDetailsVO.getStatus().equalsIgnoreCase("Success")) {
                LOG.info(this + " Sccessed in getting userdetails ");
                return userDetailsVO;
            } else {
                userDetailsVO.setStatus("Error");
                userDetailsVO.setErrorMessage("No user found for username: " + username);
                LOG.info(this + " Error in getuserdetails ");
                return userDetailsVO;
            }
        } catch (Exception e) {
            LOG.error(this + " Error on getUserStatus. ERROR: " + e.getMessage());
            userDetailsVO.setStatus("Error");
            userDetailsVO.setErrorMessage("System unavailable at the moment, please try again");
            return userDetailsVO;
        } finally {
            if (dirContext != null) {
                dirContext.close();
            }
        }
          return userDetailsVO;*/
    }


    /**
     * @param username
     * @return
     * @throws Exception
     */
    public GetUserAccountLockedOutVO getUserAccountLocked(String username) throws Exception {
        OVDLoginProxy ovdLoginProxy = new OVDLoginProxy();
        GetUserAccountLockedOutVO getUserAccountLockedOutVO = new GetUserAccountLockedOutVO();
        getUserAccountLockedOutVO.setUserAccountStatus(null);
        if (username == null || username.isEmpty()) {
            getUserAccountLockedOutVO.setStatus("Error");
            getUserAccountLockedOutVO.setErrorMessage("Mandatory field : Username");
            return getUserAccountLockedOutVO;
        }
        DirContext dirContext = null;
        AttributesUtil attributesUtil = new AttributesUtil();
        try {
            dirContext = ovdLoginProxy.connect();
            LOG.info(this + " getUserAccountLocked: Connected to dirContext " + dirContext);
            getUserAccountLockedOutVO = attributesUtil.getUserAccountLockedAttributes(dirContext, username);
            return getUserAccountLockedOutVO;
        } catch (Exception e) {
            LOG.error(this + " Error on getUserAccountLocked. ERROR: " + e.getMessage());
            getUserAccountLockedOutVO.setStatus("Error");
            getUserAccountLockedOutVO.setErrorMessage("System unavailable at the moment, please try again");
            return getUserAccountLockedOutVO;
        } finally {
            if (dirContext != null) {
                dirContext.close();
            }
        }
    }

    public GetADUsernameOutVO getADUsername(GetADUsernameInVO getADUsernameInVO) throws Exception {
        OIMClient oimClient = null;
        OIMLoginProxy oimLoginProxy = new OIMLoginProxy();
        GetADUsernameOutVO getADUsernameOutVO = new GetADUsernameOutVO();


        getADUsernameOutVO = ValidateInVOUtil.validateADUsernameInVO(getADUsernameInVO);
        if (getADUsernameOutVO.getStatus().equalsIgnoreCase("Error")) {
            LOG.error(this + " getADUsername " + getADUsernameOutVO.getErrorMessage());
            getADUsernameOutVO.setStatus("Error");
            getADUsernameOutVO.setErrorMessage(getADUsernameOutVO.getErrorMessage());
            return getADUsernameOutVO;
        }
        try {
            oimClient = oimLoginProxy.userLogin();
            if (oimClient == null) {
                getADUsernameOutVO.setStatus("Error");
                getADUsernameOutVO.setErrorMessage("System unavailable at the moment, please try again");
                LOG.error(this + " getADUsername: Failed  to OIMCLient login");
                return getADUsernameOutVO;
            }
            LOG.info(this + " getADUsername Logged into oimClient for the user id number " +
                     getADUsernameInVO.getIdNumber());
            ADUsernameAttributes adUsernameAttributes = new ADUsernameAttributes();
            getADUsernameOutVO = adUsernameAttributes.getADUsernameAttributes(oimClient, getADUsernameInVO);
            LOG.error(this + " getADUsername: after ADUsernameAttributes class");
            if (getADUsernameOutVO.getStatus().equalsIgnoreCase("Error")) {
                getADUsernameOutVO.setStatus("Error");
                getADUsernameOutVO.setErrorMessage("No username/alias could be retrieved");
                LOG.error(this + " No username/alias could be retrieved for surname " +
                          getADUsernameInVO.getSurname());
                return getADUsernameOutVO;
            } else {
                getADUsernameOutVO.setStatus("Success");
                return getADUsernameOutVO;
            }
        } catch (Exception e) {
            LOG.error(this + " Error on getADUsername. ERROR: " + e.getMessage());
            getADUsernameOutVO.setStatus("Error");
            getADUsernameOutVO.setErrorMessage("System unavailable at the moment, please try again");
            return getADUsernameOutVO;
        } finally {
            if (oimClient != null) {
                oimClient.logout();
                oimClient = null;
            }
        }
    }

    /**
     *This service is used  for getting the Role details
     * @param fetchUserRolesInVO
     * @return RoleDetailsVO
     */
    public RoleDetailsVO getRoleDetails(String rolename) {
        RoleDetailsVO roleDetailsVO = new RoleDetailsVO();
        OIMClient oimClient = null;
        OIMLoginProxy oimLoginProxy = new OIMLoginProxy();
        RoleDetailsUtil roleDetailsUtil = new RoleDetailsUtil();

        if (null == rolename || "".equalsIgnoreCase(rolename)) {
            roleDetailsVO.setStatus("Error");
            roleDetailsVO.setErrorMessage("Please provide a rolename");
            return roleDetailsVO;
        }
        try {
            oimClient = oimLoginProxy.userLogin();
            if (oimClient == null) {
                roleDetailsVO.setStatus("Error");
                roleDetailsVO.setErrorMessage("System unavailable at the moment, please try again");
                LOG.error(this + " getRoleDetails: Error Logging into oimclient");
                return roleDetailsVO;
            }
            LOG.info(this + " getRoleDetails: Logged into oimclient");
        } catch (Exception e) {
            roleDetailsVO.setStatus("Error");
            roleDetailsVO.setErrorMessage("Exception while logging in. Please try again.");
            LOG.error(this + " Error on getRoleDetails. ERROR: " + e.getMessage());
            return roleDetailsVO;
        }
        try {
            return roleDetailsUtil.roleDetails(oimClient, rolename);
        } catch (Exception e) {
            roleDetailsVO.setStatus("Error");
            roleDetailsVO.setErrorMessage("No Role found for roleName : " + rolename);
            LOG.error(this + " Error on getRoleDetails. ERROR: " + e.getMessage());
            return roleDetailsVO;
        } finally {
            if (oimClient != null) {
                oimClient.logout();
                oimClient = null;
            }
        }
    }

    public StatusOutVO provisionUser(ProvisionUserAccountVO provisionUserAccountVO) {
        LOG.info(this + " provisionUser: provisionUser Begin" + provisionUserAccountVO.getUsername());
        StatusOutVO statusOutVO = new StatusOutVO();
        OIMLoginProxy oimLoginProxy = new OIMLoginProxy();
        ProvisionUserUtil provisionUserUtil = new ProvisionUserUtil();
        OIMClient oimClient = null;
        try {
            oimClient = oimLoginProxy.userLogin();
            if (oimClient == null) {
                statusOutVO.setStatus("Error");
                statusOutVO.setErrorMessage("System unavailable at the moment, please try again");
                LOG.error(this + " provisionUser: ERROR on oimClient login");
                return statusOutVO;
            }
        } catch (Exception e) {
            statusOutVO.setStatus("Error");
            statusOutVO.setErrorMessage("Exception while logging in. Please try again.");
            LOG.error(this + " Error on provisionUser. ERROR: " + e.getMessage());
            return statusOutVO;
        }
        try {
            statusOutVO = ValidateInVOUtil.validateProvisionUserAccountVOUtil(provisionUserAccountVO);
        } catch (Exception e) {
            LOG.error(this + " Error on provisionUser. ERROR: " + e.getMessage());
            e.printStackTrace();
            return statusOutVO;

        }
        if (statusOutVO.getStatus().equalsIgnoreCase("Error")) {
            LOG.error(this + " getADUsername " + statusOutVO.getErrorMessage());
            statusOutVO.setStatus("Error");
            statusOutVO.setErrorMessage(statusOutVO.getErrorMessage());
            return statusOutVO;
        }

        try {
            return provisionUserUtil.provisionUser(oimClient, provisionUserAccountVO);
        } catch (Exception ex) {
            statusOutVO.setStatus("Error");
            statusOutVO.setErrorMessage("Error while provisioning resource to AD");
            return statusOutVO;
        } finally {
            if (oimClient != null) {
                oimClient.logout();
                oimClient = null;
            }
        }

    }

    public UserOutVO updateUser(UpdateUserInVO updateUserInVO) {
        UserOutVO userOutVO = new UserOutVO();
        UpdateUserUtil updateUserUtil = new UpdateUserUtil();
        if (updateUserInVO.getEmployeeNumber() == null || updateUserInVO.getEmployeeNumber().trim().isEmpty()) {
            userOutVO.setStatus("Error");
            userOutVO.setErrorMessage("Mandatory field : Employee Number" + System.getProperty("line.separator"));
            LOG.error(this + " updateUser", "Validation Error: mandatory field - employee number");
            return userOutVO;
        }

        OIMClient oimClient = null;
        String roleName = null;
        OIMLoginProxy oimLoginProxy = new OIMLoginProxy();
        try {
            oimClient = oimLoginProxy.userLogin();
            if (oimClient == null) {
                userOutVO.setStatus("Error");
                userOutVO.setErrorMessage("System unavailable at the moment, please try again");
                LOG.error(this + " updateUser: Failed  to OIMCLient login");
                return userOutVO;
            }
        } catch (Exception e) {
            userOutVO.setStatus("Error");
            userOutVO.setErrorMessage("Exception while logging in. Please try again.");
            LOG.error(this + " updateUser: Failed  to OIMCLient login. Error: " + e.getMessage());
            return userOutVO;
        }

        try {
            return updateUserUtil.updateUser(oimClient, updateUserInVO);
        } catch (Exception ex) {
            userOutVO.setStatus("Error");
            userOutVO.setErrorMessage(ex.getMessage());
            return userOutVO;
        } finally {
            if (oimClient != null) {
                oimClient.logout();
                oimClient = null;
            }
        }
    }

    public StatusOutVO assignRole(UserRoleGrantVO userRolegrantVO) {
        LOG.info(this + "assignRole Begin");
        StatusOutVO statusOutVO = new StatusOutVO();
        String status = null;
        AssignRoleUtil assignRoleUtil = new AssignRoleUtil();
        LOG.info(this + " assignRole: Logging into oimclient");
        OIMClient oimClient = null;
        OIMLoginProxy oimLoginProxy = new OIMLoginProxy();
        try {
            oimClient = oimLoginProxy.userLogin();
            if (oimClient == null) {
                LOG.error(this + " assignRole: Failed  to OIMCLient login");
                statusOutVO.setStatus("Exception while logging in. Please try again.");
                return statusOutVO;
            }
        } catch (Exception e) {
            LOG.error(this + " assignRole: Failed  to OIMCLient login");
            statusOutVO.setStatus("Exception while logging in. Please try again.");
            return statusOutVO;
        }

        try {
            return assignRoleUtil.assignRole(oimClient, userRolegrantVO);
        } catch (Exception ex) {
            return statusOutVO;
        } finally {
            if (oimClient != null) {
                oimClient.logout();
                oimClient = null;
            }
        }
    }
    public StatusOutVO assignRoles(UserRoleListAssignVO userRolegrantVO) {
        LOG.info(this + "assignRoles Begin");
        StatusOutVO statusOutVO = new StatusOutVO();
        String status = null;
        AssignRolesUtil assignRolesUtil = new AssignRolesUtil();
        LOG.info(this + " assignRole: Logging into oimclient");
        OIMClient oimClient = null;
        OIMLoginProxy oimLoginProxy = new OIMLoginProxy();
        try {
            oimClient = oimLoginProxy.userLogin();
            if (oimClient == null) {
                LOG.error(this + " assignRoles: Failed  to OIMCLient login");
                statusOutVO.setStatus("Exception while logging in. Please try again.");
                return statusOutVO;
            }
        } catch (Exception e) {
            LOG.error(this + " assignRoles: Failed  to OIMCLient login");
            statusOutVO.setStatus("Exception while logging in. Please try again.");
            return statusOutVO;
        }

        try {
            return assignRolesUtil.assignRoles(oimClient, userRolegrantVO);
        } catch (Exception ex) {
            return statusOutVO;
        } finally {
            if (oimClient != null) {
                oimClient.logout();
                oimClient = null;
            }
        }
    }
    
    
    
    

    public StatusOutVO revokeRole(UserRoleGrantVO userRolerevokeVO) {
        LOG.info(this + "revokeRole Begin");
        StatusOutVO statusOutVO = new StatusOutVO();
        String status = null;
        RevokeRoleUtil revokeRoleUtil = new RevokeRoleUtil();
        LOG.info(this + " revokeRole: Logging into oimclient");
        OIMClient oimClient = null;
        OIMLoginProxy oimLoginProxy = new OIMLoginProxy();
        try {
            oimClient = oimLoginProxy.userLogin();
            if (oimClient == null) {
                LOG.error(this + " revokeRole: Failed  to OIMCLient login");
                statusOutVO.setStatus("Exception while logging in. Please try again.");
                return statusOutVO;
            }
        } catch (Exception e) {
            LOG.error(this + " revokeRole: Failed  to OIMCLient login");
            statusOutVO.setStatus("Exception while logging in. Please try again.");
            return statusOutVO;
        }

        try {
            return revokeRoleUtil.revokeRole(oimClient, userRolerevokeVO);
        } catch (Exception ex) {
            return statusOutVO;
        } finally {
            if (oimClient != null) {
                oimClient.logout();
                oimClient = null;
            }
        }
    }


    public StatusOutVO revokeRoles(UserRoleListAssignVO userRolegrantVO) {
        LOG.info(this + "revokeRoles Begin");
        StatusOutVO statusOutVO = new StatusOutVO();
        String status = null;
        RevokeRolesUtil revokeRolesUtil = new RevokeRolesUtil();
        LOG.info(this + " revokeRoles: Logging into oimclient");
        OIMClient oimClient = null;
        OIMLoginProxy oimLoginProxy = new OIMLoginProxy();
        try {
            oimClient = oimLoginProxy.userLogin();
            if (oimClient == null) {
                LOG.error(this + " revokeRoles: Failed  to OIMCLient login");
                statusOutVO.setStatus("Exception while logging in. Please try again.");
                return statusOutVO;
            }
        } catch (Exception e) {
            LOG.error(this + " revokeRoles: Failed  to OIMCLient login");
            statusOutVO.setStatus("Exception while logging in. Please try again.");
            return statusOutVO;
        }

        try {
            return revokeRolesUtil.revokeRoles(oimClient, userRolegrantVO);
        } catch (Exception ex) {
            return statusOutVO;
        } finally {
            if (oimClient != null) {
                oimClient.logout();
                oimClient = null;
            }
        }
    }


    public StatusOutVO lockUser(String username) {

        LOG.info(this + " lockUser begin");
        StatusOutVO statusOutVO = new StatusOutVO();

        OIMLoginProxy oimLoginProxy = new OIMLoginProxy();
        ValidateInVOUtil validateInVOUtil = new ValidateInVOUtil();
        LockUserUtil lockUserUtil = new LockUserUtil();
        OIMClient oimClient = null;
        ADPropertiesVO adPropertiesVO;

        try {
            PropertiesUtil propertiesUtil = new PropertiesUtil();
            adPropertiesVO = propertiesUtil.getADProperties();
            statusOutVO = validateInVOUtil.validateLockUser(adPropertiesVO, username);
            LOG.info(this + " lockUser Validated username and adPropertiesVO for username " + username);
        } catch (Exception ex) {
            statusOutVO.setStatus("Error");
            statusOutVO.setErrorMessage("Exception while getting AD properties. Please try again.");
            LOG.error(this + " lockUser Error " + ex.getMessage());
            return statusOutVO;
        }
        if (statusOutVO.getStatus().equalsIgnoreCase("Error")) {
            LOG.error(this + " lockUser Error:" + statusOutVO.getErrorMessage());
            statusOutVO.setStatus("Error");
            statusOutVO.setErrorMessage(statusOutVO.getErrorMessage());
            return statusOutVO;
        }

        try {
            oimClient = oimLoginProxy.userLogin();
            if (oimClient == null) {
                statusOutVO.setStatus("Error");
                statusOutVO.setErrorMessage("Exception while logging in. Please try again.");
                LOG.error(this + " lockUser: Logging into oimclient failed for username " + username);
                return statusOutVO;
            }
        } catch (Exception e) {
            LOG.error(this + " lockUser: ERROR on Logging into oimclient " + e.getMessage());
            statusOutVO.setStatus("Error");
            statusOutVO.setErrorMessage("Exception while logging in. Please try again.");
            return statusOutVO;
        }
        LOG.info(this + " lockUser: logged into oimclient for username " + username);
        try {
            statusOutVO = lockUserUtil.lockUser(oimClient, adPropertiesVO, username);
        } catch (Exception e) {
            statusOutVO.setErrorMessage("Error while modifying provisioned account.");
            statusOutVO.setStatus("Error");
            return statusOutVO;
        } finally {
            if (oimClient != null) {
                oimClient.logout();
                oimClient = null;
            }
        }
        return statusOutVO;
    }


    public StatusOutVO unlockAccount(String username) {
        LOG.info(this + " unlockAccount begin");
        StatusOutVO statusOutVO = new StatusOutVO();
        StringBuffer buffer = new StringBuffer();
        ValidateInVOUtil validateInVOUtil = new ValidateInVOUtil();
        UnlockAccountUtil unlockAccountUtil = new UnlockAccountUtil();

        ADPropertiesVO adPropertiesVO;
        try {
            PropertiesUtil propertiesUtil = new PropertiesUtil();
            adPropertiesVO = propertiesUtil.getADProperties();
            statusOutVO = validateInVOUtil.validateUnLockUser(adPropertiesVO, username);
            LOG.info(this + " unlockAccount Validated username and adPropertiesVO for username " + username);
        } catch (Exception ex) {
            statusOutVO.setStatus("Error");
            statusOutVO.setErrorMessage("Exception while getting AD properties. Please try again.");
            LOG.error(this + " unlockAccount", "Error " + ex.getMessage());
            return statusOutVO;
        }
        if (statusOutVO.getStatus().equalsIgnoreCase("Error")) {
            LOG.error(this + " unlockAccount Error:" + statusOutVO.getErrorMessage());
            statusOutVO.setStatus("Error");
            statusOutVO.setErrorMessage(statusOutVO.getErrorMessage());
            return statusOutVO;
        }

        OIMClient oimClient = null;
        OIMLoginProxy oimLoginProxy = new OIMLoginProxy();
        try {
            oimClient = oimLoginProxy.userLogin();
            if (oimClient == null) {
                statusOutVO.setStatus("Error");
                statusOutVO.setErrorMessage("Exception while logging in. Please try again.");
                LOG.error(this + " unlockAccount: ERROR on oimClient login");
                return statusOutVO;
            }
        } catch (Exception e) {
            statusOutVO.setStatus("Error");
            statusOutVO.setErrorMessage("Exception while logging in. Please try again.");
            LOG.error(this + " unlockAccount: ERROR on oimClient login");
            return statusOutVO;
        }

        try {
            statusOutVO = unlockAccountUtil.unLockUser(oimClient, adPropertiesVO, username);
        } catch (Exception e) {
            statusOutVO.setErrorMessage("Error while modifying provisioned account.");
            statusOutVO.setStatus("Error");
            return statusOutVO;
        } finally {
            if (oimClient != null) {
                oimClient.logout();
                oimClient = null;
            }
        }
        return statusOutVO;
    }

    public StatusOutVO disableUser(DisableUserRequestInVO disableUserRequestInVO) {
        StatusOutVO statusOutVO = new StatusOutVO();
        DisableUserUtil disableUserUtil = new DisableUserUtil();
        statusOutVO = ValidateInVOUtil.validateDisableUserUtil(disableUserRequestInVO);
        LOG.info(this + " disableUser Entering");
        if (statusOutVO.getStatus().equalsIgnoreCase("Error")) {
            LOG.error(this + " disableUser " + statusOutVO.getErrorMessage());
            statusOutVO.setStatus("Error");
            statusOutVO.setErrorMessage(statusOutVO.getErrorMessage());
            return statusOutVO;
        }
        OIMClient oimClient = null;
        OIMLoginProxy oimLoginProxy = new OIMLoginProxy();
        try {
            oimClient = oimLoginProxy.userLogin();
            if (oimClient == null) {
                LOG.error(this + " disableUser: ERROR on oimClient login");
                statusOutVO.setStatus("Error");
                statusOutVO.setErrorMessage("Exception while logging in. Please try again.");
                return statusOutVO;
            }
        } catch (Exception e) {
            LOG.error(this + " disableUser: ERROR on oimClient login");
            statusOutVO.setStatus("Error");
            statusOutVO.setErrorMessage("Exception while logging in. Please try again.");
            return statusOutVO;
        }

        try {
            return disableUserUtil.disableUser(oimClient, disableUserRequestInVO);
        } catch (Exception ex) {
            return statusOutVO;
        } finally {
            if (oimClient != null) {
                oimClient.logout();
                oimClient = null;
            }
        }
    }


    public StatusOutVO validatePassword(ValidatePasswordVO passwordVO) {
        StatusOutVO statusOutVO = new StatusOutVO();
        OIMClient oimClient = null;
        OIMLoginProxy oimLoginProxy = new OIMLoginProxy();
        ValidatePasswordUtil validatePasswordUtil = new ValidatePasswordUtil();
        System.out.println("Entered password " + passwordVO.getPassword());

        try {
            LOG.info(this + " validatePassword: Login to OIMCLIENT begin ");
            oimClient = oimLoginProxy.userLogin();
            if (oimClient == null) {
                LOG.error(this + " validatePassword: Exception while logging into OIMClient ");
                statusOutVO.setStatus("Error");
                statusOutVO.setErrorMessage("Exception while logging in. Please try again.");
                return statusOutVO;
            }
        } catch (Exception e) {
            LOG.error(this + " validatePassword: Exception while logging into OIMClient ERROR: " + e.getMessage());
            statusOutVO.setStatus("Error");
            statusOutVO.setErrorMessage("Exception while logging in. Please try again.");
            return statusOutVO;
        }

        try {
            return validatePasswordUtil.validatePassword(oimClient, passwordVO);
        } catch (Exception ex) {
            return statusOutVO;
        } finally {
            if (oimClient != null) {
                oimClient.logout();
                oimClient = null;
            }
        }
    }

    /**
     * @param generateTempPasswordVO
     * @return
     */
    public GenerateTempPasswordOutVO generateTempPassword(GenerateTempPasswordInVO generateTempPasswordInVO) {

        GenerateTempPasswordOutVO generateTempPasswordOutVO = new GenerateTempPasswordOutVO();
        UserDetailsVO userDetailsVO = new UserDetailsVO();
        GenerateTempPasswordUtil generateTempPasswordUtil = new GenerateTempPasswordUtil();
        generateTempPasswordOutVO = ValidateInVOUtil.validateTempPasswordInVO(generateTempPasswordInVO);

        if (generateTempPasswordOutVO.getStatus().equalsIgnoreCase("Error")) {
            generateTempPasswordOutVO.setStatus("Error");
            generateTempPasswordOutVO.setErrorMessage(generateTempPasswordOutVO.getErrorMessage());
            LOG.error(this + " generateTempPassword: Validation error:" + generateTempPasswordOutVO.getErrorMessage());
            return generateTempPasswordOutVO;
        }

        try {
            LOG.info(this + " generateTempPassword : getUserDetails for username " +
                     generateTempPasswordInVO.getUsername());
            userDetailsVO = getUserDetails(generateTempPasswordInVO.getUsername());
            if (userDetailsVO.getStatus().equalsIgnoreCase("Error")) {
                LOG.error(this + " generateTempPassword: getUserDetails for username " +
                          generateTempPasswordInVO.getUsername() + " Error: " + userDetailsVO.getErrorMessage());
                generateTempPasswordOutVO.setStatus("Error");
                generateTempPasswordOutVO.setErrorMessage(userDetailsVO.getErrorMessage());
                return generateTempPasswordOutVO;
            }
        } catch (Exception e) {
            generateTempPasswordOutVO.setStatus("Error");
            e.printStackTrace();
            generateTempPasswordOutVO.setErrorMessage("Error in Generating Password. Please try again");
            LOG.error(this + " generateTempPassword: getUserDetails for username " +
                      generateTempPasswordInVO.getUsername() + " Error: " + userDetailsVO.getErrorMessage());
            return generateTempPasswordOutVO;
        }
        try {
            return generateTempPasswordUtil.generateTempPassword(userDetailsVO, generateTempPasswordInVO);
        } catch (Exception ex) {
            return generateTempPasswordOutVO;
        }

    }

    public StatusOutVO changePassword(ChangePasswordVO passwordVO) {

        LOG.info(this + " changePassword begin");
        StatusOutVO statusOutVO = new StatusOutVO();
        statusOutVO = ValidateInVOUtil.validateChangePasswordVO(passwordVO);
        ChangePasswordUtil changePasswordUtil = new ChangePasswordUtil();

        if (statusOutVO.getStatus().equalsIgnoreCase("Error")) {
            LOG.error(this + " changePassword " + statusOutVO.getErrorMessage());
            statusOutVO.setStatus("Error");
            statusOutVO.setErrorMessage(statusOutVO.getErrorMessage());
            return statusOutVO;
        }
        OIMClient oimClient = null;
        OIMLoginProxy oimLoginProxy = new OIMLoginProxy();
        try {
            oimClient = oimLoginProxy.userLogin();
            if (oimClient == null) {
                LOG.error(this + " changePassword: Exception while logging into OIMClient ");
                statusOutVO.setStatus("Error");
                statusOutVO.setErrorMessage("Exception while logging in. Please try again.");
                return statusOutVO;
            }
        } catch (Exception e) {
            LOG.error(this + " changePassword: Exception while logging into OIMClient ERROR: " + e.getMessage());
            statusOutVO.setStatus("Error");
            statusOutVO.setErrorMessage("Exception while logging in. Please try again.");
            return statusOutVO;
        }

        try {
            return changePasswordUtil.changePassword(oimClient, passwordVO);
        } catch (Exception ex) {
            return statusOutVO;
        } finally {
            if (oimClient != null) {
                oimClient.logout();
                oimClient = null;
            }
        }

    }
    
    public StatusOutVO changePasswordForLoggedInUser(ChangePasswordForLoggedInUserVO changePasswordForLoggedInUserVO) {
        
        StatusOutVO statusOutVO = new StatusOutVO();   
        ChangePasswordForLoggedInUserUtil changePasswordForLoggedInUserUtil = new ChangePasswordForLoggedInUserUtil();
        LOG.info(this + "changePasswordForLoggedInUser");    
        LOG.info(this + "changePasswordForLoggedInUser", "__changePasswordForLoggedInUser Begin__");
        
        LOG.info(this + " Entering in to changePasswordForLoggedInUser ");
        
        statusOutVO = ValidateInVOUtil.validateChangePasswordForLoggedInUserVO(changePasswordForLoggedInUserVO);
        LOG.info(this + " completion of changePasswordForLoggedInUser");
        if (statusOutVO.getStatus().equalsIgnoreCase("Error")) {
            LOG.error(this + " changePasswordForLoggedInUser " + statusOutVO.getErrorMessage());
            statusOutVO.setStatus("Error");
            statusOutVO.setErrorMessage(statusOutVO.getErrorMessage());
            return statusOutVO;
        }
            OIMClient oimClient = null;
            OIMLoginProxy oimLoginProxy = new OIMLoginProxy();
            try {
                oimClient = oimLoginProxy.userLogin();
                if (oimClient == null) {
                    LOG.error(this + " changePasswordForLoggedInUser: Exception while logging into OIMClient ");
                    statusOutVO.setStatus("Error");
                    statusOutVO.setErrorMessage("Exception while logging in. Please try again.");
                    return statusOutVO;
                }
            } catch (Exception e) {
                LOG.error(this + " changePasswordForLoggedInUser: Exception while logging into OIMClient ERROR: " + e.getMessage());
                statusOutVO.setStatus("Error");
                statusOutVO.setErrorMessage("Exception while logging in. Please try again.");
                return statusOutVO;
            }
            try {
                 return changePasswordForLoggedInUserUtil.changePasswordForLoggedInUser(oimClient, changePasswordForLoggedInUserVO);
             } catch (Exception ex) {
                 return statusOutVO;
             } finally {
                 if (oimClient != null) {
                     oimClient.logout();
                     oimClient = null;
                 }
             }
          }

    public ADSecurityAnswersOutVO validateADSecurityAnswers(String username) throws Exception {

        ADSecurityAnswersOutVO adSecurityAnswersOutVO = new ADSecurityAnswersOutVO();
        DirContext dirContext;
        OVDLoginProxy ovdLoginProxy = new OVDLoginProxy();
        if (username == null || username.isEmpty()) {
            adSecurityAnswersOutVO.setStatus("Error");
            adSecurityAnswersOutVO.setErrorMessage("Mandatory field : Username");
            LOG.error(" validateADSecurityAnswers: Mandatory field : Username " + username);
            return adSecurityAnswersOutVO;
        }
        LOG.info(this + " Entering into validateADSecurityAnswers " + username);

        try {
            dirContext = ovdLoginProxy.connect();
            LOG.info(this + " Entering into validateADSecurityAnswers dirContext" + dirContext);

        } catch (NamingException ne) {
            adSecurityAnswersOutVO.setStatus("Error");
            adSecurityAnswersOutVO.setErrorMessage("System unavailable at the moment, please try again");
            LOG.error(this + " validateADSecurityAnswers: Failed login to OVD (dirContext is null)  for the user" +
                      username);
            ne.printStackTrace();
            return adSecurityAnswersOutVO;
        } catch (Exception e) {
            adSecurityAnswersOutVO.setStatus("Error");
            adSecurityAnswersOutVO.setErrorMessage("System unavailable at the moment, please try again");
            LOG.error(this + " validateADSecurityAnswers: Failed login to OVD (dirContext is null)  for the user" +
                      username);
            return adSecurityAnswersOutVO;
        }

        if (dirContext == null) {
            adSecurityAnswersOutVO.setStatus("Error");
            adSecurityAnswersOutVO.setErrorMessage("System unavailable at the moment, please try again");
            LOG.error(this + " validateADSecurityAnswers: Failed login to OVD (dirContext is null)  for the user " +
                      username);
            return adSecurityAnswersOutVO;
        }

        ValidateADSecurityAnswersUtil validateADSecurityAnswersUtil = new ValidateADSecurityAnswersUtil();
        try {
            return validateADSecurityAnswersUtil.validateADSecurityAnswers(dirContext, username);
        } catch (Exception ex) {
            return adSecurityAnswersOutVO;
        } finally {
            if (dirContext != null) {
                dirContext.close();
            }
        }
    }
    
    
    /**
     *This service is used  for updating the notification preference in OIM.
     * @param notitifcationPreferenceVO
     * @return StatusOutVO
     */
    public StatusOutVO updateNotitifcationPreference(NotitifcationPreferenceVO notitifcationPreferenceVO) {
                    
            LOG.info(this + "updateNotitifcationPreference");        
            LOG.info(this + "updateNotitifcationPreference", "updateNotitifcationPreference Begin__");
            
            StatusOutVO statusOutVO = new StatusOutVO();
            StringBuffer buffer = new StringBuffer();
            if (notitifcationPreferenceVO.getUsername() == null || notitifcationPreferenceVO.getUsername().trim().isEmpty()) {
                buffer.append("Mandatory field : User Name" + System.getProperty("line.separator"));
            }

            if (buffer != null && buffer.length() > 0) {
                statusOutVO.setStatus("Error");
                statusOutVO.setErrorMessage(buffer.toString());
                LOG.info(this + "updateNotitifcationPreference", "Error: " + buffer.toString());
                return statusOutVO;
            }
            UpdateNotitifcationPreferenceUtil updateNotitifcationPreferenceUtil = new UpdateNotitifcationPreferenceUtil();
            LOG.info(this + " UpdateNotitifcationPreferenceUtil: Logging into oimclient");     
            OIMClient oimClient = null;
            OIMLoginProxy oimLoginProxy = new OIMLoginProxy();
            try {
                oimClient = oimLoginProxy.userLogin();
                if (oimClient == null) {
                    LOG.error(this + " UpdateNotitifcationPreferenceUtil: Failed  to OIMCLient login");
                    statusOutVO.setStatus("Exception while logging in. Please try again.");
                    return statusOutVO;
                }
            } catch (Exception e) {
                LOG.error(this + " UpdateNotitifcationPreferenceUtil: Failed  to OIMCLient login");
                statusOutVO.setStatus("Exception while logging in. Please try again.");
                return statusOutVO;
            }
            try {
                return updateNotitifcationPreferenceUtil.updateNotitifcationPreference(oimClient, notitifcationPreferenceVO);
            } catch (Exception ex) {
                return statusOutVO;
            } finally {
                if (oimClient != null) {
                    oimClient.logout();
                    oimClient = null;
                }
            }                      
        }
    
    /**
     *This service is used for getting the role details for which the role type  is 'Delegated Administrator'
     * @param fetchUserRolesInVO
     * @return AdminRoleListVO
     */

    public AdminRoleListVO getDelegatedAuthAdminRole(String username) {
        AdminRoleListVO roleListVO = new AdminRoleListVO();
        DelegatedAuthAdminRoleUtil delegatedAuthAdminRoleUtil = new DelegatedAuthAdminRoleUtil();
        String roletype = null;      
        LOG.info(this + "getDelegatedAuthAdminRole");                
        LOG.info(this + "getDelegatedAuthAdminRole", "__getDelegatedAuthAdminRole Begin__");       
        List<Role> roles = null;         
        OIMClient oimClient = null;
        OIMLoginProxy oimLoginProxy = new OIMLoginProxy();
        try {
            oimClient = oimLoginProxy.userLogin();
            if (oimClient == null) {
                LOG.error(this + " getDelegatedAuthAdminRole: Failed  to OIMCLient login");
                roleListVO.setStatus("Error");
                roleListVO.setErrorMessage("Exception while logging in. Please try again.");
                return roleListVO;
            }
        } catch (Exception e) {
            LOG.error(this + " getDelegatedAuthAdminRole: Failed  to OIMCLient login");
            roleListVO.setStatus("Error");
            roleListVO.setErrorMessage("Exception while logging in. Please try again.");
            return roleListVO;
        }
        
      
        try {
            return delegatedAuthAdminRoleUtil.getDelegatedAuthAdminRole(oimClient, username);
        } catch (Exception ex) {
            return roleListVO;
        } finally {
            if (oimClient != null) {
                oimClient.logout();
                oimClient = null;
            }
        }    
     }
    
    public RoleListVO getTransferrableSiteRolesOfUser(String username) {
        
            List<Role> roles = null;
            RoleListVO roleListVO = new RoleListVO();
            TransferrableSiteRolesOfUserUtil transferrableSiteRolesOfUserUtil = new TransferrableSiteRolesOfUserUtil();
            
            LOG.info(this + "getTransferrableSiteRolesOfUser");                
            LOG.info(this + "getTransferrableSiteRolesOfUser", "getTransferrableSiteRolesOfUser Begin__");
            
            OIMClient oimClient = null;
            OIMLoginProxy oimLoginProxy = new OIMLoginProxy();
            try {
                oimClient = oimLoginProxy.userLogin();
                if (oimClient == null) {
                    LOG.error(this + " getTransferrableSiteRolesOfUser: Failed  to OIMCLient login");
                    roleListVO.setStatus("Error");
                    roleListVO.setErrorMessage("Exception while logging in. Please try again.");
                    return roleListVO;
                }
            } catch (Exception e) {
                LOG.error(this + " getTransferrableSiteRolesOfUser: Failed  to OIMCLient login");
                roleListVO.setStatus("Error");
                roleListVO.setErrorMessage("Exception while logging in. Please try again.");
                return roleListVO;
            }
            
            try {
                return transferrableSiteRolesOfUserUtil.getTransferrableSiteRolesOfUser(oimClient, username);
            } catch (Exception ex) {
                return roleListVO;
            } finally {
                if (oimClient != null) {
                    oimClient.logout();
                    oimClient = null;
                }
            }          
        }

    /**
     *This service is used  for getting the details for sending notification
     * @param usernameVO
     * @return UserPreferenceDetailsVO
     */
    public UserPreferenceDetailsVO getUserDetailsToNotify(String username) {
        
            LOG.info(this +  "getUserDetailsToNotify");
            LOG.info(this + "getUserDetailsToNotify", "__getUserDetailsToNotify Begin__");
            Set<String> attrNames = null;
            UserPreferenceDetailsVO userVO = new UserPreferenceDetailsVO();
            UserPreferenceDetailsVOUtil userPreferenceDetailsVOUtil = new UserPreferenceDetailsVOUtil();
            
            OIMClient oimClient = null;
            OIMLoginProxy oimLoginProxy = new OIMLoginProxy();
            try {
                oimClient = oimLoginProxy.userLogin();
                if (oimClient == null) {
                    LOG.error(this + " getTransferrableSiteRolesOfUser: Failed  to OIMCLient login");
                    userVO.setStatus("Error");
                    userVO.setErrorMessage("Exception while logging in. Please try again.");
                    return userVO;
                }
            } catch (Exception e) {
                LOG.error(this + " getTransferrableSiteRolesOfUser: Failed  to OIMCLient login");
                userVO.setStatus("Error");
                userVO.setErrorMessage("Exception while logging in. Please try again.");
            return userVO;
        }
        try {
            return userPreferenceDetailsVOUtil.getUserDetailsToNotify(oimClient, username);
        } catch (Exception ex) {
            return userVO;
        } finally {
            if (oimClient != null) {
                oimClient.logout();
                oimClient = null;
            }
        }                            
    }
    
    /**
     *This service is used  for updating the failure counter for OTP invalid attemps
     * @param otpFailCounterVO
     * @return OTPOutVO
     */
    public OTPOutVO updateOTPFailureCounter(OTPFailCounterVO otpFailCounterVO) {
        
        LOG.info(this +  "updateOTPFailCounter");
        LOG.info(this +  "updateOTPFailCounter", "__updateOTPFailCounter Begin__");
        OTPOutVO otpOutVO = new OTPOutVO();
        UpdateOTPFailureCounterUtil updateOTPFailureCounterUtil = new UpdateOTPFailureCounterUtil();
        StringBuffer buffer = new StringBuffer();
        if (otpFailCounterVO.getUsername() == null || otpFailCounterVO.getUsername().trim().isEmpty()) {
            buffer.append("Mandatory field : User Name" + System.getProperty("line.separator"));
        }

        if (buffer != null && buffer.length() > 0) {
            otpOutVO.setStatus("Error");
            otpOutVO.setErrorMessage(buffer.toString());
            LOG.info(this +  "updateOTPFailCounter", "Error: " + buffer.toString());
            return otpOutVO;
        }
        
        OIMClient oimClient = null;
        OIMLoginProxy oimLoginProxy = new OIMLoginProxy();
        try {
            oimClient = oimLoginProxy.userLogin();
            if (oimClient == null) {
                LOG.error(this + " getTransferrableSiteRolesOfUser: Failed  to OIMCLient login");
                otpOutVO.setStatus("Error");
                otpOutVO.setErrorMessage("Exception while logging in. Please try again.");
                return otpOutVO;
            }
        } catch (Exception e) {
            LOG.error(this + " getTransferrableSiteRolesOfUser: Failed  to OIMCLient login");
            otpOutVO.setStatus("Error");
            otpOutVO.setErrorMessage("Exception while logging in. Please try again.");
            return otpOutVO;
        }
        try {
            return updateOTPFailureCounterUtil.updateOTPFailureCounter(oimClient, otpFailCounterVO);
        } catch (Exception ex) {
            return otpOutVO;
        } finally {
        if (oimClient != null) {
            oimClient.logout();
            oimClient = null;
            }
        }       
    }                  
}













