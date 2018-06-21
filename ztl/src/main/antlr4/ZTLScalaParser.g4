parser grammar ZTLScalaParser;
options { tokenVocab=ZTLScalaLexer; }

literal
  : BOOL_LITERAL
  | PRE_STRING
  | STRING
  | numericLiteral
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
  | functionCall
  ;

functionCall
  : (variable | methodDeclarator | newObject) ((WS | NEWLINE)* '.' (variable | methodDeclarator))*
  ;

variable
  : Identifier
  ;

methodDeclarator
  : Identifier '(' formalParameters? ')' type?
  ;

newObject
  : 'new' WS Identifier WS* ('[' WS* Identifier WS* ']' WS*)? '(' formalParameters? ')'
  ;

anonymousFunction
  : '(' formalParameters? ')' (WS | NEWLINE)* '=>' (WS | NEWLINE)* '{' (WS | NEWLINE)* statement+ (WS | NEWLINE)* '}'
  ;

type
  : WS* ':' WS* Identifier WS* ('[' WS* Identifier WS* ']' WS*)?
  ;

formalParameters
  : (WS | NEWLINE)* expression type? (WS | NEWLINE)* (',' (WS | NEWLINE)* expression type?)* (WS | NEWLINE)*
  ;

/*
 * expression
 */

expression
  : unaryExpression
  | conditionalExpression
  | ifElseExpression
  ;

conditionalExpression
  : conditionalOrExpression
  ;

conditionalOrExpression
  : conditionalAndExpression
  | conditionalOrExpression (WS | NEWLINE)* '||' (WS | NEWLINE)* conditionalAndExpression
  ;

conditionalAndExpression
  : equalityExpression
  | conditionalAndExpression (WS | NEWLINE)* '&&' (WS | NEWLINE)* equalityExpression
  ;

equalityExpression
  : relationalExpression
  | equalityExpression (WS | NEWLINE)* '==' (WS | NEWLINE)* relationalExpression
  | equalityExpression (WS | NEWLINE)* '!=' (WS | NEWLINE)* relationalExpression
  ;

relationalExpression
  : additiveExpression
  | relationalExpression (WS | NEWLINE)* '<' (WS | NEWLINE)* additiveExpression
  | relationalExpression (WS | NEWLINE)* '>' (WS | NEWLINE)* additiveExpression
  | relationalExpression (WS | NEWLINE)* '<=' (WS | NEWLINE)* additiveExpression
  | relationalExpression (WS | NEWLINE)* '>=' (WS | NEWLINE)* additiveExpression
  ;

additiveExpression
  : multiplicativeExpression
  | additiveExpression (WS | NEWLINE)* '+' (WS | NEWLINE)* multiplicativeExpression
  | additiveExpression (WS | NEWLINE)* '-' (WS | NEWLINE)* multiplicativeExpression
  ;

multiplicativeExpression
  : unaryExpression
  | multiplicativeExpression (WS | NEWLINE)* '*' (WS | NEWLINE)* unaryExpression
  | multiplicativeExpression (WS | NEWLINE)* '/' (WS | NEWLINE)* unaryExpression
  | multiplicativeExpression (WS | NEWLINE)* '%' (WS | NEWLINE)* unaryExpression
  ;

unaryExpression
  : '~' WS* unaryExpression
  | '!' WS* unaryExpression
  | specialMethod
  | primary
  | '(' expression ')'
  ;

specialMethod
  : wrapMethod
  | parseMethod
  | ztlUnitMethod
  | ztlTestMethod
  ;

wrapMethod
  : equalsMethod
  | containsMethod
  ;

equalsMethod
  : primary '.' EQUALS '(' expression ')'
  ;

containsMethod
  : primary '.' CONTAINS '(' expression ')'
  ;

parseMethod
  : toIntMethod
  | toDoubleMethod
  | toFloatMethod
  | toBooleanMethod
  ;

toIntMethod
  : primary '.' 'toInt'
  | 'parseInt' '(' expression ')'
  ;

toDoubleMethod
  : primary '.' 'toDouble'
  | 'parseDouble' '(' expression ')'
  ;

toFloatMethod
  : primary '.' 'toFloat'
  | 'parseFloat' '(' expression ')'
  ;

toBooleanMethod
  : 'parseBoolean' '(' expression ')'
  ;

