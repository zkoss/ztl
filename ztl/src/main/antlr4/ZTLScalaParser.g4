parser grammar ZTLScalaParser;
options { tokenVocab=ZTLScalaLexer; }

literal
	: BOOL_LITERAL
	| stringLiteral
	| '-'? numericLiteral
	;

stringLiteral
	: PRE_STRING
	| STRING
	;

numericLiteral
	: integerLiteral
	| floatLiteral
	| NULL_LITERAL
	;

integerLiteral
	: DECIMAL_LITERAL
	| HEX_LITERAL
	| OCT_LITERAL
	| BINARY_LITERAL
	;

floatLiteral
	: FLOAT_LITERAL
	| HEX_FLOAT_LITERAL
	;

primary //propertyAccess
	: literal
	| anonymousFunction
	| newObject
	| functionCall
	;

returnOrSpace
	: (WS | NEWLINE)
	;
	
functionCall
	: methodDeclarator
	| ('this' | variable | methodDeclarator | newObject | stringLiteral) (returnOrSpace* '.' (variable | methodDeclarator))*
	| '(' WS* ('this' | variable | methodDeclarator | newObject | stringLiteral) WS* ')' (returnOrSpace* '.' (variable | methodDeclarator))*
	;

variable
	: Identifier
	;

methodDeclarator
	: Identifier typeDetail? WS* '(' formalParameters? ')' type?
	;

newObject
	: 'new' WS Identifier ('.' Identifier)* WS* typeDetail? ('(' formalParameters? ')')?
	;

anonymousFunction
	: '(' formalParameters? ')' returnOrSpace* '=>' returnOrSpace* block
	;

type
	: WS* ':' WS* Identifier ('.' Identifier)* WS* typeDetail?
	| WS* ':' WS* '(' formalParameters? ')' WS* '=>' WS* Identifier WS*
	;

typeDetail
	: ('[' WS* Identifier WS* (',' WS* Identifier WS*)*']' WS*)
	;

formalParameters
	: returnOrSpace* expression type? (returnOrSpace* ',' returnOrSpace* expression type?)* returnOrSpace*
	;

/*
 * expression
 */

expression
	: unaryExpression
	| conditionalExpression
	;

conditionalExpression
	: conditionalOrExpression
	;

conditionalOrExpression
	: conditionalAndExpression
	| conditionalOrExpression returnOrSpace* '||' returnOrSpace* conditionalAndExpression
	;

conditionalAndExpression
	: equalityExpression
	| conditionalAndExpression returnOrSpace* '&&' returnOrSpace* equalityExpression
	;

equalityExpression
	: relationalExpression
	| equalityExpression returnOrSpace* '==' returnOrSpace* relationalExpression
	| equalityExpression returnOrSpace* '!=' returnOrSpace* relationalExpression
	;

relationalExpression
	: additiveExpression
	| relationalExpression returnOrSpace* '<' returnOrSpace* additiveExpression
	| relationalExpression returnOrSpace* '>' returnOrSpace* additiveExpression
	| relationalExpression returnOrSpace* '<=' returnOrSpace* additiveExpression
	| relationalExpression returnOrSpace* '>=' returnOrSpace* additiveExpression
	;

additiveExpression
	: multiplicativeExpression
	| additiveExpression returnOrSpace* '+' returnOrSpace* multiplicativeExpression
	| additiveExpression returnOrSpace* '-' returnOrSpace* multiplicativeExpression
	;

multiplicativeExpression
	: unaryExpression
	| multiplicativeExpression returnOrSpace* '*' returnOrSpace* unaryExpression
	| multiplicativeExpression returnOrSpace* '/' returnOrSpace* unaryExpression
	| multiplicativeExpression returnOrSpace* '%' returnOrSpace* unaryExpression
	;

unaryExpression
	: '~' WS* unaryExpression
	| '!' WS* unaryExpression
	| specialMethod
	| primary
	| '(' expression ')'
	;

specialMethod
	: parseMethod
	| ztlUnitMethod
	| ztlTestMethod
	;

parseMethod
	: toIntMethod
	| toDoubleMethod
	| toFloatMethod
	| toBooleanMethod
	;

toIntMethod
	: primary '.' 'toInt'
	| ztlUnitMethod '.' 'toInt'
	| thisDot? ztlTestEvalMethod '.' 'toInt'
	| 'parseInt' '(' expression ')'
	;

