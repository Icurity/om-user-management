package za.co.icurity.usermanagement.vo;

public class UpdateUserInVO extends UserVO {

    private boolean migratedUserFirstLogin;
    private String employeeNumber;
    private String password;
    private String username;
    private String gender;
    private String idType;
    //GCS change
    private String countryCode;
    private String countryName;

    public UpdateUserInVO() {
        super();
    }

    public void setMigratedUserFirstLogin(boolean migratedUserFirstLogin) {
        this.migratedUserFirstLogin = migratedUserFirstLogin;
    }

    public boolean isMigratedUserFirstLogin() {
        return migratedUserFirstLogin;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
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

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getIdType() {
        return idType;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryName() {
        return countryName;
    }
}

