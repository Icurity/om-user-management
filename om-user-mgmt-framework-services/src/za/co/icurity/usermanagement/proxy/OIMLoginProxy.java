package za.co.icurity.usermanagement.proxy;

import java.util.Hashtable;
import java.util.Properties;

import javax.security.auth.login.LoginException;

import oracle.iam.platform.OIMClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import za.co.icurity.usermanagement.util.PropertiesUtil;
import za.co.icurity.usermanagement.vo.UserOutVO;


public class OIMLoginProxy {

    private static final Logger LOG = LoggerFactory.getLogger(OIMLoginProxy.class);
    @Autowired
    private UserOutVO createUserOutVO;
    @Autowired
    private Environment environment;

    /**
     * Login to OIMClient
     * @return oimClient
     * @throws LoginException Properties values must be set and valid credentials
     * @throws Exception
     */
    public OIMClient userLogin() {

        Properties properties = null;
        OIMClient oimClient = null;
        try {
            properties = PropertiesUtil.getProperties();
        } catch (Exception e) {
            LOG.warn(this + " OIMClient getProperties " + e.getClass().getName() + " : " + e.getMessage());
            return null;
        }
        Hashtable<String, String> env = new Hashtable<String, String>();
        System.setProperty("java.security.auth.login.config", properties.getProperty("auth_conf"));
        System.setProperty("APPSERVER_TYPE", properties.getProperty("appserver_type"));
        System.setProperty("weblogic.Name", properties.getProperty("weblogic_name"));

        env.put(OIMClient.JAVA_NAMING_FACTORY_INITIAL, "weblogic.jndi.WLInitialContextFactory");
        env.put(OIMClient.JAVA_NAMING_PROVIDER_URL, properties.getProperty("oim_url"));
        oimClient = new OIMClient(env);

        try {
            if (oimClient != null)
                oimClient.login(properties.getProperty("oim_username"),
                                properties.getProperty("oim_password").toCharArray());
            LOG.info(this + " Log to oimClient successful");
        } catch (LoginException ex) {
            /* if (logger.isLoggable(Level.SEVERE)) {
                logger.logp(Level.SEVERE, CLASS_NAME, "createUser", "Error: " + ex.getMessage());
            }*/
            createUserOutVO.setStatus("Error");
            createUserOutVO.setErrorMessage("Exception while logging in. Please try again.");
            LOG.error(this + "Error on login to oimClient " + createUserOutVO.getErrorMessage());
            return null;
        } catch (Exception e) {
            /* if (logger.isLoggable(Level.SEVERE)) {
                logger.logp(Level.SEVERE, CLASS_NAME, "createUser", "Error: " + e.getMessage());
            }*/
            System.out.println("LoginException " + e);
            createUserOutVO.setStatus("Error");
            createUserOutVO.setErrorMessage("Exception while logging in. Please try again.");
            LOG.error(this + "Error on login to oimClient " + createUserOutVO.getErrorMessage());
            return null;
        }
        return oimClient;

    }

}
