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

package hydrograph.ui.graph.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IWorkbenchPart;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.graph.controller.ComponentEditPart;
import hydrograph.ui.graph.figure.ComponentFigure;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.graph.model.Container;
import hydrograph.ui.graph.model.Link;
import hydrograph.ui.graph.model.components.InputSubjobComponent;
import hydrograph.ui.graph.model.components.SubjobComponent;
import hydrograph.ui.propertywindow.widgets.utility.SchemaSyncUtility;
import hydrograph.ui.propertywindow.widgets.utility.SubjobUtility;

/**
 * add propagate action on right-click of component
 * 
 * 
 * @author Bitwise
 *
 */
public class PropagateDataAction extends SelectionAction {
    private Component component;
	
    public PropagateDataAction(IWorkbenchPart part) {
		super(part);
		setLazyEnablementCalculation(true);
	}
   
	@Override
	protected void init() {
		super.init();
		setText(Constants.PROPAGATE_FIELD_FROM_LEFT_ACTION); 
		setId(Constants.PROPAGATE);
		setEnabled(false);
	}
	
	
	@Override
	protected boolean calculateEnabled() {
		List<Object> selectedObjects = getSelectedObjects();
		if (selectedObjects != null && !selectedObjects.isEmpty() && selectedObjects.size() == 1) {
			for (Object obj : selectedObjects) {
				if (obj instanceof ComponentEditPart) {
					component=((ComponentEditPart) obj).getCastedModel();
					if(StringUtils.equalsIgnoreCase(Constants.STRAIGHTPULL, component.getCategory())
						||	StringUtils.equalsIgnoreCase(Constants.TRANSFORM,component.getCategory())
					    || StringUtils.equalsIgnoreCase(Constants.SUBJOB_COMPONENT,component.getComponentName())
							
							)
					{
						if(!component.getTargetConnections().isEmpty())
						return true;
					}	
				}
			}
		}
		return false;
	}
    
	@Override
	public void run() {
		boolean shouldsetContinuousSchemaPropagationFlagForNextConnectedComponents=true;
		Map<String,Schema> oldSchemaMap=new TreeMap<>();
		for(Link link:component.getTargetConnections())
		{
			Schema previousComponentSchema=SubjobUtility.INSTANCE.getSchemaFromPreviousComponentSchema(component, link);
			if(StringUtils.equalsIgnoreCase(component.getCategory(),Constants.STRAIGHTPULL)
					  ||StringUtils.equalsIgnoreCase(component.getComponentName(),Constants.FILTER)	
					  ||StringUtils.equalsIgnoreCase(component.getComponentName(),Constants.UNIQUE_SEQUENCE)
					  ||StringUtils.equalsIgnoreCase(component.getComponentName(),Constants.PARTITION_BY_EXPRESSION)
					)
			{	
				
				if(StringUtils.equalsIgnoreCase(Constants.UNION_ALL,component.getComponentName()))
				{
					if(!SubjobUtility.INSTANCE.isUnionAllInputSchemaInSync(component))
					{	
					component.getProperties().put(Constants.IS_UNION_ALL_COMPONENT_SYNC,Constants.FALSE);
					((ComponentEditPart)component.getComponentEditPart()).getFigure().repaint();
					shouldsetContinuousSchemaPropagationFlagForNextConnectedComponents=false;
					break;
					}
					else
					{
					component.getProperties().put(Constants.IS_UNION_ALL_COMPONENT_SYNC,Constants.TRUE);	
					((ComponentEditPart)component.getComponentEditPart()).getFigure().repaint();
					}	
				}
				Schema schema=(Schema)component.getProperties().get(Constants.SCHEMA_PROPERTY_NAME);
				if(schema==null)
				schema=new Schema();	
				
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
				else
				{	
				shouldsetContinuousSchemaPropagationFlagForNextConnectedComponents=false;
				}
				component.getProperties().put(Constants.SCHEMA_PROPERTY_NAME,schema);
				ComponentFigure componentFigure=(ComponentFigure)((ComponentEditPart)component.getComponentEditPart()).getFigure();
				component.validateComponentProperties(false);
				componentFigure.setPropertyStatus((String)(component.getProperties().get(Constants.VALIDITY_STATUS)));
				componentFigure.repaint();
				if(StringUtils.equalsIgnoreCase(Constants.UNION_ALL,component.getComponentName()))
				break;	
			}
			else if(component instanceof SubjobComponent)
			{
			//Container container=(Container)component.getProperties().get(Constants.SUBJOB_CONTAINER);
			Container container=(Container)component.getSubJobContainer().get(Constants.SUBJOB_CONTAINER);
			for(Component componentIterator:container.getUIComponentList())
			{
				if(componentIterator instanceof InputSubjobComponent)
				{
					SubjobUtility.INSTANCE.initializeSchemaMapForInputSubJobComponent(componentIterator,component);
					SubjobUtility.INSTANCE.setFlagForContinuousSchemaPropogation(componentIterator);
					break;
				}
				
			}
			shouldsetContinuousSchemaPropagationFlagForNextConnectedComponents=!SubjobUtility.INSTANCE.checkIfSubJobHasTransformOrUnionAllComponent(component);
			((ComponentEditPart)component.getComponentEditPart()).getFigure().repaint();
			break;
			}
			else if(StringUtils.equalsIgnoreCase(Constants.TRANSFORM,component.getCategory()))
			{
			 shouldsetContinuousSchemaPropagationFlagForNextConnectedComponents=false;	
			((ComponentEditPart)component.getComponentEditPart()).getFigure().repaint();
			}
			oldSchemaMap.put(link.getTargetTerminal(), previousComponentSchema);	
		}
		component.getProperties().put(Constants.PREVIOUS_COMPONENT_OLD_SCHEMA, oldSchemaMap);
		component.setContinuousSchemaPropogationAllow(true);
		if(shouldsetContinuousSchemaPropagationFlagForNextConnectedComponents)
		{
			SubjobUtility.INSTANCE.setFlagForContinuousSchemaPropogation(component);
		}
	}
}
