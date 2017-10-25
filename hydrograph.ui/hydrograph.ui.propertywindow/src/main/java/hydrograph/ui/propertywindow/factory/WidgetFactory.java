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

 
package hydrograph.ui.propertywindow.factory;

import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;

import hydrograph.ui.common.util.Constants;
import hydrograph.ui.logging.factory.LogFactory;
import hydrograph.ui.propertywindow.filemixedschema.ELTMixedSchemeWidget;
import hydrograph.ui.propertywindow.filter.FilterLogicWidget;
import hydrograph.ui.propertywindow.fixedwidthschema.ELTFixedWidget;
import hydrograph.ui.propertywindow.fixedwidthschema.TransformSchemaWidget;
import hydrograph.ui.propertywindow.ftp.AuthenticationWidget;
import hydrograph.ui.propertywindow.ftp.FTPProtocolWidget;
import hydrograph.ui.propertywindow.ftp.OperationConfigWidget;
import hydrograph.ui.propertywindow.generaterecords.schema.GenerateRecordsGridWidget;
import hydrograph.ui.propertywindow.messages.Messages;
import hydrograph.ui.propertywindow.property.ComponentConfigrationProperty;
import hydrograph.ui.propertywindow.property.ComponentMiscellaneousProperties;
import hydrograph.ui.propertywindow.propertydialog.PropertyDialogButtonBar;
import hydrograph.ui.propertywindow.runprogram.RunComponentWidget;
import hydrograph.ui.propertywindow.widgets.customwidget.inputXML.InputXMLGenerateSchemaWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.AbstractWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.DelimiterWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.DropDownWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.ELTBrowseWorkspaceWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.ELTComponentID;
import hydrograph.ui.propertywindow.widgets.customwidgets.ELTComponentNameWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.ELTComponentType;
import hydrograph.ui.propertywindow.widgets.customwidgets.ELTFilePathWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.ELTJoinMapWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.ELTJoinWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.ELTLookupConfigWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.ELTLookupMapWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.ELTOperationClassWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.ELTRetentionLogicWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.ELTXmlPropertiesContainer;
import hydrograph.ui.propertywindow.widgets.customwidgets.ExcelFileNameWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.ExportXSDWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.HiveInputSingleColumnWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.HiveOutputSingleColumnWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.JDBCDriverClassWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.OutputRecordCountWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.PortWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.PropogateWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.RadioButtonsWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.RunSQLQueryWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.SingleColumnWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.TextBoxWithIsParameterCheckBoxWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.TextBoxWithIsParameterCheckBoxWidgetForDatabaseComponents;
import hydrograph.ui.propertywindow.widgets.customwidgets.TextBoxWithLabelWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.TextBoxWithLabelWidgetWithoutAnyValidation;
import hydrograph.ui.propertywindow.widgets.customwidgets.UpdateByKeysWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.WidgetHelper;
import hydrograph.ui.propertywindow.widgets.customwidgets.WorksheetWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.config.WidgetConfig;
import hydrograph.ui.propertywindow.widgets.customwidgets.databasecomponents.DatabaseTestConnectionWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.databasecomponents.InputAdditionalParametersWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.databasecomponents.LoadTypeConfigurationWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.databasecomponents.OutputAdditionalParametersWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.databasecomponents.SelectionDatabaseWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.excelcomponent.ExcelFormattingWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.joinproperty.ELTJoinPortCount;
import hydrograph.ui.propertywindow.widgets.customwidgets.metastore.ELTExtractMetaStoreDataWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.operational.TransformWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.runtimeproperty.ELTRuntimePropertiesWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.schema.ELTGenericSchemaGridWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.schema.XPathSchemaGridWidget;
import hydrograph.ui.propertywindow.widgets.customwidgets.secondarykeys.SecondaryColumnKeysWidget;


/**
 * Factory for creating Widgets
 * @author Bitwise
 * Sep 08, 2015
 * 
 */

