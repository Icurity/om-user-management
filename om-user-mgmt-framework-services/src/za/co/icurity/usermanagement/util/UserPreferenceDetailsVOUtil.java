package za.co.icurity.usermanagement.util;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import oracle.iam.identity.usermgmt.api.UserManager;
import oracle.iam.identity.usermgmt.vo.User;
import oracle.iam.platform.OIMClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import za.co.icurity.usermanagement.vo.UserPreferenceDetailsVO;

public class UserPreferenceDetailsVOUtil {
    public UserPreferenceDetailsVOUtil() {
        super();
    }
    private static final Logger LOG = LoggerFactory.getLogger(TransferrableSiteRolesOfUserUtil.class);

    public UserPreferenceDetailsVO getUserDetailsToNotify(OIMClient oimClient, String inputUsername) {

        UserPreferenceDetailsVO userVO = new UserPreferenceDetailsVO();
        Set<String> attrNames = null;
        
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
        attrNames.add("PreferredMode");
        attrNames.add("OptOut");
        attrNames.add("Email");
        attrNames.add("country_code");
        attrNames.add("cellphone_country");

        UserManager userManager_local = oimClient.getService(UserManager.class);
        User user = null;

        try {
            user = userManager_local.getDetails("User Login", inputUsername, attrNames);
            if (user != null) {
                userVO.setFirstName(user.getFirstName());
                userVO.setLastName(user.getLastName());
                if (user.getAttribute("Telephone Number") != null) {
                    userVO.setCellPhoneNumber(user.getAttribute("Telephone Number").toString());
                }
                if (user.getAttribute("birth_date") != null) {

                    userVO.setDateOfBirth(user.getAttribute("birth_date").toString());
                }
                if (user.getAttribute("id_number") != null) {
                    userVO.setIdNumber(user.getAttribute("id_number").toString());
                }
                if (user.getAttribute("id_type") != null) {
                    userVO.setIdType(user.getAttribute("id_type").toString());
                }
                if (user.getAttribute("Employee Number") != null) {
                    userVO.setEmployeeNumber(user.getAttribute("Employee Number").toString());
                }
                if (user.getAttribute("migr_firstLogin") != null) {
                    userVO.setMigratedUserFirstLogin(user.getAttribute("migr_firstLogin").toString());
                } else {
                    userVO.setMigratedUserFirstLogin("false");
                }
                if (user.getAccountStatus() != null) {
                    if (user.getAccountStatus().equals("0")) {
                        userVO.setUserAccountLocked("false");
                    } else if (user.getAccountStatus().equals("1")) {
                        userVO.setUserAccountLocked("true");
                    }
                }
                if (user.getAttribute("PreferredMode") != null) {
                    userVO.setPreferredModeNotification(user.getAttribute("PreferredMode").toString());
                }
                if (user.getAttribute("OptOut") != null) {
                    userVO.setOptOutPreferrence(user.getAttribute("OptOut").toString());
                }
                if (user.getAttribute("Email") != null) {
                    userVO.setEmail(user.getAttribute("Email").toString());
                }
                if (user.getAttribute("country_code") != null) {
                    userVO.setCountrycode(user.getAttribute("country_code").toString());
                }
                if (user.getAttribute("cellphone_country") != null) {
                    userVO.setCountryname(user.getAttribute("cellphone_country").toString());
                }

                userVO.setStatus("Success");
            }
        } catch (Exception ex) {
            try {
                user = userManager_local.getDetails("Employee Number", inputUsername, attrNames);
                if (user != null) {
                    userVO.setFirstName(user.getFirstName());
                    userVO.setLastName(user.getLastName());
                    if (user.getAttribute("Telephone Number") != null) {
                        userVO.setCellPhoneNumber(user.getAttribute("Telephone Number").toString());
                    }
                    if (user.getAttribute("birth_date") != null) {

                        userVO.setDateOfBirth(user.getAttribute("birth_date").toString());
                    }
                    if (user.getAttribute("id_number") != null) {
                        userVO.setIdNumber(user.getAttribute("id_number").toString());
                    }
                    if (user.getAttribute("id_type") != null) {
                        userVO.setIdType(user.getAttribute("id_type").toString());
                    }
                    if (user.getAttribute("Employee Number") != null) {
                        userVO.setEmployeeNumber(user.getAttribute("Employee Number").toString());
                    }
                    if (user.getAttribute("migr_firstLogin") != null) {
                        userVO.setMigratedUserFirstLogin(user.getAttribute("migr_firstLogin").toString());
                    } else {
                        userVO.setMigratedUserFirstLogin("false");
                    }
                    if (user.getAccountStatus() != null) {
                        if (user.getAccountStatus().equals("0")) {
                            userVO.setUserAccountLocked("false");
                        } else if (user.getAccountStatus().equals("1")) {
                            userVO.setUserAccountLocked("true");
                        }
                    }
                    if (user.getAttribute("PreferredMode") != null) {
                        userVO.setPreferredModeNotification(user.getAttribute("PreferredMode").toString());
                    }
                    if (user.getAttribute("OptOut") != null) {
                        userVO.setOptOutPreferrence(user.getAttribute("OptOut").toString());
                    }
                    if (user.getAttribute("Email") != null) {
                        userVO.setEmail(user.getAttribute("Email").toString());
                    }
                    if (user.getAttribute("country_code") != null) {
                        userVO.setCountrycode(user.getAttribute("country_code").toString());
                    }
                    if (user.getAttribute("cellphone_country") != null) {
                        userVO.setCountryname(user.getAttribute("cellphone_country").toString());
                    }

                    userVO.setStatus("Success");
                }
            } catch (Exception exObj) {
                LOG.info(this + "getUserDetailsToNotify", "Error: " + exObj.getMessage());
                userVO.setStatus("Error");
                userVO.setErrorMessage("No user found for username : " + inputUsername);
            }

        } finally {
            //Logout from OIMClient
            if (oimClient != null) {
                LOG.info(this + "getUserDetailsToNotify", "logging out");
                oimClient.logout();
                oimClient = null;
            }
        }
        LOG.info(this + "getUserDetailsToNotify", "__getUserDetailsToNotify End__");
        return userVO;
    }
}
