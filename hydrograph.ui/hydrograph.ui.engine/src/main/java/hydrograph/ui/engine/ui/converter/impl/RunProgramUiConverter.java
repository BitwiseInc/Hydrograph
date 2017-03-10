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

package hydrograph.ui.engine.ui.converter.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;

import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.engine.ui.constants.UIComponentsConstants;
import hydrograph.ui.engine.ui.converter.CommandUiConverter;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.components.RunProgram;
import hydrograph.ui.logging.factory.LogFactory;

/**
 * The class RunProgramUiConverter
 * 
 * @author Bitwise
 * 
 */
public class RunProgramUiConverter extends CommandUiConverter {

	private static final Logger LOGGER = LogFactory.INSTANCE
			.getLogger(RunProgramUiConverter.class);

	public RunProgramUiConverter(TypeBaseComponent typeBaseComponent,
			Container container) {
		this.container = container;
		this.typeBaseComponent = typeBaseComponent;
		this.uiComponent = new RunProgram();
		this.propertyMap = new LinkedHashMap<>();		
	}
	
	@Override
	public void prepareUIXML() {
		super.prepareUIXML();
		LOGGER.debug("Fetching COMMAND-Properties for -{}", componentName);
		hydrograph.engine.jaxb.commandtypes.RunProgram runProgram = (hydrograph.engine.jaxb.commandtypes.RunProgram) typeBaseComponent;	
		
		container.getComponentNextNameSuffixes().put(name_suffix, 0);
		container.getComponentNames().add(runProgram.getId());
		
		propertyMap.put(Constants.RUN_COMMAND_PROPERTY_NAME, runProgram.getCommand().getValue());
		propertyMap.put(Constants.BATCH, runProgram.getBatch());
		uiComponent.setProperties(propertyMap);
		
		uiComponent.setType(UIComponentsConstants.RUN_PROGRAM.value());
		
	}

	@Override
	protected Map<String, String> getRuntimeProperties() {
		return null;
	}

}
