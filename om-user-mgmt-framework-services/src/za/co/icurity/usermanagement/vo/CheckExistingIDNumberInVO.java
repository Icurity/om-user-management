package za.co.icurity.usermanagement.vo;

public class CheckExistingIDNumberInVO {
    public CheckExistingIDNumberInVO() {
        super();
    }
    private String idNumber;
    private String IdType;
    private String countryOfIssue;
    
    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdType(String IdType) {
        this.IdType = IdType;
    }

    public String getIdType() {
        return IdType;
    }

    public void setCountryOfIssue(String countryOfIssue) {
        this.countryOfIssue = countryOfIssue;
    }

    public String getCountryOfIssue() {
        return countryOfIssue;
    }
    }