toDoubleMethod
	: primary '.' 'toDouble'
	| ztlUnitMethod '.' 'toDouble'
	| thisDot? ztlTestEvalMethod '.' 'toDouble'
	| 'parseDouble' '(' expression ')'
	;

toFloatMethod
	: primary '.' 'toFloat'
	| ztlUnitMethod '.' 'toFloat'
	| thisDot? ztlTestEvalMethod '.' 'toFloat'
	| 'parseFloat' '(' expression ')'
	;

toBooleanMethod
	: 'parseBoolean' '(' expression ')'
	;

ztlUnitMethod
	: primary returnOrSpace* '.css' '(' expression ')'
	| primary returnOrSpace* '.attr' '(' expression ')'
	| primary returnOrSpace* '.hasClass' '(' expression ')'
	| primary returnOrSpace* '.text' ('(' ')')?
	| primary returnOrSpace* '.html' ('(' ')')?
	| primary returnOrSpace* '.`val`' ('(' ')')?
	| primary returnOrSpace* '.uuid' ('(' ')')?
	| primary returnOrSpace* '.id' ('(' ')')?
	| primary returnOrSpace* '.is' '(' expression ')'
	| primary returnOrSpace* '.height' ('(' ')')?
	| primary returnOrSpace* '.width' ('(' ')')?
	| primary returnOrSpace* '.innerHeight' ('(' ')')?
	| primary returnOrSpace* '.innerWidth' ('(' ')')?
	| primary returnOrSpace* '.outerWidth' ('(' expression? ')')?
	| primary returnOrSpace* '.outerHeight' ('(' expression? ')')?
	| primary returnOrSpace* '.length' ('(' ')')?
	| primary returnOrSpace* '.scrollbarWidth' ('(' ')')?
	| primary returnOrSpace* '.offsetLeft' ('(' ')')?
	| primary returnOrSpace* '.offsetTop' ('(' ')')?
	| primary returnOrSpace* '.positionLeft' ('(' ')')?
	| primary returnOrSpace* '.positionTop' ('(' ')')?
	| primary returnOrSpace* '.scrollTop' ('(' ')')?
	| primary returnOrSpace* '.scrollLeft' ('(' ')')?
	| primary returnOrSpace* '.scrollHeight' ('(' ')')?
	| primary returnOrSpace* '.scrollWidth' ('(' ')')?
	| primary returnOrSpace* '.nChildren' ('(' ')')?
	| primary returnOrSpace* '.isVisible' ('(' ')')?
	| primary returnOrSpace* '.exists' ('(' ')')?
	| ztlUnitEvalMethod
	;

ztlUnitEvalMethod
	: primary returnOrSpace* '.eval' '(' expression ')'
	;

ztlTestMethod
	: thisDot? 'getAlertMessage' ('(' ')')?
	| thisDot? 'hasError' ('(' ')')?
	| thisDot? 'getText' '(' expression ')'
	| thisDot? 'isVisible' '(' expression ')'
	| thisDot? 'hasNativeScroll' '(' expression ')'
	| thisDot? 'hasHScrollbar' '(' expression ')'
	| thisDot? 'hasVScrollbar' '(' expression ')'
	| thisDot? 'getScrollTop' '(' expression ')'
	| thisDot? 'getScrollLeft' '(' expression ')'
	| thisDot? 'getZKLog' ('(' ')')?
	| thisDot? 'is' '(' expression ')'
	| thisDot? 'getWindowWidth' ('(' ')')?
	| thisDot? 'getWindowHeight' ('(' ')')?
	| thisDot? ztlTestEvalMethod
	;

ztlTestEvalMethod
	: 'getEval' '(' expression ')'
	;
	
thisDot
	: 'this' returnOrSpace* '.'
	;

statement
	: conditionStatement
	| otherStatement
	;

conditionStatement
	: ifThenStatement
	| elseIfStatement
	| elseStatement
	| forStatement
	| whileStatement
	;

ifThenStatement
	: WS* ifExpression returnOrSpace* conditionBodyStatements
	;

elseStatement
	: returnOrSpace* elseExpression conditionBodyStatements
	;

elseIfStatement
	: returnOrSpace* elseIfExpression returnOrSpace* conditionBodyStatements
	;

forStatement
	: WS* forExpression returnOrSpace* conditionBodyStatements
	;

whileStatement
	: WS* whileExpression returnOrSpace* conditionBodyStatements
	;

