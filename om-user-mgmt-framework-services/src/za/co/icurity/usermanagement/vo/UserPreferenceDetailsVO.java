package za.co.icurity.usermanagement.vo;

public class UserPreferenceDetailsVO extends UserDetailsVO {
    private String preferredModeNotification;
    private String optOutPreferrence;
    private String countrycode;
    private String email;
    private String countryname;
    
    public UserPreferenceDetailsVO() {
        super();
    }
    

    public void setPreferredModeNotification(String preferredModeNotification) {
        this.preferredModeNotification = preferredModeNotification;
    }

    public String getPreferredModeNotification() {
        return preferredModeNotification;
    }


    public void setOptOutPreferrence(String optOutPreferrence) {
        this.optOutPreferrence = optOutPreferrence;
    }

    public String getOptOutPreferrence() {
        return optOutPreferrence;
    }

    public void setCountrycode(String countrycode) {
        this.countrycode = countrycode;
    }

    public String getCountrycode() {
        return countrycode;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setCountryname(String countryname) {
        this.countryname = countryname;
    }

    public String getCountryname() {
        return countryname;
    }
}
