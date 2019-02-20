package za.co.icurity.usermanagement.util;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import za.co.icurity.usermanagement.proxy.OVDLoginProxy;
import za.co.icurity.usermanagement.vo.UserStatusVO;

public class UserStatusUtil {
    public UserStatusUtil() {
        super();
    }
    private static final Logger LOG = LoggerFactory.getLogger(UserStatusUtil.class);
    static final StringUtil stringUtil = null;
    static final String searchBase = "cn=users";

    public UserStatusVO userStatus(DirContext dirContext, String username) throws Exception {
        UserStatusVO userStatusVO = new UserStatusVO();
        OVDLoginProxy ovdLoginProxy = new OVDLoginProxy();
        LOG.info(this + " Entering into userstatus " + username);

        try {
            // String searchFilter = "(|(uid=" + usernameVO.getUsername() + ")(omUserAlias="
            // + usernameVO.getUsername()+ "))";
            // String searchFilter = "(|(uid=" + username + ")(omUserAlias=" + username +
            // ")(cn=" + username + "))";
            String usernametmp;
            usernametmp = stringUtil.escapeMetaCharacters(username);

            //String searchFilter = "(|(uid=" + usernametmp + ")(omUserAlias=" + usernametmp + "))";
            String searchFilter = "(|(uid=" + usernametmp + ")(cn=" + usernametmp + "))";

            SearchResult searchResult = null;

            try {
                searchResult = ovdLoginProxy.findUserAttributes(dirContext, searchBase, searchFilter);
            } catch (Exception e) {
                userStatusVO.setStatus("Error");
                userStatusVO.setErrorMessage("Failed to search the user " + username);
                LOG.error(this + " userstatus: Failed to search with the searchFilter " + searchFilter +
                          " for the user " + username);
                e.printStackTrace();
                return userStatusVO;
            }

            Attributes attributes = null;
            if (searchResult != null) {
                attributes = searchResult.getAttributes();
                LOG.info(this + " userstatus: User attributes from ovd found for the user " + username);
            } else {
                LOG.info(this + " userstatus: No user found for username: " + username);
                userStatusVO.setStatus("Error");
                userStatusVO.setErrorMessage("No user found for username: " + username);
                return userStatusVO;
            }
            if (attributes != null) {
                for (NamingEnumeration ae = attributes.getAll(); ae.hasMore(); ) {
                    Attribute attr = (Attribute)ae.next();
                    if (attr.getID().equalsIgnoreCase("obfirstlogin")) {
                        if (attr.get() != null)
                            if (attr.get().equals("true")) {
                                userStatusVO.setMigratedUserFirstLogin("true");
                            } else if (attr.get().equals("false")) {
                                userStatusVO.setMigratedUserFirstLogin("false");
                            }
                    } else if (attr.getID().equalsIgnoreCase("obpasswordchangeflag")) {
                        if (attr.get() != null)
                            if (attr.get().equals("0")) {
                                userStatusVO.setChangePassword("false");
                            } else if (attr.get().equals("1")) {
                                userStatusVO.setChangePassword("true");
                            }
                    }
                }
            }
            userStatusVO.setStatus("Success");
            userStatusVO.setMigratedUserFirstLogin(userStatusVO.getMigratedUserFirstLogin());
            userStatusVO.setChangePassword(userStatusVO.getChangePassword());
            LOG.info(this + " User status Succeess for the user: " + username);
            return userStatusVO;
        } catch (Exception e) {
            userStatusVO.setStatus("Error");
            userStatusVO.setErrorMessage("System unavailable at the moment, please try again");
            LOG.error(this + " Error on getUserStatus. ERROR: " + e.getMessage());
            return userStatusVO;
        } finally {
            if (dirContext != null) {
                dirContext.close();
            }
        }
    }
}
