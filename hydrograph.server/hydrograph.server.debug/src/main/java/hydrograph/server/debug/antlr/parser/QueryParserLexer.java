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
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class QueryParserLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, FieldIdentifier=24, 
		Identifier=25, WS=26;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
		"T__9", "T__10", "T__11", "T__12", "T__13", "T__14", "T__15", "T__16", 
		"T__17", "T__18", "T__19", "T__20", "T__21", "T__22", "FieldIdentifier", 
		"Identifier", "Val", "JavaLetter", "JavaLetterOrDigit", "WS"
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


	public QueryParserLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "QueryParser.g4"; }

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
		case 26:
			return JavaLetter_sempred((RuleContext)_localctx, predIndex);
		case 27:
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\34\u00cd\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\3\2\3\2\3\3\3"+
		"\3\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7"+
		"\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3"+
		"\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f"+
		"\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3"+
		"\16\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3"+
		"\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3"+
		"\25\3\25\3\26\3\26\3\27\3\27\3\27\3\30\3\30\3\30\3\31\3\31\7\31\u00ab"+
		"\n\31\f\31\16\31\u00ae\13\31\3\32\6\32\u00b1\n\32\r\32\16\32\u00b2\3\33"+
		"\3\33\3\34\3\34\3\34\3\34\3\34\3\34\5\34\u00bd\n\34\3\35\3\35\3\35\3\35"+
		"\3\35\3\35\5\35\u00c5\n\35\3\36\6\36\u00c8\n\36\r\36\16\36\u00c9\3\36"+
		"\3\36\2\2\37\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16"+
		"\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\2\67"+
		"\29\2;\34\3\2\t\n\2&\')).\60\62<B\\^^aac|\6\2&&C\\aac|\4\2\2\u0081\ud802"+
		"\udc01\3\2\ud802\udc01\3\2\udc02\ue001\7\2&&\62;C\\aac|\5\2\13\f\16\17"+
		"\"\"\u00d0\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2"+
		"\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27"+
		"\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2"+
		"\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2"+
		"\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2;\3\2\2\2\3=\3\2\2\2\5?\3\2\2\2"+
		"\7A\3\2\2\2\tC\3\2\2\2\13F\3\2\2\2\rM\3\2\2\2\17U\3\2\2\2\21Z\3\2\2\2"+
		"\23c\3\2\2\2\25f\3\2\2\2\27m\3\2\2\2\31u\3\2\2\2\33z\3\2\2\2\35\u0083"+
		"\3\2\2\2\37\u0088\3\2\2\2!\u008e\3\2\2\2#\u0093\3\2\2\2%\u0099\3\2\2\2"+
		"\'\u009b\3\2\2\2)\u009d\3\2\2\2+\u00a0\3\2\2\2-\u00a2\3\2\2\2/\u00a5\3"+
		"\2\2\2\61\u00a8\3\2\2\2\63\u00b0\3\2\2\2\65\u00b4\3\2\2\2\67\u00bc\3\2"+
		"\2\29\u00c4\3\2\2\2;\u00c7\3\2\2\2=>\7=\2\2>\4\3\2\2\2?@\7*\2\2@\6\3\2"+
		"\2\2AB\7+\2\2B\b\3\2\2\2CD\7k\2\2DE\7p\2\2E\n\3\2\2\2FG\7p\2\2GH\7q\2"+
		"\2HI\7v\2\2IJ\7\"\2\2JK\7k\2\2KL\7p\2\2L\f\3\2\2\2MN\7d\2\2NO\7g\2\2O"+
		"P\7v\2\2PQ\7y\2\2QR\7g\2\2RS\7g\2\2ST\7p\2\2T\16\3\2\2\2UV\7n\2\2VW\7"+
		"k\2\2WX\7m\2\2XY\7g\2\2Y\20\3\2\2\2Z[\7p\2\2[\\\7q\2\2\\]\7v\2\2]^\7\""+
		"\2\2^_\7n\2\2_`\7k\2\2`a\7m\2\2ab\7g\2\2b\22\3\2\2\2cd\7K\2\2de\7P\2\2"+
		"e\24\3\2\2\2fg\7P\2\2gh\7Q\2\2hi\7V\2\2ij\7\"\2\2jk\7K\2\2kl\7P\2\2l\26"+
		"\3\2\2\2mn\7D\2\2no\7G\2\2op\7V\2\2pq\7Y\2\2qr\7G\2\2rs\7G\2\2st\7P\2"+
		"\2t\30\3\2\2\2uv\7N\2\2vw\7K\2\2wx\7M\2\2xy\7G\2\2y\32\3\2\2\2z{\7P\2"+
		"\2{|\7Q\2\2|}\7V\2\2}~\7\"\2\2~\177\7N\2\2\177\u0080\7K\2\2\u0080\u0081"+
		"\7M\2\2\u0081\u0082\7G\2\2\u0082\34\3\2\2\2\u0083\u0084\7\"\2\2\u0084"+
		"\u0085\7q\2\2\u0085\u0086\7t\2\2\u0086\u0087\7\"\2\2\u0087\36\3\2\2\2"+
		"\u0088\u0089\7\"\2\2\u0089\u008a\7c\2\2\u008a\u008b\7p\2\2\u008b\u008c"+
		"\7f\2\2\u008c\u008d\7\"\2\2\u008d \3\2\2\2\u008e\u008f\7\"\2\2\u008f\u0090"+
		"\7Q\2\2\u0090\u0091\7T\2\2\u0091\u0092\7\"\2\2\u0092\"\3\2\2\2\u0093\u0094"+
		"\7\"\2\2\u0094\u0095\7C\2\2\u0095\u0096\7P\2\2\u0096\u0097\7F\2\2\u0097"+
		"\u0098\7\"\2\2\u0098$\3\2\2\2\u0099\u009a\7?\2\2\u009a&\3\2\2\2\u009b"+
		"\u009c\7>\2\2\u009c(\3\2\2\2\u009d\u009e\7>\2\2\u009e\u009f\7?\2\2\u009f"+
		"*\3\2\2\2\u00a0\u00a1\7@\2\2\u00a1,\3\2\2\2\u00a2\u00a3\7@\2\2\u00a3\u00a4"+
		"\7?\2\2\u00a4.\3\2\2\2\u00a5\u00a6\7>\2\2\u00a6\u00a7\7@\2\2\u00a7\60"+
		"\3\2\2\2\u00a8\u00ac\5\67\34\2\u00a9\u00ab\59\35\2\u00aa\u00a9\3\2\2\2"+
		"\u00ab\u00ae\3\2\2\2\u00ac\u00aa\3\2\2\2\u00ac\u00ad\3\2\2\2\u00ad\62"+
		"\3\2\2\2\u00ae\u00ac\3\2\2\2\u00af\u00b1\5\65\33\2\u00b0\u00af\3\2\2\2"+
		"\u00b1\u00b2\3\2\2\2\u00b2\u00b0\3\2\2\2\u00b2\u00b3\3\2\2\2\u00b3\64"+
		"\3\2\2\2\u00b4\u00b5\t\2\2\2\u00b5\66\3\2\2\2\u00b6\u00bd\t\3\2\2\u00b7"+
		"\u00b8\n\4\2\2\u00b8\u00bd\6\34\2\2\u00b9\u00ba\t\5\2\2\u00ba\u00bb\t"+
		"\6\2\2\u00bb\u00bd\6\34\3\2\u00bc\u00b6\3\2\2\2\u00bc\u00b7\3\2\2\2\u00bc"+
		"\u00b9\3\2\2\2\u00bd8\3\2\2\2\u00be\u00c5\t\7\2\2\u00bf\u00c0\n\4\2\2"+
		"\u00c0\u00c5\6\35\4\2\u00c1\u00c2\t\5\2\2\u00c2\u00c3\t\6\2\2\u00c3\u00c5"+
		"\6\35\5\2\u00c4\u00be\3\2\2\2\u00c4\u00bf\3\2\2\2\u00c4\u00c1\3\2\2\2"+
		"\u00c5:\3\2\2\2\u00c6\u00c8\t\b\2\2\u00c7\u00c6\3\2\2\2\u00c8\u00c9\3"+
		"\2\2\2\u00c9\u00c7\3\2\2\2\u00c9\u00ca\3\2\2\2\u00ca\u00cb\3\2\2\2\u00cb"+
		"\u00cc\b\36\2\2\u00cc<\3\2\2\2\b\2\u00ac\u00b2\u00bc\u00c4\u00c9\3\b\2"+
		"\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}