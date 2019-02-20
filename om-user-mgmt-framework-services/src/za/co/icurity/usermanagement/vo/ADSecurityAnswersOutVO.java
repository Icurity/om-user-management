package za.co.icurity.usermanagement.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public class ADSecurityAnswersOutVO extends StatusOutVO{
    public ADSecurityAnswersOutVO() {
        super();
    }

    private String userAccountStatus;
    private String sAMAccountname;
    private String employeeID;
    private String employeeType;
    private String omChallengeQuestion1;
    private String omChallengeQuestion2;
    private String omChallengeQuestion3;
    private String omChallengeAnswer01;
    private String omChallengeAnswer02;
    private String omChallengeAnswer03;
    private String mail;
    private String firstName;
    
   
    public void setUserAccountStatus(String userAccountStatus) {
        this.userAccountStatus = userAccountStatus;
    }

    public String getUserAccountStatus() {
        return userAccountStatus;
    }

    public void setSAMAccountname(String sAMAccountname) {
        this.sAMAccountname = sAMAccountname;
    }

    public String getSAMAccountname() {
        return sAMAccountname;
    }

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeType(String employeeType) {
        this.employeeType = employeeType;
    }

    public String getEmployeeType() {
        return employeeType;
    }

    public void setOmChallengeQuestion1(String omChallengeQuestion1) {
        this.omChallengeQuestion1 = omChallengeQuestion1;
    }

    public String getOmChallengeQuestion1() {
        return omChallengeQuestion1;
    }

    public void setOmChallengeQuestion2(String omChallengeQuestion2) {
        this.omChallengeQuestion2 = omChallengeQuestion2;
    }

    public String getOmChallengeQuestion2() {
        return omChallengeQuestion2;
    }

    public void setOmChallengeQuestion3(String omChallengeQuestion3) {
        this.omChallengeQuestion3 = omChallengeQuestion3;
    }

    public String getOmChallengeQuestion3() {
        return omChallengeQuestion3;
    }

    public void setOmChallengeAnswer01(String omChallengeAnswer01) {
        this.omChallengeAnswer01 = omChallengeAnswer01;
    }

    public String getOmChallengeAnswer01() {
        return omChallengeAnswer01;
    }

    public void setOmChallengeAnswer02(String omChallengeAnswer02) {
        this.omChallengeAnswer02 = omChallengeAnswer02;
    }

    public String getOmChallengeAnswer02() {
        return omChallengeAnswer02;
    }

    public void setOmChallengeAnswer03(String omChallengeAnswer03) {
        this.omChallengeAnswer03 = omChallengeAnswer03;
    }

    public String getOmChallengeAnswer03() {
        return omChallengeAnswer03;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getMail() {
        return mail;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }
}
