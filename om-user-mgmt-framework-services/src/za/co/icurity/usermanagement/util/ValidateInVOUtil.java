package za.co.icurity.usermanagement.util;

import java.util.logging.Level;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import za.co.icurity.usermanagement.vo.ADPropertiesVO;
import za.co.icurity.usermanagement.vo.ChangePasswordForLoggedInUserVO;
import za.co.icurity.usermanagement.vo.ChangePasswordVO;
import za.co.icurity.usermanagement.vo.CheckExistingIDNumberInVO;
import za.co.icurity.usermanagement.vo.CheckExistingIDNumberOutVO;
import za.co.icurity.usermanagement.vo.CheckExistingUserInVO;
import za.co.icurity.usermanagement.vo.DisableUserRequestInVO;
import za.co.icurity.usermanagement.vo.FetchUsernameInVO;
import za.co.icurity.usermanagement.vo.FetchUsernameOutVO;
import za.co.icurity.usermanagement.vo.FindUserAndUserDetailsOutVO;
import za.co.icurity.usermanagement.vo.GenerateTempPasswordInVO;
import za.co.icurity.usermanagement.vo.GenerateTempPasswordOutVO;
import za.co.icurity.usermanagement.vo.GetADUsernameInVO;
import za.co.icurity.usermanagement.vo.GetADUsernameOutVO;
import za.co.icurity.usermanagement.vo.ProvisionUserAccountVO;
import za.co.icurity.usermanagement.vo.StatusOutVO;
import za.co.icurity.usermanagement.vo.UserInVO;
import za.co.icurity.usermanagement.vo.UserOutVO;

public class ValidateInVOUtil {
    public ValidateInVOUtil() {
        super();
    }
    private static final Logger LOG = LoggerFactory.getLogger(ValidateInVOUtil.class);

    /**
     * @param fetchUsernameInVO
     * @return
     */
    public static FetchUsernameOutVO validateFetchUsernameInVO(FetchUsernameInVO fetchUsernameInVO) {
        FetchUsernameOutVO fetchUsernameOutVO = new FetchUsernameOutVO();
        fetchUsernameOutVO.setStatus("");
        StringBuffer buffer = new StringBuffer();
        if (fetchUsernameInVO.getCellphone() == null || fetchUsernameInVO.getCellphone().trim().isEmpty()) {
            buffer.append("Mandatory field : Cellphone number" + System.getProperty("line.separator"));
        }
        if (fetchUsernameInVO.getLastName() == null || fetchUsernameInVO.getLastName().trim().isEmpty()) {
            buffer.append("Mandatory field : Last name");
        }
        if (buffer != null && buffer.length() > 0) {
            fetchUsernameOutVO.setErrorMessage(buffer.toString());

            if (buffer.length() > 0) {
                fetchUsernameOutVO.setStatus("Error");
                fetchUsernameOutVO.setErrorMessage(buffer.toString());
                return fetchUsernameOutVO;
            }

            return fetchUsernameOutVO;

        }
        return fetchUsernameOutVO;
    }

    public static StatusOutVO validateProvisionUserAccountVOUtil(ProvisionUserAccountVO accountVO) {
        StringBuffer buffer = new StringBuffer();
        PropertiesUtil propertiesUtil = new PropertiesUtil();
        String appInstanceName = null;
        String adServerKey = null;
        String adOrganizationName = null;
        StatusOutVO statusOutVO = new StatusOutVO();
        statusOutVO.setStatus("");
        try {
            ADPropertiesVO adPropertiesVO = propertiesUtil.getADProperties();
            if (accountVO == null || accountVO.getUsername() == null) {
                buffer.append("Mandatory field : username" + System.getProperty("line.separator"));
            }
            if (accountVO == null || accountVO.getEmployeeNumber() == null) {
                buffer.append("Mandatory field : Employee Number" + System.getProperty("line.separator"));
            }
            if (accountVO == null || accountVO.getUserKey() == null) {
                buffer.append("Mandatory field : User Key" + System.getProperty("line.separator"));
            }
            if (adPropertiesVO.getAdInstanceName() == null || adPropertiesVO.getAdServerKey() == null ||
                adPropertiesVO.getAdOrganizationName() == null) {
                buffer.append("Invalid Active Directory Server properties" + System.getProperty("line.separator"));
            }
            if (buffer != null && buffer.length() > 0) {
                statusOutVO.setStatus("Error");
                statusOutVO.setErrorMessage(buffer.toString());
                // LOG.error(this + " provisionUser\", \"Validation Error: " + buffer.toString());
                return statusOutVO;
            }
        } catch (Exception e) {
            return statusOutVO;
        }
        return statusOutVO;
    }

