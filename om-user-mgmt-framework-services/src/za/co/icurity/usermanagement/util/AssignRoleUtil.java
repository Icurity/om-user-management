package za.co.icurity.usermanagement.util;

import oracle.iam.identity.rolemgmt.api.RoleManager;
import oracle.iam.identity.rolemgmt.vo.RoleManagerResult;
import oracle.iam.platform.OIMClient;

import oracle.iam.platform.authopss.vo.AdminRole;
import oracle.iam.platform.authopss.vo.AdminRoleMembership;
import oracle.iam.platformservice.api.AdminRoleService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import za.co.icurity.usermanagement.vo.ChangePasswordVO;
import za.co.icurity.usermanagement.vo.StatusOutVO;
import za.co.icurity.usermanagement.vo.UserRoleGrantVO;

public class AssignRoleUtil {
    public AssignRoleUtil() {
        super();
    }
    private static final Logger LOG = LoggerFactory.getLogger(AssignRoleUtil.class);

    public StatusOutVO assignRole(OIMClient oimClient, UserRoleGrantVO userRolegrantVO) {
        String status = null;
        RoleManagerResult result = null;
        StatusOutVO statusOutVO = new StatusOutVO();
        try {

            RoleManager roleManager_local = oimClient.getService(RoleManager.class);
            result =
                    roleManager_local.grantRole("Role Name", userRolegrantVO.getRoleName(), "usr_key", userRolegrantVO.getUserEntityId());
            statusOutVO.setStatus(result.getStatus());
            LOG.info(this + " assignRole: Status " + status + " for the Role name " + userRolegrantVO.getRoleName());
            if (userRolegrantVO.getRoleName() != null &&
                userRolegrantVO.getRoleName().equals("CallCentreAdvancedRole")) {
                // assign User Admin Role to these users
                AdminRoleService adminRoleService_local = oimClient.getService(AdminRoleService.class);

                AdminRole adminRole = adminRoleService_local.getAdminRole(("OrclOIMUserAdmin"));
                LOG.info(this + " assignRole: adminRole " + adminRole);
                AdminRoleMembership adminRoleMembership = new AdminRoleMembership();
                adminRoleMembership.setAdminRole(adminRole);
                adminRoleMembership.setUserId(userRolegrantVO.getUserEntityId());
                adminRoleMembership.setScopeId("1");
                adminRoleMembership.setHierarchicalScope(false);
                adminRoleService_local.addAdminRoleMembership(adminRoleMembership);  
            }
        } catch (Exception ex) {
            LOG.error(this + " assignRole Exception: " + ex.getMessage());
            statusOutVO.setStatus(ex.getMessage());
            return statusOutVO;
        } finally {
            //Logout from OIMClient
            if (oimClient != null) {
                LOG.info(this + " assignRole: oimClient logging out");
                oimClient.logout();
                oimClient = null;
            }
            LOG.info(this + " assignRole: Role Grant Status : " + statusOutVO.getStatus());
        }
        return statusOutVO;
    }
}
