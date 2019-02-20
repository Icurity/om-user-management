package za.co.icurity.usermanagement.vo;

public class CheckExistingIDNumberOutVO extends StatusOutVO {
    
    private String idNumberExists;
    
    public CheckExistingIDNumberOutVO() {
        super();
    }

    public void setIdNumberExists(String idNumberExists) {
        this.idNumberExists = idNumberExists;
    }

    public String getIdNumberExists() {
        return idNumberExists;
    }
}