    /**
     * @param checkExistingUserInVO
     * @return
     */
    public static GetADUsernameOutVO validateADUsernameInVO(GetADUsernameInVO getADUsernameInVO) {

        StringBuffer buffer;
        buffer = new StringBuffer();
        GetADUsernameOutVO getADUsernameOutVO = new GetADUsernameOutVO();
        getADUsernameOutVO.setStatus("");
        if (getADUsernameInVO.getSurname() == null || getADUsernameInVO.getSurname().trim().isEmpty()) {
            buffer.append("Mandatory field :  Surname" + System.getProperty("line.separator"));
        }
        if (getADUsernameInVO.getIdNumber() == null || getADUsernameInVO.getIdNumber().trim().isEmpty()) {
            buffer.append("Mandatory field : ID Number" + System.getProperty("line.separator"));
        }
        if (getADUsernameInVO.getIdType() == null || getADUsernameInVO.getIdType().trim().isEmpty()) {
            buffer.append("Mandatory field : ID Type" + System.getProperty("line.separator"));
        }
        if (getADUsernameInVO.getEmailAddress() == null || getADUsernameInVO.getEmailAddress().trim().isEmpty()) {
            buffer.append("Mandatory field : Email Address" + System.getProperty("line.separator"));
        }

        if (buffer.length() > 0) {
            getADUsernameOutVO.setStatus("Error");
            getADUsernameOutVO.setErrorMessage(buffer.toString());
            return getADUsernameOutVO;
        }

        return getADUsernameOutVO;

    }


    public static StatusOutVO validateLockUser(ADPropertiesVO adPropertiesVO, String username) {
        StatusOutVO statusOutVO = new StatusOutVO();
        StringBuffer buffer = new StringBuffer();
        statusOutVO.setStatus("");
        if (username == null || username.trim().isEmpty()) {
            buffer.append("Mandatory field : Username" + System.getProperty("line.separator"));
        }
        if (adPropertiesVO.getAdInstanceName() == null) {
            buffer.append("Invalid Active Directory Server properties" + System.getProperty("line.separator"));
        }
        if (buffer != null && buffer.length() > 0) {
            statusOutVO.setStatus("Error");
            statusOutVO.setErrorMessage(buffer.toString());
            return statusOutVO;
        }
        return statusOutVO;
    }

    public static StatusOutVO validateUnLockUser(ADPropertiesVO adPropertiesVO, String username) {
        StatusOutVO statusOutVO = new StatusOutVO();
        StringBuffer buffer = new StringBuffer();
        statusOutVO.setStatus("");
        // Mandatory fields validation
        if (username == null || username.trim().isEmpty()) {
            buffer.append("Mandatory field : Username" + System.getProperty("line.separator"));
        }

        if (adPropertiesVO.getAdInstanceName() == null) {
            buffer.append("Invalid Active Directory Server properties" + System.getProperty("line.separator"));
        }
        if (buffer != null && buffer.length() > 0) {
            statusOutVO.setStatus("Error");
            statusOutVO.setErrorMessage(buffer.toString());
            return statusOutVO;
        }
        return statusOutVO;
    }


