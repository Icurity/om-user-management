package za.co.icurity.usermanagement.vo;

public class ChangePasswordForLoggedInUserVO {
    
    private String username;
    private String oldPassword;
    private String newPassword;
    
    public ChangePasswordForLoggedInUserVO() {
        super();
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }
}
