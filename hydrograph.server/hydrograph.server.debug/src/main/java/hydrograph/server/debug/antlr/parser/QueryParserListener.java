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
package hydrograph.server.debug.antlr.parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link QueryParserParser}.
 */
public interface QueryParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link QueryParserParser#eval}.
	 * @param ctx the parse tree
	 */
	void enterEval(QueryParserParser.EvalContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryParserParser#eval}.
	 * @param ctx the parse tree
	 */
	void exitEval(QueryParserParser.EvalContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(QueryParserParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(QueryParserParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryParserParser#leftBrace}.
	 * @param ctx the parse tree
	 */
	void enterLeftBrace(QueryParserParser.LeftBraceContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryParserParser#leftBrace}.
	 * @param ctx the parse tree
	 */
	void exitLeftBrace(QueryParserParser.LeftBraceContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryParserParser#rightBrace}.
	 * @param ctx the parse tree
	 */
	void enterRightBrace(QueryParserParser.RightBraceContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryParserParser#rightBrace}.
	 * @param ctx the parse tree
	 */
	void exitRightBrace(QueryParserParser.RightBraceContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryParserParser#specialexpr}.
	 * @param ctx the parse tree
	 */
	void enterSpecialexpr(QueryParserParser.SpecialexprContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryParserParser#specialexpr}.
	 * @param ctx the parse tree
	 */
	void exitSpecialexpr(QueryParserParser.SpecialexprContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryParserParser#andOr}.
	 * @param ctx the parse tree
	 */
	void enterAndOr(QueryParserParser.AndOrContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryParserParser#andOr}.
	 * @param ctx the parse tree
	 */
	void exitAndOr(QueryParserParser.AndOrContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryParserParser#condition}.
	 * @param ctx the parse tree
	 */
	void enterCondition(QueryParserParser.ConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryParserParser#condition}.
	 * @param ctx the parse tree
	 */
	void exitCondition(QueryParserParser.ConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryParserParser#javaiden}.
	 * @param ctx the parse tree
	 */
	void enterJavaiden(QueryParserParser.JavaidenContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryParserParser#javaiden}.
	 * @param ctx the parse tree
	 */
	void exitJavaiden(QueryParserParser.JavaidenContext ctx);
	/**
	 * Enter a parse tree produced by {@link QueryParserParser#fieldname}.
	 * @param ctx the parse tree
	 */
	void enterFieldname(QueryParserParser.FieldnameContext ctx);
	/**
	 * Exit a parse tree produced by {@link QueryParserParser#fieldname}.
	 * @param ctx the parse tree
	 */
	void exitFieldname(QueryParserParser.FieldnameContext ctx);
}