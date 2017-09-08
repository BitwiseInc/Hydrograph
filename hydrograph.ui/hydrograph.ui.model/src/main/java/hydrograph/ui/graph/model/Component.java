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
/********************************************************************************
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

package hydrograph.ui.graph.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.slf4j.Logger;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

import hydrograph.ui.common.cloneableinterface.IDataStructure;
import hydrograph.ui.common.component.config.PortInfo;
import hydrograph.ui.common.component.config.PortSpecification;
import hydrograph.ui.common.component.config.Property;
import hydrograph.ui.common.datastructures.tooltip.PropertyToolTipInformation;
import hydrograph.ui.common.util.ComponentCacheUtil;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.XMLConfigUtil;
import hydrograph.ui.datastructure.property.JoinConfigProperty;
import hydrograph.ui.graph.model.components.InputSubjobComponent;
import hydrograph.ui.graph.model.components.OutputSubjobComponent;
import hydrograph.ui.graph.model.processor.DynamicClassProcessor;
import hydrograph.ui.graph.schema.propagation.SchemaData;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.validators.impl.IValidator;

/**
 * The Class Component.
 * <p>
 * This is a base class in model tier for all types of Hydrograph components.
 * 
 * @author Bitwise
 */
public abstract class Component extends Model {
	
	/** The Constant logger. */
	private static final Logger logger = LogFactory.INSTANCE.getLogger(Component.class);

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2587870876576884352L;
	
	/** The Unique component name. */
	private final String UniqueComponentName = "Component" + Math.random() * 10000;
	
	/**
	 * The Enum Props.
	 * 
	 * @author Bitwise
	 */
	public static enum Props {
		
		/** The name prop. */
		NAME_PROP("name"), 
		
		/** The id prop. */
	 	ID_PROP("id"),
		 
		/** The location prop. */
		LOCATION_PROP("Location"), 
		
		/** The size prop. */
		SIZE_PROP("Size"), 
		
		/** The inputs. */
		INPUTS("inputs"), 
		
		/** The outputs. */
		OUTPUTS("outputs"), 
		
		/** The validity status. */
		VALIDITY_STATUS("validityStatus"),
		
		/** The execution status. */
		EXECUTION_STATUS("executionStatus");

		/** The value. */
		private String value;

		/**
		 * Instantiates a new props.
		 *
		 * @param value the value
		 */
		private Props(String value) {
			this.value = value;
		}

		/**
		 * Gets the value.
		 *
		 * @return the value
		 */
		public String getValue() {
			return this.value;
		}

		/**
		 * Checks if given value is equal to this component's property value.		 * 
		 * @param property
		 *            the property
		 * @return true, if successful
		 */
		public boolean equalsTo(String property) {
			return this.value.equals(property);
		}
	}

	/**
	 * The Enum ValidityStatus.
	 * 
	 * @author Bitwise
	 */
	public static enum ValidityStatus {
		
		/** The warn. */
		WARN, 
 /** The error. */
 ERROR, 
 /** The valid. */
 VALID;
	}

	/** The location. */
	private final Point location;
	
	/** The size. */
	private final Dimension size;
	
	/** The properties. */
	private Map<String, Object> properties;
	
	/**The subjob Container	 */
	@XStreamOmitField
	private Map<String,Object> subJobContainer;
	
	/** The parent. */
	private Container parent;
	
	/** The validity status. */
	private String validityStatus;

	@Deprecated
	/** The cloned hash map. */
	private Map<String, Object> clonedHashMap;
	
	@Deprecated
	/** The cloned array list. */
	private ArrayList<JoinConfigProperty>  clonedArrayList;
	
	/** The input links hash. */
	private final Hashtable<String, ArrayList<Link>> inputLinksHash;
	
	/** The output links hash. */
	private final Hashtable<String, ArrayList<Link>> outputLinksHash;
	
	/** The input links. */
	private ArrayList<Link> inputLinks = new ArrayList<Link>();
	
	/** The output links. */
	private ArrayList<Link> outputLinks = new ArrayList<Link>();
	
	/** The inputport terminals. */
	private  List<String> inputportTerminals;
	
	/** The output port terminals. */
	private  List<String> outputPortTerminals;
	
	/** The new instance. */
	private boolean newInstance;
	
	/** The type. */
	private String type;
	
	/** The prefix. */
	private String prefix;
	
	/** The category. */
	private String category;
	
	/** The ports. */
	private Map<String, Port> ports;
	
	/** The component name. */
	private String componentName;
	
	/** The component id. */
 	private String componentId;
	
	/** The port details. */
	private List<PortDetails> portDetails;

	/** The left port count. */
	private int leftPortCount;
	
	/** The right port count. */
	private int rightPortCount;
	
	/** The bottom port count. */
	private int bottomPortCount;

	/** The change in ports cnt dynamically. */
	private boolean changeInPortsCntDynamically;
	
	/** The change out ports cnt dynamically. */
	private boolean changeOutPortsCntDynamically;
	
	/** The change unused ports cnt dynamically. */
	private boolean changeUnusedPortsCntDynamically;

	/** The component label. */
	private ComponentLabel componentLabel;
	
	/** The component label margin. */
	private int componentLabelMargin;
	 
	/** The watcher terminals. */
	private Map<String, Long> watcherTerminals;
	
	/** The latest changes in schema. */
	@XStreamOmitField
	private boolean latestChangesInSchema=false;

	/** The tooltip information. */
	@XStreamOmitField
	private Map<String, PropertyToolTipInformation> tooltipInformation;

	/** The tool tip error messages. */
	// @XStreamOmitField
	private Map<String, String> toolTipErrorMessages; // <propertyName,ErrorMessage>

	/** The component edit part. */
	@XStreamOmitField
	private Object componentEditPart;
	
	/** The status. */
	@XStreamOmitField
	private ComponentExecutionStatus status;
	
	/* Default prefix is used for creating unique component id */
	@XStreamOmitField
	private String defaultPrefix;
	
