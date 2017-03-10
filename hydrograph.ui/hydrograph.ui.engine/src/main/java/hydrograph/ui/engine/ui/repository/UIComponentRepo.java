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

 
package hydrograph.ui.engine.ui.repository;

import hydrograph.engine.jaxb.commontypes.TypeBaseComponent;
import hydrograph.engine.jaxb.main.Graph;
import hydrograph.ui.engine.ui.converter.LinkingData;
import hydrograph.ui.graph.model.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class UIComponentRepo {

	private  LinkedHashMap<String, Class> componentFactory = new LinkedHashMap<>();
	private  LinkedHashMap<String, Component> componentUiFactory = new LinkedHashMap<>();
	private  LinkedHashMap<String, List<ParameterData>> parammeterFactory = new LinkedHashMap<>();
	private  LinkedHashMap<String, List<InSocketDetail>> inSocketMap = new LinkedHashMap<>();
	private List<LinkingData> componentLinkList=new ArrayList<LinkingData>();
	
	
	public void genrateComponentRepo(Graph graph) {

		for (Object component : graph.getInputsOrOutputsOrStraightPulls()) {
			getComponentFactory().put(((TypeBaseComponent) component).getId(),
					component.getClass());
		}
	}

	public  LinkedHashMap<String, Class> getComponentFactory() {
		return componentFactory;
	}

	public LinkedHashMap<String, List<InSocketDetail>> getInsocketMap()
	{
		return inSocketMap;
	}
	
	public  LinkedHashMap<String, Component> getComponentUiFactory() {
		return componentUiFactory;
	}
	
	public List<LinkingData> getComponentLinkList() {
		return componentLinkList;
	}
	
	public LinkedHashMap<String, List<ParameterData>> getParammeterFactory() {
		return parammeterFactory;
	}

	public void flushRepository() {
		componentFactory.clear();
		componentUiFactory.clear();
		parammeterFactory.clear();
		componentLinkList.clear();
		inSocketMap.clear();
		}
}
