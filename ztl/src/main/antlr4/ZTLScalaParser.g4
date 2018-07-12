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
  : '(' formalParameters? ')' (WS | NEWLINE)* '=>' (WS | NEWLINE)* block
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
  | 'parseInt' '(' expression ')'
  ;

toDoubleMethod
  : primary '.' 'toDouble'
  | ztlUnitMethod '.' 'toDouble'
  | 'parseDouble' '(' expression ')'
  ;

toFloatMethod
  : primary '.' 'toFloat'
  | ztlUnitMethod '.' 'toFloat'
  | 'parseFloat' '(' expression ')'
  ;

toBooleanMethod
  : 'parseBoolean' '(' expression ')'
  ;

ztlUnitMethod
  : primary '.' 'css' '(' primary ')'
  | primary '.' 'attr' '(' primary ')'
  | primary '.' 'text' ('(' ')')?
  | primary '.' 'html' ('(' ')')?
  | primary '.' '`val`' ('(' ')')?
  | primary '.' 'uuid' ('(' ')')?
  | primary '.' 'id' ('(' ')')?
  | primary '.' 'is' '(' primary ')'
  | primary '.' 'height' ('(' ')')?
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
  | primary '.' 'isVisible' ('(' ')')?
  | primary '.' 'exist' ('(' ')')?
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
  : WS* ifExpression (WS | NEWLINE)* conditionBodyStatements
  ;

elseStatement
  : (WS | NEWLINE)* 'else' (WS | NEWLINE)* conditionBodyStatements
  ;

elseIfStatement
  : (WS | NEWLINE)* elseIfExpression (WS | NEWLINE)* conditionBodyStatements
  ;

forStatement
  : WS* forExpression (WS | NEWLINE)* conditionBodyStatements
  ;

whileStatement
  : WS* whileExpression (WS | NEWLINE)* conditionBodyStatements
  ;

ifExpression
  : 'if' WS* '(' WS* expression WS* ')'
  ;

elseIfExpression
  : 'else' WS 'if' WS* '(' WS* expression WS* ')'
  ;

forExpression
  : 'for' WS* '(' forCondition (';' forCondition)* (WS | NEWLINE)* ')'
  ;

forCondition
  : WS* primary WS* '<-' WS* expression WS* ('until' | 'to') WS* expression (WS | NEWLINE)*
  ;

whileExpression
  : 'while' WS* '(' WS* expression WS* ')'
  ;

conditionBodyStatements
  : (WS | NEWLINE)* block (WS | NEWLINE)*
  | (WS | NEWLINE)* statement (WS | NEWLINE)* //one-line
  ;

block
  : block_pre (statement)+ block_suf
  ;

block_pre
  : '{' (WS | NEWLINE)*
  ;

block_suf
  : (WS | NEWLINE)* '}'
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
  | WS* primary WS* (otherAssignmentSymbol | '=') (WS | NEWLINE)* expression WS*
  ;

otherAssignmentSymbol
  : ('+' | '-' | '*' | '/') '='
  ;

anyType
  : 'val'
  | 'var'
  ;

functionCallStatement
  : WS* ('this' '.')? verifyMethod WS*
  | WS* ('this' '.')? ztlActionMethod WS*
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
  ;

returnStatement
  : WS* 'return' WS expression WS*
  ;

annotionStatement
  : WS* '@' Identifier WS*
  ;

otherStatement
  :	classStatement WS* NEWLINE?
  |	defStatement WS* ';'? NEWLINE
  | singleStatement WS* ';'? NEWLINE
  | annotionStatement WS* NEWLINE
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