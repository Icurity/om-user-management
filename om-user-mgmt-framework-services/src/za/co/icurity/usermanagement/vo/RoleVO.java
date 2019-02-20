package za.co.icurity.usermanagement.vo;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
 
public class RoleVO {

	 private String displayName;
	 private String name;
         private String roleDescription;


    public RoleVO() {
        super();
    }


	public String getDisplayName() {
		return displayName;
	}


	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}
        public void setRoleDescription(String roleDescription) {
            this.roleDescription = roleDescription;
        }

        public String getRoleDescription() {
            return roleDescription;
        }
        

   
}
