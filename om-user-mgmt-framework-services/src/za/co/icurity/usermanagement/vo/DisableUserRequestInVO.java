package za.co.icurity.usermanagement.vo;

public class DisableUserRequestInVO {
 private String username;
    private String justification;
    private String effectiveDate;
    
    public DisableUserRequestInVO() {
        super();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public String getJustification() {
        return justification;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }
}
