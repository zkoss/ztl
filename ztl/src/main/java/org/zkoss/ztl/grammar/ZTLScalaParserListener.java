package org.zkoss.ztl.grammar;// Generated from ZTLScalaParser.g4 by ANTLR 4.7
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ZTLScalaParser}.
 */
public interface ZTLScalaParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(ZTLScalaParser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(ZTLScalaParser.LiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#numericLiteral}.
	 * @param ctx the parse tree
	 */
	void enterNumericLiteral(ZTLScalaParser.NumericLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#numericLiteral}.
	 * @param ctx the parse tree
	 */
	void exitNumericLiteral(ZTLScalaParser.NumericLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#integerLiteral}.
	 * @param ctx the parse tree
	 */
	void enterIntegerLiteral(ZTLScalaParser.IntegerLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#integerLiteral}.
	 * @param ctx the parse tree
	 */
	void exitIntegerLiteral(ZTLScalaParser.IntegerLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#floatLiteral}.
	 * @param ctx the parse tree
	 */
	void enterFloatLiteral(ZTLScalaParser.FloatLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#floatLiteral}.
	 * @param ctx the parse tree
	 */
	void exitFloatLiteral(ZTLScalaParser.FloatLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary(ZTLScalaParser.PrimaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary(ZTLScalaParser.PrimaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCall(ZTLScalaParser.FunctionCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCall(ZTLScalaParser.FunctionCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#variable}.
	 * @param ctx the parse tree
	 */
	void enterVariable(ZTLScalaParser.VariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#variable}.
	 * @param ctx the parse tree
	 */
	void exitVariable(ZTLScalaParser.VariableContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#methodDeclarator}.
	 * @param ctx the parse tree
	 */
	void enterMethodDeclarator(ZTLScalaParser.MethodDeclaratorContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#methodDeclarator}.
	 * @param ctx the parse tree
	 */
	void exitMethodDeclarator(ZTLScalaParser.MethodDeclaratorContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#newObject}.
	 * @param ctx the parse tree
	 */
	void enterNewObject(ZTLScalaParser.NewObjectContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#newObject}.
	 * @param ctx the parse tree
	 */
	void exitNewObject(ZTLScalaParser.NewObjectContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#anonymousFunction}.
	 * @param ctx the parse tree
	 */
	void enterAnonymousFunction(ZTLScalaParser.AnonymousFunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#anonymousFunction}.
	 * @param ctx the parse tree
	 */
	void exitAnonymousFunction(ZTLScalaParser.AnonymousFunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(ZTLScalaParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(ZTLScalaParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#formalParameters}.
	 * @param ctx the parse tree
	 */
	void enterFormalParameters(ZTLScalaParser.FormalParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#formalParameters}.
	 * @param ctx the parse tree
	 */
	void exitFormalParameters(ZTLScalaParser.FormalParametersContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(ZTLScalaParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(ZTLScalaParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void enterConditionalExpression(ZTLScalaParser.ConditionalExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#conditionalExpression}.
	 * @param ctx the parse tree
	 */
	void exitConditionalExpression(ZTLScalaParser.ConditionalExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#conditionalOrExpression}.
	 * @param ctx the parse tree
	 */
	void enterConditionalOrExpression(ZTLScalaParser.ConditionalOrExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#conditionalOrExpression}.
	 * @param ctx the parse tree
	 */
	void exitConditionalOrExpression(ZTLScalaParser.ConditionalOrExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#conditionalAndExpression}.
	 * @param ctx the parse tree
	 */
	void enterConditionalAndExpression(ZTLScalaParser.ConditionalAndExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#conditionalAndExpression}.
	 * @param ctx the parse tree
	 */
	void exitConditionalAndExpression(ZTLScalaParser.ConditionalAndExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#equalityExpression}.
	 * @param ctx the parse tree
	 */
	void enterEqualityExpression(ZTLScalaParser.EqualityExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#equalityExpression}.
	 * @param ctx the parse tree
	 */
	void exitEqualityExpression(ZTLScalaParser.EqualityExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#relationalExpression}.
	 * @param ctx the parse tree
	 */
	void enterRelationalExpression(ZTLScalaParser.RelationalExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#relationalExpression}.
	 * @param ctx the parse tree
	 */
	void exitRelationalExpression(ZTLScalaParser.RelationalExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#additiveExpression}.
	 * @param ctx the parse tree
	 */
	void enterAdditiveExpression(ZTLScalaParser.AdditiveExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#additiveExpression}.
	 * @param ctx the parse tree
	 */
	void exitAdditiveExpression(ZTLScalaParser.AdditiveExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#multiplicativeExpression}.
	 * @param ctx the parse tree
	 */
	void enterMultiplicativeExpression(ZTLScalaParser.MultiplicativeExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#multiplicativeExpression}.
	 * @param ctx the parse tree
	 */
	void exitMultiplicativeExpression(ZTLScalaParser.MultiplicativeExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#unaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterUnaryExpression(ZTLScalaParser.UnaryExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#unaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitUnaryExpression(ZTLScalaParser.UnaryExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#specialMethod}.
	 * @param ctx the parse tree
	 */
	void enterSpecialMethod(ZTLScalaParser.SpecialMethodContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#specialMethod}.
	 * @param ctx the parse tree
	 */
	void exitSpecialMethod(ZTLScalaParser.SpecialMethodContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#wrapMethod}.
	 * @param ctx the parse tree
	 */
	void enterWrapMethod(ZTLScalaParser.WrapMethodContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#wrapMethod}.
	 * @param ctx the parse tree
	 */
	void exitWrapMethod(ZTLScalaParser.WrapMethodContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#equalsMethod}.
	 * @param ctx the parse tree
	 */
	void enterEqualsMethod(ZTLScalaParser.EqualsMethodContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#equalsMethod}.
	 * @param ctx the parse tree
	 */
	void exitEqualsMethod(ZTLScalaParser.EqualsMethodContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#containsMethod}.
	 * @param ctx the parse tree
	 */
	void enterContainsMethod(ZTLScalaParser.ContainsMethodContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#containsMethod}.
	 * @param ctx the parse tree
	 */
	void exitContainsMethod(ZTLScalaParser.ContainsMethodContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#parseMethod}.
	 * @param ctx the parse tree
	 */
	void enterParseMethod(ZTLScalaParser.ParseMethodContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#parseMethod}.
	 * @param ctx the parse tree
	 */
	void exitParseMethod(ZTLScalaParser.ParseMethodContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#toIntMethod}.
	 * @param ctx the parse tree
	 */
	void enterToIntMethod(ZTLScalaParser.ToIntMethodContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#toIntMethod}.
	 * @param ctx the parse tree
	 */
	void exitToIntMethod(ZTLScalaParser.ToIntMethodContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#toDoubleMethod}.
	 * @param ctx the parse tree
	 */
	void enterToDoubleMethod(ZTLScalaParser.ToDoubleMethodContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#toDoubleMethod}.
	 * @param ctx the parse tree
	 */
	void exitToDoubleMethod(ZTLScalaParser.ToDoubleMethodContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#toFloatMethod}.
	 * @param ctx the parse tree
	 */
	void enterToFloatMethod(ZTLScalaParser.ToFloatMethodContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#toFloatMethod}.
	 * @param ctx the parse tree
	 */
	void exitToFloatMethod(ZTLScalaParser.ToFloatMethodContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#toBooleanMethod}.
	 * @param ctx the parse tree
	 */
	void enterToBooleanMethod(ZTLScalaParser.ToBooleanMethodContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#toBooleanMethod}.
	 * @param ctx the parse tree
	 */
	void exitToBooleanMethod(ZTLScalaParser.ToBooleanMethodContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#ztlUnitMethod}.
	 * @param ctx the parse tree
	 */
	void enterZtlUnitMethod(ZTLScalaParser.ZtlUnitMethodContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#ztlUnitMethod}.
	 * @param ctx the parse tree
	 */
	void exitZtlUnitMethod(ZTLScalaParser.ZtlUnitMethodContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#ztlTestMethod}.
	 * @param ctx the parse tree
	 */
	void enterZtlTestMethod(ZTLScalaParser.ZtlTestMethodContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#ztlTestMethod}.
	 * @param ctx the parse tree
	 */
	void exitZtlTestMethod(ZTLScalaParser.ZtlTestMethodContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#ifElseExpression}.
	 * @param ctx the parse tree
	 */
	void enterIfElseExpression(ZTLScalaParser.IfElseExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#ifElseExpression}.
	 * @param ctx the parse tree
	 */
	void exitIfElseExpression(ZTLScalaParser.IfElseExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(ZTLScalaParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(ZTLScalaParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#ifThenStatement}.
	 * @param ctx the parse tree
	 */
	void enterIfThenStatement(ZTLScalaParser.IfThenStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#ifThenStatement}.
	 * @param ctx the parse tree
	 */
	void exitIfThenStatement(ZTLScalaParser.IfThenStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#elseStatement}.
	 * @param ctx the parse tree
	 */
	void enterElseStatement(ZTLScalaParser.ElseStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#elseStatement}.
	 * @param ctx the parse tree
	 */
	void exitElseStatement(ZTLScalaParser.ElseStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#elseIfStatement}.
	 * @param ctx the parse tree
	 */
	void enterElseIfStatement(ZTLScalaParser.ElseIfStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#elseIfStatement}.
	 * @param ctx the parse tree
	 */
	void exitElseIfStatement(ZTLScalaParser.ElseIfStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#ifThenElseStatement}.
	 * @param ctx the parse tree
	 */
	void enterIfThenElseStatement(ZTLScalaParser.IfThenElseStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#ifThenElseStatement}.
	 * @param ctx the parse tree
	 */
	void exitIfThenElseStatement(ZTLScalaParser.IfThenElseStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#forStatement}.
	 * @param ctx the parse tree
	 */
	void enterForStatement(ZTLScalaParser.ForStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#forStatement}.
	 * @param ctx the parse tree
	 */
	void exitForStatement(ZTLScalaParser.ForStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#forCondition}.
	 * @param ctx the parse tree
	 */
	void enterForCondition(ZTLScalaParser.ForConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#forCondition}.
	 * @param ctx the parse tree
	 */
	void exitForCondition(ZTLScalaParser.ForConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#whileStatement}.
	 * @param ctx the parse tree
	 */
	void enterWhileStatement(ZTLScalaParser.WhileStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#whileStatement}.
	 * @param ctx the parse tree
	 */
	void exitWhileStatement(ZTLScalaParser.WhileStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#conditionBodyStatements}.
	 * @param ctx the parse tree
	 */
	void enterConditionBodyStatements(ZTLScalaParser.ConditionBodyStatementsContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#conditionBodyStatements}.
	 * @param ctx the parse tree
	 */
	void exitConditionBodyStatements(ZTLScalaParser.ConditionBodyStatementsContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(ZTLScalaParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(ZTLScalaParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#classStatement}.
	 * @param ctx the parse tree
	 */
	void enterClassStatement(ZTLScalaParser.ClassStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#classStatement}.
	 * @param ctx the parse tree
	 */
	void exitClassStatement(ZTLScalaParser.ClassStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#defStatement}.
	 * @param ctx the parse tree
	 */
	void enterDefStatement(ZTLScalaParser.DefStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#defStatement}.
	 * @param ctx the parse tree
	 */
	void exitDefStatement(ZTLScalaParser.DefStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#defInfo}.
	 * @param ctx the parse tree
	 */
	void enterDefInfo(ZTLScalaParser.DefInfoContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#defInfo}.
	 * @param ctx the parse tree
	 */
	void exitDefInfo(ZTLScalaParser.DefInfoContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#assignmentStatement}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentStatement(ZTLScalaParser.AssignmentStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#assignmentStatement}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentStatement(ZTLScalaParser.AssignmentStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#otherAssignmentSymbol}.
	 * @param ctx the parse tree
	 */
	void enterOtherAssignmentSymbol(ZTLScalaParser.OtherAssignmentSymbolContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#otherAssignmentSymbol}.
	 * @param ctx the parse tree
	 */
	void exitOtherAssignmentSymbol(ZTLScalaParser.OtherAssignmentSymbolContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#anyType}.
	 * @param ctx the parse tree
	 */
	void enterAnyType(ZTLScalaParser.AnyTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#anyType}.
	 * @param ctx the parse tree
	 */
	void exitAnyType(ZTLScalaParser.AnyTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#functionCallStatement}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCallStatement(ZTLScalaParser.FunctionCallStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#functionCallStatement}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCallStatement(ZTLScalaParser.FunctionCallStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#verifyMethod}.
	 * @param ctx the parse tree
	 */
	void enterVerifyMethod(ZTLScalaParser.VerifyMethodContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#verifyMethod}.
	 * @param ctx the parse tree
	 */
	void exitVerifyMethod(ZTLScalaParser.VerifyMethodContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#returnStatement}.
	 * @param ctx the parse tree
	 */
	void enterReturnStatement(ZTLScalaParser.ReturnStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#returnStatement}.
	 * @param ctx the parse tree
	 */
	void exitReturnStatement(ZTLScalaParser.ReturnStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#otherStatement}.
	 * @param ctx the parse tree
	 */
	void enterOtherStatement(ZTLScalaParser.OtherStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#otherStatement}.
	 * @param ctx the parse tree
	 */
	void exitOtherStatement(ZTLScalaParser.OtherStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#singleStatement}.
	 * @param ctx the parse tree
	 */
	void enterSingleStatement(ZTLScalaParser.SingleStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#singleStatement}.
	 * @param ctx the parse tree
	 */
	void exitSingleStatement(ZTLScalaParser.SingleStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link ZTLScalaParser#statements}.
	 * @param ctx the parse tree
	 */
	void enterStatements(ZTLScalaParser.StatementsContext ctx);
	/**
	 * Exit a parse tree produced by {@link ZTLScalaParser#statements}.
	 * @param ctx the parse tree
	 */
	void exitStatements(ZTLScalaParser.StatementsContext ctx);
}