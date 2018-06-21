package org.zkoss.ztl;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.zkoss.ztl.testcafe.*;
import org.zkoss.ztl.unit.ClientWidget;
import org.zkoss.ztl.unit.JQuery;
import org.zkoss.ztl.unit.Widget;
import org.zkoss.ztl.unit.ZK;
import org.zkoss.ztl.util.Scripts;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * A transpiler to write test cafe in ztl
 * @author jameschu
 *
 */
public class ZKClientTestCaseCafe extends ZKClientTestCase {
	public static String CAFEEVAL = "ZTLCafeEval=>";
	private static boolean _isTestCafe = false;
	protected List<CafeTestStep> _testCodeList = null;
	public void enableTestCafe() {
		_isTestCafe = true;
		_testCodeList = new LinkedList<>();
	}
	public void disableTestCafe() {
		_isTestCafe = false;
		_testCodeList = null;
	}
	public void generateCafeTest(String targetUrl) throws IllegalArgumentException {
		String testName = this.getClass().getSimpleName().replace("_", "-");
		StringBuilder testContent = new StringBuilder();
		testContent.append("import {ClientFunction, Selector} from 'testcafe';\n");
		testContent.append("import * as ztl from './module/ztl.js';\n");
		testContent.append("fixture `ZTL TEST - ");
		testContent.append(testName);
		testContent.append("`.page `");
		testContent.append(targetUrl);
		testContent.append("`;\ntest('");
		testContent.append(testName);
		testContent.append("', async t => {\nawait ztl.waitResponse(t);\n");
		String lastStepType = null;
		for (CafeTestStep step : _testCodeList) {
			String type = step.getType();
			if (CafeTestStep.ACTION.equals(lastStepType) && !CafeTestStep.ACTION.equals(type)) {
				testContent.append(";\n");
			}
			if (CafeTestStep.ACTION.equals(type)) {
				if (!CafeTestStep.ACTION.equals(lastStepType))
					testContent.append("await t");
				testContent.append(".");
				testContent.append(step.getContent());
			} else if (CafeTestStep.EVAL.equals(type)) {
				testContent.append(step.getContent());
				testContent.append(";\n");
			} else if (CafeTestStep.ASSERTION.equals(type)) {
				testContent.append("await t.");
				testContent.append(step.getContent());
				testContent.append(";\n");
			} else {
				throw new IllegalArgumentException("Not support step type : " + type);
			}
			lastStepType = type;
		}
		testContent.append("});");

		//write file
		try {
			final String destdir = "/Users/jameschu/zkworks/ztl-test/testcafe/ztl/";
			String path = destdir + testName + ".js";

			File dir = new File(path);
			if (!dir.isFile()) {
				dir.createNewFile();
			}
			FileWriter writer = new FileWriter(dir);

			writer.write(testContent.toString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		_testCodeList.clear();
	}

	//selector
	private String toCafeSelector(String locatorStr) {
		StringBuilder codeStr = new StringBuilder();
		codeStr.append("Selector(() => ");
		codeStr.append(locatorStr);
		if (!locatorStr.endsWith("get(0)") && !locatorStr.endsWith("]")) //handle get(1)
			codeStr.append("[0]");
		codeStr.append(")");
		return codeStr.toString();
	}

	//waitResponse()
	@Override
	protected void waitResponse(int timeout, boolean includingAnimation) {
		if (!_isTestCafe) {
			super.waitResponse(includingAnimation);
			return;
		}
		_testCodeList.add(new CafeTestStep(CafeTestStep.EVAL,"await ztl.waitResponse(t)"));
	}

	/**
	 >> Actions in testcafe api
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
			super.focus(locator.toLocator());
			return;
		}
		getEval(locator.toLocator() + ".focus()");
	}

	/**
	 * Click (check
	 * Double Click
	 * Right Click
	 */

	@Override
	public void check(ClientWidget locator) {
		if (!_isTestCafe) {
			super.check(locator.toLocator());
			return;
		}
		click(locator);
	}

	@Override
	public void click(ClientWidget locator) {
		if (!_isTestCafe) {
			super.click(locator.toLocator());
			return;
		}
		cafeClick("click", locator.toLocator());
	}

	@Override
	public void clickAt(ClientWidget locator, String coordString) {
		if (!_isTestCafe) {
			super.clickAt(locator.toLocator(), coordString);
			return;
		}
		cafeClick("click", locator.toLocator(), coordString);
	}

	@Override
	public void contextMenu(ClientWidget locator) {
		if (!_isTestCafe) {
			super.contextMenu(locator.toLocator());
			return;
		}
		cafeClick("rightClick", locator.toLocator());
	}

	@Override
	public void contextMenuAt(ClientWidget locator, String coordString) {
		if (!_isTestCafe) {
			super.contextMenuAt(locator.toLocator(), coordString);
			return;
		}
		cafeClick("rightClick", locator.toLocator(), coordString);
	}

	@Override
	public void doubleClick(ClientWidget locator) {
		if (!_isTestCafe) {
			super.doubleClick(locator.toLocator());
			return;
		}
		cafeClick("doubleClick", locator.toLocator());
	}

	@Override
	public void doubleClickAt(ClientWidget locator, String coordString) {
		if (!_isTestCafe) {
			super.doubleClickAt(locator.toLocator(), coordString);
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
			_testCodeList.add(new CafeTestStep(CafeTestStep.ACTION,codeStr.toString()));
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
			String[] coords = coordString.split(",");
			codeStr.append(", {offsetX: " + coords[0] + ", offsetY: " + coords[1] + "}");
		}
		codeStr.append(")");
		_testCodeList.add(new CafeTestStep(CafeTestStep.ACTION,codeStr.toString()));
	}

	/**
	 * Mouse move
	 */
	@Override
	public void mouseDown(ClientWidget locator) {
		if (!_isTestCafe) {
			super.mouseDown(locator.toLocator());
			return;
		}
		_testCodeList.add(new CafeTestStep(CafeTestStep.ACTION,"//TODO mouseDown, selector: " + toCafeSelector(locator.toString())));
	}

	@Override
	public void mouseDownAt(ClientWidget locator, String coordString) {
		if (!_isTestCafe) {
			super.mouseDownAt(locator.toLocator(), coordString);
			return;
		}
		_testCodeList.add(new CafeTestStep(CafeTestStep.ACTION,"//TODO mouseDownAt, selector: " + toCafeSelector(locator.toString())));
	}

	@Override
	public void mouseDownRight(ClientWidget locator) {
		if (!_isTestCafe) {
			super.mouseDownRight(locator.toLocator());
			return;
		}
		_testCodeList.add(new CafeTestStep(CafeTestStep.ACTION,"//TODO mouseDownRight, selector: " + toCafeSelector(locator.toString())));
	}

	@Override
	public void mouseDownRightAt(ClientWidget locator, String coordString) {
		if (!_isTestCafe) {
			super.mouseDownRightAt(locator.toLocator(), coordString);
			return;
		}
		_testCodeList.add(new CafeTestStep(CafeTestStep.ACTION,"//TODO mouseDownRightAt, selector: " + toCafeSelector(locator.toString())));
	}

	@Override
	public void mouseMove(ClientWidget locator) {
		if (!_isTestCafe) {
			super.mouseMove(locator.toLocator());
			return;
		}
		_testCodeList.add(new CafeTestStep(CafeTestStep.ACTION,"//TODO mouseMove, selector: " + toCafeSelector(locator.toString())));
	}

	@Override
	public void mouseMoveAt(ClientWidget locator, String coordString) {
		if (!_isTestCafe) {
			super.mouseMoveAt(locator.toLocator(), coordString);
			return;
		}
		_testCodeList.add(new CafeTestStep(CafeTestStep.ACTION,"//TODO mouseMoveAt, selector: " + toCafeSelector(locator.toString())));
	}

	@Override
	public void mouseOut(ClientWidget locator) {
		if (!_isTestCafe) {
			super.mouseOut(locator.toLocator());
			return;
		}
		_testCodeList.add(new CafeTestStep(CafeTestStep.ACTION,"//TODO mouseOut, selector: " + toCafeSelector(locator.toString())));
	}

	@Override
	public void mouseUp(ClientWidget locator) {
		if (!_isTestCafe) {
			super.mouseUp(locator.toLocator());
			return;
		}
		_testCodeList.add(new CafeTestStep(CafeTestStep.ACTION,"//TODO mouseUp, selector: " + toCafeSelector(locator.toString())));
	}

	@Override
	public void mouseUpAt(ClientWidget locator, String coordString) {
		if (!_isTestCafe) {
			super.mouseUpAt(locator.toLocator(), coordString);
			return;
		}
		_testCodeList.add(new CafeTestStep(CafeTestStep.ACTION,"//TODO mouseUpAt, selector: " + toCafeSelector(locator.toString())));
	}

	@Override
	public void mouseUpRight(ClientWidget locator) {
		if (!_isTestCafe) {
			super.mouseUpRight(locator.toLocator());
			return;
		}
		_testCodeList.add(new CafeTestStep(CafeTestStep.ACTION,"//TODO mouseUpRight, selector: " + toCafeSelector(locator.toString())));
	}

	@Override
	public void mouseUpRightAt(ClientWidget locator, String coordString) {
		if (!_isTestCafe) {
			super.mouseUpRightAt(locator.toLocator(), coordString);
			return;
		}
		_testCodeList.add(new CafeTestStep(CafeTestStep.ACTION,"//TODO mouseUpRightAt, selector: " + toCafeSelector(locator.toString())));
	}

	/**
	 * Drag Element
	 */

	@Override
	public void dragAndDrop(ClientWidget locator, String movementsString) {
		if (!_isTestCafe) {
			super.dragAndDrop(locator.toLocator(), movementsString);
			return;
		}
		cafeDrag("drag", locator.toLocator(), movementsString);
	}

	@Override
	public void dragdrop(ClientWidget locator, String movementsString) {
		if (!_isTestCafe) {
			super.dragdrop(locator.toLocator(), movementsString);
			return;
		}
		cafeDrag(locator.toLocator(), movementsString);
	}

	@Override
	public void dragdropTo(ClientWidget locatorOfObjectToBeDragged, String from, String to) {
		if (!_isTestCafe) {
			super.dragdropTo(locatorOfObjectToBeDragged.toLocator(), from, to);
			return;
		}
		cafeDrag(locatorOfObjectToBeDragged.toLocator(), to, from);
	}

	@Override
	public void dragdropToObject(ClientWidget locatorOfObjectToBeDragged, ClientWidget locatorOfDragDestinationObject,
								 String from, String to) {
		if (!_isTestCafe) {
			super.dragdropToObject(locatorOfObjectToBeDragged.toLocator(), locatorOfDragDestinationObject.toLocator(), from, to);
			return;
		}
		cafeDragToElement(locatorOfObjectToBeDragged.toLocator(), locatorOfDragDestinationObject.toLocator(), from, to);
	}

	@Override
	public void dragAndDropToObject(ClientWidget locatorOfObjectToBeDragged,
									ClientWidget locatorOfDragDestinationObject) {
		if (!_isTestCafe) {
			super.dragAndDropToObject(locatorOfObjectToBeDragged.toLocator(), locatorOfDragDestinationObject.toLocator());
			return;
		}
		cafeDragToElement(locatorOfObjectToBeDragged.toLocator(), locatorOfDragDestinationObject.toLocator());
	}

	private void cafeDrag(String selectorStr, String movementsString) {
		cafeDrag(selectorStr, movementsString, null);
	}

	private void cafeDrag(String selectorStr, String movementsString, String from) {
		StringBuilder codeStr = new StringBuilder();
		codeStr.append("drag(");
		codeStr.append(toCafeSelector(selectorStr));
		codeStr.append(",");
		if (from == null) {
			codeStr.append(movementsString);
		} else {
			String[] fromCoords = from.replaceAll(" ", "").split(",");
			String[] toCoords = movementsString.replaceAll(" ", "").split(",");
			try {
				movementsString = (Integer.parseInt(toCoords[0]) - Integer.parseInt(fromCoords[0])) + ","
						+ (Integer.parseInt(toCoords[1]) - Integer.parseInt(fromCoords[1]));
			} catch (NumberFormatException e) {
				movementsString = "from: " + from + "," + "to: " + movementsString;
			}
			codeStr.append(movementsString);
			codeStr.append(", {offsetX: " + fromCoords[0] + ", offsetY: " + fromCoords[1] + "}");
		}
		codeStr.append(")");
		_testCodeList.add(new CafeTestStep(CafeTestStep.ACTION, codeStr.toString()));
	}

	private void cafeDragToElement(String fromSelectorStr, String toSelectorStr) {
		cafeDragToElement(fromSelectorStr, toSelectorStr, null, null);
	}

	private void cafeDragToElement(String fromSelectorStr, String toSelectorStr, String from, String to) {
		StringBuilder codeStr = new StringBuilder();
		codeStr.append("dragToElement(");
		codeStr.append(toCafeSelector(fromSelectorStr));
		codeStr.append(",");
		codeStr.append(toCafeSelector(toSelectorStr));
		if (from != null && to != null) {
			String[] fromCoords = from.split(",");
			String[] toCoords = to.split(",");
			codeStr.append(", {offsetX: " + fromCoords[0] + ", offsetY: " + fromCoords[1]);
			codeStr.append(", destinationOffsetX: " + toCoords[0] + ", destinationOffsetY: " + toCoords[1] + "}");
		}
		codeStr.append(")");
		_testCodeList.add(new CafeTestStep(CafeTestStep.ACTION, codeStr.toString()));
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
		cafeHover(locator.toString());
	}

	private void cafeHover(String selectorStr) {
		_testCodeList.add(new CafeTestStep(CafeTestStep.ACTION, "hover(" + toCafeSelector(selectorStr) + ")"));
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
		cafeResizeWindow(width, height);
	}

	private void cafeResizeWindow(int width, int height) {
		_testCodeList.add(new CafeTestStep(CafeTestStep.ACTION, "resizeWindow(" + width + "," + height + ")"));
	}

	/**
	 * TODO
	 * Type Text
	 * Press Key
	 */
	@Override
	public void keyDown(ClientWidget locator, String keySequence) {
		if (!_isTestCafe) {
			super.keyDown(locator.toLocator(), keySequence);
			return;
		}
		//TODO skip
		_testCodeList.add(new CafeTestStep(CafeTestStep.ACTION,"//TODO keyDown"));
	}

	@Override
	public void keyPress(ClientWidget locator, String keySequence) {
		if (!_isTestCafe) {
			super.keyPress(locator.toLocator(), keySequence);
			return;
		}
		//TODO
		_testCodeList.add(new CafeTestStep(CafeTestStep.ACTION,"//TODO keyPress"));
	}

	@Override
	public void keyPressNative(String keycode) {
		if (!_isTestCafe) {
			super.keyPressNative(keycode);
			return;
		}
		//TODO
		_testCodeList.add(new CafeTestStep(CafeTestStep.ACTION,"//TODO keyPressNative"));
	}

	@Override
	public void sendKeys(ClientWidget locator, CharSequence... keysToSend) {
		StringBuilder codeStr = new StringBuilder();
		codeStr.append("pressKey('");
		int cnt = 0;
		for (CharSequence s : keysToSend) {
			String key = KeyHelper.getKey(s);
			if (cnt > 0)
				codeStr.append(" ");
			else
				cnt++;
			codeStr.append(key);
		}
		codeStr.append("')");
		_testCodeList.add(new CafeTestStep(CafeTestStep.ACTION, codeStr.toString()));
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
	public void keyUp(ClientWidget locator, String keySequence) {
		if (!_isTestCafe) {
			super.keyUp(locator, keySequence);
			return;
		}
		//TODO skip
		_testCodeList.add(new CafeTestStep(CafeTestStep.ACTION,"//TODO keyUp"));
	}

	@Override
	public void type(ClientWidget locator, String value) {
		if (!_isTestCafe) {
			super.type(locator, value);
			return;
		}
		cafeType(locator.toString(), value);
	}

	@Override
	public void typeKeys(ClientWidget locator, String value) {
		if (!_isTestCafe) {
			super.typeKeys(locator, value);
			return;
		}
		super.typeKeys(locator, value);
	}

	@Override
	public void longPress(ClientWidget locator) {
		if (!_isTestCafe) {
			super.longPress(locator);
			return;
		}
		//TODO: only used in one tablet case
		_testCodeList.add(new CafeTestStep(CafeTestStep.ACTION,"//TODO longPress"));
	}

	private void cafePressKey(String key) {
		_testCodeList.add(new CafeTestStep(CafeTestStep.ACTION,"pressKey('" + key+ "')"));
	}

	private void cafeType(String selectorStr, String text) {
		StringBuilder codeStr = new StringBuilder();
		codeStr.append("typeText(");
		codeStr.append(toCafeSelector(selectorStr));
		codeStr.append(",'");
		codeStr.append(text);
		codeStr.append("')");
		_testCodeList.add(new CafeTestStep(CafeTestStep.ACTION, codeStr.toString()));
	}

	/**
	 >> Assertions in testcafe api
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
		//ZTLCafeEval=>
		if (word1.contains(CAFEEVAL)) {
			word1 = word1.replace(CAFEEVAL, "");
		} else {
			word1 = "'" + word1 + "'";
		}
		if (contains.contains(CAFEEVAL)) {
			contains = contains.replace(CAFEEVAL, "");
		} else {
			contains = "'" + contains + "'";
		}
		cafeExpect("contains", word1, contains, null);
	}
	@Override
	public void verifyContains(String message, String s1, String s2) {
		if (!_isTestCafe) {
			super.verifyContains(message, s1, s2);
			return;
		}
		//ZTLCafeEval=>
		if (s1.contains(CAFEEVAL)) {
			s1 = s1.replace(CAFEEVAL, "");
		} else {
			s1 = "'" + s1 + "'";
		}
		if (s2.contains(CAFEEVAL)) {
			s2 = s2.replace(CAFEEVAL, "");
		} else {
			s2 = "'" + s2 + "'";
		}
		cafeExpect("contains", s1, s2, message);
	}
	@Override
	public void verifyNotContains(String word1, String contains) {
		if (!_isTestCafe) {
			super.verifyNotContains(word1, contains);
			return;
		}
		//ZTLCafeEval=>
		if (word1.contains(CAFEEVAL)) {
			word1 = word1.replace(CAFEEVAL, "");
		} else {
			word1 = "'" + word1 + "'";
		}
		if (contains.contains(CAFEEVAL)) {
			contains = contains.replace(CAFEEVAL, "");
		} else {
			contains = "'" + contains + "'";
		}
		cafeExpect("notContains", word1, contains, null);
	}


	@Override
	public void verifyNotContains(String message, String s1, String s2) {
		if (!_isTestCafe) {
			super.verifyNotContains(message, s1, s2);
			return;
		}
		cafeExpect("notContains", "'" + s1 + "'", "'" + s2 + "'", message);
	}

	private void cafeExpect(String assertion, String actualExpr, String expectExpr, String message) {
		StringBuilder codeStr = new StringBuilder();
		codeStr.append("expect(");
		codeStr.append(actualExpr);
		codeStr.append(").");
		codeStr.append(assertion);
		codeStr.append("(");
		if (expectExpr != null)
			codeStr.append(expectExpr);
		if (message != null) {
			if (expectExpr != null)
				codeStr.append(",");
			codeStr.append("'");
			codeStr.append(message);
			codeStr.append("'");
		}
		codeStr.append(")");
		_testCodeList.add(new CafeTestStep(CafeTestStep.ASSERTION, codeStr.toString()));
	}

	@Override
	public void verifyEquals(boolean arg1, boolean arg2) {
		if (!_isTestCafe) {
			super.verifyEquals(arg1, arg2);
			return;
		}
		cafeExpect("eql", String.valueOf(arg1), String.valueOf(arg2), null);
	}

	@Override
	public void verifyEquals(String message, boolean arg1, boolean arg2) {
		if (!_isTestCafe) {
			super.verifyEquals(message, arg1, arg2);
			return;
		}
		cafeExpect("eql", String.valueOf(arg1), String.valueOf(arg2), message);
	}

	@Override
	public void verifyEquals(Object s1, Object s2) {
		if (!_isTestCafe) {
			super.verifyEquals(s1, s2);
			return;
		}
		//ZTLCafeEval=>
		String s1Str = String.valueOf(s1);
		if (s1Str.contains(CAFEEVAL)) {
			s1Str = s1Str.replace(CAFEEVAL, "");
		}
		String s2Str = String.valueOf(s2);
		if (s2Str.contains(CAFEEVAL)) {
			s2Str = s2Str.replace(CAFEEVAL, "");
		}
		cafeExpect("eql", s1Str, s2Str, null);
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
	public void verifyEquals(String[] s1, String[] s2) {
		if (!_isTestCafe) {
			super.verifyEquals(s1, s2);
			return;
		}
		//TODO: no case use it
	}

	@Override
	public void verifyEquals(String message, String[] s1, String[] s2) {
		if (!_isTestCafe) {
			super.verifyEquals(message, s1, s2);
			return;
		}
		//TODO: no case use it
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
		//ZTLCafeEval=>
		String str = String.valueOf(expr);
		if (str.contains(CAFEEVAL)) {
			str = str.replace(CAFEEVAL, "");
		} else {
			throw new UnsupportedOperationException("This should be used in test cafe");
		}
		cafeExpect("notOk", str, null, message);
	}

	@Override
	public void verifyNotEquals(boolean s1, boolean s2) {
		if (!_isTestCafe) {
			super.verifyNotEquals(s1, s2);
			return;
		}
		cafeExpect("notEql", String.valueOf(s1), String.valueOf(s2), null);
	}

	@Override
	public void verifyNotEquals(String message, boolean s1, boolean s2) {
		if (!_isTestCafe) {
			super.verifyNotEquals(message, s1, s2);
			return;
		}
		cafeExpect("notEql", String.valueOf(s1), String.valueOf(s2), message);
	}

	@Override
	public void verifyNotEquals(Object s1, Object s2) {
		if (!_isTestCafe) {
			super.verifyNotEquals(s1, s2);
			return;
		}
		//ZTLCafeEval=>
		String s1Str = String.valueOf(s1);
		if (s1Str.contains(CAFEEVAL)) {
			s1Str = s1Str.replace(CAFEEVAL, "");
		}
		String s2Str = String.valueOf(s2);
		if (s2Str.contains(CAFEEVAL)) {
			s2Str = s2Str.replace(CAFEEVAL, "");
		}
		cafeExpect("notEql", s1Str, s2Str, null);
	}

	@Override
	public void verifyNotEquals(String message, Object s1, Object s2) {
		if (!_isTestCafe) {
			super.verifyNotEquals(message, s1, s2);
			return;
		}
		//ZTLCafeEval=>
		String s1Str = String.valueOf(s1);
		if (s1Str.contains(CAFEEVAL)) {
			s1Str = s1Str.replace(CAFEEVAL, "");
		}
		String s2Str = String.valueOf(s2);
		if (s2Str.contains(CAFEEVAL)) {
			s2Str = s2Str.replace(CAFEEVAL, "");
		}
		cafeExpect("notEql", s1Str, s2Str, message);
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
		//ZTLCafeEval=>
		String str = String.valueOf(expr);
		if (str.contains(CAFEEVAL)) {
			str = str.replace(CAFEEVAL, "");
		} else {
			throw new UnsupportedOperationException("This should be used in test cafe");
		}
		cafeExpect("ok", str, null, message);
	}

	@Override
	public void verifyTolerant(int number1, int number2, int tolerant) {
		if (!_isTestCafe) {
			super.verifyTolerant(number1, number2, tolerant);
			return;
		}
		if (Math.abs(number1 - number2) > tolerant) {
			cafeExpect("eql", String.valueOf(number1), String.valueOf(number2), null);
		} else {
			verifyTrue("verifyTolerant (" + number1 + "," + number2 + ") in " + tolerant, true);
		}
	}

	public void verifyTolerant(Object number1, Object number2, Object tolerant) {
		String num1 = String.valueOf(number1);
		String num2 = String.valueOf(number2);
		String tor = String.valueOf(tolerant);
		if (!_isTestCafe) {
			super.verifyTolerant(Integer.parseInt(num1), Integer.parseInt(num2), Integer.parseInt(tor));
			return;
		}
		//ZTLCafeEval=>
		if (num1.contains(CAFEEVAL)) {
			num1 = num1.replace(CAFEEVAL, "");
		}
		if (num2.contains(CAFEEVAL)) {
			num2 = num2.replace(CAFEEVAL, "");
		}
		if (tor.contains(CAFEEVAL)) {
			tor = tor.replace(CAFEEVAL, "");
		}
		cafeVerifyTolerant(num1, num2, tor);
	}

	private void cafeVerifyTolerant(String num1, String num2, String tor) {
		StringBuilder codeStr = new StringBuilder();
		codeStr.append("ztl.verifyTolerant(");
		codeStr.append(num1);
		codeStr.append(", ");
		codeStr.append(num2);
		codeStr.append(", ");
		codeStr.append(tor);
		codeStr.append(");");
		_testCodeList.add(new CafeTestStep(CafeTestStep.EVAL, codeStr.toString()));
	}

	@Override
	public String getEval(String script) {
		if (!_isTestCafe) {
			return super.getEval(script);
		}
		return cafeEval(script, true);
	}

	@Override
	protected void runZscript(String zscript) {
		super.runZscript(zscript);
		waitResponse();
	}

	private String cafeEval(String script, boolean record) {
		String cafeScript = "await ClientFunction(() => " + script + ")()";
		if (record)
			_testCodeList.add(new CafeTestStep(CafeTestStep.EVAL, cafeScript));
		return cafeScript;
	}

	@Override
	protected void sleep(long millis) {
		if (!_isTestCafe) {
			super.sleep(millis);
			return;
		}
		_testCodeList.add(new CafeTestStep(CafeTestStep.ACTION, "wait(" + millis + ")"));
	}

	public void blur(ClientWidget locator) {
		if (!_isTestCafe) {
			super.blur(locator);
			return;
		}
		cafeClick("click", "jq('body')");
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
		return new Widget$Cafe(uuid, _testCodeList);
	}

	@Override
	protected Widget widget(JQuery jQuery) {
		if (!_isTestCafe) {
			return super.widget(jQuery);
		}
		return new Widget$Cafe(jQuery, _testCodeList);
	}

	@Override
	protected JQuery jq(String selector) {
		if (!_isTestCafe) {
			return super.jq(selector);
		}
		return new JQuery$Cafe(selector, _testCodeList);
	}

	@Override
	protected JQuery jq(ClientWidget el) {
		if (!_isTestCafe) {
			return super.jq(el);
		}
		return new JQuery$Cafe(el, _testCodeList);
	}

	@Override
	protected ZK zk(ClientWidget el) {
		if (!_isTestCafe) {
			return super.zk(el);
		}
		return new ZK$Cafe(el, _testCodeList);
	}

	@Override
	protected ZK zk(String selector) {
		if (!_isTestCafe) {
			return super.zk(selector);
		}
		return new ZK$Cafe(selector, _testCodeList);
	}

	//TODO browser issue
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
	public WebDriver getWebDriver() {
		if (!_isTestCafe) {
			return super.getWebDriver();
		}
		throw new UnsupportedOperationException("getWebDriver() not support in test cafe");
	}

	@Override
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
			click(jq(selectLocator).find("option:contains(\"" + optionLocator + "\")"));
		}
	}

	@Override
	public void select(ClientWidget selectLocator, int index) {
		if (!_isTestCafe) {
			super.select(selectLocator, index);
		} else {
			click(jq(selectLocator).find("option").get(index));
		}
	}
	@Override
	public void frozenScroll(ClientWidget locator, int num) {
		if (!_isTestCafe) {
			super.frozenScroll(locator, num);
		} else {
			String lo = locator.toLocator();
			Widget wgt = jq(locator).toWidget();
			wgt.eval("frozen._doScrollNow(" + num + ")");
			waitResponse();
		}
	}

	@Override
	public void verScroll(ClientWidget locator, double percent) {
		if (!_isTestCafe) {
			super.verScroll(locator, percent);
		} else {
			StringBuilder script = new StringBuilder();
			script.append("await ztl.doScroll(t, {locator:").append(locator.toString())
					.append(", scrollType: vertical, percent: ").append(percent);
			_testCodeList.add(new CafeTestStep(CafeTestStep.EVAL, script.toString()));
			waitResponse();
		}
	}

	@Override
	public void verScrollAbs(ClientWidget locator, int dist) {
		if (!_isTestCafe) {
			super.verScrollAbs(locator, dist);
		} else {
			StringBuilder script = new StringBuilder();
			script.append("await ztl.doScroll(t, {locator:").append(locator.toString())
					.append(", scrollType: vertical, dist: ").append(dist);
			_testCodeList.add(new CafeTestStep(CafeTestStep.EVAL, script.toString()));
			waitResponse();
		}
	}

	@Override
	public void horScroll(ClientWidget locator, double percent) {
		if (!_isTestCafe) {
			super.horScroll(locator, percent);
		} else {
			StringBuilder script = new StringBuilder();
			script.append("await ztl.doScroll(t, {locator:").append(locator.toString())
					.append(", scrollType: horizontal, percent: ").append(percent);
			_testCodeList.add(new CafeTestStep(CafeTestStep.EVAL, script.toString()));
			waitResponse();
		}
	}

	@Override
	public void horScrollAbs(ClientWidget locator, int dist) {
		if (!_isTestCafe) {
			super.verScrollAbs(locator, dist);
		} else {
			StringBuilder script = new StringBuilder();
			script.append("await ztl.doScroll(t, {locator:").append(locator.toString())
					.append(", scrollType: horizontal, dist: ").append(dist);
			_testCodeList.add(new CafeTestStep(CafeTestStep.EVAL, script.toString()));
			waitResponse();
		}
	}

	@Override
	public void horScrollNoBody(ClientWidget locator, double percent) {
		if (!_isTestCafe) {
			super.horScrollNoBody(locator, percent);
		} else {
			StringBuilder script = new StringBuilder();
			script.append("await ztl.doScroll(t, {locator:").append(locator.toString())
					.append(", scrollType: horizontalNoBody, percent: ").append(percent);
			_testCodeList.add(new CafeTestStep(CafeTestStep.EVAL, script.toString()));
			waitResponse();
		}
	}

	@Override
	public void verScrollNoBody(ClientWidget locator, double percent) {
		if (!_isTestCafe) {
			super.horScrollNoBody(locator, percent);
		} else {
			StringBuilder script = new StringBuilder();
			script.append("await ztl.doScroll(t, {locator:").append(locator.toString())
					.append(", scrollType: verticalNoBody, percent: ").append(percent);
			_testCodeList.add(new CafeTestStep(CafeTestStep.EVAL, script.toString()));
			waitResponse();
		}
	}

	public void doScroll(ClientWidget locatorOfObjectToBeDragged, ClientWidget locatorOfDragDestinationObject,
						 String from, String to) {
		if (!_isTestCafe) {
			super.doScroll(locatorOfObjectToBeDragged,locatorOfDragDestinationObject, from, to);
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
			Scripts.getEval("!!jq('#zk_logbox').remove();", _testCodeList);
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
			//TODO
		}
	}

	public void navigatePage(boolean forword) {
		if (!_isTestCafe) {
			super.navigatePage(forword);
		} else {
			//TODO
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

	@Override
	protected float parseFloat(String number) {
		if (!_isTestCafe) {
			return super.parseFloat(number);
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
		return Scripts.getEval("!!jq('.z-messagebox-error')[0] || !!jq('.z-errorbox')[0] || jq('.z-error')[0]");
	}

	public String getText(ClientWidget locator) {
		if (!_isTestCafe) {
			return super.getText(locator);
		}
		return cafeEval("jq(" + locator.toString() + ").text()", false);
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
		return Scripts.getEval("!" + locator.toLocator() + "._scrollbar");
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
		return "await ztl.hasHScrollbar(t, " + cafeEval(locator.toString(), false) + ");";
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
		return "await ztl.hasVScrollbar(t, " + cafeEval(locator.toString(), false) + ");";
	}

	public int getScrollTop(Widget widget) {
		if (!_isTestCafe) {
			return super.getScrollTop(widget);
		} else {
			return 0; //CAFE DO LATER
		}
	}
	public String getScrollTop_cafeStr(ClientWidget locator) {
		return "await ztl.getScrollTop(t, " + cafeEval(locator.toString(), false) + ");";
	}

	public int getScrollLeft(Widget widget) {
		if (!_isTestCafe) {
			return super.getScrollLeft(widget);
		} else {
			return 0; //CAFE DO LATER
		}
	}
	public String getScrollLeft_cafeStr(ClientWidget locator) {
		return "await ztl.getScrollLeft(t, " + cafeEval(locator.toString(), false) + ");";
	}

	public String getZKLog() {
		if (!_isTestCafe) {
			return super.getZKLog();
		} else {
			return jq("#zk_log").val();
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
		return Scripts.getEval("!!(zk." + name + ")");
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
		return Scripts.getEval("document.body.offsetWidth");
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
		return Scripts.getEval("document.body.offsetHeight");
	}

	public int getBrowserTabCount() {
		if (!_isTestCafe) {
			return super.getBrowserTabCount();
		} else {
			throw new UnsupportedOperationException("not support in test cafe");
		}
	}
	//internal use only
	public String evalMath_cafe(String expr) {

		return expr;
	}

	public void assignment_cafe(String indentifier, String expr, boolean isNew){
		StringBuilder codeStr = new StringBuilder();
		if (isNew)
			codeStr.append("let ");
		codeStr.append(indentifier);
		codeStr.append(" = ");
		codeStr.append(cafeEval(expr, false));
		codeStr.append(";");
		_testCodeList.add(new CafeTestStep(CafeTestStep.EVAL, codeStr.toString()));
	}
}