public class WidgetFactory {
	public static final WidgetFactory INSTANCE = new WidgetFactory();
	private static final Logger logger = LogFactory.INSTANCE.getLogger(WidgetFactory.class);
	
	public enum Widgets{
		SCHEMA_WIDGET(ELTGenericSchemaGridWidget.class,WidgetHelper.INSTANCE.getSchemaConfig()),
		XPATH_SCHEMA_WIDGET(XPathSchemaGridWidget.class,WidgetHelper.INSTANCE.getSchemaConfig()),
		STRAIGHT_SCHEMA_WIDGET(ELTGenericSchemaGridWidget.class, WidgetHelper.INSTANCE.getStraightPullSchemaConfig()),
		FIXED_WIDGET(ELTFixedWidget.class,WidgetHelper.INSTANCE.getSchemaConfig()),
		MIXED_SCHEME(ELTMixedSchemeWidget.class,WidgetHelper.INSTANCE.getSchemaConfig()),		
		TRANSFORM_SCHEMA_WIDGET(TransformSchemaWidget.class,WidgetHelper.INSTANCE.getSchemaConfig()),
		GENERATE_RECORDS_SCHEMA_WIDGET(GenerateRecordsGridWidget.class,WidgetHelper.INSTANCE.getSchemaConfig()),
		FILE_PATH_WIDGET(ELTFilePathWidget.class,WidgetHelper.INSTANCE.getFilePathWidgetConfig(Messages.FILE_PATH_LABEL)),
		XML_FILE_PATH_WIDGET(ELTFilePathWidget.class,WidgetHelper.INSTANCE.getXMLFilePathWidgetConfig()),
		EXTERNAL_TABLE_PATH_WIDGET(ELTFilePathWidget.class,WidgetHelper.INSTANCE.getExternalTablePathWidgetConfig(Messages.EXTERNAL_TABLE_PATH_LABEL)),
		BROWSE_WORKSPACE_WIDGET(ELTBrowseWorkspaceWidget.class),
		COMPONENT_NAME_WIDGET(ELTComponentNameWidget.class),
		
		COMPONENT_ID_WIDGET(ELTComponentID.class),
		COMPONENT_TYPE_WIDGET(ELTComponentType.class),

		RETENTION_LOGIC_WIDGET(ELTRetentionLogicWidget.class),

		STRICT_CLASS_WIDGET(DropDownWidget.class, WidgetHelper.INSTANCE.getStrictWidgetConfig()),
		SAFE_PROPERTY_WIDGET(DropDownWidget.class, WidgetHelper.INSTANCE.getSafeWidgetConfig()),
		CHARACTER_SET_WIDGET(DropDownWidget.class, WidgetHelper.INSTANCE.getCharacterSetWidgetConfig()),
		HAS_HEADER_WIDGET(DropDownWidget.class, WidgetHelper.INSTANCE.getHasHeaderWidgetConfig()),
		OVERWRITE_WIDGET(DropDownWidget.class, WidgetHelper.INSTANCE.getOverWriteWidgetConfig()),
		WRITE_MODE_WIDGET(DropDownWidget.class, WidgetHelper.INSTANCE.getWriteModeWidgetConfig()),
		STRIP_LEADING_QUOTE_WIDGET(DropDownWidget.class, WidgetHelper.INSTANCE.getStripLeadingQuoteWidgetConfig()),
		AUTO_SIZE_COLUMN_WIDGET(DropDownWidget.class, WidgetHelper.INSTANCE.getAutoSizeColumnWidgetQuote()),
		ABORT_ON_ERROR_WIDGET(DropDownWidget.class, WidgetHelper.INSTANCE.getAbortOnErrorWidgetQuote()),
		//DATABASE_WIDGET(DropDownWidget.class, WidgetHelper.INSTANCE.getDatabaseWidgetConfig()),
		
