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


public class CellNumberExistsUtil {
    public CellNumberExistsUtil() {
        super();
    }
    private static final Logger LOG = LoggerFactory.getLogger(CellNumberExistsUtil.class);

    public boolean checkExistingCellphoneAttribute(OIMClient oimClient, String cellnumber) {

        List<User> users = null;
        HashMap<String, Object> parameters = null;
        Set<String> attrNames = null;

        SearchCriteria criteria = new SearchCriteria("Telephone Number", cellnumber, SearchCriteria.Operator.EQUAL);
        attrNames = new HashSet<String>();
        attrNames.add("Last Name");
        attrNames.add("User Login");
        try {
            UserManager userManager_local = oimClient.getService(UserManager.class);
            users = userManager_local.search(criteria, attrNames, parameters);
            if (users != null && !users.isEmpty() && users.get(0) != null && users.get(0).getLastName() != null) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            LOG.error(this + " checkExistingCellphoneAttribute Search error ");
        }
        return false;
    }
}
