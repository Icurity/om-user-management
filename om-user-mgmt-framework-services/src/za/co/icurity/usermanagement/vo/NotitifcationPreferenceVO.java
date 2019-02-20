package za.co.icurity.usermanagement.vo;

public class NotitifcationPreferenceVO {
    
    private String username;
    
    private String preferredModeNotification;
        private String optOutPreferrence;
        
    public NotitifcationPreferenceVO() {
        super();
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
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
}