	private boolean isContinuousSchemaPropogationAllow;
	/**
	 * Instantiates a new component.
	 */
	public Component() {
		location = new Point(0, 0);
		size = new Dimension(100, 80);
		properties = new LinkedHashMap<>();
		subJobContainer= new LinkedHashMap<>();
		leftPortCount = 0;
		rightPortCount = 0;
		bottomPortCount = 0;
		inputLinksHash = new Hashtable<String, ArrayList<Link>>();

		inputLinks = new ArrayList<Link>();
		outputLinksHash = new Hashtable<String, ArrayList<Link>>();
		outputLinks = new ArrayList<Link>();
		inputportTerminals = new ArrayList<String>();
		outputPortTerminals = new ArrayList<String>();
		watcherTerminals = new HashMap();
		newInstance = true;
		validityStatus = ValidityStatus.WARN.name();
		componentName = DynamicClassProcessor.INSTANCE.getClazzName(this
				.getClass());

		componentLabel = new ComponentLabel(componentName);
		componentLabelMargin = 16;

		prefix = XMLConfigUtil.INSTANCE.getComponent(componentName)
				.getDefaultNamePrefix();
		
		defaultPrefix=XMLConfigUtil.INSTANCE.getComponent(componentName)
				.getDefaultNamePrefix();
		initPortSettings();
		toolTipErrorMessages = new LinkedHashMap<>();
		status = ComponentExecutionStatus.BLANK;
	}

	/**
	 * Returns tooltip error message.
	 *
	 * @return {@link Map}
	 */
	public Map<String, String> getToolTipErrorMessages() {
		return toolTipErrorMessages;
	}
	
	/**
	 * 	
	 * 	Set Tooltip error message .
	 *
	 * @param toolTipErrorMessages the tool tip error messages
	 */
	public void setToolTipErrorMessages(Map<String, String> toolTipErrorMessages) {
		this.toolTipErrorMessages = toolTipErrorMessages;
	}
   	
	/**
	 * Inits the port settings.
	 */
	private void initPortSettings() {

		List<PortSpecification> portSpecification = XMLConfigUtil.INSTANCE.getComponent(componentName)
				.getPort().getPortSpecification();

		portDetails = new ArrayList<PortDetails>();
		ports = new HashMap<String, Port>();
		PortTypeEnum pEnum = null;
		PortAlignmentEnum pAlignEnum = null;

		for (PortSpecification p : portSpecification) {
			pAlignEnum = PortAlignmentEnum.fromValue(p.getPortAlignment().value());
			setPortCount(pAlignEnum, p.getNumberOfPorts(), p.isChangePortCountDynamically());
			for(PortInfo portInfo :p.getPort()){
				String portTerminal = portInfo.getPortTerminal();
				pEnum = PortTypeEnum.fromValue(portInfo.getTypeOfPort().value());
				
				Port port = new Port(portInfo.getLabelOfPort(),
						portTerminal, this, getNumberOfPortsForAlignment(pAlignEnum), pEnum
								, portInfo.getSequenceOfPort(), p.isAllowMultipleLinks(), p.isLinkMandatory(), pAlignEnum);
				logger.trace("Adding portTerminal {}", portTerminal);
				
				ports.put(portTerminal, port);
			}
			PortDetails pd = new PortDetails(ports, pAlignEnum, p.getNumberOfPorts(), p.isChangePortCountDynamically(), p.isAllowMultipleLinks(), p.isLinkMandatory());
			portDetails.add(pd);
		}
		
	}

	/**
	 * Gets the number of ports for alignment.
	 *
	 * @param pAlignEnum the align enum
	 * @return the number of ports for alignment
	 */
	private int getNumberOfPortsForAlignment(PortAlignmentEnum pAlignEnum) {
		if(pAlignEnum.equals(PortAlignmentEnum.LEFT)){
			return leftPortCount;
		}else if(pAlignEnum.equals(PortAlignmentEnum.RIGHT)){
			return rightPortCount;
		}else if(pAlignEnum.equals(PortAlignmentEnum.BOTTOM)){
			return bottomPortCount;
		}
		return 0;
	}

	/**
	 * Sets the port count.
	 *
	 * @param pAlign the align
	 * @param portCount the port count
	 * @param changePortCount the change port count
	 */
	private void setPortCount(PortAlignmentEnum pAlign, int portCount, boolean changePortCount) {
		if(pAlign.equals(PortAlignmentEnum.LEFT)){
			leftPortCount = leftPortCount + portCount;
			properties.put("inPortCount", String.valueOf(portCount));
			changeInPortsCntDynamically=changePortCount;
		} else if (pAlign.equals(PortAlignmentEnum.RIGHT)) {
			rightPortCount = rightPortCount + portCount;
			properties.put("outPortCount", String.valueOf(portCount));
			changeOutPortsCntDynamically=changePortCount;
		} else if (pAlign.equals(PortAlignmentEnum.BOTTOM)) {
			bottomPortCount = bottomPortCount + portCount;
			properties.put("unusedPortCount", String.valueOf(portCount));
			changeUnusedPortsCntDynamically=changePortCount;
			
		}
	}

	/**
	 * Returns list of input ports of this component.
	 * 
	 * @return
	 */
	public List<String> getInputportTerminals() {
		return inputportTerminals;
	}
	
	/**
	 * Set input ports for this component.
	 * 
	 * @param portTerminals
	 */
	public void setInputportTerminals(List<String> portTerminals){
		this.inputportTerminals=portTerminals;
	}

	/**
	 * 
	 * Returns list of output ports for this component.
	 * 
	 * @return
	 */
	public List<String> getOutputPortTerminals() {
		return outputPortTerminals;
	}
	
	/**
	 * 
	 * Set output ports for this component.
	 * 
	 * @param portTerminals
	 */
	public void setOutputPortTerminals(List<String> portTerminals) {
		this.outputPortTerminals=portTerminals;
	}
	
	/**
	 * Set true when number of input port count changed dynamically.
	 *
	 * @param changeInPortsCntDynamically the new change in ports cnt dynamically
	 */
	public void setChangeInPortsCntDynamically(boolean changeInPortsCntDynamically) {
		this.changeInPortsCntDynamically = changeInPortsCntDynamically;
	}

