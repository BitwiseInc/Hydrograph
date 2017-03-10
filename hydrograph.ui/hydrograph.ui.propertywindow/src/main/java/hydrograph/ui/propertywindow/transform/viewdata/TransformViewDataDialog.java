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
package hydrograph.ui.propertywindow.transform.viewdata;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.CustomColorRegistry;
import hydrograph.ui.common.util.ImagePathConstant;
import hydrograph.ui.datastructure.property.NameValueProperty;
import hydrograph.ui.datastructure.property.mapping.MappingSheetRow;

/**
 * The Class TransformViewDataDialog displays the expressions or operations applied on Transform component.
 * 
 * @author Bitwise
 */

public class TransformViewDataDialog extends Dialog {

	
	private static final String COPY_BUTTON_TOOL_TIP = "Copy";
	private List<MappingSheetRow> mappingRowList;
	private List<NameValueProperty> mapAndPassthroughField;
	private StyledText styledText;
	List<StyleRange> styleRangeList=new ArrayList<>();
	
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public TransformViewDataDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.CLOSE| SWT.MIN|SWT.MAX|SWT.RESIZE| SWT.APPLICATION_MODAL);
		
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));
		
		Shell shell=container.getShell();
		shell.setText("View Transform");
		shell.setImage(ImagePathConstant.APP_ICON.getImageFromRegistry());
		
		CoolBar coolBar = new CoolBar(container, SWT.FLAT);
		coolBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		 CoolItem buttonItem = new CoolItem(coolBar, SWT.NONE | SWT.DROP_DOWN);
		  Composite buttonComposite = new Composite(coolBar, SWT.NONE);
		  buttonComposite.setLayout(new GridLayout(3, false));
		 
		  	createWrapButton(buttonComposite);

		    createCopyButton(buttonComposite);
		    
		    buttonComposite.pack();

		    Point size = buttonComposite.getSize();
		    buttonItem.setControl(buttonComposite);
		    buttonItem.setSize(buttonItem.computeSize(size.x, size.y));

		    createStyleTextEditor(container);
		
			getShell().setMinimumSize(290,290);
			return container;
	}
	


	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		
		Button cancelButton = createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CLOSE_LABEL, false);
		
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(666, 300);
	}
	
	/**
	 * Creates the style text editor to display transform information
	 * @param container
	 */
	private void createStyleTextEditor(Composite container)
	{
		Composite composite = new Composite(container, SWT.BORDER);
		composite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		styledText = new StyledText(composite, SWT.NONE|SWT.H_SCROLL|SWT.V_SCROLL);
		styledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		styledText.setEditable(false);
		
		
		StringBuffer stringOutput = new StringBuffer("");
		
		if(mappingRowList !=null){
		for(MappingSheetRow mappingSheetRow:mappingRowList){
			if(!mappingSheetRow.getOutputList().isEmpty()){
				if(mappingSheetRow.isExpression())
				{
					for(int i=0;i<mappingSheetRow.getOutputList().size();i++)
						
					{	
					    Color blue =CustomColorRegistry.INSTANCE.getColorFromRegistry( 92,172,238);
						int start5=stringOutput.length();
						stringOutput.append(TransformViewDataDialogConstants.START + mappingSheetRow.getOperationID() + TransformViewDataDialogConstants.BEGIN);
						int length5=stringOutput.length()-start5;
						applyStyleRange(start5, length5, blue, SWT.BOLD);
						stringOutput.append(Constants.OUTPUT_SOCKET_TYPE+"."+mappingSheetRow.getOutputList().get(i).getPropertyname());
						stringOutput.append(TransformViewDataDialogConstants.EQUALTO);
						stringOutput.append(mappingSheetRow.getExpressionEditorData().getExpression());
						stringOutput.append(TransformViewDataDialogConstants.TAB);
						
						for(int k=0;k<mappingSheetRow.getInputFields().size();k++){
							stringOutput.append(Constants.INPUT_SOCKET_TYPE+"."+mappingSheetRow.getInputFields().get(k).getPropertyname());
							if(k<mappingSheetRow.getInputFields().size()-1){
							stringOutput.append(TransformViewDataDialogConstants.COMMA);
							}
							
						}
						stringOutput.append(TransformViewDataDialogConstants.SEMICOLON);
						int start6=stringOutput.length();
						stringOutput.append(TransformViewDataDialogConstants.EXTRA_LINE+TransformViewDataDialogConstants.START + mappingSheetRow.getOperationID() +TransformViewDataDialogConstants.END);						
						int length6=stringOutput.length()-start6;
						applyStyleRange(start6, length6, blue, SWT.BOLD);
						
						stringOutput.append(TransformViewDataDialogConstants.EXTRA_LINE);
						stringOutput.append(TransformViewDataDialogConstants.EXTRA_LINE);
						
					}
					
				}
				else if(!mappingSheetRow.isExpression())
				{
					 for(int i=0;i<mappingSheetRow.getOutputList().size();i++){
						
						 Color green = CustomColorRegistry.INSTANCE.getColorFromRegistry( 0,199,140);
						 int start7=stringOutput.length();
						 stringOutput.append(TransformViewDataDialogConstants.START + mappingSheetRow.getOperationID() + TransformViewDataDialogConstants.BEGIN);
						 int length7=stringOutput.length()-start7;
						 applyStyleRange(start7, length7, green, SWT.BOLD);
						 stringOutput.append(Constants.OUTPUT_SOCKET_TYPE+"."+mappingSheetRow.getOutputList().get(i).getPropertyname());
						 stringOutput.append(TransformViewDataDialogConstants.EQUALTO);
						 stringOutput.append(mappingSheetRow.getOperationClassPath());
						 stringOutput.append(TransformViewDataDialogConstants.TAB);
						 
						 for(int j=0;j<mappingSheetRow.getInputFields().size();j++){
							 stringOutput.append(Constants.INPUT_SOCKET_TYPE+"."+mappingSheetRow.getInputFields().get(j).getPropertyname());
							 if(j<mappingSheetRow.getInputFields().size()-1){
								 stringOutput.append(TransformViewDataDialogConstants.COMMA);
							 }
							 
						 }
						 	stringOutput.append(TransformViewDataDialogConstants.SEMICOLON);
						     int start8=stringOutput.length();
						 	 stringOutput.append(TransformViewDataDialogConstants.EXTRA_LINE+TransformViewDataDialogConstants.START + mappingSheetRow.getOperationID() +TransformViewDataDialogConstants.END);
						 	 int length8=stringOutput.length()-start8;
						 	 applyStyleRange(start8, length8, green, SWT.BOLD);
							stringOutput.append(TransformViewDataDialogConstants.EXTRA_LINE);
							stringOutput.append(TransformViewDataDialogConstants.EXTRA_LINE);
						
					 }
				}
			}
				
			}
		}
		StringBuffer passThrough = new StringBuffer();
		StringBuffer mapThrough = new StringBuffer();
		
		if(mapAndPassthroughField !=null){
			for (NameValueProperty nameValueProperty : mapAndPassthroughField) {
				if(nameValueProperty.getPropertyName().trim().equals(nameValueProperty.getPropertyValue().trim())){
					passThrough.append(Constants.OUTPUT_SOCKET_TYPE+"."+nameValueProperty.getPropertyValue()+TransformViewDataDialogConstants.EQUALTO+"$ "+Constants.INPUT_SOCKET_TYPE+"."+nameValueProperty.getPropertyName()+TransformViewDataDialogConstants.SEMICOLON+TransformViewDataDialogConstants.EXTRA_LINE);
				}
				else if(!nameValueProperty.getPropertyName().trim().equals(nameValueProperty.getPropertyValue().trim())){
					mapThrough.append(Constants.OUTPUT_SOCKET_TYPE+"."+nameValueProperty.getPropertyValue()+TransformViewDataDialogConstants.EQUALTO+"$ "+Constants.INPUT_SOCKET_TYPE+"."+nameValueProperty.getPropertyName()+TransformViewDataDialogConstants.SEMICOLON+TransformViewDataDialogConstants.EXTRA_LINE);
				}
			}
		}
		
		if(mapThrough.toString().length()!=0){
		
			 Color brown =CustomColorRegistry.INSTANCE.getColorFromRegistry( 160,103,0);
			int start=stringOutput.length();
			stringOutput.append(TransformViewDataDialogConstants.START+TransformViewDataDialogConstants.MAPTHROUGHBEGINS);
			int length=stringOutput.length()-start;
			applyStyleRange(start,length,brown,SWT.BOLD);
			stringOutput.append(TransformViewDataDialogConstants.EXTRA_LINE);
			stringOutput.append(mapThrough);
			int start1=stringOutput.length();
			stringOutput.append(TransformViewDataDialogConstants.START +TransformViewDataDialogConstants.MAPTHROUGHENDS);
			int length1=stringOutput.length()-start1;
			applyStyleRange(start1,length1,brown,SWT.BOLD);
			stringOutput.append(TransformViewDataDialogConstants.EXTRA_LINE);
			stringOutput.append(TransformViewDataDialogConstants.EXTRA_LINE);		
		}
		
		if(passThrough.toString().length()!=0){
			
			 Color cyan = CustomColorRegistry.INSTANCE.getColorFromRegistry( 64,224,208);
			int start3=stringOutput.length();
			stringOutput.append(TransformViewDataDialogConstants.START+TransformViewDataDialogConstants.PASSTHROUGHBEGINS);
			int length3=stringOutput.length()-start3;
			applyStyleRange(start3, length3, cyan, SWT.BOLD);
			stringOutput.append(TransformViewDataDialogConstants.EXTRA_LINE);
			stringOutput.append(passThrough);
			int start4=stringOutput.length();
			stringOutput.append(TransformViewDataDialogConstants.START +TransformViewDataDialogConstants.PASSTHROUGHENDS);
			int length4=stringOutput.length()-start4;
			applyStyleRange(start4, length4, cyan, SWT.BOLD);
			stringOutput.append(TransformViewDataDialogConstants.EXTRA_LINE);
			stringOutput.append(TransformViewDataDialogConstants.EXTRA_LINE);
		}
		
		
		
		StringBuffer transformText = new StringBuffer();
		
		transformText.append(stringOutput.toString());
		styledText.setText(transformText.toString());
		styledText.setStyleRanges(styleRangeList.toArray(new StyleRange[styleRangeList.size()]));
	}
	
	/***
	 * Applies style range for the style text
	 * @param start
	 * @param length
	 * @param fgColor
	 * @param fontStyle
	 */
	private void applyStyleRange(int start,int length,Color fgColor,int fontStyle){
		StyleRange range =new StyleRange();
		range.start = start;
		range.length = length;
		range.foreground = fgColor;
		range.fontStyle = fontStyle;
		styleRangeList.add(range);
		
	}
	
	
	/**
	 * Creates the word wrap button 
	 * @param buttonComposite
	 */
	private void createWrapButton(Composite buttonComposite)
	{
		final Button btnCheckButton = new Button(buttonComposite, SWT.CHECK);
	    btnCheckButton.setText("Wrap Text");
	    btnCheckButton.pack();
	    
	    btnCheckButton.addSelectionListener(new SelectionAdapter() {
	    	@Override
			public void widgetSelected(SelectionEvent e) {
	    		styledText.setWordWrap(btnCheckButton.getSelection());
			}
	    	
		});
	}
	
	/**
	 * Creates the copy button 
	 * @param buttonComposite
	 */
	private void createCopyButton(Composite buttonComposite){
		Button copyButton = new Button(buttonComposite, SWT.PUSH);
	 
	    	copyButton.setImage(ImagePathConstant.ENABLE_COPY_ICON.getImageFromRegistry());
	    	copyButton.setToolTipText(COPY_BUTTON_TOOL_TIP);
	    	copyButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
				if (StringUtils.isNotBlank(styledText.getSelectionText())) {
					styledText.copy();
				} else {
					styledText.selectAll();
					styledText.copy();
					styledText.setSelection(0);
				}

			}
		});
			copyButton.pack();
	}
	
	/**
	 * Contains mapping list
	 * @param mappingRowList
	 */
	public void getMappingSheet(List<MappingSheetRow> mappingRowList,List<NameValueProperty> mapAndPassthroughField){
		this.mappingRowList =  mappingRowList;
		this.mapAndPassthroughField = mapAndPassthroughField;
	}
	
	
	
}
