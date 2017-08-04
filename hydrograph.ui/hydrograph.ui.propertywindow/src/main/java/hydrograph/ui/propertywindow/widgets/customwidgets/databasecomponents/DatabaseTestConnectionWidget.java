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
package hydrograph.ui.propertywindow.widgets.customwidgets.databasecomponents;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.slf4j.Logger;

import hydrograph.ui.common.datastructures.property.database.DatabaseParameterType;
import hydrograph.ui.common.property.util.Utils;
import hydrograph.ui.common.util.Constants;
import hydrograph.ui.common.util.OSValidator;
import hydrograph.ui.common.util.ParameterUtil;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.property.ComponentConfigrationProperty;
import hydrograph.ui.propertywindow.property.ComponentMiscellaneousProperties;
import hydrograph.ui.propertywindow.property.Property;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.widgets.customwidgets.AbstractWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultButton;
import hydrograph.ui.propertywindow.widgets.gridwidgets.basic.ELTDefaultLable;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.AbstractELTContainerWidget;
import hydrograph.ui.propertywindow.widgets.gridwidgets.container.ELTDefaultSubgroupComposite;
import hydrograph.ui.propertywindow.widgets.utility.WidgetUtility;

/***
 * The class to test the connection for different DB components
 * @author Bitwise
 *
 */
public class DatabaseTestConnectionWidget extends AbstractWidget{
	
	private static final Logger logger = LogFactory.INSTANCE.getLogger(DatabaseTestConnectionWidget.class);
	private Map<String, String> initialMap;
	private String propertyName;
	protected ControlDecoration buttonDecorator;
	private Button testConnectionButton;
	private ArrayList<AbstractWidget> widgets;
	private static final String ORACLE = "oracle";
	private static final String REDSHIFT = "redshift";
	private static final String MYSQL = "mysql";
	private static final String TERADATA = "teradata";
	private static final String TEST_CONNECTION="\"Connection";
	
	public DatabaseTestConnectionWidget(
			ComponentConfigrationProperty componentConfigProp,
			ComponentMiscellaneousProperties componentMiscProps,
			PropertyDialogButtonBar propDialogButtonBar) {
		super(componentConfigProp, componentMiscProps, propDialogButtonBar);

		this.propertyName = componentConfigProp.getPropertyName();
		if(initialMap==null){
			this.initialMap = new LinkedHashMap<String, String>();
		}
		
	}
	