	/**
	 * Set true when number of output port count changed dynamically.
	 *
	 * @param changeOutPortsCntDynamically the new change out ports cnt dynamically
	 */	
	public void setChangeOutPortsCntDynamically(boolean changeOutPortsCntDynamically) {
		this.changeOutPortsCntDynamically = changeOutPortsCntDynamically;
	}

	/**
	 * Set true when number of unused port count changed.
	 *
	 * @param changeUnusedPortsCntDynamically the new change unused ports cnt dynamically
	 */
	public void setChangeUnusedPortsCntDynamically(
			boolean changeUnusedPortsCntDynamically) {
		this.changeUnusedPortsCntDynamically = changeUnusedPortsCntDynamically;
	}

	/**
	 * 
	 * Get input port count
	 * 	
	 * @return - number of input ports
	 */
	public int getInPortCount() {
		return leftPortCount;
	}

	/**
	 * set number of input ports.
	 *
	 * @param inPortCount the new in port count
	 */
	public void setInPortCount(int inPortCount) {
		this.leftPortCount = inPortCount;
		this.properties.put("inPortCount", String.valueOf(inPortCount));
	}

	/**
	 * Get number of output port.
	 *
	 * @return the out port count
	 */
	public int getOutPortCount() {
		return rightPortCount;
	}

	/**
	 * Set number of output port.
	 *
	 * @param outPortCount the new out port count
	 */
	public void setOutPortCount(int outPortCount) {
		this.rightPortCount = outPortCount;
		this.properties.put("outPortCount", String.valueOf(outPortCount));
	}

	/**
	 * Get number of unused port.
	 *
	 * @return the unused port count
	 */
	public int getUnusedPortCount() {
		return bottomPortCount;
	}

	/**
	 * set number of unused port.
	 *
	 * @param unusedPortCount the new unused port count
	 */
	public void setUnusedPortCount(int unusedPortCount) {
		this.bottomPortCount = unusedPortCount;
		this.properties.put("unusedPortCount", String.valueOf(unusedPortCount));
	}

	/**
	 * Get port specification list.
	 *
	 * @return - list of {@link PortDetails}
	 */
	public List<PortDetails> getPortDetails() {
		return portDetails;
	}
	
	
	/**
	 * Get port details based on its alignment.
	 *
	 * @return - list of {@link PortDetails}
	 */
	public PortDetails getPortDetails(PortAlignmentEnum alignmentEnum) {
		for(PortDetails portDetails :this.portDetails){
			if(alignmentEnum.equals(portDetails.getPortAlignment())){
				return portDetails;
			}
		}
		return null;
	}
	
	/**
	 * Set ports.
	 *
	 * @param ports the ports
	 */
	public void setPorts(Map<String, Port> ports) {
		this.ports = ports;
	}
	
	/**
	 * returns true when there is change in count of input port.
	 *
	 * @return boolean
	 */
	public boolean isChangeInPortsCntDynamically() {
		return changeInPortsCntDynamically;
	}

	/**
	 * returns true when there is change in count of output port.
	 *
	 * @return boolean
	 */
	public boolean isChangeOutPortsCntDynamically() {
		return changeOutPortsCntDynamically;
	}

	/**
	 * returns true when there is change in count of unused port.
	 *
	 * @return boolean
	 */
	public boolean isChangeUnusedPortsCntDynamically() {
		return changeUnusedPortsCntDynamically;
	}

	/**
	 * Add input ports.
	 *
	 * @param key the key
	 * @return true, if is allow multiple links for port
	 */
	
	private boolean isAllowMultipleLinksForPort(String key){
		for(PortDetails portDetailsInfo :this.portDetails){
			if(portDetailsInfo.getPorts().containsKey(key))
				return (portDetailsInfo.getPorts().get(key).isAllowMultipleLinks());
		}
		return false;
	}
	
	/**
	 * Checks if is link mandatory for port.
	 *
	 * @param key the key
	 * @return true, if is link mandatory for port
	 */
	private boolean isLinkMandatoryForPort(String key){
		for(PortDetails portDetailsInfo :this.portDetails){
			if(portDetailsInfo.getPorts().containsKey(key))
				return (portDetailsInfo.getPorts().get(key).isLinkMandatory());
		}
		return false;
	}
	
	/**
	 * Checks if there are latest changes in schema.
	 * 
	 * @return true, if is latest changes in schema
	 */
	public boolean isLatestChangesInSchema() {
		return latestChangesInSchema;
	}

	/**
	 * Sets the latest changes in schema.
	 * 
	 * @param latestChangesInSchema
	 *            the new latest changes in schema
	 */
	public void setLatestChangesInSchema(boolean latestChangesInSchema) {
		this.latestChangesInSchema = latestChangesInSchema;
	}

	/**
	 * Increment left side ports i.e. input ports.
	 * 
	 * @param newPortCount
	 *            the new port count
	 * @param oldPortCount
	 *            the old port count
	 */
	public void incrementLeftSidePorts(int newPortCount, int oldPortCount) {

		for (int i = oldPortCount; i < newPortCount; i++) {

			Port inPort = new Port(Constants.INPUT_SOCKET_TYPE + i, Constants.INPUT_SOCKET_TYPE + i, this,
					newPortCount, PortTypeEnum.IN, i, isAllowMultipleLinksForPort("in0"), isLinkMandatoryForPort("in0"),
					PortAlignmentEnum.LEFT);
			ports.put(Constants.INPUT_SOCKET_TYPE + i, inPort);
			firePropertyChange("Component:add", null, inPort);
			for(PortDetails p:portDetails){
				if(PortAlignmentEnum.LEFT.equals(p.getPortAlignment())){
					p.setPorts(ports);
				}
			}
		}
	}

	
	/**
	 * Add output ports.
	 *
	 * @param newPortCount the new port count
	 * @param oldPortCount the old port count
	 */
	public void incrementRightSidePorts(int newPortCount, int oldPortCount) {
		for (int i = oldPortCount; i < newPortCount; i++) {
			Port outputPort = new Port(Constants.OUTPUT_SOCKET_TYPE + i, Constants.OUTPUT_SOCKET_TYPE + i, this,
					newPortCount, PortTypeEnum.OUT, i, isAllowMultipleLinksForPort("out0"), isLinkMandatoryForPort("out0"),
					PortAlignmentEnum.RIGHT);
			ports.put(Constants.OUTPUT_SOCKET_TYPE + i, outputPort);
			firePropertyChange("Component:add", null, outputPort);
			for(PortDetails p:portDetails){
				if(PortAlignmentEnum.RIGHT.equals(p.getPortAlignment())){
					p.setPorts(ports);
				}
			}
		}
	}

