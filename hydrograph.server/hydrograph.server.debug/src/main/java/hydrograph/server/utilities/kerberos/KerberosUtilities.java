/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package hydrograph.server.utilities.kerberos;

import hydrograph.server.utilities.Constants;
import hydrograph.server.utilities.ServiceUtilities;
import hydrograph.server.utilities.kerberos.callback.UserPassCallbackHandler;
import hydrograph.server.service.HydrographService;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.net.URL;
import java.security.PrivilegedAction;
import javax.security.auth.Subject;

/**
 * Created by prabodhm on 12/26/2016.
 */
public class KerberosUtilities implements PrivilegedAction<Object> {

    private static Logger LOG = LoggerFactory.getLogger(KerberosUtilities.class);

    /**
     * @param userId
     * @param password
     * @param conf
     * @throws LoginException
     * @throws IOException
     */


    String userId,password;
    Configuration conf;
    LoginContext lc;

    public KerberosUtilities(String userId, String password, Configuration conf){

        this.userId=userId;
        this.password=password;
        this.conf=conf;

    }

    public void login(){

        try {
            applyKerberosToken(userId,password,conf);
        } catch (LoginException e) {
            LOG.error("Error while Logging in to Kerberos environment: "+e.getMessage());
        } catch (IOException e) {
            LOG.error("Error while Logging in to Kerberos environment: "+e.getMessage());
        }

    }

    private void applyKerberosToken(String userId, String password, Configuration conf)
            throws LoginException, IOException {
        String enableKerberos = ServiceUtilities.getServiceConfigResourceBundle().getString(Constants.ENABLE_KERBEROS);
        if (Boolean.parseBoolean(enableKerberos)) {
            LOG.debug("Kerberos is enabled. Kerberos ticket will be generated for user: " + userId);
            if (ServiceUtilities.getServiceConfigResourceBundle().containsKey(Constants.KERBEROS_DOMAIN_NAME)) {
                LOG.debug("Kerberos domain name is set in config. UserID will be updated with the domain name.");
                String kerberosDomainName = ServiceUtilities.getServiceConfigResourceBundle()
                        .getString(Constants.KERBEROS_DOMAIN_NAME);
                kerberosDomainName = kerberosDomainName.equals("") ? "" : "@" + kerberosDomainName;
                userId = userId + kerberosDomainName;
                LOG.debug("Updated userId: " + userId);
            }
            getKerberosToken(userId, password.toCharArray(), conf);
        }
    }

    /**
     *
     * @param user
     * @param password
     * @param configuration
     * @throws LoginException
     * @throws IOException
     */
    private void getKerberosToken(String user, char[] password, Configuration configuration)
            throws LoginException, IOException {
        LOG.trace("Entering method getKerberosToken() for user: " + user);
        URL url = HydrographService.class.getClassLoader().getResource("jaas.conf");
        System.setProperty("java.security.auth.login.config", url.toExternalForm());

        LOG.info("Generating Kerberos ticket for user: " + user);
        UserGroupInformation.setConfiguration(configuration);

        lc = new LoginContext("EntryName", new UserPassCallbackHandler(user, password));
        lc.login();

        Subject subject = lc.getSubject();
        UserGroupInformation.loginUserFromSubject(subject);
        Subject.doAs(subject, this);
        LOG.info("Kerberos ticket successfully generated for user: " + user);
    }

    public void logout(){
        try {
            lc.logout();
        } catch (LoginException e) {
            LOG.error("Error While Logging Out from  Kerberos environment: "+e.getMessage());
        }
    }

    @Override
    public Object run() {
        LOG.trace("Entering method run()");
        return null;
    }

}
