package za.co.icurity.usermanagement.vo;

public class FetchUsernameInVO {

    private String lastName;
    private String cellphone;

    public FetchUsernameInVO() {
        super();
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getCellphone() {
        return cellphone;
    }
}