	/**
	 * Add unused ports.
	 *
	 * @param newPortCount the new port count
	 * @param oldPortCount the old port count
	 */
	public void incrementBottomSidePorts(int newPortCount, int oldPortCount) {
		for (int i = oldPortCount; i < newPortCount; i++) {
			Port unusedPort = new Port("un" + i, Constants.UNUSED_SOCKET_TYPE + i,
					this, newPortCount, PortTypeEnum.UNUSED, i, isAllowMultipleLinksForPort("unused0"), isLinkMandatoryForPort("unused0"),
					PortAlignmentEnum.BOTTOM);
			ports.put(Constants.UNUSED_SOCKET_TYPE + i, unusedPort);
			firePropertyChange("Component:add", null, unusedPort);
			for(PortDetails p:portDetails){
				if(PortAlignmentEnum.BOTTOM.equals(p.getPortAlignment())){
					p.setPorts(ports);
				}
			}
		}
	}

	/**
	 *  Change input port count.
	 *
	 * @param newPortCount the new port count
	 */
	public void changeInPortCount(int newPortCount) {

		List<String> portTerminals = new ArrayList<>();
		portTerminals.addAll(ports.keySet());
		for (String key : portTerminals) {
			if (key.contains("in")) {
				ports.get(key).setNumberOfPortsOfThisType(newPortCount);
			}
		}
		setInPortCount(newPortCount);
	}

	/**
	 * Change unused port count.
	 *
	 * @param newPortCount the new port count
	 */
	public void changeUnusedPortCount(int newPortCount) {

		List<String> portTerminals = new ArrayList<>();
		portTerminals.addAll(ports.keySet());
		for (String key : portTerminals) {
			if (key.contains("un")) {
				ports.get(key).setNumberOfPortsOfThisType(newPortCount);
			}
		}
		setUnusedPortCount(newPortCount);
	}

	/**
	 * Change output port count.
	 *
	 * @param newPortCount the new port count
	 */
	public void changeOutPortCount(int newPortCount) {

		List<String> portTerminals = new ArrayList<>();
		portTerminals.addAll(ports.keySet());
		for (String key : portTerminals) {
			if (key.contains("out")) {
				ports.get(key).setNumberOfPortsOfThisType(newPortCount);
			}
		}
		setOutPortCount(newPortCount);
	}

	/**
	 * Get ports.
	 *
	 * @return the ports
	 */
	public Map<String, Port> getPorts() {
		return ports;
	}

	/**
	 * Gets the port.
	 * 
	 * @param terminal
	 *            the terminal
	 * @return the port
	 */
	public Port getPort(String terminal) {
		return ports.get(terminal);
	}

	/**
	 * Get list of component children.
	 *
	 * @return list of {@link Model}
	 */
	public List<Model> getChildren() {

		List<Model> children = new ArrayList<Model>(ports.values());
		children.add(componentLabel);

		return children;

	}
	
	/**
	 * Update connection property.
	 *
	 * @param prop the prop
	 * @param newValue the new value
	 */
	private void updateConnectionProperty(String prop, Object newValue) {
		firePropertyChange(prop, null, newValue);
	}

	/**
	 * Connect input to given link.
	 * 
	 * @param {@link Link}
	 */
	public void connectInput(Link link) {
		inputLinks.add(link);
		inputLinksHash.put(link.getTargetTerminal(), inputLinks);
		updateConnectionProperty(Props.INPUTS.getValue(), link);
	}

	/**
	 * Connect output to given link.
	 * 
	 * @param {{@link Link}
	 * 
	 */

	public void connectOutput(Link link) {
		if (outputLinksHash.get(link.getSourceTerminal()) != null){
			link.setLinkNumber(getNewLinkNumber(link));
		}else{
			link.setLinkNumber(0);
		}	
		outputLinks.add(link);
		outputLinksHash.put(link.getSourceTerminal(), outputLinks);
		updateConnectionProperty(Props.OUTPUTS.getValue(), link);
	}

	private int getNewLinkNumber(Link newlink){
		List<Integer> existingOutputLinkNumbers = getExistingOutputLinkNumbers(newlink);
		
		Integer newLinkNumber = getNewLinkNumberFromExistingLinkNumberRange(existingOutputLinkNumbers);
		if(newLinkNumber==null){
			newLinkNumber = existingOutputLinkNumbers.size();
		}
		
		return newLinkNumber;
	}

	private Integer getNewLinkNumberFromExistingLinkNumberRange(
			List<Integer> existingOutputLinkNumbers) {
		int index=0;
		Integer newLinkNumber=null;
		for(;index<existingOutputLinkNumbers.size();index++){
			if(!existingOutputLinkNumbers.contains(index)){
				newLinkNumber = index;
				break;
			}
		}
		return newLinkNumber;
	}

	private List<Integer> getExistingOutputLinkNumbers(Link newlink) {
		List<Integer> existingOutputLinkNumbers = new ArrayList<>();
		
		for(Link link: outputLinksHash.get(newlink.getSourceTerminal())){
			existingOutputLinkNumbers.add(link.getLinkNumber());
		}
		return existingOutputLinkNumbers;
	}
	
	/**
	 * Disconnect input from given link.
	 * 
	 * @param link
	 *            the link
	 */
	public void disconnectInput(Link link) {
		inputLinks.remove(link);
		inputLinksHash.remove(link.getTargetTerminal());
		updateConnectionProperty(Props.INPUTS.getValue(), link);
	}

	/**
	 * Disconnect output from given link.
	 * 
	 * @param link
	 *            the link
	 */
	public void disconnectOutput(Link link) {
		outputLinks.remove(link);
		updateConnectionProperty(Props.OUTPUTS.getValue(), link);
	}

