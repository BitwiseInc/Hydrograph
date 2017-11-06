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

package hydrograph.ui.common.util;

/**
 * NOTE : Do not change/modify values for below constants(not even space) until you know 
 * where it is affecting the behavior 
 * @author Bitwise
 */
public class Constants {
	public static final String VALIDATOR_PACKAGE_PREFIX = "hydrograph.ui.validators.impl.";
	public static final String COMPONENT_PACKAGE_PREFIX = "hydrograph.ui.graph.model.components.";
	
	public static final String ERROR = "ERROR";
	public static final String BATCH = "Batch";
	public static final String PARAM_NO_OF_RECORDS = "recordCount";
	
	public static final String INPUT_PORT_COUNT_PROPERTY="inPortCount";
 	public static final String OUTPUT_PORT_COUNT_PROPERTY="outPortCount";
	
	public static final String PARAM_OPERATION = "operation";
	public static final String PARAM_NAME = "name";
	public static final String PARAM_BATCH = "batch";
	public static final String PARAM_DEPENDS_ON = "dependsOn";
	public static final String PARAM_PROPERTY_NAME = "propertyName";
	public static final String PARAM_COUNT = "count";
	public static final String UNION_ALL = "UnionallComponent";
	public static final String IS_UNION_ALL_COMPONENT_SYNC = "isUnionAllComponentSync";
	
	public static final String PARAMETER = "Parameter";
	public static final String ISO_8859_1 = "ISO-8859-1";
	public static final String US_ASCII = "US-ASCII";
	public static final String UTF_8 = "UTF-8";

	public static final String OTHERS= "Others";
	
	public static final String TRUE = "True";
	public static final String FALSE = "False";
	public static final String OPERATION_FIELDS_WINDOW_TITLE = "Operation Fields";
	public static final String KEY_FIELDS_WINDOW_TITLE = "Key Fields";
	
	public static final String PARTITION_KEYS_WINDOW_TITLE = "Partition Keys";
	
	public static final String COMPONENT_NAME = "Component Name";
	 // Used for validating AlphaNumeric or Parameter E.g Aplha_123 or @{Param_123}
	public static final String REGEX = "[\\@]{1}[\\{]{1}[\\w]*[\\}]{1}||[\\w]*";
	// Used for validating Numeric values (only 4 digits) E.g. 1234 and Parameters E.g  @{Param_123}
	public static final String REGEX_NUMERIC_AND_PARAMETER = "^([\\@]{1}[\\{]{1}[\\s\\S]+[\\}]{1})|([\\d])$";
	// Used for validating only Parameters E.g  @{Param_123}
	public static final String PARAMETER_REGEX ="^[\\@]{1}[\\{]{1}[\\s\\S]+[\\}]{1}";
	
	//Used for validating AlphaNumeric with , and = operator or Parameter E.g Aplha_123 or @{Param_123} or i = 10, j=20
	public static final String DB_REGEX = "[\\@]{1}[\\{]{1}[\\w]*[\\}]{1}||[\\w,\\=,\\s]*";

	// Used for validating AlphaNumeric or Parameter E.g Aplha_123
	public static final String REGEX_ALPHA_NUMERIC = "[\\w]*";
	public static final String MATCH_PROPERTY_WIDGET = "match_value";
	public static final String MATCH = "Match";
	public static final String FIRST = "First";
	public static final String LAST = "Last";
	public static final String ALL = "All";
	
	public static final String DEFAULT = "DEFAULT";
	public static final String FASTLOAD = "FASTLOAD";
	public static final String FASTEXPORT = "FASTEXPORT";
	
