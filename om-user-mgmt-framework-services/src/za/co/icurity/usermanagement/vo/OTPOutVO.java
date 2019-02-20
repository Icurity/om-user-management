package za.co.icurity.usermanagement.vo;

public class OTPOutVO {
    private String status;
    private String errorMessage;
    private int otpFailCounter;
    
    public OTPOutVO() {
        super();
    }
    
    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }


    public void setOtpFailCounter(int otpFailCounter) {
        this.otpFailCounter = otpFailCounter;
    }

    public int getOtpFailCounter() {
        return otpFailCounter;
    }
}
