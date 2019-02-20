package za.co.icurity.usermanagement.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public class GenerateTempPasswordOutVO extends StatusOutVO {
    public GenerateTempPasswordOutVO() {
        super();
    }

    private String password;

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
