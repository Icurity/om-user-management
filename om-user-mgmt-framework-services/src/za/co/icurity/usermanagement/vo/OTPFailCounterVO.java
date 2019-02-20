package za.co.icurity.usermanagement.vo;

public class OTPFailCounterVO {
    private String username;
    private String otpValidFlag;
    
    public OTPFailCounterVO() {
        super();
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }


    public void setOtpValidFlag(String otpValidFlag) {
        this.otpValidFlag = otpValidFlag;
    }

    public String getOtpValidFlag() {
        return otpValidFlag;
    }
}
