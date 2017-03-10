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
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class QueryParserParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, FieldIdentifier=24, 
		Identifier=25, WS=26;
	public static final int
		RULE_eval = 0, RULE_expression = 1, RULE_leftBrace = 2, RULE_rightBrace = 3, 
		RULE_specialexpr = 4, RULE_andOr = 5, RULE_condition = 6, RULE_javaiden = 7, 
		RULE_fieldname = 8;
	public static final String[] ruleNames = {
		"eval", "expression", "leftBrace", "rightBrace", "specialexpr", "andOr", 
		"condition", "javaiden", "fieldname"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "';'", "'('", "')'", "'in'", "'not in'", "'between'", "'like'", 
		"'not like'", "'IN'", "'NOT IN'", "'BETWEEN'", "'LIKE'", "'NOT LIKE'", 
		"' or '", "' and '", "' OR '", "' AND '", "'='", "'<'", "'<='", "'>'", 
		"'>='", "'<>'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		"FieldIdentifier", "Identifier", "WS"
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
	public String getGrammarFileName() { return "QueryParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public QueryParserParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class EvalContext extends ParserRuleContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<LeftBraceContext> leftBrace() {
			return getRuleContexts(LeftBraceContext.class);
		}
		public LeftBraceContext leftBrace(int i) {
			return getRuleContext(LeftBraceContext.class,i);
		}
		public List<AndOrContext> andOr() {
			return getRuleContexts(AndOrContext.class);
		}
		public AndOrContext andOr(int i) {
			return getRuleContext(AndOrContext.class,i);
		}
		public List<RightBraceContext> rightBrace() {
			return getRuleContexts(RightBraceContext.class);
		}
		public RightBraceContext rightBrace(int i) {
			return getRuleContext(RightBraceContext.class,i);
		}
		public EvalContext eval() {
			return getRuleContext(EvalContext.class,0);
		}
		public EvalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eval; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryParserListener ) ((QueryParserListener)listener).enterEval(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryParserListener ) ((QueryParserListener)listener).exitEval(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryParserVisitor ) return ((QueryParserVisitor<? extends T>)visitor).visitEval(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EvalContext eval() throws RecognitionException {
		return eval(0);
	}

	private EvalContext eval(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		EvalContext _localctx = new EvalContext(_ctx, _parentState);
		EvalContext _prevctx = _localctx;
		int _startState = 0;
		enterRecursionRule(_localctx, 0, RULE_eval, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(22);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(19);
				leftBrace();
				}
				}
				setState(24);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(25);
			expression();
			setState(42);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(26);
					andOr();
					setState(30);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==T__1) {
						{
						{
						setState(27);
						leftBrace();
						}
						}
						setState(32);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(33);
					expression();
					setState(37);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
					while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
						if ( _alt==1 ) {
							{
							{
							setState(34);
							rightBrace();
							}
							} 
						}
						setState(39);
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
					}
					}
					} 
				}
				setState(44);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			}
			setState(48);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(45);
					rightBrace();
					}
					} 
				}
				setState(50);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			}
			}
			_ctx.stop = _input.LT(-1);
			setState(55);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new EvalContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_eval);
					setState(51);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(52);
					match(T__0);
					}
					} 
				}
				setState(57);
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

	public static class ExpressionContext extends ParserRuleContext {
		public List<FieldnameContext> fieldname() {
			return getRuleContexts(FieldnameContext.class);
		}
		public FieldnameContext fieldname(int i) {
			return getRuleContext(FieldnameContext.class,i);
		}
		public ConditionContext condition() {
			return getRuleContext(ConditionContext.class,0);
		}
		public SpecialexprContext specialexpr() {
			return getRuleContext(SpecialexprContext.class,0);
		}
		public JavaidenContext javaiden() {
			return getRuleContext(JavaidenContext.class,0);
		}
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryParserListener ) ((QueryParserListener)listener).enterExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryParserListener ) ((QueryParserListener)listener).exitExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryParserVisitor ) return ((QueryParserVisitor<? extends T>)visitor).visitExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		ExpressionContext _localctx = new ExpressionContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(58);
			fieldname();
			setState(64);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				{
				setState(59);
				condition();
				setState(62);
				switch (_input.LA(1)) {
				case Identifier:
					{
					setState(60);
					javaiden();
					}
					break;
				case FieldIdentifier:
					{
					setState(61);
					fieldname();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				break;
			}
			setState(67);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				{
				setState(66);
				specialexpr();
				}
				break;
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

	public static class LeftBraceContext extends ParserRuleContext {
		public LeftBraceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_leftBrace; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryParserListener ) ((QueryParserListener)listener).enterLeftBrace(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryParserListener ) ((QueryParserListener)listener).exitLeftBrace(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryParserVisitor ) return ((QueryParserVisitor<? extends T>)visitor).visitLeftBrace(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LeftBraceContext leftBrace() throws RecognitionException {
		LeftBraceContext _localctx = new LeftBraceContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_leftBrace);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(69);
			match(T__1);
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

	public static class RightBraceContext extends ParserRuleContext {
		public RightBraceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rightBrace; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryParserListener ) ((QueryParserListener)listener).enterRightBrace(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryParserListener ) ((QueryParserListener)listener).exitRightBrace(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryParserVisitor ) return ((QueryParserVisitor<? extends T>)visitor).visitRightBrace(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RightBraceContext rightBrace() throws RecognitionException {
		RightBraceContext _localctx = new RightBraceContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_rightBrace);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(71);
			match(T__2);
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

	public static class SpecialexprContext extends ParserRuleContext {
		public LeftBraceContext leftBrace() {
			return getRuleContext(LeftBraceContext.class,0);
		}
		public List<JavaidenContext> javaiden() {
			return getRuleContexts(JavaidenContext.class);
		}
		public JavaidenContext javaiden(int i) {
			return getRuleContext(JavaidenContext.class,i);
		}
		public RightBraceContext rightBrace() {
			return getRuleContext(RightBraceContext.class,0);
		}
		public AndOrContext andOr() {
			return getRuleContext(AndOrContext.class,0);
		}
		public List<FieldnameContext> fieldname() {
			return getRuleContexts(FieldnameContext.class);
		}
		public FieldnameContext fieldname(int i) {
			return getRuleContext(FieldnameContext.class,i);
		}
		public SpecialexprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_specialexpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryParserListener ) ((QueryParserListener)listener).enterSpecialexpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryParserListener ) ((QueryParserListener)listener).exitSpecialexpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryParserVisitor ) return ((QueryParserVisitor<? extends T>)visitor).visitSpecialexpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SpecialexprContext specialexpr() throws RecognitionException {
		SpecialexprContext _localctx = new SpecialexprContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_specialexpr);
		int _la;
		try {
			setState(128);
			switch (_input.LA(1)) {
			case T__3:
				enterOuterAlt(_localctx, 1);
				{
				setState(73);
				match(T__3);
				setState(74);
				leftBrace();
				setState(75);
				javaiden();
				setState(76);
				rightBrace();
				}
				break;
			case T__4:
				enterOuterAlt(_localctx, 2);
				{
				setState(78);
				match(T__4);
				setState(79);
				leftBrace();
				setState(80);
				javaiden();
				setState(81);
				rightBrace();
				}
				break;
			case T__5:
				enterOuterAlt(_localctx, 3);
				{
				setState(83);
				match(T__5);
				setState(86);
				switch (_input.LA(1)) {
				case Identifier:
					{
					setState(84);
					javaiden();
					}
					break;
				case FieldIdentifier:
					{
					setState(85);
					fieldname();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(88);
				andOr();
				setState(91);
				switch (_input.LA(1)) {
				case Identifier:
					{
					setState(89);
					javaiden();
					}
					break;
				case FieldIdentifier:
					{
					setState(90);
					fieldname();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				break;
			case T__6:
				enterOuterAlt(_localctx, 4);
				{
				setState(93);
				match(T__6);
				setState(95);
				_la = _input.LA(1);
				if (_la==T__1) {
					{
					setState(94);
					leftBrace();
					}
				}

				setState(97);
				javaiden();
				setState(99);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
				case 1:
					{
					setState(98);
					rightBrace();
					}
					break;
				}
				}
				break;
			case T__7:
				enterOuterAlt(_localctx, 5);
				{
				setState(101);
				match(T__7);
				setState(103);
				_la = _input.LA(1);
				if (_la==T__1) {
					{
					setState(102);
					leftBrace();
					}
				}

				setState(105);
				javaiden();
				setState(107);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
				case 1:
					{
					setState(106);
					rightBrace();
					}
					break;
				}
				}
				break;
			case T__8:
				enterOuterAlt(_localctx, 6);
				{
				setState(109);
				match(T__8);
				setState(110);
				leftBrace();
				setState(111);
				javaiden();
				setState(112);
				rightBrace();
				}
				break;
			case T__9:
				enterOuterAlt(_localctx, 7);
				{
				setState(114);
				match(T__9);
				setState(115);
				leftBrace();
				setState(116);
				javaiden();
				setState(117);
				rightBrace();
				}
				break;
			case T__10:
				enterOuterAlt(_localctx, 8);
				{
				setState(119);
				match(T__10);
				setState(120);
				javaiden();
				setState(121);
				andOr();
				setState(122);
				javaiden();
				}
				break;
			case T__11:
				enterOuterAlt(_localctx, 9);
				{
				setState(124);
				match(T__11);
				setState(125);
				javaiden();
				}
				break;
			case T__12:
				enterOuterAlt(_localctx, 10);
				{
				setState(126);
				match(T__12);
				setState(127);
				javaiden();
				}
				break;
			default:
				throw new NoViableAltException(this);
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

	public static class AndOrContext extends ParserRuleContext {
		public AndOrContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_andOr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryParserListener ) ((QueryParserListener)listener).enterAndOr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryParserListener ) ((QueryParserListener)listener).exitAndOr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryParserVisitor ) return ((QueryParserVisitor<? extends T>)visitor).visitAndOr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AndOrContext andOr() throws RecognitionException {
		AndOrContext _localctx = new AndOrContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_andOr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(130);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__13) | (1L << T__14) | (1L << T__15) | (1L << T__16))) != 0)) ) {
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

	public static class ConditionContext extends ParserRuleContext {
		public ConditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_condition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryParserListener ) ((QueryParserListener)listener).enterCondition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryParserListener ) ((QueryParserListener)listener).exitCondition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryParserVisitor ) return ((QueryParserVisitor<? extends T>)visitor).visitCondition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConditionContext condition() throws RecognitionException {
		ConditionContext _localctx = new ConditionContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_condition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(132);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__17) | (1L << T__18) | (1L << T__19) | (1L << T__20) | (1L << T__21) | (1L << T__22))) != 0)) ) {
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

	public static class JavaidenContext extends ParserRuleContext {
		public List<TerminalNode> Identifier() { return getTokens(QueryParserParser.Identifier); }
		public TerminalNode Identifier(int i) {
			return getToken(QueryParserParser.Identifier, i);
		}
		public JavaidenContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_javaiden; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryParserListener ) ((QueryParserListener)listener).enterJavaiden(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryParserListener ) ((QueryParserListener)listener).exitJavaiden(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryParserVisitor ) return ((QueryParserVisitor<? extends T>)visitor).visitJavaiden(this);
			else return visitor.visitChildren(this);
		}
	}

	public final JavaidenContext javaiden() throws RecognitionException {
		JavaidenContext _localctx = new JavaidenContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_javaiden);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(135); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(134);
					match(Identifier);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(137); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,16,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
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

	public static class FieldnameContext extends ParserRuleContext {
		public TerminalNode FieldIdentifier() { return getToken(QueryParserParser.FieldIdentifier, 0); }
		public FieldnameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fieldname; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QueryParserListener ) ((QueryParserListener)listener).enterFieldname(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QueryParserListener ) ((QueryParserListener)listener).exitFieldname(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QueryParserVisitor ) return ((QueryParserVisitor<? extends T>)visitor).visitFieldname(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FieldnameContext fieldname() throws RecognitionException {
		FieldnameContext _localctx = new FieldnameContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_fieldname);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(139);
			match(FieldIdentifier);
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
		case 0:
			return eval_sempred((EvalContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean eval_sempred(EvalContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\34\u0090\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\3\2\3"+
		"\2\7\2\27\n\2\f\2\16\2\32\13\2\3\2\3\2\3\2\7\2\37\n\2\f\2\16\2\"\13\2"+
		"\3\2\3\2\7\2&\n\2\f\2\16\2)\13\2\7\2+\n\2\f\2\16\2.\13\2\3\2\7\2\61\n"+
		"\2\f\2\16\2\64\13\2\3\2\3\2\7\28\n\2\f\2\16\2;\13\2\3\3\3\3\3\3\3\3\5"+
		"\3A\n\3\5\3C\n\3\3\3\5\3F\n\3\3\4\3\4\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6"+
		"\3\6\3\6\3\6\3\6\3\6\3\6\3\6\5\6Y\n\6\3\6\3\6\3\6\5\6^\n\6\3\6\3\6\5\6"+
		"b\n\6\3\6\3\6\5\6f\n\6\3\6\3\6\5\6j\n\6\3\6\3\6\5\6n\n\6\3\6\3\6\3\6\3"+
		"\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\5\6\u0083"+
		"\n\6\3\7\3\7\3\b\3\b\3\t\6\t\u008a\n\t\r\t\16\t\u008b\3\n\3\n\3\n\2\3"+
		"\2\13\2\4\6\b\n\f\16\20\22\2\4\3\2\20\23\3\2\24\31\u009f\2\24\3\2\2\2"+
		"\4<\3\2\2\2\6G\3\2\2\2\bI\3\2\2\2\n\u0082\3\2\2\2\f\u0084\3\2\2\2\16\u0086"+
		"\3\2\2\2\20\u0089\3\2\2\2\22\u008d\3\2\2\2\24\30\b\2\1\2\25\27\5\6\4\2"+
		"\26\25\3\2\2\2\27\32\3\2\2\2\30\26\3\2\2\2\30\31\3\2\2\2\31\33\3\2\2\2"+
		"\32\30\3\2\2\2\33,\5\4\3\2\34 \5\f\7\2\35\37\5\6\4\2\36\35\3\2\2\2\37"+
		"\"\3\2\2\2 \36\3\2\2\2 !\3\2\2\2!#\3\2\2\2\" \3\2\2\2#\'\5\4\3\2$&\5\b"+
		"\5\2%$\3\2\2\2&)\3\2\2\2\'%\3\2\2\2\'(\3\2\2\2(+\3\2\2\2)\'\3\2\2\2*\34"+
		"\3\2\2\2+.\3\2\2\2,*\3\2\2\2,-\3\2\2\2-\62\3\2\2\2.,\3\2\2\2/\61\5\b\5"+
		"\2\60/\3\2\2\2\61\64\3\2\2\2\62\60\3\2\2\2\62\63\3\2\2\2\639\3\2\2\2\64"+
		"\62\3\2\2\2\65\66\f\3\2\2\668\7\3\2\2\67\65\3\2\2\28;\3\2\2\29\67\3\2"+
		"\2\29:\3\2\2\2:\3\3\2\2\2;9\3\2\2\2<B\5\22\n\2=@\5\16\b\2>A\5\20\t\2?"+
		"A\5\22\n\2@>\3\2\2\2@?\3\2\2\2AC\3\2\2\2B=\3\2\2\2BC\3\2\2\2CE\3\2\2\2"+
		"DF\5\n\6\2ED\3\2\2\2EF\3\2\2\2F\5\3\2\2\2GH\7\4\2\2H\7\3\2\2\2IJ\7\5\2"+
		"\2J\t\3\2\2\2KL\7\6\2\2LM\5\6\4\2MN\5\20\t\2NO\5\b\5\2O\u0083\3\2\2\2"+
		"PQ\7\7\2\2QR\5\6\4\2RS\5\20\t\2ST\5\b\5\2T\u0083\3\2\2\2UX\7\b\2\2VY\5"+
		"\20\t\2WY\5\22\n\2XV\3\2\2\2XW\3\2\2\2YZ\3\2\2\2Z]\5\f\7\2[^\5\20\t\2"+
		"\\^\5\22\n\2][\3\2\2\2]\\\3\2\2\2^\u0083\3\2\2\2_a\7\t\2\2`b\5\6\4\2a"+
		"`\3\2\2\2ab\3\2\2\2bc\3\2\2\2ce\5\20\t\2df\5\b\5\2ed\3\2\2\2ef\3\2\2\2"+
		"f\u0083\3\2\2\2gi\7\n\2\2hj\5\6\4\2ih\3\2\2\2ij\3\2\2\2jk\3\2\2\2km\5"+
		"\20\t\2ln\5\b\5\2ml\3\2\2\2mn\3\2\2\2n\u0083\3\2\2\2op\7\13\2\2pq\5\6"+
		"\4\2qr\5\20\t\2rs\5\b\5\2s\u0083\3\2\2\2tu\7\f\2\2uv\5\6\4\2vw\5\20\t"+
		"\2wx\5\b\5\2x\u0083\3\2\2\2yz\7\r\2\2z{\5\20\t\2{|\5\f\7\2|}\5\20\t\2"+
		"}\u0083\3\2\2\2~\177\7\16\2\2\177\u0083\5\20\t\2\u0080\u0081\7\17\2\2"+
		"\u0081\u0083\5\20\t\2\u0082K\3\2\2\2\u0082P\3\2\2\2\u0082U\3\2\2\2\u0082"+
		"_\3\2\2\2\u0082g\3\2\2\2\u0082o\3\2\2\2\u0082t\3\2\2\2\u0082y\3\2\2\2"+
		"\u0082~\3\2\2\2\u0082\u0080\3\2\2\2\u0083\13\3\2\2\2\u0084\u0085\t\2\2"+
		"\2\u0085\r\3\2\2\2\u0086\u0087\t\3\2\2\u0087\17\3\2\2\2\u0088\u008a\7"+
		"\33\2\2\u0089\u0088\3\2\2\2\u008a\u008b\3\2\2\2\u008b\u0089\3\2\2\2\u008b"+
		"\u008c\3\2\2\2\u008c\21\3\2\2\2\u008d\u008e\7\32\2\2\u008e\23\3\2\2\2"+
		"\23\30 \',\629@BEX]aeim\u0082\u008b";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}