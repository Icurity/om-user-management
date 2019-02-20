package za.co.icurity.usermanagement.vo;

import java.util.List;

public class AdminRoleListVO extends StatusOutVO {
    
    private List<RoleDetailsVO> role;
    
    public AdminRoleListVO() {
        super();
    }   
    public void setRole(List<RoleDetailsVO> role) {
        this.role = role;
    }

    public List<RoleDetailsVO> getRole() {
        return role;
    }
}