    public static StatusOutVO validateChangePasswordVO(ChangePasswordVO passwordVO) {
        StatusOutVO statusOutVO = new StatusOutVO();
        statusOutVO.setStatus("");
        StringBuffer buffer = new StringBuffer();
        if (passwordVO.getUsername() == null || passwordVO.getUsername().trim().isEmpty()) {
            buffer.append("Mandatory field : User Name" + System.getProperty("line.separator"));
        }
        if (passwordVO.getChangePasswordAtNextLogin() == null ||
            passwordVO.getChangePasswordAtNextLogin().trim().isEmpty()) {
            buffer.append("Mandatory field : Change password at next login" + System.getProperty("line.separator"));
        }
        if (buffer != null && buffer.length() > 0) {
            statusOutVO.setStatus("Error");
            statusOutVO.setErrorMessage(buffer.toString());

            return statusOutVO;
        }
        return statusOutVO;
    }
    
    public static StatusOutVO validateChangePasswordForLoggedInUserVO(ChangePasswordForLoggedInUserVO changePasswordForLoggedInUserVO) {
        
        StatusOutVO statusOutVO = new StatusOutVO();
        DecryptPasswordUtil decryptPasswordUtil = new DecryptPasswordUtil();
        StringBuffer buffer = new StringBuffer();
        if (changePasswordForLoggedInUserVO != null) {
            if (changePasswordForLoggedInUserVO.getUsername() == null || changePasswordForLoggedInUserVO.getUsername().trim().isEmpty()) {
                buffer.append("Mandatory field : Username" + System.getProperty("line.separator"));
            }
            if (changePasswordForLoggedInUserVO.getOldPassword() == null || changePasswordForLoggedInUserVO.getOldPassword().trim().isEmpty()) {
                buffer.append("Mandatory field : Old Password" + System.getProperty("line.separator"));
            } else {
                //Added for Encrypted password to Decrypt
                changePasswordForLoggedInUserVO.setOldPassword(decryptPasswordUtil.decrypt(changePasswordForLoggedInUserVO.getOldPassword()));
            }
            if (changePasswordForLoggedInUserVO.getNewPassword() == null || changePasswordForLoggedInUserVO.getNewPassword().trim().isEmpty()) {
                buffer.append("Mandatory field : New Password" + System.getProperty("line.separator"));
            } else {
                //Added for Encrypted password to Decrypt
                changePasswordForLoggedInUserVO.setNewPassword(decryptPasswordUtil.decrypt(changePasswordForLoggedInUserVO.getNewPassword()));
            }
            if (buffer != null && buffer.length() > 0) {
                statusOutVO.setStatus("Error");
                statusOutVO.setErrorMessage(buffer.toString());            
                LOG.info("changePasswordForLoggedInUser", "Validation Error: " + buffer.toString());
                return statusOutVO;
            }
            //return statusOutVO;
        }
        
        return statusOutVO;
    }

    public static StatusOutVO validateDisableUserUtil(DisableUserRequestInVO disableUserRequestInVO) {
        StatusOutVO statusOutVO = new StatusOutVO();
        statusOutVO.setStatus("");
        StringBuffer buffer = new StringBuffer();
        if (disableUserRequestInVO.getUsername() == null || disableUserRequestInVO.getUsername().trim().isEmpty()) {
            buffer.append("Mandatory field : Username" + System.getProperty("line.separator"));
        }
        if (disableUserRequestInVO.getJustification() == null ||
            disableUserRequestInVO.getJustification().trim().isEmpty()) {
            buffer.append("Mandatory field : Justification" + System.getProperty("line.separator"));
        }
        if (disableUserRequestInVO.getEffectiveDate() == null ||
            disableUserRequestInVO.getEffectiveDate().trim().isEmpty()) {
            buffer.append("Mandatory field : Effective Date" + System.getProperty("line.separator"));
        }
        if (buffer != null && buffer.length() > 0) {
            statusOutVO.setStatus("Error");
            statusOutVO.setErrorMessage(buffer.toString());
            return statusOutVO;
        }

        // Date format validation
        if (disableUserRequestInVO.getEffectiveDate() == null ||
            !disableUserRequestInVO.getEffectiveDate().matches("^(1[0-2]|0[1-9])/(3[01]|[12][0-9]|0[1-9])/[0-9]{4}$")) {
            statusOutVO.setStatus("Error");
            statusOutVO.setErrorMessage("Please enter date in format MM/dd/yyyy");
            return statusOutVO;
        }
        return statusOutVO;
    }

