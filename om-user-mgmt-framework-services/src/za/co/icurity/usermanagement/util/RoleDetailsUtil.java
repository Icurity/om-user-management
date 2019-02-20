package za.co.icurity.usermanagement.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import oracle.iam.identity.exception.RoleSearchException;
import oracle.iam.identity.rolemgmt.api.RoleManager;
import oracle.iam.identity.rolemgmt.vo.Role;
import oracle.iam.platform.OIMClient;

import oracle.iam.platform.entitymgr.vo.SearchCriteria;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import za.co.icurity.usermanagement.vo.RoleDetailsVO;
import za.co.icurity.usermanagement.vo.StatusOutVO;
import za.co.icurity.usermanagement.vo.UserRoleGrantVO;

public class RoleDetailsUtil {
    public RoleDetailsUtil() {
        super();
    }

    private static final Logger LOG = LoggerFactory.getLogger(RoleDetailsUtil.class);

    public RoleDetailsVO roleDetails(OIMClient oimClient, String rolename) {

        RoleDetailsVO roleDetailsVO = new RoleDetailsVO();
        RoleManager roleManager = oimClient.getService(RoleManager.class);
        Set<String> resAttrs = new HashSet<String>();
        List<Role> role;
        role = null;
        SearchCriteria criteria1 = new SearchCriteria("Role Name", rolename, SearchCriteria.Operator.EQUAL);

        resAttrs = new HashSet<String>();
        resAttrs.add("RoleRiskCategory");
        resAttrs.add("RoleType");
        resAttrs.add("Role Name");
        resAttrs.add("Role Display Name");
        resAttrs.add("RoleDelegationAllowed");
        resAttrs.add("LinkedDelegableRole");
        resAttrs.add("SiteUrl");
        resAttrs.add("SiteName");

        HashMap<String, Object> parameters = null;

        try {
            role = roleManager.search(criteria1, resAttrs, null);
            if (null != role && role.size() > 0) {
                for (int i = 0; i < role.size(); i++) {
                    if (null != role.get(i).getAttribute("RoleRiskCategory"))
                        roleDetailsVO.setRoleRiskCategory(role.get(i).getAttribute("RoleRiskCategory").toString());
                    if (null != role.get(i).getAttribute("RoleType"))
                        roleDetailsVO.setRoleType(role.get(i).getAttribute("RoleType").toString());
                    if (null != role.get(i).getAttribute("Role Name"))
                        roleDetailsVO.setName(role.get(i).getAttribute("Role Name").toString());
                    if (null != role.get(i).getAttribute("Role Display Name"))
                        roleDetailsVO.setDisplayName(role.get(i).getAttribute("Role Display Name").toString());
                    if (null != role.get(i).getAttribute("RoleDelegationAllowed"))
                        roleDetailsVO.setRoleDelegationAllowed(role.get(i).getAttribute("RoleDelegationAllowed").toString());
                    if (null != role.get(i).getAttribute("LinkedDelegableRole"))
                        roleDetailsVO.setLinkedDelegableRole(role.get(i).getAttribute("LinkedDelegableRole").toString());
                    if (null != role.get(i).getAttribute("SiteUrl"))
                        roleDetailsVO.setSiteUrl(role.get(i).getAttribute("SiteUrl").toString());
                    if (null != role.get(i).getAttribute("SiteName"))
                        roleDetailsVO.setSiteName(role.get(i).getAttribute("SiteName").toString());

                }
                LOG.info(this + " getRoleDetails: Success for " + rolename);
                roleDetailsVO.setStatus("Success");
            } else {
                roleDetailsVO.setStatus("Error");
                roleDetailsVO.setErrorMessage("No Role found for roleName : " + rolename);
                LOG.info(this + " getRoleDetails: No Role found for roleName : " + rolename);
                return roleDetailsVO;
            }
        } catch (RoleSearchException rse) {
            LOG.error(this + " Error on getRoleDetails. ERROR: " + rse.getMessage());
            roleDetailsVO.setStatus("Error");
            roleDetailsVO.setErrorMessage("No Role found for roleName : " + rolename);
            LOG.info(this + " getRoleDetails: RoleSearchException for rolename " + rolename+" Error: "+rse.getMessage());
            return roleDetailsVO;
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
        return roleDetailsVO;
    }
}
