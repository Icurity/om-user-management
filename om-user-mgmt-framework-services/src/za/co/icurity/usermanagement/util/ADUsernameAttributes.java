package za.co.icurity.usermanagement.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import oracle.iam.identity.usermgmt.api.UserManager;
import oracle.iam.identity.usermgmt.vo.User;
import oracle.iam.platform.OIMClient;
import oracle.iam.platform.entitymgr.vo.SearchCriteria;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import za.co.icurity.usermanagement.vo.GetADUsernameInVO;
import za.co.icurity.usermanagement.vo.GetADUsernameOutVO;


public class ADUsernameAttributes {
    public ADUsernameAttributes() {
        super();
    }
    private static final Logger LOG = LoggerFactory.getLogger(ADUsernameAttributes.class);

    public GetADUsernameOutVO getADUsernameAttributes(OIMClient oimClient, GetADUsernameInVO getADUsernameInVO) {
        LOG.error(this + " getADUsername: In ADUsernameAttributes class");
        GetADUsernameOutVO getADUsernameOutVO = new GetADUsernameOutVO();
        Set<String> attrNames = null;
        List<User> users = null;
        HashMap<String, Object> parameters = null;
        SearchCriteria criteriaSurname = new SearchCriteria("Last Name", getADUsernameInVO.getSurname(), SearchCriteria.Operator.EQUAL);
        SearchCriteria criteriaIDNumber = new SearchCriteria("id_number", getADUsernameInVO.getIdNumber(), SearchCriteria.Operator.EQUAL);
        SearchCriteria criteriaIDType = new SearchCriteria("id_type", getADUsernameInVO.getIdType(), SearchCriteria.Operator.EQUAL);
        SearchCriteria criteriaEmail = new SearchCriteria("Email", getADUsernameInVO.getEmailAddress(), SearchCriteria.Operator.EQUAL);

        SearchCriteria criteriaSurnameIDNo = new SearchCriteria(criteriaSurname, criteriaIDNumber, SearchCriteria.Operator.AND);
        SearchCriteria criteriaIDTypeEmail = new SearchCriteria(criteriaIDType, criteriaEmail, SearchCriteria.Operator.AND);
        SearchCriteria criteria = new SearchCriteria(criteriaSurnameIDNo, criteriaIDTypeEmail, SearchCriteria.Operator.AND);

        attrNames = new HashSet<String>();
        attrNames.add("Employee Number");
        attrNames.add("User Login");
        attrNames.add("First Name");
        attrNames.add("Email");
        try {
            UserManager userManager_local = oimClient.getService(UserManager.class);
            users = userManager_local.search(criteria, attrNames, parameters);
            if (users != null && !users.isEmpty() && users.size() == 1 && users.get(0) != null) {
                getADUsernameOutVO.setStatus("Success");
                if (users.get(0).getAttribute("Employee Number") != null) {
                    getADUsernameOutVO.setUsernumber(users.get(0).getAttribute("Employee Number").toString());
                }
                if (users.get(0).getAttribute("User Login") != null) {
                    getADUsernameOutVO.setOmUserAlias(users.get(0).getAttribute("User Login").toString());
                }
                if (users.get(0).getAttribute("First Name") != null) {
                    getADUsernameOutVO.setFirstName(users.get(0).getAttribute("First Name").toString());
                }
                if (users.get(0).getAttribute("Email") != null) {
                    getADUsernameOutVO.setMail(users.get(0).getAttribute("Email").toString());
                }
            } else {
                getADUsernameOutVO.setStatus("Error");
                getADUsernameOutVO.setErrorMessage("No username/alias could be retrieved");
            }
        } catch (Exception ex) {
            LOG.error(this +" getADUsernameAttributes error "+ex.getMessage());
            getADUsernameOutVO.setStatus("Error");
            getADUsernameOutVO.setErrorMessage("No username/alias could be retrieved");
            return getADUsernameOutVO;
        }
        return getADUsernameOutVO;
    }
}
