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
package hydrograph.ui.propertywindow.widgets.customwidgets.metastore;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.OSValidator;
import hydrograph.ui.common.util.PreferenceConstants;
import hydrograph.ui.communication.debugservice.DebugServiceClient;
import hydrograph.ui.datastructure.property.BasicSchemaGridRow;
import hydrograph.ui.datastructure.property.GridRow;
import hydrograph.ui.datastructure.property.Schema;
import hydrograph.ui.datastructures.metadata.MetaDataDetails;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.property.ComponentConfigrationProperty;
import hydrograph.ui.propertywindow.property.ComponentMiscellaneousProperties;
import hydrograph.ui.propertywindow.property.Property;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.customwidgets.AbstractWidget;
import hydrograph.ui.propertywindow.widgets.dialogs.HiveInputExtractMetaStoreDialog;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.AbstractELTWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultButton;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultLable;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.AbstractELTContainerWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.ELTDefaultSubgroupComposite;
import hydrograph.ui.propertywindow.widgets.utility.GridWidgetCommonBuilder;
/**
 * This class to extract the details of hive component from metastore.
 * @author Bitwise
 *
 */
public class ELTExtractMetaStoreDataWidget extends AbstractWidget {

	private static final String DBTYPE = "hive";
	private static final String ERROR = "ERR";
	private static final String INFO = "INF";
	private static final String HIVE_TEXT_FILE = "Hive Text File";
	private static final String PARQUET = "parquet";
	private static final String TEXTDELIMITED = "textdelimited";
	private static Logger logger = LogFactory.INSTANCE.getLogger(ELTExtractMetaStoreDataWidget.class);
	private String propertyName;
	private ArrayList<AbstractWidget> widgets;
	private String dbName;
	private String dbTableName;
	private String hiveType;
	private PropertyDialogButtonBar propDialogButtonBar;
	
	public ELTExtractMetaStoreDataWidget(ComponentConfigrationProperty componentConfigProp,
			ComponentMiscellaneousProperties componentMiscProps, PropertyDialogButtonBar propDialogButtonBar) {

		super(componentConfigProp, componentMiscProps, propDialogButtonBar);
		propertyName = componentConfigProp.getPropertyName();
		this.propDialogButtonBar=propDialogButtonBar;
	}

	@Override
	public void attachToPropertySubGroup(AbstractELTContainerWidget container) {
		ELTDefaultSubgroupComposite defaultSubgroupComposite = new ELTDefaultSubgroupComposite(
				container.getContainerControl());
		defaultSubgroupComposite.createContainerWidget();

		AbstractELTWidget defaultLable = new ELTDefaultLable(Messages.EXTRACT_FROM_METASTORE);
		defaultSubgroupComposite.attachWidget(defaultLable);
		setPropertyHelpWidget((Control) defaultLable.getSWTWidgetControl());
		
		
		AbstractELTWidget defaultButton;
		if(OSValidator.isMac()){
			defaultButton = new ELTDefaultButton(Messages.EXTRACT).buttonWidth(120);
		}else{
			defaultButton = new ELTDefaultButton(Messages.EXTRACT);
		}
		defaultSubgroupComposite.attachWidget(defaultButton);
		Button button = (Button) defaultButton.getSWTWidgetControl();
		
		
		button.addSelectionListener(attachExtractButtonListner());
	}

	/**
	 * @return SelectionAdapter
	 */
	private SelectionAdapter attachExtractButtonListner() {
		
		SelectionAdapter adapter = new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {

				String host = PlatformUI.getPreferenceStore().getString(PreferenceConstants.REMOTE_HOST);
				
				
				String port_no =PlatformUI.getPreferenceStore().getString(PreferenceConstants.REMOTE_PORT_NO);
				if(StringUtils.isBlank(port_no)){
					port_no = PreferenceConstants.DEFAULT_PORT_NO;
				}
				if(null!=host&& StringUtils.isNotBlank(host)){
					
					
					if(getDBTableDetailsFromWidgets()){
					
					HiveInputExtractMetaStoreDialog extractMetaStoreDialog = new HiveInputExtractMetaStoreDialog(Display.getCurrent().getActiveShell());
					
					extractMetaStoreDialog.open();
					
					List<String> userCredentials = extractMetaStoreDialog.getProperties();
					
					if(null!=userCredentials && userCredentials.size()>0){
					
						extractMetaStoreDetails(userCredentials,host,port_no);
	
					}
			}else{
				createMessageDialog(Messages.METASTORE_FORMAT_ERROR,ERROR).open();
			}

			}else{
				createMessageDialog(Messages.HOST_NAME_BLANK_ERROR,ERROR).open();
				}
			}
			 };
		
