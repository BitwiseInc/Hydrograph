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

import bsh.EvalError;
import bsh.Interpreter;
import hydrograph.engine.expression.antlr.ExpressionEditorLexer;
import hydrograph.engine.expression.antlr.ExpressionEditorParser;
import hydrograph.engine.expression.api.wrapper.ValidationAPIWrapper;
import hydrograph.engine.expression.utils.ClassToDataTypeConversion;
import hydrograph.engine.expression.utils.CompileUtils;
import hydrograph.engine.expression.utils.PropertiesLoader;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.codehaus.janino.ExpressionEvaluator;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * The Class ValidationAPI .
 *
 * @author Bitwise
 */
public class ValidationAPI implements Serializable {

	private final static String USER_FUNCTIONS_PROPS = "UserFunctions.properties";
	private String packageNames = "";
	private String expr;
	private Interpreter interpreter;
	private List<String> listOfPackage;
	private  ExpressionEvaluator expressionEvaluator;
	private ValidationAPIWrapper apiWrapper;
	private List<ValidationAPIWrapper> apiWrapperList;


	public ValidationAPI(String expression, String propertiesFilePath) {
		listOfPackage=new ArrayList<>();
		if (propertiesFilePath != null && !propertiesFilePath.equals(""))
			this.packageNames += generatePackageName(propertiesFilePath);
		if (expression != null && !expression.equals("")) {
			this.expr = expression;
			this.packageNames += generatePackageName();
		} else
			throw new ExpressionNotFound("Expression is not found");
	}

	private String generatePackageName(String propertiesFile) {
		String PACKAGE_NAME = "";

		Properties properties = new Properties();
		try {
			properties = PropertiesLoader.getProperties(propertiesFile);
		} catch (IOException e) {
			throw new RuntimeException("Error reading the properties file: USER_FUNCTIONS_PROPS" + e);
		}
		for (Object importPackage : properties.keySet()) {
			PACKAGE_NAME += "import " + importPackage.toString() + "; ";
			listOfPackage.add(importPackage.toString());
		}
		return PACKAGE_NAME;
	}

	private String generatePackageName() {
		String PACKAGE_NAME = "";

		Properties properties = new Properties();
		try {
			properties = PropertiesLoader.getProperties(USER_FUNCTIONS_PROPS);
		} catch (IOException e) {
			throw new RuntimeException("Error reading the properties file: USER_FUNCTIONS_PROPS" + e);
		}
		for (Object importPackage : properties.keySet()) {
			PACKAGE_NAME += "import " + importPackage.toString() + "; ";
		}
		return PACKAGE_NAME;
	}

