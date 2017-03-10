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

 
package hydrograph.ui.graph.utility;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.ComponentsOutputSchema;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.graph.model.components.CloneComponent;
import hydrograph.ui.graph.model.components.InputSubjobComponent;
import hydrograph.ui.graph.model.components.OutputSubjobComponent;
import hydrograph.ui.graph.model.components.UnionallComponent;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;


/**
 * Class SubJobPortLinkUtilty has business logic to link the port in sub graph component.
 * @author Bitwise
 *
 */
public class SubJobPortLinkUtilty {
	
	/**
	 * Adds the input sub graph component and link.
	 *
	 * @param container the container
	 * @param cacheInputSubjobComp map of component and number of port.
	 * @param clipboardList the clipboard list
	 * @return the component
	 */
	public static Component addInputSubJobComponentAndLink(Container container,Map<Component,Integer> cacheInputSubjobComp,List clipboardList){
	 int outPort=0;
	 InputSubjobComponent inputSubComponent=new InputSubjobComponent();
	 for (Map.Entry<Component,Integer> entry : cacheInputSubjobComp.entrySet()) {
	   		inputSubComponent.setProperties(new LinkedHashMap<String,Object>());
	   		List<Link> linkedList = entry.getKey().getTargetConnections();
	   		int inputLinksCountForEntry = linkedList.size();
	   		
	   		if(inputLinksCountForEntry == 0){
				for(int j=0;j<entry.getValue();j++) {
					createNewLink(entry, inputSubComponent, outPort, j);
					outPort++;
				}
			}else if(inputLinksCountForEntry < entry.getValue()){
				for(int j=0;j<entry.getValue();j++) {
					for (int i = 0; i < inputLinksCountForEntry; i++){
						if(!linkedList.get(i).getTargetTerminal().equalsIgnoreCase(Constants.INPUT_SOCKET_TYPE + j)){
							createNewLink(entry, inputSubComponent, outPort, j);
							outPort++;
						}
					}
				}
			}
			inputSubComponent.getProperties().put(Constants.TYPE, Constants.INPUT_SUBJOB);							   	
			container.addSubJobChild((Component) entry.getKey());
			clipboardList.remove(entry.getKey());						
		}
	 
	   	if(cacheInputSubjobComp.size()>0){
	   		inputSubComponent.getProperties().put(Constants.NAME, Constants.INPUT_SUBJOB);
	   		inputSubComponent.setComponentId(Constants.INPUT_SUBJOB);
	   		inputSubComponent.setComponentLabel(Constants.INPUT_SUBJOB);
	   		inputSubComponent.setType(Constants.INPUT_SUBJOB_TYPE);
	   		inputSubComponent.setCategory(Constants.SUBJOB_COMPONENT_CATEGORY);
	   		inputSubComponent.completeOutputPortSettings(outPort);
	   		fixComponentSize(inputSubComponent, outPort);
	   		setXYCoordinate(inputSubComponent,0 , 0);
	   		inputSubComponent.setParent(container); 
	   		container.addSubJobChild(inputSubComponent);
	   	}
	   	return inputSubComponent;
	}

	
	private static void createNewLink(Entry<Component, Integer> entry, InputSubjobComponent inputSubComponent, int outPort, int j) {
		Link linkNew = new Link();
		linkNew.setTarget(entry.getKey());
		linkNew.setSource(inputSubComponent); 
		if(entry.getKey() instanceof UnionallComponent){
			linkNew.setTargetTerminal(Constants.FIXED_INSOCKET_ID);
		}else{
			linkNew.setTargetTerminal(Constants.INPUT_SOCKET_TYPE + j);
		}
		linkNew.setSourceTerminal(Constants.OUTPUT_SOCKET_TYPE + outPort);
		entry.getKey().connectInput(linkNew);
		inputSubComponent.connectOutput(linkNew);
	}


