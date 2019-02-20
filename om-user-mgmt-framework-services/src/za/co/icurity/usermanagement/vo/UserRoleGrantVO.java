package za.co.icurity.usermanagement.vo;

public class UserRoleGrantVO {
    public UserRoleGrantVO() {
        super();
    }
    private String roleName;
     private String userEntityId;

     public void setRoleName(String roleName) {
         this.roleName = roleName;
     }

     public String getRoleName() {
         return roleName;
     }

     public void setUserEntityId(String userEntityId) {
         this.userEntityId = userEntityId;
     }

     public String getUserEntityId() {
         return userEntityId;
     }
}
