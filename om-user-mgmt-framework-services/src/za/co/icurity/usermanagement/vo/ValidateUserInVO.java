package za.co.icurity.usermanagement.vo;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ch.qos.logback.classic.Level;

/**
 * @author icurity
 *
 */
 
public class ValidateUserInVO {
	
	@Autowired
	private UserOutVO createUserOutVO;
	
	/*This method is to validate the input fields*/
	public UserOutVO validateUserInVO(UserInVO createUserVO) {
		
		  createUserOutVO.setStatus(""); 	
		 StringBuffer buffer = new StringBuffer();
	        if (createUserVO == null || createUserVO.getLastName() == null || createUserVO.getLastName().trim().isEmpty() || createUserVO.getPassword() == null || createUserVO.getPassword().trim().isEmpty() || 
	        		createUserVO.getUsername() == null || createUserVO.getUsername().trim().isEmpty() || createUserVO.getDateOfBirth() == null || createUserVO.getDateOfBirth().trim().isEmpty() 
	        		|| createUserVO.getIdNumber() == null || createUserVO.getIdNumber().trim().isEmpty()) {
	            createUserOutVO.setStatus("Error");
	            if (createUserVO.getLastName() == null || createUserVO.getLastName().trim().isEmpty()) {
	                buffer.append("Mandatory field : Last Name" + System.getProperty("line.separator"));
	            }
	            if (createUserVO.getPassword() == null || createUserVO.getPassword().trim().isEmpty()) {
	                buffer.append("Mandatory field : Password" + System.getProperty("line.separator"));
	            }
	            if (createUserVO.getUsername() == null || createUserVO.getUsername().trim().isEmpty()) {
	                buffer.append("Mandatory field : Username" + System.getProperty("line.separator"));
	            }
	            if (createUserVO.getDateOfBirth() == null || createUserVO.getDateOfBirth().trim().isEmpty()) {
	                buffer.append("Mandatory field : Date of Birth" + System.getProperty("line.separator"));
	            }
	            if (createUserVO.getIdNumber() == null || createUserVO.getIdNumber().trim().isEmpty()) {
	                buffer.append("Mandatory field : ID Number" + System.getProperty("line.separator"));
	            }
	            System.out.println("buffer in 1"+buffer);
	            createUserOutVO.setErrorMessage(buffer.toString());
	           /* if (logger.isLoggable(Level.SEVERE)) {
	                logger.logp(Level.SEVERE, CLASS_NAME, "createUser", "Validation Errors: " + buffer.toString());
	            } */
	            return createUserOutVO;
	        } else {
	            if (createUserVO.getDateOfBirth() == null || !createUserVO.getDateOfBirth().matches("^(3[01]|[12][0-9]|0[1-9])/(1[0-2]|0[1-9])/[0-9]{4}$")) {
	                createUserOutVO.setStatus("Error");
	                buffer.append("Please enter date in format dd/MM/yyyy" + System.getProperty("line.separator"));
	                createUserOutVO.setErrorMessage(buffer.toString());
	                System.out.println("buffer in 2"+buffer);
	              /*  if (logger.isLoggable(Level.SEVERE)) {
	                    logger.logp(Level.SEVERE, CLASS_NAME, "createUser", "Validation errors: " + buffer.toString());
	                }*/
	                return createUserOutVO;
	            }
	        }
	        
		return createUserOutVO;
		
	}
	
}
