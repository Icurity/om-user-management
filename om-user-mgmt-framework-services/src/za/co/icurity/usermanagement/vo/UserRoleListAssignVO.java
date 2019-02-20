package za.co.icurity.usermanagement.vo;

import java.util.ArrayList;

public class UserRoleListAssignVO {
    private ArrayList<String> roleName;
    private String userEntityId;
    public UserRoleListAssignVO() {
        super();
    }
    

    public void setUserEntityId(String userEntityId) {
        this.userEntityId = userEntityId;
    }

    public String getUserEntityId() {
        return userEntityId;
    }

    public void setRoleName(ArrayList<String> roleName) {
        this.roleName = roleName;
    }

    public ArrayList<String> getRoleName() {
        return roleName;
    }
}
