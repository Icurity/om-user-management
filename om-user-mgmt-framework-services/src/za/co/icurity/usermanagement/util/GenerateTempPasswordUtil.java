package za.co.icurity.usermanagement.util;

import oracle.iam.platform.OIMClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import za.co.icurity.usermanagement.service.UserManagerService;
import za.co.icurity.usermanagement.vo.ChangePasswordVO;
import za.co.icurity.usermanagement.vo.GenerateTempPasswordInVO;
import za.co.icurity.usermanagement.vo.GenerateTempPasswordOutVO;
import za.co.icurity.usermanagement.vo.StatusOutVO;
import za.co.icurity.usermanagement.vo.UserDetailsVO;
import za.co.icurity.usermanagement.vo.ValidatePasswordVO;

public class GenerateTempPasswordUtil {
    public GenerateTempPasswordUtil() {
        super();
    }
    private static final Logger LOG = LoggerFactory.getLogger(GenerateTempPasswordUtil.class);

    public GenerateTempPasswordOutVO generateTempPassword(UserDetailsVO userDetailsVO,GenerateTempPasswordInVO generateTempPasswordInVO) {
        GenerateTempPasswordOutVO generateTempPasswordOutVO = new GenerateTempPasswordOutVO();
        UserManagerService userManagerService = new UserManagerService();
              
        if (userDetailsVO.getMigratedUserFirstLogin().equalsIgnoreCase("true") ||
            userDetailsVO.getCellPhoneNumber() == generateTempPasswordInVO.getCellphoneNumber()) {
            LOG.error("getMigratedUserFirstLogin()>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            StatusOutVO statusOutVO = new StatusOutVO();
            statusOutVO.setStatus("false");
            ValidatePasswordVO validatePasswordVO;
            String passwordReceived;
            while (true) {
                validatePasswordVO = validateRandomPassword(generateTempPasswordInVO, userDetailsVO);//password::true
                passwordReceived = validatePasswordVO.getPassword();//password::true
                statusOutVO = userManagerService.validatePassword(validatePasswordVO);
                LOG.error("statusOutVO in While conduction >>>>>>>>>>>>>>>>>>>>>>>>>>>" + statusOutVO);
                if (statusOutVO.getStatus().equalsIgnoreCase("true")) {
                    LOG.error("passwordReceived>>>>>>>>>>>>>>>>>>>>>>>>>>>" + passwordReceived);
                    String[] randomPassword = passwordReceived.split("::");
                    LOG.error("In randompassword>>>>>>>>>>>>>>>>>>>>>>>>>>>" + randomPassword);
                    int length = randomPassword.length;
                    LOG.error("In while condation>>>>>>>>>>>>>>>>>>>>>>>>>>>" + length);
                    if (length > 1) {
                        if ("true".equalsIgnoreCase(randomPassword[1])) {
                            validatePasswordVO.setPassword((randomPassword[0]));
                            LOG.error("validatePasswordVO>>>>>>>>>>>>>>>>>>>>>>>>>>>" + (randomPassword[0]));
                            LOG.error("validatePasswordVO>>>>>>>>>>>>>>>>>>>>>>>>>>>" + (randomPassword[1]));
                            break;
                        }
                    } else {
                        generateTempPasswordOutVO.setStatus("Error");
                        LOG.error("generateTempPasswordOutVO else block>>>>>>>>>>>>>>>>>>>>>>>>>>>" +
                                  (randomPassword[0]));
                        generateTempPasswordOutVO.setErrorMessage("Error in Generating Password. Please try again");
                        return generateTempPasswordOutVO;
                    }

                }
            }

            LOG.error("generateTempPasswordOutVO passwordReceived >>>>>>>>>>>>>passwordReceived " + passwordReceived);
            if (changeUserPassword(generateTempPasswordInVO.getUsername(),
                                   passwordReceived).equalsIgnoreCase("Error")) {
                generateTempPasswordOutVO.setStatus("Error");
                LOG.error("generateTempPasswordOutVO>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                generateTempPasswordOutVO.setErrorMessage("Error in Generating Password. Please try again");
                return generateTempPasswordOutVO;
            } else {
                generateTempPasswordOutVO.setPassword(validatePasswordVO.getPassword());
                generateTempPasswordOutVO.setErrorMessage("Temporary password generated successfully");
                return generateTempPasswordOutVO;
            }
        } else {
            generateTempPasswordOutVO.setStatus("Error");
            generateTempPasswordOutVO.setErrorMessage("Incorrect Cellphone Number provided. Please enter the cellphone number used while creating the user");
            LOG.error(this +
                      " Error on generateTempPassword: Incorrect Cellphone Number provided. Please enter the cellphone number used while creating the user " +
                      generateTempPasswordInVO.getUsername());
            return generateTempPasswordOutVO;
        }
    }
    
    /**
     * @param generateTempPasswordInVO
     * @param userDetailsVO
     * @return
     */
    public ValidatePasswordVO validateRandomPassword(GenerateTempPasswordInVO generateTempPasswordInVO,
                                                     UserDetailsVO userDetailsVO) {
        RandomPasswordGenerator randomPasswordGenerator = new RandomPasswordGenerator();
        String randomPassword =
            String.copyValueOf(randomPasswordGenerator.generatePassword()).concat("::").concat("true");
        LOG.info("Randompassword in valdateRandomPassword method11" + randomPassword);
        ValidatePasswordVO validatePasswordVO = new ValidatePasswordVO();
        validatePasswordVO.setFirstName(userDetailsVO.getFirstName());
        validatePasswordVO.setLastName(userDetailsVO.getLastName());
        validatePasswordVO.setUsername(generateTempPasswordInVO.getUsername());
        LOG.info("Randompassword in valdateRandomPassword method12");
        validatePasswordVO.setPassword(randomPassword);
        LOG.info("Randompassword in valdateRandomPassword method13" + randomPassword);
        return validatePasswordVO;
    }
    
    /**
     * @param username
     * @param password
     * @return
     */
    public String changeUserPassword(String username, String password) {
        ChangePasswordVO changePasswordVO = new ChangePasswordVO();
        UserManagerService userManagerService = new UserManagerService();
        changePasswordVO.setChangePasswordAtNextLogin("Yes"); //confirm with Master
        changePasswordVO.setUsername(username);
        changePasswordVO.setPassword(password);
        LOG.info(this + " generateTempPassword: changePassword process begin for the user " + username);
        StatusOutVO statusOutVO = userManagerService.changePassword(changePasswordVO);
        return statusOutVO.getStatus();
    }
}