	public static final String OFIXED_WIDTH = "OFixedWidth";
	public static final String FILTER = "FILTER";
	public static final String NORMALIZE = "NORMALIZE";
	public static final String TRANSFORM = "TRANSFORM";
	public static final String AGGREGATE = "AGGREGATE";
	public static final String CUMULATE = "CUMULATE";
	public static final String GROUP_COMBINE = "Groupcombine";
	public static final String LOOKUP = "LOOKUP";
	public static final String JOIN = "JOIN";
	public static final String GROUP_COMBINE_DISPLAYNAME = "Group\nCombine";
	public static final String AGGREGATE_DISPLAYNAME = "Aggregate";
	public static final String TRANSFORM_DISPLAYNAME = "Transform";
	public static final String CUMULATE_DISPLAYNAME = "Cumulate";
	public static final String NORMALIZE_DISPLAYNAME = "Normalize";
	public static final String ADD_FIELDS_AS_PASSTHROUGH_FIELDS = "Add fields as Passthrough fields";
	public static final String AGGREGATE_WINDOW_TITLE = "Aggregate";
	public static final String TRANSFORM_WINDOW_TITLE = "Transform";
	public static final String CUMULATE_WINDOW_TITLE= "Cumulate";
	public static final String NORMALIZE_WINDOW_TITLE = "Normalize";
	public static final String GROUP_COMBINE_WINDOW_TITLE = "Group Combine";
	public static final String ISSUE_PROPERTY_NAME="Other Issues";
	public static final String PARTITION_BY_EXPRESSION = "PartitionByExpression";
	public static final String HASH = "#";
	public static final String INNER_PROPERTY_HELP_WIDGET = "innerPropertyHelpWidget_";
	
    public static final String SHOW_TOOLTIP="TRUE";
	public static final String HIDE_TOOLTIP="FALSE";
	public static final String TOOLTIP_DATATYPE="TEXT";
	 
	public static final String INPUT_SOCKET_TYPE = "in";
	public static final String OUTPUT_SOCKET_TYPE = "out";
	public static final String UNUSED_SOCKET_TYPE = "unused";
	public static final String UNKNOWN_COMPONENT_CATEGORY = "UNKNOWN";
	public static final String UNKNOWN_COMPONENT = "UnknownComponent";
	
	public static final String LOOKUP_CONFIG_FIELD = "hash_join";
	public static final String LOOKUP_MAP_FIELD = "hash_join_map";
	
	public static final String JOIN_CONFIG_FIELD = "join_config";
	public static final String JOIN_MAP_FIELD = "join_mapping";
	public static final String UNKNOWN_COMPONENT_TYPE = "UNKNOWN";
	public static final String JOIN_TYPE_ATTRIBUTE_NAME="joinType";
	
	public static final String COMPONENT_ORIGINAL_NAME = "componentOriginalName";
	public static final String COMPONENT_TYPE = "componentType";
	public static final String COMPONENT_BASE_TYPE = "componentBaseType";
	public static final String COMPONENT_NAMES = "componentNames";
	public static final String SCHEMA_TO_PROPAGATE="output_schema_map";

	public static final String ASCENDING_SORT_ORDER = "Asc";
	public static final String DESCENDING_SORT_ORDER="Desc";
	public static final String NONE_SORT_ORDER="From Param";
	
	public static final String SUBJOB_TOOLTIP_INFO = "Subjob Info";
	public static final String ALREADY_EXISTS = " already exists.";


	/*
	 * Sub Job required constants 
	 */
	public static final String SUBJOB_COMPONENT_CATEGORY = "SUBJOB";
	public static final String SUBJOB_COMPONENT = "SubjobComponent";
	public static final String PATH = "path";
	public static final String TYPE = "type";
	public static final String OPERATION = "operation";
	public static final String OUTPUT = "output";
	public static final String INPUT = "input";
	public static final String INPUT_SUBJOB = "InputSubjobComponent";
 	public static final String OUTPUT_SUBJOB = "OutputSubjobComponent";
	public static final String NAME = "name";
	public static final String SUBJOB_NAME = "subjob.job";
	