	/**
	 * Get source connections.
	 *
	 * @return the source connections
	 */
	public List<Link> getSourceConnections() {
		return outputLinks;
	}
	
	/**
	 * Set source connections.
	 *
	 * @param links the new source connections
	 */
	public void  setSourceConnections(List<Link> links) {
		 outputLinks=(ArrayList<Link>) links;
	}

	/**
	 * Gets the target connections.
	 * @return the target connections
	 */
	public List<Link> getTargetConnections() {
		return inputLinks;
	}

	/**
	 * Engage input port.
	 * 
	 * @param terminal
	 *            the terminal
	 */
	public void engageInputPort(String terminal) {
		inputportTerminals.add(terminal);
	}

	/**
	 * Free up given input port.
	 * 
	 * @param terminal
	 *            the terminal
	 */
	public void freeInputPort(String terminal) {
		inputportTerminals.remove(terminal);
	}

	/**
	 * Checks if input port is engaged (connected ).
	 * 
	 * @param terminal
	 *            the terminal
	 * @return true, if is input port engaged
	 */
	public boolean isInputPortEngaged(String terminal) {
		return inputportTerminals.contains(terminal);

	}

	/**
	 * Engage output port.
	 * 
	 * @param terminal
	 *            the terminal
	 */
	public void engageOutputPort(String terminal) {
		outputPortTerminals.add(terminal);
	}

	/**
	 * Free up given output port.
	 * 
	 * @param terminal
	 *            the terminal
	 */
	public void freeOutputPort(String terminal) {
		outputPortTerminals.remove(terminal);
	}

	/**
	 * Checks if output port is engaged ( Connected ).
	 * 
	 * @param terminal
	 *            the terminal
	 * @return true, if is output port engaged
	 */
	public boolean isOutputPortEngaged(String terminal) {
		return outputPortTerminals.contains(terminal);
	}

	/**
	 * Sets the properties.
	 * 
	 * @param properties
	 *            the properties
	 */
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	/**
	 * Return the property value for the given propertyId, or null.
	 *
	 * @param propertyId the property id
	 * @return the property value
	 */
	public Object getPropertyValue(Object propertyId) {
		if (properties.containsKey(propertyId)) {
			return properties.get(propertyId);
		}
		throw new PropertyNotAvailableException();
	}

	/**
	 * Set the property value for the given property id.
	 *
	 * @param propertyId the property id
	 * @param value the value
	 */
	public void setPropertyValue(Object propertyId, Object value) {
		properties.put((String) propertyId, value);
		// tooltipInformation.get(propertyId).setPropertyValue(value);
	}

	/**
	 * Gets the component name.
	 * @return the component name
	 */
	public String getComponentName() {
		return componentName;
	}

	/**
	 * Gets the component label.
	 * @return the component label
	 */
	public ComponentLabel getComponentLabel() {
		return componentLabel;
	}

	/**
	 * Sets the component label.
	 * 
	 * @param componentLabel
	 *            the new component label
	 */
	public void setComponentLabel(ComponentLabel componentLabel) {
		this.componentLabel = componentLabel;
	}

	
	/**
 	 * Sets the component id.
 	 * 
 	 * @param String
 	 *            the new component id
 	 */
 	public void setComponentId(String id) {
 		this.componentId = id;
 	}
	 	
 	/**
 	 * Gets the component id.
 	 * @return the component id
 	 */
 	public String getComponentId() {
 		return componentId;
 	}
	 
	 
	/**
	 * Set the Location of this shape.
	 * 
	 * @param newLocation
	 *            a non-null Point instance
	 * @throws IllegalArgumentException
	 *             if the parameter is null
	 */
	public void setLocation(Point newLocation) {
		resetLocation(newLocation);
		location.setLocation(newLocation);
		firePropertyChange(Props.LOCATION_PROP.getValue(), null, location);
	}

	/**
	 * reset if x or y coordinates of components are negative
	 * 
	 * @param newLocation
	 */
	private void resetLocation(Point newLocation) {
		if (newLocation.x < 0) {
			newLocation.x = 0;
		}

		if (newLocation.y < 0) {
			newLocation.y = 0;
		}
	}

	/**
	 * Return the Location of this shape.
	 * 
	 * @return a non-null location instance
	 */
	public Point getLocation() {
		return location.getCopy();
	}

	/**
	 * Set the Size of this shape. Will not modify the size if newSize is null.
	 * 
	 * @param newSize
	 *            a non-null Dimension instance or null
	 */
	public void setSize(Dimension newSize) {
		if(newSize.height<80)
			newSize.height=80;
		if (newSize != null) {
			size.setSize(newSize);
			firePropertyChange(Props.SIZE_PROP.getValue(), null, size);
		}
	}

	/**
	 * Return the Size of this shape.
	 * 
	 * @return a non-null Dimension instance
	 */
	public Dimension getSize() {
		return size.getCopy();
	}

	/**
	 * get component label margin.
	 *
	 * @return the component label margin
	 */
	public int getComponentLabelMargin() {
		return componentLabelMargin;
	}

	/**
	 * Set component label margin.
	 *
	 * @param componentLabelMargin the new component label margin
	 */
	public void setComponentLabelMargin(int componentLabelMargin) {
		this.componentLabelMargin = componentLabelMargin;
		firePropertyChange("componentLabelMargin", null, componentLabelMargin);
	}

	/**
	 * Get parent of the component.
	 *
	 * @return {@link Container}
	 */
	public Container getParent() {
		return parent;
	}

	/**
	 * Set component parent.
	 *
	 * @param parent the new parent
	 */
	public void setParent(Container parent) {
		this.parent = parent;
	}

	/**
	 * Returns component properties.
	 *
	 * @return the properties
	 */
	public LinkedHashMap<String, Object> getProperties() {
		return (LinkedHashMap<String, Object>) properties;
	}

	/**
	 * PropertyNotAvailableException exception.
	 *
	 * @author Bitwise
	 */
	private class PropertyNotAvailableException extends RuntimeException {
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = -7978238880803956846L;

	}

