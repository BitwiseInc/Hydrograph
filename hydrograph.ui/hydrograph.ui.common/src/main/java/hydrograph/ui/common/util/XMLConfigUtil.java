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
 * limitations under the License.
 *******************************************************************************/

 
package hydrograph.ui.common.util;

import hydrograph.ui.common.Activator;
import hydrograph.ui.common.component.config.Component;
import hydrograph.ui.common.component.config.Config;
import hydrograph.ui.common.component.config.Policy;
import hydrograph.ui.common.component.policyconfig.CategoryPolicies;
import hydrograph.ui.common.component.policyconfig.PolicyConfig;
import hydrograph.ui.common.message.Messages;
import hydrograph.ui.logging.factory.LogFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.statushandlers.StatusManager;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;



/**
 * Utility class to read and validate the xml configuration for following :
 * <ul>
 * 	<li>Component</li>
 * 	<li>Policy</li>
 * </ul>
 * @author Bitwise
 */
public class XMLConfigUtil {
	private Logger logger=LogFactory.INSTANCE.getLogger(XMLConfigUtil.class);
	public static final XMLConfigUtil INSTANCE = new XMLConfigUtil();
	
	private static HashMap<String, Component> map = new HashMap<>();
	private static final String SEPARATOR = "/";
	
	public final static String CONFIG_FILES_PATH = Platform.getInstallLocation().getURL().getPath() + Messages.XMLConfigUtil_CONFIG_FOLDER;
	public final static String XML_CONFIG_FILES_PATH = Platform.getInstallLocation().getURL().getPath() + Messages.XMLConfigUtil_XML_CONFIG_FOLDER;
	public final static String COMPONENT_CONFIG_XSD_PATH = Platform.getInstallLocation().getURL().getPath()+Messages.XMLConfigUtil_COMPONENTCONFIG_XSD_PATH;
	public final static String POLICY_CONFIG_XSD_PATH = Platform.getInstallLocation().getURL().getPath()+Messages.XMLConfigUtil_POLICYCONFIG_XSD_PATH;
	public final static List<Component> componentList = new ArrayList<>();
	public PolicyConfig policyConfig ;
	
	private XMLConfigUtil() {}

	/** Reads the xml configuration files stored under the platform installation.
	 * 	These files contain the configuration required to create the component on UI. 
	 * @return see {@link Component}
	 * @throws RuntimeException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public List<Component> getComponentConfig() throws RuntimeException, SAXException, IOException {
		if(componentList != null && !componentList.isEmpty()){
			return componentList;
		}
		else{
			try{
				JAXBContext jaxbContext = JAXBContext.newInstance(Config.class);
				Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
				String[] configFileList = getFilteredFiles(XML_CONFIG_FILES_PATH, getFileNameFilter(Messages.XMLConfigUtil_FILE_EXTENTION));
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				dbf.setNamespaceAware(true);
				dbf.setExpandEntityReferences(false);
				dbf.setFeature(Constants.DISALLOW_DOCTYPE_DECLARATION,true);
				DocumentBuilder builder = dbf.newDocumentBuilder();
				for (int i = 0; i < configFileList.length; i++){
					logger.trace("Creating palette component: ", configFileList[i]);
					if(validateXMLSchema(COMPONENT_CONFIG_XSD_PATH, XML_CONFIG_FILES_PATH + SEPARATOR + configFileList[i])){
						
						Document document = builder.parse(new File(XML_CONFIG_FILES_PATH + SEPARATOR + configFileList[i]));
						Config config = (Config) unmarshaller.unmarshal(document);
						componentList.addAll(config.getComponent());
						builder.reset();
					}
				}
				validateAndFillComponentConfigList(componentList);
				return componentList;
			}catch(JAXBException | SAXException | IOException | ParserConfigurationException exception){
				Status status = new Status(IStatus.ERROR,Activator.PLUGIN_ID, "XML read failed", exception);
				StatusManager.getManager().handle(status, StatusManager.BLOCK);
				logger.error(exception.getMessage());
				throw new RuntimeException("Faild in reading XML Config files", exception); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Gets the component.
	 * 
	 * @param componentName
	 *            the component name
	 * @return the component
	 */
	public Component getComponent(String componentName){
			return map.get(componentName);
	}
	

