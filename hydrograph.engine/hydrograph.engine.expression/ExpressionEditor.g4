/********************************************************************************
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
 ******************************************************************************/
grammar ExpressionEditor;


//----------------------> Block
block
    :    blockStatement*
    ;

blockStatement
    :     statement;
//----------------------> Block End



//----------------------> Expression
expression
    :  primary
         |   '(' expression ')'
         |   expression ('++' | '--')
         |   ('+'|'-'|'++'|'--') expression
         |   ('~'|'!') expression
         |   expression  '(' expressionList? ')'
         |   expression ('*'|'/'|'%') expression
         |   expression ('+'|'-') expression
         |   expression ('<' '<' | '>' '>' '>' | '>' '>') expression
         |   expression ('<=' | '>=' | '>' | '<') expression
         |   expression ('==' | '!=') expression
         |   expression '&' expression
         |   expression '^' expression
         |   expression '|' expression
         |   expression '&&' expression
         |   expression '||' expression
         |   expression '?' expression ':' expression
     ;

primary : literal | functions | javaIdentifier ;

expressionList
    :   expression (',' expression)*
    ;

arithmeticOperator : '+'|'-'|'++'|'--';
//----------------------> Expression End.


//----------------------> Statememt
statement
    :    statementExpression (';')?;

statementExpression
    :   expression
    ;

parExpression
    :   '(' expression ')'
    ;
//----------------------> Statememt End.


LPAREN          : '(';
RPAREN          : ')';
DOT             : '.';

functions : javaIdentifier (DOT functions)*
    |literal
    |javaIdentifier
    | 'new'
    | javaIdentifier LPAREN functions? (',' functions)* RPAREN
    ;

 literal
     :   IntegerLiteral
     |   FloatingPointLiteral
     |   CharacterLiteral
     |   StringLiteral
     |   BooleanLiteral
     |   NullLiteral
     ;

// §3.10.1 Integer Literals

IntegerLiteral
    :   DecimalIntegerLiteral
    |   HexIntegerLiteral
    |   OctalIntegerLiteral
    |   BinaryIntegerLiteral
    ;

fragment
DecimalIntegerLiteral
    :   DecimalNumeral IntegerTypeSuffix?
    ;

fragment
HexIntegerLiteral
    :   HexNumeral IntegerTypeSuffix?
    ;

fragment
OctalIntegerLiteral
    :   OctalNumeral IntegerTypeSuffix?
    ;

fragment
BinaryIntegerLiteral
    :   BinaryNumeral IntegerTypeSuffix?
    ;

fragment
IntegerTypeSuffix
    :   [lL]
    ;

fragment
DecimalNumeral
    :   '0'
    |   NonZeroDigit (Digits? | Underscores Digits)
    ;

fragment
Digits
    :   Digit (DigitOrUnderscore* Digit)?
    ;

fragment
Digit
    :   '0'
    |   NonZeroDigit
    ;

fragment
NonZeroDigit
    :   [1-9]
    ;

fragment
DigitOrUnderscore
    :   Digit
    |   '_'
    ;

fragment
Underscores
    :   '_'+
    ;

fragment
HexNumeral
    :   '0' [xX] HexDigits
    ;

fragment
HexDigits
    :   HexDigit (HexDigitOrUnderscore* HexDigit)?
    ;

fragment
HexDigit
    :   [0-9a-fA-F]
    ;

fragment
HexDigitOrUnderscore
    :   HexDigit
    |   '_'
    ;

fragment
OctalNumeral
    :   '0' Underscores? OctalDigits
    ;

fragment
OctalDigits
    :   OctalDigit (OctalDigitOrUnderscore* OctalDigit)?
    ;

fragment
OctalDigit
    :   [0-7]
    ;

fragment
OctalDigitOrUnderscore
    :   OctalDigit
    |   '_'
    ;

fragment
BinaryNumeral
    :   '0' [bB] BinaryDigits
    ;