		JDBC_DRIVER_WIDGET(TextBoxWithLabelWidget.class, WidgetHelper.INSTANCE.getJdbcDriverWidgetConfig()),
		BATCH_WIDGET(TextBoxWithLabelWidget.class, WidgetHelper.INSTANCE.getBatchWidgetConfig()),
		SID_NAME_WIDGET(TextBoxWithLabelWidget.class, WidgetHelper.INSTANCE.getSIDNameWidgetConfig()),
		DATABASE_NAME_WIDGET(TextBoxWithLabelWidget.class, WidgetHelper.INSTANCE.getDatabaseNameWidgetConfig()),
		HOST_NAME_WIDGET(TextBoxWithLabelWidget.class, WidgetHelper.INSTANCE.getHostNameWidgetConfig()),
		TEMPORARY_DIR_NAME_WIDGET(TextBoxWithLabelWidget.class, WidgetHelper.INSTANCE.getTemporaryWidgetConfig()),
		PORT_WIDGET(PortWidget.class, WidgetHelper.INSTANCE.getPortWidgetConfig()),
		TABLE_NAME_WIDGET(TextBoxWithLabelWidget.class, WidgetHelper.INSTANCE.getTableNameWidgetConfig()),
		USER_NAME_WIDGET(TextBoxWithLabelWidget.class, WidgetHelper.INSTANCE.getUserNameWidgetConfig()),
		NO_OF_RECORDS_WIDGET(TextBoxWithLabelWidget.class, WidgetHelper.INSTANCE.getNoOfRecordsWidgetConfig()),
		COUNT_WIDGET(TextBoxWithLabelWidget.class, WidgetHelper.INSTANCE.getCountWidgetConfig ()),
		XPATH_QUERY_WIDGET(TextBoxWithLabelWidget.class, WidgetHelper.INSTANCE.getXPathQueryWidgetConfig()),
		ROOT_TAG_WIDGET(TextBoxWithLabelWidget.class, WidgetHelper.INSTANCE.getRootTagWidgetConfig()),
	    ROW_TAG_WIDGET(TextBoxWithLabelWidget.class, WidgetHelper.INSTANCE.getRowTagWidgetConfig()),
	    JDBC_DRIVER_CLASS_WIDGET(JDBCDriverClassWidget.class, WidgetHelper.INSTANCE.getJDBCDriverClassWidgetConfig()),
	    DB_URL_WIDGET(TextBoxWithLabelWidget.class, WidgetHelper.INSTANCE.getDBUrlWidgetConfig()),
	    DB_UPDATE_USER_NAME_WIDGET(TextBoxWithLabelWidgetWithoutAnyValidation.class, WidgetHelper.INSTANCE.getTextBoxWithoutAnyValidationWidgetConfig(Messages.LABEL_USER_NAME)),
	    DB_UPDATE_PASSWORD_WIDGET(TextBoxWithIsParameterCheckBoxWidgetForDatabaseComponents.class, WidgetHelper.INSTANCE.getPasswordWidgetConfig(false)),
	    WORKSHEET_NAME_WIDGET(WorksheetWidget.class),
		TEXTBOX_WITH_IS_PARAMETER_CHECKBOX_WIDGET(TextBoxWithIsParameterCheckBoxWidget.class,WidgetHelper.INSTANCE.getSequenceFieldWidgetConfig()),
		DELIMETER_WIDGET(DelimiterWidget.class, WidgetHelper.INSTANCE.getDelimiterWidgetConfig()),
		QUOTE_WIDGET(TextBoxWithLabelWidgetWithoutAnyValidation.class, WidgetHelper.INSTANCE.getQuoteWidgetConfig()),
		ORACLE_SCHEMA_WIDGET(TextBoxWithLabelWidgetWithoutAnyValidation.class, WidgetHelper.INSTANCE.getOracleSchemaWidgetConfig()),
		PASSWORD_WIDGET(TextBoxWithIsParameterCheckBoxWidgetForDatabaseComponents.class, WidgetHelper.INSTANCE.getPasswordWidgetConfig(true)),
		CHUNK_SIZE_WIDGET(TextBoxWithLabelWidgetWithoutAnyValidation.class, WidgetHelper.INSTANCE.getTextBoxWithoutAnyValidationWidgetConfig(Messages.LABEL_CHUNK_SIZE)),
		FILTER_PROPERTY_WIDGET(SingleColumnWidget.class, WidgetHelper.INSTANCE.getOperationFieldsConfig()),
		COLUMN_NAME_WIDGET(SingleColumnWidget.class, WidgetHelper.INSTANCE.getColumnNameConfig()),
		PARTITION_KEYS_WIDGET(HiveOutputSingleColumnWidget.class, WidgetHelper.INSTANCE.getPartitionKeysConfig()),
		
