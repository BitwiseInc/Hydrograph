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

 
package hydrograph.ui.engine.ui.constants;


public enum UIComponentsConstants {
	
	FILE_DELIMITED("File Delimited"),
	FILE_PARQUET("File Parquet"),
	HIVE_PARQUET("Hive Parquet"),
	HIVE_TEXTFILE("Hive Text File"),
	VALIDITY_STATUS("validityStatus"),
	INPUT_CATEGORY("INPUT"),
	FILE_FIXEDWIDTH("File Fixed Width"),
	FILE_MIXEDSCHEMA("File Mixed Schema"),		
	OUTPUT_CATEGORY("OUTPUT"),
	STRAIGHTPULL_CATEGORY("STRAIGHTPULL"),
	CLONE("Clone"),
	ORACLE("Oracle"),
	MYSQL("Mysql"),
	TERADATA("Teradata"),
	REDSHIFT("RedShift"),
	UNION_ALL("Union All"), 
	REMOVE_DUPS("Remove Dups"),
	TRANSFORM_CATEGORY("TRANSFORM"),
	AGGREGATE("Aggregate"),
	GROUP_COMBINE("Groupcombine"),
	TRANSFORM("Transform"),
	JOIN("Join"),
	LOOKUP("Lookup"),
	FILTER("Filter"),
	LIMIT("Limit"),
	DISCARD("Discard"),
	SORT("Sort"),
	CUMULATE("Cumulate"),
	NORMALIZE("Normalize"),
	WARN("WARN"),
	ERROR("ERROR"),
	VALID("VALID"),
	COMMAND_CATEGORY("COMMAND"),
	RUN_PROGRAM("RunProgram"),
	RUN_SQL("RunSQL"),
	BATCH("batch"),
	PARTITION_BY_EXPRESSION("PartitionByExpression"),
	XML("XML"),
	DB_UPDATE("DB Update"),
	EXCEL("Excel");
	
	private final String value;

	UIComponentsConstants(String value) {
		this.value = value;
	}

	public String value() {
		return value;
	}

	public static UIComponentsConstants fromValue(String value) {
		for (UIComponentsConstants uiComponentsConstant : UIComponentsConstants
				.values()) {
			if (uiComponentsConstant.value.equals(value)) {
				return uiComponentsConstant;
			}
		}
		return null;
	}
}
