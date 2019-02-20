package za.co.icurity.usermanagement.util;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;

import javax.naming.directory.SearchResult;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import za.co.icurity.usermanagement.proxy.OVDLoginProxy;
import za.co.icurity.usermanagement.vo.ADSecurityAnswersOutVO;
 

public class ValidateADSecurityAnswersUtil {
    public ValidateADSecurityAnswersUtil() {
        super();
    }
    private static final Logger LOG = LoggerFactory.getLogger(ValidateADSecurityAnswersUtil.class);
    static final StringUtil stringUtil = null;
    static final String searchBase = "cn=users";
    
    public ADSecurityAnswersOutVO validateADSecurityAnswers(DirContext dirContext, String username) throws Exception {
        ADSecurityAnswersOutVO adSecurityAnswersOutVO = new ADSecurityAnswersOutVO();
        OVDLoginProxy ovdLoginProxy = new OVDLoginProxy();
        try {
            String usernametmp;
            usernametmp = stringUtil.escapeMetaCharacters(username);

            //String searchFilter = "(|(uid=" + usernametmp + ")(omUserAlias=" + usernametmp + "))";
            String searchFilter = "(|(uid=" + usernametmp + ")(cn=" + usernametmp + "))";
            SearchResult searchResult = null;

            try {
                searchResult = ovdLoginProxy.findUserAttributes(dirContext, searchBase, searchFilter);
            } catch (Exception e) {
                adSecurityAnswersOutVO.setStatus("Error");
                adSecurityAnswersOutVO.setErrorMessage("Failed to search the user " + username);
                LOG.error(this + " validateADSecurityAnswers: Failed to search with the searchFilter " + searchFilter +
                          " for the user " + username);
                e.printStackTrace();
                return adSecurityAnswersOutVO;
            }
            Attributes attributes = null;
            if (searchResult != null) {
                attributes = searchResult.getAttributes();
                LOG.info(this + " validateADSecurityAnswers: User attributes from ovd found for the user " + username);
            } else {
                LOG.info(this + " validateADSecurityAnswers: User does not Exist.Please provide a valid username " +
                         username);
                adSecurityAnswersOutVO.setStatus("Error");
                adSecurityAnswersOutVO.setErrorMessage("User does not Exist.Please provide a valid username " +
                                                       username);
                return adSecurityAnswersOutVO;
            }

            if (attributes != null) {
                for (NamingEnumeration ae = attributes.getAll(); ae.hasMore(); ) {
                    Attribute attr = (Attribute)ae.next();
                    /*  Attribute ID:obfirstlogin Attribute Value:false
                    Attribute ID:ismemberof Attribute Value:cn=GWBroker,cn=groups,dc=oldmutual,dc=co,dc=za,dc=dev
                    Attribute ID:memberof Attribute Value:CN=R-CalmILMSAdmin Members Group,OU=omGroups,OU=Old Mutual,DC=Dev,DC=SSA,DC=LocalHost
                    Attribute ID:obpasswordchangeflag Attribute Value:0
                    Attribute ID:objectClass Attribute Value:user
                    Attribute ID:uid Attribute Value:RUARIPLINT
                    Attribute ID:omchallengeanswer03 Attribute Value:C
                    Attribute ID:omchallengeanswer02 Attribute Value:B
                    Attribute ID:cn Attribute Value:33951996
                    Attribute ID:omchallengequestion3 Attribute Value:In what month was your spouse born?
                    Attribute ID:omchallengeanswer01 Attribute Value:A
                    Attribute ID:employeenumber Attribute Value:33951996
                    Attribute ID:omuseraccountstatus Attribute Value:Active
                    Attribute ID:omchallengequestion2 Attribute Value:In what month was your father born?
                    Attribute ID:omdateofbirth Attribute Value:04/03/1969
                    Attribute ID:omchallengequestion1 Attribute Value:In what month was your mother born?
                    Attribute ID:omuseralias Attribute Value:RuariPlint */

                    if (attr.getID().equalsIgnoreCase("omuseraccountstatus")) {
                        adSecurityAnswersOutVO.setUserAccountStatus(attr.get().toString());
                    }
                    if (attr.getID().equalsIgnoreCase("omchallengequestion1")) {
                        adSecurityAnswersOutVO.setOmChallengeQuestion1(attr.get().toString());
                    }
                    if (attr.getID().equalsIgnoreCase("omchallengequestion2")) {
                        adSecurityAnswersOutVO.setOmChallengeQuestion2(attr.get().toString());
                    }
                    if (attr.getID().equalsIgnoreCase("omchallengequestion3")) {
                        adSecurityAnswersOutVO.setOmChallengeQuestion3(attr.get().toString());
                    }
                    if (attr.getID().equalsIgnoreCase("omChallengeAnswer01")) {
                        adSecurityAnswersOutVO.setOmChallengeAnswer01(attr.get().toString());
                    }
                    if (attr.getID().equalsIgnoreCase("omChallengeAnswer02")) {
                        adSecurityAnswersOutVO.setOmChallengeAnswer02(attr.get().toString());
                    }
                    if (attr.getID().equalsIgnoreCase("omChallengeAnswer03")) {
                        adSecurityAnswersOutVO.setOmChallengeAnswer03(attr.get().toString());
                    }
                    if (attr.getID().equalsIgnoreCase("employeenumber")) { //check with RK
                        adSecurityAnswersOutVO.setEmployeeID(attr.get().toString());
                    }
                    /*    if (attr.getID().equalsIgnoreCase("sAMAccountname")) {
                        adSecurityAnswersOutVO.setSAMAccountname(attr.get().toString());
                    }
                    if (attr.getID().equalsIgnoreCase("employeeID")) {
                        adSecurityAnswersOutVO.setEmployeeID(attr.get().toString());
                    }
                    if (attr.getID().equalsIgnoreCase("employeeType")) {
                        adSecurityAnswersOutVO.setEmployeeType(attr.get().toString());
                    }

                    if (attr.getID().equalsIgnoreCase("mail")) {
                        adSecurityAnswersOutVO.setMail(attr.get().toString());
                    }
                    if (attr.getID().equalsIgnoreCase("firstName")) {
                        adSecurityAnswersOutVO.setFirstName(attr.get().toString());
                    } */

                }
            }
            adSecurityAnswersOutVO.setStatus("Success");
            LOG.info(this + " validateADSecurityAnswers Succeess for the user: " + username);
            return adSecurityAnswersOutVO;
        } catch (Exception e) {
            adSecurityAnswersOutVO.setStatus("Error");
            adSecurityAnswersOutVO.setErrorMessage("System unavailable at the moment, please try again");
            LOG.error(this + " Error on validateADSecurityAnswers. ERROR: " + e.getMessage());
            return adSecurityAnswersOutVO;
        } finally {
            if (dirContext != null) {
                dirContext.close();
            }
        } 
    }
}
