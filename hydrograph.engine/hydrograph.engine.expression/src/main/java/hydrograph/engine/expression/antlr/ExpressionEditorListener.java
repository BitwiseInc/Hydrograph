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
// Generated from C:/Users/gurdits/git/ExpressionEditor\ExpressionEditor.g4 by ANTLR 4.5.1
package hydrograph.engine.expression.antlr;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * The Interface ExpressionEditorListener.
 *
 * @author Bitwise
 */
public interface ExpressionEditorListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ExpressionEditorParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(ExpressionEditorParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionEditorParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(ExpressionEditorParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionEditorParser#blockStatement}.
	 * @param ctx the parse tree
	 */
	void enterBlockStatement(ExpressionEditorParser.BlockStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionEditorParser#blockStatement}.
	 * @param ctx the parse tree
	 */
	void exitBlockStatement(ExpressionEditorParser.BlockStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionEditorParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(ExpressionEditorParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionEditorParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(ExpressionEditorParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionEditorParser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary(ExpressionEditorParser.PrimaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionEditorParser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary(ExpressionEditorParser.PrimaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionEditorParser#expressionList}.
	 * @param ctx the parse tree
	 */
	void enterExpressionList(ExpressionEditorParser.ExpressionListContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionEditorParser#expressionList}.
	 * @param ctx the parse tree
	 */
	void exitExpressionList(ExpressionEditorParser.ExpressionListContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionEditorParser#arithmeticOperator}.
	 * @param ctx the parse tree
	 */
	void enterArithmeticOperator(ExpressionEditorParser.ArithmeticOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionEditorParser#arithmeticOperator}.
	 * @param ctx the parse tree
	 */
	void exitArithmeticOperator(ExpressionEditorParser.ArithmeticOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionEditorParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(ExpressionEditorParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionEditorParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(ExpressionEditorParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionEditorParser#statementExpression}.
	 * @param ctx the parse tree
	 */
	void enterStatementExpression(ExpressionEditorParser.StatementExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionEditorParser#statementExpression}.
	 * @param ctx the parse tree
	 */
	void exitStatementExpression(ExpressionEditorParser.StatementExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionEditorParser#parExpression}.
	 * @param ctx the parse tree
	 */
	void enterParExpression(ExpressionEditorParser.ParExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionEditorParser#parExpression}.
	 * @param ctx the parse tree
	 */
	void exitParExpression(ExpressionEditorParser.ParExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionEditorParser#functions}.
	 * @param ctx the parse tree
	 */
	void enterFunctions(ExpressionEditorParser.FunctionsContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionEditorParser#functions}.
	 * @param ctx the parse tree
	 */
	void exitFunctions(ExpressionEditorParser.FunctionsContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionEditorParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(ExpressionEditorParser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionEditorParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(ExpressionEditorParser.LiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExpressionEditorParser#javaIdentifier}.
	 * @param ctx the parse tree
	 */
	void enterJavaIdentifier(ExpressionEditorParser.JavaIdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExpressionEditorParser#javaIdentifier}.
	 * @param ctx the parse tree
	 */
	void exitJavaIdentifier(ExpressionEditorParser.JavaIdentifierContext ctx);
}