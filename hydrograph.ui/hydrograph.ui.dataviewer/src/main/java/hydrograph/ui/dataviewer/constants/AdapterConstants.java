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

package hydrograph.ui.dataviewer.constants;

/**
 * The Class AdapterConstants.
 * Holds the constants for Data viewer adapter.
 * 
 * @author Bitwise
 *
 */
public class AdapterConstants {
	public static final String CSV_DRIVER_CLASS="org.relique.jdbc.csv.CsvDriver";
	public static final String CSV_DRIVER_CONNECTION_PREFIX="jdbc:relique:csv:";
	public static final String CSV = ".csv";
	public static final String SCHEMA_FILE_EXTENTION=".xml";
	public static final String COLUMN_TYPES = "columnTypes";
	public static final String DATE="Date";
	public static final String TIMESTAMP="TimeStamp";
	public static final String DATE_FORMAT="yyyy-MM-dd HH:mm:ss";
}
