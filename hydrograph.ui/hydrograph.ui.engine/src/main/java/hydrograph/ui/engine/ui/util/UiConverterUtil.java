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

 
package hydrograph.ui.engine.ui.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Scanner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.TextUtilities;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.thoughtworks.xstream.XStream;

import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.main.Graph;
import hydrograph.ui.common.util.CanvasDataAdapter;
import hydrograph.ui.common.util.ComponentCacheUtil;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.XMLConfigUtil;
import hydrograph.ui.common.util.XMLUtil;
import hydrograph.ui.engine.exceptions.EngineException;
import hydrograph.ui.engine.parsing.XMLParser;
import hydrograph.ui.engine.ui.converter.LinkingData;
import hydrograph.ui.engine.ui.converter.UiConverter;
import hydrograph.ui.engine.ui.converter.UiConverterFactory;
import hydrograph.ui.engine.ui.exceptions.ComponentNotFoundException;
import hydrograph.ui.engine.ui.helper.ConverterUiHelper;
import hydrograph.ui.engine.ui.repository.ImportedJobsRepository;
import hydrograph.ui.engine.ui.repository.ParameterData;
import hydrograph.ui.engine.ui.repository.UIComponentRepo;
import hydrograph.ui.engine.ui.xygenration.CoordinateProcessor;
import hydrograph.ui.engine.util.ConverterUtil;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.ComponentLabel;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.graph.model.ModelConstants;
import hydrograph.ui.graph.model.processor.DynamicClassProcessor;
import hydrograph.ui.graph.model.utils.GenerateUniqueJobIdUtil;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.messages.Messages;

/**
 * The class UiConverterUtil
 * 
 * @author Bitwise
 * 
 */
public class UiConverterUtil {
	private static final Logger LOGGER = LogFactory.INSTANCE.getLogger(UiConverterUtil.class);
	private static final String FIXED_OUTPUT_PORT = "out0";
	private static final String FIXED_UNUSED_PORT = "unused0";
	private UIComponentRepo componentRepo ;
	
	public UiConverterUtil() {
		componentRepo = new UIComponentRepo();
	}

	/**
	 * Initiates the conversion process of source xml into graph
	 * 
	 * @param sourceXML
	 *            , XML file that will be executed in engine.
	 * @param jobFile
	 *            , Job file path
	 * @param parameterFile
	 *            , Parameter file path
	 * @param isSubJob     
	 *          , check whether it is subJob or not.      
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws EngineException
	 * @throws JAXBException
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public Object[] convertToUiXml(File sourceXML, IFile jobFile, IFile parameterFile, boolean isSubJob)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, EngineException, JAXBException, ParserConfigurationException,
			SAXException, IOException, ComponentNotFoundException {
		Container container = getJobContainer(sourceXML, parameterFile);
		Object[] array = updateUniqueIdForStandAloneJObs(jobFile, isSubJob, container);
		generateUiXml(container, jobFile, parameterFile);
		
		return array;
	}

	private Object[] updateUniqueIdForStandAloneJObs(IFile jobFile, boolean isSubJob, Container container) {
		Object[] array = new Object[2];
		array[0] = container;
		array[1] = null;
		if (!container.isCurrentGraphSubjob() && !ImportedJobsRepository.INSTANCE.contains(jobFile)) {
			int buttonId = showMessageForGeneratingUniqueJobId(container, jobFile, isSubJob);
			if (buttonId == SWT.YES) {
				try {
					String newUniqueJobId = GenerateUniqueJobIdUtil.INSTANCE.generateUniqueJobId();
					container.setUniqueJobId(newUniqueJobId);
					IFile file = updateGraphAndConvertToTargetXML(newUniqueJobId, jobFile,container);
					array[1] = file;
				} catch (NoSuchAlgorithmException e) {
					LOGGER.warn("Exception while generating new UniqueJobId");
				}
			}
		}
		return array;
	}
	
	private IFile updateGraphAndConvertToTargetXML(String newUniiqueJobId, IFile jobFile, Container container) {
		IPath xmlFileIPath = new Path(jobFile.getFullPath().toOSString());
		xmlFileIPath = xmlFileIPath.removeFileExtension().addFileExtension(Constants.XML_EXTENSION_FOR_IPATH);
		IFile fileXml = ResourcesPlugin.getWorkspace().getRoot().getFile(xmlFileIPath);
		try {
			ConverterUtil.INSTANCE.convertToXML(container, false, fileXml, null);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException
				| NoSuchMethodException exception) {
			MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_ERROR);
			messageBox.setMessage("Exception occurred while creating XML file. Please check logs.");
			messageBox.open();
			LOGGER.error("Exception while converting into xml",exception);
		}
		return fileXml;
	}

	/**
	 * Create a Message Box displays a currentJob UniqueId and a message to
	 * generate new Unique Id.
	 * @param container
	 * @param jobFile
	 * @param isSubjob
	 * @return {@link Integer}
	 */
	private int showMessageForGeneratingUniqueJobId(Container container, IFile jobFile, boolean isSubJob) {
		int buttonId = SWT.NO;
		if(StringUtils.isBlank(container.getUniqueJobId())){
			return SWT.YES;
		}
		MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(),
				SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		messageBox.setText("Question");
		String previousUniqueJobId = container.getUniqueJobId();
		if (isSubJob) {
			messageBox.setMessage(Messages.bind(Messages.GENERATE_NEW_UNIQUE_JOB_ID_FOR_SUB_JOB, jobFile.getName(),
					previousUniqueJobId));
		} else {
			messageBox.setMessage(
					Messages.bind(Messages.GENERATE_NEW_UNIQUE_JOB_ID_FOR_JOB, jobFile.getName(), previousUniqueJobId));
		}
		buttonId = messageBox.open();
		return buttonId;
	}

	
	/**
	 * @param sourceXML
	 * @param file
	 * @param parameterFile
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws EngineException
	 * @throws JAXBException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ComponentNotFoundException
	 */
	public Container convertSubjobToUiXml(File sourceXML, File file, IFile parameterFile) throws InstantiationException,
	IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
	SecurityException, EngineException, JAXBException, ParserConfigurationException, SAXException, IOException,
	ComponentNotFoundException {
		Container container = getJobContainer(sourceXML, parameterFile);
		generateSubjobUiXml(container, file, parameterFile);
		return container;

}
	
