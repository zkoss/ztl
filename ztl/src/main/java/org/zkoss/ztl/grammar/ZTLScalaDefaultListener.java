package org.zkoss.ztl.grammar;

import org.zkoss.ztl.ZKClientTestCaseCafe;

import java.util.*;

/**
 * @author jameschu
 */
public class ZTLScalaDefaultListener extends ZTLScalaParserBaseListener {
	private static boolean _isDebug = true;
	private static void log(Object... os) {
		if (!_isDebug)
			return;
		for (Object o : os) {
			System.out.print(o + (os[os.length - 1] != o ? "," : ""));
		}
		System.out.println();
	}

	public Map<String, String> getPreCodeReplacements() {
		return _preCodeReplacements;
	}

	public Map<String, String> getCodeReplacements() {
		return _codeReplacements;
	}

	private Map<String, String> _preCodeReplacements;
	private Map<String, String> _codeReplacements;
	private Map<String, String> _variableNameReplacements;

	public ZTLScalaDefaultListener() {
		super();
		_preCodeReplacements = new HashMap<>();
		_codeReplacements = new HashMap<>();
		_variableNameReplacements = new HashMap<>();
		// use in scope
		_ZTLFunctionReplacements = new HashSet<>();
		_originTextReplacements = new HashSet<>();
		_variableInStatement = new HashSet<>();
		//used to add block or not
		_inCafeConditionStatements = new LinkedList<>();
	}

	private int _scopeCnt = 0;
	@Override
	public void enterClassStatement(ZTLScalaParser.ClassStatementContext ctx) {
	}

	@Override
	public void exitClassStatement(ZTLScalaParser.ClassStatementContext ctx) {
		//classStatement
		String className = ctx.Identifier().get(0).getText();
		_codeReplacements.put(className, className + "$cafe");
	}

	@Override
	public void exitBlock(ZTLScalaParser.BlockContext ctx) {
		List<ZTLScalaParser.StatementContext> statement = ctx.statement();
		int size = statement.size();
		int cafeConditionSize = _inCafeConditionStatements.size();
		if (size > 0 && cafeConditionSize > 0 && _inCafeConditionStatements.get(cafeConditionSize - 1)) {
			String firstStatement = statement.get(0).getText();
			_preCodeReplacements.put(ctx.block_pre().getText() + firstStatement, "conditionBlock_cafe(true);\n" + firstStatement);
			String lastStatement = statement.get(size - 1).getText();
			_preCodeReplacements.put(lastStatement + ctx.block_suf().getText(), lastStatement + "conditionBlock_cafe(false);\n");
		}
	}

	boolean _inConditionBodyStatements = false;
	@Override
	public void enterConditionBodyStatements(ZTLScalaParser.ConditionBodyStatementsContext ctx) {
		_inConditionBodyStatements = true;
	}

	@Override
	public void exitConditionBodyStatements(ZTLScalaParser.ConditionBodyStatementsContext ctx) {
		_inConditionBodyStatements = false;
	}

	private boolean _inAssignment = false;
	private boolean _inVerification = false;
	private Set<TextReplacement> _ZTLFunctionReplacements;
	private Set<TextReplacement> _originTextReplacements;
	private Set<String> _variableInStatement;
	@Override
	public void enterAssignmentStatement(ZTLScalaParser.AssignmentStatementContext ctx) {
		_inAssignment = true;
	}

