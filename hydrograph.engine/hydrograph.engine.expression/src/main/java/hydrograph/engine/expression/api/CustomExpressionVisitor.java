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

import hydrograph.engine.expression.antlr.ExpressionEditorBaseVisitor;
import hydrograph.engine.expression.antlr.ExpressionEditorParser;
import hydrograph.engine.expression.antlr.ExpressionEditorVisitor;

import java.util.HashSet;
import java.util.Set;

/**
 * The Class CustomExpressionVisitor .
 *
 * @author Bitwise
 */
class CustomExpressionVisitor extends ExpressionEditorBaseVisitor<String> implements ExpressionEditorVisitor<String> {

	private Set<String> fieldList;

	public CustomExpressionVisitor() {
		fieldList = new HashSet<String>();
	}

	@Override
	public String visitJavaIdentifier(ExpressionEditorParser.JavaIdentifierContext ctx) {
		fieldList.add(ctx.getText());
		return visitChildren(ctx);
	}

	/**
	 * @return a set of {@link String} objects which contains all the fields
	 *         used in expression.
	 */
	public Set<String> getFieldList() {
		return fieldList;
	}

}
