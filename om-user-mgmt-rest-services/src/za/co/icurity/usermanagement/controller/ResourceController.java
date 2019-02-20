package za.co.icurity.usermanagement.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import za.co.icurity.usermanagement.httpstatus.HttpStatusCode;
import za.co.icurity.usermanagement.service.UserManagerService;
import za.co.icurity.usermanagement.vo.ChangePasswordVO;
import za.co.icurity.usermanagement.vo.CheckExistingCellphoneOutVO;
import za.co.icurity.usermanagement.vo.CheckExistingEmployeeNoOutVO;
import za.co.icurity.usermanagement.vo.CheckExistingSSAIDOutVO;
import za.co.icurity.usermanagement.vo.CheckExistingUserInVO;
import za.co.icurity.usermanagement.vo.CheckExistingUserOutVO;
import za.co.icurity.usermanagement.vo.CheckExistingUsernameOutVO;
import za.co.icurity.usermanagement.vo.DisableUserRequestInVO;
import za.co.icurity.usermanagement.vo.FetchUsernameInVO;
import za.co.icurity.usermanagement.vo.FetchUsernameOutVO;
import za.co.icurity.usermanagement.vo.GenerateTempPasswordInVO;
import za.co.icurity.usermanagement.vo.GenerateTempPasswordOutVO;
import za.co.icurity.usermanagement.vo.GetADUsernameInVO;
import za.co.icurity.usermanagement.vo.GetADUsernameOutVO;
import za.co.icurity.usermanagement.vo.GetUserAccountLockedOutVO;
import za.co.icurity.usermanagement.vo.ProvisionUserAccountVO;
import za.co.icurity.usermanagement.vo.RoleDetailsVO;
import za.co.icurity.usermanagement.vo.RoleListVO;
import za.co.icurity.usermanagement.vo.StatusOutVO;
import za.co.icurity.usermanagement.vo.UpdateUserInVO;
import za.co.icurity.usermanagement.vo.UserDetailsVO;
import za.co.icurity.usermanagement.vo.UserInVO;
import za.co.icurity.usermanagement.vo.UserOutVO;
import za.co.icurity.usermanagement.vo.UserRoleGrantVO;
import za.co.icurity.usermanagement.vo.UserStatusVO;
import za.co.icurity.usermanagement.vo.ValidatePasswordVO;
import za.co.icurity.usermanagement.vo.ADSecurityAnswersOutVO;
import za.co.icurity.usermanagement.vo.AdminRoleListVO;
import za.co.icurity.usermanagement.vo.ChangePasswordForLoggedInUserVO;
import za.co.icurity.usermanagement.vo.CheckExistingIDNumberInVO;
import za.co.icurity.usermanagement.vo.CheckExistingIDNumberOutVO;
import za.co.icurity.usermanagement.vo.FindUserAndUserDetailsOutVO;
import za.co.icurity.usermanagement.vo.NotitifcationPreferenceVO;
import za.co.icurity.usermanagement.vo.OTPFailCounterVO;
import za.co.icurity.usermanagement.vo.OTPOutVO;
import za.co.icurity.usermanagement.vo.UserPreferenceDetailsVO;
import za.co.icurity.usermanagement.vo.UserRoleListAssignVO;


/**
 * @author
 *
 */
@RestController
@RequestMapping("/omuser")
public class ResourceController {

    UserManagerService userManagerService = new UserManagerService();

    @RequestMapping(value = "userstatus", method = RequestMethod.GET, headers = "Accept=application/json")
    public ResponseEntity<UserStatusVO> userstatus(@RequestParam("username")
        String username) throws Exception {
        HttpStatusCode httpStatusCodes = new HttpStatusCode();
        UserStatusVO userStatusVO;
        userStatusVO = userManagerService.userstatus(username);
        return new ResponseEntity(userStatusVO, httpStatusCodes.getHttpStatus(userStatusVO.getStatus(), userStatusVO.getErrorMessage()));
    }