	public static final String LENGTH_QNAME = "length";
	public static final String DELIMITER_QNAME = "delimiter";	
	public static final String RANGE_FROM_QNAME = "rangeFrom";
	public static final String RANGE_TO_QNAME = "rangeTo";
	public static final String DEFAULT_VALUE_QNAME = "default";
	public static final String ABSOLUTE_OR_RELATIVE_XPATH_QNAME = "absoluteOrRelativeXPath";
	
	
	public static final String GENERATE_RECORDS_COMPONENT_TYPE = "Generate Records";
	public static final String FIXED_INSOCKET_ID = "in0";
	public static final String FIXED_OUTSOCKET_ID = "out0";
	public static final String SEQUENCE_FIELD = "Sequence Field";
	public static final String IS_PARAMETER = "Is Parameter";
	public static final String SCHEMA_DEFAULT_FIELD_NAME_SUFFIX="DefaultField";
	public static final String DEFAULT_INDEX_VALUE_FOR_COMBOBOX="0";
	public static final String PARAMETER_PREFIX = "@{";
	public static final String UNIQUE_SEQUENCE = "UniqueSequence";
	public static final String UNIQUE_SEQUENCE_TYPE = "Unique Sequence";
	public static final String PROPAGATE_FIELD_FROM_LEFT= "Propagate Field\nFrom Left";
	public static final String PROPAGATE_FIELD_FROM_LEFT_ACTION= "Propagate Field From Left";
	public static final String PROPAGATE= "Propagate";
	public static final String STRAIGHTPULL = "STRAIGHTPULL";
	public static final String FILTER_COMPONENT = "filter";
	public static final String UNIQUE_SEQUENCE_COMPONENT ="uniquesequence";
	
	public static final String PARAMETER_SUFFIX = "}";
	public static final String UNIQUE_SEQUENCE_PROPERTY_NAME = "sequence_field";
	public static final String ADD_ALL_FIELDS_SYMBOL = "*";
	public static final String INPUT_SUBJOB_COMPONENT_NAME = "InputSubjobComponent";
	

	public static final String EDIT = "Edit";
	public static final String RUNTIME_PROPERTIES_COLUMN_NAME = "Runtime\nProperties";
	public static final String RUNTIME_PROPERTIES_WINDOW_LABEL = "Runtime Properties";
	
	
	public static final String RUNTIME_PROPERTY_NAME = "runtime_properties";
	
	public static final String RUNTIME_PROPERTY_LABEL = "Runtime\nProperties";
	public static final String LOAD_TYPE_CONFIGURATION_LABEL = "Loadtype\nConfiguration";
	public static final String SUBJOB_PROPERTY_LABEL = "Subjob\nProperties";
	public static final String UNUSED_PORT_COUNT_PROPERTY = "unusedPortCount";
	public static final String UNUSED_AND_INPUT_PORT_COUNT_PROPERTY = "inPortCount|unusedPortCount";
	public static final String SUBJOB_ALREADY_PRESENT_IN_CANVAS = " - already present in canvas.";
	public static final String PATH_PROPERTY_NAME = "path";
	public static final String SUBJOB_CREATE = "Create";
	public static final String SUBJOB_OPEN = "Open"; 	
	public static final String SUBJOB_TRACKING = "Track Subjob"; 
	public static final String VIEW_TRACKING_OR_WATCH_POINT_DATA = "View Tracking / Watch Point Data";
	public static final String SUBJOB_ACTION = "SubJob";
	public static final String SUBJOB_ACTION_ToolTip = "Path operations";
	public static final String SUBJOB_CONTAINER = "Container";
	public static final String STANDALONE_SUBJOB = "StandAlone_Subjob";
	public static final String SUBJOB_WINDOW_LABEL = "Subjob Parameters";
	public static final String WINDOW_TITLE="WINDOW TITLE";
	public static final String JOB_PATH="path";
	public static final String JOB_EXTENSION=".job";
	public static final String XML_EXTENSION=".xml";
	public static final String JOB_EXTENSION_FOR_IPATH="job";
	public static final String XML_EXTENSION_FOR_IPATH="xml";
	public static final String SUBJOB_UPDATE = "Refresh";
	public static final String JOIN_KEYS_WINDOW_TITLE = "Join Key(s)";
	public static final String LOOKUP_KEYS_WINDOW_TITLE = "Lookup Key(s)";
	