		SELECT_BY_KEYS_WIDGET(UpdateByKeysWidget.class),
		TEST_CONNECTION_WIDGET(DatabaseTestConnectionWidget.class),
		PARTITION_KEYS_WIDGET_INPUT_HIVE(HiveInputSingleColumnWidget.class, WidgetHelper.INSTANCE.getPartitionKeysConfigInputHive()),
		
		OPERATIONAL_CLASS_WIDGET(ELTOperationClassWidget.class, WidgetHelper.INSTANCE.getOperationClassForFilterWidgetConfig()),
		PARTITION_BY_EXP_OPERATIONAL_CLASS_WIDGET(ELTOperationClassWidget.class, WidgetHelper.INSTANCE.getOperationClassForPartitionByExpressionWidgetConfig()),
		
		RUNTIME_PROPERTIES_WIDGET(ELTRuntimePropertiesWidget.class,WidgetHelper.INSTANCE.getRunTimeWidgetConfig(Constants.RUNTIME_PROPERTY_LABEL,Constants.RUNTIME_PROPERTIES_WINDOW_LABEL)),
		SUBJOB_PROPERTIES_WIDGET(ELTRuntimePropertiesWidget.class,WidgetHelper.INSTANCE.getRunTimeWidgetConfig(Constants.SUBJOB_PROPERTY_LABEL,Constants.SUBJOB_WINDOW_LABEL)),
		PRIMARY_COLUMN_KEYS_WIDGET(SecondaryColumnKeysWidget.class, WidgetHelper.INSTANCE.getPrimaryKeyWidgetConfig()),
		SECONDARY_COLUMN_KEYS_WIDGET(SecondaryColumnKeysWidget.class, WidgetHelper.INSTANCE.getSecondaryKeyWidgetConfig()),
		
		TRANSFORM_WIDGET(TransformWidget.class,WidgetHelper.INSTANCE.getOperationClassForTransformWidgetConfig(Constants.TRANSFORM,Constants.TRANSFORM_DISPLAYNAME, Constants.TRANSFORM_WINDOW_TITLE)),
		AGGREGATE_WIDGET(TransformWidget.class,WidgetHelper.INSTANCE.getOperationClassForTransformWidgetConfig(Constants.AGGREGATE,Constants.AGGREGATE_DISPLAYNAME, Constants.AGGREGATE_WINDOW_TITLE)),
		CUMULATE_WIDGET(TransformWidget.class,WidgetHelper.INSTANCE.getOperationClassForTransformWidgetConfig(Constants.CUMULATE, Constants.CUMULATE_DISPLAYNAME, Constants.CUMULATE_WINDOW_TITLE)),
		NORMALIZE_WIDGET(TransformWidget.class,WidgetHelper.INSTANCE.getOperationClassForTransformWidgetConfig(Constants.NORMALIZE,Constants.NORMALIZE_DISPLAYNAME, Constants.NORMALIZE_WINDOW_TITLE)),		
		GROUP_COMBINE_WIDGET(TransformWidget.class,WidgetHelper.INSTANCE.getOperationClassForTransformWidgetConfig(Constants.GROUP_COMBINE,Constants.GROUP_COMBINE_DISPLAYNAME,Constants.GROUP_COMBINE_WINDOW_TITLE)),
		XML_CONTENT_WIDGET(ELTXmlPropertiesContainer.class),
		
