package org.zkoss.ztl.grammar;

import org.antlr.v4.runtime.tree.TerminalNode;
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

	public List<String[]> getCodeReplacements() {
		return _codeReplacements;
	}

	private Map<String, String> _preCodeReplacements;
	private List<String[]> _codeReplacements;
	private Map<String, String> _variableNameReplacements;

	public ZTLScalaDefaultListener() {
		super();
		_preCodeReplacements = new HashMap<>();
		_codeReplacements = new LinkedList<>();
		_variableNameReplacements = new HashMap<>();
		// use in scope
		_ZTLFunctionReplacements = new HashSet<>();
		_originTextReplacements = new HashSet<>();
		_variableInStatement = new HashSet<>();
		//used to add block or not
		_inCafeConditionStatements = new LinkedList<>();
	}

	@Override
	public void enterClassStatement(ZTLScalaParser.ClassStatementContext ctx) {
	}

	@Override
	public void exitClassStatement(ZTLScalaParser.ClassStatementContext ctx) {
		String statement = ctx.getText();
		statement = statement.substring(0, statement.indexOf("{")).trim();
		String newStatement = statement;
		//classStatement
		List<TerminalNode> identifiers = ctx.Identifier();
		String className = identifiers.get(0).getText();
		if (className.matches(".*Test$")) {
			newStatement = statement.replace(className, className + "Cafe");
			if (identifiers.size() > 1) {
				String extendsClass = identifiers.get(1).getText();
				if (!"ZTL4ScalaTestCase".endsWith(extendsClass) && extendsClass.matches(".*Test$")) {
					newStatement = newStatement.replace(extendsClass, extendsClass + "Cafe");
				}
			}
			_codeReplacements.add(0, new String[]{statement, newStatement});
		}
	}

	private boolean _inAssignment = false;
	private boolean _inVerification = false;
	private Set<TextReplacement> _ZTLFunctionReplacements;
	private Set<TextReplacement> _originTextReplacements;
	private Set<String> _variableInStatement;
	private int jsVarIndex = 0;
	@Override
	public void enterAssignmentStatement(ZTLScalaParser.AssignmentStatementContext ctx) {
		_inAssignment = true;
	}

	@Override
	public void exitAssignmentStatement(ZTLScalaParser.AssignmentStatementContext ctx) {
		String text = ctx.getText().trim();
		if (!text.contains("\"\"\"")) {
			String assignName = "";
			boolean isDeclaration = false;
			String startToken = ctx.anyType() != null ? ctx.anyType().getText() : ctx.Identifier().get(0).getText();
			if ("val".equals(startToken) || "var".equals(startToken)) {
				isDeclaration = true;
				assignName = ctx.Identifier().get(0).getText();
			} else {
				assignName = startToken;
			}
			if (assignName.isEmpty())
				throw new UnsupportedOperationException("Error assignment : " + text);

			String newAssignName = assignName + "_cafe";
			if (isDeclaration || _variableNameReplacements.containsKey(assignName)) {
				StringBuilder replacement = new StringBuilder();
				String exprText = ctx.expression().getText();
				String newExprText = exprText;
				boolean wrapString = false;
				boolean changed = false;
				for (String variable : _variableInStatement) { // ex. t -> t_cafe
					if (_variableNameReplacements.containsKey(variable)) {
						newExprText = replaceCodeText(newExprText, variable, _variableNameReplacements.get(variable));
						wrapString = true;
					}
				}
				List<TextReplacement> innerReplacements = new LinkedList<>();
				if (_ZTLFunctionReplacements.size() > 0) { // do replace: .width() -> .widthStr()
					changed = true; // something need replace
					Set<TextReplacement> textReplacements = _ZTLFunctionReplacements;
					for (TextReplacement tr : textReplacements) {
						String replaceText = tr.getReplacement();
						if (_isConditionalExprExist) {
							wrapString = true;
							String name = newAssignName + "_" + jsVarIndex;
							jsVarIndex++;
							replacement.append("assignment_cafe(\"").append(name).append("\",").append(replaceText).append(", true)\n");
							replaceText = name;
							innerReplacements.add(new TextReplacement(replaceText, name));
						}
						newExprText = newExprText.replace(tr.getText(), replaceText);
						changed = true;
					}
				}

				ZTLScalaParser.AssignmentOperatorContext assignmentOperatorContext = ctx.assignmentOperator();
				boolean isOp = assignmentOperatorContext != null;
				if (isOp) {
					String opExpr = ctx.expression().getText();
					if (!"=".equals(assignmentOperatorContext.getText()) && (opExpr.startsWith("\"") || opExpr.matches("[0-9.]+"))) {
						isOp = true;
					} else {
						isOp = false;
					}
				}

				if (changed || isOp || wrapString) {
					for (TextReplacement tr : innerReplacements) { // replace inner assignment
						newExprText = replaceCodeText(newExprText, tr.getText(), tr.getReplacement());
					}
					replacement.append("assignment_cafe(\"").append(newAssignName).append("\", ");
					if (wrapString) {
						replacement.append("\"" + ZKClientTestCaseCafe.CAFEEVAL);
					}
					if (isOp && assignmentOperatorContext.getText().length() > 1) {
						replacement.append("\"").append(ZKClientTestCaseCafe.CAFEEVAL).append(newAssignName).append(" ")
								.append(assignmentOperatorContext.getText().charAt(0)).append(" \" + ");
					}
					if (wrapString) {
						replacement.append(escapeString(newExprText, true)).append("\"");
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
					addToReplacements(new String[]{text, newText + "\n" + replacement.toString()});
					_variableNameReplacements.put(assignName, newAssignName);
				}
				String logText = "";
//			log(logText + "exitAssignmentStatment (cond:" + _isConditionalExprExist + ") >>> " + text);
			}
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
			_unitFunctionInParseMethod.clear();
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
			if ("dragdropToObject".equals(actionMethod) && exprTotalCnt == 2)
				break;
			if (exprCnt > 0)
				exprReplacement.append(", ");
			if (!"evalScript".equals(actionMethod) && !"setWindowSize".equals(actionMethod) &&
					(exprCnt == 0 || (exprCnt == 1 && "dragdropToObject".equals(actionMethod)))) {
				exprReplacement.append(expr.getText());
				exprCnt++;
				continue; //skip locator
			}
			exprCnt++;
			String exprText = expr.getText();
			String newExprText = exprText;
			String trimExprText = exprText.trim();
			if (trimExprText.matches("^\"((\\\\\")|[^\"])*\"$") || trimExprText.matches("[0-9.]*")) {
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
				for (TextReplacement tr : textReplacements) {
					String replaceText = tr.getReplacement();
					if (_isConditionalExprExist && !tr.getText().equals(replaceText)) {
						String name = "actionVariable_cafe" + "_" + newActionAssignCnt + "_" + jsVarIndex;
						newActionAssignCnt++;
						jsVarIndex++;
						replacement.append("assignment_cafe(\"").append(name).append("\",").append(replaceText).append(", true)\n");
						replaceText = name;
						wrapString = true;
					}
					String targetText = tr.getText();
					if (newExprText.indexOf(targetText) != -1 && !targetText.equals(replaceText) && !newExprText.equals(replaceText)) {
						newExprText =  newExprText.replace(tr.getText(), replaceText);
					}
				}
			}
			if (!exprText.equals(newExprText)) {
				paramChanged = true;
				if (wrapString)
					newExprText = "\"" + escapeString(newExprText, true) + "\"";
			}
			exprReplacement.append(newExprText);
		}

		if (paramChanged) {
			replacement.append(ctx.getChild(0).getText()).append("(").append(exprReplacement).append(")\n");
			addToReplacements(new String[]{text, replacement.toString()});
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

			if ((exprText.trim().startsWith("\"") &&  exprText.trim().endsWith("\"")) || exprText.trim().matches("[0-9.]*")) {
				exprReplacement.append(exprText);
				continue; //skip String & number
			}
			boolean wrapString = false;
			for (String variable: _variableInStatement) { // ex. t -> t_cafe
				if (_variableNameReplacements.containsKey(variable)) {
					String oldExprText = newExprText;
					newExprText = replaceCodeText(newExprText, variable, _variableNameReplacements.get(variable));
					if (!oldExprText.equals(newExprText))
						wrapString = true;
				}
			}
			if (_ZTLFunctionReplacements.size() > 0) { // do replace: .width() -> .widthStr()
				Set<TextReplacement> textReplacements = _ZTLFunctionReplacements;
				for (TextReplacement tr : textReplacements) {
					String replaceText = tr.getReplacement();
					if (_isConditionalExprExist) {
						wrapString = true;
						if (!tr.getText().equals(replaceText)) {
							String name = "verifyVariable_cafe" + "_" + newVerifyAssignCnt + "_" + jsVarIndex;
							newVerifyAssignCnt++;
							jsVarIndex++;
							replacement.append("assignment_cafe(\"").append(name).append("\",").append(replaceText).append(", true)\n");
							replaceText = name;
						}

					}
					String targetText = tr.getText();
					if (newExprText.indexOf(targetText) != -1 && !targetText.equals(replaceText) && !newExprText.equals(replaceText)) {
						newExprText =  newExprText.replace(tr.getText(), replaceText);
					}
				}
			}
			if (!exprText.equals(newExprText)) {
				paramChanged = true;
				if (wrapString)
					newExprText = "\"" + ZKClientTestCaseCafe.CAFEEVAL + escapeString(newExprText, true) + "\"";
			}
			exprReplacement.append(newExprText);
		}

		if (paramChanged) {
			replacement.append(ctx.getChild(0).getText()).append("(").append(exprReplacement).append(")\n");
			addToReplacements(new String[]{text, replacement.toString()});
		}
		_inVerification = false;
		_isConditionalExprExist = false;
		_ZTLFunctionReplacements.clear();
		_variableInStatement.clear();
	}

	private int conditionStatementCnt = 0;
	private List<Boolean> _inCafeConditionStatements; //used to add block or not
	private boolean _inConditionStatementExpression;
	private boolean _ifExprRemoved = true;
	private boolean _inElseStatment = false;

	@Override
	public void exitBlock(ZTLScalaParser.BlockContext ctx) {
		List<ZTLScalaParser.StatementContext> statement = ctx.statement();
		int size = statement.size();
		int cafeConditionSize = _inCafeConditionStatements.size();
		if (size > 0 && cafeConditionSize > 0 && _inCafeConditionStatements.get(cafeConditionSize - 1)) {
			String firstStatement = statement.get(0).getText();
			String preText = ctx.block_pre().getText() + firstStatement;
			if (_inElseStatment && _ifExprRemoved) {
				preText = _elseText + preText;
			}
			_preCodeReplacements.put(preText, "conditionBlock_cafe(true);\n" + firstStatement);
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

	@Override
	public void enterIfThenStatement(ZTLScalaParser.IfThenStatementContext ctx) {
		_inCafeConditionStatements.add(Boolean.TRUE);
		_ifExprRemoved = false;
	}

	@Override
	public void exitIfThenStatement(ZTLScalaParser.IfThenStatementContext ctx) {
		_inCafeConditionStatements.remove(_inCafeConditionStatements.size() - 1);
	}

	@Override
	public void enterElseStatement(ZTLScalaParser.ElseStatementContext ctx) {
		if (_ifExprRemoved) {
			_inCafeConditionStatements.add(Boolean.TRUE);
			_inElseStatment = true;
		}
	}

	private String _elseText = null;
	@Override
	public void exitElseExpression(ZTLScalaParser.ElseExpressionContext ctx) {
		if (_ifExprRemoved) {
			_elseText = ctx.getText();
		}
	}

	@Override
	public void exitElseStatement(ZTLScalaParser.ElseStatementContext ctx) {
		if (_ifExprRemoved) {
			_inCafeConditionStatements.remove(_inCafeConditionStatements.size() - 1);
			_inElseStatment = false;
			_ifExprRemoved = false;
		}
	}

	@Override
	public void enterElseIfStatement(ZTLScalaParser.ElseIfStatementContext ctx) {
		_inCafeConditionStatements.add(Boolean.TRUE);
	}

	@Override
	public void exitElseIfStatement(ZTLScalaParser.ElseIfStatementContext ctx) {
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

	@Override
	public void exitElseIfExpression(ZTLScalaParser.ElseIfExpressionContext ctx) {
		handleIfExpression(ctx.getChild(0).getText(), ctx.getText(), ctx.expression());
	}

	private void handleIfExpression(String cond, String text, ZTLScalaParser.ExpressionContext expression) {
		StringBuilder replacement = new StringBuilder();
		String exprText = expression.getText();
		String newExprText = exprText;
		for (String variable: _variableInStatement) { // ex. t -> t_cafe
			if (_variableNameReplacements.containsKey(variable)) {
				newExprText = replaceCodeText(newExprText, variable, _variableNameReplacements.get(variable));
			}
		}
		if (_ZTLFunctionReplacements.size() > 0) { // do replace: .width() -> .widthStr()
			Set<TextReplacement> textReplacements = _ZTLFunctionReplacements;
			String newAssignName = "conditionStatementExpr_cafe" + conditionStatementCnt++;
			for (TextReplacement tr : textReplacements) {
				String replaceText = tr.getReplacement();
				if (_isConditionalExprExist && !tr.getText().equals(replaceText)) {
					String name = newAssignName + "_" + jsVarIndex;
					jsVarIndex++;
					replacement.append("assignment_cafe(\"").append(name).append("\",").append(replaceText).append(", true)\n");
					replaceText = name;
				}
				String targetText = tr.getText();
				if (newExprText.indexOf(targetText) != -1 && !targetText.equals(newExprText)) {
					newExprText = newExprText.replace(tr.getText(), replaceText); // Same key risk? replace xxx.width() is ok
				}
			}
		}
		if (!exprText.equals(newExprText)) {
			replacement.append("conditionStatement_cafe(\"").append(cond).append("\", \"");
			replacement.append(escapeString(newExprText, true)).append("\")\n");
			addToReplacements(new String[]{text, replacement.toString()});
			_ifExprRemoved = true;
		} else {
			_inCafeConditionStatements.add(_inCafeConditionStatements.size() - 1, Boolean.FALSE);
			_inCafeConditionStatements.remove(_inCafeConditionStatements.size() - 1);
		}
		String logText = "";
//		log(logText + "handleIfExpression >>> " + text);
		_inConditionStatementExpression = false;
		_isConditionalExprExist = false;
		_ZTLFunctionReplacements.clear();
		_variableInStatement.clear();
	}

	@Override
	public void exitZtlTestMethod(ZTLScalaParser.ZtlTestMethodContext ctx) {
		String text = ctx.getText().trim();
		String replacement = text.replace("this.", "");
		if (ctx.ztlTestEvalMethod() == null) {
			String methodName = text.contains("this.") ? ctx.getChild(1).getText() : ctx.getChild(0).getText();
			if (!ZTLTestStringFunctionList.contains(methodName)) {
				replacement = text.replace(methodName, methodName + "_cafeStr");
			}
		}
		if (replacement.isEmpty())
			log("ERROR: exitZtlTestMethod");
		else
			_ZTLFunctionReplacements.add(new TextReplacement(text, replacement));
	}

	private static List<String> ZTLTestStringFunctionList = new ArrayList<>();
	static {
		ZTLTestStringFunctionList.add("getAlertMessage");
		ZTLTestStringFunctionList.add("getText");
		ZTLTestStringFunctionList.add("getZKLog");
		ZTLTestStringFunctionList.add("getEval");
	}

	@Override
	public void exitZtlUnitMethod(ZTLScalaParser.ZtlUnitMethodContext ctx) {
		if (_inAssignment || _inVerification || _inAction || _inConditionStatementExpression) {
			String text = ctx.getText().trim();
			String replacement = text;
			if (ctx.ztlUnitEvalMethod() == null) {
				String methodName = ctx.getChild(1).getText();
				if (!unitJSStringFunctionList.contains(methodName)) {
					replacement = text.replace(methodName, methodName + "_cafeStr");
				}
			}
			TextReplacement t = new TextReplacement(text, replacement);
			if (_inParseMethod) {
				_unitFunctionInParseMethod.put(text, t);
			} else
				_ZTLFunctionReplacements.add(t);
		}
	}

	private static List<String> unitJSStringFunctionList = new ArrayList<>();
	static {
		unitJSStringFunctionList.add(".css");
		unitJSStringFunctionList.add(".attr");
		unitJSStringFunctionList.add(".text");
		unitJSStringFunctionList.add(".html");
		unitJSStringFunctionList.add(".`val`");
		unitJSStringFunctionList.add(".uuid");
		unitJSStringFunctionList.add(".id");
		unitJSStringFunctionList.add(".get");
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
		target = target.replaceAll("\\(", "\\\\\\\\(");
		// before and after the target, those characters should not be 1-9a-zA-Z
		String result = text.replaceAll("([^a-zA-Z0-9\\-_\\$\"])" + escapeString(target, false) + "([^a-zA-Z0-9\\-_\\$\"(])", replacement);
		if (pre)
			result = result.substring(1);
		if (suf)
			result = result.substring(0, result.length() - 1);
		return result;
	}

	private String escapeString(String text, boolean replaceReturn) {
		if (replaceReturn)
			text = text.replaceAll("\n", "");
		return text.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"");
	}

	private void addToReplacements(String[] replacement) {
		String[] replacementOriginArr = replacement[0].split("\n");
		for (int i = 0; i < replacementOriginArr.length - 1; i++) {
			_codeReplacements.add(new String[] {replacementOriginArr[i], ""});
		}
		_codeReplacements.add(new String[] {replacementOriginArr[replacementOriginArr.length - 1], replacement[1]});
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
