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

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;
/**
 * The Class ExpressionEditorParser .
 *
 * @author Bitwise
 */
@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ExpressionEditorParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		T__24=25, T__25=26, T__26=27, LPAREN=28, RPAREN=29, DOT=30, IntegerLiteral=31, 
		FloatingPointLiteral=32, BooleanLiteral=33, CharacterLiteral=34, StringLiteral=35, 
		NullLiteral=36, Identifier=37, WS=38, COMMENT=39, LINE_COMMENT=40;
	public static final int
		RULE_block = 0, RULE_blockStatement = 1, RULE_expression = 2, RULE_primary = 3, 
		RULE_expressionList = 4, RULE_arithmeticOperator = 5, RULE_statement = 6, 
		RULE_statementExpression = 7, RULE_parExpression = 8, RULE_functions = 9, 
		RULE_literal = 10, RULE_javaIdentifier = 11;
	public static final String[] ruleNames = {
		"block", "blockStatement", "expression", "primary", "expressionList", 
		"arithmeticOperator", "statement", "statementExpression", "parExpression", 
		"functions", "literal", "javaIdentifier"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'['", "']'", "'++'", "'--'", "'+'", "'-'", "'~'", "'!'", "'*'", 
		"'/'", "'%'", "'<'", "'>'", "'<='", "'>='", "'=='", "'!='", "'&'", "'^'", 
		"'|'", "'&&'", "'||'", "'?'", "':'", "','", "';'", "'new'", "'('", "')'", 
		"'.'", null, null, null, null, null, "'null'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, "LPAREN", "RPAREN", "DOT", "IntegerLiteral", "FloatingPointLiteral", 
		"BooleanLiteral", "CharacterLiteral", "StringLiteral", "NullLiteral", 
		"Identifier", "WS", "COMMENT", "LINE_COMMENT"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "ExpressionEditor.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public ExpressionEditorParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class BlockContext extends ParserRuleContext {
		public List<BlockStatementContext> blockStatement() {
			return getRuleContexts(BlockStatementContext.class);
		}
		public BlockStatementContext blockStatement(int i) {
			return getRuleContext(BlockStatementContext.class,i);
		}
		public BlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionEditorListener ) ((ExpressionEditorListener)listener).enterBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionEditorListener ) ((ExpressionEditorListener)listener).exitBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpressionEditorVisitor ) return ((ExpressionEditorVisitor<? extends T>)visitor).visitBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BlockContext block() throws RecognitionException {
		BlockContext _localctx = new BlockContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(27);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__26) | (1L << LPAREN) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << Identifier))) != 0)) {
				{
				{
				setState(24);
				blockStatement();
				}
				}
				setState(29);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BlockStatementContext extends ParserRuleContext {
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public BlockStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_blockStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionEditorListener ) ((ExpressionEditorListener)listener).enterBlockStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionEditorListener ) ((ExpressionEditorListener)listener).exitBlockStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpressionEditorVisitor ) return ((ExpressionEditorVisitor<? extends T>)visitor).visitBlockStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BlockStatementContext blockStatement() throws RecognitionException {
		BlockStatementContext _localctx = new BlockStatementContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_blockStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(30);
			statement();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExpressionContext extends ParserRuleContext {
		public PrimaryContext primary() {
			return getRuleContext(PrimaryContext.class,0);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public ExpressionListContext expressionList() {
			return getRuleContext(ExpressionListContext.class,0);
		}
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionEditorListener ) ((ExpressionEditorListener)listener).enterExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionEditorListener ) ((ExpressionEditorListener)listener).exitExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpressionEditorVisitor ) return ((ExpressionEditorVisitor<? extends T>)visitor).visitExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		return expression(0);
	}

	private ExpressionContext expression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExpressionContext _localctx = new ExpressionContext(_ctx, _parentState);
		ExpressionContext _prevctx = _localctx;
		int _startState = 4;
		enterRecursionRule(_localctx, 4, RULE_expression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(42);
			switch (_input.LA(1)) {
			case T__26:
			case IntegerLiteral:
			case FloatingPointLiteral:
			case BooleanLiteral:
			case CharacterLiteral:
			case StringLiteral:
			case NullLiteral:
			case Identifier:
				{
				setState(33);
				primary();
				}
				break;
			case LPAREN:
				{
				setState(34);
				match(LPAREN);
				setState(35);
				expression(0);
				setState(36);
				match(RPAREN);
				}
				break;
			case T__2:
			case T__3:
			case T__4:
			case T__5:
				{
				setState(38);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(39);
				expression(15);
				}
				break;
			case T__6:
			case T__7:
				{
				setState(40);
				_la = _input.LA(1);
				if ( !(_la==T__6 || _la==T__7) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(41);
				expression(14);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(106);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(104);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
					case 1:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(44);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(45);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__8) | (1L << T__9) | (1L << T__10))) != 0)) ) {
						_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(46);
						expression(13);
						}
						break;
					case 2:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(47);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(48);
						_la = _input.LA(1);
						if ( !(_la==T__4 || _la==T__5) ) {
						_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(49);
						expression(12);
						}
						break;
					case 3:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(50);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(58);
						_errHandler.sync(this);
						switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
						case 1:
							{
							setState(51);
							match(T__11);
							setState(52);
							match(T__11);
							}
							break;
						case 2:
							{
							setState(53);
							match(T__12);
							setState(54);
							match(T__12);
							setState(55);
							match(T__12);
							}
							break;
						case 3:
							{
							setState(56);
							match(T__12);
							setState(57);
							match(T__12);
							}
							break;
						}
						setState(60);
						expression(11);
						}
						break;
					case 4:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(61);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(62);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__11) | (1L << T__12) | (1L << T__13) | (1L << T__14))) != 0)) ) {
						_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(63);
						expression(10);
						}
						break;
					case 5:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(64);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(65);
						_la = _input.LA(1);
						if ( !(_la==T__15 || _la==T__16) ) {
						_errHandler.recoverInline(this);
						} else {
							consume();
						}
						setState(66);
						expression(9);
						}
						break;
					case 6:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(67);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(68);
						match(T__17);
						setState(69);
						expression(8);
						}
						break;
					case 7:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(70);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(71);
						match(T__18);
						setState(72);
						expression(7);
						}
						break;
					case 8:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(73);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(74);
						match(T__19);
						setState(75);
						expression(6);
						}
						break;
					case 9:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(76);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(77);
						match(T__20);
						setState(78);
						expression(5);
						}
						break;
					case 10:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(79);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(80);
						match(T__21);
						setState(81);
						expression(4);
						}
						break;
					case 11:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(82);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(83);
						match(T__22);
						setState(84);
						expression(0);
						setState(85);
						match(T__23);
						setState(86);
						expression(3);
						}
						break;
					case 12:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(88);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(89);
						match(DOT);
						setState(90);
						expression(2);
						}
						break;
					case 13:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(91);
						if (!(precpred(_ctx, 18))) throw new FailedPredicateException(this, "precpred(_ctx, 18)");
						setState(92);
						match(T__0);
						setState(93);
						expression(0);
						setState(94);
						match(T__1);
						}
						break;
					case 14:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(96);
						if (!(precpred(_ctx, 16))) throw new FailedPredicateException(this, "precpred(_ctx, 16)");
						setState(97);
						_la = _input.LA(1);
						if ( !(_la==T__2 || _la==T__3) ) {
						_errHandler.recoverInline(this);
						} else {
							consume();
						}
						}
						break;
					case 15:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(98);
						if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
						setState(99);
						match(LPAREN);
						setState(101);
						_la = _input.LA(1);
						if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__26) | (1L << LPAREN) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << Identifier))) != 0)) {
							{
							setState(100);
							expressionList();
							}
						}

						setState(103);
						match(RPAREN);
						}
						break;
					}
					} 
				}
				setState(108);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class PrimaryContext extends ParserRuleContext {
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public FunctionsContext functions() {
			return getRuleContext(FunctionsContext.class,0);
		}
		public JavaIdentifierContext javaIdentifier() {
			return getRuleContext(JavaIdentifierContext.class,0);
		}
		public PrimaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primary; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionEditorListener ) ((ExpressionEditorListener)listener).enterPrimary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionEditorListener ) ((ExpressionEditorListener)listener).exitPrimary(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpressionEditorVisitor ) return ((ExpressionEditorVisitor<? extends T>)visitor).visitPrimary(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PrimaryContext primary() throws RecognitionException {
		PrimaryContext _localctx = new PrimaryContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_primary);
		try {
			setState(112);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(109);
				literal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(110);
				functions();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(111);
				javaIdentifier();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExpressionListContext extends ParserRuleContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public ExpressionListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressionList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionEditorListener ) ((ExpressionEditorListener)listener).enterExpressionList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionEditorListener ) ((ExpressionEditorListener)listener).exitExpressionList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpressionEditorVisitor ) return ((ExpressionEditorVisitor<? extends T>)visitor).visitExpressionList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionListContext expressionList() throws RecognitionException {
		ExpressionListContext _localctx = new ExpressionListContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_expressionList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(114);
			expression(0);
			setState(119);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__24) {
				{
				{
				setState(115);
				match(T__24);
				setState(116);
				expression(0);
				}
				}
				setState(121);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ArithmeticOperatorContext extends ParserRuleContext {
		public ArithmeticOperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arithmeticOperator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionEditorListener ) ((ExpressionEditorListener)listener).enterArithmeticOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionEditorListener ) ((ExpressionEditorListener)listener).exitArithmeticOperator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpressionEditorVisitor ) return ((ExpressionEditorVisitor<? extends T>)visitor).visitArithmeticOperator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArithmeticOperatorContext arithmeticOperator() throws RecognitionException {
		ArithmeticOperatorContext _localctx = new ArithmeticOperatorContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_arithmeticOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(122);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StatementContext extends ParserRuleContext {
		public StatementExpressionContext statementExpression() {
			return getRuleContext(StatementExpressionContext.class,0);
		}
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionEditorListener ) ((ExpressionEditorListener)listener).enterStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionEditorListener ) ((ExpressionEditorListener)listener).exitStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpressionEditorVisitor ) return ((ExpressionEditorVisitor<? extends T>)visitor).visitStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_statement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(124);
			statementExpression();
			setState(126);
			_la = _input.LA(1);
			if (_la==T__25) {
				{
				setState(125);
				match(T__25);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StatementExpressionContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public StatementExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statementExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionEditorListener ) ((ExpressionEditorListener)listener).enterStatementExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionEditorListener ) ((ExpressionEditorListener)listener).exitStatementExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpressionEditorVisitor ) return ((ExpressionEditorVisitor<? extends T>)visitor).visitStatementExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatementExpressionContext statementExpression() throws RecognitionException {
		StatementExpressionContext _localctx = new StatementExpressionContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_statementExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(128);
			expression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ParExpressionContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ParExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionEditorListener ) ((ExpressionEditorListener)listener).enterParExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionEditorListener ) ((ExpressionEditorListener)listener).exitParExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpressionEditorVisitor ) return ((ExpressionEditorVisitor<? extends T>)visitor).visitParExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParExpressionContext parExpression() throws RecognitionException {
		ParExpressionContext _localctx = new ParExpressionContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_parExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(130);
			match(LPAREN);
			setState(131);
			expression(0);
			setState(132);
			match(RPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FunctionsContext extends ParserRuleContext {
		public JavaIdentifierContext javaIdentifier() {
			return getRuleContext(JavaIdentifierContext.class,0);
		}
		public List<TerminalNode> DOT() { return getTokens(ExpressionEditorParser.DOT); }
		public TerminalNode DOT(int i) {
			return getToken(ExpressionEditorParser.DOT, i);
		}
		public List<FunctionsContext> functions() {
			return getRuleContexts(FunctionsContext.class);
		}
		public FunctionsContext functions(int i) {
			return getRuleContext(FunctionsContext.class,i);
		}
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public TerminalNode LPAREN() { return getToken(ExpressionEditorParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(ExpressionEditorParser.RPAREN, 0); }
		public FunctionsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functions; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionEditorListener ) ((ExpressionEditorListener)listener).enterFunctions(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionEditorListener ) ((ExpressionEditorListener)listener).exitFunctions(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpressionEditorVisitor ) return ((ExpressionEditorVisitor<? extends T>)visitor).visitFunctions(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionsContext functions() throws RecognitionException {
		FunctionsContext _localctx = new FunctionsContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_functions);
		int _la;
		try {
			int _alt;
			setState(159);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(134);
				javaIdentifier();
				setState(139);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(135);
						match(DOT);
						setState(136);
						functions();
						}
						} 
					}
					setState(141);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(142);
				literal();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(143);
				javaIdentifier();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(144);
				match(T__26);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(145);
				javaIdentifier();
				setState(146);
				match(LPAREN);
				setState(148);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__26) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << Identifier))) != 0)) {
					{
					setState(147);
					functions();
					}
				}

				setState(154);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__24) {
					{
					{
					setState(150);
					match(T__24);
					setState(151);
					functions();
					}
					}
					setState(156);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(157);
				match(RPAREN);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LiteralContext extends ParserRuleContext {
		public TerminalNode IntegerLiteral() { return getToken(ExpressionEditorParser.IntegerLiteral, 0); }
		public TerminalNode FloatingPointLiteral() { return getToken(ExpressionEditorParser.FloatingPointLiteral, 0); }
		public TerminalNode CharacterLiteral() { return getToken(ExpressionEditorParser.CharacterLiteral, 0); }
		public TerminalNode StringLiteral() { return getToken(ExpressionEditorParser.StringLiteral, 0); }
		public TerminalNode BooleanLiteral() { return getToken(ExpressionEditorParser.BooleanLiteral, 0); }
		public TerminalNode NullLiteral() { return getToken(ExpressionEditorParser.NullLiteral, 0); }
		public LiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionEditorListener ) ((ExpressionEditorListener)listener).enterLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionEditorListener ) ((ExpressionEditorListener)listener).exitLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpressionEditorVisitor ) return ((ExpressionEditorVisitor<? extends T>)visitor).visitLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LiteralContext literal() throws RecognitionException {
		LiteralContext _localctx = new LiteralContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_literal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(161);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << CharacterLiteral) | (1L << StringLiteral) | (1L << NullLiteral))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class JavaIdentifierContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(ExpressionEditorParser.Identifier, 0); }
		public JavaIdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_javaIdentifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionEditorListener ) ((ExpressionEditorListener)listener).enterJavaIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExpressionEditorListener ) ((ExpressionEditorListener)listener).exitJavaIdentifier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ExpressionEditorVisitor ) return ((ExpressionEditorVisitor<? extends T>)visitor).visitJavaIdentifier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final JavaIdentifierContext javaIdentifier() throws RecognitionException {
		JavaIdentifierContext _localctx = new JavaIdentifierContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_javaIdentifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(163);
			match(Identifier);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 2:
			return expression_sempred((ExpressionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 12);
		case 1:
			return precpred(_ctx, 11);
		case 2:
			return precpred(_ctx, 10);
		case 3:
			return precpred(_ctx, 9);
		case 4:
			return precpred(_ctx, 8);
		case 5:
			return precpred(_ctx, 7);
		case 6:
			return precpred(_ctx, 6);
		case 7:
			return precpred(_ctx, 5);
		case 8:
			return precpred(_ctx, 4);
		case 9:
			return precpred(_ctx, 3);
		case 10:
			return precpred(_ctx, 2);
		case 11:
			return precpred(_ctx, 1);
		case 12:
			return precpred(_ctx, 18);
		case 13:
			return precpred(_ctx, 16);
		case 14:
			return precpred(_ctx, 13);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3*\u00a8\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\3\2\7\2\34\n\2\f\2\16\2\37\13\2\3\3\3\3\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4-\n\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4=\n\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4"+
		"\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4h\n\4"+
		"\3\4\7\4k\n\4\f\4\16\4n\13\4\3\5\3\5\3\5\5\5s\n\5\3\6\3\6\3\6\7\6x\n\6"+
		"\f\6\16\6{\13\6\3\7\3\7\3\b\3\b\5\b\u0081\n\b\3\t\3\t\3\n\3\n\3\n\3\n"+
		"\3\13\3\13\3\13\7\13\u008c\n\13\f\13\16\13\u008f\13\13\3\13\3\13\3\13"+
		"\3\13\3\13\3\13\5\13\u0097\n\13\3\13\3\13\7\13\u009b\n\13\f\13\16\13\u009e"+
		"\13\13\3\13\3\13\5\13\u00a2\n\13\3\f\3\f\3\r\3\r\3\r\2\3\6\16\2\4\6\b"+
		"\n\f\16\20\22\24\26\30\2\n\3\2\5\b\3\2\t\n\3\2\13\r\3\2\7\b\3\2\16\21"+
		"\3\2\22\23\3\2\5\6\3\2!&\u00bc\2\35\3\2\2\2\4 \3\2\2\2\6,\3\2\2\2\br\3"+
		"\2\2\2\nt\3\2\2\2\f|\3\2\2\2\16~\3\2\2\2\20\u0082\3\2\2\2\22\u0084\3\2"+
		"\2\2\24\u00a1\3\2\2\2\26\u00a3\3\2\2\2\30\u00a5\3\2\2\2\32\34\5\4\3\2"+
		"\33\32\3\2\2\2\34\37\3\2\2\2\35\33\3\2\2\2\35\36\3\2\2\2\36\3\3\2\2\2"+
		"\37\35\3\2\2\2 !\5\16\b\2!\5\3\2\2\2\"#\b\4\1\2#-\5\b\5\2$%\7\36\2\2%"+
		"&\5\6\4\2&\'\7\37\2\2\'-\3\2\2\2()\t\2\2\2)-\5\6\4\21*+\t\3\2\2+-\5\6"+
		"\4\20,\"\3\2\2\2,$\3\2\2\2,(\3\2\2\2,*\3\2\2\2-l\3\2\2\2./\f\16\2\2/\60"+
		"\t\4\2\2\60k\5\6\4\17\61\62\f\r\2\2\62\63\t\5\2\2\63k\5\6\4\16\64<\f\f"+
		"\2\2\65\66\7\16\2\2\66=\7\16\2\2\678\7\17\2\289\7\17\2\29=\7\17\2\2:;"+
		"\7\17\2\2;=\7\17\2\2<\65\3\2\2\2<\67\3\2\2\2<:\3\2\2\2=>\3\2\2\2>k\5\6"+
		"\4\r?@\f\13\2\2@A\t\6\2\2Ak\5\6\4\fBC\f\n\2\2CD\t\7\2\2Dk\5\6\4\13EF\f"+
		"\t\2\2FG\7\24\2\2Gk\5\6\4\nHI\f\b\2\2IJ\7\25\2\2Jk\5\6\4\tKL\f\7\2\2L"+
		"M\7\26\2\2Mk\5\6\4\bNO\f\6\2\2OP\7\27\2\2Pk\5\6\4\7QR\f\5\2\2RS\7\30\2"+
		"\2Sk\5\6\4\6TU\f\4\2\2UV\7\31\2\2VW\5\6\4\2WX\7\32\2\2XY\5\6\4\5Yk\3\2"+
		"\2\2Z[\f\3\2\2[\\\7 \2\2\\k\5\6\4\4]^\f\24\2\2^_\7\3\2\2_`\5\6\4\2`a\7"+
		"\4\2\2ak\3\2\2\2bc\f\22\2\2ck\t\b\2\2de\f\17\2\2eg\7\36\2\2fh\5\n\6\2"+
		"gf\3\2\2\2gh\3\2\2\2hi\3\2\2\2ik\7\37\2\2j.\3\2\2\2j\61\3\2\2\2j\64\3"+
		"\2\2\2j?\3\2\2\2jB\3\2\2\2jE\3\2\2\2jH\3\2\2\2jK\3\2\2\2jN\3\2\2\2jQ\3"+
		"\2\2\2jT\3\2\2\2jZ\3\2\2\2j]\3\2\2\2jb\3\2\2\2jd\3\2\2\2kn\3\2\2\2lj\3"+
		"\2\2\2lm\3\2\2\2m\7\3\2\2\2nl\3\2\2\2os\5\26\f\2ps\5\24\13\2qs\5\30\r"+
		"\2ro\3\2\2\2rp\3\2\2\2rq\3\2\2\2s\t\3\2\2\2ty\5\6\4\2uv\7\33\2\2vx\5\6"+
		"\4\2wu\3\2\2\2x{\3\2\2\2yw\3\2\2\2yz\3\2\2\2z\13\3\2\2\2{y\3\2\2\2|}\t"+
		"\2\2\2}\r\3\2\2\2~\u0080\5\20\t\2\177\u0081\7\34\2\2\u0080\177\3\2\2\2"+
		"\u0080\u0081\3\2\2\2\u0081\17\3\2\2\2\u0082\u0083\5\6\4\2\u0083\21\3\2"+
		"\2\2\u0084\u0085\7\36\2\2\u0085\u0086\5\6\4\2\u0086\u0087\7\37\2\2\u0087"+
		"\23\3\2\2\2\u0088\u008d\5\30\r\2\u0089\u008a\7 \2\2\u008a\u008c\5\24\13"+
		"\2\u008b\u0089\3\2\2\2\u008c\u008f\3\2\2\2\u008d\u008b\3\2\2\2\u008d\u008e"+
		"\3\2\2\2\u008e\u00a2\3\2\2\2\u008f\u008d\3\2\2\2\u0090\u00a2\5\26\f\2"+
		"\u0091\u00a2\5\30\r\2\u0092\u00a2\7\35\2\2\u0093\u0094\5\30\r\2\u0094"+
		"\u0096\7\36\2\2\u0095\u0097\5\24\13\2\u0096\u0095\3\2\2\2\u0096\u0097"+
		"\3\2\2\2\u0097\u009c\3\2\2\2\u0098\u0099\7\33\2\2\u0099\u009b\5\24\13"+
		"\2\u009a\u0098\3\2\2\2\u009b\u009e\3\2\2\2\u009c\u009a\3\2\2\2\u009c\u009d"+
		"\3\2\2\2\u009d\u009f\3\2\2\2\u009e\u009c\3\2\2\2\u009f\u00a0\7\37\2\2"+
		"\u00a0\u00a2\3\2\2\2\u00a1\u0088\3\2\2\2\u00a1\u0090\3\2\2\2\u00a1\u0091"+
		"\3\2\2\2\u00a1\u0092\3\2\2\2\u00a1\u0093\3\2\2\2\u00a2\25\3\2\2\2\u00a3"+
		"\u00a4\t\t\2\2\u00a4\27\3\2\2\2\u00a5\u00a6\7\'\2\2\u00a6\31\3\2\2\2\17"+
		"\35,<gjlry\u0080\u008d\u0096\u009c\u00a1";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}