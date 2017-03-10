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

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathNotFoundException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cascading.lingual.type.SQLDateCoercibleType;

/**
 * Created by santlalg on 6/27/2016.
 */

public class LingualSchemaCreatorTest {

	static JsonParser jsonParser;
	static Object obj;
	static JsonObject JsonObject;
	static String tableName = "test";
	static String stereotypeName = "test_stereo";
	static String inputPath = "testData/Input/JobId/Input1_out0";
	static String linugalMetaDataPath = "testData/MetaData/";
	static String processingSchema = "lingualschema";
	static String resultSchema = "resultschema";

	@BeforeClass
	public static void init() throws ClassNotFoundException, IOException {

		String fieldNames[] = { "f1", "f2", "f3", "f4" };
		Type[] fieldTypes = { String.class, new SQLDateCoercibleType(), new SQLDateCoercibleType(), BigDecimal.class };

		new LingualSchemaCreator().createCatalog(linugalMetaDataPath, processingSchema, tableName, stereotypeName,
				inputPath, fieldNames, fieldTypes);

		jsonParser = new JsonParser();
		FileReader file = new FileReader("testData/MetaData/.lingual/catalog");
		obj = jsonParser.parse(file);
		JsonObject = (JsonObject) obj;
		file.close();
	}

	@Test
	public void itShouldTestHadoop2Mr1Platform() {

		// given
		String expectedPlatform = "hadoop2-mr1";

		// when
		String platformName = JsonObject.get("platformName").getAsString();

		// then
		Assert.assertTrue(expectedPlatform.equals(platformName));
	}

	@Test
	public void itShouldTestExistenceOfSchema() {
		// given
		String expectedLingualSchema = "lingualschema";
		String expectedResultSchema = "resultSchema";

		// when
		JsonObject lingualSchObj = getLingualSchema(processingSchema);
		JsonObject resultSchObj = getLingualSchema(resultSchema);

		// then
		Assert.assertTrue(expectedLingualSchema.equals(lingualSchObj.get("name").getAsString()));
		Assert.assertTrue(expectedResultSchema.equals(resultSchObj.get("name").getAsString()));
	}

	@Test
	public void itShouldTestStereoTypeNameOfLingualSchema() {
		// given
		String expectedStereotypeName = "test_stereo";

		// when
		JsonObject lingualSchema = getLingualSchema(processingSchema);
		JsonArray jsonArray = (JsonArray) lingualSchema.get("stereotypes");
		String stereotypeName = null;
		for (Object obj : jsonArray) {
			JsonObject jsonObj = (JsonObject) obj;
			stereotypeName = jsonObj.get("name").getAsString();
		}

		// then
		Assert.assertTrue(expectedStereotypeName.equals(stereotypeName));

	}

	@Test
	public void itShouldTestFieldNameAndFieldType() {
		// given
		String expectedFieldName = "f1, f2, f3, f4";
		String expectedFieldType = "java.lang.String, cascading.lingual.type.SQLDateCoercibleType, cascading.lingual.type.SQLDateCoercibleType, java.math.BigDecimal";
		String fieldName = null;
		String fieldType = null;

		// when
		JsonObject lingualSchema = getLingualSchema(processingSchema);
		JsonArray jsonArray = lingualSchema.get("stereotypes").getAsJsonArray();
		String actualFields = getConvertedArrayToString(
				jsonArray.get(0).getAsJsonObject().getAsJsonObject("fields").getAsJsonArray("names"));
		String actualTypes = getConvertedArrayToString(
				jsonArray.get(0).getAsJsonObject().getAsJsonObject("fields").getAsJsonArray("types"));

		// then
		Assert.assertEquals(expectedFieldName, actualFields);
		Assert.assertEquals(expectedFieldType, actualTypes);
	}

	@Test
	public void itShouldTestLingualSchemaTableName_StereoTypeFor_Identifier_ForTable() {
		// given
		String expectedIdentifier = "testData/Input/JobId/Input1_out0";
		String expectedTableName = "test";
		String expectedStereotypeName = "test_stereo";

		// when
		JsonObject lingualSchema = getLingualSchema(processingSchema);
		JsonObject childTableObject = lingualSchema.get("childTables").getAsJsonObject();
		JsonObject tableObject = childTableObject.getAsJsonObject("test");

		String identifier = tableObject.get("identifier").getAsString();
		String tableName = tableObject.get("name").getAsString();
		String stereotypeName = tableObject.get("stereotypeName").getAsString();

		// then
		Assert.assertEquals(expectedIdentifier, identifier);
		Assert.assertEquals(expectedTableName, tableName);
		Assert.assertEquals(expectedStereotypeName, stereotypeName);
	}

	private String getConvertedArrayToString(JsonArray array) {
		String string = "";
		for (int i = 0; i < array.size(); i++)
			string += (i == array.size() - 1) ? array.get(i).getAsString() : array.get(i).getAsString() + ", ";

		return string;
	}

	private JsonObject getLingualSchema(String schema) {
		JsonObject childSchema = getChildSchema();
		JsonObject lingualSchema = (JsonObject) jsonParser.parse(childSchema.get(schema).toString());
		return lingualSchema;
	}

	private JsonObject getChildSchema() {
		JsonObject rootSchema = (JsonObject) jsonParser.parse(JsonObject.get("rootSchemaDef").toString());
		String default1 = rootSchema.get("defaultProtocol").getAsString();
		JsonObject childSchema = (JsonObject) jsonParser.parse(rootSchema.get("childSchemas").toString());
		return childSchema;
	}

	@AfterClass
	public static void cleanUp() {
		System.gc();
		Configuration configuration = new Configuration();
		FileSystem fileSystem = null;

		try {
			fileSystem = FileSystem.get(configuration);
			Path deletingFilePath = new Path("testData/MetaData/");
			if (!fileSystem.exists(deletingFilePath)) {
				throw new PathNotFoundException(deletingFilePath.toString());
			} else {

				boolean isDeleted = fileSystem.delete(deletingFilePath, true);
				if (isDeleted) {
					fileSystem.deleteOnExit(deletingFilePath);
				}
			}
			fileSystem.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