    /**
     * @param username
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "fetchUserRoles", method = RequestMethod.GET, headers = "Accept=application/json")
    public ResponseEntity<RoleListVO> fetchUserRoles(@RequestParam("username")
        String username) throws Exception {
        StatusOutVO statusOutVO = new StatusOutVO();
        HttpStatus httpStatus;
        HttpStatusCode httpStatusCode;
        httpStatusCode = new HttpStatusCode();
        RoleListVO roleListVO = userManagerService.fetchUserRoles(username);
        return new ResponseEntity(roleListVO, httpStatusCode.getHttpStatus(roleListVO.getStatus(), roleListVO.getErrorMessage()));
    }

    /**
     * @param userInVO
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/createUser", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<UserOutVO> createUser(@RequestBody
        UserInVO userInVO) throws Exception {
        HttpStatusCode httpStatusCode = new HttpStatusCode();
        UserOutVO userOutVO = userManagerService.createUser(userInVO);
        return new ResponseEntity(userOutVO, httpStatusCode.getHttpStatus(userOutVO.getStatus(), userOutVO.getErrorMessage()));
    }
    /**
     * @param userInVO
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/updateuser", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<UserOutVO> updateUser(@RequestBody
        UpdateUserInVO updateUserInVO) throws Exception {
        HttpStatusCode httpStatusCode = new HttpStatusCode();
        UserOutVO userOutVO = userManagerService.updateUser(updateUserInVO);
        return new ResponseEntity(userOutVO, httpStatusCode.getHttpStatus(userOutVO.getStatus(), userOutVO.getErrorMessage()));
    }

    @RequestMapping(value = "/getADUsername", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<GetADUsernameOutVO> getADUsername(@RequestBody
        GetADUsernameInVO getADUsernameInVO) throws Exception {
        System.out.println("getADUsernameOutVO " + getADUsernameInVO.getEmailAddress());
        GetADUsernameOutVO getADUsernameOutVO = userManagerService.getADUsername(getADUsernameInVO);
        System.out.println("getADUsernameOutVO " + getADUsernameOutVO.getFirstName());
        System.out.println("getADUsernameOutVO " + getADUsernameOutVO.getStatus());
        HttpStatusCode httpStatusCode = new HttpStatusCode();
        return new ResponseEntity(getADUsernameOutVO, httpStatusCode.getHttpStatus(getADUsernameOutVO.getStatus(), getADUsernameOutVO.getErrorMessage()));
    }

    /**
     * @param username
     * @return
     */
    @RequestMapping(value = "checkExistingUserName", method = RequestMethod.GET, headers = "Accept=application/json")
    public ResponseEntity<CheckExistingUsernameOutVO> checkExistingUserName(@RequestParam("username")
        String username) {
        CheckExistingUsernameOutVO checkExistingUsernameOutVO = userManagerService.checkExistingUserName(username);
        HttpStatusCode httpStatusCode = new HttpStatusCode();
        return new ResponseEntity(checkExistingUsernameOutVO, httpStatusCode.getHttpStatus(checkExistingUsernameOutVO.getStatus(), checkExistingUsernameOutVO.getErrorMessage()));
    }

    /**
     * @param employeeNumber
     * @return
     */
    @RequestMapping(value = "checkExistingEmployeeNumber", method = RequestMethod.GET, headers = "Accept=application/json")
    public ResponseEntity<CheckExistingEmployeeNoOutVO> checkExistingEmployeeNumber(@RequestParam("employeeNumber")
        String employeeNumber) {
        HttpStatusCode httpStatusCode = new HttpStatusCode();
        CheckExistingEmployeeNoOutVO checkExistingEmployeeNoOutVO = userManagerService.checkExistingEmployeeNumber(employeeNumber);
        return new ResponseEntity(checkExistingEmployeeNoOutVO, httpStatusCode.getHttpStatus(checkExistingEmployeeNoOutVO.getStatus(), checkExistingEmployeeNoOutVO.getErrorMessage()));
    }

    @RequestMapping(value = "checkExistingCellphone", method = RequestMethod.GET, headers = "Accept=application/json")
    public ResponseEntity<CheckExistingCellphoneOutVO> checkExistingCellphone(@RequestParam("cellNumber")
        String cellNumber) {
        HttpStatusCode httpStatusCode = new HttpStatusCode();
        CheckExistingCellphoneOutVO checkExistingCellphoneOutVO = userManagerService.checkExistingCellphone(cellNumber);
        return new ResponseEntity(checkExistingCellphoneOutVO, httpStatusCode.getHttpStatus(checkExistingCellphoneOutVO.getStatus(), checkExistingCellphoneOutVO.getErrorMessage()));
    }

