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

import hydrograph.ui.datastructure.expression.ExpressionEditorData;
import hydrograph.ui.datastructure.property.FilterProperties;
import hydrograph.ui.datastructure.property.NameValueProperty;
import hydrograph.ui.datastructure.property.mapping.TransformMapping;
import hydrograph.ui.expression.editor.util.ExpressionEditorUtil;
import hydrograph.ui.propertywindow.widgets.utility.DragDropOperation;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.TableViewer;



public class DragDropTransformOpImp implements DragDropOperation {

	private List<FilterProperties> listOfInputFields;
	private List<NameValueProperty> mapAndPassThroughField;
	private boolean isSingleColumn;
	private TableViewer operationInputfieldtableviewer;
	private List<FilterProperties> listOfOutputFields;
	private TableViewer operationOutputFieldTableViewer;
	private Map<String,List<FilterProperties>> outputFieldMap;
	private TransformDialog transformDialogNew;
	private List<FilterProperties> outerOutputList;
	private boolean isExpression;
	private AbstractExpressionComposite expressionComposite;
	/**
	 * @param transformDialogNew
	 * @param mappingSheetRows
	 * @param outputFieldTableViewer
	 * @param mapAndPassThroughField
	 * @param outputFieldMap
	 * @param listOfOutputFields
	 * @param listOfInputFields
	 * @param isSingleColumn
	 * @param tableViewer
	 * @param t
	 */
	public DragDropTransformOpImp(TransformDialog transformDialogNew,Map<String,List<FilterProperties>> outputFieldMap,
			List<FilterProperties> listOfOutputFields,List<FilterProperties> listOfInputFields, 
			boolean isSingleColumn,TableViewer tableViewer,TableViewer t,List<FilterProperties> outerOutputList) 
	{
		this.listOfInputFields = listOfInputFields;
		this.isSingleColumn = isSingleColumn;
		this.operationInputfieldtableviewer=tableViewer;
		this.listOfOutputFields=listOfOutputFields;
		this.operationOutputFieldTableViewer=t;
		this.outputFieldMap=outputFieldMap;
		this.transformDialogNew=transformDialogNew;
		this.outerOutputList=outerOutputList;
	}
	
	/**
	 * @param transformDialogNew
	 * @param transformMapping
	 * @param inputFields
	 * @param isSingleColumn
	 * @param isExpression
	 * @param tableViewer
	 */
	public DragDropTransformOpImp(TransformDialog transformDialogNew,TransformMapping transformMapping,List<FilterProperties> inputFields, 
			boolean isSingleColumn,boolean isExpression,TableViewer tableViewer) 
	{
		this.mapAndPassThroughField = transformMapping.getMapAndPassthroughField();
		this.listOfInputFields=inputFields;
		this.isSingleColumn = isSingleColumn;
		this.operationInputfieldtableviewer=tableViewer;
		this.transformDialogNew=transformDialogNew;
		this.outerOutputList=transformMapping.getOutputFieldList();
		this.isExpression=isExpression;
	}

	
	public DragDropTransformOpImp(TransformDialog transformDialog, TransformMapping transformMapping,
			List<FilterProperties> inputFields, boolean b, boolean c, TableViewer operationalInputFieldTableViewer,
			AbstractExpressionComposite expressionComposite) {
		this(transformDialog,transformMapping,inputFields,b,c,operationalInputFieldTableViewer);
		this.expressionComposite=expressionComposite;
	}

	@Override
	public void saveResult(String result) {
		if(isSingleColumn && isExpression)
		{
			FilterProperties inputField = new FilterProperties();
	        inputField.setPropertyname(result);
	        if(!listOfInputFields.contains(inputField))
        	{	
        		listOfInputFields.add(inputField);
        		operationInputfieldtableviewer.refresh();
        	}	
	        if(expressionComposite!=null && StringUtils.isNotBlank(expressionComposite.getExressionTextBox().getText())){
	        	ExpressionEditorData expressionEditorData=expressionComposite.createExpressionEditorData();
	        	ExpressionEditorUtil.validateExpression(expressionEditorData.getExpression(),
						expressionEditorData.getExtraFieldDatatypeMap(), expressionEditorData);
	        	transformDialogNew.showHideValidationMessage();
	        }
		}
		else 
		{	
			if(isSingleColumn){
			   FilterProperties inputField = new FilterProperties();
	           inputField.setPropertyname(result);
	           FilterProperties outputField = new FilterProperties();
	           outputField.setPropertyname(result);
	        	if(!listOfInputFields.contains(inputField))
	        	{	
	        		listOfInputFields.add(inputField);
	        		listOfOutputFields.add(outputField); 	
	        		outerOutputList.add(outputField);
	        		transformDialogNew.refreshOutputTable();
	        		}	
	        	
	             operationOutputFieldTableViewer.refresh();
	        	
	        }
	        else{
	        	NameValueProperty field = new NameValueProperty();
	        	field.setPropertyName(result);
	        	field.setPropertyValue(result);
	        	mapAndPassThroughField.add(field);
	        	field.getFilterProperty().setPropertyname(field.getPropertyValue());
	        		
	        		outerOutputList.add(field.getFilterProperty());
	        		transformDialogNew.refreshOutputTable();
	        
	        }
		 operationInputfieldtableviewer.refresh();
		 transformDialogNew.showHideValidationMessage();
		 transformDialogNew.getComponent().setLatestChangesInSchema(false);
		}
		}

	/**
	 * @return the outputFieldMap
	 */
	public Map<String, List<FilterProperties>> getOutputFieldMap() {
		return outputFieldMap;
	}
}