	private Container getJobContainer(File sourceXML, IFile parameterFile) throws JAXBException, ParserConfigurationException,
			SAXException, IOException {
		LOGGER.debug("Creating UI-Converter based on component");
		loadClass();
		Graph graph = unMarshall(sourceXML);
		
		Container container = new Container();
		container.setUniqueJobId(graph.getUniqueJobId());
		container.setJobRunCount(0);
		
		List<TypeBaseComponent> children = graph.getInputsOrOutputsOrStraightPulls();
		if (children != null && !children.isEmpty()) {
			for (TypeBaseComponent typeBaseComponent : children) {
				UiConverter uiConverter = UiConverterFactory.INSTANCE.getUiConverter(typeBaseComponent, container);
				uiConverter.setCurrentRepository(componentRepo);
				uiConverter.setSourceXMLPath(sourceXML);
				uiConverter.setParameterFile(parameterFile);
				uiConverter.prepareUIXML();
				Component component = uiConverter.getComponent();
				adjustComponentFigure(component);
				ComponentCacheUtil.INSTANCE.getProperties(component.getComponentName());
				container.getUIComponentList().add(component);
			}
			createLinks();
		}
		ImportedSchemaPropagation.INSTANCE.initiateSchemaPropagationAfterImport(container, false);
		return container;
	}

	private void adjustComponentFigure(Component component) {
		Font font = new Font( Display.getDefault(), ModelConstants.labelFont, 10,
				SWT.NORMAL );
		int labelLength = TextUtilities.INSTANCE.getStringExtents(component.getComponentLabel().getLabelContents(), font).width;
		if(labelLength >= ModelConstants.compLabelOneLineLengthLimit && component.getSize().height<96 ){
			component.setSize(new Dimension(component.getSize().width, component.getSize().height + ModelConstants.componentOneLineLabelMargin));
			ComponentLabel componentLabel = component.getComponentLabel();
			componentLabel.setSize(new Dimension(componentLabel.getSize().width, componentLabel.getSize().height + ModelConstants.componentOneLineLabelMargin));
			component.setComponentLabelMargin(ModelConstants.componentTwoLineLabelMargin);
		}
	}

	/**
	 * Load classes for all component classes. i.e. creates a logical palette into memory.
	 */
	public void loadClass() {
		LOGGER.debug("Loading class from component's XML");
		List<hydrograph.ui.common.component.config.Component> componentsConfig;
		try {
			componentsConfig = XMLConfigUtil.INSTANCE.getComponentConfig();

			for (hydrograph.ui.common.component.config.Component componentConfig : componentsConfig) {
				DynamicClassProcessor.INSTANCE.createClass(componentConfig);

			}

		} catch (RuntimeException| SAXException| IOException e) {
			LOGGER.error("Error occurred while loading classes from xml: ", e);
		}

	}

