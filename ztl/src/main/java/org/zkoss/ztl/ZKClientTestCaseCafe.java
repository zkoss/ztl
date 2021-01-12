package org.zkoss.ztl;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.zkoss.ztl.testcafe.CafeTestStep;
import org.zkoss.ztl.testcafe.JQuery$Cafe;
import org.zkoss.ztl.testcafe.KeyHelper;
import org.zkoss.ztl.testcafe.Widget$Cafe;
import org.zkoss.ztl.testcafe.ZK$Cafe;
import org.zkoss.ztl.unit.ClientWidget;
import org.zkoss.ztl.unit.JQuery;
import org.zkoss.ztl.unit.Widget;
import org.zkoss.ztl.unit.ZK;
import org.zkoss.ztl.util.Scripts;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * A transpiler to write test cafe in ztl
 * @author jameschu
 *
 */
public class ZKClientTestCaseCafe extends ZKClientTestCase {
	final public static String CAFEEVAL = "ZTLCafeEval=>";
	final private static String AWAIT_TOKEN = "await ";
	private static boolean _isTestCafe = false;
	protected List<CafeTestStep> testCodeList = null;

	public void enableTestCafe() {
		_isTestCafe = true;
		if (testCodeList == null)
			testCodeList = new LinkedList<>();
	}

	public void disableTestCafe() {
		_isTestCafe = false;
		testCodeList = null;
	}

