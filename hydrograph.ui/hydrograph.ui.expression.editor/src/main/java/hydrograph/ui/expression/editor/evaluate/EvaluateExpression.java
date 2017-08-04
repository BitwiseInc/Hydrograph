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

package hydrograph.ui.expression.editor.evaluate;

import hydrograph.ui.expression.editor.Messages;
import hydrograph.ui.expression.editor.buttons.ValidateExpressionToolButton;
import hydrograph.ui.expression.editor.dialogs.ExpressionEditorDialog;
import hydrograph.ui.logging.factory.LogFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.custom.StyledText;
import org.slf4j.Logger;

public class EvaluateExpression {

	private static final String TARGET_ERROR_EXCEPTION_CLASS = "TargetError";
	private StyledText expressionEditor;
	private StyledText outputConsole;
	private EvaluateDialog evaluateDialog;

	private static final Logger LOGGER = LogFactory.INSTANCE.getLogger(ValidateExpressionToolButton.class);
	private static final String EVALUATE_METHOD_OF_EXPRESSION_JAR = "execute";

	public EvaluateExpression(StyledText expressionEditor, StyledText outputConsole, EvaluateDialog evaluateDialog) {
		this.expressionEditor = expressionEditor;
		this.outputConsole = outputConsole;
		this.evaluateDialog = evaluateDialog;
	}

	boolean isValidExpression() {
		try {
			DiagnosticCollector<JavaFileObject> diagnosticCollector = ValidateExpressionToolButton
					.compileExpresion(expressionEditor.getText(),
							(Map<String, Class<?>>) expressionEditor.getData(ExpressionEditorDialog.FIELD_DATA_TYPE_MAP),
							String.valueOf(expressionEditor.getData(ExpressionEditorDialog.COMPONENT_NAME_KEY)));
			if (diagnosticCollector != null && !diagnosticCollector.getDiagnostics().isEmpty()) {
				for (Diagnostic<?> diagnostic : diagnosticCollector.getDiagnostics()) {
					if (StringUtils.equals(diagnostic.getKind().name(), Diagnostic.Kind.ERROR.name())) {
						evaluateDialog.showError(diagnostic.getMessage(Locale.ENGLISH));
						return false;
					}
				}
			}
			return true;
		} catch (JavaModelException | MalformedURLException | IllegalAccessException | IllegalArgumentException exception) {
			LOGGER.error("Exception occurred while compiling expression", exception);
		} catch (InvocationTargetException exception) {
			LOGGER.warn("Exception occurred while invoking compile method for compiling expression");
			evaluateDialog.showError(exception.getCause().getMessage());
		} catch (ClassNotFoundException classNotFoundException) {
			evaluateDialog.showError("Cannot find validation jar in build path");
		}
		return false;
	}

	@SuppressWarnings({ "unchecked" })
	String invokeEvaluateFunctionFromJar(String expression,String[] fieldNames,Object[] values) {
		LOGGER.debug("Evaluating expression from jar");
		String output = null;
		Object[] returnObj;
		expression=ValidateExpressionToolButton.getExpressionText(expression);
		URLClassLoader child =null;
		try {
		returnObj = ValidateExpressionToolButton.getBuildPathForMethodInvocation();
		List<URL> urlList=(List<URL>) returnObj[0];
		String userFunctionsPropertyFileName=(String) returnObj[2];
		 child = URLClassLoader.newInstance(urlList.toArray(new URL[urlList.size()]));
		 Thread.currentThread().setContextClassLoader(child);
		Class<?> class1 = Class.forName(ValidateExpressionToolButton.HYDROGRAPH_ENGINE_EXPRESSION_VALIDATION_API_CLASS,true, child);
		Method[] methods = class1.getDeclaredMethods();
		for (Method method : methods) {
			if (method.getParameterTypes().length == 4
					&& StringUtils.equals(method.getName(), EVALUATE_METHOD_OF_EXPRESSION_JAR)) {
				method.getDeclaringClass().getClassLoader();
				output = String.valueOf(method.invoke(null, expression,userFunctionsPropertyFileName,fieldNames,values));
				break;
			}
		}
		} catch (JavaModelException | MalformedURLException | ClassNotFoundException | IllegalAccessException | IllegalArgumentException exception) {
			evaluateDialog.showError(Messages.ERROR_OCCURRED_WHILE_EVALUATING_EXPRESSION);
			LOGGER.error(Messages.ERROR_OCCURRED_WHILE_EVALUATING_EXPRESSION,exception);
		}catch (InvocationTargetException |RuntimeException exception) {
				if(exception.getCause().getCause()!=null){
					if(StringUtils.equals(exception.getCause().getCause().getClass().getSimpleName(), TARGET_ERROR_EXCEPTION_CLASS)){
						evaluateDialog.showError(getTargetException(exception.getCause().getCause().toString()));
					}else
						evaluateDialog.showError(exception.getCause().getCause().getMessage());
				}else 
					evaluateDialog.showError(exception.getCause().getMessage());
					LOGGER.debug("Invalid Expression....",exception);
		}
		finally{
			if(child!=null){
				try {
					child.close();
				} catch (IOException ioException) {
					LOGGER.error("Error occurred while closing classloader",ioException);
				}
			}
		}
		return output;
	}

	private String getTargetException(String error) {
		String tagetException="Target exception:";
		StringBuffer buffer=new StringBuffer(error);
		if(buffer.lastIndexOf(tagetException)>-1)
			buffer.delete(0,buffer.lastIndexOf(tagetException));
		return buffer.toString();
	}

}
