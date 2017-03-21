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
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
/**
 * The Class ExpressionEditorLexer .
 *
 * @author Bitwise
 */
@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ExpressionEditorLexer extends Lexer {
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
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
		"T__9", "T__10", "T__11", "T__12", "T__13", "T__14", "T__15", "T__16", 
		"T__17", "T__18", "T__19", "T__20", "T__21", "T__22", "T__23", "T__24", 
		"T__25", "T__26", "LPAREN", "RPAREN", "DOT", "IntegerLiteral", "DecimalIntegerLiteral", 
		"HexIntegerLiteral", "OctalIntegerLiteral", "BinaryIntegerLiteral", "IntegerTypeSuffix", 
		"DecimalNumeral", "Digits", "Digit", "NonZeroDigit", "DigitOrUnderscore", 
		"Underscores", "HexNumeral", "HexDigits", "HexDigit", "HexDigitOrUnderscore", 
		"OctalNumeral", "OctalDigits", "OctalDigit", "OctalDigitOrUnderscore", 
		"BinaryNumeral", "BinaryDigits", "BinaryDigit", "BinaryDigitOrUnderscore", 
		"FloatingPointLiteral", "DecimalFloatingPointLiteral", "ExponentPart", 
		"ExponentIndicator", "SignedInteger", "Sign", "FloatTypeSuffix", "HexadecimalFloatingPointLiteral", 
		"HexSignificand", "BinaryExponent", "BinaryExponentIndicator", "BooleanLiteral", 
		"CharacterLiteral", "SingleCharacter", "StringLiteral", "StringCharacters", 
		"StringCharacter", "EscapeSequence", "OctalEscape", "UnicodeEscape", "ZeroToThree", 
		"NullLiteral", "Identifier", "JavaLetter", "JavaLetterOrDigit", "WS", 
		"COMMENT", "LINE_COMMENT"
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


	public ExpressionEditorLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "ExpressionEditor.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 77:
			return JavaLetter_sempred((RuleContext)_localctx, predIndex);
		case 78:
			return JavaLetterOrDigit_sempred((RuleContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean JavaLetter_sempred(RuleContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return Character.isJavaIdentifierStart(_input.LA(-1));
		case 1:
			return Character.isJavaIdentifierStart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)));
		}
		return true;
	}
	private boolean JavaLetterOrDigit_sempred(RuleContext _localctx, int predIndex) {
		switch (predIndex) {
		case 2:
			return Character.isJavaIdentifierPart(_input.LA(-1));
		case 3:
			return Character.isJavaIdentifierPart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)));
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2*\u022c\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\4K\tK\4L\tL\4M\tM\4N\tN\4O\tO\4P\tP\4Q\tQ\4R\tR\4S\tS\3\2\3"+
		"\2\3\3\3\3\3\4\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n"+
		"\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\17\3\17\3\17\3\20\3\20\3\20"+
		"\3\21\3\21\3\21\3\22\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\26\3\26"+
		"\3\26\3\27\3\27\3\27\3\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34"+
		"\3\34\3\34\3\35\3\35\3\36\3\36\3\37\3\37\3 \3 \3 \3 \5 \u00f2\n \3!\3"+
		"!\5!\u00f6\n!\3\"\3\"\5\"\u00fa\n\"\3#\3#\5#\u00fe\n#\3$\3$\5$\u0102\n"+
		"$\3%\3%\3&\3&\3&\5&\u0109\n&\3&\3&\3&\5&\u010e\n&\5&\u0110\n&\3\'\3\'"+
		"\7\'\u0114\n\'\f\'\16\'\u0117\13\'\3\'\5\'\u011a\n\'\3(\3(\5(\u011e\n"+
		"(\3)\3)\3*\3*\5*\u0124\n*\3+\6+\u0127\n+\r+\16+\u0128\3,\3,\3,\3,\3-\3"+
		"-\7-\u0131\n-\f-\16-\u0134\13-\3-\5-\u0137\n-\3.\3.\3/\3/\5/\u013d\n/"+
		"\3\60\3\60\5\60\u0141\n\60\3\60\3\60\3\61\3\61\7\61\u0147\n\61\f\61\16"+
		"\61\u014a\13\61\3\61\5\61\u014d\n\61\3\62\3\62\3\63\3\63\5\63\u0153\n"+
		"\63\3\64\3\64\3\64\3\64\3\65\3\65\7\65\u015b\n\65\f\65\16\65\u015e\13"+
		"\65\3\65\5\65\u0161\n\65\3\66\3\66\3\67\3\67\5\67\u0167\n\67\38\38\58"+
		"\u016b\n8\39\39\39\59\u0170\n9\39\59\u0173\n9\39\59\u0176\n9\39\39\39"+
		"\59\u017b\n9\39\59\u017e\n9\39\39\39\59\u0183\n9\39\39\39\59\u0188\n9"+
		"\3:\3:\3:\3;\3;\3<\5<\u0190\n<\3<\3<\3=\3=\3>\3>\3?\3?\3?\5?\u019b\n?"+
		"\3@\3@\5@\u019f\n@\3@\3@\3@\5@\u01a4\n@\3@\3@\5@\u01a8\n@\3A\3A\3A\3B"+
		"\3B\3C\3C\3C\3C\3C\3C\3C\3C\3C\5C\u01b8\nC\3D\3D\3D\3D\3D\3D\3D\3D\5D"+
		"\u01c2\nD\3E\3E\3F\3F\5F\u01c8\nF\3F\3F\3G\6G\u01cd\nG\rG\16G\u01ce\3"+
		"H\3H\5H\u01d3\nH\3I\3I\3I\3I\5I\u01d9\nI\3J\3J\3J\3J\3J\3J\3J\3J\3J\3"+
		"J\3J\5J\u01e6\nJ\3K\3K\3K\3K\3K\3K\3K\3L\3L\3M\3M\3M\3M\3M\3N\3N\7N\u01f8"+
		"\nN\fN\16N\u01fb\13N\3O\3O\3O\3O\3O\3O\5O\u0203\nO\3P\3P\3P\3P\3P\3P\5"+
		"P\u020b\nP\3Q\6Q\u020e\nQ\rQ\16Q\u020f\3Q\3Q\3R\3R\3R\3R\7R\u0218\nR\f"+
		"R\16R\u021b\13R\3R\3R\3R\3R\3R\3S\3S\3S\3S\7S\u0226\nS\fS\16S\u0229\13"+
		"S\3S\3S\3\u0219\2T\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r"+
		"\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33"+
		"\65\34\67\359\36;\37= ?!A\2C\2E\2G\2I\2K\2M\2O\2Q\2S\2U\2W\2Y\2[\2]\2"+
		"_\2a\2c\2e\2g\2i\2k\2m\2o\"q\2s\2u\2w\2y\2{\2}\2\177\2\u0081\2\u0083\2"+
		"\u0085#\u0087$\u0089\2\u008b%\u008d\2\u008f\2\u0091\2\u0093\2\u0095\2"+
		"\u0097\2\u0099&\u009b\'\u009d\2\u009f\2\u00a1(\u00a3)\u00a5*\3\2\30\4"+
		"\2NNnn\3\2\63;\4\2ZZzz\5\2\62;CHch\3\2\629\4\2DDdd\3\2\62\63\4\2GGgg\4"+
		"\2--//\6\2FFHHffhh\4\2RRrr\4\2))^^\4\2$$^^\n\2$$))^^ddhhppttvv\3\2\62"+
		"\65\6\2&&C\\aac|\4\2\2\u0081\ud802\udc01\3\2\ud802\udc01\3\2\udc02\ue001"+
		"\7\2&&\62;C\\aac|\5\2\13\f\16\17\"\"\4\2\f\f\17\17\u023a\2\3\3\2\2\2\2"+
		"\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2"+
		"\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2"+
		"\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2"+
		"\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2"+
		"\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2"+
		"\2\2?\3\2\2\2\2o\3\2\2\2\2\u0085\3\2\2\2\2\u0087\3\2\2\2\2\u008b\3\2\2"+
		"\2\2\u0099\3\2\2\2\2\u009b\3\2\2\2\2\u00a1\3\2\2\2\2\u00a3\3\2\2\2\2\u00a5"+
		"\3\2\2\2\3\u00a7\3\2\2\2\5\u00a9\3\2\2\2\7\u00ab\3\2\2\2\t\u00ae\3\2\2"+
		"\2\13\u00b1\3\2\2\2\r\u00b3\3\2\2\2\17\u00b5\3\2\2\2\21\u00b7\3\2\2\2"+
		"\23\u00b9\3\2\2\2\25\u00bb\3\2\2\2\27\u00bd\3\2\2\2\31\u00bf\3\2\2\2\33"+
		"\u00c1\3\2\2\2\35\u00c3\3\2\2\2\37\u00c6\3\2\2\2!\u00c9\3\2\2\2#\u00cc"+
		"\3\2\2\2%\u00cf\3\2\2\2\'\u00d1\3\2\2\2)\u00d3\3\2\2\2+\u00d5\3\2\2\2"+
		"-\u00d8\3\2\2\2/\u00db\3\2\2\2\61\u00dd\3\2\2\2\63\u00df\3\2\2\2\65\u00e1"+
		"\3\2\2\2\67\u00e3\3\2\2\29\u00e7\3\2\2\2;\u00e9\3\2\2\2=\u00eb\3\2\2\2"+
		"?\u00f1\3\2\2\2A\u00f3\3\2\2\2C\u00f7\3\2\2\2E\u00fb\3\2\2\2G\u00ff\3"+
		"\2\2\2I\u0103\3\2\2\2K\u010f\3\2\2\2M\u0111\3\2\2\2O\u011d\3\2\2\2Q\u011f"+
		"\3\2\2\2S\u0123\3\2\2\2U\u0126\3\2\2\2W\u012a\3\2\2\2Y\u012e\3\2\2\2["+
		"\u0138\3\2\2\2]\u013c\3\2\2\2_\u013e\3\2\2\2a\u0144\3\2\2\2c\u014e\3\2"+
		"\2\2e\u0152\3\2\2\2g\u0154\3\2\2\2i\u0158\3\2\2\2k\u0162\3\2\2\2m\u0166"+
		"\3\2\2\2o\u016a\3\2\2\2q\u0187\3\2\2\2s\u0189\3\2\2\2u\u018c\3\2\2\2w"+
		"\u018f\3\2\2\2y\u0193\3\2\2\2{\u0195\3\2\2\2}\u0197\3\2\2\2\177\u01a7"+
		"\3\2\2\2\u0081\u01a9\3\2\2\2\u0083\u01ac\3\2\2\2\u0085\u01b7\3\2\2\2\u0087"+
		"\u01c1\3\2\2\2\u0089\u01c3\3\2\2\2\u008b\u01c5\3\2\2\2\u008d\u01cc\3\2"+
		"\2\2\u008f\u01d2\3\2\2\2\u0091\u01d8\3\2\2\2\u0093\u01e5\3\2\2\2\u0095"+
		"\u01e7\3\2\2\2\u0097\u01ee\3\2\2\2\u0099\u01f0\3\2\2\2\u009b\u01f5\3\2"+
		"\2\2\u009d\u0202\3\2\2\2\u009f\u020a\3\2\2\2\u00a1\u020d\3\2\2\2\u00a3"+
		"\u0213\3\2\2\2\u00a5\u0221\3\2\2\2\u00a7\u00a8\7]\2\2\u00a8\4\3\2\2\2"+
		"\u00a9\u00aa\7_\2\2\u00aa\6\3\2\2\2\u00ab\u00ac\7-\2\2\u00ac\u00ad\7-"+
		"\2\2\u00ad\b\3\2\2\2\u00ae\u00af\7/\2\2\u00af\u00b0\7/\2\2\u00b0\n\3\2"+
		"\2\2\u00b1\u00b2\7-\2\2\u00b2\f\3\2\2\2\u00b3\u00b4\7/\2\2\u00b4\16\3"+
		"\2\2\2\u00b5\u00b6\7\u0080\2\2\u00b6\20\3\2\2\2\u00b7\u00b8\7#\2\2\u00b8"+
		"\22\3\2\2\2\u00b9\u00ba\7,\2\2\u00ba\24\3\2\2\2\u00bb\u00bc\7\61\2\2\u00bc"+
		"\26\3\2\2\2\u00bd\u00be\7\'\2\2\u00be\30\3\2\2\2\u00bf\u00c0\7>\2\2\u00c0"+
		"\32\3\2\2\2\u00c1\u00c2\7@\2\2\u00c2\34\3\2\2\2\u00c3\u00c4\7>\2\2\u00c4"+
		"\u00c5\7?\2\2\u00c5\36\3\2\2\2\u00c6\u00c7\7@\2\2\u00c7\u00c8\7?\2\2\u00c8"+
		" \3\2\2\2\u00c9\u00ca\7?\2\2\u00ca\u00cb\7?\2\2\u00cb\"\3\2\2\2\u00cc"+
		"\u00cd\7#\2\2\u00cd\u00ce\7?\2\2\u00ce$\3\2\2\2\u00cf\u00d0\7(\2\2\u00d0"+
		"&\3\2\2\2\u00d1\u00d2\7`\2\2\u00d2(\3\2\2\2\u00d3\u00d4\7~\2\2\u00d4*"+
		"\3\2\2\2\u00d5\u00d6\7(\2\2\u00d6\u00d7\7(\2\2\u00d7,\3\2\2\2\u00d8\u00d9"+
		"\7~\2\2\u00d9\u00da\7~\2\2\u00da.\3\2\2\2\u00db\u00dc\7A\2\2\u00dc\60"+
		"\3\2\2\2\u00dd\u00de\7<\2\2\u00de\62\3\2\2\2\u00df\u00e0\7.\2\2\u00e0"+
		"\64\3\2\2\2\u00e1\u00e2\7=\2\2\u00e2\66\3\2\2\2\u00e3\u00e4\7p\2\2\u00e4"+
		"\u00e5\7g\2\2\u00e5\u00e6\7y\2\2\u00e68\3\2\2\2\u00e7\u00e8\7*\2\2\u00e8"+
		":\3\2\2\2\u00e9\u00ea\7+\2\2\u00ea<\3\2\2\2\u00eb\u00ec\7\60\2\2\u00ec"+
		">\3\2\2\2\u00ed\u00f2\5A!\2\u00ee\u00f2\5C\"\2\u00ef\u00f2\5E#\2\u00f0"+
		"\u00f2\5G$\2\u00f1\u00ed\3\2\2\2\u00f1\u00ee\3\2\2\2\u00f1\u00ef\3\2\2"+
		"\2\u00f1\u00f0\3\2\2\2\u00f2@\3\2\2\2\u00f3\u00f5\5K&\2\u00f4\u00f6\5"+
		"I%\2\u00f5\u00f4\3\2\2\2\u00f5\u00f6\3\2\2\2\u00f6B\3\2\2\2\u00f7\u00f9"+
		"\5W,\2\u00f8\u00fa\5I%\2\u00f9\u00f8\3\2\2\2\u00f9\u00fa\3\2\2\2\u00fa"+
		"D\3\2\2\2\u00fb\u00fd\5_\60\2\u00fc\u00fe\5I%\2\u00fd\u00fc\3\2\2\2\u00fd"+
		"\u00fe\3\2\2\2\u00feF\3\2\2\2\u00ff\u0101\5g\64\2\u0100\u0102\5I%\2\u0101"+
		"\u0100\3\2\2\2\u0101\u0102\3\2\2\2\u0102H\3\2\2\2\u0103\u0104\t\2\2\2"+
		"\u0104J\3\2\2\2\u0105\u0110\7\62\2\2\u0106\u010d\5Q)\2\u0107\u0109\5M"+
		"\'\2\u0108\u0107\3\2\2\2\u0108\u0109\3\2\2\2\u0109\u010e\3\2\2\2\u010a"+
		"\u010b\5U+\2\u010b\u010c\5M\'\2\u010c\u010e\3\2\2\2\u010d\u0108\3\2\2"+
		"\2\u010d\u010a\3\2\2\2\u010e\u0110\3\2\2\2\u010f\u0105\3\2\2\2\u010f\u0106"+
		"\3\2\2\2\u0110L\3\2\2\2\u0111\u0119\5O(\2\u0112\u0114\5S*\2\u0113\u0112"+
		"\3\2\2\2\u0114\u0117\3\2\2\2\u0115\u0113\3\2\2\2\u0115\u0116\3\2\2\2\u0116"+
		"\u0118\3\2\2\2\u0117\u0115\3\2\2\2\u0118\u011a\5O(\2\u0119\u0115\3\2\2"+
		"\2\u0119\u011a\3\2\2\2\u011aN\3\2\2\2\u011b\u011e\7\62\2\2\u011c\u011e"+
		"\5Q)\2\u011d\u011b\3\2\2\2\u011d\u011c\3\2\2\2\u011eP\3\2\2\2\u011f\u0120"+
		"\t\3\2\2\u0120R\3\2\2\2\u0121\u0124\5O(\2\u0122\u0124\7a\2\2\u0123\u0121"+
		"\3\2\2\2\u0123\u0122\3\2\2\2\u0124T\3\2\2\2\u0125\u0127\7a\2\2\u0126\u0125"+
		"\3\2\2\2\u0127\u0128\3\2\2\2\u0128\u0126\3\2\2\2\u0128\u0129\3\2\2\2\u0129"+
		"V\3\2\2\2\u012a\u012b\7\62\2\2\u012b\u012c\t\4\2\2\u012c\u012d\5Y-\2\u012d"+
		"X\3\2\2\2\u012e\u0136\5[.\2\u012f\u0131\5]/\2\u0130\u012f\3\2\2\2\u0131"+
		"\u0134\3\2\2\2\u0132\u0130\3\2\2\2\u0132\u0133\3\2\2\2\u0133\u0135\3\2"+
		"\2\2\u0134\u0132\3\2\2\2\u0135\u0137\5[.\2\u0136\u0132\3\2\2\2\u0136\u0137"+
		"\3\2\2\2\u0137Z\3\2\2\2\u0138\u0139\t\5\2\2\u0139\\\3\2\2\2\u013a\u013d"+
		"\5[.\2\u013b\u013d\7a\2\2\u013c\u013a\3\2\2\2\u013c\u013b\3\2\2\2\u013d"+
		"^\3\2\2\2\u013e\u0140\7\62\2\2\u013f\u0141\5U+\2\u0140\u013f\3\2\2\2\u0140"+
		"\u0141\3\2\2\2\u0141\u0142\3\2\2\2\u0142\u0143\5a\61\2\u0143`\3\2\2\2"+
		"\u0144\u014c\5c\62\2\u0145\u0147\5e\63\2\u0146\u0145\3\2\2\2\u0147\u014a"+
		"\3\2\2\2\u0148\u0146\3\2\2\2\u0148\u0149\3\2\2\2\u0149\u014b\3\2\2\2\u014a"+
		"\u0148\3\2\2\2\u014b\u014d\5c\62\2\u014c\u0148\3\2\2\2\u014c\u014d\3\2"+
		"\2\2\u014db\3\2\2\2\u014e\u014f\t\6\2\2\u014fd\3\2\2\2\u0150\u0153\5c"+
		"\62\2\u0151\u0153\7a\2\2\u0152\u0150\3\2\2\2\u0152\u0151\3\2\2\2\u0153"+
		"f\3\2\2\2\u0154\u0155\7\62\2\2\u0155\u0156\t\7\2\2\u0156\u0157\5i\65\2"+
		"\u0157h\3\2\2\2\u0158\u0160\5k\66\2\u0159\u015b\5m\67\2\u015a\u0159\3"+
		"\2\2\2\u015b\u015e\3\2\2\2\u015c\u015a\3\2\2\2\u015c\u015d\3\2\2\2\u015d"+
		"\u015f\3\2\2\2\u015e\u015c\3\2\2\2\u015f\u0161\5k\66\2\u0160\u015c\3\2"+
		"\2\2\u0160\u0161\3\2\2\2\u0161j\3\2\2\2\u0162\u0163\t\b\2\2\u0163l\3\2"+
		"\2\2\u0164\u0167\5k\66\2\u0165\u0167\7a\2\2\u0166\u0164\3\2\2\2\u0166"+
		"\u0165\3\2\2\2\u0167n\3\2\2\2\u0168\u016b\5q9\2\u0169\u016b\5}?\2\u016a"+
		"\u0168\3\2\2\2\u016a\u0169\3\2\2\2\u016bp\3\2\2\2\u016c\u016d\5M\'\2\u016d"+
		"\u016f\7\60\2\2\u016e\u0170\5M\'\2\u016f\u016e\3\2\2\2\u016f\u0170\3\2"+
		"\2\2\u0170\u0172\3\2\2\2\u0171\u0173\5s:\2\u0172\u0171\3\2\2\2\u0172\u0173"+
		"\3\2\2\2\u0173\u0175\3\2\2\2\u0174\u0176\5{>\2\u0175\u0174\3\2\2\2\u0175"+
		"\u0176\3\2\2\2\u0176\u0188\3\2\2\2\u0177\u0178\7\60\2\2\u0178\u017a\5"+
		"M\'\2\u0179\u017b\5s:\2\u017a\u0179\3\2\2\2\u017a\u017b\3\2\2\2\u017b"+
		"\u017d\3\2\2\2\u017c\u017e\5{>\2\u017d\u017c\3\2\2\2\u017d\u017e\3\2\2"+
		"\2\u017e\u0188\3\2\2\2\u017f\u0180\5M\'\2\u0180\u0182\5s:\2\u0181\u0183"+
		"\5{>\2\u0182\u0181\3\2\2\2\u0182\u0183\3\2\2\2\u0183\u0188\3\2\2\2\u0184"+
		"\u0185\5M\'\2\u0185\u0186\5{>\2\u0186\u0188\3\2\2\2\u0187\u016c\3\2\2"+
		"\2\u0187\u0177\3\2\2\2\u0187\u017f\3\2\2\2\u0187\u0184\3\2\2\2\u0188r"+
		"\3\2\2\2\u0189\u018a\5u;\2\u018a\u018b\5w<\2\u018bt\3\2\2\2\u018c\u018d"+
		"\t\t\2\2\u018dv\3\2\2\2\u018e\u0190\5y=\2\u018f\u018e\3\2\2\2\u018f\u0190"+
		"\3\2\2\2\u0190\u0191\3\2\2\2\u0191\u0192\5M\'\2\u0192x\3\2\2\2\u0193\u0194"+
		"\t\n\2\2\u0194z\3\2\2\2\u0195\u0196\t\13\2\2\u0196|\3\2\2\2\u0197\u0198"+
		"\5\177@\2\u0198\u019a\5\u0081A\2\u0199\u019b\5{>\2\u019a\u0199\3\2\2\2"+
		"\u019a\u019b\3\2\2\2\u019b~\3\2\2\2\u019c\u019e\5W,\2\u019d\u019f\7\60"+
		"\2\2\u019e\u019d\3\2\2\2\u019e\u019f\3\2\2\2\u019f\u01a8\3\2\2\2\u01a0"+
		"\u01a1\7\62\2\2\u01a1\u01a3\t\4\2\2\u01a2\u01a4\5Y-\2\u01a3\u01a2\3\2"+
		"\2\2\u01a3\u01a4\3\2\2\2\u01a4\u01a5\3\2\2\2\u01a5\u01a6\7\60\2\2\u01a6"+
		"\u01a8\5Y-\2\u01a7\u019c\3\2\2\2\u01a7\u01a0\3\2\2\2\u01a8\u0080\3\2\2"+
		"\2\u01a9\u01aa\5\u0083B\2\u01aa\u01ab\5w<\2\u01ab\u0082\3\2\2\2\u01ac"+
		"\u01ad\t\f\2\2\u01ad\u0084\3\2\2\2\u01ae\u01af\7v\2\2\u01af\u01b0\7t\2"+
		"\2\u01b0\u01b1\7w\2\2\u01b1\u01b8\7g\2\2\u01b2\u01b3\7h\2\2\u01b3\u01b4"+
		"\7c\2\2\u01b4\u01b5\7n\2\2\u01b5\u01b6\7u\2\2\u01b6\u01b8\7g\2\2\u01b7"+
		"\u01ae\3\2\2\2\u01b7\u01b2\3\2\2\2\u01b8\u0086\3\2\2\2\u01b9\u01ba\7)"+
		"\2\2\u01ba\u01bb\5\u0089E\2\u01bb\u01bc\7)\2\2\u01bc\u01c2\3\2\2\2\u01bd"+
		"\u01be\7)\2\2\u01be\u01bf\5\u0091I\2\u01bf\u01c0\7)\2\2\u01c0\u01c2\3"+
		"\2\2\2\u01c1\u01b9\3\2\2\2\u01c1\u01bd\3\2\2\2\u01c2\u0088\3\2\2\2\u01c3"+
		"\u01c4\n\r\2\2\u01c4\u008a\3\2\2\2\u01c5\u01c7\7$\2\2\u01c6\u01c8\5\u008d"+
		"G\2\u01c7\u01c6\3\2\2\2\u01c7\u01c8\3\2\2\2\u01c8\u01c9\3\2\2\2\u01c9"+
		"\u01ca\7$\2\2\u01ca\u008c\3\2\2\2\u01cb\u01cd\5\u008fH\2\u01cc\u01cb\3"+
		"\2\2\2\u01cd\u01ce\3\2\2\2\u01ce\u01cc\3\2\2\2\u01ce\u01cf\3\2\2\2\u01cf"+
		"\u008e\3\2\2\2\u01d0\u01d3\n\16\2\2\u01d1\u01d3\5\u0091I\2\u01d2\u01d0"+
		"\3\2\2\2\u01d2\u01d1\3\2\2\2\u01d3\u0090\3\2\2\2\u01d4\u01d5\7^\2\2\u01d5"+
		"\u01d9\t\17\2\2\u01d6\u01d9\5\u0093J\2\u01d7\u01d9\5\u0095K\2\u01d8\u01d4"+
		"\3\2\2\2\u01d8\u01d6\3\2\2\2\u01d8\u01d7\3\2\2\2\u01d9\u0092\3\2\2\2\u01da"+
		"\u01db\7^\2\2\u01db\u01e6\5c\62\2\u01dc\u01dd\7^\2\2\u01dd\u01de\5c\62"+
		"\2\u01de\u01df\5c\62\2\u01df\u01e6\3\2\2\2\u01e0\u01e1\7^\2\2\u01e1\u01e2"+
		"\5\u0097L\2\u01e2\u01e3\5c\62\2\u01e3\u01e4\5c\62\2\u01e4\u01e6\3\2\2"+
		"\2\u01e5\u01da\3\2\2\2\u01e5\u01dc\3\2\2\2\u01e5\u01e0\3\2\2\2\u01e6\u0094"+
		"\3\2\2\2\u01e7\u01e8\7^\2\2\u01e8\u01e9\7w\2\2\u01e9\u01ea\5[.\2\u01ea"+
		"\u01eb\5[.\2\u01eb\u01ec\5[.\2\u01ec\u01ed\5[.\2\u01ed\u0096\3\2\2\2\u01ee"+
		"\u01ef\t\20\2\2\u01ef\u0098\3\2\2\2\u01f0\u01f1\7p\2\2\u01f1\u01f2\7w"+
		"\2\2\u01f2\u01f3\7n\2\2\u01f3\u01f4\7n\2\2\u01f4\u009a\3\2\2\2\u01f5\u01f9"+
		"\5\u009dO\2\u01f6\u01f8\5\u009fP\2\u01f7\u01f6\3\2\2\2\u01f8\u01fb\3\2"+
		"\2\2\u01f9\u01f7\3\2\2\2\u01f9\u01fa\3\2\2\2\u01fa\u009c\3\2\2\2\u01fb"+
		"\u01f9\3\2\2\2\u01fc\u0203\t\21\2\2\u01fd\u01fe\n\22\2\2\u01fe\u0203\6"+
		"O\2\2\u01ff\u0200\t\23\2\2\u0200\u0201\t\24\2\2\u0201\u0203\6O\3\2\u0202"+
		"\u01fc\3\2\2\2\u0202\u01fd\3\2\2\2\u0202\u01ff\3\2\2\2\u0203\u009e\3\2"+
		"\2\2\u0204\u020b\t\25\2\2\u0205\u0206\n\22\2\2\u0206\u020b\6P\4\2\u0207"+
		"\u0208\t\23\2\2\u0208\u0209\t\24\2\2\u0209\u020b\6P\5\2\u020a\u0204\3"+
		"\2\2\2\u020a\u0205\3\2\2\2\u020a\u0207\3\2\2\2\u020b\u00a0\3\2\2\2\u020c"+
		"\u020e\t\26\2\2\u020d\u020c\3\2\2\2\u020e\u020f\3\2\2\2\u020f\u020d\3"+
		"\2\2\2\u020f\u0210\3\2\2\2\u0210\u0211\3\2\2\2\u0211\u0212\bQ\2\2\u0212"+
		"\u00a2\3\2\2\2\u0213\u0214\7\61\2\2\u0214\u0215\7,\2\2\u0215\u0219\3\2"+
		"\2\2\u0216\u0218\13\2\2\2\u0217\u0216\3\2\2\2\u0218\u021b\3\2\2\2\u0219"+
		"\u021a\3\2\2\2\u0219\u0217\3\2\2\2\u021a\u021c\3\2\2\2\u021b\u0219\3\2"+
		"\2\2\u021c\u021d\7,\2\2\u021d\u021e\7\61\2\2\u021e\u021f\3\2\2\2\u021f"+
		"\u0220\bR\2\2\u0220\u00a4\3\2\2\2\u0221\u0222\7\61\2\2\u0222\u0223\7\61"+
		"\2\2\u0223\u0227\3\2\2\2\u0224\u0226\n\27\2\2\u0225\u0224\3\2\2\2\u0226"+
		"\u0229\3\2\2\2\u0227\u0225\3\2\2\2\u0227\u0228\3\2\2\2\u0228\u022a\3\2"+
		"\2\2\u0229\u0227\3\2\2\2\u022a\u022b\bS\2\2\u022b\u00a6\3\2\2\2\64\2\u00f1"+
		"\u00f5\u00f9\u00fd\u0101\u0108\u010d\u010f\u0115\u0119\u011d\u0123\u0128"+
		"\u0132\u0136\u013c\u0140\u0148\u014c\u0152\u015c\u0160\u0166\u016a\u016f"+
		"\u0172\u0175\u017a\u017d\u0182\u0187\u018f\u019a\u019e\u01a3\u01a7\u01b7"+
		"\u01c1\u01c7\u01ce\u01d2\u01d8\u01e5\u01f9\u0202\u020a\u020f\u0219\u0227"+
		"\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}