		JOIN_INPUT_COUNT_WIDGET(ELTJoinPortCount.class,WidgetHelper.INSTANCE.getInputCountWidgetConfig(Messages.LABEL_INPUT_COUNT,2)),
		INPUT_COUNT_WIDGET(ELTJoinPortCount.class,WidgetHelper.INSTANCE.getInputCountWidgetConfig(Messages.LABEL_INPUT_COUNT,1)),
		OUTPUT_COUNT_WIDGET(ELTJoinPortCount.class,WidgetHelper.INSTANCE.getInputCountWidgetConfig(Messages.LABEL_OUTPUT_COUNT,1)),
		PARTITION_COMPONENT_OUTPUT_COUNT_WIDGET(ELTJoinPortCount.class,WidgetHelper.INSTANCE.getInputCountWidgetConfig(Messages.LABEL_OUTPUT_PARTITIONS,2)),
		
		SELECT_JDBC_VALUES_WIDGET(SelectionDatabaseWidget.class, WidgetHelper.INSTANCE.getSelectWidgetConfig() ),
		JOIN_TYPE_WIDGET(ELTJoinWidget.class),
		JOIN_MAPPING_WIDGET(ELTJoinMapWidget.class),
		HASH_JOIN_WIDGET(ELTLookupConfigWidget.class),
		HASH_JOIN_MAPPING_WIDGET(ELTLookupMapWidget.class),
		
		MATCH_PROPERTY_WIDGET(RadioButtonsWidget.class, WidgetHelper.INSTANCE.getMatchValueWidgetConfig()),
		SELECT_INTERFACE_WITH_EXPORT_WIDGET(RadioButtonsWidget.class, WidgetHelper.INSTANCE.getSelectInterfaceWithExportWidgetConfig()),
		SELECT_INTERFACE_WITH_LOAD_WIDGET(RadioButtonsWidget.class, WidgetHelper.INSTANCE.getSelectInterfaceWithLoadWidgetConfig()),
		
		EXTRACT_METASTORE_DATA_WIDGET(ELTExtractMetaStoreDataWidget.class),
		LOAD_TYPE_CONFIGURATION_WIDGET(LoadTypeConfigurationWidget.class, WidgetHelper.INSTANCE.getRunTimeWidgetConfig(Constants.LOAD_TYPE_CONFIGURATION_LABEL,Messages.LOAD_TYPE_CONFIGURATION_WINDOW_LABEL)),
		INPUT_ADDITIONAL_PARAMETERS_WIDGET(InputAdditionalParametersWidget.class, WidgetHelper.INSTANCE.getRunTimeWidgetConfig(Messages.ADDITIONAL_PARAMETERS_FOR_DB_LABEL,Messages.ADDITIONAL_PARAMETERS_FOR_DB_WINDOW_LABEL)),
		OUTPUT_ADDITIONAL_PARAMETERS_WIDGET(OutputAdditionalParametersWidget.class, WidgetHelper.INSTANCE.getRunTimeWidgetConfig(Messages.ADDITIONAL_PARAMETERS_FOR_DB_LABEL,Messages.ADDITIONAL_PARAMETERS_FOR_DB_WINDOW_LABEL)),
		PROPOGATE_WIDGET(PropogateWidget.class),
		RUNPROGRAM_TEXT_WIDGET (RunComponentWidget.class),
		OUTPUT_RECORD_COUNT_WIDGET(OutputRecordCountWidget.class,WidgetHelper.INSTANCE.getOperationClassForTransformWidgetConfig(Constants.NORMALIZE,Constants.NORMALIZE_DISPLAYNAME, Constants.NORMALIZE_WINDOW_TITLE)),
		RUN_SQL_QUERY_WIDGET(RunSQLQueryWidget.class),
		DATABASE_LIST_WIDGET(DropDownWidget.class, WidgetHelper.INSTANCE.getDatabaseListWidgetConfig()),
		