	@Override
	public void exitAssignmentStatement(ZTLScalaParser.AssignmentStatementContext ctx) {
		String text = ctx.getText().trim();
		if (!text.contains("\"\"\"")) {
			if (_codeReplacements.containsKey(text))
				log("More than one assignments with same text: " + text);
			String assignName = "";
			boolean isDeclaration = false;
			String startToken = ctx.anyType() != null ? ctx.anyType().getText() : ctx.primary().getText();
			if ("val".equals(startToken) || "var".equals(startToken)) {
				isDeclaration = true;
				assignName = ctx.Identifier().getText();
			} else {
				assignName = startToken;
			}
			if (assignName.isEmpty())
				throw new UnsupportedOperationException("Error assignment : " + text);

			StringBuilder replacement = new StringBuilder();
			String newAssignName = assignName + "_cafe";

			String exprText = ctx.expression().getText();
			String newExprText = exprText;
			boolean wrapString = false;
			boolean changed = false;
			for (String variable: _variableInStatement) { // ex. t -> t_cafe
				if (_variableNameReplacements.containsKey(variable)) {
					newExprText = replaceCodeText(newExprText, variable, _variableNameReplacements.get(variable));
					wrapString = true;
				}
			}
			if (_ZTLFunctionReplacements.size() > 0) { // do replace: .width() -> .widthStr()
				changed = true; // something need replace
				Set<TextReplacement> textReplacements = _ZTLFunctionReplacements;
				int paramCnt = 0;
				for (TextReplacement tr : textReplacements) {
					String replaceText = tr.getReplacement();
					if (_isConditionalExprExist) {
						String name = newAssignName + "_" + paramCnt;
						replacement.append("assignment_cafe(\"").append(name).append("\",").append(replaceText).append(", true)\n");
						replaceText = name;
					}
					String targetText = tr.getText();
					while (newExprText.indexOf(targetText) != -1 && !targetText.equals(newExprText)) {
						newExprText = newExprText.replace(tr.getText(), replaceText); // Same key risk? replace xxx.width() is ok
					}
					paramCnt++;
				}
			}

			if (changed) {
				replacement.append("assignment_cafe(\"").append(newAssignName).append("\", ");
				if (wrapString) {
					replacement.append("\"" + ZKClientTestCaseCafe.CAFEEVAL);
				}
				if (ctx.otherAssignmentSymbol() != null) {
					replacement.append(newAssignName).append(" ")
							.append(ctx.otherAssignmentSymbol().getChild(0).getText()).append(" ");
				}
				if (wrapString) {
					replacement.append(newExprText.replaceAll("\"", "\\\\\"")).append("\"");
				} else {
					replacement.append(newExprText);
				}
				replacement.append(", ").append(isDeclaration ? "true" : "false").append(")\n");
				String newText = text;
				for (TextReplacement tr : _originTextReplacements) {
					while (newText.indexOf(tr.getText()) != -1) {
						newText = newText.replace(tr.getText(), tr.getReplacement());
					}
				}
				_codeReplacements.put(text, newText + "\n" + replacement.toString());
				_variableNameReplacements.put(assignName, newAssignName);
			}
			String logText = "";
//			log(logText + "exitAssignmentStatment (cond:" + _isConditionalExprExist + ") >>> " + text);
		}
		_inAssignment = false;
		_isConditionalExprExist = false;
		_ZTLFunctionReplacements.clear();
		_originTextReplacements.clear();
		_variableInStatement.clear();
	}

	private boolean _isConditionalExprExist = false;

	@Override
	public void enterConditionalExpression(ZTLScalaParser.ConditionalExpressionContext ctx) {
		if (_inAssignment || _inVerification || _inAction || _inConditionStatementExpression) _isConditionalExprExist = true;
	}

	@Override
	public void exitVariable(ZTLScalaParser.VariableContext ctx) {
		if (_inAssignment || _inVerification || _inAction || _inConditionStatementExpression)
			_variableInStatement.add(ctx.getText());
	}

	private boolean _inParseMethod = false;
	private Map<String, TextReplacement> _unitFunctionInParseMethod = new HashMap<>();
	@Override
	public void enterParseMethod(ZTLScalaParser.ParseMethodContext ctx) {
		if (_inAssignment || _inVerification || _inAction || _inConditionStatementExpression) {
			_inParseMethod = true;
		}
	}

	@Override
	public void exitParseMethod(ZTLScalaParser.ParseMethodContext ctx) {
		if (_inAssignment || _inVerification || _inAction || _inConditionStatementExpression) {
			String text = ctx.getText();
			String replacement = "";
			String originTextReplacement;
			boolean handled = false;
			if (text.startsWith("parse")) {
				String firstText = ctx.getChild(0).getChild(0).getText(); // ex. 'parseDouble' '(' expression ')'
				String expr = ctx.getChild(0).getChild(2).getText();
				if ("parseInt".equals(firstText) || "parseDouble".equals(firstText) || "parseFloat".equals(firstText)) {
					replacement = firstText + "Str(" + expr + ")";
				} else {
					replacement = expr;
				}
			} else {
				String expr = ctx.getChild(0).getChild(0).getText(); // ex. primary '.' 'toInt'
				replacement = expr;
				if (_unitFunctionInParseMethod.containsKey(expr)) {
					_ZTLFunctionReplacements.add(new TextReplacement(text, _unitFunctionInParseMethod.get(expr).getReplacement()));
					handled = true;
				}
				// avoid parse exception
				originTextReplacement = "parse" + ctx.getChild(0).getChild(2).getText().replace("to", "") + "(" + expr + ")";
				_originTextReplacements.add(new TextReplacement(text, originTextReplacement));
			}
			if (!handled)
				_ZTLFunctionReplacements.add(new TextReplacement(text, replacement));
			_inParseMethod = false;
		}

	}

