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
package hydrograph.ui.propertywindow.widgets.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.validator.Validator;
import hydrograph.ui.datastructure.property.ComponentsOutputSchema;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.datastructure.property.mapping.TransformMapping;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.graph.model.components.InputSubjobComponent;
import hydrograph.ui.graph.model.components.OutputSubjobComponent;
import hydrograph.ui.graph.model.components.SubjobComponent;
import hydrograph.ui.graph.schema.propagation.SchemaPropagation;
import hydrograph.ui.propertywindow.schema.propagation.helper.SchemaPropagationHelper;

public class SubjobUtility {
public static final SubjobUtility INSTANCE= new SubjobUtility();
	
     /**
	 * This method set continuousSchema propagation flag to true until it encounters transform or union All component.
	 * @param component through which continuous propagation starts. 
	 */
	public void setFlagForContinuousSchemaPropogation(Component component) {
		
		for(Link link:component.getSourceConnections())
		{
			Schema previousComponentSchema=SubjobUtility.INSTANCE.getSchemaFromPreviousComponentSchema(component, link);
			Component nextComponent=link.getTarget();
			if (nextComponent != null) {
			Schema schema=(Schema)nextComponent.getProperties().get(Constants.SCHEMA_PROPERTY_NAME);
			while(StringUtils.equalsIgnoreCase(nextComponent.getCategory(), Constants.STRAIGHTPULL)
					||StringUtils.equalsIgnoreCase(nextComponent.getComponentName(),Constants.FILTER)	
					 ||StringUtils.equalsIgnoreCase(nextComponent.getComponentName(),Constants.UNIQUE_SEQUENCE)
					 ||StringUtils.equalsIgnoreCase(nextComponent.getComponentName(),Constants.PARTITION_BY_EXPRESSION)
					 ||nextComponent instanceof SubjobComponent
					 ||nextComponent instanceof OutputSubjobComponent
					)
			{
				if(StringUtils.equalsIgnoreCase(Constants.UNION_ALL,nextComponent.getComponentName()))
				{
					if(!isUnionAllInputSchemaInSync(nextComponent))
					{	
					nextComponent.getProperties().put(Constants.IS_UNION_ALL_COMPONENT_SYNC,Constants.FALSE);
					((AbstractGraphicalEditPart)nextComponent.getComponentEditPart()).getFigure().repaint();
					break;
					}
					else
					{	
					nextComponent.getProperties().put(Constants.IS_UNION_ALL_COMPONENT_SYNC,Constants.TRUE);
					((AbstractGraphicalEditPart)nextComponent.getComponentEditPart()).getFigure().repaint();
					}
				}
				
				if(schema==null)
				schema=new Schema();	
				ComponentsOutputSchema outputSchema=SchemaPropagation.INSTANCE.getComponentsOutputSchema(link);
				if(schema.getGridRow()==null)
				{
					List<GridRow> gridRows=new ArrayList<>();
					schema.setGridRow(gridRows);
				}	
				schema.getGridRow().clear();
				if(previousComponentSchema!=null &&!previousComponentSchema.getGridRow().isEmpty())
				{	
				schema.getGridRow().addAll(SchemaSyncUtility.INSTANCE.convertGridRowsSchemaToBasicSchemaGridRows(previousComponentSchema.getGridRow()));
				}
				if(!StringUtils.equalsIgnoreCase(Constants.SUBJOB_COMPONENT_CATEGORY, nextComponent.getCategory()))
				{	
				nextComponent.getProperties().put(Constants.SCHEMA_PROPERTY_NAME,schema);
				nextComponent.validateComponentProperties(false);
				Validator validator=(Validator)((AbstractGraphicalEditPart)nextComponent.getComponentEditPart()).getFigure();
				validator.setPropertyStatus((String)nextComponent.getProperties().get(Constants.VALIDITY_STATUS));
				((AbstractGraphicalEditPart)nextComponent.getComponentEditPart()).getFigure().repaint();
				}
				
				nextComponent.setContinuousSchemaPropogationAllow(true);
				
				if(nextComponent instanceof SubjobComponent)
				{	
					Container container=(Container)nextComponent.getSubJobContainer().get(Constants.SUBJOB_CONTAINER);
					for(Component subjobComponent:container.getUIComponentList())
					{
						if(subjobComponent instanceof InputSubjobComponent)
						{
							initializeSchemaMapForInputSubJobComponent(subjobComponent,nextComponent);
							setFlagForContinuousSchemaPropogation(subjobComponent);
							break;
						}
					}
					showOrHideErrorSymbolOnComponent(container,nextComponent);
					Validator validator=(Validator)((AbstractGraphicalEditPart)nextComponent.getComponentEditPart()).getFigure();
					validator.setPropertyStatus((String)nextComponent.getProperties().get(Constants.VALIDITY_STATUS));
					((AbstractGraphicalEditPart)nextComponent.getComponentEditPart()).getFigure().repaint();	
				}
				else if(nextComponent instanceof OutputSubjobComponent)
				{
					Component subJobComponent = (Component) nextComponent.getSubJobContainer().get(Constants.SUBJOB_COMPONENT);
					if(subJobComponent!=null)
					SubjobUtility.INSTANCE.initializeSchemaMapForInputSubJobComponent(subJobComponent, nextComponent);
					setFlagForContinuousSchemaPropogation(subJobComponent);
				}
				if(!nextComponent.getSourceConnections().isEmpty())
				{
				   if(nextComponent.getSourceConnections().size()==1)
			    	{
				     if(nextComponent instanceof SubjobComponent)
				     {
					   if(!checkIfSubJobHasTransformOrUnionAllComponent(nextComponent))
					    {
						nextComponent=nextComponent.getSourceConnections().get(0).getTarget();	
					    }
					   else
					   {
						((AbstractGraphicalEditPart)nextComponent.getComponentEditPart()).getFigure().repaint();      
						break; 
					   }
				     }	
				    else
				    {
				    nextComponent=nextComponent.getSourceConnections().get(0).getTarget();
				    }
				   }
			       else
				   {
					setFlagForContinuousSchemaPropogation(nextComponent);
					break;
				   }
				}
				else
				break;	
			}
		}
			if(StringUtils.equalsIgnoreCase(nextComponent.getComponentName(),Constants.JOIN)||
					StringUtils.equalsIgnoreCase(nextComponent.getComponentName(),Constants.LOOKUP)){
				nextComponent.setContinuousSchemaPropogationAllow(true);
				((AbstractGraphicalEditPart)nextComponent.getComponentEditPart()).getFigure().repaint();
			}
			else if(StringUtils.equalsIgnoreCase(nextComponent.getComponentName(),Constants.AGGREGATE)
		    		||StringUtils.equalsIgnoreCase(nextComponent.getComponentName(),Constants.CUMULATE)
		    		||StringUtils.equalsIgnoreCase(nextComponent.getComponentName(),Constants.GROUP_COMBINE)
		    		||StringUtils.equalsIgnoreCase(nextComponent.getComponentName(),Constants.NORMALIZE)
		    		||StringUtils.equalsIgnoreCase(nextComponent.getComponentName(),Constants.TRANSFORM))
			{
				TransformMapping transformMapping;
				if(nextComponent.getProperties().get(Constants.OPERATION)==null){
	        	  transformMapping=new TransformMapping();
	        	  nextComponent.getProperties().put(Constants.OPERATION, transformMapping);
				}else{
	        	  transformMapping=(TransformMapping)nextComponent.getProperties().get(Constants.OPERATION);
				}
	        	OutputRecordCountUtility.INSTANCE.getPropagatedSchema(transformMapping, nextComponent);
				if(transformMapping.isAllInputFieldsArePassthrough()){
					Schema schema= (Schema)nextComponent.getProperties().get(Constants.SCHEMA_PROPERTY_NAME);
					if(schema==null){
		        	 schema=initSchemaObject();
					}
					OutputRecordCountUtility.INSTANCE.addPassThroughFieldsToSchema(transformMapping,nextComponent,schema);
					setFlagForContinuousSchemaPropogation(nextComponent);
					nextComponent.getProperties().put(Constants.SCHEMA_PROPERTY_NAME,schema);
					nextComponent.validateComponentProperties(false);
					Validator validator=(Validator)((AbstractGraphicalEditPart)nextComponent.getComponentEditPart()).getFigure();
					validator.setPropertyStatus((String)nextComponent.getProperties().get(Constants.VALIDITY_STATUS));
					((AbstractGraphicalEditPart)nextComponent.getComponentEditPart()).getFigure().repaint();
					
				}else{
					nextComponent.setContinuousSchemaPropogationAllow(true);
					((AbstractGraphicalEditPart)nextComponent.getComponentEditPart()).getFigure().repaint();
				}
				
			}
			nextComponent.getProperties().put(Constants.PREVIOUS_COMPONENT_OLD_SCHEMA,
					component.getProperties().get(Constants.PREVIOUS_COMPONENT_OLD_SCHEMA));

		}
	}
	
