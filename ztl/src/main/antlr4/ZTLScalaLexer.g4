lexer grammar ZTLScalaLexer;
// Keywords
ELSE:			'else';
FOR:			'for';
IF:				'if';
VAL:			'val';
VAR:			'var';
WHILE:			'while';
CLASS:			'class';
DEF:			'def';
EXTENDS:		'extends';
NEW:			'new';
RETURN:			'return';

//special method
EQUALS:			'equals';
CONTAINS:		'contains';
TOINT:			'toInt';
TODOUBLE:		'toDouble';
TOFLOAT:		'toFloat';

// Literals
DECIMAL_LITERAL:('0' | [1-9] (Digits? | '_'+ Digits)) [lL]?;
HEX_LITERAL:	'0' [xX] [0-9a-fA-F] ([0-9a-fA-F_]* [0-9a-fA-F])? [lL]?;
OCT_LITERAL:	'0' '_'* [0-7] ([0-7_]* [0-7])? [lL]?;
BINARY_LITERAL:	'0' [bB] [01] ([01_]* [01])? [lL]?;
FLOAT_LITERAL:	(Digits '.' Digits? | '.' Digits) ExponentPart? [fFdD]? | Digits (ExponentPart [fFdD]? | [fFdD]);
HEX_FLOAT_LITERAL:'0' [xX] (HexDigits '.'? | HexDigits? '.' HexDigits) [pP] [+-]? Digits [fFdD]?;
BOOL_LITERAL:	'true' | 'false';
NULL_LITERAL:	'null';

//§3.11 Separators
LPAREN:			'(';
RPAREN:			')';
LBRACE:			'{';
RBRACE:			'}';
LBRACK:			'[';
RBRACK:			']';
SEMI:			';';
COMMA:			',';
DOT:			'.';
//§3.12 Operators
ASSIGN:			'=';
GT:				'>';
LT:				'<';
BANG:			'!';
TILDE:			'~';
QUESTION:		'?';
COLON:			':';
EQUAL:			'==';
LE:				'<=';
GE:				'>=';
NOTEQUAL:		'!=';
AND:			'&&';
OR:				'||';
ADD:			'+';
SUB:			'-';
MUL:			'*';
DIV:			'/';
CARET:			'^';
MOD:			'%';
ARROW:			'->';
LAMBDA_ARROW:	'<-';
TO:				'to';
UNTIL:			'until';
FUN_LAMBDA:		'=>';

ADD_ASSIGN:		'+=';
SUB_ASSIGN:		'-=';
MUL_ASSIGN:		'*=';
DIV_ASSIGN:		'/=';
MOD_ASSIGN:		'%=';

AT:				'@';
ELLIPSIS:		'...';

NEWLINE:		[\r\n]+;
WS:				[ \t]+;
STRING:			'"' ~["\r\n]* '"';
PRE_STRING:		'"""' (LetterOrDigit | [ \t\r\n])* '"""';
COMMENT:		'/*' .*? '*/' -> channel(HIDDEN);
LINE_COMMENT:	'//' ~[\r\n]* -> channel(HIDDEN);
Identifier:		ScalaLetter ScalaLetterOrDigit*;
//WORD:			Letter LetterOrDigit*;

// Fragment rules
fragment ExponentPart
	: [eE] [+-]? Digits
	;

fragment EscapeSequence
	: '\\' [btnfr"'\\]
	| '\\' ([0-3]? [0-7])? [0-7]
	| '\\' 'u'+ HexDigit HexDigit HexDigit HexDigit
	;

fragment HexDigits
	: HexDigit ((HexDigit | '_')* HexDigit)?
	;

fragment HexDigit
	: [0-9a-fA-F]
	;

fragment Digits
	: [0-9] ([0-9_]* [0-9])?
	;

fragment LetterOrDigit
	: Letter
	| [0-9]
	;

fragment Letter
	: [a-zA-Z]
	| [\u0020-\u002F\u003A-\u0040\u005B-\u0060\u007B-\u007E]
	| ~[\u0000-\u007F\uD800-\uDBFF] // covers all characters above 0x7F which are not a surrogate
	| [\uD800-\uDBFF] [\uDC00-\uDFFF] // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
	;

fragment ScalaLetter
	:	[a-zA-Z$_] // these are the "java letters" below 0x7F
	|	// covers all characters above 0x7F which are not a surrogate
		~[\u0000-\u007F\uD800-\uDBFF]
		{Character.isJavaIdentifierStart(_input.LA(-1))}?
	|	// covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
		[\uD800-\uDBFF] [\uDC00-\uDFFF]
		{Character.isJavaIdentifierStart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
	;

fragment ScalaLetterOrDigit
	:	[a-zA-Z0-9$_] // these are the "java letters or digits" below 0x7F
	|	// covers all characters above 0x7F which are not a surrogate
		~[\u0000-\u007F\uD800-\uDBFF]
		{Character.isJavaIdentifierPart(_input.LA(-1))}?
	|	// covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
		[\uD800-\uDBFF] [\uDC00-\uDFFF]
		{Character.isJavaIdentifierPart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
	;