	private boolean _inAction = false;
	private int newActionAssignCnt = 0;

	@Override
	public void enterZtlActionMethod(ZTLScalaParser.ZtlActionMethodContext ctx) {
		_inAction = true;
	}

	@Override
	public void exitZtlActionMethod(ZTLScalaParser.ZtlActionMethodContext ctx) {
		String text = ctx.getText();
		StringBuilder replacement = new StringBuilder();
		String actionMethod = ctx.getChild(0).getText();
		ZTLScalaParser.FormalParametersContext paramCtx = ctx.formalParameters();
		List<ZTLScalaParser.ExpressionContext> expressions = paramCtx.expression();
		StringBuilder exprReplacement = new StringBuilder();
		int exprCnt = 0;
		int exprTotalCnt = expressions.size();
		boolean paramChanged = false;
		for (ZTLScalaParser.ExpressionContext expr : expressions) {
			if (exprCnt > 0)
				exprReplacement.append(", ");
			if (exprCnt == 0) {
				exprReplacement.append(expr.getText());
				exprCnt++;
				continue; //skip locator
			} else if (exprCnt == 1) {
				if ("dragdropToObject".equals(actionMethod)) {
					if (exprTotalCnt == 2)
						break;
					else {
						continue;
					}
				}
			}
			exprCnt++;
			String exprText = expr.getText();
			String newExprText = exprText;
			if (exprText.trim().startsWith("\"") || exprText.trim().matches("[0-9.]*")) {
				exprReplacement.append(exprText);
				continue; //skip String & number
			}
			boolean wrapString = false;
			for (String variable: _variableInStatement) { // ex. t -> t_cafe
				if (_variableNameReplacements.containsKey(variable)) {
					newExprText = replaceCodeText(newExprText, variable, _variableNameReplacements.get(variable));
					wrapString = true;
				}
			}
			if (_ZTLFunctionReplacements.size() > 0) { // do replace: .width() -> .widthStr()
				Set<TextReplacement> textReplacements = _ZTLFunctionReplacements;
				int paramCnt = 0;
				for (TextReplacement tr : textReplacements) {
					String replaceText = tr.getReplacement();
					if (_isConditionalExprExist) {
						String name = "actionVariable_cafe" + "_" + newActionAssignCnt + "_" + paramCnt;
						replacement.append("assignment_cafe(\"").append(name).append("\",").append(replaceText).append(", true)\n");
						replaceText = name;
						wrapString = true;
						newActionAssignCnt++;
					}
					String targetText = tr.getText();
					while (newExprText.indexOf(targetText) != -1 && !targetText.equals(newExprText)) {
						newExprText = newExprText.replace(tr.getText(), replaceText);
					}
					paramCnt++;
				}
			}
			if (!exprText.equals(newExprText)) {
				paramChanged = true;
				if (wrapString)
					newExprText = "\"" + newExprText + "\"";
			}
			exprReplacement.append(newExprText);
		}

		if (paramChanged) {
			replacement.append(ctx.getChild(0).getText()).append("(").append(exprReplacement).append(")\n");
			_codeReplacements.put(text, replacement.toString());
		}
		_inAction = false;
		_isConditionalExprExist = false;
		_ZTLFunctionReplacements.clear();
		_variableInStatement.clear();
	}

	@Override
	public void enterVerifyMethod(ZTLScalaParser.VerifyMethodContext ctx) {
		_inVerification = true;
	}