	/** Filters out the files as per the applied filter and 
	 *  returns the file names array
	 * @param filePath directory location of the files
	 * @param filter criteria on which files are to be filtered
	 * @return
	 */
	public String[] getFilteredFiles(String filePath, FilenameFilter filter){
		File file = new File(filePath);
		String[] list = file.list(filter);
		return (list == null) ? new String[0] : list;
	}
	
	
	/** Creates a file name filter in order to filter out only the required files.
	 * @param extention the files to be filtered on ex. .xml
	 * @return FilenameFilter 
	 */
	public FilenameFilter getFileNameFilter(final String extention) {
		FilenameFilter filenameFilter = new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
			  if(name.lastIndexOf('.')>0)
               {
                  // get last index for '.' char
                  int lastIndex = name.lastIndexOf('.');
                  
                  // get extension
                  String str = name.substring(lastIndex);
                  
                  // match path name extension
                  if(str.equals(extention))
                  {
                     return true;
                  }
               }
               return false;
			}
		};
		return filenameFilter;
	}

	/**
	 * Gets the policy config.
	 * 
	 * @return the policy config
	 * @throws RuntimeException
	 *             the runtime exception
	 * @throws SAXException
	 *             the SAX exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public PolicyConfig getPolicyConfig() throws RuntimeException, SAXException, IOException {
		if(policyConfig !=null){
			return policyConfig;
		}
		else{
			try{
				JAXBContext jaxbContext = JAXBContext.newInstance(PolicyConfig.class);
				Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
				
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				dbf.setNamespaceAware(true);
				dbf.setExpandEntityReferences(false);
				dbf.setFeature(Constants.DISALLOW_DOCTYPE_DECLARATION,true);
				DocumentBuilder builder = dbf.newDocumentBuilder();
				
				String[] configFileList = getFilteredFiles(CONFIG_FILES_PATH + SEPARATOR + Messages.XMLConfigUtil_POLICY, getFileNameFilter(Messages.XMLConfigUtil_FILE_EXTENTION));
				for (int i = 0; i < configFileList.length; i++) {
					if(validateXMLSchema(POLICY_CONFIG_XSD_PATH, CONFIG_FILES_PATH + SEPARATOR + Messages.XMLConfigUtil_POLICY + SEPARATOR + configFileList[i]))	{
						Document document = builder.parse(new File(CONFIG_FILES_PATH + SEPARATOR
								+ Messages.XMLConfigUtil_POLICY + SEPARATOR + configFileList[i]));
						policyConfig = (PolicyConfig) unmarshaller.unmarshal(document);
						builder.reset();
					}
				}
				return policyConfig;
			}catch(JAXBException | SAXException | IOException | ParserConfigurationException  exception){
				Status status = new Status(IStatus.ERROR,Activator.PLUGIN_ID, "XML read failed", exception);
				StatusManager.getManager().handle(status, StatusManager.BLOCK);
				logger.error(exception.getMessage());
				throw new RuntimeException("Faild in reading XML Config files", exception); //$NON-NLS-1$
			}
		}
	}
	
	/**
	 * Returns the collection of policies.This collection contains :<br>
	 * <ol>
	 * <li><b>Master policies :</b> applicable to all components</li>
	 * <li><b>Category policies : </b> applicable to category</li>
	 * <li><b>component policies : </b> applicable to component only</li>
	 * </ol>
	 * @param component
	 * @param componentName
	 * @return
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws RuntimeException 
	 */
	public List<Policy> getPoliciesForComponent(Component component) throws RuntimeException, SAXException, IOException {
		List<Policy> policies = new ArrayList<>();
		PolicyConfig policyConfig = XMLConfigUtil.INSTANCE.getPolicyConfig();
		//put all master policies
		
		policies.addAll(policyConfig.getMasterpolicies().getPolicy());
		for (CategoryPolicies categoryPolicies : policyConfig.getCategorypolicies()) {
			if (categoryPolicies.getCategory().toString().equalsIgnoreCase(component.getCategory().toString())) {
				//put all category policies
				policies.addAll(categoryPolicies.getPolicy());
				//put all component policies
			}
		}
		policies.addAll(component.getPolicy());
		return policies;
	}
	
	/**
	 * Validates the xmls based on the provided XSD's constraints
	 * @param xsdPath
	 * @param xmlPath
	 * @return
	 * @throws Exception 
	 * @throws SAXException
	 * @throws IOException
	 */
	public  boolean validateXMLSchema(String xsdPath, String xmlPath) throws SAXException, IOException{
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema;
		try {
			schema = factory.newSchema(new File(xsdPath));
			Validator validator = schema.newValidator();
		    validator.validate(new StreamSource(new File(xmlPath)));
		} catch (SAXException | IOException exception) {
			logger.error(exception.getMessage());
			throw exception;
		     
		}
        return true;
	}
	
	private void validateAndFillComponentConfigList(List<Component> componentList) {
		for (Component component : componentList) {
			if(map.containsKey(component.getName())){
				Status status = new Status(IStatus.ERROR,Activator.PLUGIN_ID, 
						"One or more configuration files have similar names, reconfigure the files", null);
				StatusManager.getManager().handle(status, StatusManager.BLOCK);
				//remove all component configuration from list
				componentList.clear();
				throw new RuntimeException("One or more Component names are similar");
			}
			map.put(component.getName(), component);	
		}		
	}
}