ztlUnitMethod
  : primary '.' 'height' ('(' ')')?
  | primary '.' 'width' ('(' ')')?
  | primary '.' 'innerHeight' ('(' ')')?
  | primary '.' 'innerWidth' ('(' ')')?
  | primary '.' 'outerWidth' ('(' BOOL_LITERAL? ')')?
  | primary '.' 'outerHeight' ('(' BOOL_LITERAL? ')')?
  | primary '.' 'length' ('(' ')')?
  | primary '.' 'scrollbarWidth' ('(' ')')?
  | primary '.' 'offsetLeft' ('(' ')')?
  | primary '.' 'offsetTop' ('(' ')')?
  | primary '.' 'positionLeft' ('(' ')')?
  | primary '.' 'positionTop' ('(' ')')?
  | primary '.' 'scrollTop' ('(' ')')?
  | primary '.' 'scrollLeft' ('(' ')')?
  | primary '.' 'scrollHeight' ('(' ')')?
  | primary '.' 'scrollWidth' ('(' ')')?
  | primary '.' 'nChildren' ('(' ')')?
  ;

ztlTestMethod
  : ('this' '.')? 'getAlertMessage' ('(' ')')?
  | ('this' '.')? 'hasError' ('(' ')')?
  | ('this' '.')? 'getText' '(' primary ')'
  | ('this' '.')? 'isVisible' '(' primary ')'
  | ('this' '.')? 'hasNativeScroll' '(' primary ')'
  | ('this' '.')? 'hasHScrollbar' '(' primary ')'
  | ('this' '.')? 'hasVScrollbar' '(' primary ')'
  | ('this' '.')? 'getScrollTop' '(' primary ')'
  | ('this' '.')? 'getScrollLeft' '(' primary ')'
  | ('this' '.')? 'getZKLog' '(' primary ')'
  | ('this' '.')? 'is' '(' primary ')'
  | ('this' '.')? 'getWindowWidth' ('(' ')')?
  | ('this' '.')? 'getWindowHeight' ('(' ')')?
  | ('this' '.')? 'getBrowserTabCount' ('(' ')')?
  ;

ifElseExpression
  : 'if' WS conditionalExpression WS 'else' WS conditionalExpression
  ;

statement
  : ifThenStatement
  | ifThenElseStatement
  | forStatement
  | whileStatement
  | otherStatement
  ;

ifThenStatement
  : WS* 'if' WS* '(' WS* expression WS* ')' (WS | NEWLINE)* conditionBodyStatements
  ;

elseStatement
  : (WS | NEWLINE)* 'else' (WS | NEWLINE)* conditionBodyStatements
  ;

elseIfStatement
  : (WS | NEWLINE)* 'else' WS 'if' (WS | NEWLINE)* conditionBodyStatements
  ;

ifThenElseStatement
  : ifThenStatement ifThenElseStatement* elseStatement*
  ;

forStatement
  : WS* 'for' WS* '(' forCondition (';' forCondition)* (WS | NEWLINE)* ')' (WS | NEWLINE)* conditionBodyStatements
  ;

forCondition
  : WS* primary WS* '<-' WS* expression WS* ('until' | 'to') WS* expression (WS | NEWLINE)*
  ;

whileStatement
  : WS* 'while' WS* '(' WS* expression WS* ')' (WS | NEWLINE)* conditionBodyStatements
  ;

conditionBodyStatements
  : (WS | NEWLINE)* '{' (WS | NEWLINE)* (statement)+ (WS | NEWLINE)* '}' (WS | NEWLINE)*
  | (WS | NEWLINE)* statement (WS | NEWLINE)* //one-line
  ;

classStatement
  :	WS* 'class' WS Identifier (WS? '(' formalParameters? ')' WS?)? (WS 'extends' WS Identifier)? (WS | NEWLINE)* '{' (WS | NEWLINE)* statement+ (WS | NEWLINE)* '}'
  ;

defStatement
  :	defInfo '='? (WS | NEWLINE)* '{' (WS | NEWLINE)* statement+ (WS | NEWLINE)* '}'
  |	defInfo '='? (WS | NEWLINE)* expression //one-line
  ;

defInfo
  : WS* 'def' WS methodDeclarator WS*
  ;

assignmentStatement
  : WS* anyType WS Identifier type? WS* '=' (WS | NEWLINE)* expression
  | WS* primary WS* '=' (WS | NEWLINE)* expression WS*
  ;

anyType
  : 'val'
  | 'var'
  ;

functionCallStatement
  : WS* verifyMethod WS*
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

returnStatement
  : WS* 'return' WS expression WS*
  ;

otherStatement
  :	classStatement WS* NEWLINE?
  |	defStatement WS* ';'? NEWLINE
  | singleStatement WS* ';'? NEWLINE
  |	(WS | ';' )* NEWLINE //block or empty
  ;

singleStatement
  : assignmentStatement
  | functionCallStatement
  | returnStatement
  ;

statements
  : statement+ NEWLINE? EOF
  ;