ifExpression
	: 'if' WS* '(' WS* expression WS* ')'
	;

elseExpression
	: 'else' returnOrSpace*
	;

elseIfExpression
	: 'else' WS 'if' WS* '(' WS* expression WS* ')'
	;

forExpression
	: 'for' WS* '(' forCondition (';' forCondition)* returnOrSpace* ')'
	;

forCondition
	: WS* primary type? WS* '<-' WS* expression WS* ('until' | 'to') WS* expression returnOrSpace*
	| WS* variable type? WS* '<-' WS* expression returnOrSpace*
	;

whileExpression
	: 'while' WS* '(' WS* expression WS* ')'
	;

conditionBodyStatements
	: returnOrSpace* block returnOrSpace*
	| returnOrSpace* statement returnOrSpace* //one-line
	;

block
	: block_pre (statement)+ block_suf
	;

block_pre
	: '{' returnOrSpace*
	;

block_suf
	: returnOrSpace* '}'
	;

classStatement
	:	returnOrSpace* 'class' WS Identifier (WS? '(' formalParameters? ')' WS?)? (WS 'extends' WS Identifier)? returnOrSpace* '{' returnOrSpace* statement+ returnOrSpace* '}'
	;

defStatement
	:	defInfo '='? returnOrSpace* '{' returnOrSpace* statement+ returnOrSpace* '}'
	|	defInfo '='? returnOrSpace* expression //one-line
	| defInfo '=' WS* anonymousFunction
	;

defInfo
	: WS* 'def' WS methodDeclarator WS*
	| WS* 'def' WS Identifier type? WS*
	;

assignmentStatement
	: WS* anyType WS Identifier type? (WS* ',' WS* Identifier)* WS* '=' returnOrSpace* expression
	| WS* Identifier WS* assignmentOperator returnOrSpace* expression WS*
	;

assignmentOperator
	: '='
	| '*='
	| '/='
	| '%='
	| '+='
	| '-='
	;

anyType
	: 'val'
	| 'var'
	;

functionCallStatement
	: WS* thisDot? verifyMethod WS*
	| WS* thisDot? ztlActionMethod WS*
	| WS* thisDot? ztlTestEvalMethod WS*
	|	WS* ztlUnitEvalMethod WS*
	| WS* functionCall WS*
	;

verifyMethod
	: 'verifyContains' '(' formalParameters ')'
	| 'verifyNotContains' '(' formalParameters ')'
	| 'verifyEquals' '(' formalParameters ')'
	| 'verifyNotEquals' '(' formalParameters ')'
	| 'verifyTrue' '(' formalParameters ')'
	| 'verifyFalse' '(' formalParameters ')'
	| 'verifyTolerant' '(' formalParameters ')'
	;

//handle action param
ztlActionMethod	
	: 'clickAt' ('(' formalParameters ')')
	| 'contextMenuAt' ('(' formalParameters ')')
	| 'doubleClickAt' ('(' formalParameters ')')
	| 'dragAndDrop' ('(' formalParameters ')')
	| 'dragdropTo' ('(' formalParameters ')')
	| 'dragdropToObject' ('(' formalParameters ')')
	| 'dragAndDropToObject' ('(' formalParameters ')')
	| 'dragdrop' ('(' formalParameters ')')
	| 'mouseDownAt' ('(' formalParameters ')')
	| 'verScroll' '(' formalParameters ')'
	| 'verScrollAbs' '(' formalParameters ')'
	| 'verScrollNoBody' '(' formalParameters ')'
	| 'verScrollNoBodyAbs' '(' formalParameters ')'
	| 'horScroll' '(' formalParameters ')'
	| 'horScrollAbs' '(' formalParameters ')'
	| 'horScrollNoBody' '(' formalParameters ')'
	| 'horScrollNoBodyAbs' '(' formalParameters ')'
	;

returnStatement
	: WS* 'return' WS expression WS*
	;

annotionStatement
	: WS* '@' Identifier WS*
	;

otherStatement
	: classStatement WS* NEWLINE?
	| defStatement WS* (';' WS*)? NEWLINE
	| assignmentStatement WS* (';' WS*)? NEWLINE
	| functionCallStatement WS* (';' WS*)? NEWLINE
	| returnStatement WS* (';' WS*)? NEWLINE
	| annotionStatement WS* NEWLINE
	| (WS | ';' WS*)* NEWLINE //block or empty
	;

statements
	: statement+ NEWLINE? EOF
	;