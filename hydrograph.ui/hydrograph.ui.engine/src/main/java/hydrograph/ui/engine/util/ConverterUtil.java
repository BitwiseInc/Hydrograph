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

 
package hydrograph.ui.engine.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.slf4j.Logger;

import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.commontypes.TypeProperties;
import hydrograph.engine.jaxb.commontypes.TypeProperties.Property;
import hydrograph.engine.jaxb.main.Graph;
import hydrograph.engine.jaxb.main.ObjectFactory;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.engine.converter.Converter;
import hydrograph.ui.engine.converter.ConverterFactory;
import hydrograph.ui.engine.xpath.ComponentXpath;
import hydrograph.ui.engine.xpath.ComponentXpathConstants;
import hydrograph.ui.engine.xpath.ComponentsAttributeAndValue;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.logging.factory.LogFactory;




/**
 * The Class ConverterUtil.
 * This class has utility methods for generating target XML using UI objects.
 * 
 * @author Bitwise
 */
public class ConverterUtil {
	
	private static final String UNKNOWN_COMPONENTS_SEPERATOR = "=====Unknown-Components-Converted-Data-Seperator======";

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogFactory.INSTANCE.getLogger(ConverterUtil.class);
	
	/** The Constant INSTANCE. */
	public static final ConverterUtil INSTANCE = new ConverterUtil();
	private List<Component> unknownComponentLists;
	/**
	 * Instantiates a new converter util.
	 */
	private ConverterUtil(){
		
	}
	
