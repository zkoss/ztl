package org.zkoss.ztl.grammar;// Generated from ZTLScalaParser.g4 by ANTLR 4.7
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ZTLScalaParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ZTLScalaParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral(ZTLScalaParser.LiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#stringLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringLiteral(ZTLScalaParser.StringLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#numericLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumericLiteral(ZTLScalaParser.NumericLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#integerLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntegerLiteral(ZTLScalaParser.IntegerLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#floatLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFloatLiteral(ZTLScalaParser.FloatLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#primary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimary(ZTLScalaParser.PrimaryContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#returnOrSpace}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnOrSpace(ZTLScalaParser.ReturnOrSpaceContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#functionCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionCall(ZTLScalaParser.FunctionCallContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#variable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable(ZTLScalaParser.VariableContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#methodDeclarator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethodDeclarator(ZTLScalaParser.MethodDeclaratorContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#newObject}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewObject(ZTLScalaParser.NewObjectContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#anonymousFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnonymousFunction(ZTLScalaParser.AnonymousFunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(ZTLScalaParser.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#typeDetail}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeDetail(ZTLScalaParser.TypeDetailContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#formalParameters}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormalParameters(ZTLScalaParser.FormalParametersContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(ZTLScalaParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#conditionalExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditionalExpression(ZTLScalaParser.ConditionalExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#conditionalOrExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditionalOrExpression(ZTLScalaParser.ConditionalOrExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#conditionalAndExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditionalAndExpression(ZTLScalaParser.ConditionalAndExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#equalityExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEqualityExpression(ZTLScalaParser.EqualityExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#relationalExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelationalExpression(ZTLScalaParser.RelationalExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#additiveExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdditiveExpression(ZTLScalaParser.AdditiveExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#multiplicativeExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiplicativeExpression(ZTLScalaParser.MultiplicativeExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#unaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryExpression(ZTLScalaParser.UnaryExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#specialMethod}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSpecialMethod(ZTLScalaParser.SpecialMethodContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#parseMethod}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParseMethod(ZTLScalaParser.ParseMethodContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#toIntMethod}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitToIntMethod(ZTLScalaParser.ToIntMethodContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#toDoubleMethod}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitToDoubleMethod(ZTLScalaParser.ToDoubleMethodContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#toFloatMethod}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitToFloatMethod(ZTLScalaParser.ToFloatMethodContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#toBooleanMethod}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitToBooleanMethod(ZTLScalaParser.ToBooleanMethodContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#ztlUnitMethod}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitZtlUnitMethod(ZTLScalaParser.ZtlUnitMethodContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#ztlTestMethod}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitZtlTestMethod(ZTLScalaParser.ZtlTestMethodContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(ZTLScalaParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#conditionStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditionStatement(ZTLScalaParser.ConditionStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#ifThenStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfThenStatement(ZTLScalaParser.IfThenStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#elseStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElseStatement(ZTLScalaParser.ElseStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#elseIfStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElseIfStatement(ZTLScalaParser.ElseIfStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#forStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForStatement(ZTLScalaParser.ForStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#whileStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileStatement(ZTLScalaParser.WhileStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#ifExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfExpression(ZTLScalaParser.IfExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#elseExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElseExpression(ZTLScalaParser.ElseExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#elseIfExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElseIfExpression(ZTLScalaParser.ElseIfExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#forExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForExpression(ZTLScalaParser.ForExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#forCondition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForCondition(ZTLScalaParser.ForConditionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#whileExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileExpression(ZTLScalaParser.WhileExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#conditionBodyStatements}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditionBodyStatements(ZTLScalaParser.ConditionBodyStatementsContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(ZTLScalaParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#block_pre}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock_pre(ZTLScalaParser.Block_preContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#block_suf}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock_suf(ZTLScalaParser.Block_sufContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#classStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassStatement(ZTLScalaParser.ClassStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#defStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefStatement(ZTLScalaParser.DefStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#defInfo}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefInfo(ZTLScalaParser.DefInfoContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#assignmentStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignmentStatement(ZTLScalaParser.AssignmentStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#assignmentOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignmentOperator(ZTLScalaParser.AssignmentOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#anyType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnyType(ZTLScalaParser.AnyTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#functionCallStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionCallStatement(ZTLScalaParser.FunctionCallStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#verifyMethod}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVerifyMethod(ZTLScalaParser.VerifyMethodContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#ztlActionMethod}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitZtlActionMethod(ZTLScalaParser.ZtlActionMethodContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#returnStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStatement(ZTLScalaParser.ReturnStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#annotionStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnnotionStatement(ZTLScalaParser.AnnotionStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#otherStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOtherStatement(ZTLScalaParser.OtherStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link ZTLScalaParser#statements}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatements(ZTLScalaParser.StatementsContext ctx);
}