	public static final String INNER = "Inner";
	public static final String OUTER = "Outer";
	public static final String INPUT_SOCKET_FOR_SUBJOB="Input Socket for subjob";
	public static final String OUTPUT_SOCKET_FOR_SUBJOB="Output Socket for subjob";
	public static final String PROPERTIES="properties";
	public static final String SUBJOB_INPUT_COMPONENT_NAME="InSubjob_01";
	public static final String SUBJOB_OUTPUT_COMPONENT_NAME="OutSubjob_01";
	public static final String SCHEMA_FOR_INPUTSUBJOBCOMPONENT="schema_for_Inputsubjobcomponent";
	
	/*
	 * Debug required constants 
	 */
	
	public static final String CURRENT_VIEW_DATA_ID = "currentJobViewDataId";
	public static final String PRIOR_VIEW_DATA_ID = "priorViewDataId";
	
	
	
	public static final String WATCHER_ACTION = "Watch Point";
	public static final String WATCHER_ACTION_TEXT = "Add watch";
	public static final String ADD_WATCH_POINT_ID = "watchPointId";
	public static final String WATCH_RECORD_ID = "watchRecordId";
	public static final String REMOVE_WATCH_POINT_ID = "removeWatchPointId";
	public static final String SOCKET_ID = "socketId";
	public static final String JOB_ID = "jobId";
	public static final String PORT_NO = ":8004";
	public static final String ROUTE_TO_READ_DATA = "/read";
	public static final String ROUTE_TO_REMOVE_FILES = "/delete";
	
	public static final String HTTP_PROTOCOL = "http://";
	public static final String DEBUG_EXTENSION = "_debug.xml";
	
	
	public static final String UPDATE_AVAILABLE = "update_available";
	public static final String SUBJOB_VERSION = "subjobVersion";
	public static final String VALIDITY_STATUS = "validityStatus";
	public static final String VALID = "VALID";

	public static final String HELP_ID="helpId";
	public static final String COMPONENT_PROPERTIES_ID="propertiesId";

	// Components Properties Keys
	public static final String PARAM_PRIMARY_COLUMN_KEYS = "Key_fields_sort";
	public static final String PARAM_SECONDARY_COLUMN_KEYS = "Secondary_keys";
	public static final String PROPERTY_COLUMN_NAME = "Key_fields";
	public static final String PROPERTY_SECONDARY_COLUMN_KEYS = "Secondary_keys";
	public static final String  WARN="WARN";
	

	public static final String GRAPH_PROPERTY = "Graph Property";
	public static final String GRAPH_PROPERTY_COMMAND_ID = "hydrograph.ui.propertywindow.graphProperties";
	public static final String SCHEMA_PROPERTY_NAME = "schema";
	
	public static final String COMMENT_BOX="Add";
	
	//Types of UI- Schema 
	public static final String SCHEMA = "schema";
	public static final String FIXEDWIDTH_GRID_ROW = "FixedWidth";
	public static final String MIXEDSCHEMA_GRID_ROW = "MixedScheme";		
	public static final String GENERATE_RECORD_GRID_ROW="GenerateRecord";
	public static final String GENERIC_GRID_ROW="Generic";
	public static final String PACKAGE = "package";
	
	
	public static final String EXTERNAL_SCHEMA = "External";
	public static final String INTERNAL_SCHEMA = "Internal";

	public static String SCHEMA_NOT_SYNC_MESSAGE="Fields in schema and mapping are not in sync.";
	public static String SYNC_WARNING="Sync Warning";
	public static String SYNC_CONFIRM="Confirm";
	public static String SYNC_CONFIRM_MESSAGE="Do you want to sync schema? It will override existing .";
	public static String SYNC_OUTPUT_FIELDS_CONFIRM_MESSAGE="Do you want to pull output fields from schema? It will over write existing Output fields.";
	public static String CLICK_TO_FOCUS="Click to focus";

