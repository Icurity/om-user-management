package za.co.icurity.usermanagement.vo;

public class ADPropertiesVO {
    public ADPropertiesVO() {
        super();
    }
    private String adInstanceName;
    private String adServerKey;
    private String adOrganizationName;

    public String getAdInstanceName() {
        return adInstanceName;
    }


    public void setAdInstanceName(String adInstanceName) {
        this.adInstanceName = adInstanceName;
    }


    public String getAdServerKey() {
        return adServerKey;
    }


    public void setAdServerKey(String adServerKey) {
        this.adServerKey = adServerKey;
    }


    public String getAdOrganizationName() {
        return adOrganizationName;
    }


    public void setAdOrganizationName(String adOrganizationName) {
        this.adOrganizationName = adOrganizationName;
    }

}