	private int newVerifyAssignCnt = 0;
	@Override
	public void exitVerifyMethod(ZTLScalaParser.VerifyMethodContext ctx) {
		String text = ctx.getText();
		StringBuilder replacement = new StringBuilder();
		ZTLScalaParser.FormalParametersContext paramCtx = ctx.formalParameters();
		List<ZTLScalaParser.ExpressionContext> expressions = paramCtx.expression();
		StringBuilder exprReplacement = new StringBuilder();
		int exprCnt = 0;
		boolean paramChanged = false;
		for (ZTLScalaParser.ExpressionContext expr : expressions) {
			if (exprCnt > 0)
				exprReplacement.append(", ");
			exprCnt++;
			String exprText = expr.getText();
			String newExprText = exprText;
			if (exprText.trim().startsWith("\"") || exprText.trim().matches("[0-9.]*")) {
				exprReplacement.append(exprText);
				continue; //skip String & number
			}
			boolean wrapString = false;
			for (String variable: _variableInStatement) { // ex. t -> t_cafe
				if (_variableNameReplacements.containsKey(variable)) {
					newExprText = replaceCodeText(newExprText, variable, _variableNameReplacements.get(variable));
					wrapString = true;
				}
			}
			if (_ZTLFunctionReplacements.size() > 0) { // do replace: .width() -> .widthStr()
				Set<TextReplacement> textReplacements = _ZTLFunctionReplacements;
				int paramCnt = 0;
				for (TextReplacement tr : textReplacements) {
					String replaceText = tr.getReplacement();
					if (_isConditionalExprExist) {
						String name = "verifyVariable_cafe" + "_" + newVerifyAssignCnt + "_" + paramCnt;
						replacement.append("assignment_cafe(\"").append(name).append("\",").append(replaceText).append(", true)\n");
						replaceText = name;
						wrapString = true;
						newVerifyAssignCnt++;
					}
					String targetText = tr.getText();
					while (newExprText.indexOf(targetText) != -1 && !targetText.equals(newExprText)) {
						newExprText = newExprText.replace(tr.getText(), replaceText);
					}
					paramCnt++;
				}
			}
			if (!exprText.equals(newExprText)) {
				paramChanged = true;
				if (wrapString)
					newExprText = "\"" + ZKClientTestCaseCafe.CAFEEVAL + newExprText + "\"";
			}
			exprReplacement.append(newExprText);
		}

		if (paramChanged) {
			replacement.append(ctx.getChild(0).getText()).append("(").append(exprReplacement).append(")\n");
			_codeReplacements.put(text, replacement.toString());
		}
		_inVerification = false;
		_isConditionalExprExist = false;
		_ZTLFunctionReplacements.clear();
		_variableInStatement.clear();
	}

	private int conditionStatementCnt = 0;
	private List<Boolean> _inCafeConditionStatements; //used to add block or not
	private boolean _inConditionStatementExpression;

	@Override
	public void enterConditionStatement(ZTLScalaParser.ConditionStatementContext ctx) {
		_inCafeConditionStatements.add(Boolean.TRUE);
	}

	@Override
	public void exitConditionStatement(ZTLScalaParser.ConditionStatementContext ctx) {
		_inCafeConditionStatements.remove(_inCafeConditionStatements.size() - 1);
	}

	@Override
	public void enterIfExpression(ZTLScalaParser.IfExpressionContext ctx) {
		_inConditionStatementExpression = true;
	}

	@Override
	public void exitIfExpression(ZTLScalaParser.IfExpressionContext ctx) {
		handleIfExpression(ctx.getChild(0).getText(), ctx.getText(), ctx.expression());
	}

	@Override
	public void enterElseIfExpression(ZTLScalaParser.ElseIfExpressionContext ctx) {
		_inConditionStatementExpression = true;
	}

	private void handleIfExpression(String cond, String text, ZTLScalaParser.ExpressionContext expression) {
		StringBuilder replacement = new StringBuilder();
		String exprText = text;
		String newExprText = exprText;
		for (String variable: _variableInStatement) { // ex. t -> t_cafe
			if (_variableNameReplacements.containsKey(variable)) {
				newExprText = replaceCodeText(newExprText, variable, _variableNameReplacements.get(variable));
			}
		}
		if (_ZTLFunctionReplacements.size() > 0) { // do replace: .width() -> .widthStr()
			Set<TextReplacement> textReplacements = _ZTLFunctionReplacements;
			String newAssignName = "conditionStatementExpr_cafe" + conditionStatementCnt++;
			int paramCnt = 0;
			for (TextReplacement tr : textReplacements) {
				String replaceText = tr.getReplacement();
				if (_isConditionalExprExist) {
					String name = newAssignName + "_" + paramCnt;
					replacement.append("assignment_cafe(\"").append(name).append("\",").append(replaceText).append(", true)\n");
					replaceText = name;
				}
				String targetText = tr.getText();
				while (newExprText.indexOf(targetText) != -1 && !targetText.equals(newExprText)) {
					newExprText = newExprText.replace(tr.getText(), replaceText); // Same key risk? replace xxx.width() is ok
				}
				paramCnt++;
			}
		}
		if (!exprText.equals(newExprText)) {
			replacement.append("conditionStatement_cafe(\"").append(cond).append("\", \"");
			replacement.append(newExprText).append("\")\n");
			_codeReplacements.put(text, replacement.toString());
		} else {
			_inCafeConditionStatements.add(_inCafeConditionStatements.size() - 1, Boolean.FALSE);
			_inCafeConditionStatements.remove(_inCafeConditionStatements.size() - 1);
		}
		String logText = "";
		log(logText + "handleIfExpression >>> " + text);
		_inConditionStatementExpression = false;
		_isConditionalExprExist = false;
		_ZTLFunctionReplacements.clear();
		_variableInStatement.clear();
	}