	// Temporary property names of component 
	public static String SCHEMA_FIELD_SEQUENCE = "schema_field_sequence";
	public static String COPY_FROM_INPUT_PORT_PROPERTY = "Copy of ";
	//Hive input partition key
	public static String PARTITION_KEYS="PartitionKeys";

	public static String INPUT_SUBJOB_TYPE="Input Socket for subjob";
	public static String OUTPUT_SUBJOB_TYPE="Output Socket for subjob";

	public  static final String OUTPUT_RECORD_COUNT = "Output\nRecordCount";
	public static final String PROPERTY_TABLE = "PROPERTY_TABLE";
	public static final String PROPERTY_NAME = "PROPERTY_NAME";
	public static final String PROPERTY_VALUE = "PROPERTY_VALUE";
	public static final Character KEY_D = 'd';
	public static final Character KEY_N = 'n';

	public static final String DATABASE_WIDGET_NAME="databaseName";
	public static final String DBTABLE_WIDGET_NAME="tableName";
	public static final String PARTITION_KEYS_WIDGET_NAME="partitionKeys";
	public static final String EXTERNAL_TABLE_PATH_WIDGET_NAME="externalTablePath";
	public static final String HOST = "host";


	//Workbench - ToolBar ,CoolBar, MenuBar, MenuItems, ToolItems IDs
	
	// ID of tool-bar having stop and run tool-items which is defined in Graph plugin's plugin.xml 
	public static final String RUN_STOP_BUTTON_TOOLBAR_ID="hydrograph.ui.graph.toolbar2";
	public static final String RUN_BUITTON_TOOLITEM_ID="hydrograph.ui.graph.command.runjob";
	public static final String STOP_BUITTON_TOOLITEM_ID="hydrograph.ui.graph.stopjob";
	public static final String FIRST_TOOLBAR_ID = "hydrograph.ui.graph.toolbar1";
	public static final String REMOVE_WATCH_POINTS_TOOLITEM_ID = "hydrograph.ui.graph.command.removedebug";
	public static final String ACCUMULATOR_VARIABLE = "_accumulator";
	public static final String ACCUMULATOR_VARIABLE_1 = "_accumulator1";
	public static final String ACCUMULATOR_VARIABLE_2 = "_accumulator2";
	

	// Oracle Component
	public static final String ORACLE_SID_WIDGET_NAME="sid";
	public static final String CHUNKSIZE_WIDGET_NAME="chunkSize";
	public static final String HOST_WIDGET_NAME="hostName";
	public static final String PORT_WIDGET_NAME="port";
	public static final String JDBC_DRIVER_WIDGET_NAME="jdbcDriver";
	public static final String SCHEMA_WIDGET_NAME="oracleSchema";
	public static final String USER_NAME_WIDGET_NAME="userName";
	public static final String PASSWORD_WIDGET_NAME="password";
	public static final String LOAD_TYPE_UPDATE_KEY = "Update";
	public static final String LOAD_TYPE_NEW_TABLE_KEY = "New Table";
	public static final String LOAD_TYPE_INSERT_KEY = "Insert";
	public static final String LOAD_TYPE_REPLACE_KEY = "Replace";
	public static String LOAD_TYPE_UPDATE_VALUE_SEPERATOR=",";
	public static String LOAD_TYPE_NEW_TABLE_VALUE_SEPERATOR=",";
	public static final String EXTENSION=".*";
	public static final String RUN_COMMAND_PROPERTY_NAME = "runcommand";
	public static final String XML_CONTENT_PROPERTY_NAME = "xml_properties_content";

	
	//key fields name for expression or operation widget
	public static final String EXPRESSION_TEXT_BOX = "expressionTextBox";
	public static final String EXPRESSION_TEXT_BOX1 = "expression_text_1";
	public static final String PARAMETER_TEXT_BOX = "parameterTextBox";
	public static final String OUTPUT_FIELD_TEXT_BOX = "outputFieldTextBox";
	public static final String EXPRESSION_EDITOR_BUTTON = "expressionEditorButton";
	public static final String EXPRESSION_EDITOR_BUTTON1 = "expressionbutton";
	public static final String EXPRESSION_ID_TEXT_BOX = "expressionIdTextBox";
	public static final String DELETE_BUTTON = "deleteButton";
	public static final String ADD_BUTTON = "addButton";
	public static final String INPUT_FIELD_TABLE = "inputFieldTable";
	public static final String PREVIOUS_COMPONENT_OLD_SCHEMA = "previous_component_old_schema";
	public static final String _INDEX = "_index";
	public static final String SECURE_STORAGE_HYDROGRAPH_CREDENTIALS_ROOT_NODE = "Hydrograph";
	public static final String EXPLICIT_SCALE_TYPE_VALUE = "explicit";
	