		FTP_AUTHENTICATION_WIDGET(AuthenticationWidget.class, WidgetHelper.INSTANCE
				.getRunTimeWidgetConfig("Authentication", "Authentication Editor")),
		FTP_CONNECTION_TIME_OUT_WIDGET(PortWidget.class, WidgetHelper.INSTANCE
				.getNumbericValueWidgetConfig("Connection\nTime-Out\n(milllisecond)")),
		FTP_NO_OF_RETRIES_WIDGET(PortWidget.class, WidgetHelper.INSTANCE.getNumbericValueWidgetConfig("Number of \nRetries")),
		FTP_OPERATION_CONFIG_WIDGET(OperationConfigWidget.class, WidgetHelper.INSTANCE
				.getRunTimeWidgetConfig("Operation\nConfiguration", "Operation Config")),
		FTP_ENCODING_WIDGET(DropDownWidget.class, WidgetHelper.INSTANCE.getEncodingWidgetConfig()),
		FAIL_ON_ERROR_WIDGET(DropDownWidget.class, WidgetHelper.INSTANCE.getFailOnErrorWidgetConfig()),
		FTP_PROTOCOL_SELECTION_WIDGET(FTPProtocolWidget.class),
		EXCEL_FORMATTING_WIDGET(ExcelFormattingWidget.class,WidgetHelper.INSTANCE.getRunTimeWidgetConfig(Messages.LABEL_EXCEL_FORMATTING,Messages.EXCEL_FORMATTING_WINDOW_LABEL)),
		EXCEL_FILE_PATH_WIDGET(ELTFilePathWidget.class,WidgetHelper.INSTANCE.getExcelFilePathWidgetConfig(Messages.FILE_PATH_LABEL)),
		EXCEL_FILE_NAME_WIDGET(ExcelFileNameWidget.class,WidgetHelper.INSTANCE.getExcelFileNameWidgetConfig()),
		SORT_EXCEL_COLUMNS_KEYS_WIDGET(SecondaryColumnKeysWidget.class,WidgetHelper.INSTANCE.getSortExcelColumnWidgetConfig()),
		FILTER_LOGIC(FilterLogicWidget.class),
		EXPORT_XSD_WIDGET(ExportXSDWidget.class),
		XML_GENERATE_SCHEMA_WIDGET(InputXMLGenerateSchemaWidget.class);
              
		private Class<?> clazz = null;
		private WidgetConfig widgetConfig = null;
		
		private Widgets(Class<?> clazz) {
			this.clazz = clazz;
		}
		
		private Widgets(Class<?> clazz, WidgetConfig widgetConfig) {
			this.clazz = clazz;
			this.widgetConfig = widgetConfig;
		}
		
		public Class<?> getClazz(){
			return this.clazz;
		}
		
		public WidgetConfig getWidgetConfig() {
			return widgetConfig;
		}
	}

	public AbstractWidget getWidget(String widgetName, ComponentConfigrationProperty componentConfigProperty, 
			ComponentMiscellaneousProperties componentMiscProperties, PropertyDialogButtonBar propertyDialogButtonBar){
		try {
			Widgets widget = Widgets.valueOf(widgetName);
			AbstractWidget abstractWidget = (AbstractWidget) widget.getClazz().getDeclaredConstructor(ComponentConfigrationProperty.class,
					ComponentMiscellaneousProperties.class,	PropertyDialogButtonBar.class).
					newInstance(componentConfigProperty, componentMiscProperties, propertyDialogButtonBar);
			abstractWidget.setWidgetConfig(widget.getWidgetConfig());
			return abstractWidget;
		
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException |
				InvocationTargetException | NoSuchMethodException | SecurityException exception) {
			logger.error("Failed to create widget for class : {}, {}", widgetName, exception);
			throw new RuntimeException("Failed to instantiate the Listner {}" + widgetName);
		}
	}
}