	/**
	 * Create links between the components based on there
	 */
	public void createLinks() throws ComponentNotFoundException {
		LOGGER.debug("Creating UI-Links between Components");
		preProcessLinkData();
		CoordinateProcessor pc = new CoordinateProcessor(componentRepo);
		pc.initiateCoordinateGenration();
		for (LinkingData linkingData : componentRepo.getComponentLinkList()) {
			Component sourceComponent = componentRepo.getComponentUiFactory().get(
					linkingData.getSourceComponentId());
			Component targetComponent = componentRepo.getComponentUiFactory().get(
					linkingData.getTargetComponentId());

			Link link = new Link();
			link.setSourceTerminal(ConverterUiHelper.getFromSocketId(linkingData, componentRepo));
			link.setTargetTerminal(linkingData.getTargetTerminal());
			link.setSource(sourceComponent);
			link.setTarget(targetComponent);
			sourceComponent.connectOutput(link);
			targetComponent.connectInput(link);
			LOGGER.debug("Links Details:" + linkingData);
		}

	}
	
	/**
	 * Creates the job file based for the container object.
	 * 
	 * @param container
	 * @param jobFile
	 * @param parameterFile
	 */
	private void generateUiXml(Container container, IFile jobFile, IFile parameterFile) {
		LOGGER.debug("Generating UI-XML");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		String jobXmlData = null;
		IPath iPath = new Path(jobFile.getFullPath().toString());
		
		try {
			XStream xs = new XStream();
			xs.autodetectAnnotations(true);
			xs.toXML(container,out);
			XMLUtil.unformatXMLString(out);
			
			if (jobFile.exists()) {
				showMessageBox(jobFile, out, jobFile.getName() + " already exists.Do you want to replace it?");
			} else {
				jobFile.create(new ByteArrayInputStream(out.toByteArray()), true, null);
			}

		} catch (CoreException e) {
			LOGGER.error("Exception occurred while creating UI-XML", e);
		} finally {
			componentRepo.flushRepository();
			try {
				out.close();
			} catch (IOException ioe) {
				LOGGER.error("IOException occurred while closing output stream", ioe);
			}
		}
		
		ImportedJobsRepository.INSTANCE.addImportedJobName(jobFile);
		jobXmlData = getStringValueFromXMLFile(iPath);
		storeParameterData(parameterFile, jobXmlData);
	}
	