	public static final String RUN_SQL_DATABASE_CONNECTION_NAME = "databaseConnectionName";
	public static final Object RUN_SQL_QUERY = "runsqlquery";
	public static final String PORT_WIDGET_ERROR = "Should be numeric or Paramerter e.g. 1234, @{Param}";
	public static final String JAVA_EXTENSION = ".java";
	public static final String ProjectSupport_SRC = "src/main/java";
	public static final String COMMENT_BOX_IMAGE_PATH="/icons/comment-box-icon.png";
	public static final String PORT_VALIDATION_REGEX="^([0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$";
	public static final String CONTAINER="Container";
  	public static final String DOT_SEPERATOR=".";
  	public static final String UNDERSCORE_SEPERATOR="_";
	public static final String XPATH_GRID_ROW = "XPath";
	
	public static final String PROPERTIES_EXTENSION=".properties";
	public static final String PARAM="/param/";
	public static final String SEPERATOR="\\.";
	
	public static final String GRAPH_PLUGIN_QUALIFIER = "hydrograph.ui.graph";
	public static final String CONSOLE_BUFFER_SIZE_PREFERANCE_NAME="console_buffer_size";
	public static final String DEFUALT_CONSOLE_BUFFER_SIZE = "10000";
	public static final String DISALLOW_DOCTYPE_DECLARATION = "http://apache.org/xml/features/disallow-doctype-decl";
	public static final String NUMERIC_REGEX = "[\\d]*";
	
	
	public static final String NUMBER_OF_PARTITIONS = "numPartitions";
	public static final String NOP_LOWER_BOUND = "lowerBound";
	public static final String NOP_UPPER_BOUND = "upperBound";
	public static final String ADDITIONAL_DB_FETCH_SIZE = "fetchSize";
	public static final String ADDITIONAL_DB_CHUNK_SIZE = "chunkSize";
	public static final String DB_PARTITION_KEY="partitionKey";
	public static final String ADDITIONAL_PARAMETERS_FOR_DB ="additionalDBParameters";
	
	public static final String FTP="FTP";
	public static final String SFTP="SFTP";
	public static final String AWS_S3="AWS S3 HTTPS";
	public static final String STAND_AUTH="Standard Authentication";
	public static final String USER_ID_KEY="User ID and Key";
	public static final String AWS_S3_KEY="AWS S3 Access Key";
	public static final String AWS_S3_PROP_FILE="AWS S3 Property File";
	public static final String PROTOCOL_SELECTION="protocolSelection";
	public static final String GET_FILE="Get Files";
	public static final String PUT_FILE="Put Files";
	public static final String GET_FILE_S3="Get Files with AWS S3";
	public static final String PUT_FILE_S3="Put Files with AWS S3";
	public static final String S3FILETRANSFER="S3FileTransfer";
	
	
	
	public static final String PARAM_FOLDER="param";
	
	public static final String APPEND = "Append";
	public static final String OVERWRITE = "Overwrite";
	public static final String FAIL_IF_EXISTS = "FailIfFileExists";
	public static final String IS_FIELD = "Is Field";
	public static final String OUTPUT_EXCEL = "OFExcel";
	public static final String ERROR_MESSAGE = "ErrorMessage";
}

