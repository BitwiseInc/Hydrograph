/*******************************************************************************
 *  Copyright 2017 Capital One Services, LLC and Bitwise, Inc.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *******************************************************************************/
package hydrograph.server.debug.lingual;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.Properties;

import hydrograph.server.utilities.ServiceUtilities;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class will filter data based on query
 * 
 * @author Santlal
 * 
 */
public class LingualFilter {
	static Logger LOG = LoggerFactory.getLogger(LingualFilter.class);
	String resultSchema = null;
	String processingSchema = null;

	public void filterData(String linugalMetaDataPath, String uniqueID, String hdfsFilePath, double sizeOfDataInByte,
			String localDebugFilePath, String condition, String[] fieldNames, Type[] fieldDatatype,
			Configuration conf) {

		String tableName = uniqueID + "_table";
		String stereotypeName = uniqueID + "_stereo";
		processingSchema = uniqueID + "_process";
		LingualSchemaCreator lingualSchemaCreator = new LingualSchemaCreator();
		try {
			lingualSchemaCreator.createCatalog(linugalMetaDataPath, processingSchema, tableName, stereotypeName,
					hdfsFilePath, fieldNames, fieldDatatype);

			runJdbcQuery(tableName, condition, fieldNames, getProperties(conf), sizeOfDataInByte,
					localDebugFilePath);


			lingualSchemaCreator.cleanUp(processingSchema);
		} catch (ClassNotFoundException | IOException | SQLException e) {
			LOG.error(e.getMessage());
		}
	}

	private Properties getProperties(Configuration conf) {
		Properties properties = new Properties();
		properties.putAll(conf.getValByRegex(".*"));
		return properties;
	}

	private String getFieldNamesWithQuotes(String[] fieldNames) {
		String fieldName="";
		for(int i=0;i<fieldNames.length;i++){
			fieldName += (i==0)?"\"" +fieldNames[i] +  "\"" :",\"" +fieldNames[i]  + "\"";
		}
			
		return fieldName;
	}

	private void runJdbcQuery(String tableName, String condition, String[] fieldNames, Properties properties, double sizeOfDataInByte, String localDebugFile )
			throws ClassNotFoundException, SQLException, IOException {
		LOG.debug("Initializing Connection ");
		Connection connection;

		String query = "select " + getFieldNamesWithQuotes(fieldNames) + " from  \"" + processingSchema + "\"" + ".\""
				+ tableName + "\" where " + condition;

		Class.forName("cascading.lingual.jdbc.Driver");
		connection = DriverManager.getConnection("jdbc:lingual:hadoop2-mr1", properties);
		PreparedStatement psmt = connection.prepareStatement(query);



		LOG.debug("executing query: " + query);
		ResultSet resultSet = psmt.executeQuery();

		writeFiles(resultSet, sizeOfDataInByte, localDebugFile);

        SQLException exception = null;
        try {
            ServiceUtilities.safeResultSetClose(resultSet);
        } catch (SQLException sqlException) {
            exception = sqlException;
        }

        try {
            ServiceUtilities.safeStatementClose(psmt);
        } catch (SQLException sqlException) {
            exception = sqlException;
        }
        try {
            ServiceUtilities.safeConnectionClose(connection);
        } catch (SQLException sqlException) {
            exception = sqlException;
        }
        if (exception != null) {
            throw exception;
        }
	}

	private void writeFiles(ResultSet resultSet, double sizeOfDataInByte, String localDebugFile)
			throws SQLException, IOException {

		String row = "";
		ResultSetMetaData metaData = resultSet.getMetaData();
		int columnLength = metaData.getColumnCount();
		int numOfBytes = 0;
		OutputStream os = new FileOutputStream(localDebugFile);
		StringBuilder stringBuilder = new StringBuilder();
		os.write((getColumnName(metaData) + "\n").toString().getBytes());

		while (resultSet.next()) {
			row = "";
			for (int i = 1; i <= columnLength; i++) {
				row = (resultSet.getObject(i) == null) ? "" : resultSet.getObject(i).toString();
				if (i != columnLength) {
					row += ",";
				}
				numOfBytes += row.length();
				stringBuilder.append(row);
			}

			if (numOfBytes <= sizeOfDataInByte) {
				os.write((stringBuilder + "\n").toString().getBytes());
			} else {
				break;
			}
			stringBuilder.setLength(0);
		}

        ServiceUtilities.safeOutputStreamClose(os);
	}

	private StringBuilder getColumnName(ResultSetMetaData metaData) throws SQLException {
		String columnName = "";
		StringBuilder stringBuilder = new StringBuilder();
		int numberOfColumn = metaData.getColumnCount();
		for (int i = 1; i <= numberOfColumn; i++) {
			columnName = metaData.getColumnName(i);
			if (i != numberOfColumn)
				columnName += ",";
			stringBuilder.append(columnName);
		}
		return stringBuilder;
	}
}