	/**
	 * Adds the output sub graph component and link.
	 *
	 * @param container the container
	 * @param cacheInputSubjobComp the cache input subjob comp
	 * @param cacheOutSubjobComp the cache out subjob comp
	 * @param clipboardList the clipboard list
	 * @return the component
	 */
	public static Component addOutputSubJobComponentAndLink(Container container,Map<Component,Integer> cacheInputSubjobComp,Map<Component,List<String>> cacheOutSubjobComp,List clipboardList){
		 int inPort=0;
		OutputSubjobComponent outSubComponent=new OutputSubjobComponent();
		Map<String, ComponentsOutputSchema> schemaMap = new HashMap<String, ComponentsOutputSchema>();
		for (Map.Entry<Component,List<String>> entry : cacheOutSubjobComp.entrySet()) {
		   					outSubComponent.setProperties(new LinkedHashMap<String,Object>());
		   					for (String sourceTerminal : entry.getValue()) {
		   					Link linkNew = new Link();
							linkNew.setSource(entry.getKey());
							linkNew.setTarget(outSubComponent);
							linkNew.setSourceTerminal(sourceTerminal);
							linkNew.setTargetTerminal(Constants.INPUT_SOCKET_TYPE+inPort);
							entry.getKey().connectOutput(linkNew);
							outSubComponent.connectInput(linkNew);
							if(entry.getKey() instanceof CloneComponent){
								rearrangeLinkNumbers(entry.getKey());
							}
							outSubComponent.getProperties().put(Constants.TYPE, Constants.OUTPUT_SUBJOB);			
							inPort++;
		   					}		   					
							if(cacheInputSubjobComp.get(entry.getKey())==null){
								container.addSubJobChild(entry.getKey());
								clipboardList.remove(entry.getKey());
							}							

		   	}
		   	if(cacheOutSubjobComp.size()>0){
		   		outSubComponent.getProperties().put(Constants.NAME,Constants.OUTPUT_SUBJOB);
		   		outSubComponent.setComponentLabel(Constants.OUTPUT_SUBJOB);
		   		outSubComponent.setComponentId(Constants.OUTPUT_SUBJOB);
		   		outSubComponent.setType(Constants.OUTPUT_SUBJOB_TYPE);
		   		outSubComponent.setCategory(Constants.SUBJOB_COMPONENT_CATEGORY);
		   		outSubComponent.completeInputPortSettings(inPort);	
		   		fixComponentSize(outSubComponent, inPort);
		   		setXYCoordinate(outSubComponent,getMaxXCoordinate(container)+300 , 0);
		   		outSubComponent.getProperties().put(Constants.SCHEMA_TO_PROPAGATE,schemaMap);
		   		outSubComponent.setParent(container); 
		   		container.addSubJobChild(outSubComponent);
		   		
		   	}
		   	return outSubComponent;
		}
		 
	private static void rearrangeLinkNumbers(Component component) {
		for(Link link:component.getSourceConnections()){
			link.setLinkNumber(component.getSourceConnections().indexOf(link));
		}
	}


	/**
	 * Gets the max x coordinate.
	 *
	 * @param container the container
	 * @return the max x coordinate
	 */
	public static int getMaxXCoordinate(Container container){
		int maxXCoordinate=Integer.MIN_VALUE;
		  for (Object object : container.getChildren()) {
			  if(object instanceof Component){
				  Component component = (Component)object;
			 if(component.getLocation().x > maxXCoordinate){
				 maxXCoordinate = component.getLocation().x ;
		     }
		}
	  }
		return maxXCoordinate;
	 }
	/**
	 * Fix component size.
	 *
	 * @param component the component
	 * @param portCount the port count
	 */
	public static void fixComponentSize(Component component,int portCount){
		Dimension newSize = component.getSize();
		component.setSize(newSize.expand(0, portCount * 15));
	}
	
	/**
	 * Sets the xy coordinate.
	 *
	 * @param component the component
	 * @param x the x Coordinate
	 * @param y the y Coordinate
	 */
	public static void setXYCoordinate(Component component,int x ,int y){
		Point point = new Point();
		point.x = x;
		point.y = y;
		component.setLocation(point);
	}
}