    /**
     * validates the user input.
     * @param createUserVO
     * @return
     */
    public static UserOutVO validateUserInVO(UserInVO createUserVO) {
        UserOutVO userOutVO = new UserOutVO();
        userOutVO.setStatus("");
        StringBuffer buffer = new StringBuffer();
        if (createUserVO == null || createUserVO.getLastName() == null ||
            createUserVO.getLastName().trim().isEmpty() || createUserVO.getPassword() == null ||
            createUserVO.getPassword().trim().isEmpty() || createUserVO.getUsername() == null ||
            createUserVO.getUsername().trim().isEmpty() || createUserVO.getDateOfBirth() == null ||
            createUserVO.getDateOfBirth().trim().isEmpty() || createUserVO.getIdNumber() == null ||
            createUserVO.getIdNumber().trim().isEmpty()) {
            userOutVO.setStatus("Error");
            if (createUserVO.getLastName() == null || createUserVO.getLastName().trim().isEmpty()) {
                buffer.append("Mandatory field : Last Name" + System.getProperty("line.separator"));
            }
            if (createUserVO.getPassword() == null || createUserVO.getPassword().trim().isEmpty()) {
                buffer.append("Mandatory field : Password" + System.getProperty("line.separator"));
            }
            if (createUserVO.getUsername() == null || createUserVO.getUsername().trim().isEmpty()) {
                buffer.append("Mandatory field : Username" + System.getProperty("line.separator"));
            }
            if (createUserVO.getDateOfBirth() == null || createUserVO.getDateOfBirth().trim().isEmpty()) {
                buffer.append("Mandatory field : Date of Birth" + System.getProperty("line.separator"));
            }
            if (createUserVO.getIdNumber() == null || createUserVO.getIdNumber().trim().isEmpty()) {
                buffer.append("Mandatory field : ID Number" + System.getProperty("line.separator"));
            }
            userOutVO.setErrorMessage(buffer.toString());
            return userOutVO;
        } else {
            if (createUserVO.getDateOfBirth() == null ||
                !createUserVO.getDateOfBirth().matches("^(3[01]|[12][0-9]|0[1-9])/(1[0-2]|0[1-9])/[0-9]{4}$")) {
                userOutVO.setStatus("Error");
                buffer.append("Please enter date in format dd/MM/yyyy" + System.getProperty("line.separator"));
                userOutVO.setErrorMessage(buffer.toString());
                return userOutVO;
            }
        }

        return userOutVO;

    }

    /**
     * @param checkExistingUserInVO
     * @return
     */
    public static UserOutVO validateCheckExistingUserInVO(CheckExistingUserInVO checkExistingUserInVO) {
        UserOutVO userOutVO = new UserOutVO();
        userOutVO.setStatus("");
        StringBuffer buffer = new StringBuffer();
        if (checkExistingUserInVO.getFirstName() == null || checkExistingUserInVO.getFirstName().trim().isEmpty()) {
            buffer.append("Mandatory field : First Name" + System.getProperty("line.separator"));
        }
        if (checkExistingUserInVO.getLastName() == null || checkExistingUserInVO.getLastName().trim().isEmpty()) {
            buffer.append("Mandatory field : Last Name" + System.getProperty("line.separator"));
        }
        if (checkExistingUserInVO.getIdNumber() == null || checkExistingUserInVO.getIdNumber().trim().isEmpty()) {
            buffer.append("Mandatory field : ID Number" + System.getProperty("line.separator"));
        }
        if (checkExistingUserInVO.getDateOfBirth() == null ||
            checkExistingUserInVO.getDateOfBirth().trim().isEmpty()) {
            buffer.append("Mandatory field : Date of Birth" + System.getProperty("line.separator"));
        }
        if (buffer.length() > 0) {
            userOutVO.setStatus("Error");
            userOutVO.setErrorMessage(buffer.toString());
            return userOutVO;
        }

        return userOutVO;

    }