	/**
	 * Checks if it's a new instance.
	 * 
	 * @return true, if is new instance
	 */
	public boolean isNewInstance() {
		return newInstance;
	}

	/**
	 * Sets the new instance.
	 *
	 * @param newInstance the new new instance
	 */
	public void setNewInstance(boolean newInstance) {
		this.newInstance = newInstance;
	}

	/**
	 * Returns component type.
	 *
	 * @return - component type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Set Component type.
	 *
	 * @param type the new type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Get Component prefix i.e. base name
	 *
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * Set Component prefix i.e. base name
	 *
	 * @param prefix the new prefix
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * Get Component category .
	 *
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * Set component category.
	 *
	 * @param category the new category
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * returns component validation status.
	 *
	 * @return the validity status
	 */
	public String getValidityStatus() {
		return validityStatus;
	}

	/**
	 * Set component validation status.
	 *
	 * @param validityStatus the new validity status
	 */
	public void setValidityStatus(String validityStatus) {
		this.validityStatus = validityStatus;
	}
	/**
	 * 
	 * @return subjobcontainer 
	 */
	public Map<String, Object> getSubJobContainer() {
		if(subJobContainer==null){
			subJobContainer=new LinkedHashMap<>();
		}
		return subJobContainer;
	}
    /**
     * Sets the subjob Container
     * @param subJobContainer
     */
	public void setSubJobContainer(Map<String, Object> subJobContainer) {
		this.subJobContainer = subJobContainer;
	}

	/**
	 * Gets the converter.
	 *
	 * @return the converter
	 */
	// For Target XML
	public abstract String getConverter();

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Component clone() throws CloneNotSupportedException {
		Component component = null;
		Map<String,Object> clonedHashMap = new LinkedHashMap<String, Object>();
		try {
			component = this.getClass().newInstance();
			
			for (Map.Entry<String, Object> entry : getProperties().entrySet()) {
				if (entry.getValue() instanceof String)
				{	
					clonedHashMap.put(entry.getKey(), entry.getValue());
				}
				else {
					
					if(entry.getValue() instanceof ArrayList)
					{
						List clonedArrayList = new ArrayList<>();
						List orignalList = (ArrayList) entry.getValue();
						if (!orignalList.isEmpty() && String.class.isAssignableFrom(orignalList.get(0).getClass())) {
						    	 clonedArrayList.addAll(orignalList);
							
						} else {
							ArrayList<JoinConfigProperty>  joinConfigList= (ArrayList<JoinConfigProperty>) orignalList ;
					     for(int i=0;i<joinConfigList.size();i++)
					     {
					    	 clonedArrayList.add(joinConfigList.get(i).clone());
					     } }
						clonedHashMap.put(entry.getKey(), clonedArrayList);
					}
					
					else if (entry.getValue() instanceof LinkedHashMap)
						clonedHashMap.put(entry.getKey(),new LinkedHashMap<>((LinkedHashMap<String, String>) entry.getValue()));
					
					else if (entry.getValue() instanceof HashMap)
					clonedHashMap.put(entry.getKey(),new HashMap<>((HashMap<String, String>) entry.getValue()));
						
					else if (entry.getValue() instanceof LinkedHashSet)
						clonedHashMap.put(entry.getKey(),new LinkedHashSet<>((LinkedHashSet<String>) entry.getValue()));
							
					
					else if (entry.getValue() instanceof HashSet)
					clonedHashMap.put(entry.getKey(),new HashSet<>((HashSet<String>) entry.getValue()));
						
					
					else if (entry.getValue() instanceof TreeMap)
					clonedHashMap.put(entry.getKey(), new TreeMap<>((TreeMap<String,String>) entry.getValue()));
						
					else if (entry.getValue() instanceof InputSubjobComponent) {
						clonedHashMap.put(entry.getKey(), null);
					}

					else if (entry.getValue() instanceof OutputSubjobComponent) {
						clonedHashMap.put(entry.getKey(), null);
					} 
					else if (entry.getValue() !=null && isWrapperType(entry.getValue().getClass())){
						clonedHashMap.put(entry.getKey(), entry.getValue());
					}
					else if(StringUtils.equalsIgnoreCase(entry.getKey(), "Container")){
						clonedHashMap.put(entry.getKey(), entry.getValue());
					}
					else  if(entry.getValue()!=null)
					{   
						IDataStructure c=(IDataStructure) entry.getValue();
						clonedHashMap.put(entry.getKey(), c.clone());
					}
				}

			}
		} catch ( IllegalAccessException | InstantiationException e) {
			logger.debug("Unable to clone Component ", e);
		}
		if(component!=null){
		component.setType(getType());
		component.setCategory(getCategory());
		component.setParent(getParent());
		component.setProperties(clonedHashMap);
		component.setSize(getSize());
		component.setLocation(getLocation());
		component.setComponentLabel(new ComponentLabel((String) clonedHashMap.get(Constants.NAME)));
		component.setComponentId(this.getComponentId());
		HashMap<String, Port> clonedPorts=new HashMap<String, Port>();
		
	    for (Map.Entry<String, Port> entry : ports.entrySet()) {
	    	Port p = entry.getValue();
	    	Port clonedPort  = p.clone();
	    	clonedPort.setParent(component);
	    	clonedPorts.put(entry.getKey(), clonedPort);
	    }
		
		component.setPorts(clonedPorts);
		
		component.setInputportTerminals(new ArrayList<String> ());
		component.setOutputPortTerminals(new ArrayList<String> ());
		component.setValidityStatus(validityStatus);
		component.setChangeInPortsCntDynamically(changeInPortsCntDynamically);
		component.setChangeOutPortsCntDynamically(changeOutPortsCntDynamically);
		component.setChangeUnusedPortsCntDynamically(changeUnusedPortsCntDynamically);
		component.setInPortCount(leftPortCount);
		component.setOutPortCount(rightPortCount);
		component.setUnusedPortCount(bottomPortCount);
		}
		return component;
	}
	
