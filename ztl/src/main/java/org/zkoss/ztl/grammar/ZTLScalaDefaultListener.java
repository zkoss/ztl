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
	public Map<String, String> getCodeReplacements() {
		return _codeReplacements;
	}

	private Scope _scope;
	private Scope _currentScope;
	private Map<String, String> _codeReplacements;
	private Map<String, String> _variableNameReplacements;

	public ZTLScalaDefaultListener() {
		super();
		_scope = new Scope("root", null);
		_currentScope = _scope;
		_codeReplacements = new HashMap<>();
		_variableNameReplacements = new HashMap<>();
		// use in scope
		_ZTLFunctionReplacements = new HashSet<>();
		_originTextReplacements = new HashSet<>();
		_variableInStatement = new HashSet<>();
	}

	private int _scopeCnt = 0;
	@Override
	public void enterClassStatement(ZTLScalaParser.ClassStatementContext ctx) {
		_currentScope = new Scope("class" + _scopeCnt++, _currentScope);
	}

	@Override
	public void exitClassStatement(ZTLScalaParser.ClassStatementContext ctx) {
		//classStatement
		String className = ctx.Identifier().get(0).getText();
		_codeReplacements.put(className, className + "$cafe");
		_currentScope = _currentScope.getParent();
	}

	@Override
	public void enterDefStatement(ZTLScalaParser.DefStatementContext ctx) {
		_currentScope = new Scope("def" + _scopeCnt++, _currentScope);
	}

	@Override
	public void exitDefStatement(ZTLScalaParser.DefStatementContext ctx) {
		_currentScope = _currentScope.getParent();
	}

	@Override
	public void enterBlock(ZTLScalaParser.BlockContext ctx) {
		if (_inAssignment) {
			_currentScope = new Scope("assignment" + _scopeCnt++, _currentScope);
		} //else -> in conditionStatement
	}

	@Override
	public void exitBlock(ZTLScalaParser.BlockContext ctx) {
		_currentScope = _currentScope.getParent();
	}

	@Override
	public void enterConditionBodyStatements(ZTLScalaParser.ConditionBodyStatementsContext ctx) {
		_currentScope = new Scope("condition" + _scopeCnt++, _currentScope);
	}

	@Override
	public void exitConditionBodyStatements(ZTLScalaParser.ConditionBodyStatementsContext ctx) {
		_currentScope = _currentScope.getParent();
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

	private void recordId(String name, String type) {
		_currentScope.getIds().add(new ID(name, type, _currentScope));
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
			for (String variable: _variableInStatement) { // ex. t -> t_cafe
				if (_variableNameReplacements.containsKey(variable)) {
					newExprText = replaceCodeText(exprText, variable, _variableNameReplacements.get(variable));
				}
			}
			if (_ZTLFunctionReplacements.size() > 0) { // do replace: .width() -> .widthStr()
				Set<TextReplacement> textReplacements = _ZTLFunctionReplacements;
				int paramCnt = 0;
				for (TextReplacement tr : textReplacements) {
					String replaceText = tr.getReplacement();
					if (_isConditionalExprExist) {
						String name = newAssignName + "_" + paramCnt;
						replacement.append("assignment_cafe(\"").append(name).append("\",").append(replaceText).append(", true)\n");
						replaceText = name;
						recordId(name, "cafe"); //record id
					}
					String targetText = tr.getText();
					while (newExprText.indexOf(targetText) != -1) {
						newExprText = newExprText.replace(tr.getText(), replaceText); // Same key risk? replace xxx.width() is ok
					}
					paramCnt++;
				}
			}

			if (!exprText.equals(newExprText)) {
				replacement.append("assignment_cafe(\"").append(newAssignName).append("\", ");
				if (ctx.otherAssignmentSymbol() != null) {
					replacement.append(newAssignName).append(" ")
							.append(ctx.otherAssignmentSymbol().getChild(0).getText()).append(" ");
				}
				replacement.append(newExprText).append(", ").append(isDeclaration ? "true" : "false").append(")\n");
				recordId(newAssignName, "cafe"); //record id
				String newText = text;
				for (TextReplacement tr : _originTextReplacements) {
					while (newText.indexOf(tr.getText()) != -1) {
						newText = newText.replace(tr.getText(), tr.getReplacement());
					}
				}
				_codeReplacements.put(text, newText + "\n" + replacement.toString());
				_variableNameReplacements.put(assignName, newAssignName);
			} else {

			}
			String logText = "";
			log(logText + "exitAssignmentStatment (cond:" + _isConditionalExprExist + ") >>> " + text);
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
		if (_inAssignment || _inVerification) _isConditionalExprExist = true;
	}

	@Override
	public void exitVariable(ZTLScalaParser.VariableContext ctx) {
		if (_inAssignment || _inVerification)
			_variableInStatement.add(ctx.getText());
	}

	@Override
	public void exitParseMethod(ZTLScalaParser.ParseMethodContext ctx) {
		if (_inAssignment || _inVerification) {
			String text = ctx.getText();
			String replacement = "";
			String originTextReplacement;
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
				originTextReplacement = "parse" + ctx.getChild(0).getChild(2).getText().replace("to", "") + "(" + expr + ")";
				_originTextReplacements.add(new TextReplacement(text, originTextReplacement));
			}
			_ZTLFunctionReplacements.add(new TextReplacement(text, replacement));
		}
	}

	@Override
	public void exitSpecialMethod(ZTLScalaParser.SpecialMethodContext ctx) {
		if (ctx.ztlUnitMethod() == null) {
			log("exitSpecialMethod" + " >>> " + ctx.getText());
		}
		// inside function call or assignment
		//contains -> strContains
		//equals -> strEquals
		//
		//Math.xxx(ooo) -> "Math.xxx(ooo)"
	}

	@Override
	public void enterFunctionCallStatement(ZTLScalaParser.FunctionCallStatementContext ctx) {
	}
	@Override
	public void exitFunctionCallStatement(ZTLScalaParser.FunctionCallStatementContext ctx) {
		String text = ctx.getText();
		if (text.trim().startsWith("waitResponse") || text.trim().startsWith("runZTL")) {
			return;
		}
		StringBuilder replacement = new StringBuilder();
		log("exitFunctionCallStatement" + " >>> " + text);
	}

	@Override
	public void exitIfThenElseStatement(ZTLScalaParser.IfThenElseStatementContext ctx) {
		log("exitIfThenElseStatement");
	}

	@Override
	public void exitForStatement(ZTLScalaParser.ForStatementContext ctx) {
		log("exitForStatement");
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
					while (newExprText.indexOf(targetText) != -1) {
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
	@Override
	public void exitIfThenStatement(ZTLScalaParser.IfThenStatementContext ctx) {

	}

	@Override
	public void exitElseStatement(ZTLScalaParser.ElseStatementContext ctx) {

	}

	@Override
	public void exitElseIfStatement(ZTLScalaParser.ElseIfStatementContext ctx) {

	}


	@Override
	public void enterForStatement(ZTLScalaParser.ForStatementContext ctx) {
		super.enterForStatement(ctx);
	}

	@Override
	public void enterForCondition(ZTLScalaParser.ForConditionContext ctx) {
		super.enterForCondition(ctx);
	}

	@Override
	public void exitForCondition(ZTLScalaParser.ForConditionContext ctx) {
		super.exitForCondition(ctx);
	}

	@Override
	public void enterWhileStatement(ZTLScalaParser.WhileStatementContext ctx) {
		super.enterWhileStatement(ctx);
	}

	@Override
	public void exitWhileStatement(ZTLScalaParser.WhileStatementContext ctx) {
		super.exitWhileStatement(ctx);
	}

	@Override
	public void enterStatement(ZTLScalaParser.StatementContext ctx) {
		super.enterStatement(ctx);
	}

	@Override
	public void exitZtlTestMethod(ZTLScalaParser.ZtlTestMethodContext ctx) {
		String text = ctx.getText().trim();
		String replacement = text.replace("this.", "");
		for (String checkFunction : ZTLFunctionCheckList) {
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

	private static List<String> ZTLFunctionCheckList = new ArrayList<>();
	static {
		ZTLFunctionCheckList.add("getAlertMessage");
		ZTLFunctionCheckList.add("hasError");
		ZTLFunctionCheckList.add("getText");
		ZTLFunctionCheckList.add("isVisible");
		ZTLFunctionCheckList.add("hasNativeScroll");
		ZTLFunctionCheckList.add("hasHScrollbar");
		ZTLFunctionCheckList.add("hasVScrollbar");
		ZTLFunctionCheckList.add("getScrollTop");
		ZTLFunctionCheckList.add("getScrollLeft");
		ZTLFunctionCheckList.add("getZKLog");
		ZTLFunctionCheckList.add("is");
		ZTLFunctionCheckList.add("getWindowWidth");
		ZTLFunctionCheckList.add("getWindowHeight");
		ZTLFunctionCheckList.add("getBrowserTabCount");
	}

	@Override
	public void exitZtlUnitMethod(ZTLScalaParser.ZtlUnitMethodContext ctx) {
		String text = ctx.getText().trim();
		String replacement = "";
		//TODO: check start with jq, zk, element
		for (String checkFunction : unitJSFunctionCheckList) {
			String checkToken = "." + checkFunction;
			int index = text.indexOf(checkToken);
			if (index != -1) {
				replacement = text.replace(checkToken, checkToken + "_cafeStr");
				break;
			}
		}
		if (replacement.isEmpty())
			log("ERROR: exitZtlUnitMethod");
		else
			_ZTLFunctionReplacements.add(new TextReplacement(text, replacement));
	}

	private static List<String> unitJSFunctionCheckList = new ArrayList<>();
	static {
		unitJSFunctionCheckList.add("hasClass");
		unitJSFunctionCheckList.add("is");
		unitJSFunctionCheckList.add("isVisible");
		unitJSFunctionCheckList.add("exists");
		unitJSFunctionCheckList.add("height");
		unitJSFunctionCheckList.add("width");
		unitJSFunctionCheckList.add("innerHeight");
		unitJSFunctionCheckList.add("innerWidth");
		unitJSFunctionCheckList.add("outerWidth");
		unitJSFunctionCheckList.add("outerHeight");
		unitJSFunctionCheckList.add("length");
		unitJSFunctionCheckList.add("scrollbarWidth");
		unitJSFunctionCheckList.add("offsetLeft");
		unitJSFunctionCheckList.add("offsetTop");
		unitJSFunctionCheckList.add("positionLeft");
		unitJSFunctionCheckList.add("positionTop");
		unitJSFunctionCheckList.add("scrollTop");
		unitJSFunctionCheckList.add("scrollLeft");
		unitJSFunctionCheckList.add("scrollHeight");
		unitJSFunctionCheckList.add("scrollWidth");
		unitJSFunctionCheckList.add("nChildren");
	}

	private String replaceCodeText(String text, String target, String replacement) {
		replacement = "$1" + replacement + "$2";
		// before and after the target, those characters should not be 1-9a-zA-Z
		return text.replaceAll("([^a-zA-Z0-9\\-_\\$])" + target + "([^a-zA-Z0-9\\-_\\$])?", replacement);
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

	class ID {
		private String _identifier;
		private String _type;
		private Scope _scope;

		ID(String identifier, String type, Scope scope) {
			_identifier = identifier;
			_type = type;
			_scope = scope;
		}

		public String getIdentifier() {
			return _identifier;
		}

		public void setIdentifier(String identifier) {
			this._identifier = identifier;
		}

		public String getType() {
			return _type;
		}

		public void setType(String type) {
			this._type = type;
		}

		public Scope getScope() {
			return _scope;
		}

		public void setScope(Scope scope) {
			this._scope = scope;
		}
	}

	class Scope {
		private String _scopeID;
		private List<ID> _ids;
		private Scope _parent;

		Scope(String scopeID, Scope parent) {
			_scopeID = scopeID;
			_ids = new LinkedList<>();
			_parent = parent;
		}

		public String getScopeID() {
			return _scopeID;
		}

		public void setScopeID(String scopeID) {
			this._scopeID = scopeID;
		}

		public List<ID> getIds() {
			return _ids;
		}

		public void setIds(List<ID> ids) {
			this._ids = ids;
		}

		public Scope getParent() {
			return _parent;
		}

		public void setParent(Scope parent) {
			this._parent = parent;
		}
	}
}
