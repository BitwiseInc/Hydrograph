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
package hydrograph.ui.graph.command;

import hydrograph.ui.common.component.config.PortSpecification;
import hydrograph.ui.common.component.config.Property;
import hydrograph.ui.common.datastructures.tooltip.PropertyToolTipInformation;
import hydrograph.ui.common.util.ComponentCacheUtil;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.XMLConfigUtil;
import hydrograph.ui.graph.figure.ELTFigureConstants;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.PortAlignmentEnum;
import hydrograph.ui.graph.model.processor.DynamicClassProcessor;
import hydrograph.ui.graph.schema.propagation.SchemaData;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.validators.impl.IValidator;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.slf4j.Logger;


/**
 * The Class ComponentCreateCommand.
 * @author Bitwise
 */
public class ComponentCreateCommand extends Command {
	private static final Logger logger = LogFactory.INSTANCE.getLogger(ComponentCreateCommand.class);

	private SchemaData schemaData;
	/** The new Component. */
	private final Component component;
	/** Graph to add to. */
	private final Container parent;
	/** The bounds of the new Component. */
	private final Rectangle bounds;
	
	/**
	 * Create a command that will add a new Component in Graph.
	 * 
	 * @param component the new Component that is to be added
	 * @param parent the Graph that will hold the new element
	 * @param bounds the bounds of the new component; the size can be (-1, -1) if not known
	 * @throws IllegalArgumentException if any parameter is null, or the request does not provide a
	 *             new Component instance
	 */
	public ComponentCreateCommand(Component component, Container parent, Rectangle bounds) {

		String componentName = DynamicClassProcessor.INSTANCE.getClazzName(component.getClass());
		hydrograph.ui.common.component.config.Component components = XMLConfigUtil.INSTANCE.getComponent(componentName);
		schemaData = new SchemaData();
		//attach tooltip information to component
		Map<String,PropertyToolTipInformation> tooltipInformation = new LinkedHashMap<>();
		for(Property property : components.getProperty()){
			tooltipInformation.put(property.getName(),new PropertyToolTipInformation(property.getName(), property.getShowAsTooltip().value(), property.getTooltipDataType().value()));
		}
		component.setTooltipInformation(tooltipInformation);
		
		int totalPortsOnLeftSide=0, totalPortsOnRightSide=0, totalPortsOnBottomSide=0;
		List<PortSpecification> portSpecification = XMLConfigUtil.INSTANCE.getComponent(componentName).getPort().getPortSpecification();
		for(PortSpecification p:portSpecification)
		{	
			if(PortAlignmentEnum.LEFT.value().equals(p.getPortAlignment().value())){
				totalPortsOnLeftSide=p.getNumberOfPorts();
			}else if(PortAlignmentEnum.RIGHT.value().equals(p.getPortAlignment().value())){
				totalPortsOnRightSide=p.getNumberOfPorts();
			}else if(PortAlignmentEnum.BOTTOM.value().equals(p.getPortAlignment().value())){
				totalPortsOnBottomSide=p.getNumberOfPorts();
			}
		}
		int heightFactor=totalPortsOnLeftSide > totalPortsOnRightSide ? totalPortsOnLeftSide : totalPortsOnRightSide;
		int height = (heightFactor+1)*27;
		
		int widthFactor = totalPortsOnBottomSide;
		int width = 100;
		if(widthFactor > 1)
			width =(widthFactor+1)*33;

		setupComponent(component);		

		Dimension newSize = new Dimension(width, height + ELTFigureConstants.componentOneLineLabelMargin);
		if(newSize.height<75)
			newSize.height=75;
		this.component = component;
		this.parent = parent;
		Point p = new Point(bounds.x, bounds.y);
		this.bounds = new Rectangle(p, newSize);
		setLabel("Component creation");
		
	}
	
	@Override
	public void execute() {
		component.setLocation(bounds.getLocation());
		Dimension size = bounds.getSize();
		if (size.width > 0 && size.height > 0)
			component.setSize(size);
		redo();
	}
	
	/**
	 * Can execute if all the necessary information has been provided.
	 * 
	 */
	@Override
	public boolean canExecute() {
		return component != null && parent != null && bounds != null;
	}
	
	/**
	 * Add component to container
	 */
	@Override
	public void redo() {
		parent.addChild(component);
	}

	/**
	 * Undo add action
	 */
	@Override
	public void undo() {
		parent.removeChild(component);
	}
	
	private void setupComponent(Component component) {
		String componentName = DynamicClassProcessor.INSTANCE.getClazzName(component.getClass());
		hydrograph.ui.common.component.config.Component componentConfig = XMLConfigUtil.INSTANCE.getComponent(componentName);
		component.setProperties(prepareComponentProperties(componentName, component.getProperties()));
		component.setType(componentConfig.getNameInPalette());
		component.setCategory(componentConfig.getCategory().value());
		component.setPrefix(componentConfig.getDefaultNamePrefix());
	}
	
	private Map<String, Object> prepareComponentProperties(String componentName, Map<String, Object> existingProperties) {
		boolean componentHasRequiredValues = Boolean.TRUE;
		Map<String, Object> properties = ComponentCacheUtil.INSTANCE.getProperties(componentName);
		properties.put(Constants.PARAM_NAME, componentName);
		
		hydrograph.ui.common.component.config.Component component = XMLConfigUtil.INSTANCE.getComponent(componentName);
		for (Property configProperty : component.getProperty()) {
			Object propertyValue = properties.get(configProperty.getName());
			
			List<String> validators = ComponentCacheUtil.INSTANCE.getValidatorsForProperty(componentName, configProperty.getName());
			
			IValidator validator = null;
			for (String validatorName : validators) {
				try {
					validator = (IValidator) Class.forName(Constants.VALIDATOR_PACKAGE_PREFIX + validatorName).newInstance();
				} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
					logger.error("Failed to create validator", e);
					throw new RuntimeException("Failed to create validator", e);
				}
				boolean status = validator.validate(propertyValue, configProperty.getName(),schemaData.getInputSchema(this.component),false);
				//NOTE : here if any of the property is not valid then whole component is not valid 
				if(status == false){
					componentHasRequiredValues = Boolean.FALSE;
				}
			}
		}
		if(!componentHasRequiredValues){
			properties.put(Component.Props.VALIDITY_STATUS.getValue(), Component.ValidityStatus.WARN.name());
		}
		properties.putAll(existingProperties);
		return properties;
	}
}