    @RequestMapping(value = "getUserId", method = RequestMethod.GET, headers = "Accept=application/json")
    public ResponseEntity<UserOutVO> getUserId(@RequestParam("username")
        String username) {
        HttpStatusCode httpStatusCode = new HttpStatusCode();
        UserOutVO userOutVO = userManagerService.getUserId(username);
        return new ResponseEntity(userOutVO, httpStatusCode.getHttpStatus(userOutVO.getStatus(), userOutVO.getErrorMessage()));
    }

    /**
     * @param checkExistingUserInVO
     * @return
     */
    @RequestMapping(value = "checkExistingUser", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<CheckExistingUserOutVO> checkExistingUser(@RequestBody
        CheckExistingUserInVO checkExistingUserInVO) {
        CheckExistingUserOutVO checkExistingUserOutVO = userManagerService.checkExistingUser(checkExistingUserInVO);
        System.out.println("test 1" + checkExistingUserOutVO.getStatus());
        System.out.println("test 2" + checkExistingUserOutVO.getErrorMessage());
        System.out.println("test 3" + checkExistingUserOutVO.getUserExists());
        HttpStatusCode httpStatusCode = new HttpStatusCode();
        return new ResponseEntity(checkExistingUserOutVO, httpStatusCode.getHttpStatus(checkExistingUserOutVO.getStatus(), checkExistingUserOutVO.getErrorMessage()));
    }

    @RequestMapping(value = "getUsername", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<FetchUsernameOutVO> getUsername(@RequestBody
        FetchUsernameInVO fetchUsernameInVO) {
        FetchUsernameOutVO fetchUsernameOutVO = userManagerService.getUsername(fetchUsernameInVO);
        HttpStatusCode httpStatusCode = new HttpStatusCode();
        return new ResponseEntity(fetchUsernameOutVO, httpStatusCode.getHttpStatus(fetchUsernameOutVO.getStatus(), fetchUsernameOutVO.getErrorMessage()));
    }

    /**
     * @param ssaid
     * @return
     */
    @RequestMapping(value = "checkExistingSSAID", method = RequestMethod.GET, headers = "Accept=application/json")
    public ResponseEntity<CheckExistingSSAIDOutVO> checkExistingSSAID(@RequestParam("ssaid")
        String ssaid) {
        CheckExistingSSAIDOutVO checkExistingSSAIDOutVO = userManagerService.checkExistingSSAID(ssaid);
        HttpStatusCode httpStatusCode = new HttpStatusCode();
        return new ResponseEntity(checkExistingSSAIDOutVO, httpStatusCode.getHttpStatus(checkExistingSSAIDOutVO.getStatus(), checkExistingSSAIDOutVO.getErrorMessage()));
    }

    /**
     * @param username
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "userdetails", method = RequestMethod.GET, headers = "Accept=application/json")
    public ResponseEntity<UserDetailsVO> getUserDetails(@RequestParam("username")
        String username) throws Exception {
        UserDetailsVO userDetailsVO = userManagerService.getUserDetails(username);
        HttpStatusCode httpStatusCode = new HttpStatusCode();
        return new ResponseEntity(userDetailsVO, httpStatusCode.getHttpStatus(userDetailsVO.getStatus(), userDetailsVO.getErrorMessage()));
    }

    /**
     * @param username
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "getUserAccountLocked", method = RequestMethod.GET, headers = "Accept=application/json")
    public ResponseEntity<GetUserAccountLockedOutVO> getUserAccountLocked(@RequestParam("username")
        String username) throws Exception {
        GetUserAccountLockedOutVO getUserAccountLockedOutVO = userManagerService.getUserAccountLocked(username);
        HttpStatusCode httpStatusCode = new HttpStatusCode();
        return new ResponseEntity(getUserAccountLockedOutVO, httpStatusCode.getHttpStatus(getUserAccountLockedOutVO.getStatus(), getUserAccountLockedOutVO.getErrorMessage()));
    }
    
    @RequestMapping(value = "getRoleDetails", method = RequestMethod.GET, headers = "Accept=application/json")
    public ResponseEntity<RoleDetailsVO> getRoleDetails(@RequestParam("rolename")
        String rolename) throws Exception {
        RoleDetailsVO roleDetailsVO = userManagerService.getRoleDetails(rolename);
        HttpStatusCode httpStatusCode = new HttpStatusCode();
        return new ResponseEntity(roleDetailsVO, httpStatusCode.getHttpStatus(roleDetailsVO.getStatus(), roleDetailsVO.getErrorMessage()));
    }
    
    @RequestMapping(value = "provisionUser", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<StatusOutVO> provisionUser(@RequestBody
        ProvisionUserAccountVO provisionUserAccountVO) {
        StatusOutVO statusOutVO = userManagerService.provisionUser(provisionUserAccountVO);
        HttpStatusCode httpStatusCode = new HttpStatusCode();
        return new ResponseEntity(statusOutVO, httpStatusCode.getHttpStatus(statusOutVO.getStatus(), statusOutVO.getErrorMessage()));
    }
    
    /**
     * @param userRoleGrantVO
     * @return
     */
    @RequestMapping(value = "assignRole", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<StatusOutVO> assignRole(@RequestBody
        UserRoleGrantVO userRoleGrantVO) {
        StatusOutVO statusOutVO = userManagerService.assignRole(userRoleGrantVO);
        HttpStatusCode httpStatusCode = new HttpStatusCode();
        return new ResponseEntity(statusOutVO, httpStatusCode.getHttpStatus(statusOutVO.getStatus(), statusOutVO.getErrorMessage()));
    }
 
    @RequestMapping(value = "lockUser", method = RequestMethod.GET, headers = "Accept=application/json")
    public ResponseEntity<StatusOutVO> lockUser(@RequestParam("username")
        String username) {
        StatusOutVO statusOutVO = userManagerService.lockUser(username);
        HttpStatusCode httpStatusCode = new HttpStatusCode();
        return new ResponseEntity(statusOutVO, httpStatusCode.getHttpStatus(statusOutVO.getStatus(), statusOutVO.getErrorMessage()));
    }
    
    @RequestMapping(value = "unlockAccount", method = RequestMethod.GET, headers = "Accept=application/json")
    public ResponseEntity<StatusOutVO> unlockAccount(@RequestParam("username")
        String username) {
        StatusOutVO statusOutVO = userManagerService.unlockAccount(username);
        HttpStatusCode httpStatusCode = new HttpStatusCode();
        return new ResponseEntity(statusOutVO, httpStatusCode.getHttpStatus(statusOutVO.getStatus(), statusOutVO.getErrorMessage()));
    }
    
    @RequestMapping(value = "validatePassword", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<StatusOutVO> validatePassword(@RequestBody
        ValidatePasswordVO passwordVO) {
        StatusOutVO statusOutVO = userManagerService.validatePassword(passwordVO);
        HttpStatusCode httpStatusCode = new HttpStatusCode();
        return new ResponseEntity(statusOutVO, httpStatusCode.getHttpStatus(statusOutVO.getStatus(), statusOutVO.getErrorMessage()));
    }
    
    @RequestMapping(value = "changePassword", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<StatusOutVO> changePassword(@RequestBody
        ChangePasswordVO passwordVO) {
        StatusOutVO statusOutVO = userManagerService.changePassword(passwordVO);
        HttpStatusCode httpStatusCode = new HttpStatusCode();
        return new ResponseEntity(statusOutVO, httpStatusCode.getHttpStatus(statusOutVO.getStatus(), statusOutVO.getErrorMessage()));
    }
    
    @RequestMapping(value = "/disableUser", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<StatusOutVO> disableUser(@RequestBody
        DisableUserRequestInVO disableUserRequestInVO) {
        StatusOutVO statusOutVO = userManagerService.disableUser(disableUserRequestInVO);
        HttpStatusCode httpStatusCode = new HttpStatusCode();
        return new ResponseEntity(statusOutVO, httpStatusCode.getHttpStatus(statusOutVO.getStatus(), statusOutVO.getErrorMessage()));
    }
    
    @RequestMapping(value = "/generateTempPassword", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<GenerateTempPasswordOutVO> generateTempPassword(@RequestBody
        GenerateTempPasswordInVO generateTempPasswordInVO) {
        GenerateTempPasswordOutVO generateTempPasswordOutVO = userManagerService.generateTempPassword(generateTempPasswordInVO);
        HttpStatusCode httpStatusCode = new HttpStatusCode();
        return new ResponseEntity(generateTempPasswordOutVO, httpStatusCode.getHttpStatus(generateTempPasswordOutVO.getStatus(), generateTempPasswordOutVO.getErrorMessage()));
    }
    
    
    @RequestMapping(value = "/findUserAndGetDetails", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<FindUserAndUserDetailsOutVO> findUserAndGetDetails(@RequestBody
        CheckExistingUserInVO checkExistingUserInVO) {
        FindUserAndUserDetailsOutVO findUserAndUserDetailsOutVO = userManagerService.findUserAndGetDetails(checkExistingUserInVO);
        HttpStatusCode httpStatusCode = new HttpStatusCode();
        return new ResponseEntity(findUserAndUserDetailsOutVO, httpStatusCode.getHttpStatus(findUserAndUserDetailsOutVO.getStatus(), findUserAndUserDetailsOutVO.getErrorMessage()));
    }
    
    @RequestMapping(value = "/checkExistingIDNumber", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<CheckExistingIDNumberOutVO> checkExistingIDNumber(@RequestBody
        CheckExistingIDNumberInVO checkExistingIDNumberInVO) {
        CheckExistingIDNumberOutVO checkExistingIDNumberOutVO = userManagerService.checkExistingIDNumber(checkExistingIDNumberInVO);
        HttpStatusCode httpStatusCode = new HttpStatusCode();
        return new ResponseEntity(checkExistingIDNumberOutVO, httpStatusCode.getHttpStatus(checkExistingIDNumberOutVO.getStatus(), checkExistingIDNumberOutVO.getErrorMessage()));
    }
    
    /**
     * @param username
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "validateADSecurityAnswers", method = RequestMethod.GET, headers = "Accept=application/json")
    public ResponseEntity<RoleListVO> validateADSecurityAnswers(@RequestParam("username")
        String username) throws Exception {
        ADSecurityAnswersOutVO adSecurityAnswersOutVO = new ADSecurityAnswersOutVO();
        HttpStatus httpStatus;
        HttpStatusCode httpStatusCode;
        httpStatusCode = new HttpStatusCode();
         adSecurityAnswersOutVO = userManagerService.validateADSecurityAnswers(username);
        return new ResponseEntity(adSecurityAnswersOutVO, httpStatusCode.getHttpStatus(adSecurityAnswersOutVO.getStatus(), adSecurityAnswersOutVO.getErrorMessage()));
    }      
    /**
     * @param userRolerevokeVO
     * @return
     * @param revokeRoleGrantVO
     */
    @RequestMapping(value = "revokeRole", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<StatusOutVO> revokeRole(@RequestBody
        UserRoleGrantVO userRolerevokeVO) {
        StatusOutVO statusOutVO = userManagerService.revokeRole(userRolerevokeVO);
        HttpStatusCode httpStatusCode = new HttpStatusCode();
        return new ResponseEntity(statusOutVO, httpStatusCode.getHttpStatus(statusOutVO.getStatus(), statusOutVO.getErrorMessage()));
    }    
    /**
     * @param userRolegrantVO
     * @return
     * @param revokeRoleGrantVO
     */
    @RequestMapping(value = "revokeRoles", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<StatusOutVO> revokeRoles(@RequestBody
        UserRoleListAssignVO userRolegrantVO) {
        StatusOutVO statusOutVO = userManagerService.revokeRoles(userRolegrantVO);
        HttpStatusCode httpStatusCode = new HttpStatusCode();
        return new ResponseEntity(statusOutVO, httpStatusCode.getHttpStatus(statusOutVO.getStatus(), statusOutVO.getErrorMessage()));
    }    
    /**
     * @param userRolegrantVO
     * @return
     * @param userRoleGrantVO
     */
    @RequestMapping(value = "assignRoles", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<StatusOutVO> assignRoles(@RequestBody
        UserRoleListAssignVO userRolegrantVO) {
        StatusOutVO statusOutVO = userManagerService.assignRoles(userRolegrantVO);
        HttpStatusCode httpStatusCode = new HttpStatusCode();
        return new ResponseEntity(statusOutVO, httpStatusCode.getHttpStatus(statusOutVO.getStatus(), statusOutVO.getErrorMessage()));
    }  
    /**
     * @param notitifcationPreferenceVO
     * @return
     * @param updateNotitifcationPreference
     */
    @RequestMapping(value = "updateNotitifcationPreference", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<StatusOutVO> updateNotitifcationPreference(@RequestBody
        NotitifcationPreferenceVO notitifcationPreferenceVO) {
        StatusOutVO statusOutVO = userManagerService.updateNotitifcationPreference(notitifcationPreferenceVO);
        HttpStatusCode httpStatusCode = new HttpStatusCode();
        return new ResponseEntity(statusOutVO, httpStatusCode.getHttpStatus(statusOutVO.getStatus(), statusOutVO.getErrorMessage()));
    }            
    @RequestMapping(value = "changePasswordForLoggedInUser", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<StatusOutVO> changePasswordForLoggedInUser(@RequestBody
        ChangePasswordForLoggedInUserVO changePasswordForLoggedInUserVO) {
        StatusOutVO statusOutVO = userManagerService.changePasswordForLoggedInUser(changePasswordForLoggedInUserVO);
        HttpStatusCode httpStatusCode = new HttpStatusCode();
        return new ResponseEntity(statusOutVO, httpStatusCode.getHttpStatus(statusOutVO.getStatus(), statusOutVO.getErrorMessage()));
    }    
    /**
     * @param username
     * @return
     * @throws Exception
     */ 
   @RequestMapping(value = "getDelegatedAuthAdminRole", method = RequestMethod.GET, headers = "Accept=application/json")
    public ResponseEntity<AdminRoleListVO> getDelegatedAuthAdminRole(@RequestParam("username")  String username){
       AdminRoleListVO adminRoleListVO = userManagerService.getDelegatedAuthAdminRole(username);
       HttpStatusCode httpStatusCode = new HttpStatusCode();
       return new ResponseEntity(adminRoleListVO, httpStatusCode.getHttpStatus(adminRoleListVO.getStatus(), adminRoleListVO.getErrorMessage()));
   }    
    /**
     * @param username
     * @return
     * @throws Exception
     */ 
    @RequestMapping(value = "getTransferrableSiteRolesOfUser", method = RequestMethod.GET, headers = "Accept=application/json")
    public ResponseEntity<RoleListVO> getTransferrableSiteRolesOfUser(@RequestParam("username")  String username){
       RoleListVO roleListVO = userManagerService.getTransferrableSiteRolesOfUser(username);
       HttpStatusCode httpStatusCode = new HttpStatusCode();
       return new ResponseEntity(roleListVO, httpStatusCode.getHttpStatus(roleListVO.getStatus(), roleListVO.getErrorMessage()));
    }    
    /**
     * @param username
     * @return
     * @throws Exception
     */ 
    @RequestMapping(value = "getUserDetailsToNotify", method = RequestMethod.GET, headers = "Accept=application/json")
    public ResponseEntity<UserPreferenceDetailsVO> getUserDetailsToNotify(@RequestParam("username")  String username){
       UserPreferenceDetailsVO userPreferenceDetailsVO = userManagerService.getUserDetailsToNotify(username);
       HttpStatusCode httpStatusCode = new HttpStatusCode();
       return new ResponseEntity(userPreferenceDetailsVO, httpStatusCode.getHttpStatus(userPreferenceDetailsVO.getStatus(), userPreferenceDetailsVO.getErrorMessage()));
    }    
    @RequestMapping(value = "updateOTPFailureCounter", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<OTPOutVO> updateOTPFailureCounter(@RequestBody
        OTPFailCounterVO otpFailCounterVO) {
        OTPOutVO otpOutVO = userManagerService.updateOTPFailureCounter(otpFailCounterVO);
        HttpStatusCode httpStatusCode = new HttpStatusCode();
        return new ResponseEntity(otpOutVO, httpStatusCode.getHttpStatus(otpOutVO.getStatus(), otpOutVO.getErrorMessage()));
    }    
}

