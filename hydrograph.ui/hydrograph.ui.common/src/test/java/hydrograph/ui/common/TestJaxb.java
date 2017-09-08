/********************************************************************************
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
 ******************************************************************************/


package hydrograph.ui.common;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import junit.framework.Assert;

import org.eclipse.core.runtime.Platform;
import org.junit.Test;
import org.xml.sax.SAXException;

import hydrograph.ui.common.component.config.Component;
import hydrograph.ui.common.component.config.Config;
import hydrograph.ui.common.component.config.ObjectFactory;
import hydrograph.ui.common.message.Messages;
import hydrograph.ui.common.util.XMLConfigUtil;


public class TestJaxb {

	@Test
	public void testReadFile() throws Exception{
//		String filePath = Platform.getInstallLocation().getURL().getPath() + Messages.XMLConfigUtil_CONFIG_FOLDER;
//		
//		ObjectFactory factory = new ObjectFactory();
//		JAXBContext jaxbContext = JAXBContext.newInstance(Config.class);
//		Marshaller marshaller = jaxbContext.createMarshaller();
//		
//		File directory = new File(filePath);
//		if (!directory.exists()) {
//			directory.mkdir();
//		}
//		
//		List<Component> components = null;
//		Config config = null;
//		
//		File file1 = null;
//		File file2 = null;
//		try{
//			file1 = File.createTempFile("file1", ".xml", directory);
//			config = factory.createConfig();
//			marshaller.marshal(config, file1);
//			components = XMLConfigUtil.INSTANCE.getComponentConfig();
//			Assert.assertEquals(config.getComponent().size(), components.size());
//	
//			Component component = factory.createComponent();
//			config.getComponent().add(component);
//			
//			file2 = File.createTempFile("file2", ".xml", directory);
//			marshaller.marshal(config, file2);
//			components = XMLConfigUtil.INSTANCE.getComponentConfig();
//			Assert.assertEquals(config.getComponent().size(), components.size());
//		}
//		finally{
//			file2.deleteOnExit();
//			file1.deleteOnExit();
//			directory.deleteOnExit();
//		}
	}
	
	@Test
	public void testFilteredFiles(){
		/*FilenameFilter fileNameFilter = XMLConfigUtil.INSTANCE.getFileNameFilter(Messages.XMLConfigUtil_FILE_EXTENTION);
		String filePath = Platform.getInstallLocation().getURL().getPath() + Messages.XMLConfigUtil_XML_CONFIG_FOLDER;
		String[] filteredFiles = XMLConfigUtil.INSTANCE.getFilteredFiles(filePath, fileNameFilter);
		
		
		File file = new File(filePath);
		String[] fileList = file.list();
		int count = 0;
		if(fileList != null && fileList.length != 0){
			for (int i = 0; i < fileList.length; i++) {
				if(fileList[i].endsWith(Messages.XMLConfigUtil_FILE_EXTENTION))
					count ++;
			}
		}
		else{
			Assert.assertEquals(0, filteredFiles.length);
			return;
		}
		
		Assert.assertEquals(count, filteredFiles.length);*/
	}
	@Test
	public void itShouldValidateXmlWithXsd() throws Exception
	{
		/*String xsdPath="../hydrograph.ui.product/resources/config/xsds/ComponentConfig.xsd";
		String xmlPath="../hydrograph.ui.product/resources/config/xml/inputdelimited.xml";
		Assert.assertTrue(XMLConfigUtil.INSTANCE.validateXMLSchema(xsdPath,xmlPath));*/
	}
}
