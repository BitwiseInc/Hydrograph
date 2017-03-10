grammar QueryParser;

eval
:
     leftBrace* expression
     (
          andOr leftBrace* expression rightBrace*
     )* rightBrace*
     | eval ';'
;

expression
:
     fieldname
     (
          condition
          (
              javaiden
              | fieldname
          )
     )?
     (
          specialexpr
     )?
;

leftBrace
:
     '('
;

rightBrace
:
     ')'
;

specialexpr
:
     'in' leftBrace javaiden rightBrace
     | 'not in' leftBrace javaiden rightBrace
     | 'between' (javaiden|fieldname) andOr (javaiden|fieldname)
     | 'like' leftBrace? javaiden rightBrace?
     | 'not like' leftBrace? javaiden rightBrace?
     | 'IN' leftBrace javaiden rightBrace
     | 'NOT IN' leftBrace javaiden rightBrace
     | 'BETWEEN' javaiden andOr javaiden
     | 'LIKE' javaiden
     | 'NOT LIKE' javaiden
;

andOr
:
     ' or '
     | ' and '
     | ' OR '
     | ' AND '
;

condition
:
     '='
     | '<'
     | '<='
     | '>'
     | '>='
     | '<>'
;

javaiden
:
     Identifier+
;

fieldname
:
     FieldIdentifier
;

FieldIdentifier
:
     JavaLetter JavaLetterOrDigit*
;

Identifier
:
     Val+
;

fragment
Val
:
     [a-zA-Z0-9-.$_,%@:'\s]
;

fragment
JavaLetter
:
     [a-zA-Z$_] // these are the "java letters" below 0x7F

     | // covers all characters above 0x7F which are not a surrogate
     ~[\u0000-\u007F\uD800-\uDBFF]
     {Character.isJavaIdentifierStart(_input.LA(-1))}?

     | // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
     [\uD800-\uDBFF] [\uDC00-\uDFFF]
     {Character.isJavaIdentifierStart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?

;

fragment
JavaLetterOrDigit
:
     [a-zA-Z0-9$_] // these are the "java letters or digits" below 0x7F

     | // covers all characters above 0x7F which are not a surrogate
     ~[\u0000-\u007F\uD800-\uDBFF]
     {Character.isJavaIdentifierPart(_input.LA(-1))}?

     | // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
     [\uD800-\uDBFF] [\uDC00-\uDFFF]
     {Character.isJavaIdentifierPart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?

;

WS
:
     [ \t\r\n\u000C]+ -> skip
;
