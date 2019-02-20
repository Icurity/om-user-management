package za.co.icurity.usermanagement.vo;

public class RoleDetailsVO extends StatusOutVO{

    private String displayName;
    private String name;
//    private String roleCategory;
//    private String roleNamespace;
    private String roleDescription;
    private String roleType;
    private String roleRiskCategory;
    private String roleDelegationAllowed;
    private String linkedDelegableRole;
    private String siteUrl;
    private String siteName;
    
    

    public RoleDetailsVO() {
        super();
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }

    public String getRoleDescription() {
        return roleDescription;
    }


    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleRiskCategory(String roleRiskCategory) {
        this.roleRiskCategory = roleRiskCategory;
    }

    public String getRoleRiskCategory() {
        return roleRiskCategory;
    }

    public void setRoleDelegationAllowed(String roleDelegationAllowed) {
        this.roleDelegationAllowed = roleDelegationAllowed;
    }

    public String getRoleDelegationAllowed() {
        return roleDelegationAllowed;
    }

    public void setLinkedDelegableRole(String linkedDelegableRole) {
        this.linkedDelegableRole = linkedDelegableRole;
    }

    public String getLinkedDelegableRole() {
        return linkedDelegableRole;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    public String getSiteUrl() {
        return siteUrl;
    }


    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteName() {
        return siteName;
    }
}

