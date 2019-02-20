package za.co.icurity.usermanagement.util;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.util.logging.Level;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchResult;

import oracle.iam.identity.usermgmt.api.UserManager;

import oracle.iam.identity.usermgmt.vo.User;
import oracle.iam.platform.OIMClient;
import oracle.iam.platform.entitymgr.vo.SearchCriteria;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import za.co.icurity.usermanagement.proxy.OVDLoginProxy;
import za.co.icurity.usermanagement.vo.GetUserAccountLockedOutVO;
import za.co.icurity.usermanagement.vo.UserDetailsVO;


/**
 * @author icurity
 *
 */
@JsonInclude(value = Include.NON_NULL)

public class AttributesUtil {
    static final String searchBase = "cn=users";
    private static final Logger LOG = LoggerFactory.getLogger(AttributesUtil.class);

    //public UserDetailsVO getUserDetailsAttributes(DirContext dirContext, String username)

    public UserDetailsVO getUserDetailsAttributes(OIMClient oimClient, String username) {
        UserDetailsVO userDetailsVO = new UserDetailsVO();
        Set<String> attrNames = null;
        List<User> users = null;
        HashMap<String, Object> parameters = null;
        SearchCriteria criteria = new SearchCriteria("User Name", username, SearchCriteria.Operator.EQUAL);
        attrNames = new HashSet<String>();
        User user = null;
        UserManager userManager_local;

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
            userManager_local = oimClient.getService(UserManager.class);
        } catch (Exception e) {
            userDetailsVO.setStatus("Error");
            userDetailsVO.setErrorMessage("System unavailable at the moment, please try again");
            LOG.error(this + " Error on getUserDetailsAttributes: " + e.getMessage());
            return userDetailsVO;
        }

