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
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * The Interface ExpressionEditorVisitor.
 *
 * @author Bitwise
 */
public interface ExpressionEditorVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ExpressionEditorParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(ExpressionEditorParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionEditorParser#blockStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlockStatement(ExpressionEditorParser.BlockStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionEditorParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(ExpressionEditorParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionEditorParser#primary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimary(ExpressionEditorParser.PrimaryContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionEditorParser#expressionList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionList(ExpressionEditorParser.ExpressionListContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionEditorParser#arithmeticOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArithmeticOperator(ExpressionEditorParser.ArithmeticOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionEditorParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(ExpressionEditorParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionEditorParser#statementExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatementExpression(ExpressionEditorParser.StatementExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionEditorParser#parExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParExpression(ExpressionEditorParser.ParExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionEditorParser#functions}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctions(ExpressionEditorParser.FunctionsContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionEditorParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral(ExpressionEditorParser.LiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExpressionEditorParser#javaIdentifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJavaIdentifier(ExpressionEditorParser.JavaIdentifierContext ctx);
}