	/**
	 * Set tooltip information.
	 *
	 * @param tooltipInformation the tooltip information
	 */
	public void setTooltipInformation(
			Map<String, PropertyToolTipInformation> tooltipInformation) {
		this.tooltipInformation = tooltipInformation;
	}

	/**
	 * get tooltip information.
	 *
	 * @return the tooltip information
	 */
	public Map<String, PropertyToolTipInformation> getTooltipInformation() {
		return tooltipInformation;
	}

	/**
	 * Update tooltip information.
	 */
	public void updateTooltipInformation() {
		for (String propertyName : properties.keySet()) {
			if (tooltipInformation != null) {
				if (tooltipInformation.get(propertyName) != null) {
					tooltipInformation.get(propertyName).setPropertyValue(
							properties.get(propertyName));
					if (toolTipErrorMessages != null)
						tooltipInformation.get(propertyName).setErrorMessage(
								toolTipErrorMessages.get(propertyName));
				}
			}
		}
	}

	/**
	 * Set component label.
	 *
	 * @param label the new component label
	 */
	public void setComponentLabel(String label) {
		setPropertyValue(Component.Props.NAME_PROP.getValue(), label);
		componentLabel.setLabelContents(label);
	}

	/**
	 * Get component Description.
	 *
	 * @return the component description
	 */
	public String getComponentDescription() {
		return XMLConfigUtil.INSTANCE.getComponent(componentName)
				.getDescription();
	}

	/**
	 * unused port settings.
	 *
	 * @param newPortCount the new port count
	 */
	public void unusedPortSettings(int newPortCount) {
       changeUnusedPortCount(newPortCount);
		for (int i = 0; i < (newPortCount - 2); i++) {
			Port unusedPort = new Port("un" + (i + 2),
					Constants.UNUSED_SOCKET_TYPE + (i + 2), this, newPortCount, PortTypeEnum.UNUSED, (i + 2), isAllowMultipleLinksForPort("unused0"), 
					isLinkMandatoryForPort("unused0"), PortAlignmentEnum.BOTTOM);
			ports.put(Constants.UNUSED_SOCKET_TYPE + (i + 2), unusedPort);
			firePropertyChange("Component:add", null, unusedPort);
		}
	}
	
	
	/**
	 * Complete input port settings.
	 * 
	 * @param newPortCount
	 *            the new port count
	 */
	public void completeInputPortSettings(int newPortCount) {
       changeInPortCount(newPortCount);
		for (int i = 0; i < (newPortCount); i++) {
			Port inPort = new Port(Constants.INPUT_SOCKET_TYPE + (i), Constants.INPUT_SOCKET_TYPE
					+ (i), this, newPortCount, PortTypeEnum.IN, (i), isAllowMultipleLinksForPort("in0"), 
					isLinkMandatoryForPort("in0"), PortAlignmentEnum.LEFT);
			ports.put(Constants.INPUT_SOCKET_TYPE + (i), inPort);
			firePropertyChange("Component:add", null, inPort);
		}
	}
	
	
	/**
	 * Complete output port settings.
	 * 
	 * @param newPortCount
	 *            the new port count
	 */
	public void completeOutputPortSettings(int newPortCount) {
		changeOutPortCount(newPortCount);
		for (int i = 0; i < (newPortCount); i++) {
			Port outPort = new Port(Constants.OUTPUT_SOCKET_TYPE + (i), Constants.OUTPUT_SOCKET_TYPE
					+ (i), this, newPortCount, PortTypeEnum.OUT, (i), isAllowMultipleLinksForPort("out0"), 
					isLinkMandatoryForPort("out0"), PortAlignmentEnum.RIGHT);
			ports.put(Constants.OUTPUT_SOCKET_TYPE + (i), outPort);
			firePropertyChange("Component:add", null, outPort);
		}
	} 


	/**
	 * Decrements port.
	 *
	 * @param portsToBeRemoved the ports to be removed
	 */
	public void decrementPorts(List<String> portsToBeRemoved) {

		deleteInputLinks(portsToBeRemoved);
		deleteOutputLinks(portsToBeRemoved);
		removePorts(portsToBeRemoved);
	}

	/**
	 * Delete input links.
	 *
	 * @param portsToBeRemoved the ports to be removed
	 */
	private void deleteInputLinks(List<String> portsToBeRemoved) {
		if (inputLinks.size() > 0) {
			Link[] inLinks = new Link[inputLinks.size()];
			inputLinks.toArray(inLinks);
			for (String portRemove : portsToBeRemoved) {

				for (Link l : inLinks) {
					if (l.getTargetTerminal().equals(portRemove)) {
						l.detachSource();
						l.detachTarget();
						l.getSource().freeOutputPort(l.getSourceTerminal());
						l.getTarget().freeInputPort(l.getTargetTerminal());
						l.setTarget(null);
						l.setSource(null);
					}
				}
			}
		}
	}

	/**
	 * Delete output links.
	 *
	 * @param portsToBeRemoved the ports to be removed
	 */
	private void deleteOutputLinks(List<String> portsToBeRemoved) {
		if (outputLinks.size() > 0) {
			Link[] outLinks = new Link[outputLinks.size()];
			outputLinks.toArray(outLinks);
			for (String portRemove : portsToBeRemoved) {
				for (Link l : outLinks) {
					if (l.getSourceTerminal().equals(portRemove)) {
						l.detachSource();
						l.detachTarget();
						l.getSource().freeOutputPort(l.getSourceTerminal());
						l.getTarget().freeInputPort(l.getTargetTerminal());
						l.setTarget(null);
						l.setSource(null);
					}
				}
			}
		}
	}

	/**
	 * Remove ports.
	 *
	 * @param portsToBeRemoved the ports to be removed
	 */
	private void removePorts(List<String> portsToBeRemoved) {
		for (String portRemove : portsToBeRemoved) {
			ports.remove(portRemove);
		}

	}

	/**
	 * Get watcher terminals.
	 *
	 * @return the watcher terminals
	 */
	public Map<String, Long> getWatcherTerminals() {
		return watcherTerminals;
	}

	/**
	 * Set watcher terminals.
	 *
	 * @param watcherTerminals the watcher terminals
	 */
	public void setWatcherTerminals(Map<String, Long> watcherTerminals) {
		this.watcherTerminals = watcherTerminals;
	}

