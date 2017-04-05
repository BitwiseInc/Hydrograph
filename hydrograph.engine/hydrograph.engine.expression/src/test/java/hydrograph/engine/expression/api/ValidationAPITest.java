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
package hydrograph.engine.expression.api;


import org.junit.Assert;
import org.junit.Test;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * The Class ValidationAPITest.
 *
 * @author Bitwise
 */
public class ValidationAPITest {

	@Test
	public void itShouldThrowException() {
		ValidationAPI validationAPI = new ValidationAPI("(1==1)12:20;", "");
		boolean isValid = validationAPI.isExpressionValid();
		Assert.assertFalse(isValid);
	}

	@Test
	public void itShouldValidateTheExpression() {
		ValidationAPI validationAPI = new ValidationAPI("StringFunctions.stringMatch(\"AAA\",\"a\")?1:2", "");
		Assert.assertTrue(validationAPI.isExpressionValid());
	}

	@Test
	public void itShouldImportDefauldPackage() {
		ValidationAPI validationAPI = new ValidationAPI("(1==1)?12:20", "");
		Assert.assertTrue(validationAPI.getValidExpression().contains("import"));
	}

	@Test
	public void itShouldCompileFilterExpressionWithOutSemiColon() {
		ValidationAPI validationAPI = new ValidationAPI(
				"StringFunctions.stringMatch(\"HELLO WORLD\",DateFunctions.getStringDateFromDateObject(f1, \"\"))?true:false",
				"");
		Map<String, Class<?>> schemaFields = new HashMap<String, Class<?>>();
		schemaFields.put("f1", Date.class);
		DiagnosticCollector<JavaFileObject> dig = validationAPI.filterCompiler(schemaFields);

		Assert.assertTrue(dig.getDiagnostics().size() <= 0);
	}
	
	@Test
	public void itShouldCompileTransformExpressionWithSemicolon() {
		ValidationAPI validationAPI = new ValidationAPI(
				"StringFunctions.stringMatch(\"HELLO WORLD\",DateFunctions.getStringDateFromDateObject(f1, \"\"))?f1:\"HELLO WORLD\";",
				"");
		Map<String, Class<?>> schemaFields = new HashMap<String, Class<?>>();
		schemaFields.put("f1", Date.class);
		DiagnosticCollector<JavaFileObject> dig = validationAPI.transformCompiler(schemaFields);

		Assert.assertTrue(dig.getDiagnostics().size() <= 0);
	}
	
	@Test
	public void itShouldCompileTransformExpression() {
		ValidationAPI validationAPI = new ValidationAPI(
				"StringFunctions.stringMatch(\"HELLO WORLD\",DateFunctions.getStringDateFromDateObject(f1, \"\"))?f1:\"HELLO WORLD\"",
				"");
		Map<String, Class<?>> schemaFields = new HashMap<String, Class<?>>();
		schemaFields.put("f1", Date.class);
		DiagnosticCollector<JavaFileObject> dig = validationAPI.transformCompiler(schemaFields);

		Assert.assertTrue(dig.getDiagnostics().size() <= 0);
	}

	@Test
	public void itShouldMatchFieldName() {
		ValidationAPI validationAPI = new ValidationAPI(
				"StringFunctions.stringMatch(f2,DateFunctions.getStringDateFromDateObject(f1, \"\"))?1:0", "");
		Map<String, Class<?>> schemaFields = new HashMap<String, Class<?>>();
		schemaFields.put("f1", Date.class);
		schemaFields.put("f2", String.class);
		schemaFields.put("f3", Date.class);

		List<String> fieldList = validationAPI.getFieldNameList(schemaFields);
		Assert.assertEquals("f1", fieldList.get(0));
	}
	
	@Test
	public void itShouldExcuteTheExpressionWithOutSemicolon() {
		ValidationAPI validationAPI = new ValidationAPI("StringFunctions.stringMatch(\"test\",\"Testing\")?1:0", "");
		Assert.assertEquals(0, validationAPI.execute());
	}

	@Test
	public void itShouldExcuteTheExpressionWithSemicolon() {
		ValidationAPI validationAPI = new ValidationAPI("StringFunctions.stringMatch(\"test\",\"test\")?1:0;", "");
		Assert.assertEquals(1, validationAPI.execute());
	}
}