package za.co.icurity.usermanagement.vo;

public class ChangePasswordVO {
    private String password;
    private String username;
    private String changePasswordAtNextLogin;

    public ChangePasswordVO() {
        super();
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setChangePasswordAtNextLogin(String changePasswordAtNextLogin) {
        this.changePasswordAtNextLogin = changePasswordAtNextLogin;
    }

    public String getChangePasswordAtNextLogin() {
        return changePasswordAtNextLogin;
    }
}