	@Override
	public void attachToPropertySubGroup(AbstractELTContainerWidget subGroup) {
		logger.debug("Starting {} button creation");
		ELTDefaultSubgroupComposite testConnectionComposite = new ELTDefaultSubgroupComposite(subGroup.getContainerControl());
		testConnectionComposite.createContainerWidget();
		

		ELTDefaultLable defaultLable1 = new ELTDefaultLable("");
		testConnectionComposite.attachWidget(defaultLable1);
		setPropertyHelpWidget((Control) defaultLable1.getSWTWidgetControl());
		
		ELTDefaultButton eltDefaultButton = new ELTDefaultButton(Messages.TEST_CONNECTION);

		testConnectionComposite.attachWidget(eltDefaultButton);
		 testConnectionButton=(Button)eltDefaultButton.getSWTWidgetControl();
		testConnectionButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,false,false,0,0));

		buttonDecorator = WidgetUtility.addDecorator(
				(Control) eltDefaultButton.getSWTWidgetControl(),
				Messages.bind(Messages.EmptyValueNotification,Messages.TEST_CONNECTION));
		if (OSValidator.isMac()) {
			buttonDecorator.setMarginWidth(-2);
		}
		else{
			buttonDecorator.setMarginWidth(3);
		}
		
		attachButtonListner(testConnectionButton);
		setDecoratorsVisibility();
		Utils.INSTANCE.loadProperties();
	}
	
	private String getComponentType(){
		if(StringUtils.equalsIgnoreCase(getComponent().getType(), ORACLE)){
			return ORACLE;
		}else if(StringUtils.equalsIgnoreCase(getComponent().getType(), REDSHIFT)){
			return REDSHIFT;
		}else if(StringUtils.equalsIgnoreCase(getComponent().getType(), MYSQL)){
			return MYSQL;
		}else if(StringUtils.equalsIgnoreCase(getComponent().getType(), TERADATA)){
			return TERADATA;
		}
		return "";
	}
	
	

	/**
	 * Attaches selection listener on TestConnection button
	 * @param testConnectionButton
	 */
	private void attachButtonListner(Button testConnectionButton) {
		
		testConnectionButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				String	connectionResponse="";
				String host = DataBaseUtility.getInstance().getServiceHost();
				
				if(null!=host&& StringUtils.isNotBlank(host)){
					
					DatabaseParameterType parameterType =  getDatabaseConnectionDetails();
					validateDatabaseFields(parameterType);
				
					connectionResponse=	DataBaseUtility.getInstance().testDBConnection(parameterType,host);
				if(null != connectionResponse){
					if(connectionResponse.startsWith(TEST_CONNECTION)){
						String response=connectionResponse.replaceAll("\"", "");
						WidgetUtility.createMessageBox(response,Messages.INFORMATION , SWT.ICON_INFORMATION);
					}else{
						if(StringUtils.isNotBlank(connectionResponse)){
							WidgetUtility.createMessageBox(connectionResponse,Messages.ERROR , SWT.ICON_ERROR);
						}else{
							WidgetUtility.createMessageBox(Messages.INVALID_HOST_NAME+" : "+host,Messages.ERROR , SWT.ICON_ERROR);
						}
					}
				}
				}else{
					WidgetUtility.createMessageBox(Messages.HOST_NAME_BLANK_ERROR,Messages.ERROR , SWT.ICON_ERROR);
				}
			}
		});
		
	}
	
	
	private void validateDatabaseFields(DatabaseParameterType parameterType){
		
		if(parameterType.getDataBaseType().equalsIgnoreCase(ORACLE)){
			if ( StringUtils.isEmpty(parameterType.getHostName())|| StringUtils.isEmpty(parameterType.getJdbcName())
				|| StringUtils.isEmpty(parameterType.getPortNo())|| StringUtils.isEmpty(parameterType.getUserName())
				|| StringUtils.isEmpty(parameterType.getSid())|| StringUtils.isEmpty(parameterType.getPassword())) {
			}
		}else{
			if (StringUtils.isEmpty(parameterType.getDatabaseName()) || StringUtils.isEmpty(parameterType.getHostName())
					|| StringUtils.isEmpty(parameterType.getJdbcName()) || StringUtils.isEmpty(parameterType.getPortNo())
					|| StringUtils.isEmpty(parameterType.getUserName()) || StringUtils.isEmpty(parameterType.getPassword())) {
			}
		}
	}
	
	/**
	 * Provides the value for all the DB details
	 * @return 
	 */
	private DatabaseParameterType getDatabaseConnectionDetails() {
		String databaseName = "";
		String hostName = "";
		String portNo = "";
		String jdbcName = "";
		String schemaName = "";
		String userName = "";
		String password = "";
		String sid ="";
		
		for (AbstractWidget textAbtractWgt : widgets) {
			if (textAbtractWgt.getProperty().getPropertyName()
					.equalsIgnoreCase(Constants.DATABASE_WIDGET_NAME)) {
				databaseName = getValue((String) textAbtractWgt.getProperties().get(Constants.DATABASE_WIDGET_NAME));
			} else if (textAbtractWgt.getProperty().getPropertyName()
					.equalsIgnoreCase(Constants.HOST_WIDGET_NAME)) {
				hostName = getValue((String)textAbtractWgt.getProperties().get(Constants.HOST_WIDGET_NAME));
			} else if (textAbtractWgt.getProperty().getPropertyName()
					.equalsIgnoreCase(Constants.PORT_WIDGET_NAME)) {
				portNo = getValue((String) textAbtractWgt.getProperties().get(Constants.PORT_WIDGET_NAME));
			} else if (textAbtractWgt.getProperty().getPropertyName()
					.equalsIgnoreCase(Constants.JDBC_DRIVER_WIDGET_NAME)) {
				jdbcName = getValue((String) textAbtractWgt.getProperties().get(Constants.JDBC_DRIVER_WIDGET_NAME));
			} else if (textAbtractWgt.getProperty().getPropertyName()
					.equalsIgnoreCase(Constants.SCHEMA_WIDGET_NAME)) {
				schemaName = getValue((String) textAbtractWgt.getProperties().get(Constants.SCHEMA_WIDGET_NAME));
			} else if (textAbtractWgt.getProperty().getPropertyName()
					.equalsIgnoreCase(Constants.USER_NAME_WIDGET_NAME)) {
				userName = getValue((String) textAbtractWgt.getProperties().get(Constants.USER_NAME_WIDGET_NAME));
			} else if (textAbtractWgt.getProperty().getPropertyName()
					.equalsIgnoreCase(Constants.PASSWORD_WIDGET_NAME)) {
				password = getValue((String) textAbtractWgt.getProperties().get(Constants.PASSWORD_WIDGET_NAME));
			}else if (textAbtractWgt.getProperty().getPropertyName()
					.equalsIgnoreCase(Constants.ORACLE_SID_WIDGET_NAME)) {
				sid = getValue((String) textAbtractWgt.getProperties().get(Constants.ORACLE_SID_WIDGET_NAME));
			}
		}
		
		DatabaseParameterType parameterType = new DatabaseParameterType.DatabaseBuilder(getComponentType(), hostName, 
				portNo, userName, password).jdbcName(jdbcName).schemaName(schemaName)
				.databaseName(databaseName).sid(sid).build();


		return parameterType;
	}

	private String getValue(String input) {
		if(ParameterUtil.isParameter(input)){
			return Utils.INSTANCE.getParamValue(input);
		}
		return input;
	}
	
	@Override
	public LinkedHashMap<String, Object> getProperties() {
		LinkedHashMap<String, Object> property = new LinkedHashMap<>();
		property.put(propertyName, this.initialMap);

		setToolTipErrorMessage();
		return property;
	}
	

	@Override
	public boolean isWidgetValid() {
		return validateAgainstValidationRule(initialMap);
	}

	@Override
	public void addModifyListener(Property property, ArrayList<AbstractWidget> widgetList) {
			widgets = widgetList;
		}
		
	/**
	 * Sets the tool tip error message
	 */
	protected void setToolTipErrorMessage() {
		String toolTipErrorMessage = null;

		if (buttonDecorator.isVisible())
			toolTipErrorMessage = buttonDecorator.getDescriptionText();

		setToolTipMessage(toolTipErrorMessage);
	}
	
	/**
	 * Show or hides the decorator
	 */
	protected void setDecoratorsVisibility() {

		if (!isWidgetValid()) {
			buttonDecorator.show();
		} else {
          buttonDecorator.hide();
		}

	}
	

}
