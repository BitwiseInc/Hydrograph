/*******************************************************************************
 * Copyright 2017 Capital One Services, LLC and Bitwise, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 *******************************************************************************/

package hydrograph.engine.utilities;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.thrift.DelegationTokenIdentifier;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.Token;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HiveMetastoreTokenProvider {

	private static Logger LOG = LoggerFactory.getLogger(HiveMetastoreTokenProvider.class);

	private HiveMetastoreTokenProvider() {

	}

	public static void obtainTokenForHiveMetastore(Configuration conf) throws TException, IOException {
		conf.addResource(new Path(HiveConfigurationMapping.getHiveConf("path_to_hive_site_xml")));
		HiveConf hiveConf = new HiveConf();
		hiveConf.addResource(conf);
		try {
			UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
			HiveMetaStoreClient hiveMetaStoreClient = new HiveMetaStoreClient(hiveConf);

			if (UserGroupInformation.isSecurityEnabled()) {
				String metastore_uri = conf.get("hive.metastore.uris");

				LOG.trace("Metastore URI:" + metastore_uri);

				// Check for local metastore
				if (metastore_uri != null && metastore_uri.length() > 0) {
					String principal = conf.get("hive.metastore.kerberos.principal");
					String username = ugi.getUserName();

					if (principal != null && username != null) {
						LOG.debug("username: " + username);
						LOG.debug("principal: " + principal);

						String tokenStr;
						try {
							// Get a delegation token from the Metastore.
							tokenStr = hiveMetaStoreClient.getDelegationToken(username, principal);
							// LOG.debug("Token String: " + tokenStr);
						} catch (TException e) {
							LOG.error(e.getMessage(), e);
							throw new RuntimeException(e);
						}

						// Create the token from the token string.
						Token<DelegationTokenIdentifier> hmsToken = new Token<DelegationTokenIdentifier>();
						hmsToken.decodeFromUrlString(tokenStr);
						// LOG.debug("Hive Token: " + hmsToken);

						// Add the token to the credentials.
						ugi.addToken(new Text("hive.metastore.delegation.token"), hmsToken);
						LOG.trace("Added hive.metastore.delegation.token to conf.");
					} else {
						LOG.debug("Username or principal == NULL");
						LOG.debug("username= " + username);
						LOG.debug("principal= " + principal);
						throw new IllegalArgumentException("username and/or principal is equal to null!");
					}

				} else {
					LOG.info("HiveMetaStore configured in local mode");
				}
			}
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (MetaException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
}
