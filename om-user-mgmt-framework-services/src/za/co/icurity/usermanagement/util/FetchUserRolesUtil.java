package za.co.icurity.usermanagement.util;

import java.util.ArrayList;
import java.util.Enumeration;

import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;

import javax.naming.directory.SearchResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import za.co.icurity.usermanagement.proxy.OVDLoginProxy;
import za.co.icurity.usermanagement.vo.RoleListVO;
import za.co.icurity.usermanagement.vo.RoleVO;


public class FetchUserRolesUtil {
    public FetchUserRolesUtil() {
        super();
    }
    private static final Logger LOG = LoggerFactory.getLogger(FetchUserRolesUtil.class);
    static final StringUtil stringUtil = null;
    static final String searchBase = "cn=users";
    
    public RoleListVO fetchUserRoles(DirContext dirContext, String username) throws Exception {
        RoleListVO roleListVO;
        roleListVO = new RoleListVO();
        OVDLoginProxy ovdLoginProxy = new OVDLoginProxy();
        try {
            String usernametmp = stringUtil.escapeMetaCharacters(username);
            //String searchFilter = "(|(uid=" + usernametmp + ")(omUserAlias=" + usernametmp + "))";
            String searchFilter = "(|(uid=" + usernametmp + ")(cn=" + usernametmp + "))";
            LOG.info(this + " fetchUserRoles search filter ");
            SearchResult searchResult = null;
            try {
                searchResult = ovdLoginProxy.findUserAttributes(dirContext, searchBase, searchFilter);
            } catch (Exception e) {
                roleListVO.setStatus("Error");
                roleListVO.setErrorMessage("Failed to search the user " + username);
                LOG.error(this + " fetchUserRoles: Failed to search with the searchFilter " + searchFilter +
                          " for the user " + username);
                return roleListVO;
            }
            Attributes attributes = null;
            if (searchResult != null) {
                attributes = searchResult.getAttributes();
            } else {
                roleListVO.setStatus("Error");
                roleListVO.setErrorMessage("No user found for username: " + username);
                LOG.info(this + " fetchUserRoles: No user found for username: " + username);
                return roleListVO;
            }
            ArrayList<RoleVO> memberOfList = new ArrayList<RoleVO>();
            if (attributes.get("memberOf") != null) {
                for (Enumeration vals = attributes.get("memberOf").getAll(); vals.hasMoreElements(); ) {
                    String[] myData = vals.nextElement().toString().split("CN=");
                    // int i = 0;
                    for (String str : myData) {
                        String group = str.split(",")[0];
                        if (!group.contains("Admin Group")) {
                            group = group.replace("R-", "").replace("Members Group", "").trim();
                            RoleVO roleVO = new RoleVO();
                            if (group.length() > 0) {
                                roleVO.setName(group);
                                memberOfList.add(roleVO);
                            }
                        }
                    }

                }
            }
            if (attributes.get("ismemberOf") != null) {
                for (Enumeration vals = attributes.get("ismemberOf").getAll(); vals.hasMoreElements(); ) {
                    String[] myData = vals.nextElement().toString().split("cn=");
                    for (String str : myData) {
                        String group = str.split(",")[0];
                        if (!group.contains("Admin Group") && !group.contains("groups")) {
                            group = group.replace("R-", "").replace("Members Group", "").trim();
                            RoleVO roleVO = new RoleVO();
                            if (group.length() > 0) {
                                roleVO.setName(group);
                                memberOfList.add(roleVO);
                            }
                        }
                    }
                }
            }
            if (memberOfList.size() > 0) {
                roleListVO.setStatus("Success");
                roleListVO.setRoles(memberOfList);
                LOG.info(this + " fetchUserRoles: Fetch user roles success for: " + username);
                return roleListVO;
            } else {
                RoleVO roleVO = new RoleVO();
                roleVO.setName("ALL USERS");
                memberOfList.add(roleVO);
                roleListVO.setStatus("Success");
                roleListVO.setRoles(memberOfList);
                //roleListVO.setErrorMessage("No roles granted for username: " + username);
                LOG.info(this + " fetchUserRoles: No roles granted for username: " + username + " so ALL USERS");
                return roleListVO;
            }
        } catch (Exception e) {
            roleListVO.setStatus("Error");
            roleListVO.setErrorMessage("System unavailable at the moment, please try again");
            LOG.error(this + " Error on fetchUserRoles " + e.getMessage());
            return roleListVO;
        } finally {
            if (dirContext != null) {
                dirContext.close();
            }
        }
    }
}