	/**
	 * Add watcher terminal.
	 *
	 * @param port the port
	 * @param limit the limit
	 */
	public void addWatcherTerminal(String port, Long limit) {

		watcherTerminals.put(port, limit);
	}

	/**
	 * Remove watcher terminal.
	 *
	 * @param port the port
	 */
	public void removeWatcherTerminal(String port) {
		watcherTerminals.remove(port);
	}

	
	/**
	 * Clear watchers.
	 */
	public void clearWatchers() {
		watcherTerminals.clear();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((UniqueComponentName == null) ? 0 : UniqueComponentName.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Component other = (Component) obj;
		if (UniqueComponentName == null) {
			if (other.UniqueComponentName != null)
				return false;
		} else if (!UniqueComponentName.equals(other.UniqueComponentName))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Component [UniqueComponentName=" + UniqueComponentName + ", type=" + type + ", prefix=" + prefix
				+ ", category=" + category + ", inPortCount=" + leftPortCount + ", outPortCount=" + rightPortCount
				+ ", unusedPortCount=" + bottomPortCount + ", componentLabel=" + componentLabel + "]";
	}

	/**
	 * Gets the component edit part.
	 * 
	 * @return the component edit part
	 */
	public Object getComponentEditPart() {
		return componentEditPart;
	}

	/**
	 * Sets the component edit part.
	 * 
	 * @param componentEditPart
	 *            the new component edit part
	 */
	public void setComponentEditPart(Object componentEditPart) {
		this.componentEditPart = componentEditPart;
	}	
	
	
	/**
	 * Update status.
	 * 
	 * @param currentStatus
	 *            the current status
	 */
	public void updateStatus(String currentStatus) {
		status = ComponentExecutionStatus.fromValue(currentStatus);
		firePropertyChange(Props.EXECUTION_STATUS.getValue(), null, currentStatus);
	}

	/**
	 * Gets the status.
	 * 
	 * @return the status
	 */
	public ComponentExecutionStatus getStatus() {
		return status;
	}

	
	public boolean isContinuousSchemaPropogationAllow() {
		return isContinuousSchemaPropogationAllow;
	}

	public void setContinuousSchemaPropogationAllow(boolean isContinuousSchemaPropogationAllow) {
		this.isContinuousSchemaPropogationAllow = isContinuousSchemaPropogationAllow;
	}

	/**
	 * Sets the status.
	 * 
	 * @param status
	 *            the new status
	 */
	public void setStatus(ComponentExecutionStatus status) {
		this.status = status;
	}			  

	/**
	 * Validates all the properties of component and updates its validity status accordingly.
	 *  
	 * @return properties 
	 */
	public Map<String, Object> validateComponentProperties(boolean isJobImported) {
		boolean componentHasRequiredValues = Boolean.TRUE;
		hydrograph.ui.common.component.config.Component component = XMLConfigUtil.INSTANCE.getComponent(this.getComponentName());
		Map<String, Object> properties=this.properties;
		for (Property configProperty : component.getProperty()) {
			Object propertyValue = properties.get(configProperty.getName());
			
			List<String> validators = ComponentCacheUtil.INSTANCE.getValidatorsForProperty(this.getComponentName(), configProperty.getName());
			
			IValidator validator = null;
			for (String validatorName : validators) {
				try {
					validator = (IValidator) Class.forName(Constants.VALIDATOR_PACKAGE_PREFIX + validatorName).newInstance();
				} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
					logger.error("Failed to create validator", e);
					throw new RuntimeException("Failed to create validator", e);
				}
				boolean status = validator.validate(propertyValue, configProperty.getName(),new SchemaData().getInputSchema(this),
						isJobImported);
				//NOTE : here if any of the property is not valid then whole component is not valid 
				if(status == false){
					componentHasRequiredValues = Boolean.FALSE;
				}
			}
		}
		
		if (!componentHasRequiredValues && properties.get(Component.Props.VALIDITY_STATUS.getValue()) == null)
			properties.put(Component.Props.VALIDITY_STATUS.getValue(), Component.ValidityStatus.WARN.name());
		else if (!componentHasRequiredValues
				&& StringUtils.equals((String) properties.get(Component.Props.VALIDITY_STATUS.getValue()),
						Component.ValidityStatus.WARN.name()))
			properties.put(Component.Props.VALIDITY_STATUS.getValue(), Component.ValidityStatus.WARN.name());
		else if (!componentHasRequiredValues
				&& StringUtils.equals((String) properties.get(Component.Props.VALIDITY_STATUS.getValue()),
						Component.ValidityStatus.ERROR.name()))
			properties.put(Component.Props.VALIDITY_STATUS.getValue(), Component.ValidityStatus.ERROR.name());
		else if (!componentHasRequiredValues
				&& StringUtils.equals((String) properties.get(Component.Props.VALIDITY_STATUS.getValue()),
						Component.ValidityStatus.VALID.name()))
			properties.put(Component.Props.VALIDITY_STATUS.getValue(), Component.ValidityStatus.ERROR.name());
		else if (componentHasRequiredValues)
			properties.put(Component.Props.VALIDITY_STATUS.getValue(), Component.ValidityStatus.VALID.name());
		return properties;
	}	
	
	/**
	 * Checks if given class is of wrapper type.
	 * 
	 * @param clazz
	 *            the clazz
	 * @return true, if it's a wrapper type
	 */
	public static boolean isWrapperType(Class<?> clazz) {
		if (clazz == Boolean.class || clazz == Character.class || clazz == Byte.class || clazz == Short.class
				|| clazz == Integer.class || clazz == Long.class || clazz == Float.class || clazz == Double.class) {
			return true;
		}
		return false;
	}
	
	public String getDefaultPrefix() {
		return defaultPrefix;
	}
	public ArrayList<Link> getInputLinks() {
	  	return inputLinks;
	  		}
	
	
	/**
	 * Get Grid row type
	 * @return gridRowType
	 */
	public String getGridRowType() {
		return Constants.GENERIC_GRID_ROW;
	}
}