	/**
	 * Convert UI objects to target xml.
	 *
	 * @param container
	 * @param validate
	 * @param outPutFile
	 * @param externalOutputFile
	 * @throws Exception
	 */
	public void convertToXML(Container container, boolean validate, IFile outPutFile,  IFileStore externalOutputFile) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		LOGGER.debug("Creating converter based on component");
			Graph graph = new ObjectFactory().createGraph();
			graph.setUniqueJobId(container.getUniqueJobId());
			graph.setName(getGraphName(outPutFile,externalOutputFile));
			List<Component> children = container.getUIComponentList();
			unknownComponentLists = new ArrayList<>();
			if(children != null && !children.isEmpty()){
				for (Component component : children) {
					Converter converter = ConverterFactory.INSTANCE.getConverter(component);
//					if(converter ==null){
//						unknownComponentLists.add(component);
//						continue;
//					}
					converter.prepareForXML();
					TypeBaseComponent typeBaseComponent = converter.getComponent();
					graph.getInputsOrOutputsOrStraightPulls().add(typeBaseComponent);
				}
			}
//			To-do will be removed in future
//			processUnknownComponents();
			graph.setRuntimeProperties(getRuntimeProperties(container));
			marshall(graph, validate, outPutFile,externalOutputFile);
	}
	
	
	/**
	 * Gets the graph name.
	 *
	 * @param outPutFile the out put file
	 * @param externalOutputFile the external output file
	 * @return the graph name
	 */
	private String getGraphName(IFile outPutFile, IFileStore externalOutputFile) {
		if (outPutFile != null && StringUtils.isNotBlank(outPutFile.getName()))
			return outPutFile.getName();
		else if (externalOutputFile != null && StringUtils.isNotBlank(externalOutputFile.getName()))
			return externalOutputFile.getName();
		else
			return "Temp.xml";
	}

	/**
	 * Marshall UI objects to target XML.
	 *
	 * @param graph
	 * @param validate
	 * @param outPutFile
	 * @param externalOutputFile
	 */
	private void marshall(Graph graph, boolean validate, IFile outPutFile, IFileStore externalOutputFile) {
		LOGGER.debug("Marshaling generated object into target XML");
		ByteArrayOutputStream out = null;
		try {
			 if (outPutFile!=null)
				 storeFileIntoWorkspace(graph, validate, outPutFile, out);
			else if(externalOutputFile!=null)
				storeFileIntoLocalFileSystem(graph, validate, externalOutputFile, out);
			else
				validateJobState(graph, validate, externalOutputFile, out);			
			
		} catch (JAXBException |CoreException| IOException exception) {
			LOGGER.error("Failed in marshal", exception);
		}finally{
			if(out != null){
				try {
					out.close();
				} catch (IOException e) {
				LOGGER.error("ERROR WHILE CLOSING OUT STREAM OF TARGETXML"+e);
				}
			}
		}
	}
	
	
	private void validateJobState(Graph graph, boolean validate, IFileStore externalOutputFile, ByteArrayOutputStream out) throws CoreException, JAXBException, IOException {
		JAXBContext jaxbContext = JAXBContext.newInstance(graph.getClass());
		Marshaller marshaller = jaxbContext.createMarshaller();
		out = new ByteArrayOutputStream();
	    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(graph, out);
		out = ComponentXpath.INSTANCE.addParameters(out);
	}

	/**
	 * Store file into local file system.
	 *
	 * @param graph
	 * @param externalOutputFile
	 * @param out
	 * @throws CoreException the core exception
	 * @throws JAXBException the JAXB exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void storeFileIntoLocalFileSystem(Graph graph, boolean validate, IFileStore externalOutputFile, ByteArrayOutputStream out) throws CoreException, JAXBException, IOException {
		
		File externalFile=externalOutputFile.toLocalFile(0, null);
		OutputStream outputStream = new FileOutputStream (externalFile.getAbsolutePath().replace(".job", ".xml")); 
		
		JAXBContext jaxbContext = JAXBContext.newInstance(graph.getClass());
		Marshaller marshaller = jaxbContext.createMarshaller();
		out = new ByteArrayOutputStream();
	    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(graph, out);
		out = ComponentXpath.INSTANCE.addParameters(out);
		out.writeTo(outputStream);
		outputStream.close();
	}

	/**
	 * Store file into workspace.
	 *
	 * @param graph the graph
	 * @param outPutFile the out put file
	 * @param out the out
	 * @throws JAXBException the JAXB exception
	 * @throws CoreException the core exception
	 */
	private void storeFileIntoWorkspace(Graph graph, boolean validate, IFile outPutFile, ByteArrayOutputStream out) throws JAXBException, CoreException {
		
		JAXBContext jaxbContext = JAXBContext.newInstance(graph.getClass());
		Marshaller marshaller = jaxbContext.createMarshaller();
		out = new ByteArrayOutputStream();
	    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	    marshaller.marshal(graph, out);
	    out = ComponentXpath.INSTANCE.addParameters(out);

	    /*
	     * To-do will be removed in future
	     * String updatedXML=escapeXml(out.toString());
	    out.reset();
	    try {
	    	if(!unknownComponentLists.isEmpty())
			out.write(updatedXML.getBytes());
		} catch (IOException ioException) {
			LOGGER.error("Unable to update escape sequene in xml file",ioException);
		}*/
	    if (outPutFile.exists()){
	    	outPutFile.setContents(new ByteArrayInputStream(out.toByteArray()), true,false, null);
	    }else{
	    	outPutFile.create(new ByteArrayInputStream(out.toByteArray()),true, null);
		}		
	}
	
	@Deprecated
	private void processUnknownComponents() {
		StringBuffer buffer=new StringBuffer();
		buffer.append(UNKNOWN_COMPONENTS_SEPERATOR);
		for(Component component:unknownComponentLists){
			String xmlText=(String)component.getProperties().get(Constants.XML_CONTENT_PROPERTY_NAME);
			xmlText = xmlText.substring(xmlText.indexOf('\n') + 1);
			xmlText = xmlText.substring(xmlText.indexOf('\n') + 1, xmlText.lastIndexOf('\n') - 13);
			buffer.append(xmlText+"\n\n");
		}
		ComponentXpath.INSTANCE.getXpathMap().put(ComponentXpathConstants.GRAPH_XPATH.value(),new ComponentsAttributeAndValue(true,buffer.toString(),false));
	}

	/**
	 * Gets the runtime properties.
	 *
	 * @param container the container
	 * @return the runtime properties
	 */
	private TypeProperties getRuntimeProperties(Container container) {
		TypeProperties typeProperties = null;
		Map<String, String> runtimeProps = container.getGraphRuntimeProperties();
		if (runtimeProps != null && !runtimeProps.isEmpty()) {
			typeProperties = new TypeProperties();
			List<TypeProperties.Property> runtimePropertyList = typeProperties.getProperty();
			for (Map.Entry<String, String> entry : runtimeProps.entrySet()) {
				Property runtimeProperty = new Property();
				runtimeProperty.setName(entry.getKey());
				runtimeProperty.setValue(entry.getValue());
				runtimePropertyList.add(runtimeProperty);
			}
		}
		return typeProperties;
	}
	
	@Deprecated
	private String escapeXml(String xmlText) {
		String[] arr=xmlText.split(UNKNOWN_COMPONENTS_SEPERATOR);
		StringBuffer buffer=new StringBuffer();
		if(arr.length==2){
			buffer.append(arr[0]);
			buffer.append(arr[1].replaceAll("&amp;","&").replaceAll( "&gt;",">").replaceAll("&lt;","<").replaceAll("&quot;","\"").replaceAll( "&apos;","'"));
		}
		return buffer.toString();
	    
	}
	
}
