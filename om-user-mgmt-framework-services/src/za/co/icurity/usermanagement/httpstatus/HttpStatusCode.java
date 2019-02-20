package za.co.icurity.usermanagement.httpstatus;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import org.springframework.http.HttpStatus;


@JsonInclude(Include.NON_NULL)
public class HttpStatusCode {
    public HttpStatusCode() {
        super();
    }

    /**
     * @param status
     * @param statusMessage
     * @return
     */
    public HttpStatus getHttpStatus(String status, String statusMessage) {
        if (status !=null && (status.equalsIgnoreCase("Success") || status.equalsIgnoreCase("SUCCESSFUL") )) {
            return HttpStatus.OK;
        } else {
            if (statusMessage !=null && statusMessage.contains("Mandatory field")) {
                return HttpStatus.BAD_REQUEST;
            } else if (statusMessage !=null && (statusMessage.contains("System unavailable") || statusMessage.contains("Failed to search the user"))) {
                return HttpStatus.SERVICE_UNAVAILABLE;
            } else if (statusMessage !=null && (statusMessage.contains("No user found for username") || statusMessage.contains("Temporary password generated successfully") )) {  
                return HttpStatus.OK;
            } else {
                return HttpStatus.OK;
            }
        }
    }
}