	private ExpressionEditorParser.BlockContext generateAntlrTree() {
		ANTLRInputStream stream = new ANTLRInputStream(expr);
		ExpressionEditorLexer lexer = new ExpressionEditorLexer(stream);
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);
		ExpressionEditorParser parser = new ExpressionEditorParser(tokenStream);
		parser.removeErrorListeners();
		parser.addErrorListener(DescriptiveErrorListener.INSTANCE);
		lexer.removeErrorListeners();
		lexer.addErrorListener(DescriptiveErrorListener.INSTANCE);
		return parser.block();
	}

	/**
	 * @return the boolean value {@link Boolean} according to expression parsed
	 *         through antlr
	 */
	public boolean isExpressionValid() {
		try {
			CustomExpressionVisitor customExpressionVisitor = new CustomExpressionVisitor();
			customExpressionVisitor.visit(generateAntlrTree());
			return true;
		} catch (Exception e) {
			try {
				throw e;
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * @return the string value {@link String} if expression is valid then
	 *         return valid expression with package name;
	 */
	public String getValidExpression() {
		if (isExpressionValid()) {
			return packageNames + expr;
		}
		return expr;
	}

	/**
	 * @param schemaFields
	 *            of {@link Map} which contains field name as a key
	 *            {@link String} and Data types as a value {@link Class}
	 * @return a DiagnosticCollector of {@link JavaFileObject} objects which
	 *         contains all the compile time information .
	 */
	public DiagnosticCollector<JavaFileObject> transformCompiler(Map<String, Class<?>> schemaFields) {
		String fields = "";
		CustomExpressionVisitor customExpressionVisitor = new CustomExpressionVisitor();
		customExpressionVisitor.visit(generateAntlrTree());
		for (String field : customExpressionVisitor.getFieldList()) {
			if (schemaFields.get(field) != null) {
				fields += ClassToDataTypeConversion.valueOf(schemaFields.get(field).getSimpleName()).getValue(field);
			}
		}

		return CompileUtils.javaCompile(fields, expr, packageNames, "Object");

	}

	/**
	 * @param schemaFields
	 *            of {@link Map} which contains field name as a key
	 *            {@link String} and Data types as a value {@link Class}
	 * @return a DiagnosticCollector of {@link JavaFileObject} objects which
	 *         contains all the compile time information .
	 */
	public DiagnosticCollector<JavaFileObject> filterCompiler(Map<String, Class<?>> schemaFields) {
		String fields = "";
		CustomExpressionVisitor customExpressionVisitor = new CustomExpressionVisitor();
		customExpressionVisitor.visit(generateAntlrTree());
		for (String field : customExpressionVisitor.getFieldList()) {
			if (schemaFields.get(field) != null) {
				fields += ClassToDataTypeConversion.valueOf(schemaFields.get(field).getSimpleName()).getValue(field);
			}
		}

		return CompileUtils.javaCompile(fields, expr, packageNames, "boolean");

	}

	private DiagnosticCollector<JavaFileObject> filterCompiler(Map<String, Class<?>> schemaFields,
			String externalJarPath) {
		String fields = "";
		CustomExpressionVisitor customExpressionVisitor = new CustomExpressionVisitor();
		customExpressionVisitor.visit(generateAntlrTree());
		for (String field : customExpressionVisitor.getFieldList()) {
			if (schemaFields.get(field) != null) {
				fields += ClassToDataTypeConversion.valueOf(schemaFields.get(field).getSimpleName()).getValue(field);
			}
		}

		return CompileUtils.javaCompile(fields, expr, externalJarPath, packageNames, "boolean");

	}

	private DiagnosticCollector<JavaFileObject> transformCompiler(Map<String, Class<?>> schemaFields,
			String externalJarPath) {
		String fields = "";
		CustomExpressionVisitor customExpressionVisitor = new CustomExpressionVisitor();
		customExpressionVisitor.visit(generateAntlrTree());
		for (String field : customExpressionVisitor.getFieldList()) {
			if (schemaFields.get(field) != null) {
				fields += ClassToDataTypeConversion.valueOf(schemaFields.get(field).getSimpleName()).getValue(field);
			}
		}

		return CompileUtils.javaCompile(fields, expr, externalJarPath, packageNames, "Object");

	}

	/**
	 * @param schemaFields
	 *            of {@link Map} which contains field name as a key
	 *            {@link String} and Data types as a value {@link Class}
	 * @return the list of String {@link List} contains fields name extract form
	 *         expression.
	 */
	public List<String> getFieldNameList(Map<String, Class<?>> schemaFields) {
		String fields = "";
		CustomExpressionVisitor customExpressionVisitor = new CustomExpressionVisitor();
		customExpressionVisitor.visit(generateAntlrTree());
		List<String> fieldNameList = new ArrayList<String>();
		for (String field : customExpressionVisitor.getFieldList()) {
			if (schemaFields.get(field) != null) {
				fieldNameList.add(field);
			}
		}
		return fieldNameList;

	}

	/**
	 * @return the object value {@link Object} w.r.t expression.
	 */
	public Object execute() {
		Interpreter interpreter = new Interpreter();
		try {
			return interpreter.eval(getValidExpression());
		} catch (EvalError e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * @param fieldNames
	 *            values are {@link String} array which contains field name used
	 *            in expression
	 * @param data
	 *            values are {@link Object} array which contains data used in
	 *            expression
	 * @return the object value {@link Object} w.r.t expression.
	 * @throws EvalError
	 */
	public Object execute(String[] fieldNames, Object[] data) throws EvalError {
		Interpreter interpreter = new Interpreter();
		for (int i = 0; i < fieldNames.length; i++) {
			interpreter.set(fieldNames[i], data[i]);
		}
		return interpreter.eval(getValidExpression());
	}
	
	/**
	 * @param fieldNames
	 *            values are {@link String} array which contains field name used
	 *            in expression
	 * @param data
	 *            values are {@link Object} array which contains data used in
	 *            expression
	 * @param validExpression
	 * 			  values are {@link String} which contains expression
	 * @return the object value {@link Object} w.r.t expression.
	 * @throws EvalError
	 */
	public Object execute(String[] fieldNames, Object[] data, String validExpression) throws EvalError {
		validExpression = packageNames + validExpression;
		if(null == interpreter ) {
			interpreter = new Interpreter();
		}
		for (int i = 0; i < fieldNames.length; i++) {
			interpreter.set(fieldNames[i], data[i]);
		}
		return interpreter.eval(validExpression);
	}

	private Class getType(String className){
		if(className.equalsIgnoreCase("string"))
			return String.class;
		else if(className.equalsIgnoreCase("Integer"))
			return Integer.class;
		else if(className.equalsIgnoreCase("Short"))
			return Short.class;
		else if(className.equalsIgnoreCase("Long"))
			return Long.class;
		else if(className.equalsIgnoreCase("Boolean"))
			return Boolean.class;
		else if(className.equalsIgnoreCase("Float"))
			return Float.class;
		else if(className.equalsIgnoreCase("Double"))
			return Double.class;
		else if(className.equalsIgnoreCase("Date") || className.equalsIgnoreCase("timestamp"))
			return Date.class;
		else if(className.equalsIgnoreCase("Object"))
			return Object.class;
		else if(className.contains("decimal") || className.contains("BigDecimal") )
			return BigDecimal.class;
		return Object.class;

	}

	public void init(String[] fieldNames,String[] fieldTypes){
		Class [] types=new Class[fieldTypes.length];
		int index=0;
			for(String keys:fieldTypes){
				types[index]=getType(keys);
				index++;
			}
//			expressionEvaluator=new ExpressionEvaluator(
//					packageNames + getValidExpression(),
//					Object.class,fieldNames,types);

			 apiWrapper=new ValidationAPIWrapper(packageNames+getValidExpression(),fieldNames,types);

//			expressionEvaluator.setDefaultImports(listOfPackage.toArray(new String[listOfPackage.size()]));

	}

	public ValidationAPI init(String validExpression,Map<String,String> fieldMap){
		String [] fields=new String[fieldMap.size()];
		Class [] types=new Class[fieldMap.size()];
		int index=0;
		try {
		for(String keys:fieldMap.keySet()){
				fields[index]=keys;
				types[index]=getType(fieldMap.get(keys));
			index++;
		}
			expressionEvaluator=new ExpressionEvaluator(
					packageNames + validExpression,
					Object.class,fields,types);

//			expressionEvaluator.setDefaultImports(listOfPackage.toArray(new String[listOfPackage.size()]));

			return this;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void init(String expression){
		try {
			apiWrapper=new ValidationAPIWrapper(expression,new String[]{},new Class[]{});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	public  Object exec(Object[] data) {
		try {
			return apiWrapper.execute(data);
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * @param validExpression
	 * 			  values are {@link String} which contains expression
	 * @return the object value {@link Object} w.r.t expression.
	 * @throws EvalError
	 */
	public Object execute(String validExpression) throws EvalError {
		if(null == interpreter ) {
			interpreter = new Interpreter();
		}
		return interpreter.eval(validExpression);
	}
	
	/**
	 * @param expression
	 *            {@link String} is a construct made up of fields, operators,
	 *            and method invocations
	 * @param propertiesFilePath
	 *            {@link String} is used to fetch the property file which
	 *            contains custom classes imports.
	 * @param schemaFields
	 *            of {@link Map} which contains field name as a key
	 *            {@link String} and Data types as a value {@link Class}
	 * @param externalJarPath
	 * @return
	 */
	public static DiagnosticCollector<JavaFileObject> filterCompiler(String expression, String propertiesFilePath,
			Map<String, Class<?>> schemaFields, String externalJarPath) {
		return new ValidationAPI(expression, propertiesFilePath).filterCompiler(schemaFields, externalJarPath);
	}

	/**
	 * @param expression
	 *            {@link String} is a construct made up of fields, operators,
	 *            and method invocations
	 * @param propertiesFilePath
	 *            {@link String} is used to fetch the property file which
	 *            contains custom classes imports.
	 * @param schemaFields
	 *            of {@link Map} which contains field name as a key
	 *            {@link String} and Data types as a value {@link Class}
	 * @param externalJarPath
	 * @return
	 */
	public static DiagnosticCollector<JavaFileObject> transformCompiler(String expression, String propertiesFilePath,
			Map<String, Class<?>> schemaFields, String externalJarPath) {
		return new ValidationAPI(expression, propertiesFilePath).transformCompiler(schemaFields, externalJarPath);
	}

	public static List<String> getFieldNameList(Map<String, Class<?>> schemaFields,String expression,String propertiesFilePath) {
		return new ValidationAPI(expression, propertiesFilePath).getFieldNameList(schemaFields);
	}
	
	/**
	 * @param expression
	 *            {@link String} is a construct made up of fields, operators,
	 *            and method invocations
	 * @param propertiesFilePath
	 *            {@link String} is used to fetch the property file which
	 *            contains custom classes imports.
	 * @param fieldNames
	 *            values are {@link String} array which contains field name used
	 *            in expression
	 * @param data
	 *            values are {@link Object} array which contains data used in
	 *            expression
	 * @return
	 */
	public static Object execute(String expression, String propertiesFilePath, String[] fieldNames, Object[] data) {
		try {
			return new ValidationAPI(expression, propertiesFilePath).execute(fieldNames, data);
		} catch (EvalError e) {
			throw new RuntimeException(e);
		}
	}

	public class ExpressionNotFound extends RuntimeException {
		public ExpressionNotFound(String mags) {
			super(mags);
		}

	}
	
	public String getExpr() {
		return expr;
	}

	public void setExpr(String expr) {
		this.expr = expr;
	}
	
}