        try {
            user = userManager_local.getDetails("User Login", username, attrNames);
            LOG.info(this + "getUserDetailsAttributes User Login " + user);
            if (user != null) {
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
                LOG.info(this + " getUserDetailsAttributes: User details with User Login found for username: " + username);
                userDetailsVO.setStatus("Success");
            }
        } catch (Exception ex) {
            try {
                user = userManager_local.getDetails("Employee Number", username, attrNames);
                if (user != null) {
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
                    LOG.info(this + " getUserDetailsAttributes: User details with Employee Number found for username: " + username);
                    return userDetailsVO;
                }
            } catch (Exception exObj) {
                userDetailsVO.setStatus("Error");
                userDetailsVO.setErrorMessage("No user found for username: " + username);
                LOG.error(this + "getUserDetails: Error " + exObj.getMessage() + " for the user  " + username);
                return userDetailsVO;
            }
        }
        return userDetailsVO;
    }


    public UserDetailsVO getUserDetailsAttributes1(OIMClient oimClient, String username) {
        UserDetailsVO userDetailsVO = new UserDetailsVO();
        OVDLoginProxy ovdLoginProxy = new OVDLoginProxy();
        Set<String> attrNames = null;
        List<User> users = null;
        HashMap<String, Object> parameters = null;
        SearchCriteria criteria = new SearchCriteria("User Name", username, SearchCriteria.Operator.EQUAL);
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

        try {
            UserManager userManager_local = oimClient.getService(UserManager.class);
            LOG.info(this + "In First TRY block>>>>>>>>>>>>>>>>>..: " + username);
            users = userManager_local.search(criteria, attrNames, parameters);
            //User users = userManager_local.search("User Name", username, attrNames);
            LOG.info(this + "In users>>>>>>>>>>>>>>>>>..: " + users);
            if (users != null && !users.isEmpty() && users.size() == 1 && users.get(0) != null) {
                //userDetailsVO.setStatus("Success");
                LOG.info(this + "In First IF block>>>>>>>>>>>>>>>>>..: " + username);
                if (users.get(0).getAttribute("Telephone Number") != null) {
                    userDetailsVO.setCellPhoneNumber(users.get(0).getAttribute("Telephone Number").toString());
                }
                if (users.get(0).getAttribute("birth_date") != null) {

                    userDetailsVO.setDateOfBirth(users.get(0).getAttribute("birth_date").toString());
                }
                if (users.get(0).getAttribute("id_number") != null) {
                    userDetailsVO.setIdNumber(users.get(0).getAttribute("id_number").toString());
                }
                if (users.get(0).getAttribute("id_type") != null) {
                    userDetailsVO.setIdType(users.get(0).getAttribute("id_type").toString());
                }
                if (users.get(0).getAttribute("Employee Number") != null) {
                    userDetailsVO.setEmployeeNumber(users.get(0).getAttribute("Employee Number").toString());
                }
                if (users.get(0).getAttribute("migr_firstLogin") != null) {
                    userDetailsVO.setMigratedUserFirstLogin(users.get(0).getAttribute("migr_firstLogin").toString());
                } else {
                    userDetailsVO.setMigratedUserFirstLogin("false");
                }
                if (users.get(0).getAccountStatus() != null) {
                    if (users.get(0).getAccountStatus().equals("0")) {
                        userDetailsVO.setUserAccountLocked("false");
                    } else if (users.get(0).getAccountStatus().equals("1")) {
                        userDetailsVO.setUserAccountLocked("true");
                    }
                }
                /* if (users.get(0).getAttribute("PreferredMode") != null) {
                    userDetailsVO.setPreferredModeNotification(users.get(0).getAttribute("PreferredMode").toString());
                }
                if (users.get(0).getAttribute("OptOut") != null) {
                    userDetailsVO.setOptOutPreferrence(user.getAttribute("OptOut").toString());
                }
                if (users.get(0).getAttribute("Email") != null) {
                userDetailsVO.setEmail(users.get(0).getAttribute("Email").toString());
                }
                if (users.getAttribute("country_code") != null) {
                userDetailsVO.setCountrycode(user.getAttribute("country_code").toString());
                }
                if (users.getAttribute("cellphone_country") != null) {
                userDetailsVO.setCountryname(user.getAttribute("cellphone_country").toString());
                } */
                userDetailsVO.setStatus("Success");
                LOG.info(this + "User found for username: " + username);
                return userDetailsVO;
            } else {
                LOG.info(this + "In First ELSE block>>>>>>>>>>>>>>>>>..: " + username);
                userDetailsVO.setStatus("Error");
                userDetailsVO.setErrorMessage("No user found for username: " + username);
                LOG.info(this + "No user found for username: " + username);
                return userDetailsVO;
            }
        } catch (Exception ex) {
            LOG.info(this + "In Second Catch block>>>>>>>>>>>>>>>>>..: " + userDetailsVO);
            ex.printStackTrace();
            LOG.error(this + " userDetailsVO error " + ex.getMessage());
            userDetailsVO.setStatus("Error");
            userDetailsVO.setErrorMessage("No username/alias could be retrieved");
            //return userDetailsVO;
        } finally {
            if (oimClient != null) {
                LOG.info(this + " getUserId", "logging out");
                oimClient.logout();
                oimClient = null;
            }
        }
        return userDetailsVO;
    }

    /*  try {
            String searchFilter = "(|(uid=" + username + ")(cn=" + username + "))";
            SearchResult searchResult = ovdLoginProxy.findUserAttributes(dirContext, searchBase, searchFilter);
            Attributes attributes = null;
            if (searchResult != null) {
                attributes = searchResult.getAttributes();
                LOG.info(this + "getUserDetailsAttributes: searchResult attributes for  username " + username + " : " +
                         attributes);
            } else {
                LOG.info(this + "No user found for username: " + username);
                userDetailsVO.setStatus("Error");
                userDetailsVO.setErrorMessage("No user found for username: " + username);
                return userDetailsVO;
            }

            if (attributes != null) {
                for (NamingEnumeration ae = attributes.getAll(); ae.hasMore(); ) {
                    Attribute attr = (Attribute)ae.next();
                    if (attr.getID().equalsIgnoreCase("givenName")) {
                        userDetailsVO.setFirstName(attr.get().toString());
                    }
                    if (attr.getID().equalsIgnoreCase("sn")) {
                        userDetailsVO.setLastName(attr.get().toString());
                    }
                    if (attr.getID().equalsIgnoreCase("mobile")) {
                        userDetailsVO.setCellPhoneNumber(attr.get().toString());
                    }
                    if (attr.getID().equalsIgnoreCase("omDateOfBirth")) {
                        userDetailsVO.setDateOfBirth(attr.get().toString());
                    }
                    if (attr.getID().equalsIgnoreCase("employeeID")) {
                        userDetailsVO.setIdNumber(attr.get().toString());
                    }
                    if (attr.getID().equalsIgnoreCase("obfirstlogin")) {
                        userDetailsVO.setMigratedUserFirstLogin(attr.get().toString());
                    }
                    if (attr.getID().equalsIgnoreCase("omUserAccountStatus")) {
                        userDetailsVO.setUserAccountLocked(attr.get().toString());
                    }
                    if (attr.getID().equalsIgnoreCase("employeeType")) {
                        userDetailsVO.setIdType(attr.get().toString());
                    }
                    if (attr.getID().equalsIgnoreCase("employeeNumber")) {
                        userDetailsVO.setEmployeeNumber(attr.get().toString());
                    }
                }
                userDetailsVO.setStatus("Success");
                LOG.info(this + "User found for username: " + username);
                return userDetailsVO;
            } else {
                userDetailsVO.setStatus("Error");
                userDetailsVO.setErrorMessage("No user found for username: " + username);
                LOG.info(this + "No user found for username: " + username);
                return userDetailsVO;
            }
        } catch (Exception e) {
            LOG.error(this + " Error on getUserDetailsAttributes: " + e.getMessage());
        }
        return userDetailsVO;
     }*/


    /**
     * @param dirContext
     * @param username
     * @return
     */
    public GetUserAccountLockedOutVO getUserAccountLockedAttributes(DirContext dirContext, String username) {
        GetUserAccountLockedOutVO getUserAccountLockedOutVO = new GetUserAccountLockedOutVO();
        OVDLoginProxy ovdLoginProxy = new OVDLoginProxy();
        try {
            String searchFilter = "(|(uid=" + username + ")(cn=" + username + "))";
            SearchResult searchResult = ovdLoginProxy.findUserAttributes(dirContext, searchBase, searchFilter);
            LOG.info(this + " getUserAccountLockedAttributes searchResult for user " + username);
            Attributes attributes = null;
            if (searchResult != null) {
                attributes = searchResult.getAttributes();
            } else {
                LOG.info(this + "No user found for username: " + username);
                getUserAccountLockedOutVO.setStatus("Error");
                getUserAccountLockedOutVO.setErrorMessage("No user found for username: " + username);
                return getUserAccountLockedOutVO;
            }
            if (attributes != null) {
                for (NamingEnumeration ae = attributes.getAll(); ae.hasMore(); ) {
                    Attribute attr = (Attribute)ae.next();
                    if (attr.getID().equalsIgnoreCase("omUserAccountStatus")) {
                        getUserAccountLockedOutVO.setUserAccountStatus(attr.get().toString());
                    }
                }
                getUserAccountLockedOutVO.setStatus("Success");
                return getUserAccountLockedOutVO;
            } else {
                getUserAccountLockedOutVO.setStatus("Error");
                getUserAccountLockedOutVO.setErrorMessage("No user found for username: " + username);
                return getUserAccountLockedOutVO;
            }
        } catch (Exception e) {
            LOG.error(this + " Error on getUserAccountLockedAttributes: " + e.getMessage());
            getUserAccountLockedOutVO.setStatus("Error");
            getUserAccountLockedOutVO.setErrorMessage("No user found for username: " + username);
            return getUserAccountLockedOutVO;
        }
    }


    /* public UserDetailsVO getADUsernameAttributes(DirContext dirContext, GetADUsernameInVO getADUsernameInVO) {
			
			try {
				String searchFilter = "(|(uid=" + username + ")(omUserAlias=" + username + ")(cn=" + username + "))";
				SearchResult searchResult = ovdLoginProxy.findUserAttributes(dirContext, searchBase, searchFilter);
				Attributes attributes = null;
				if (searchResult != null) {
					attributes = searchResult.getAttributes();
				}else{
					LOG.info(this + "No user found for username: " + username);
					userDetailsVO.setStatus("Error");
					userDetailsVO.setErrorMessage("No user found for username: " + username);
					return userDetailsVO;
				}	
				
				
				}catch (Exception e) {
					LOG.error(this + " Error on getUserDetailsAttributes: " + e.getMessage());
				}
			return userDetailsVO;
		}*/
}