	private void showMessageBox(IFile iFile, ByteArrayOutputStream out, String message) throws CoreException {
		MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(),
				SWT.ICON_ERROR | SWT.YES | SWT.NO);
		messageBox.setText("Error");
		messageBox.setMessage(message);
		int status = messageBox.open();
		if (status == SWT.YES) {
			iFile.setContents(new ByteArrayInputStream(out.toByteArray()), true, false, null);
		}
	}
	
	private void generateSubjobUiXml(Container container, File file, IFile parameterFile) {
		LOGGER.debug("Generating UI-XML");
		XStream xs = new XStream();
		xs.autodetectAnnotations(true);
		try(FileOutputStream fileOutputStream = new FileOutputStream(file.getPath())) {
			xs.toXML(container, fileOutputStream);
			IPath iPath = new Path(file.getPath());
			storeParameterData(parameterFile, getStringValueFromXMLFile(iPath));
		} catch (IOException ioException) {
			LOGGER.error("Exception occurred while creating UI-XML", ioException);
		} finally {
			componentRepo.flushRepository();
		}
	}

	private Graph unMarshall(File inputFile) throws JAXBException, ParserConfigurationException, SAXException,
			IOException {
		LOGGER.debug("Un-Marshaling generated object into target XML");
		JAXBContext jaxbContext;
		Graph graph = null;
		Document document =null;
		parseXML(inputFile);
		String inputFileAsString = replaceParametersWithDefaultValues(inputFile);
		
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setExpandEntityReferences(false);
		builderFactory.setNamespaceAware(true);
		builderFactory.setFeature(Constants.DISALLOW_DOCTYPE_DECLARATION,true);
		
		DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
		ByteArrayInputStream byteStream = new ByteArrayInputStream(inputFileAsString.getBytes());
		InputSource inputSource=new InputSource(byteStream);

		document = documentBuilder.parse(inputSource);
		jaxbContext = JAXBContext.newInstance(Graph.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		graph = (Graph) jaxbUnmarshaller.unmarshal(document);
		if (graph != null) {
			componentRepo.genrateComponentRepo(graph);
		}
		byteStream.close();
		return graph;
	}

	private String replaceParametersWithDefaultValues(File inputFile) {
		String fileContentAsString = readFileContentInString(inputFile).toString();
		List<ParameterData> parameterDataList = new ArrayList<>();
		for (Entry<String, List<ParameterData>> entry : componentRepo.getParammeterFactory().entrySet()) {
			parameterDataList.addAll(entry.getValue());
		}
		for (ParameterData parameterData : parameterDataList) {
			String parameterName = parameterData.getParameterName();
			parameterName = parameterName.replaceAll("\\@\\{", "");
			parameterName = parameterName.replaceAll("\\}", "");
			fileContentAsString = fileContentAsString.replaceAll("value=\"\\@\\{" + parameterName + "\\}\"", "");
			fileContentAsString = fileContentAsString.replaceAll("\\@\\{" + parameterName + "\\}", "");
		}
		return fileContentAsString;
	}

	

	private StringBuilder readFileContentInString(File inputFile) {
		StringBuilder inputStringBuilder = new StringBuilder();
		try(BufferedReader br= new BufferedReader(new FileReader(inputFile))){
			String sCurrentLine = null;
			while ((sCurrentLine = br.readLine()) != null) {
				inputStringBuilder.append(sCurrentLine);
			}
		} catch (IOException e) {
			LOGGER.error("Failed to read the xml file", e);
		}
		return inputStringBuilder;
	}

	private void parseXML(File inputFile) throws ParserConfigurationException, SAXException, IOException {
		XMLParser xmlParser = new XMLParser();
		xmlParser.parseXML(inputFile,componentRepo);
	}
	
	private void preProcessLinkData() {
		LOGGER.debug("Process links data for one to many port generation");
		boolean isMultiplePortAllowed;
		for (LinkingData linkingData : componentRepo.getComponentLinkList()) {
			LOGGER.debug("Process links data for one to many port generation : {}", linkingData);
			if (componentRepo.getComponentUiFactory().get(linkingData.getSourceComponentId()) != null){
				isMultiplePortAllowed = componentRepo.getComponentUiFactory()
						.get(linkingData.getSourceComponentId()).getPortDetails().get(0).isAllowMultipleLinks();
				
				if (isMultiplePortAllowed){
					if (linkingData.getSourceTerminal().contains("out")){
						linkingData.setSourceTerminal(FIXED_OUTPUT_PORT);
					}
					else if (linkingData.getSourceTerminal().contains("unused")){
						linkingData.setSourceTerminal(FIXED_UNUSED_PORT);
					}
				}
			}
		}
	}

	
	private void storeParameterData(IFile parameterFile, String jobXmlData) {
		LOGGER.debug("Creating Parameter(i.e *properties) File at {}", parameterFile.getFullPath());
		CanvasDataAdapter canvasDataAdapter = new CanvasDataAdapter(jobXmlData);
		String defaultParameterValue = "";
		Properties properties = new Properties();
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		for (String parameter : canvasDataAdapter.getParameterList()) {
			properties.setProperty(parameter, defaultParameterValue);
		}
		try {
			properties.store(out, null);
			if (parameterFile.exists()) {
				showMessageBox(parameterFile, out,parameterFile.getName()+" already exists.Do you want to replace it?");
			} else {
				parameterFile.create(new ByteArrayInputStream(out.toByteArray()), true, null);
			}
		} catch (IOException | CoreException e) {
			LOGGER.error("Exception occurred while creating parameter file -", e);
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				LOGGER.error("Exception occurred while closing parameter file's out stream -", e);
			}
		}
	}
	
	public String getStringValueFromXMLFile(IPath xmlPath) {
		if (xmlPath != null) {
				xmlPath = xmlPath.removeFileExtension().addFileExtension(Constants.XML_EXTENSION_FOR_IPATH);
				if (xmlPath.toFile().exists()){
					try(InputStream inputStream = new FileInputStream(xmlPath.toFile());
							Scanner scanner = new Scanner(inputStream)){
						return scanner.useDelimiter("\\Z").next();
					} catch (IOException exception) {
						LOGGER.error("Exception occurred while fetching data from " + xmlPath.toString(), exception);
					} 
				}
				else if (ResourcesPlugin.getWorkspace().getRoot().getFile(xmlPath).exists()){
					try(InputStream inputStream = ResourcesPlugin.getWorkspace().getRoot().getFile(xmlPath).getContents();
							Scanner scanner = new Scanner(inputStream)){
						return scanner.useDelimiter("\\Z").next();
					} catch (IOException | CoreException exception) {
						LOGGER.error("Exception occurred while fetching data from " + xmlPath.toString(), exception);
					}
				}	
		}
		return "";
	}
	
}
