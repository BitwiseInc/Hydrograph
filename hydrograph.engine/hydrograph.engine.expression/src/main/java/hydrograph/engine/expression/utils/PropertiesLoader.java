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
package hydrograph.engine.expression.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * The Class PropertiesLoader.
 *
 * @author Bitwise
 */
public class PropertiesLoader {
	 private static Logger LOG =
	 LoggerFactory.getLogger(PropertiesLoader.class);

	/**
	 * Constructor marked as private to disable instantiation
	 */
	private PropertiesLoader() {

	}

	/**
	 * Reads the property file and returns an object of
	 * {@link PropertiesLoader} class for that property file.
	 * 
	 * @param propertyFileName
	 *            the properties file for whom the
	 *            <code>OrderedProperties</code> object is to be retrieved.
	 * @return an object of {@link PropertiesLoader} class for that property
	 *         file.
	 * @throws IOException
	 *             if there is a problem reading the properties file.
	 */
	public static Properties getProperties(String propertyFileName) throws IOException {
		// LOG.trace("Entering method : 'getProperties', file name: '" +
		// propertyFileName + "'");
		Properties properties = new Properties();
		File file = new File(propertyFileName);
		InputStream in;
		if(!getFileExtension(file).equals("properties"))
			throw new RuntimeException("file extension should be .properties");
		if (file.isFile())
			in = new FileInputStream(file);
		else
			in = Thread.currentThread().getContextClassLoader().getResourceAsStream(propertyFileName);
		// InputStream in = ClassLoader
		// .getSystemResourceAsStream(propertyFileName);
		try {
			properties.load(in);
			// LOG.trace("Property file name: '" + propertyFileName + "'
			// successfully loaded.");
		} catch (IOException e) {
			// LOG.error("Error reading properties file: '" + propertyFileName +
			// "'");
			throw new RuntimeException(e);
		} finally {
			try {
				safeInputStreamClose(in);
			} catch (IOException e) {
				// Exception not thrown as properties file was already read. An
				// exception in closing input stream will
				// cause a memory leak, however, the desired functionality works
				// LOG.warn("Exception in closing input stream for properties file:
				// '" + propertyFileName + "'", e);
			}
		}


		return properties;
	}
	
	private static String getFileExtension(File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
        return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }

	public static void safeInputStreamClose(InputStream is) throws IOException {
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				LOG.warn("Exception in closing input stream. The error message is " + e.getMessage());
				throw e;
			}
		}
	}

}