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
package hydrograph.engine.core.utilities;

import hydrograph.engine.core.component.utils.SafeResourceClose;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * The Class PropertiesHelper.
 *
 * @author Bitwise
 */
public class PropertiesHelper {

    private static final Logger LOG = LoggerFactory
            .getLogger(PropertiesHelper.class);

    /**
     * Constructor marked as private to disable instantiation
     */
    private PropertiesHelper() {

    }

    /**
     * Reads the property file and returns an object of
     * {@link Properties} class for that property file.
     *
     * @param propertyFileName
     *            the properties file for whom the
     *            <code>Properties</code> object is to be retrieved.
     * @return an object of {@link Properties} class for that property
     *         file.
     * @throws IOException
     *             if there is a problem reading the properties file.
     */
    public static Properties getProperties(String propertyFileName) throws IOException {
        Properties properties = new Properties();
        InputStream in = ClassLoader
                .getSystemResourceAsStream(propertyFileName);
        try {
            properties.load(in);
            LOG.trace("Property file name: '" + propertyFileName
                    + "' successfully loaded.");
        } catch (IOException e) {
            LOG.error("Error reading properties file: '" + propertyFileName
                    + "'");
            throw new RuntimeException(e);
        } finally {
            try {
                SafeResourceClose.safeInputStreamClose(in);
            } catch (IOException e) {
                // Exception not thrown as properties file was already read. An
                // exception in closing input stream will
                // cause a memory leak, however, the desired functionality works
                LOG.warn("Exception in closing input stream for properties file: '"
                        + propertyFileName + "'", e);
            }
        }

        return properties;
    }

}