fragment
BinaryDigits
    :   BinaryDigit (BinaryDigitOrUnderscore* BinaryDigit)?
    ;

fragment
BinaryDigit
    :   [01]
    ;

fragment
BinaryDigitOrUnderscore
    :   BinaryDigit
    |   '_'
    ;

// §3.10.2 Floating-Point Literals

FloatingPointLiteral
    :   DecimalFloatingPointLiteral
    |   HexadecimalFloatingPointLiteral
    ;

fragment
DecimalFloatingPointLiteral
    :   Digits '.' Digits? ExponentPart? FloatTypeSuffix?
    |   '.' Digits ExponentPart? FloatTypeSuffix?
    |   Digits ExponentPart FloatTypeSuffix?
    |   Digits FloatTypeSuffix
    ;

fragment
ExponentPart
    :   ExponentIndicator SignedInteger
    ;

fragment
ExponentIndicator
    :   [eE]
    ;

fragment
SignedInteger
    :   Sign? Digits
    ;

fragment
Sign
    :   [+-]
    ;

fragment
FloatTypeSuffix
    :   [fFdD]
    ;

fragment
HexadecimalFloatingPointLiteral
    :   HexSignificand BinaryExponent FloatTypeSuffix?
    ;

fragment
HexSignificand
    :   HexNumeral '.'?
    |   '0' [xX] HexDigits? '.' HexDigits
    ;

fragment
BinaryExponent
    :   BinaryExponentIndicator SignedInteger
    ;

fragment
BinaryExponentIndicator
    :   [pP]
    ;

// §3.10.3 Boolean Literals

BooleanLiteral
    :   'true'
    |   'false'
    ;

// §3.10.4 Character Literals

CharacterLiteral
    :   '\'' SingleCharacter '\''
    |   '\'' EscapeSequence '\''
    ;

fragment
SingleCharacter
    :   ~['\\]
    ;
// §3.10.5 String Literals
StringLiteral
    :   '"' StringCharacters? '"'
    ;
fragment
StringCharacters
    :   StringCharacter+
    ;
fragment
StringCharacter
    :   ~["\\]
    |   EscapeSequence
    ;
// §3.10.6 Escape Sequences for Character and String Literals
fragment
EscapeSequence
    :   '\\' [btnfr"'\\]
    |   OctalEscape
    |   UnicodeEscape
    ;

fragment
OctalEscape
    :   '\\' OctalDigit
    |   '\\' OctalDigit OctalDigit
    |   '\\' ZeroToThree OctalDigit OctalDigit
    ;

fragment
UnicodeEscape
    :   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;

fragment
ZeroToThree
    :   [0-3]
    ;

// §3.10.7 The Null Literal

NullLiteral
    :   'null';



javaIdentifier: Identifier;

Identifier
         :   JavaLetter JavaLetterOrDigit*
         ;

     fragment
     JavaLetter
         :   [a-zA-Z$_] // these are the "java letters" below 0x7F
         |   // covers all characters above 0x7F which are not a surrogate
             ~[\u0000-\u007F\uD800-\uDBFF]
             {Character.isJavaIdentifierStart(_input.LA(-1))}?
         |   // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
             [\uD800-\uDBFF] [\uDC00-\uDFFF]
             {Character.isJavaIdentifierStart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
         ;

     fragment
     JavaLetterOrDigit
         :   [a-zA-Z0-9$_] // these are the "java letters or digits" below 0x7F
         |   // covers all characters above 0x7F which are not a surrogate
             ~[\u0000-\u007F\uD800-\uDBFF]
             {Character.isJavaIdentifierPart(_input.LA(-1))}?
         |   // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
             [\uD800-\uDBFF] [\uDC00-\uDFFF]
             {Character.isJavaIdentifierPart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
         ;

    // Whitespace and comments
    //

    WS  :  [ \t\r\n\u000C]+ -> skip
        ;

    COMMENT
        :   '/*' .*? '*/' -> skip
        ;

    LINE_COMMENT
        :   '//' ~[\r\n]* -> skip
        ;