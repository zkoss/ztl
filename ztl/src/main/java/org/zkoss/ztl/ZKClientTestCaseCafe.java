package org.zkoss.ztl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.openqa.selenium.*;
import org.zkoss.ztl.testcafe.CafeTestStep;
import org.zkoss.ztl.testcafe.KeyHelper;

/**
 * A transpiler to write test cafe in ztl
 * @author jameschu
 *
 */
public class ZKClientTestCaseCafe extends ZKClientTestCase {
	public static List<CafeTestStep> testCodeList = new LinkedList<CafeTestStep>();

	public void generateCafeTest(String targetUrl) throws IllegalArgumentException {
		/*
		import { ClientFunction, Selector} from 'testcafe';
		import * as ztl from './module/ztl.js';

		fixture `BXX`.page `URL`;

		test('ZTL TEST', async t => {

		});
		 */

		//this.getClass.getSimpleName.replace("_", "-").replace("Test", ".zul"
		String testName = this.getClass().getSimpleName().replace("_", "-");
		StringBuilder testContent = new StringBuilder();
		testContent.append("import { ClientFunction, Selector} from 'testcafe';\n");
		testContent.append("import * as ztl from './module/ztl.js';\n");
		testContent.append("fixture `ZTL TEST - ");
		testContent.append(testName);
		testContent.append("`.page `");
		testContent.append(targetUrl);
		testContent.append("`;\ntest('");
		testContent.append(testName);
		testContent.append("', async t => {\n");
		String lastStepType = null;
		for (CafeTestStep step : testCodeList) {
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
		testCodeList.clear();
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

	//waitResponse
	@Override
	protected void waitResponse() {
		testCodeList.add(new CafeTestStep(CafeTestStep.EVAL,"await ztl.waitResponse(t)"));
	}

	@Override
	protected void waitResponse(boolean includingAnimation) {
		waitResponse();
	}

	@Override
	protected void waitResponse(int timeout, boolean includingAnimation) {
		waitResponse();
	}

	@Override
	protected void waitResponse(int timeout) {
		waitResponse();
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
		getEval(locator.toLocator() + ".focus()");
	}

	/**
	 * Click (check
	 * Double Click
	 * Right Click
	 */

	@Override
	public void check(ClientWidget locator) {
		click(locator);
	}

	@Override
	public void click(ClientWidget locator) {
		cafeClick("click", locator.toLocator());
	}

	@Override
	public void clickAt(ClientWidget locator, String coordString) {
		cafeClick("click", locator.toLocator(), coordString);
	}

	@Override
	public void contextMenu(ClientWidget locator) {
		cafeClick("rightClick", locator.toLocator());
	}

	@Override
	public void contextMenuAt(ClientWidget locator, String coordString) {
		cafeClick("rightClick", locator.toLocator(), coordString);
	}

	@Override
	public void doubleClick(ClientWidget locator) {
		cafeClick("doubleClick", locator.toLocator());
	}

	@Override
	public void doubleClickAt(ClientWidget locator, String coordString) {
		cafeClick("doubleClick", locator.toLocator(), coordString);
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
		testCodeList.add(new CafeTestStep(CafeTestStep.ACTION,codeStr.toString()));
	}

	/**
	 * Mouse move
	 */

	@Override
	public void mouseDown(ClientWidget locator) {
		testCodeList.add(new CafeTestStep(CafeTestStep.ACTION,"//TODO mouseDown, selector: " + toCafeSelector(locator.toString())));
	}

	@Override
	public void mouseDownAt(ClientWidget locator, String coordString) {
		testCodeList.add(new CafeTestStep(CafeTestStep.ACTION,"//TODO mouseDownAt, selector: " + toCafeSelector(locator.toString())));
	}

	@Override
	public void mouseDownRight(ClientWidget locator) {
		testCodeList.add(new CafeTestStep(CafeTestStep.ACTION,"//TODO mouseDownRight, selector: " + toCafeSelector(locator.toString())));
	}

	@Override
	public void mouseDownRightAt(ClientWidget locator, String coordString) {
		testCodeList.add(new CafeTestStep(CafeTestStep.ACTION,"//TODO mouseDownRightAt, selector: " + toCafeSelector(locator.toString())));
	}

	@Override
	public void mouseMove(ClientWidget locator) {
		testCodeList.add(new CafeTestStep(CafeTestStep.ACTION,"//TODO mouseMove, selector: " + toCafeSelector(locator.toString())));
	}

	@Override
	public void mouseMoveAt(ClientWidget locator, String coordString) {
		testCodeList.add(new CafeTestStep(CafeTestStep.ACTION,"//TODO mouseMoveAt, selector: " + toCafeSelector(locator.toString())));
	}

	@Override
	public void mouseOut(ClientWidget locator) {
		testCodeList.add(new CafeTestStep(CafeTestStep.ACTION,"//TODO mouseOut, selector: " + toCafeSelector(locator.toString())));
	}

	@Override
	public void mouseUp(ClientWidget locator) {
		testCodeList.add(new CafeTestStep(CafeTestStep.ACTION,"//TODO mouseUp, selector: " + toCafeSelector(locator.toString())));
	}

	@Override
	public void mouseUpAt(ClientWidget locator, String coordString) {
		testCodeList.add(new CafeTestStep(CafeTestStep.ACTION,"//TODO mouseUpAt, selector: " + toCafeSelector(locator.toString())));
	}

	@Override
	public void mouseUpRight(ClientWidget locator) {
		testCodeList.add(new CafeTestStep(CafeTestStep.ACTION,"//TODO mouseUpRight, selector: " + toCafeSelector(locator.toString())));
	}

	@Override
	public void mouseUpRightAt(ClientWidget locator, String coordString) {
		testCodeList.add(new CafeTestStep(CafeTestStep.ACTION,"//TODO mouseUpRightAt, selector: " + toCafeSelector(locator.toString())));
	}

	/**
	 * Drag Element
	 */

	@Override
	public void dragAndDrop(ClientWidget locator, String movementsString) {
		cafeDrag("drag", locator.toLocator(), movementsString);
	}

	@Override
	public void dragdrop(ClientWidget locator, String movementsString) {
		cafeDrag(locator.toLocator(), movementsString);
	}

	@Override
	public void dragdropTo(ClientWidget locatorOfObjectToBeDragged, String from, String to) {
		cafeDrag(locatorOfObjectToBeDragged.toLocator(), to, from);
	}

	@Override
	public void dragdropToObject(ClientWidget locatorOfObjectToBeDragged, ClientWidget locatorOfDragDestinationObject,
			String from, String to) {
		cafeDragToElement(locatorOfObjectToBeDragged.toLocator(), locatorOfDragDestinationObject.toLocator(), from, to);
	}

	@Override
	public void dragAndDropToObject(ClientWidget locatorOfObjectToBeDragged,
			ClientWidget locatorOfDragDestinationObject) {
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
		testCodeList.add(new CafeTestStep(CafeTestStep.ACTION, codeStr.toString()));
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
		testCodeList.add(new CafeTestStep(CafeTestStep.ACTION, codeStr.toString()));
	}

	/**
	 * Hover
	 */
	@Override
	public void mouseOver(By locator) {
		cafeHover(locator.toString());
	}

	private void cafeHover(String selectorStr) {
		testCodeList.add(new CafeTestStep(CafeTestStep.ACTION, "hover(" + toCafeSelector(selectorStr) + ")"));
	}

	/**
	 * Resize Window
	 */
	@Override
	public void windowResizeTo(int width, int height) {
		cafeResizeWindow(width, height);
	}

	private void cafeResizeWindow(int width, int height) {
		testCodeList.add(new CafeTestStep(CafeTestStep.ACTION, "resizeWindow(" + width + "," + height + ")"));
	}

	/**
	 * TODO
	 * Type Text
	 * Press Key
	 */
	@Override
	public void keyDown(ClientWidget locator, String keySequence) {
		//TODO skip
		testCodeList.add(new CafeTestStep(CafeTestStep.ACTION,"//TODO keyDown"));
	}

	@Override
	public void keyPress(ClientWidget locator, String keySequence) {
		//TODO
		testCodeList.add(new CafeTestStep(CafeTestStep.ACTION,"//TODO keyPress"));
	}

	@Override
	public void keyPressNative(String keycode) {
		//TODO
		testCodeList.add(new CafeTestStep(CafeTestStep.ACTION,"//TODO keyPressNative"));
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
		testCodeList.add(new CafeTestStep(CafeTestStep.ACTION, codeStr.toString()));
	}

	@Override
	public void keyPressEnter(ClientWidget locator) {
		//click + press enter
		click(locator);
		cafePressKey("enter");
	}

	@Override
	public void keyUp(ClientWidget locator, String keySequence) {
		//TODO skip
		testCodeList.add(new CafeTestStep(CafeTestStep.ACTION,"//TODO keyUp"));
	}

	@Override
	public void type(ClientWidget locator, String value) {
		cafeType(locator.toString(), value);
	}

	@Override
	public void typeKeys(ClientWidget locator, String value) {
		super.typeKeys(locator, value);
	}

	@Override
	public void longPress(ClientWidget locator) {
		//TODO: only used in one tablet case
		testCodeList.add(new CafeTestStep(CafeTestStep.ACTION,"//TODO longPress"));
	}

	private void cafePressKey(String key) {
		testCodeList.add(new CafeTestStep(CafeTestStep.ACTION,"pressKey('" + key+ "')"));
	}

	private void cafeType(String selectorStr, String text) {
		StringBuilder codeStr = new StringBuilder();
		codeStr.append("typeText(");
		codeStr.append(toCafeSelector(selectorStr));
		codeStr.append(",'");
		codeStr.append(text);
		codeStr.append("')");
		testCodeList.add(new CafeTestStep(CafeTestStep.ACTION, codeStr.toString()));
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
		cafeExpect("contains", "'" + word1 + "'", "'" + contains + "'", null);
	}

	@Override
	public void verifyNotContains(String word1, String contains) {
		cafeExpect("notContains", "'" + word1 + "'", "'" + contains + "'", null);
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
		testCodeList.add(new CafeTestStep(CafeTestStep.ASSERTION, codeStr.toString()));
	}

	@Override
	public void verifyEquals(boolean arg1, boolean arg2) {
		cafeExpect("eql", String.valueOf(arg1), String.valueOf(arg2), null);
	}

	@Override
	public void verifyEquals(String message, boolean arg1, boolean arg2) {
		cafeExpect("eql", String.valueOf(arg1), String.valueOf(arg2), message);
	}

	@Override
	public void verifyEquals(Object s1, Object s2) {
		System.out.println("verifyEquals Object -> " + s1);
		cafeExpect("eql", String.valueOf(s1), String.valueOf(s2), null);
	}

	@Override
	public void verifyEquals(String message, Object s1, Object s2) {
		System.out.println("verifyEquals Object -> " + s1);
		cafeExpect("eql", String.valueOf(s1), String.valueOf(s2), message);
	}

	@Override
	public void verifyContains(String message, String s1, String s2) {
		cafeExpect("contains", "'" + s1 + "'", "'" + s2 + "'", message);
	}

	@Override
	public void verifyNotContains(String message, String s1, String s2) {
		cafeExpect("notContains", "'" + s1 + "'", "'" + s2 + "'", message);
	}


	@Override
	public void verifyEquals(String[] s1, String[] s2) {
		//TODO: no case use it
	}

	@Override
	public void verifyEquals(String message, String[] s1, String[] s2) {
		//TODO: no case use it
	}

	@Override
	public void verifyFalse(boolean b) {
		cafeExpect("notOk", String.valueOf(b), null, null);
	}

	@Override
	public void verifyFalse(String message, boolean b) {
		cafeExpect("notOk", String.valueOf(b), null, message);
	}

	@Override
	public void verifyNotEquals(boolean s1, boolean s2) {
		cafeExpect("notEql", String.valueOf(s1), String.valueOf(s2), null);
	}

	@Override
	public void verifyNotEquals(String message, boolean s1, boolean s2) {
		cafeExpect("notEql", String.valueOf(s1), String.valueOf(s2), message);
	}

	@Override
	public void verifyNotEquals(Object s1, Object s2) {
		System.out.println("verifyNotEquals Object -> " + s1);
		cafeExpect("notEql", String.valueOf(s1), String.valueOf(s2), null);
	}

	@Override
	public void verifyNotEquals(String message, Object s1, Object s2) {
		System.out.println("verifyNotEquals Object -> " + s1);
		cafeExpect("notEql", String.valueOf(s1), String.valueOf(s2), message);
	}

	@Override
	public void verifyTrue(boolean b) {
		cafeExpect("ok", String.valueOf(b), null, null);
	}

	@Override
	public void verifyTrue(String message, boolean b) {
		cafeExpect("ok", String.valueOf(b), null, message);
	}

	@Override
	public void verifyTolerant(int number1, int number2, int tolerant) {
		if (Math.abs(number1 - number2) > tolerant) {
			cafeExpect("eql", String.valueOf(number1), String.valueOf(number2), null);
		} else {
			verifyTrue("verifyTolerant (" + number1 + "," + number2 + ") in " + tolerant, true);
		}
	}

	@Override
	public String getEval(String script) {
		return cafeEval(script);
	}

	public static String callEval(String script) {
		return cafeEval(script);
	}

	private static String cafeEval(String script) {
		String cafeScript = "await ClientFunction(() => " + script + ")()";
		testCodeList.add(new CafeTestStep(CafeTestStep.EVAL, cafeScript));
		return cafeScript;
	}

	@Override
	public String getText(ClientWidget locator) {
		return cafeEval("jq(" + locator.toString() + ").text()");
	}

	@Override
	protected void sleep(long millis) {
		testCodeList.add(new CafeTestStep(CafeTestStep.ACTION, "wait(" + millis + ")"));
	}


	//TODO browser issue
	@Override
	public boolean isIE() {
		return false;
	}

	@Override
	public boolean isEdge() {
		return false;
	}

	@Override
	public boolean isFirefox() {
		return false;
	}

	@Override
	public boolean isChrome() {
		return false;
	}

	@Override
	public boolean isSafari() {
		return false;
	}

	@Override
	public boolean isOpera() {
		return false;
	}

	@Override
	public boolean isIPhone() {
		return false;
	}

	@Override
	public boolean isAndroid() {
		return false;
	}
}