	/**
	 * check whether union compoent's  input schema are in sync or not
	 * @param union All component
	 * @return true if input schema are in sync otherwise false
	 */
	public boolean isUnionAllInputSchemaInSync(Component component) {
		Schema previousSchema=null;
		if(component.getTargetConnections()!=null && component.getTargetConnections().size()>=2){
			for(Link link:component.getTargetConnections()){
				Schema currentComponentSchema=getSchemaFromPreviousComponentSchema(component,link);
				if(previousSchema!=null && !isSameSchema(previousSchema,currentComponentSchema)){
					return false;
				}
				previousSchema=currentComponentSchema;
			}
		}
       return true;
	}
	
	
	private boolean isSameSchema(Schema previousSchema, Schema currentComponentSchema) {
		if(	(previousSchema!=null && currentComponentSchema!=null)
			&& (previousSchema.getGridRow()!=null && currentComponentSchema.getGridRow()!=null)
			&& (previousSchema.getGridRow().size()==currentComponentSchema.getGridRow().size())){
			for(int index = 0; index <previousSchema.getGridRow().size(); index++){
				if(!SchemaPropagationHelper.INSTANCE.isGridRowEqual(previousSchema.getGridRow().get(index),
						currentComponentSchema.getGridRow().get(index))){
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * check if sub job contains transform or union All component
	 * 
	 * @param Subjob component
	 * @return true if Sub job contains transform or union all component otherwise false
	 */
	public boolean checkIfSubJobHasTransformOrUnionAllComponent(Component component) {
		boolean containsTransformOrUnionAllComponent=false;
		Container container=(Container)component.getSubJobContainer().get(Constants.SUBJOB_CONTAINER);
		
		if(container!=null)
		{
		for(Object object:container.getChildren())
		{
			if(object instanceof Component)
			{
			Component component1=(Component)object;	
			if((StringUtils.equalsIgnoreCase(component1.getCategory(), Constants.TRANSFORM)
					&&!StringUtils.equalsIgnoreCase(component1.getComponentName(), Constants.FILTER)
					&&!StringUtils.equalsIgnoreCase(component1.getComponentName(), Constants.UNIQUE_SEQUENCE)
					&&!StringUtils.equalsIgnoreCase(component1.getComponentName(),Constants.PARTITION_BY_EXPRESSION))
					&& component1.isContinuousSchemaPropogationAllow()
					 )
			{
				containsTransformOrUnionAllComponent=true;
			    break;
			}
			else if((StringUtils.equalsIgnoreCase( Constants.UNION_ALL, component1.getComponentName())))
			{
				if(!isUnionAllInputSchemaInSync(component1))
				{
					containsTransformOrUnionAllComponent=true;
				    break;
				}	
			}		
			else if(component1 instanceof SubjobComponent)
			{
				containsTransformOrUnionAllComponent=checkIfSubJobHasTransformOrUnionAllComponent(component1);
				if(containsTransformOrUnionAllComponent)
				break;	
			}
			}
		}
		}
		return containsTransformOrUnionAllComponent;
	}
	/**
	 * 
	 * initialize SchemaMap for inputSubjobComponent.
	 * @param inputSubJobComponent
	 * @param subjobComponent
	 */
	public void initializeSchemaMapForInputSubJobComponent(Component inputSubJobComponent,Component subjobComponent) {
		Map<String,Schema> inputSubJobComponentHashMap=new HashMap<>();
		for(int i=0;i<subjobComponent.getTargetConnections().size();i++)
		{
			Component previousComponentToSubJobComponenet=subjobComponent.getTargetConnections().get(i).getSource();
			if(previousComponentToSubJobComponenet instanceof SubjobComponent)
			{
				inputSubJobComponentHashMap	=(Map<String,Schema>)previousComponentToSubJobComponenet.getProperties()
						.get(Constants.SCHEMA_FOR_INPUTSUBJOBCOMPONENT);
			}	
			else
			{
				inputSubJobComponentHashMap.put(Constants.INPUT_SOCKET_TYPE+i,((Schema)previousComponentToSubJobComponenet
						.getProperties().get(Constants.SCHEMA)));	
			}	
		}	
		inputSubJobComponent.getProperties().put(Constants.SCHEMA_FOR_INPUTSUBJOBCOMPONENT, inputSubJobComponentHashMap);
	}
	/**
	 * This method shows or hides error icon on component
	 * @param subJobContainer
	 * @param uiComponent
	 */
	public void showOrHideErrorSymbolOnComponent(Container subJobContainer, Component uiComponent) {
		if (subJobContainer == null) {
			uiComponent.setValidityStatus(Constants.ERROR);
		} else {
			for (int i = 0; i < subJobContainer.getUIComponentList().size(); i++) {
				if(subJobContainer.getUIComponentList().get(i) instanceof Component){
					Component component = subJobContainer.getUIComponentList().get(i);
				if (!(component instanceof InputSubjobComponent || component instanceof OutputSubjobComponent)) {
					if (StringUtils.equalsIgnoreCase(Constants.ERROR, 
							component.getProperties().get(Constants.VALIDITY_STATUS).toString())
							|| StringUtils.equalsIgnoreCase(
									Constants.WARN,
									component.getProperties()
											.get(Constants.VALIDITY_STATUS).toString())) {
						uiComponent.getProperties().put(Constants.VALIDITY_STATUS,
								Constants.ERROR);
						uiComponent.setValidityStatus(Constants.ERROR);
						break;
					} else {
						uiComponent.getProperties().put(Constants.VALIDITY_STATUS,
								Constants.VALID);
						uiComponent.setValidityStatus(Constants.VALID);
					}
				}
			   }
			}
		}
		
	}
	
	/**
	 * 
	 * gets the schema of previous component
	 * @param present component
	 * @param link
	 * @return
	 */
	public Schema getSchemaFromPreviousComponentSchema(Component component,Link link) {
		Schema previousComponentSchema=null;
		if(StringUtils.equalsIgnoreCase(Constants.INPUT_SUBJOB_COMPONENT_NAME, link.getSource().getComponentName())
		||StringUtils.equalsIgnoreCase(Constants.SUBJOB_COMPONENT, link.getSource().getComponentName())		
				)
		{
			Map<String,Schema> inputSchemaMap=(HashMap<String,Schema>)link.getSource().getProperties().
					get(Constants.SCHEMA_FOR_INPUTSUBJOBCOMPONENT);
			if(inputSchemaMap!=null)
			previousComponentSchema=inputSchemaMap.get(Constants.INPUT_SOCKET_TYPE+getPortIndex(link));
		}	
		else
		{
			 previousComponentSchema=SchemaPropagation.INSTANCE.getSchema(link);
		}
		return previousComponentSchema;
	}

	private String getPortIndex(Link link) {
			if(StringUtils.startsWithIgnoreCase(link.getSourceTerminal(), Constants.INPUT_SOCKET_TYPE)){
				return StringUtils.remove(link.getSourceTerminal(), Constants.INPUT_SOCKET_TYPE);
			}else {
				return StringUtils.remove(link.getSourceTerminal(), Constants.OUTPUT_SOCKET_TYPE);
			}
		
	}
	
	private Schema initSchemaObject() {
		Schema setSchemaForInternalPapogation = new Schema();
		setSchemaForInternalPapogation.setIsExternal(false);
		List<GridRow> gridRows = new ArrayList<>();
		setSchemaForInternalPapogation.setGridRow(gridRows);
		setSchemaForInternalPapogation.setExternalSchemaPath("");
		return setSchemaForInternalPapogation;
		
	}
	
}
