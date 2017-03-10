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

package hydrograph.ui.graph.debug.service;

import hydrograph.ui.graph.Messages;
import hydrograph.ui.logging.factory.LogFactory;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

 

/**
 * @author Bitwise
 *
 */
public class DebugDataWizard extends Dialog {
	 
	private static final Logger logger = LogFactory.INSTANCE.getLogger(DebugDataWizard.class);
	private Table table;
	private boolean isLocalMode; 
	private JSONArray jsonArray;
	private List<String> list;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public DebugDataWizard(Shell parentShell, JSONArray jsonArray, boolean isLocalMode) {
		super(parentShell);
		this.isLocalMode = isLocalMode;
		this.jsonArray = jsonArray;
	}

	public DebugDataWizard(Shell parentShell, List<String> list, boolean isLocalMode) {
		super(parentShell);
		//setShellStyle(getShellStyle() | SWT.MIN | SWT.MAX | SWT.RESIZE);
		this.isLocalMode = isLocalMode;
		this.list = list;
	}
	
	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent){
		Composite container = (Composite) super.createDialogArea(parent);
		container.getShell().setText(Messages.DEBUG_WIZARD_TEXT);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		
		Composite composite = new Composite(container, SWT.BORDER);
		composite.setLayout(new GridLayout(1, false));
		GridData gd_composite = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gd_composite.heightHint = 348;
		gd_composite.widthHint = 715;
		composite.setLayoutData(gd_composite);
		
		ScrolledComposite scrolledComposite = new ScrolledComposite(composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		GridData gd_scrolledComposite = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_scrolledComposite.heightHint = 318;
		gd_scrolledComposite.widthHint = 681;
		scrolledComposite.setLayoutData(gd_scrolledComposite);
		
		table = new Table(scrolledComposite, SWT.BORDER|SWT.FULL_SELECTION|SWT.MULTI);
		scrolledComposite.setContent(table);
		scrolledComposite.setMinSize(table.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		 
			try {
				if(isLocalMode){
					localMode(list);
					logger.info("records fetched.");
				}else{
					remoteMode(jsonArray);
					logger.info("records fetched.");
				}
			} catch (JSONException exception) {
				logger.error(exception.getMessage(), exception);
			}
		  
			container.getShell().setMaximized(true);
			setDialogLocation();
	 
		return container;
	}
 
	private void createTableColumns(Table table, String[] fields, int width) {
		for (String field : fields) {
			TableColumn tableColumn = new TableColumn(table, SWT.LEFT|SWT.BOLD);
			tableColumn.setText(field);
			tableColumn.setWidth(width);
		}
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

	}
	
	public void localMode(List<String> debugDataList) throws JSONException{
		logger.debug("local mode");
		String obj = debugDataList.get(0).toString();
		JSONObject jsonObject = new JSONObject(debugDataList.get(0).toString());
		String[] columnValue = new String[jsonObject.length()];
		String[] objectValue = new String[jsonObject.length()];
		for(int i=0; i<jsonObject.length();i++){
			JSONObject jsonOb = new JSONObject(obj);
			 String data = (String) jsonOb.names().get(i);
			 columnValue[i] = data;
		}
		createTableColumns(table, columnValue, 690/jsonObject.length());
	 
		for(int i = 0; i < debugDataList.size(); i++){
			TableItem tableItem = new TableItem(table, SWT.LEFT);
			for(int j=0;j<jsonObject.length();j++){
				JSONObject jsonObj = new JSONObject(debugDataList.get(i).toString());
				String data = jsonObj.names().getString(j);
				objectValue[j] = jsonObj.getString(data);
			}	
			tableItem.setText(objectValue);
		}
	}
	
	public void remoteMode(JSONArray jsonArray) throws JSONException{
		logger.debug("remote mode");
		JSONObject jsonObj = jsonArray.getJSONObject(0);
		String[] columnValue = new String[jsonObj.length()];
		for(int j=0; j<jsonObj.length(); j++){
			jsonObj = jsonArray.getJSONObject(0);
			String data = jsonObj.names().getString(j);
			columnValue[j] = data;
		}
		createTableColumns(table, columnValue, 690/jsonObj.length());
		String[] objectValue = new String[jsonObj.length()];
		for(int i = 0; i < jsonArray.length(); i++){
			TableItem tableItem = new TableItem(table, SWT.None);
			for(int j=0;j<jsonObj.length();j++){
				jsonObj = jsonArray.getJSONObject(i);
				String data = jsonObj.names().getString(j);
				objectValue[j] = jsonObj.getString(data);
			}	
				tableItem.setText(objectValue);
			}
	}
	
	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(733, 443);
	}
	
	private void setDialogLocation()
	{
		Rectangle monitorArea = getShell().getDisplay().getPrimaryMonitor().getBounds();
		Rectangle shellArea = getShell().getBounds();
		int x = monitorArea.x + (monitorArea.width - shellArea.width)/2;
		int y = monitorArea.y + (monitorArea.height - shellArea.height)/2;
		getShell().setLocation(x,y);
	}
	
}
