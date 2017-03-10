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
 * limitations under the License
 *******************************************************************************/
package hydrograph.engine.cascading.assembly.utils;

import java.util.HashMap;
import java.util.Map;

public enum JavaToSQLTypeMapping {
	MySQL(){
		@Override
		Map<String, String> mapping() {
			return new HashMap<String, String>(){
				private static final long serialVersionUID = 1L;
			{
				put("java.lang.String","VARCHAR(256)");
				put("java.lang.Integer","INT");
				put("java.lang.Long","BIGINT");
				put("java.lang.Double","DOUBLE");
				put("java.lang.Float","FLOAT");
				put("java.lang.Short","SMALLINT");
				put("java.lang.Boolean","TINYINT");
				put("java.util.Date","TIMESTAMP");
				put("java.math.BigDecimal","DECIMAL");
			}};
		}
	},
	
	Redshift(){
		@Override
		Map<String, String> mapping() {
			return new HashMap<String, String>(){
				private static final long serialVersionUID = 1L;
			{
				put("java.lang.String","VARCHAR(256)");
				put("java.lang.Integer","INTEGER");
				put("java.lang.Long","BIGINT");
				put("java.lang.Double","DOUBLE PRECISION");
				put("java.lang.Float","FLOAT");
				put("java.lang.Short","SMALLINT");
				put("java.lang.Boolean","BOOLEAN");
				put("java.util.Date","TIMESTAMP");
				put("java.math.BigDecimal","DECIMAL");
			}};
		}
	},
	
	Teradata(){
		@Override
		Map<String, String> mapping() {
			return new HashMap<String, String>(){
				private static final long serialVersionUID = 1L;
			{
				put("java.lang.String","VARCHAR(256)");
				put("java.lang.Integer","INT");
				put("java.lang.Long","BIGINT");
				put("java.lang.Double","DOUBLE");
				put("java.lang.Float","FLOAT");
				put("java.lang.Short","SMALLINT");
				put("java.lang.Boolean","TINYINT");
				put("java.util.Date","TIMESTAMP");
				put("java.math.BigDecimal","DECIMAL");
			}};
		}
	},
	
	ORACLE(){
		@Override
		Map<String, String> mapping() {
			return new HashMap<String, String>(){
				private static final long serialVersionUID = 1L;
			{
                put("java.lang.String", "VARCHAR(256)");
                put("java.lang.Integer", "NUMBER(10)");
                put("java.lang.Long", "NUMBER(19)");
                put("java.lang.Short", "NUMBER(5)");
                put("java.lang.Boolean", "CHAR(5)");
                put("java.util.Date", "DATE");
                put("java.sql.Timestamp", "TIMESTAMP");
                put("java.math.BigDecimal", "NUMBER");

            }
            };
        }
    }
	;	
	
	private static JavaToSQLTypeMapping selectMapping(String dbName) {
		for (JavaToSQLTypeMapping i : JavaToSQLTypeMapping.values()) {
			if(i.name().equalsIgnoreCase(dbName))
				return i;
		}
		throw new NoJavaTODBTypeMappingFound();
	}
	
	/**
	 * this will map java data type to specific database type like mysql,oracle,teradata,redshit
	 * //@param String databaseType
	 * //@param String[] fieldsDataType
	 * //@param int[] fieldsScale,
	 * //@param int[] fieldsPrecision
	 *
	 * return String[] of database type
	 */
	public static String[] createTypeMapping(String databaseType, String[] fieldsDataType, int[] fieldsScale,
											 int[] fieldsPrecision) {
		Map<String, String> map = selectMapping(databaseType).mapping();
		String[] arr = new String[fieldsDataType.length];
		int counter = 0;
		for(int i=0;i<fieldsDataType.length;i++){
			if(fieldsDataType[i].equals("java.math.BigDecimal"))
                arr[i] = map.get(fieldsDataType[i]) + "(" + fieldsPrecision[i] + "," + fieldsScale[i] + ")";
            else
                arr[i] = map.get(fieldsDataType[i]);
        }
		return arr;
	}

    abstract Map<String, String> mapping();

	static class NoJavaTODBTypeMappingFound extends RuntimeException{
		private static final long serialVersionUID = 1L;
	}
}