		return adapter;
	}
	
	
	private void extractMetaStoreDetails(List<String> userCredentials, String host, String port_no) {
		HiveTableSchema hiveTableSchema = null;
		String jsonResponse = "";
       
		
		try {

			ObjectMapper mapper = new ObjectMapper();
			jsonResponse = DebugServiceClient.INSTANCE.readMetaStoreDb(getMetaDataDetails(userCredentials, host, port_no),host,port_no);
			hiveTableSchema = mapper.readValue(jsonResponse,
					HiveTableSchema.class);

		} catch (NumberFormatException | HttpException | MalformedURLException exp) {
			logger.error("Json to object Mapping issue ", exp);
		} catch (IOException ex) {
			logger.error("Json to object Mapping issue ", ex.getMessage());
		}

		if (null != hiveTableSchema) {
			
				if(hiveType.equalsIgnoreCase(hiveTableSchema.getInputOutputFormat())){
	
						for (AbstractWidget abstractWgt : widgets) {
			
							if (abstractWgt.getProperty().getPropertyName()
									.equalsIgnoreCase(Constants.DELIMITER_QNAME)&& null != hiveTableSchema.getFieldDelimiter()) {
								
								abstractWgt.refresh(hiveTableSchema.getFieldDelimiter());
						
							}else if (abstractWgt.getProperty().getPropertyName().equalsIgnoreCase(Constants.SCHEMA_PROPERTY_NAME)) {
								
								abstractWgt.refresh(getComponentSchema(hiveTableSchema));
								
							}else if (abstractWgt.getProperty().getPropertyName().equalsIgnoreCase(Constants.PARTITION_KEYS_WIDGET_NAME)
									&& null != hiveTableSchema.getPartitionKeys()) {
			
								List<String> keys = new ArrayList<>(Arrays.asList(hiveTableSchema.getPartitionKeys().split(",")));
								
								List<Object> temp = new ArrayList<>();
								temp.add(keys);
								temp.add(getComponentSchema(hiveTableSchema));
								
								abstractWgt.refresh(temp);
								
							} else if (abstractWgt.getProperty().getPropertyName().equalsIgnoreCase(Constants.EXTERNAL_TABLE_PATH_WIDGET_NAME)
									&& null != hiveTableSchema.getExternalTableLocation()) {
								
								abstractWgt.refresh(hiveTableSchema.getExternalTableLocation());
							}
							
							
						}
			
					createMessageDialog(Messages.METASTORE_IMPORT_SUCCESS,INFO).open();
					propDialogButtonBar.enableApplyButton(true);
				
				}else{
					createMessageDialog(Messages.INVALID_DB_ERROR,ERROR).open();
				 }
			} else {
				if(StringUtils.isNotBlank(jsonResponse)){
					createMessageDialog(jsonResponse,ERROR).open();
				}else{
					createMessageDialog("Invalid Host Name:" +host,ERROR).open();
				}
		}
	}

	private MetaDataDetails getMetaDataDetails(List<String> userCredentials, String host, String port_no) {
		MetaDataDetails connectionDetails = new MetaDataDetails();
        connectionDetails.setDbType(DBTYPE);
        connectionDetails.setHost(host);
        connectionDetails.setPort(port_no);
        connectionDetails.setUserId(userCredentials.get(0));
        connectionDetails.setPassword(userCredentials.get(1));
        connectionDetails.setDatabase(dbName);
        connectionDetails.setTableName(dbTableName);
		return connectionDetails;
	}

	/**
	 * @param hiveTableSchema
	 * @return
	 */
	private Schema getComponentSchema(HiveTableSchema hiveTableSchema) {
		Schema schema = new Schema();
		List<GridRow> rows = new ArrayList<>();

		for (HiveTableSchemaField hsf : hiveTableSchema
				.getSchemaFields()) {
			BasicSchemaGridRow gridRow = new BasicSchemaGridRow();

			gridRow.setFieldName(hsf.getFieldName());
			gridRow.setDataTypeValue(hsf.getFieldType());
			gridRow.setPrecision(hsf.getPrecision());
			gridRow.setScale(hsf.getScale());
			gridRow.setDateFormat(hsf.getFormat());
			gridRow.setDataType(GridWidgetCommonBuilder
					.getDataTypeByValue(hsf.getFieldType()));

			rows.add(gridRow);
		}

		schema.setGridRow(rows);
		return schema;
	}

	/**
	 * 
	 */
	private boolean getDBTableDetailsFromWidgets() {
		
		for (AbstractWidget textAbtractWgt : widgets) {

			if (textAbtractWgt.getProperty().getPropertyName()
					.equalsIgnoreCase(Constants.DATABASE_WIDGET_NAME)) {
				dbName = (String) textAbtractWgt.getProperties().get(Constants.DATABASE_WIDGET_NAME);
			}else if (textAbtractWgt.getProperty().getPropertyName().equalsIgnoreCase(Constants.DBTABLE_WIDGET_NAME)) {
				dbTableName = (String) textAbtractWgt.getProperties().get(Constants.DBTABLE_WIDGET_NAME);
			}

			if(HIVE_TEXT_FILE.equalsIgnoreCase(getComponent().getType())){
				hiveType=TEXTDELIMITED;
			}else{
				hiveType=PARQUET;
			}
		}
		
		if(StringUtils.isNotEmpty(dbName)&&StringUtils.isNotEmpty(dbTableName)){
		
		  return true;
		}
		
		return false;
	}
   /**
    * 
    */
	@Override
	public LinkedHashMap<String, Object> getProperties() {
		
		LinkedHashMap<String, Object> property = new LinkedHashMap<>();
		property.put(propertyName, "");
		return property;
	}

	/**
	 * 
	 */
	@Override
	public boolean isWidgetValid() {
		return true;
	}

	@Override
	public void addModifyListener(Property property,
			ArrayList<AbstractWidget> widgetList) {
		
		widgets=widgetList;

	}
	/**
	 * 
	 * @param errorMessage
	 * @return
	 */
	public MessageBox createMessageDialog(String errorMessage,String messageType) {
		
		MessageBox messageBox=null;
		if("INF".equalsIgnoreCase(messageType)){
			 messageBox = new MessageBox(new Shell(), SWT.ICON_INFORMATION | SWT.OK);
			 messageBox.setText("Information");

		}else{
			 messageBox = new MessageBox(new Shell(), SWT.ERROR | SWT.OK);	
			 messageBox.setText("Error");
		}
		
		messageBox.setMessage(errorMessage);
		return messageBox;
	}

}