    public static FindUserAndUserDetailsOutVO validateCheckExistingUserAndUserDetailsInVO(CheckExistingUserInVO checkExistingUserInVO) {
        FindUserAndUserDetailsOutVO userOutVO = new FindUserAndUserDetailsOutVO();
        userOutVO.setStatus("");
        StringBuffer buffer = new StringBuffer();
        if (checkExistingUserInVO.getFirstName() == null || checkExistingUserInVO.getFirstName().trim().isEmpty()) {
            buffer.append("Mandatory field : First Name" + System.getProperty("line.separator"));
        }
        if (checkExistingUserInVO.getLastName() == null || checkExistingUserInVO.getLastName().trim().isEmpty()) {
            buffer.append("Mandatory field : Last Name" + System.getProperty("line.separator"));
        }
        if (checkExistingUserInVO.getIdNumber() == null || checkExistingUserInVO.getIdNumber().trim().isEmpty()) {
            buffer.append("Mandatory field : ID Number" + System.getProperty("line.separator"));
        }
        if (checkExistingUserInVO.getDateOfBirth() == null ||
            checkExistingUserInVO.getDateOfBirth().trim().isEmpty()) {
            buffer.append("Mandatory field : Date of Birth" + System.getProperty("line.separator"));
        }
        if (buffer.length() > 0) {
            userOutVO.setStatus("Error");
            userOutVO.setErrorMessage(buffer.toString());
            return userOutVO;
        }

        return userOutVO;

    }

    public static CheckExistingIDNumberOutVO validateCheckExistingIDNumberInVO(CheckExistingIDNumberInVO checkExistingIDNumberInVO) {
        CheckExistingIDNumberOutVO checkExistingIDNumberOutVO = new CheckExistingIDNumberOutVO();
        checkExistingIDNumberOutVO.setStatus("");
        StringBuffer buffer = new StringBuffer();

        if (checkExistingIDNumberInVO.getIdNumber() == null ||
            checkExistingIDNumberInVO.getIdNumber().trim().isEmpty()) {
            checkExistingIDNumberOutVO.setStatus("Error");
            checkExistingIDNumberOutVO.setErrorMessage("Mandatory field : idNumber");
            return checkExistingIDNumberOutVO;
        }

        if (checkExistingIDNumberInVO.getIdType() == null || checkExistingIDNumberInVO.getIdType().trim().isEmpty()) {
            checkExistingIDNumberOutVO.setStatus("Error");
            checkExistingIDNumberOutVO.setErrorMessage("Mandatory field : idType");
            return checkExistingIDNumberOutVO;
        }

        if (checkExistingIDNumberInVO.getCountryOfIssue() == null ||
            checkExistingIDNumberInVO.getCountryOfIssue().trim().isEmpty()) {
            checkExistingIDNumberOutVO.setStatus("Error");
            checkExistingIDNumberOutVO.setErrorMessage("Mandatory field : countryOfIssue");
            return checkExistingIDNumberOutVO;
        }
        return checkExistingIDNumberOutVO;
    }

    /**
     * @param generateTempPasswordInVO
     * @return
     */
    public static GenerateTempPasswordOutVO validateTempPasswordInVO(GenerateTempPasswordInVO generateTempPasswordInVO) {
        GenerateTempPasswordOutVO generateTempPasswordOutVO = new GenerateTempPasswordOutVO();
        LOG.info(" In generateTempPasswordOutVO");
        generateTempPasswordOutVO.setStatus("");
        StringBuffer buffer = new StringBuffer();
        if (generateTempPasswordInVO.getUsername() == null ||
            generateTempPasswordInVO.getUsername().trim().isEmpty()) {
            buffer.append("Mandatory field : User Name" + System.getProperty("line.separator"));
        }
        if (generateTempPasswordInVO.getCellphoneNumber() == null ||
            generateTempPasswordInVO.getCellphoneNumber().trim().isEmpty()) {
            buffer.append("Mandatory field : Cell phone Number" + System.getProperty("line.separator"));
        }
        if (buffer.length() > 0) {
            generateTempPasswordOutVO.setStatus("Error");
            generateTempPasswordOutVO.setErrorMessage(buffer.toString());
            return generateTempPasswordOutVO;
        }
        LOG.info(" Out of generateTempPasswordOutVO");
        return generateTempPasswordOutVO;

    }

}