	@Override
	public void exitElseIfExpression(ZTLScalaParser.ElseIfExpressionContext ctx) {
		handleIfExpression(ctx.getChild(0).getText(), ctx.getText(), ctx.expression());
	}

	@Override
	public void exitZtlTestMethod(ZTLScalaParser.ZtlTestMethodContext ctx) {
		String text = ctx.getText().trim();
		String replacement = text.replace("this.", "");
		for (String checkFunction : ZTLTestFunctionCheckList) {
			String checkToken = "." + checkFunction;
			int index = text.indexOf(checkToken);
			if (index != -1) {
				if (!text.equals("getZKLog") && !text.equals("getText") && !text.equals("getAlertMessage")) {
					replacement = replacement.replace(checkToken, checkToken + "_cafeStr");
				}
				break;
			}
		}
		if (replacement.isEmpty())
			log("ERROR: exitZtlTestMethod");
		else
			_ZTLFunctionReplacements.add(new TextReplacement(text, replacement));
	}

	private static List<String> ZTLTestFunctionCheckList = new ArrayList<>();
	static {
		ZTLTestFunctionCheckList.add("getAlertMessage");
		ZTLTestFunctionCheckList.add("hasError");
		ZTLTestFunctionCheckList.add("getText");
		ZTLTestFunctionCheckList.add("isVisible");
		ZTLTestFunctionCheckList.add("hasNativeScroll");
		ZTLTestFunctionCheckList.add("hasHScrollbar");
		ZTLTestFunctionCheckList.add("hasVScrollbar");
		ZTLTestFunctionCheckList.add("getScrollTop");
		ZTLTestFunctionCheckList.add("getScrollLeft");
		ZTLTestFunctionCheckList.add("getZKLog");
		ZTLTestFunctionCheckList.add("is");
		ZTLTestFunctionCheckList.add("getWindowWidth");
		ZTLTestFunctionCheckList.add("getWindowHeight");
		ZTLTestFunctionCheckList.add("getBrowserTabCount");
	}

	@Override
	public void exitZtlUnitMethod(ZTLScalaParser.ZtlUnitMethodContext ctx) {
		String text = ctx.getText().trim();
		String replacement = text;
		String methodName = ctx.getChild(2).getText();
		if (!unitJSStringFunctionList.contains(methodName)) {
			replacement = text.replace("." + methodName, "." + methodName + "_cafeStr");
		}
		TextReplacement t = new TextReplacement(text, replacement);
		if (_inParseMethod) {
			_unitFunctionInParseMethod.put(text, t);
		} else
			_ZTLFunctionReplacements.add(t);
	}

	private static List<String> unitJSStringFunctionList = new ArrayList<>();
	static {
		unitJSStringFunctionList.add("css");
		unitJSStringFunctionList.add("attr");
		unitJSStringFunctionList.add("text");
		unitJSStringFunctionList.add("html");
		unitJSStringFunctionList.add("`val`");
		unitJSStringFunctionList.add("uuid");
		unitJSStringFunctionList.add("id");
		unitJSStringFunctionList.add("get");
		unitJSStringFunctionList.add("is");
	}

	private String replaceCodeText(String text, String target, String replacement) {
		replacement = "$1" + replacement + "$2";
		boolean pre = false;
		boolean suf = false;
		if (text.startsWith(target)) {
			text = " " + text;
			pre = true;
		}
		if (text.endsWith(target)) {
			text = text + " ";
			suf = true;
		}
		// before and after the target, those characters should not be 1-9a-zA-Z
		String result = text.replaceAll("([^a-zA-Z0-9\\-_\\$])" + target + "([^a-zA-Z0-9\\-_\\$])", replacement);
		if (pre)
			result = result.substring(1);
		if (suf)
			result = result.substring(0, result.length() - 1);
		return result;
	}
	class TextReplacement {
		private String _text;
		private String _replacement;
		TextReplacement(String text, String replacement) {
			_text = text;
			_replacement = replacement;
		}
		public String getText() {
			return _text;
		}
		public String getReplacement() {
			return _replacement;
		}
	}
}