	public void generateCafeTest(String targetUrl, String folder, String annotIgnoreBrowsers) throws IllegalArgumentException {
		String testName = this.getClass().getSimpleName().replace("_", "-");
		StringBuilder testContent = new StringBuilder();
		testContent.append("import {ClientFunction, Selector} from 'testcafe';\n")
				.append("import * as ztl from './module/ztl.js';\n")
				.append("fixture `ZTL TEST - ").append(testName).append("`.page `").append(targetUrl)
				.append("`;\ntest('").append(testName)
				.append("', async t => {\n")
				.append("await t.maximizeWindow();\nawait ztl.waitResponse(t);\n")
				.append("if (await ztl.isBrowserIgnored('").append(annotIgnoreBrowsers)
				.append("')) {console.log('This issue is ignored in current browser! (")
				.append(annotIgnoreBrowsers)
				.append(")'); return;} \n");
		String lastStepType = null;
		for (CafeTestStep step : testCodeList) {
			String type = step.getType();
			if (CafeTestStep.ACTION.equals(lastStepType) && !CafeTestStep.ACTION.equals(type)) {
				testContent.append(";\n");
			}
			if (CafeTestStep.ACTION.equals(type)) {
				if (!CafeTestStep.ACTION.equals(lastStepType))
					testContent.append("await t");
				testContent.append(".").append(step.getContent());
			} else if (CafeTestStep.EVAL.equals(type)) {
				testContent.append(step.getContent()).append(";\n");
			} else if (CafeTestStep.ASSERTION.equals(type)) {
				testContent.append("await t.").append(step.getContent()).append(";\n");
			} else if (CafeTestStep.PRE.equals(type)) {
				testContent.append(step.getContent()).append("\n");
			} else {
				throw new IllegalArgumentException("Not support step type : " + type);
			}
			lastStepType = type;
		}
		testContent.append("});");

		//write file
		try {
			String path = folder + testName + ".js";
			File dir = new File(path);
			if (!dir.isFile()) {
				dir.createNewFile();
			}
			FileWriter writer = new FileWriter(dir);

			writer.write(modifyOutputContent(testContent.toString()));
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		testCodeList.clear();
	}

	private String modifyOutputContent(String content) {
		String[] lines = content.split("\n");
		Set<String> identifiers = new HashSet<>();
		Map<String, String> identifierToMap = new HashMap<>();
		StringBuilder newContent = new StringBuilder();
		int midIndex = -1;
		for (String line: lines) {
			String lineTrim = line.trim();
			if (lineTrim.startsWith("let")) {
				String identifier = lineTrim.split("=")[0].replace("let ", "").trim();
				if (identifiers.contains(identifier)) {
					String newIdentifier = identifier;
					if (identifierToMap.containsKey(identifier)) {
						newIdentifier = identifierToMap.get(identifier);
					}
					identifierToMap.put(identifier, newIdentifier + "t");

				} else {
					identifiers.add(identifier);
				}
			}
			for (String identifier : identifiers) {
				if (line.contains(identifier) && identifierToMap.containsKey(identifier)) {
					line = line.replaceAll(identifier + "([^_])", identifierToMap.get(identifier) + "$1");
				}
			}
			newContent.append(line).append("\n");
		}
		return newContent.toString();
	}

	//selector
	private String toCafeSelector(String locatorStr) {
		StringBuilder codeStr = new StringBuilder();
		codeStr.append("Selector(() => ");
		codeStr.append(locatorStr);
		if (!locatorStr.matches(".*\\.\\$n\\([^\\(\\)]*\\)$")) {
			if (locatorStr.startsWith("zk.Desktop._dt") || locatorStr.startsWith("zk.Widget.$")) {
				codeStr.append(".$n()");
			} else if (!locatorStr.matches(".*\\.get\\([0-9]+\\)$") && !locatorStr.endsWith("]")) {
				codeStr.append("[0]");
			}
		}
		codeStr.append(")");
		return codeStr.toString();
	}

	//waitResponse()
	@Override
	protected void waitResponse(int timeout, boolean includingAnimation) {
		if (!_isTestCafe) {
			super.waitResponse(_timeout, includingAnimation);
			return;
		}
		if (timeout != _timeout)
			testCodeList.add(new CafeTestStep(CafeTestStep.EVAL, "await t.wait(" + timeout + ")"));
		testCodeList.add(new CafeTestStep(CafeTestStep.EVAL, "await ztl.waitResponse(t)"));
	}

	/**
	  Actions in testcafe api
	 TODO:
	 Navigate
	 Select Text
	 Upload
	 Take Screenshot
	 Action Options
	 */

	@Override
	public void focus(ClientWidget locator) {
		if (!_isTestCafe) {
			super.focus(locator);
			return;
		}
		Scripts.doCafeEval(locator.toLocator() + ".focus();", testCodeList);
	}

	/**
	 * Click (check
	 * Double Click
	 * Right Click
	 */

	@Override
	public void check(ClientWidget locator) {
		if (!_isTestCafe) {
			super.check(locator);
			return;
		}
		click(locator);
	}

	@Override
	public void click(ClientWidget locator) {
		if (!_isTestCafe) {
			super.click(locator);
			return;
		}
		cafeClick("click", locator.toLocator());
	}

	@Override
	public void clickAt(ClientWidget locator, String coordString) {
		if (!_isTestCafe) {
			super.clickAt(locator, coordString);
			return;
		}
		cafeClick("click", locator.toLocator(), coordString);
	}

	@Override
	public void contextMenu(ClientWidget locator) {
		if (!_isTestCafe) {
			super.contextMenu(locator);
			return;
		}
		cafeClick("rightClick", locator.toLocator());
	}

	@Override
	public void contextMenuAt(ClientWidget locator, String coordString) {
		if (!_isTestCafe) {
			super.contextMenuAt(locator, coordString);
			return;
		}
		cafeClick("rightClick", locator.toLocator(), coordString);
	}

	@Override
	public void doubleClick(ClientWidget locator) {
		if (!_isTestCafe) {
			super.doubleClick(locator);
			return;
		}
		cafeClick("doubleClick", locator.toLocator());
	}

	@Override
	public void doubleClickAt(ClientWidget locator, String coordString) {
		if (!_isTestCafe) {
			super.doubleClickAt(locator, coordString);
			return;
		}
		cafeClick("doubleClick", locator.toLocator(), coordString);
	}

	@Override
	public void shiftClickItems(List<ClientWidget> locators) {
		if (!_isTestCafe) {
			super.shiftClickItems(locators);
			return;
		}
		for (ClientWidget locator : locators) {
			StringBuilder codeStr = new StringBuilder();
			codeStr.append("click(");
			codeStr.append(toCafeSelector(locator.toLocator()));
			codeStr.append(", {modifiers: {shift: true}}");
			codeStr.append(")");
			testCodeList.add(new CafeTestStep(CafeTestStep.ACTION, codeStr.toString()));
		}
	}

	private void cafeClick(String action, String selectorStr) {
		cafeClick(action, selectorStr, null);
	}

	private void cafeClick(String action, String selectorStr, String coordString) {
		StringBuilder codeStr = new StringBuilder();
		codeStr.append(action);
		codeStr.append("(");
		codeStr.append(toCafeSelector(selectorStr));
		if (coordString != null) {
			testCodeList.add(new CafeTestStep(CafeTestStep.PRE, genCoordStringExpr(coordString)));
			String coordVar = coordArrayIdentifier + coordArrayVarCnt;
			codeStr.append(", {offsetX: ").append(coordVar).append("[0], offsetY: ").append(coordVar).append("[1]}");
		}
		codeStr.append(")");
		testCodeList.add(new CafeTestStep(CafeTestStep.ACTION, codeStr.toString()));
	}

	/**
	 * Mouse move
	 */
	@Override
	public void mouseDown(ClientWidget locator) {
		if (!_isTestCafe) {
			super.mouseDown(locator);
			return;
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Override
	public void mouseDownAt(ClientWidget locator, String coordString) {
		if (!_isTestCafe) {
			super.mouseDownAt(locator, coordString);
			return;
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Override
	public void mouseDownRight(ClientWidget locator) {
		if (!_isTestCafe) {
			super.mouseDownRight(locator);
			return;
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Override
	public void mouseDownRightAt(ClientWidget locator, String coordString) {
		if (!_isTestCafe) {
			super.mouseDownRightAt(locator, coordString);
			return;
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Override
	public void mouseMove(ClientWidget locator) {
		if (!_isTestCafe) {
			super.mouseMove(locator);
			return;
		}
		mouseOver(locator);
	}

	@Override
	public void mouseOut(ClientWidget locator) {
		if (!_isTestCafe) {
			super.mouseOut(locator);
			return;
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Override
	public void mouseUp(ClientWidget locator) {
		if (!_isTestCafe) {
			super.mouseUp(locator);
			return;
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Override
	public void mouseUpAt(ClientWidget locator, String coordString) {
		if (!_isTestCafe) {
			super.mouseUpAt(locator, coordString);
			return;
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Override
	public void mouseUpRight(ClientWidget locator) {
		if (!_isTestCafe) {
			super.mouseUpRight(locator);
			return;
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Override
	public void mouseUpRightAt(ClientWidget locator, String coordString) {
		if (!_isTestCafe) {
			super.mouseUpRightAt(locator, coordString);
			return;
		} else {

		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	/**
	 * Drag Element
	 */

	@Override
	public void dragAndDrop(ClientWidget locator, String movementsString) {
		if (!_isTestCafe) {
			super.dragAndDrop(locator, movementsString);
			return;
		}
		cafeDrag(locator.toLocator(), movementsString);
	}

	@Override
	public void dragdrop(ClientWidget locator, String movementsString) {
		if (!_isTestCafe) {
			super.dragdrop(locator, movementsString);
			return;
		}
		cafeDrag(locator.toLocator(), movementsString);
	}

	@Override
	public void dragdropTo(ClientWidget locatorOfObjectToBeDragged, String from, String to) {
		if (!_isTestCafe) {
			super.dragdropTo(locatorOfObjectToBeDragged, from, to);
			return;
		}
		cafeDrag(locatorOfObjectToBeDragged.toLocator(), to, from);
	}

	@Override
	public void dragdropToObject(ClientWidget locatorOfObjectToBeDragged, ClientWidget locatorOfDragDestinationObject,
			String from, String to) {
		if (!_isTestCafe) {
			super.dragdropToObject(locatorOfObjectToBeDragged, locatorOfDragDestinationObject, from, to);
			return;
		}
		cafeDragToElement(locatorOfObjectToBeDragged.toLocator(), locatorOfDragDestinationObject.toLocator(), from, to);
	}

	@Override
	public void dragAndDropToObject(ClientWidget locatorOfObjectToBeDragged,
			ClientWidget locatorOfDragDestinationObject) {
		if (!_isTestCafe) {
			super.dragAndDropToObject(locatorOfObjectToBeDragged, locatorOfDragDestinationObject);
			return;
		}
		cafeDragToElement(locatorOfObjectToBeDragged.toLocator(), locatorOfDragDestinationObject.toLocator());
	}

	private void cafeDrag(String selectorStr, String movementsString) {
		cafeDrag(selectorStr, movementsString, null);
	}

	private void cafeDrag(String selectorStr, String movementsString, String from) {
		StringBuilder codeStr = new StringBuilder();
		codeStr.append("drag(").append(toCafeSelector(selectorStr)).append(",");
		testCodeList.add(new CafeTestStep(CafeTestStep.PRE, genCoordStringExpr(movementsString)));
		String coordVar = coordArrayIdentifier + coordArrayVarCnt;
		if (from != null) {
			testCodeList.add(new CafeTestStep(CafeTestStep.PRE, genCoordStringExpr(from, true)));
			String coordVarFrom = coordArrayIdentifier + coordArrayVarCnt;
			codeStr.append(coordVar).append("[0] - ").append(coordVarFrom).append("[0], ")
					.append(coordVar).append("[1] - ").append(coordVarFrom).append("[1]")
					.append(", {offsetX: ").append(coordVarFrom).append("[0], offsetY: ")
					.append(coordVarFrom).append("[1]}");
		} else {
			codeStr.append(coordVar).append("[0], ").append(coordVar).append("[1]");
		}
		codeStr.append(")");
		testCodeList.add(new CafeTestStep(CafeTestStep.ACTION, codeStr.toString()));
	}

	private void cafeDragToElement(String fromSelectorStr, String toSelectorStr) {
		cafeDragToElement(fromSelectorStr, toSelectorStr, null, null);
	}

	private void cafeDragToElement(String fromSelectorStr, String toSelectorStr, String from, String to) {
		StringBuilder codeStr = new StringBuilder();
		codeStr.append("dragToElement(").append(toCafeSelector(fromSelectorStr)).append(",").append(toCafeSelector(toSelectorStr));
		if (from != null && to != null) {
			testCodeList.add(new CafeTestStep(CafeTestStep.PRE, genCoordStringExpr(from, true)));
			String coordVarFrom = coordArrayIdentifier + coordArrayVarCnt;
			testCodeList.add(new CafeTestStep(CafeTestStep.PRE, genCoordStringExpr(to)));
			String coordVarTo = coordArrayIdentifier + coordArrayVarCnt;
			codeStr.append(", {offsetX: ").append(coordVarFrom).append("[0], offsetY: ").append(coordVarFrom).append("[1]");
			codeStr.append(", destinationOffsetX: ").append(coordVarTo).append("[0], destinationOffsetY: ").append(coordVarTo).append("[1]}");
		}
		codeStr.append(")");
		testCodeList.add(new CafeTestStep(CafeTestStep.ACTION, codeStr.toString()));
	}

	/**
	 * Hover
	 */
	@Override
	public void mouseOver(By locator) {
		if (!_isTestCafe) {
			super.mouseOver(locator);
			return;
		}
		cafeHover(locator.toString(), null);
	}

	public void mouseMoveAt(ClientWidget locator, String coordString) {
		if (!_isTestCafe) {
			super.mouseMoveAt(locator, coordString);
			return;
		}
		cafeHover(locator.toString(), coordString);
	}

	private void cafeHover(String selectorStr, String coordString) {
		StringBuilder codeStr = new StringBuilder();
		codeStr.append("hover(");
		codeStr.append(toCafeSelector(selectorStr));
		if (coordString != null) {
			testCodeList.add(new CafeTestStep(CafeTestStep.PRE, genCoordStringExpr(coordString)));
			String coordVar = coordArrayIdentifier + coordArrayVarCnt;
			codeStr.append(", {offsetX: ").append(coordVar).append("[0], offsetY: ").append(coordVar).append("[1]}");
		}
		codeStr.append(")");
		testCodeList.add(new CafeTestStep(CafeTestStep.ACTION, codeStr.toString()));
	}

	/**
	 * Resize Window
	 */
	@Override
	public void windowResizeTo(int width, int height) {
		if (!_isTestCafe) {
			super.windowResizeTo(width, height);
			return;
		}
		cafeResizeWindow(String.valueOf(width), String.valueOf(height));
	}

	public void windowResizeTo(Object width, Object height) {
		if (!_isTestCafe) {
			throw new UnsupportedOperationException("Not support in test cafe");
		}
		cafeResizeWindow(String.valueOf(width), String.valueOf(height));
	}

	private void cafeResizeWindow(String width, String height) {
		testCodeList.add(new CafeTestStep(CafeTestStep.ACTION, "resizeWindow(" + width + "," + height + ")"));
	}

	/**
	 * Type Text
	 * Press Key
	 */
	@Override
	@Deprecated /* Not support in test cafe */
	public void keyDown(ClientWidget locator, String keySequence) {
		if (!_isTestCafe) {
			super.keyDown(locator, keySequence);
			return;
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Override
	@Deprecated /* Not support in test cafe */
	public void keyPress(ClientWidget locator, String keySequence) {
		if (!_isTestCafe) {
			super.keyPress(locator, keySequence);
			return;
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Override
	@Deprecated /* Not support in test cafe */
	public void keyPressNative(String keycode) {
		if (!_isTestCafe) {
			super.keyPressNative(keycode);
			return;
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	public void sendKeys(ClientWidget locator, CharSequence... keysToSend) {
		if (!_isTestCafe) {
			super.sendKeys(locator, keysToSend);
			return;
		}

		StringBuilder codeStr = new StringBuilder();
		codeStr.append("pressKey(");
		int cnt = 0;
		boolean allInKeys = true;
		boolean isModifiedKey = false;
		boolean isVariable = false;
		if (keysToSend.length == 1 && keysToSend[0].toString().contains("_cafe")) {
			isVariable = true;
			allInKeys = false;
			codeStr.append("await ztl.splitInputText(").append(keysToSend[0].toString()).append("))");
		} else {
			codeStr.append("'");
			for (CharSequence s : keysToSend) {
				if (!(s instanceof Keys))
					allInKeys = false;
				String keyString = s.toString();
				if (s instanceof Keys) {
					keyString = KeyHelper.getKeys(keyString);
					isModifiedKey = ("alt".equals(keyString) || "ctrl".equals(keyString) || "shift".equals(keyString));
				} else {
					StringBuilder k = new StringBuilder();
					int keyStrLength = keyString.length();
					for (int i = 0; i < keyStrLength; i++) {
						if (i > 0) {
							if (isModifiedKey) {
								k.append("+");
								isModifiedKey = false;
							} else
								k.append(" ");
						}
						String keyChar = keyString.substring(i, i + 1);
						if (" ".equals(keyChar)) {
							keyChar = "space";
						} else {
							keyChar = KeyHelper.getKeys(keyChar);
							isModifiedKey = ("alt".equals(keyChar) || "ctrl".equals(keyChar) || "shift".equals(keyChar));
						}
						k.append(keyChar);
					}
					keyString = k.toString();
				}
				if (cnt > 0) {
					if (isModifiedKey) {
						codeStr.append("+");
						isModifiedKey = false;
					} else
						codeStr.append(" ");
				} else
					cnt++;
				codeStr.append(keyString);
			}
			codeStr.append("')");
		}
		if (!allInKeys) {
			String s = locator.toLocator();
			testCodeList.add(new CafeTestStep(CafeTestStep.PRE, "if (await ClientFunction(() => jq(" + s + ")[0] != document.activeElement)())"));
			click(locator);
			waitResponse();
		}
		testCodeList.add(new CafeTestStep(CafeTestStep.ACTION, codeStr.toString()));
	}

	@Override
	public void keyPressEnter(ClientWidget locator) {
		if (!_isTestCafe) {
			super.keyPressEnter(locator);
			return;
		}
		//click + press enter
		click(locator);
		cafePressKey("enter");
	}

	@Override
	@Deprecated /* Not support in test cafe */
	public void keyUp(ClientWidget locator, String keySequence) {
		if (!_isTestCafe) {
			super.keyUp(locator, keySequence);
			return;
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	//clear and type
	@Override
	public void type(ClientWidget locator, String value) {
		if (!_isTestCafe) {
			super.type(locator, value);
			return;
		}
		jq(locator).toElement().set("value", "");
		waitResponse();
		cafeType(locator, value, true);
	}

	//append
	@Override
	public void typeKeys(ClientWidget locator, String value) {
		if (!_isTestCafe) {
			super.typeKeys(locator, value);
			return;
		}
		cafeType(locator, value , false);
	}

	@Override
	@Deprecated /* Not support in test cafe */
	public void longPress(ClientWidget locator) {
		if (!_isTestCafe) {
			super.longPress(locator);
			return;
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	private void cafePressKey(String key) {
		testCodeList.add(new CafeTestStep(CafeTestStep.ACTION, "pressKey('" + key + "')"));
	}

	private void cafeType(ClientWidget locator, String text, boolean doTab) {
		if (text == null || text.length() == 0) {
			click(locator);
			waitResponse();
			jq(locator).toElement().set("value", "");
		} else {
			StringBuilder codeStr = new StringBuilder();
			codeStr.append("typeText(").append(toCafeSelector(locator.toString())).append(",").append(toClientExpr(text)).append(")");
			testCodeList.add(new CafeTestStep(CafeTestStep.ACTION, codeStr.toString()));
		}
		waitResponse();
		if (doTab) {
			sendKeys(locator, Keys.TAB);
			waitResponse();
		}
	}

	/**
	 Assertions in testcafe api
	 Deep Equal
	 Not Deep Equal
	 Ok
	 Not Ok
	 Contains
	 Not Contains
	 Type of
	 Not Type of
	 Greater than
	 Greater than or Equal to
	 Less than
	 Less than or Equal to
	 Within
	 Not Within
	 Match
	 Not Match
	 */

	@Override
	public void verifyContains(String word1, String contains) {
		if (!_isTestCafe) {
			super.verifyContains(word1, contains);
			return;
		}
		verifyContains("", word1, contains);
	}

	@Override
	public void verifyContains(String message, String word1, String contains) {
		if (!_isTestCafe) {
			super.verifyContains(message, word1, contains);
			return;
		}
		cafeExpect("contains", word1, contains, message);
	}

	@Override
	public void verifyNotContains(String word1, String contains) {
		if (!_isTestCafe) {
			super.verifyNotContains(word1, contains);
			return;
		}
		verifyNotContains("", word1, contains);
	}

	@Override
	public void verifyNotContains(String message, String word1, String contains) {
		if (!_isTestCafe) {
			super.verifyNotContains(message, word1, contains);
			return;
		}
		cafeExpect("notContains", word1, contains, message);
	}

	private void cafeExpect(String assertion, String actualExpr, String expectExpr, String message) {
		boolean isTF = false;
		if ("ok".equals(assertion) || "notOk".equals(assertion))
			isTF = true;
		StringBuilder codeStr = new StringBuilder();

		codeStr.append("expect(").append(toClientExpr(actualExpr, !isTF)).append(").").append(assertion).append("(");
		if (expectExpr != null)
			codeStr.append(toClientExpr(expectExpr));
		if (message != null) {
			if (expectExpr != null)
				codeStr.append(",");
			codeStr.append(toClientExpr(message.replaceAll("\n", "")));
		}
		codeStr.append(")");
		testCodeList.add(new CafeTestStep(CafeTestStep.ASSERTION, codeStr.toString()));
	}

	//toString
	private String toClientExpr(String str) {
		return toClientExpr(str, true);
	}

	//toString
	private String toClientExpr(String str, boolean toJSString) {
		if (str.contains(CAFEEVAL)) {
			str = toClientGetEval(str);
		} else if (!str.matches("^\\(*" + AWAIT_TOKEN + ".*") && !str.contains("_cafe")){
			str = "'" + str.replaceAll("\n", "\\\\n").replaceAll("'", "\\\\'") + "'";
		}
		if (toJSString){
			str = "((" + str + ") + '').replace('&nbsp;', ' ')" ; // replace html space
		}
		return str;
	}

	private String toClientGetEval(String str) {
		String ztlGetEvalToken = CAFEEVAL + "getEval(";
		if (str.startsWith(ztlGetEvalToken)) {
			str = str.replace(ztlGetEvalToken, "await ClientFunction(() => eval(") + toClientFunctionDependencies(str);
		} else
			str = str.replace(CAFEEVAL, "");
		return str;
	}

	private String toClientFunctionDependencies(String originStr) {
		String newStr = ", {dependencies: {";
		Pattern pattern = compile("[a-zA-Z0-9_]+_cafe[a-zA-Z0-9_]*");
		Matcher matcher = pattern.matcher(originStr);
		int cnt = 0;
		while (matcher.find()) {
			if (cnt > 0)
				newStr += ", ";
			newStr += matcher.group(0);
			cnt++;
		}
		if (cnt > 0)
			newStr += "}})()";
		else
			newStr = ")()";
		return newStr;
	}

	@Override
	public void verifyEquals(boolean arg1, boolean arg2) {
		verifyEquals(null, arg1, arg2);
	}

	@Override
	public void verifyEquals(String message, boolean arg1, boolean arg2) {
		if (!_isTestCafe) {
			super.verifyEquals(message, arg1, arg2);
			return;
		}
		verifyEquals(message, String.valueOf(arg1), String.valueOf(arg2));
	}

	@Override
	public void verifyEquals(Object o1, Object o2) {
		verifyEquals(null, o1, o2);
	}

	@Override
	public void verifyEquals(String message, Object s1, Object s2) {
		if (!_isTestCafe) {
			super.verifyEquals(message, s1, s2);
			return;
		}
		cafeExpect("eql", String.valueOf(s1), String.valueOf(s2), message);
	}

	@Override
	@Deprecated /* Not support in test cafe */
	public void verifyEquals(String[] s1, String[] s2) {
		if (!_isTestCafe) {
			super.verifyEquals(s1, s2);
			return;
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Override
	@Deprecated /* Not support in test cafe */
	public void verifyEquals(String message, String[] s1, String[] s2) {
		if (!_isTestCafe) {
			super.verifyEquals(message, s1, s2);
			return;
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Override
	public void verifyFalse(boolean b) {
		if (!_isTestCafe) {
			super.verifyFalse(b);
			return;
		}
		cafeExpect("notOk", String.valueOf(b), null, null);
	}

	@Override
	public void verifyFalse(String message, boolean b) {
		if (!_isTestCafe) {
			super.verifyFalse(message, b);
			return;
		}
		cafeExpect("notOk", String.valueOf(b), null, message);
	}

	//only used in cafe test
	public void verifyFalse(String expr) {
		verifyFalse(null, expr);
	}

	//only used in cafe test
	public void verifyFalse(String message, String expr) {
		if (!_isTestCafe) {
			super.verifyFalse(message, Boolean.valueOf(expr));
			return;
		}
		cafeExpect("notOk", expr, null, message);
	}

	@Override
	public void verifyNotEquals(boolean s1, boolean s2) {
		verifyNotEquals(null, s1, s2);
	}

	@Override
	public void verifyNotEquals(String message, boolean s1, boolean s2) {
		if (!_isTestCafe) {
			super.verifyNotEquals(message, s1, s2);
			return;
		}
		verifyNotEquals(message, String.valueOf(s1), String.valueOf(s2));
	}

	@Override
	public void verifyNotEquals(Object s1, Object s2) {
		if (!_isTestCafe) {
			super.verifyNotEquals(s1, s2);
			return;
		}
		verifyNotEquals("", s1, s2);
	}

	@Override
	public void verifyNotEquals(String message, Object s1, Object s2) {
		if (!_isTestCafe) {
			super.verifyNotEquals(message, s1, s2);
			return;
		}
		cafeExpect("notEql", String.valueOf(s1), String.valueOf(s2), message);
	}

	@Override
	public void verifyTrue(boolean b) {
		if (!_isTestCafe) {
			super.verifyTrue(b);
			return;
		}
		cafeExpect("ok", String.valueOf(b), null, null);
	}

	@Override
	public void verifyTrue(String message, boolean b) {
		if (!_isTestCafe) {
			super.verifyTrue(message, b);
			return;
		}
		cafeExpect("ok", String.valueOf(b), null, message);
	}

	//only used in cafe test
	public void verifyTrue(String expr) {
		verifyTrue(null, expr);
	}

	//only used in cafe test
	public void verifyTrue(String message, String expr) {
		if (!_isTestCafe) {
			super.verifyTrue(message, Boolean.valueOf(expr));
			return;
		}
		cafeExpect("ok", expr, null, message);
	}

	@Override
	public void verifyTolerant(int number1, int number2, int tolerant) {
		if (!_isTestCafe) {
			super.verifyTolerant(number1, number2, tolerant);
			return;
		}
		verifyTolerant(String.valueOf(number1), String.valueOf(number2), String.valueOf(tolerant));
	}

	public void verifyTolerant(Object number1, Object number2, Object tolerant) {
		String num1 = String.valueOf(number1);
		String num2 = String.valueOf(number2);
		String tor = String.valueOf(tolerant);
		if (!_isTestCafe) {
			super.verifyTolerant(super.parseInt(num1), super.parseInt(num2), super.parseInt(tor));
			return;
		}
		cafeVerifyTolerant(num1, num2, tor);
	}

	private void cafeVerifyTolerant(String num1, String num2, String tor) {
		StringBuilder codeStr = new StringBuilder();
		codeStr.append("await ztl.verifyTolerant(t, ").append(toClientExpr(num1)).append(", ")
				.append(toClientExpr(num2)).append(", ").append(toClientExpr(tor)).append(");");
		testCodeList.add(new CafeTestStep(CafeTestStep.PRE, codeStr.toString()));
	}

	@Override
	public String getEval(String script) {
		if (!_isTestCafe) {
			return super.getEval(script);
		}
		return Scripts.getCafeClientFunction(script);
	}

	public void evalScript(String script) {
		if (!_isTestCafe) {
			super.executeScript(script);
		} else {
			testCodeList.add(new CafeTestStep(CafeTestStep.EVAL, "await ClientFunction(() => {" + script + "}" + toClientFunctionDependencies(script)));
		}
	}

	@Override
	public void runScript(String script) {
		throw new UnsupportedOperationException("Please use evalScript(script) instead");
	}

	@Override
	protected void sleep(long millis) {
		if (!_isTestCafe) {
			super.sleep(millis);
			return;
		}
		testCodeList.add(new CafeTestStep(CafeTestStep.ACTION, "wait(" + millis + ")"));
	}

	public void blur(ClientWidget locator) {
		if (!_isTestCafe) {
			super.blur(locator);
			return;
		}
		testCodeList.add(new CafeTestStep(CafeTestStep.EVAL, "await t.pressKey('tab')"));
	}

	protected void runZscript(String zscript) {
		if (!_isTestCafe) {
			super.runZscript(zscript);
			return;
		}
		Scripts.doCafeEval("zAu.send(new zk.Event(null, 'onZTLService', '" + zscript + "', 10))", testCodeList);
		waitResponse();
	}

	//JQuery
	@Override
	protected Widget widget(int number) {
		if (!_isTestCafe) {
			return super.widget(number);
		}
		return widget(uuid(number));
	}

	@Override
	protected Widget widget(String uuid) {
		if (!_isTestCafe) {
			return super.widget(uuid);
		}
		return new Widget$Cafe(uuid, testCodeList);
	}

	@Override
	protected Widget widget(JQuery jQuery) {
		if (!_isTestCafe) {
			return super.widget(jQuery);
		}
		return new Widget$Cafe(jQuery, testCodeList);
	}

	@Override
	protected JQuery jq(String selector) {
		if (!_isTestCafe) {
			return super.jq(selector);
		}
		return new JQuery$Cafe(selector, testCodeList);
	}

	@Override
	protected JQuery jq(ClientWidget el) {
		if (!_isTestCafe) {
			return super.jq(el);
		}
		return new JQuery$Cafe(el, testCodeList);
	}

	@Override
	protected ZK zk(ClientWidget el) {
		if (!_isTestCafe) {
			return super.zk(el);
		}
		return new ZK$Cafe(el, testCodeList);
	}

	@Override
	protected ZK zk(String selector) {
		if (!_isTestCafe) {
			return super.zk(selector);
		}
		return new ZK$Cafe(selector, testCodeList);
	}

	@Override
	public boolean isIE() {
		if (!_isTestCafe) {
			return super.isIE();
		}
		return false;
	}

	@Override
	public boolean isEdge() {
		if (!_isTestCafe) {
			return super.isEdge();
		}
		return false;
	}

	@Override
	public boolean isFirefox() {
		if (!_isTestCafe) {
			return super.isFirefox();
		}
		return false;
	}

	@Override
	public boolean isChrome() {
		if (!_isTestCafe) {
			return super.isChrome();
		}
		return false;
	}

	@Override
	public boolean isSafari() {
		if (!_isTestCafe) {
			return super.isSafari();
		}
		return false;
	}

	@Override
	public boolean isOpera() {
		if (!_isTestCafe) {
			return super.isOpera();
		}
		return false;
	}

	@Override
	public boolean isIPhone() {
		if (!_isTestCafe) {
			return super.isIPhone();
		}
		return false;
	}

	@Override
	public boolean isAndroid() {
		if (!_isTestCafe) {
			return super.isAndroid();
		}
		return false;
	}

	@Override
	@Deprecated /* Not support in test cafe */
	public WebDriver getWebDriver() {
		if (!_isTestCafe) {
			return super.getWebDriver();
		}
		throw new UnsupportedOperationException("getWebDriver() not support in test cafe");
	}

	@Override
	@Deprecated /* Not support in test cafe */
	public Actions getActions() {
		if (!_isTestCafe) {
			return super.getActions();
		}
		throw new UnsupportedOperationException("getActions() not support in test cafe");
	}

	@Override
	public void closeErrorBox() {
		if (!_isTestCafe) {
			super.closeErrorBox();
			return;
		}
		cafeClick("click", "jq('body')");
	}

	@Override
	public void select(ClientWidget selectLocator, String optionLocator) {
		if (!_isTestCafe) {
			super.select(selectLocator, optionLocator);
		} else {
			click(jq(selectLocator));
			click(jq(selectLocator).find("option:contains(" + optionLocator + ")"));
		}
	}

	@Override
	public void select(ClientWidget selectLocator, int index) {
		if (!_isTestCafe) {
			super.select(selectLocator, index);
		} else {
			click(jq(selectLocator));
			click(jq(selectLocator).find("option").get(index));
		}
	}

	@Override
	public void frozenScroll(ClientWidget locator, int num) {
		if (!_isTestCafe) {
			super.frozenScroll(locator, num);
		} else {
			Widget wgt = jq(locator).toWidget();
			evalScript(wgt + ".frozen._doScrollNow(" + num + ")");
			waitResponse();
		}
	}

	@Override
	public void verScroll(ClientWidget locator, double percent) {
		if (!_isTestCafe) {
			super.verScroll(locator, percent);
		} else {
			verScroll(locator, percent + "");
		}
	}

	public void verScroll(ClientWidget locator, String percent) {
		if (!_isTestCafe) {
			throw new UnsupportedOperationException("Only used in test cafe");
		} else {
			StringBuilder script = new StringBuilder();
			script.append("await ztl.doScroll({locator:").append(toCafeSelector(locator.toString()))
					.append(", scrollType: 'vertical', percent: ").append(toClientExpr(percent, false)).append("});");
			testCodeList.add(new CafeTestStep(CafeTestStep.EVAL, script.toString()));
			waitResponse();
		}
	}

	@Override
	public void verScrollAbs(ClientWidget locator, int dist) {
		if (!_isTestCafe) {
			super.verScrollAbs(locator, dist);
		} else {
			verScrollAbs(locator, dist + "");
		}
	}

	public void verScrollAbs(ClientWidget locator, String dist) {
		if (!_isTestCafe) {
			throw new UnsupportedOperationException("Only used in test cafe");
		} else {
			StringBuilder script = new StringBuilder();
			script.append("await ztl.doScroll({locator:").append(toCafeSelector(locator.toString()))
					.append(", scrollType: 'vertical', dist: ").append(toClientExpr(dist, false)).append("});");
			testCodeList.add(new CafeTestStep(CafeTestStep.EVAL, script.toString()));
			waitResponse();
		}
	}

	@Override
	public void horScroll(ClientWidget locator, double percent) {
		if (!_isTestCafe) {
			super.horScroll(locator, percent);
		} else {
			horScroll(locator, percent + "");
		}
	}

	public void horScroll(ClientWidget locator, String percent) {
		if (!_isTestCafe) {
			throw new UnsupportedOperationException("Only used in test cafe");
		} else {
			StringBuilder script = new StringBuilder();
			script.append("await ztl.doScroll({locator:").append(toCafeSelector(locator.toString()))
					.append(", scrollType: 'horizontal', percent: ").append(toClientExpr(percent, false)).append("});");
			testCodeList.add(new CafeTestStep(CafeTestStep.EVAL, script.toString()));
			waitResponse();
		}
	}


	@Override
	public void horScrollAbs(ClientWidget locator, int dist) {
		if (!_isTestCafe) {
			super.horScrollAbs(locator, dist);
		} else {
			horScrollAbs(locator, dist + "");
		}
	}

	public void horScrollAbs(ClientWidget locator, String dist) {
		if (!_isTestCafe) {
			throw new UnsupportedOperationException("Only used in test cafe");
		} else {
			StringBuilder script = new StringBuilder();
			script.append("await ztl.doScroll({locator:").append(toCafeSelector(locator.toString()))
					.append(", scrollType: 'horizontal', dist: ").append(toClientExpr(dist, false)).append("});");
			testCodeList.add(new CafeTestStep(CafeTestStep.EVAL, script.toString()));
			waitResponse();
		}
	}

	@Override
	public void horScrollNoBody(ClientWidget locator, double percent) {
		if (!_isTestCafe) {
			super.horScrollNoBody(locator, percent);
		} else {
			horScrollNoBody(locator, percent + "");
		}
	}

	public void horScrollNoBody(ClientWidget locator, String percent) {
		if (!_isTestCafe) {
			throw new UnsupportedOperationException("Only used in test cafe");
		} else {
			StringBuilder script = new StringBuilder();
			script.append("await ztl.doScroll({locator:").append(toCafeSelector(locator.toString()))
					.append(", scrollType: 'horizontal', noBody: true, percent: ").append(toClientExpr(percent, false)).append("});");
			testCodeList.add(new CafeTestStep(CafeTestStep.EVAL, script.toString()));
			waitResponse();
		}
	}

	@Override
	public void horScrollNoBodyAbs(ClientWidget locator, int dist) {
		if (!_isTestCafe) {
			super.horScrollNoBodyAbs(locator, dist);
		} else {
			horScrollNoBodyAbs(locator, dist + "");
		}
	}

	public void horScrollNoBodyAbs(ClientWidget locator, String dist) {
		if (!_isTestCafe) {
			throw new UnsupportedOperationException("Only used in test cafe");
		} else {
			StringBuilder script = new StringBuilder();
			script.append("await ztl.doScroll({locator:").append(toCafeSelector(locator.toString()))
					.append(", scrollType: 'horizontal', noBody: true, dist: ").append(toClientExpr(dist, false)).append("});");
			testCodeList.add(new CafeTestStep(CafeTestStep.EVAL, script.toString()));
			waitResponse();
		}
	}


	@Override
	public void verScrollNoBody(ClientWidget locator, double percent) {
		if (!_isTestCafe) {
			super.verScrollNoBody(locator, percent);
		} else {
			verScrollNoBody(locator, percent + "");
		}
	}

	public void verScrollNoBody(ClientWidget locator, String percent) {
		if (!_isTestCafe) {
			throw new UnsupportedOperationException("Only used in test cafe");
		} else {
			StringBuilder script = new StringBuilder();
			script.append("await ztl.doScroll({locator:").append(toCafeSelector(locator.toString()))
					.append(", scrollType: 'vertical', noBody: true, percent: ").append(toClientExpr(percent, false)).append("});");
			testCodeList.add(new CafeTestStep(CafeTestStep.EVAL, script.toString()));
			waitResponse();
		}
	}

	@Override
	public void verScrollNoBodyAbs(ClientWidget locator, int dist) {
		if (!_isTestCafe) {
			super.verScrollNoBodyAbs(locator, dist);
		} else {
			verScrollNoBodyAbs(locator, dist + "");
		}
	}

	public void verScrollNoBodyAbs(ClientWidget locator, String dist) {
		if (!_isTestCafe) {
			throw new UnsupportedOperationException("Only used in test cafe");
		} else {
			StringBuilder script = new StringBuilder();
			script.append("await ztl.doScroll({locator:").append(toCafeSelector(locator.toString()))
					.append(", scrollType: 'vertical', noBody: true, dist: ").append(toClientExpr(dist, false)).append("});");
			testCodeList.add(new CafeTestStep(CafeTestStep.EVAL, script.toString()));
			waitResponse();
		}
	}

	public void doScroll(ClientWidget locatorOfObjectToBeDragged, ClientWidget locatorOfDragDestinationObject,
			String from, String to) {
		if (!_isTestCafe) {
			super.doScroll(locatorOfObjectToBeDragged, locatorOfDragDestinationObject, from, to);
		} else {
			mouseMoveAt(locatorOfObjectToBeDragged, from);
			waitResponse();
			mouseDownAt(locatorOfObjectToBeDragged, from);
			waitResponse();
			mouseMoveAt(locatorOfDragDestinationObject, to);
			waitResponse();
			mouseUpAt(locatorOfDragDestinationObject, to);
			waitResponse();
		}
	}

	@Override
	public void closeZKLog() {
		if (!_isTestCafe) {
			super.closeZKLog();
		} else {
			Scripts.doCafeEval("jq('#zk_logbox').remove()", testCodeList);
			waitResponse();
		}
	}

	public void clickAlert() {
		click(jq("@window[title=\"ZK Test\"] @button"));
	}

	@Override
	public void switchTab() {
		if (!_isTestCafe) {
			super.switchTab();
		} else {
			//TODO
		}
	}

	public void setWindowSize(int width, int height) {
		if (!_isTestCafe) {
			super.setWindowSize(width, height);
		} else {
			setWindowSize(width + "", height + "");
		}
	}
	public void setWindowSize(Object width, Object height) {
		if (!_isTestCafe) {
			throw new UnsupportedOperationException("Only used in test cafe");
		} else {
			testCodeList.add(new CafeTestStep(CafeTestStep.EVAL, "await t.resizeWindow(" + width + ", " + height + ")"));
		}
	}

	public void navigatePage(boolean forward) {
		if (!_isTestCafe) {
			super.navigatePage(forward);
		} else {
			String action = "forward";
			if (!forward)
				action = "back";
			evalScript("window.history." + action + "()");
		}
	}

	@Override
	protected int parseInt(String number) {
		if (!_isTestCafe) {
			return super.parseInt(number);
		} else {
			return 0; // will be removed
		}
	}

	protected int parseInt(int number) {
		if (!_isTestCafe) {
			return super.parseInt("" + number);
		} else {
			return 0; // will be removed
		}
	}

	protected String parseIntStr(int number) {
		return parseIntStr(String.valueOf(number));
	}

	protected String parseIntStr(String number) {
		if (!_isTestCafe)
			throw new UnsupportedOperationException("Only used in test cafe");
		else
			return CAFEEVAL + "parseInt(" + number + ")";
	}

	@Override
	protected float parseFloat(String number) {
		if (!_isTestCafe) {
			return super.parseFloat(number);
		} else {
			return 0; // will be removed
		}
	}

	protected String parseFloatStr(int number) {
		return parseFloatStr(String.valueOf(number));
	}

	protected String parseFloatStr(String number) {
		if (!_isTestCafe)
			throw new UnsupportedOperationException("Only used in test cafe");
		else
			return CAFEEVAL + "parseFloat(" + number + ")";
	}

	protected double parseFloat(int number) {
		if (!_isTestCafe) {
			return super.parseFloat("" + number);
		} else {
			return 0; // will be removed
		}
	}

	@Override
	protected double parseDouble(String number) {
		if (!_isTestCafe) {
			return super.parseDouble(number);
		} else {
			return 0; // will be removed
		}
	}

	protected double parseDouble(int number) {
		if (!_isTestCafe) {
			return super.parseDouble("" + number);
		} else {
			return 0; // will be removed
		}
	}

	protected String parseDoubleStr(int number) {
		return parseDoubleStr(String.valueOf(number));
	}

	protected String parseDoubleStr(String number) {
		if (!_isTestCafe)
			throw new UnsupportedOperationException("Only used in test cafe");
		else
			return "parseFloat(" + number + ")";
	}

	@Override
	protected boolean parseBoolean(String bool) {
		if (!_isTestCafe) {
			return super.parseBoolean(bool);
		} else {
			return false; // will be removed
		}
	}

	/**
	 * a shortcut for getting alert message.
	 * (Take care about the dom class and model will be different
	 * 	when event-thread is enable/disable , so we use title .)
	 * @return
	 */
	public String getAlertMessage() {
		if (!_isTestCafe) {
			return super.getAlertMessage();
		} else {
			return jq("@window[title=\"ZK Test\"] @label").text();
		}
	}

	protected boolean hasError() {
		if (!_isTestCafe) {
			return super.hasError();
		} else {
			return false;
		}
	}

	public String hasError_cafeStr() {
		return Scripts.getCafeClientFunction("!!jq('.z-messagebox-error')[0] || !!jq('.z-errorbox')[0] || jq('.z-error')[0]");
	}

	public String getText(ClientWidget locator) {
		if (!_isTestCafe) {
			return super.getText(locator);
		}
		return Scripts.getCafeClientFunction("jq(" + locator.toString() + ").text().replace(/\\s/g,' ')");
	}

	@Override
	public boolean isVisible(ClientWidget locator) {
		if (!_isTestCafe) {
			return super.isVisible(locator);
		} else {
			return false;
		}
	}

	public String isVisible_cafeStr(ClientWidget locator) {
		return jq(locator).isVisible_cafeStr();
	}

	@Override
	public boolean hasNativeScroll(ClientWidget locator) {
		if (!_isTestCafe) {
			return super.hasNativeScroll(locator);
		} else {
			return false;
		}
	}

	public String hasNativeScroll_cafeStr(ClientWidget locator) {
		return Scripts.getCafeClientFunction("!" + locator.toLocator() + "._scrollbar");
	}

	@Override
	public boolean hasHScrollbar(ClientWidget locator) {
		if (!_isTestCafe) {
			return super.hasHScrollbar(locator);
		} else {
			return false; //CAFE DO LATER
		}
	}

	public String hasHScrollbar_cafeStr(ClientWidget locator) {
		return "await ztl.hasHScrollbar({locator:" + toCafeSelector(locator.toString()) + "})";
	}

	@Override
	public boolean hasVScrollbar(ClientWidget locator) {
		if (!_isTestCafe) {
			return super.hasVScrollbar(locator);
		} else {
			return false; //CAFE DO LATER
		}
	}

	public String hasVScrollbar_cafeStr(ClientWidget locator) {
		return "await ztl.hasVScrollbar({locator:" + toCafeSelector(locator.toString()) + "})";
	}

	public int getScrollTop(Widget widget) {
		if (!_isTestCafe) {
			return super.getScrollTop(widget);
		} else {
			return 0; //CAFE DO LATER
		}
	}

	public String getScrollTop_cafeStr(ClientWidget locator) {
		return "await ztl.getScrollTop({locator:" + toCafeSelector(locator.toString()) + "})";
	}

	public int getScrollLeft(Widget widget) {
		if (!_isTestCafe) {
			return super.getScrollLeft(widget);
		} else {
			return 0; //CAFE DO LATER
		}
	}

	public String getScrollLeft_cafeStr(ClientWidget locator) {
		return "await ztl.getScrollLeft({locator:" + toCafeSelector(locator.toString()) + "})";
	}

	public String getZKLog() {
		if (!_isTestCafe) {
			return super.getZKLog();
		} else {
			return "await ClientFunction(() => jq(\"#zk_log\").length > 0 ? " + cafeTrim("jq(\"#zk_log\").val()") + " : '')()";
		}
	}

	public boolean is(String name) {
		if (!_isTestCafe) {
			return super.is(name);
		} else {
			return false;
		}
	}

	public String is_cafeStr(String name) {
		return Scripts.getCafeClientFunction("!!(zk." + name + ")");
	}

	@Override
	public int getWindowWidth() {
		if (!_isTestCafe) {
			return super.getWindowWidth();
		} else {
			return 0;
		}
	}

	public String getWindowWidth_cafeStr() {
		return Scripts.getCafeClientFunction("document.body.offsetWidth");
	}

	@Override
	public int getWindowHeight() {
		if (!_isTestCafe) {
			return super.getWindowHeight();
		} else {
			return 0;
		}
	}

	public String getWindowHeight_cafeStr() {
		return Scripts.getCafeClientFunction("document.body.offsetHeight");
	}

	@Deprecated /* Not support in test cafe */
	public int getBrowserTabCount() {
		if (!_isTestCafe) {
			return super.getBrowserTabCount();
		} else {
			throw new UnsupportedOperationException("Not support in test cafe");
		}
	}

	public void assignment_cafe(String identifier, Object expr, boolean isNew) {
		StringBuilder codeStr = new StringBuilder();
		if (isNew)
			codeStr.append("let ");
		codeStr.append(identifier);
		codeStr.append(" = ");
		String exprStr = String.valueOf(expr);
		if (exprStr.startsWith(CAFEEVAL)) {
			codeStr.append(toClientGetEval(exprStr));
		} else {
			codeStr.append(Scripts.getCafeClientFunction(String.valueOf(expr)));
		}
		testCodeList.add(new CafeTestStep(CafeTestStep.EVAL, codeStr.toString()));
	}

	public void conditionStatement_cafe(String cond, String condtionContent) {
		StringBuilder codeStr = new StringBuilder();
		codeStr.append(cond).append(" (").append(condtionContent).append(")");
		testCodeList.add(new CafeTestStep(CafeTestStep.PRE, codeStr.toString()));
	}

	public void forStatement_cafe(String param, String start, String type, String restriction) {
		StringBuilder codeStr = new StringBuilder();
		codeStr.append("for (var ").append(param).append(" = ").append(start).append(";");
		if ("to".equals(type))
			codeStr.append(param).append(" > ").append(restriction).append("? <= : >=").append(restriction);
		else
			codeStr.append(param).append(" > ").append(restriction).append("? < : >").append(restriction);
		codeStr.append(";").append(param).append(" > ").append(restriction).append("?").append(param).append("-- : ++")
				.append(param).append(")");
		testCodeList.add(new CafeTestStep(CafeTestStep.PRE, codeStr.toString()));
	}

	public void conditionBlock_cafe(boolean isStart) {
		StringBuilder codeStr = new StringBuilder();
		if (isStart)
			codeStr.append(" {\n");
		else
			codeStr.append(" \n}");
		testCodeList.add(new CafeTestStep(CafeTestStep.PRE, codeStr.toString()));
	}

	/**
	 * unsupported functions
	 */
	@Deprecated /* Not support in test cafe */
	public void fireEvent(ClientWidget locator, String eventName) {
		if (!_isTestCafe) {
			super.fireEvent(locator, eventName);
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Deprecated /* Not support in test cafe */
	public String getAttribute(ClientWidget attributeLocator) {
		if (!_isTestCafe) {
			return super.getAttribute(attributeLocator);
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Deprecated /* Not support in test cafe */
	public Number getCursorPosition(ClientWidget locator) {
		if (!_isTestCafe) {
			return super.getCursorPosition(locator);
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Deprecated /* Not support in test cafe */
	public Number getElementHeight(ClientWidget locator) {
		if (!_isTestCafe) {
			return super.getElementHeight(locator);
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Deprecated /* Not support in test cafe */
	public Number getElementIndex(ClientWidget locator) {
		if (!_isTestCafe) {
			return super.getElementIndex(locator);
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Deprecated /* Not support in test cafe */
	public Number getElementPositionLeft(ClientWidget locator) {
		if (!_isTestCafe) {
			return super.getElementPositionLeft(locator);
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Deprecated /* Not support in test cafe */
	public Number getElementPositionTop(ClientWidget locator) {
		if (!_isTestCafe) {
			return super.getElementPositionTop(locator);
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Deprecated /* Not support in test cafe */
	public Number getElementWidth(ClientWidget locator) {
		if (!_isTestCafe) {
			return super.getElementWidth(locator);
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Deprecated /* Not support in test cafe */
	public String[] getSelectOptions(ClientWidget selectLocator) {
		if (!_isTestCafe) {
			return super.getSelectOptions(selectLocator);
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Deprecated /* Not support in test cafe */
	public String getSelectedId(ClientWidget selectLocator) {
		if (!_isTestCafe) {
			return super.getSelectedId(selectLocator);
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Deprecated /* Not support in test cafe */
	public String[] getSelectedIds(ClientWidget selectLocator) {
		if (!_isTestCafe) {
			return super.getSelectedIds(selectLocator);
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	public String getSelectedIndex(ClientWidget selectLocator) {
		if (!_isTestCafe) {
			return super.getSelectedIndex(selectLocator);
		} else {
			return Scripts.getCafeClientFunction("zk.Widget.$(jq(" + selectLocator.toString() + ")).getSelectedIndex()");
		}
	}

	@Deprecated /* Not support in test cafe */
	public String[] getSelectedIndexes(ClientWidget selectLocator) {
		if (!_isTestCafe) {
			return super.getSelectedIndexes(selectLocator);
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Deprecated /* Not support in test cafe */
	public String getSelectedLabel(ClientWidget selectLocator) {
		if (!_isTestCafe) {
			return super.getSelectedLabel(selectLocator);
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Deprecated /* Not support in test cafe */
	public String[] getSelectedLabels(ClientWidget selectLocator) {
		if (!_isTestCafe) {
			return super.getSelectedLabels(selectLocator);
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Deprecated /* Not support in test cafe */
	public String getSelectedValue(ClientWidget selectLocator) {
		if (!_isTestCafe) {
			return super.getSelectedValue(selectLocator);
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Deprecated /* Not support in test cafe */
	public String[] getSelectedValues(ClientWidget selectLocator) {
		if (!_isTestCafe) {
			return super.getSelectedValues(selectLocator);
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Deprecated /* Not support in test cafe */
	public String getTable(ClientWidget tableCellAddress) {
		if (!_isTestCafe) {
			return super.getTable(tableCellAddress);
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	public String getValue(ClientWidget locator) {
		if (!_isTestCafe) {
			return super.getValue(locator);
		} else {
			return Scripts.getCafeClientFunction("jq(" + locator.toString() + ").val()");
		}
	}

	@Deprecated /* Not support in test cafe */
	public void highlight(ClientWidget locator) {
		if (!_isTestCafe) {
			super.highlight(locator);
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Deprecated /* Not support in test cafe */
	public boolean isChecked(ClientWidget locator) {
		if (!_isTestCafe) {
			return super.isChecked(locator);
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Deprecated /* Not support in test cafe */
	public boolean isEditable(ClientWidget locator) {
		if (!_isTestCafe) {
			return super.isEditable(locator);
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Deprecated /* Not support in test cafe */
	public boolean isElementPresent(ClientWidget locator) {
		if (!_isTestCafe) {
			return super.isElementPresent(locator);
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Deprecated /* Not support in test cafe */
	public boolean isOrdered(ClientWidget locator1, ClientWidget locator2) {
		if (!_isTestCafe) {
			return super.isOrdered(locator1, locator2);
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Deprecated /* Not support in test cafe */
	public boolean isSomethingSelected(ClientWidget selectLocator) {
		if (!_isTestCafe) {
			return super.isSomethingSelected(selectLocator);
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Override
	@Deprecated /* Not support in test cafe */
	public boolean isTextPresent(String pattern) {
		if (!_isTestCafe) {
			return super.isTextPresent(pattern);
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Deprecated /* Not support in test cafe */
	public void removeAllSelections(ClientWidget locator) {
		if (!_isTestCafe) {
			super.removeAllSelections(locator);
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Deprecated /* Not support in test cafe */
	public void removeSelection(ClientWidget locator, String optionLocator) {
		if (!_isTestCafe) {
			super.removeSelection(locator, optionLocator);
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Deprecated /* Not support in test cafe */
	public void rollup(ClientWidget rollupName, String kwargs) {
		if (!_isTestCafe) {
			super.rollup(rollupName, kwargs);
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Deprecated /* Not support in test cafe */
	public void selectFrame(ClientWidget locator) {
		if (!_isTestCafe) {
			if (locator instanceof Widget)
				getWebDriver().switchTo().frame(((Widget) locator).uuid().toString());
			else
				getWebDriver().switchTo().frame(findElement(locator));
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Deprecated /* Not support in test cafe */
	public void setContext(ClientWidget context) {
		if (!_isTestCafe) {
			super.setContext(context);
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Deprecated /* Not support in test cafe */
	public void setCursorPosition(ClientWidget locator, String position) {
		if (!_isTestCafe) {
			super.setCursorPosition(locator, position);
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Deprecated /* Not support in test cafe */
	public void submit(ClientWidget formLocator) {
		if (!_isTestCafe) {
			super.submit(formLocator);
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Override
	public void verifyEqualColor(String color1, String color2) {
		if (!_isTestCafe) {
			super.verifyEqualColor(color1, color2);
		} else {
			cafeVerifyColor(null, "ok", color1, color2);
		}
	}

	@Override
	public void verifyEqualColor(String msg, String color1, String color2) {
		if (!_isTestCafe) {
			super.verifyEqualColor(msg, color1, color2);
		} else {
			cafeVerifyColor(msg, "ok", color1, color2);
		}
	}

	@Override
	public void verifyNotEqualColor(String color1, String color2) {
		if (!_isTestCafe) {
			super.verifyNotEqualColor(color1, color2);
		} else {
			cafeVerifyColor(null, "notOk", color1, color2);
		}
	}

	@Override
	public void verifyNotEqualColor(String msg, String color1, String color2) {
		if (!_isTestCafe) {
			super.verifyNotEqualColor(msg, color1, color2);
		} else {
			cafeVerifyColor(msg, "notOk", color1, color2);
		}
	}

	private void cafeVerifyColor(String message, String testEqualExpr, String color1, String color2) {
		StringBuilder codeStr = new StringBuilder();
		codeStr.append("await t.expect(await ztl.isEqualColor(").append(toClientExpr(color1))
				.append(", ").append(toClientExpr(color2)).append(")).")
				.append(testEqualExpr).append("(");
		if (message != null)
			codeStr.append(toClientExpr(message.replaceAll("\n", "")));
		codeStr.append(")");
		testCodeList.add(new CafeTestStep(CafeTestStep.PRE, codeStr.toString()));
	}

	@Override
	@Deprecated /* Not support in test cafe */
	public void waitForCondition(String script, String timeout) {
		if (!_isTestCafe) {
			super.waitForCondition(script, timeout);
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Override
	@Deprecated /* Not support in test cafe */
	public void waitForFrameToLoad(String frameAddress, String timeout) {
		if (!_isTestCafe) {
			super.waitForFrameToLoad(frameAddress, timeout);
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Override
	public void waitForPageToLoad(String timeout) {
		if (!_isTestCafe) {
			super.waitForPageToLoad(timeout);
		} else {
			sleep(Integer.parseInt(timeout));
		}
	}

	@Override
	@Deprecated /* Not support in test cafe */
	public void waitForPopUp(String windowID, String timeout) {
		if (!_isTestCafe) {
			super.waitForPopUp(windowID, timeout);
		}
		throw new UnsupportedOperationException("Not support in test cafe");
	}

	@Override
	public void refresh() {
		if (!_isTestCafe) {
			super.refresh();
		} else {
			testCodeList.add(new CafeTestStep(CafeTestStep.PRE, "await t.eval(() => location.reload(true));"));
		}
	}

	public void refresh(String zscript) {
		if (!_isTestCafe) {
			throw new UnsupportedOperationException("Not support in ZTL (selenium)");
		} else {
			testCodeList.add(new CafeTestStep(CafeTestStep.PRE, "await t.eval(() => location.reload(true));"));
			runRawZscript(zscript);
		}
	}

	private String cafeTrim(String text) {
		return "(" + text + ").trim()";
	}

	private final static String coordArrayIdentifier = "cafeCoord_";
	private int coordArrayVarCnt = 0;
	private String genCoordStringExpr(String coordString) {
		return genCoordStringExpr(coordString, false);
	}
	private String genCoordStringExpr(String coordString, boolean isDragFrom) {
		coordArrayVarCnt++;
		boolean isNum = false;
		if (coordString.matches("^[-0-9].*[-0-9]$")) {
			coordString = "\"" + coordString + "\"";
			isNum = true;
		}
		StringBuilder codeStr = new StringBuilder();
		codeStr.append("let ").append(coordArrayIdentifier).append(coordArrayVarCnt)
				.append(" = await ztl.convertCoordStrToArr(").append(coordString);
		if (isDragFrom && !isNum) // for modify
			codeStr.append(", true");
		codeStr.append(");\n");
		return codeStr.toString();
	}
}
