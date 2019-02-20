package za.co.icurity.usermanagement.vo;

public class FetchUsernameOutVO extends StatusOutVO{
    private String username;
    private String errorMessage;

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public FetchUsernameOutVO() {
        super();
    }
}
