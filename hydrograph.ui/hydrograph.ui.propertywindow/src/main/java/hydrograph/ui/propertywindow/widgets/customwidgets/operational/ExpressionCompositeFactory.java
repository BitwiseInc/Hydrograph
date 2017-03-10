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


package hydrograph.ui.propertywindow.widgets.customwidgets.operational;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ExpandBar;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.datastructure.property.mapping.MappingSheetRow;
import hydrograph.ui.graph.model.Component;
import hydrograph.ui.propertywindow.widgets.customwidgets.config.WidgetConfig;

/**
 * @author Bitwise
 * Factory class for expression composite
 */
public class ExpressionCompositeFactory {
	 
    public static final ExpressionCompositeFactory INSTANCE= new ExpressionCompositeFactory();
    
     /**
     * @param parent
     * @param mappingSheetRow
     * @param component
     * @param widgetConfig
     * @return ExpressionComposite object on the basis of component name
     */
    public AbstractExpressionComposite getComposite(ExpandBar parent,MappingSheetRow mappingSheetRow,Component component
		,WidgetConfig widgetConfig )
    {
	  if(Constants.NORMALIZE.equalsIgnoreCase(component.getComponentName()))
	  {
		return new NormalizeExpressionComposite(parent, SWT.None, mappingSheetRow, component, widgetConfig);
	  }
	  else if(Constants.TRANSFORM.equalsIgnoreCase(component.getComponentName()))
	  {
		return new TransformExpressionComposite(parent, SWT.None, mappingSheetRow, component, widgetConfig);
	  }
	  else if(Constants.AGGREGATE.equalsIgnoreCase(component.getComponentName())||
			Constants.CUMULATE.equalsIgnoreCase(component.getComponentName()))
	  {
		return new AggregateCumulateExpressionComposite(parent, SWT.None, mappingSheetRow, component, widgetConfig);
	  }
	  else if(Constants.GROUP_COMBINE.equalsIgnoreCase(component.getComponentName()))
		  {
			return new GroupCombineExpressionComposite(parent, SWT.None, mappingSheetRow, component, widgetConfig);
		  }
      return null;
   }

}
