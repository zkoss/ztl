/* ZKTestCase.java

	Purpose:
		
	Description:
		
	History:
		Wed Sep 16 12:49:43 TST 2009, Created by sam

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.ztl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.HasInputDevices;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keyboard;
import org.openqa.selenium.Mouse;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.android.AndroidDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.internal.WrapsDriver;
import org.openqa.selenium.iphone.IPhoneDriver;
import org.zkoss.ztl.util.ConfigHelper;
import org.zkoss.ztl.util.Scripts;
import org.zkoss.ztl.util.ZKSelenium;
import org.zkoss.ztl.util.image.Comparator;
import org.zkoss.ztl.util.image.DefaultComparator;
import org.zkoss.ztl.webdriver.ZKRemoteWebDriver;

import com.opera.core.systems.OperaDriver;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import com.thoughtworks.selenium.Selenium;
/**
 * A skeleton of ZK Selenium test, which implements all of the methods of {@link Selenium}
 * interface.
 * 
 * @author sam
 * @author jumperchen
 *
 */
public class ZKTestCase extends ZKSeleneseTestCase implements Selenium {
	protected static final ThreadLocal<Selenium> _selenium = new ThreadLocal<Selenium>();
	/**
	 * The keycode of the PAGE_UP key
	 */
	public static final String PAGE_UP = "33";
	/**
	 * The keycode of the PAGE_DOWN key
	 */
	public static final String PAGE_DOWN = "34";
	/**
	 * The keycode of the END key
	 */
	public static final String END = "35";
	/**
	 * The keycode of the HOME key
	 */
	public static final String HOME = "36";
	/**
	 * The keycode of the LEFT key
	 */
	public static final String LEFT = "37";
	/**
	 * The keycode of the UP key
	 */
	public static final String UP = "38";
	/**
	 * The keycode of the RIGHT key
	 */
	public static final String RIGHT = "39";
	/**
	 * The keycode of the DOWN key
	 */
	public static final String DOWN = "40";
	/**
	 * The keycode of the INSERT key
	 */
	public static final String INSERT = "45";
	/**
	 * The keycode of the DELETE key
	 */
	public static final String DELETE = "46";
	/**
	 * keypressNative native follow java.awt.Event's keycode .
	 */
	public static final String DELETE_NATIVE = "127";
	/**
	 * The keycode of the BACKSPACE key
	 */
	public static final String BACKSPACE = "8";
	/**
	 * The keycode of the TAB key
	 */
	public static final String TAB = "9";
	/**
	 * The keycode of the ENTER key
	 */
	public static final String ENTER = "10";
	/**
	 * The keycode of the ESC key
	 */
	public static final String ESC = "27";
	/**
	 * The keycode of the SHIFT key
	 */
	public static final String SHIFT = "16";
	/**
	 * The keycode of the CTRL key
	 */
	public static final String CTRL = "17";
	/**
	 * The keycode of the ALT key
	 */
	public static final String ALT = "18";
	
	/** * The keycode of the Num_0 */
	public static final String NUM_0 = "48";

	/**
	 * The keycode of the Num_1
	 * */
	public static final String NUM_1 = "49";

	/** * The keycode of the Num_2 */
	public static final String NUM_2 = "50";

	/** * The keycode of the Num_3 */
	public static final String NUM_3 = "51";

	/** * The keycode of the Num_4 */
	public static final String NUM_4 = "52";

	/** * The keycode of the Num_5 */
	public static final String NUM_5 = "53";

	/** * The keycode of the Num_6 */
	public static final String NUM_6 = "54";

	/** * The keycode of the Num_7 */
	public static final String NUM_7 = "55";

	/** * The keycode of the Num_8 */
	public static final String NUM_8 = "56";

	/** * The keycode of the Num_9 */
	public static final String NUM_9 = "57";	
	/**
	 * The keycode of the A key
	 */
	public static final String A = "65";
	/**
	 * The keycode of the C key
	 */
	public static final String C = "67";
	/**
	 * The keycode of the V key
	 */
	public static final String V = "86";
	/**
	 * The keycode of the X key
	 */
	public static final String X = "88";
	
	/**
	 * The prefix is depended on what the ID generator is.
	 */
	private static String PREFIX = "zk_comp_";

	private static SimpleDateFormat format = new SimpleDateFormat("yyMMddHH");
	
	// implicit variable
	protected String target;
	protected List<Selenium> browsers;
	protected String caseID;
	protected int recordCount;

	/**
	 * Launches the browser with a new Selenium session
	 */
	protected void start(Selenium selenium) {
		_selenium.set(selenium);
		System.out.println("testing:"+((ZKSelenium)selenium).getBrowserName());
		selenium.open(target);
		
		// fixed ZK timing issue for ZTL
		((JavascriptExecutor) getWebDriver()).executeScript(Scripts.ZK_FIXED_SCRIPTS);
		
		if (ConfigHelper.getInstance().isDebuggable()) {
			((JavascriptExecutor) getWebDriver()).executeScript(Scripts.ZTL_DEBUGGER_SCRIPTS);
		}
		this.selenium = selenium;
		recordCount = 0; // reset
	}
	
	/**
	 * Returns the current browser.
	 */
	public static final Selenium getCurrent() {
	    Selenium selenium = _selenium.get();
	    if (selenium == null)
	        Thread.dumpStack();
	    
	    return selenium;
	}

	/**
	 * Returns the web driver.
	 * @since 2.0.0
	 */
	public WebDriver getWebDriver() {
		return ((ZKSelenium) getCurrent()).getWrappedDriver();
	}

	/**
	 * Returns the browser mouse for Selenium 2.x
	 * @since 2.0.0
	 */
	public Mouse getMouse() {
		return ((HasInputDevices) getWebDriver()).getMouse(); 	
	}
	

	/**
	 * Returns the browser keyboard for Selenium 2.x
	 * @since 2.0.0
	 */
    public Keyboard getKeyboard() {
		return ((HasInputDevices) getWebDriver()).getKeyboard(); 	
    }
    
	/**
	 * Returns the browser actions for Selenium 2.x
	 * @since 2.0.0
	 */
	public Actions getActions() {
		return new Actions(getWebDriver());
	}

	/**
	 * Returns whether is InternatExplorer Driver
	 * @since 2.0.0
	 */
	public boolean isIE() {
		WebDriver driver = getWebDriver();
		return (driver instanceof InternetExplorerDriver || 
				(driver instanceof ZKRemoteWebDriver && ((ZKRemoteWebDriver) driver)
						.getCapabilities().getBrowserName().contains("internet")));
	}

	/**
	 * Returns whether is Firefox Driver
	 * @since 2.0.0
	 */
	public boolean isFirefox() {
		WebDriver driver = getWebDriver();
		return (driver instanceof FirefoxDriver
				|| (driver instanceof ZKRemoteWebDriver && ((ZKRemoteWebDriver) driver)
						.getCapabilities().getBrowserName().contains("firefox")));
	}

	/**
	 * Returns whether is Chrome Driver
	 * @since 2.0.0
	 */
	public boolean isChrome() {
		WebDriver driver = getWebDriver();
		return (driver instanceof ChromeDriver
				|| (driver instanceof ZKRemoteWebDriver && ((ZKRemoteWebDriver) driver)
						.getCapabilities().getBrowserName().contains("chrome")));
	}

	/**
	 * Returns whether is Opera Driver
	 * @since 2.0.0
	 */
	public boolean isOpera() {
		WebDriver driver = getWebDriver();
		return (driver instanceof OperaDriver
				|| (driver instanceof ZKRemoteWebDriver && ((ZKRemoteWebDriver) driver)
						.getCapabilities().getBrowserName().contains("opera")));
	}


	/**
	 * Returns whether is IPhone Driver
	 * @since 2.0.0
	 */
	public boolean isIPhone() {
		WebDriver driver = getWebDriver();
		return (driver instanceof IPhoneDriver
				|| (driver instanceof ZKRemoteWebDriver && ((ZKRemoteWebDriver) driver)
						.getCapabilities().getBrowserName().contains("iphone")));
	}


	/**
	 * Returns whether is Android Driver
	 * @since 2.0.0
	 */
	public boolean isAndroid() {
		WebDriver driver = getWebDriver();
		return (driver instanceof AndroidDriver
				|| (driver instanceof ZKRemoteWebDriver && ((ZKRemoteWebDriver) driver)
						.getCapabilities().getBrowserName().contains("android")));
	}
	
	/**
	 * Find the first {@link WebElement} using the given method.
	 * 
	 * @param by
	 *            The locating mechanism
	 * @return The first matching element on the current page
	 * @throws NoSuchElementException
	 *             If no matching elements are found
	 */
	public WebElement findElement(By by) {
		return getWebDriver().findElement(by);
	}
	
	@Override
	public void setUp() {
		if (target == null)
			throw new NullPointerException("target cannot be null!");
	}

	protected final static String uuid(int number) {
		return PREFIX + number;
	}
	
	protected List<Selenium> getBrowsers(String browsers) {
		return ConfigHelper.getInstance().getBrowsersForLazy(browsers);
	}
	
	/**
	 * Resizes the current window to the size(width and height).
	 */
	public void windowResizeTo(int width, int height) {
		getCurrent().getEval("window.resizeTo("+width + "," + height+")");
	}
	
	/** untested yet
	protected List<LoggingSelenium> getLoggingBrowsers(String browsers, BufferedWriter loggingWriter){
		return ConfigHelper.getInstance().getLoggingBrowsers(browsers, loggingWriter);
	}
	*/
	
	@Override
	public void addLocationStrategy(String strategyName,
			String functionDefinition) {
		getCurrent().addLocationStrategy(strategyName, functionDefinition);
	}

	@Override
	public void addScript(String scriptContent, String scriptTagId) {
		getCurrent().addScript(scriptContent, scriptTagId);
	}

	@Override
	public void addSelection(String locator, String optionLocator) {
		getCurrent().addSelection(locator, optionLocator);
	}

	@Override
	public void allowNativeXpath(String allow) {
		getCurrent().allowNativeXpath(allow);
	}

	@Override
	public void altKeyDown() {
		getCurrent().altKeyDown();
	}

	@Override
	public void altKeyUp() {
		getCurrent().altKeyUp();
	}

	@Override
	public void answerOnNextPrompt(String answer) {
		getCurrent().answerOnNextPrompt(answer);
	}

	@Override
	public void assignId(String locator, String identifier) {
		getCurrent().assignId(locator, identifier);
	}

	@Override
	public void attachFile(String fieldLocator, String fileLocator) {
		getCurrent().attachFile(fieldLocator, fileLocator);		
	}

	@Override
	public void captureEntirePageScreenshot(String filename, String kwargs) {
		getCurrent().captureEntirePageScreenshot(filename, kwargs);
	}

	@Override
	public String captureEntirePageScreenshotToString(String kwargs) {
		return getCurrent().captureEntirePageScreenshotToString(kwargs);
	}

	@Override
	public void captureScreenshot(String filename) {
		getCurrent().captureScreenshot(filename);
	}

	@Override
	public String captureScreenshotToString() {
		return getCurrent().captureScreenshotToString();
	}

	@Override
	public void check(String locator) {
		getCurrent().check(locator);
	}

	@Override
	public void chooseCancelOnNextConfirmation() {
		getCurrent().chooseCancelOnNextConfirmation();
	}

	@Override
	public void chooseOkOnNextConfirmation() {
		getCurrent().chooseOkOnNextConfirmation();
	}

	@Override
	public void click(String locator) {
		getCurrent().click(locator);
	}

	@Override
	public void clickAt(String locator, String coordString) {
		getCurrent().clickAt(locator, coordString);
	}

	@Override
	public void close() {
		getCurrent().close();
	}

	@Override
	public void contextMenu(String locator) {
		getCurrent().contextMenu(locator);
	}

	@Override
	public void contextMenuAt(String locator, String coordString) {
		getCurrent().contextMenuAt(locator, coordString);
	}

	@Override
	public void controlKeyDown() {
		getCurrent().controlKeyDown();
	}

	@Override
	public void controlKeyUp() {
		getCurrent().controlKeyUp();
	}

	@Override
	public void createCookie(String nameValuePair, String optionsString) {
		getCurrent().createCookie(nameValuePair, optionsString);
	}

	@Override
	public void deleteAllVisibleCookies() {
		getCurrent().deleteAllVisibleCookies();
	}

	@Override
	public void deleteCookie(String name, String optionsString) {
		getCurrent().deleteCookie(name, optionsString);
	}

	@Override
	public void doubleClick(String locator) {
		getCurrent().doubleClick(locator);
	}

	@Override
	public void doubleClickAt(String locator, String coordString) {
		getCurrent().doubleClickAt(locator, coordString);
	}

	@Override
	public void dragAndDrop(String locator, String movementsString) {
		getCurrent().dragAndDrop(locator, movementsString);
	}
	
	/**
	 * Drags and drops the specific element from its specific area to another area.
	 * <p>For example,<br/>
	 * 		draggdropTo("z-xxx", "10,20", "20,20")
	 * <p>The result of the "z-xxx" is moved 10px right.
	 * @param locatorOfObjectToBeDragged the draggable UUID
	 * @param from the "x,y" value is related to the draggable element, which is dragged from.
	 * @param to the "x,y" value is related to the draggable element, which is dropped to.
	 */
	public void dragdropTo(String locatorOfObjectToBeDragged, String from, String to) {
		ZKSelenium browser = (ZKSelenium) getCurrent();
		browser.mouseMoveAt(locatorOfObjectToBeDragged, from);
		browser.mouseDownAt(locatorOfObjectToBeDragged, from);

		// wait mouse to move (B30-1822564.ztl)
		if (isChrome())
			sleep(Integer.parseInt(ConfigHelper.getInstance().getDelay()));
		browser.mouseMoveAt(locatorOfObjectToBeDragged, to);
		browser.mouseUpAt(locatorOfObjectToBeDragged, to);
	}
	/**
	 * Drags and drops the specific element from its specific area to another element.
	 * <p>For example,<br/>
	 * 		draggdropToObject("z-xxx", "z-yyy", "10,20", "10,20")
	 * <p>The result of the "z-xxx" is moved to the position(10,20) of the "z-yyy".
	 * @param locatorOfObjectToBeDragged the draggable UUID
	 * @param locatorOfDragDestinationObject the droppable UUID
	 * @param from the "x,y" value is related to the draggable element, which is dragged from.
	 * @param to the "x,y" value is related to the droppable element, which is dropped to.
	 */
	public void dragdropToObject(String locatorOfObjectToBeDragged,
			String locatorOfDragDestinationObject, String from, String to) {
		ZKSelenium browser = (ZKSelenium) getCurrent();
		browser.mouseMoveAt(locatorOfObjectToBeDragged, from);
		browser.mouseDownAt(locatorOfObjectToBeDragged, from);
		browser.mouseMoveAt(locatorOfDragDestinationObject, to);
		browser.mouseUpAt(locatorOfDragDestinationObject, to);
	}
	
	@Override
	public void dragAndDropToObject(String locatorOfObjectToBeDragged,
			String locatorOfDragDestinationObject) {
		getCurrent().dragAndDropToObject(locatorOfObjectToBeDragged, locatorOfDragDestinationObject);
	}

	@Override
	public void dragdrop(String locator, String movementsString) {
		getCurrent().dragdrop(locator, movementsString);
	}

	@Override
	public void fireEvent(String locator, String eventName) {
		getCurrent().fireEvent(locator, eventName);
	}

	@Override
	public void focus(String locator) {
		getCurrent().focus(locator);
	}

	@Override
	public String getAlert() {
		return getCurrent().getAlert();
	}

	@Override
	public String[] getAllButtons() {
		return getCurrent().getAllButtons();
	}

	@Override
	public String[] getAllFields() {
		return getCurrent().getAllFields();
	}

	@Override
	public String[] getAllLinks() {
		return getCurrent().getAllLinks();
	}

	@Override
	public String[] getAllWindowIds() {
		return getCurrent().getAllWindowIds();
	}

	@Override
	public String[] getAllWindowNames() {
		return getCurrent().getAllWindowNames();
	}

	@Override
	public String[] getAllWindowTitles() {
		return getCurrent().getAllWindowTitles();
	}

	@Override
	public String getAttribute(String attributeLocator) {
		return getCurrent().getAttribute(attributeLocator);
	}

	@Override
	public String[] getAttributeFromAllWindows(String attributeName) {
		return getCurrent().getAttributeFromAllWindows(attributeName);
	}

	@Override
	public String getBodyText() {
		return getCurrent().getBodyText();
	}

	@Override
	public String getConfirmation() {
		return getCurrent().getConfirmation();
	}

	@Override
	public String getCookie() {
		return getCurrent().getCookie();
	}

	@Override
	public String getCookieByName(String name) {
		return getCurrent().getCookieByName(name);
	}

	@Override
	public Number getCursorPosition(String locator) {
		return getCurrent().getCursorPosition(locator);
	}

	@Override
	public Number getElementHeight(String locator) {
		return getCurrent().getElementHeight(locator);
	}

	@Override
	public Number getElementIndex(String locator) {
		return getCurrent().getElementIndex(locator);
	}

	@Override
	public Number getElementPositionLeft(String locator) {
		return getCurrent().getElementPositionLeft(locator);
	}

	@Override
	public Number getElementPositionTop(String locator) {
		return getCurrent().getElementPositionTop(locator);
	}

	@Override
	public Number getElementWidth(String locator) {
		return getCurrent().getElementWidth(locator);
	}

	@Override
	public String getEval(String script) {
		//return getCurrent().(script);
		WebDriver driver = ((WrapsDriver) getCurrent()).getWrappedDriver(); 
		return String.valueOf(((JavascriptExecutor) driver).executeScript("return ("+ script+");"));
	}

	@Override
	public String getExpression(String expression) {
		return getCurrent().getExpression(expression);
	}

	@Override
	public String getHtmlSource() {
		return getCurrent().getHtmlSource();
	}

	@Override
	public String getLocation() {
		return getCurrent().getLocation();
	}

	@Override
	public Number getMouseSpeed() {
		return getCurrent().getMouseSpeed();
	}

	@Override
	public String getPrompt() {
		return getCurrent().getPrompt();
	}

	@Override
	public String[] getSelectOptions(String selectLocator) {
		return getCurrent().getSelectOptions(selectLocator);
	}

	@Override
	public String getSelectedId(String selectLocator) {
		return getCurrent().getSelectedId(selectLocator);
	}

	@Override
	public String[] getSelectedIds(String selectLocator) {
		return getCurrent().getSelectedIds(selectLocator);
	}

	@Override
	public String getSelectedIndex(String selectLocator) {
		return getCurrent().getSelectedIndex(selectLocator);
	}

	@Override
	public String[] getSelectedIndexes(String selectLocator) {
		return getCurrent().getSelectedIndexes(selectLocator);
	}

	@Override
	public String getSelectedLabel(String selectLocator) {
		return getCurrent().getSelectedLabel(selectLocator);
	}

	@Override
	public String[] getSelectedLabels(String selectLocator) {
		return getCurrent().getSelectedLabels(selectLocator);
	}

	@Override
	public String getSelectedValue(String selectLocator) {
		return getCurrent().getSelectedValue(selectLocator);
	}

	@Override
	public String[] getSelectedValues(String selectLocator) {
		return getCurrent().getSelectedValues(selectLocator);
	}

	@Override
	public String getSpeed() {
		return getCurrent().getSpeed();
	}

	@Override
	public String getTable(String tableCellAddress) {
		return getCurrent().getTable(tableCellAddress);
	}

	@Override
	public String getText(String locator) {
		return getCurrent().getText(locator);
	}

	@Override
	public String getTitle() {
		return getCurrent().getTitle();
	}

	@Override
	public String getValue(String locator) {
		return getCurrent().getValue(locator);
	}

	@Override
	public boolean getWhetherThisFrameMatchFrameExpression(
			String currentFrameString, String target) {
		return getCurrent().getWhetherThisFrameMatchFrameExpression(currentFrameString, target);
	}

	@Override
	public boolean getWhetherThisWindowMatchWindowExpression(
			String currentWindowString, String target) {
		return getCurrent().getWhetherThisWindowMatchWindowExpression(currentWindowString, target);
	}

	@Override
	public Number getXpathCount(String xpath) {
		return getCurrent().getXpathCount(xpath);
	}

	@Override
	public void goBack() {
		getCurrent().goBack();
	}

	@Override
	public void highlight(String locator) {
		getCurrent().highlight(locator);
	}

	@Override
	public void ignoreAttributesWithoutValue(String ignore) {
		getCurrent().ignoreAttributesWithoutValue(ignore);
	}

	@Override
	public boolean isAlertPresent() {
		return getCurrent().isAlertPresent();
	}

	@Override
	public boolean isChecked(String locator) {
		return getCurrent().isChecked(locator);
	}

	@Override
	public boolean isConfirmationPresent() {
		return getCurrent().isConfirmationPresent();
	}

	@Override
	public boolean isCookiePresent(String name) {
		return getCurrent().isCookiePresent(name);
	}

	@Override
	public boolean isEditable(String locator) {
		return getCurrent().isEditable(locator);
	}

	@Override
	public boolean isElementPresent(String locator) {
		return getCurrent().isElementPresent(locator);
	}

	@Override
	public boolean isOrdered(String locator1, String locator2) {
		return getCurrent().isOrdered(locator1, locator2);
	}

	@Override
	public boolean isPromptPresent() {
		return getCurrent().isPromptPresent();
	}

	@Override
	public boolean isSomethingSelected(String selectLocator) {
		return getCurrent().isSomethingSelected(selectLocator);
	}

	@Override
	public boolean isTextPresent(String pattern) {
		return getCurrent().isTextPresent(pattern);
	}

	@Override
	public boolean isVisible(String locator) {
		return getCurrent().isVisible(locator);
	}

	@Override
	public void keyDown(String locator, String keySequence) {
		getCurrent().keyDown(locator, keySequence);
	}

	@Override
	public void keyDownNative(String keycode) {
		getCurrent().keyDownNative(keycode);
	}

	@Override
	public void keyPress(String locator, String keySequence) {
		getCurrent().keyPress(locator, keySequence);
	}

	@Override
	public void keyPressNative(String keycode) {
		getCurrent().keyPressNative(keycode);
	}

	@Override
	public void keyUp(String locator, String keySequence) {
		getCurrent().keyUp(locator, keySequence);
	}

	@Override
	public void keyUpNative(String keycode) {
		getCurrent().keyUpNative(keycode);
	}

	@Override
	public void metaKeyDown() {
		getCurrent().metaKeyDown();
	}

	@Override
	public void metaKeyUp() {
		getCurrent().metaKeyUp();
	}

	@Override
	public void mouseDown(String locator) {
		getCurrent().mouseDown(locator);
	}

	@Override
	public void mouseDownAt(String locator, String coordString) {
		getCurrent().mouseDownAt(locator, coordString);
	}

	@Override
	public void mouseDownRight(String locator) {
		getCurrent().mouseDownRight(locator);
	}

	@Override
	public void mouseDownRightAt(String locator, String coordString) {
		getCurrent().mouseDownRightAt(locator, coordString);		
	}

	@Override
	public void mouseMove(String locator) {
		getCurrent().mouseMove(locator);
	}

	@Override
	public void mouseMoveAt(String locator, String coordString) {
		getCurrent().mouseMoveAt(locator, coordString);
	}

	@Override
	public void mouseOut(String locator) {
		getCurrent().mouseOut(locator);
	}

	@Override
	public void mouseOver(String locator) {
		getCurrent().mouseOver(locator);
	}

	@Override
	public void mouseUp(String locator) {
		getCurrent().mouseUp(locator);
	}

	@Override
	public void mouseUpAt(String locator, String coordString) {
		getCurrent().mouseUpAt(locator, coordString);
	}

	@Override
	public void mouseUpRight(String locator) {
		getCurrent().mouseUpRight(locator);
	}

	@Override
	public void mouseUpRightAt(String locator, String coordString) {
		getCurrent().mouseUpRightAt(locator, coordString);
	}

	@Override
	public void open(String url) {
		getCurrent().open(url);
	}

	@Override
	public void openWindow(String url, String windowID) {
		getCurrent().openWindow(url, windowID);
	}

	@Override
	public void refresh() {
		getCurrent().refresh();
	}

	@Override
	public void removeAllSelections(String locator) {
		getCurrent().removeAllSelections(locator);
	}

	@Override
	public void removeScript(String scriptTagId) {
		getCurrent().removeScript(scriptTagId);
	}

	@Override
	public void removeSelection(String locator, String optionLocator) {
		getCurrent().removeSelection(locator, optionLocator);
	}

	@Override
	public String retrieveLastRemoteControlLogs() {
		return getCurrent().retrieveLastRemoteControlLogs();
	}

	@Override
	public void rollup(String rollupName, String kwargs) {
		getCurrent().rollup(rollupName, kwargs);
	}

	@Override
	public void runScript(String script) {
		getCurrent().runScript(script);
	}

	@Override
	public void select(String selectLocator, String optionLocator) {
		getCurrent().focus(selectLocator);
		getCurrent().select(selectLocator, optionLocator);
		
		// fixed for IE to fire onchange event.
		getCurrent().windowFocus();
	}

	@Override
	public void selectFrame(String locator) {
		getCurrent().selectFrame(locator);
	}

	@Override
	public void selectWindow(String windowID) {
		getCurrent().selectWindow(windowID);
	}

	@Override
	public void setBrowserLogLevel(String logLevel) {
		getCurrent().setBrowserLogLevel(logLevel);
	}

	@Override
	public void setContext(String context) {
		getCurrent().setContext(context);
	}

	@Override
	public void setCursorPosition(String locator, String position) {
		getCurrent().setCursorPosition(locator, position);
	}

	@Override
	public void setExtensionJs(String extensionJs) {
		getCurrent().setExtensionJs(extensionJs);
	}

	@Override
	public void setMouseSpeed(String pixels) {
		getCurrent().setMouseSpeed(pixels);
	}

	@Override
	public void setSpeed(String value) {
		getCurrent().setSpeed(value);
	}

	@Override
	public void setTimeout(String timeout) {
		getCurrent().setTimeout(timeout);
	}

	@Override
	public void shiftKeyDown() {
		getCurrent().shiftKeyDown();
	}

	@Override
	public void shiftKeyUp() {
		getCurrent().shiftKeyUp();
	}

	@Override
	public void showContextualBanner() {
		getCurrent().showContextualBanner();
	}

	@Override
	public void showContextualBanner(String className, String methodName) {
		getCurrent().showContextualBanner(className, methodName);
	}

	@Override
	public void shutDownSeleniumServer() {
		getCurrent().shutDownSeleniumServer();
	}

	@Override
	public void start() {
		getCurrent().start();
	}

	@Override
	public void start(String optionsString) {
		getCurrent().start(optionsString);
	}

	@Override
	public void start(Object optionsObject) {
		getCurrent().start(optionsObject);
	}

	@Override
	public void submit(String formLocator) {
		getCurrent().submit(formLocator);
	}

	@Override
	public void type(String locator, String value) {
		getCurrent().type(locator, value);		
	}

	@Override
	public void typeKeys(String locator, String value) {
		getCurrent().typeKeys(locator, value);
	}

	@Override
	public void uncheck(String locator) {
		getCurrent().uncheck(locator);
	}

	@Override
	public void useXpathLibrary(String libraryName) {
		getCurrent().useXpathLibrary(libraryName);		
	}

	@Override
	public void waitForCondition(String script, String timeout) {
		getCurrent().waitForCondition(script, timeout);		
	}

	@Override
	public void waitForFrameToLoad(String frameAddress, String timeout) {
		getCurrent().waitForFrameToLoad(frameAddress, timeout);
	}

	@Override
	public void waitForPageToLoad(String timeout) {
		getCurrent().waitForPageToLoad(timeout);
	}

	@Override
	public void waitForPopUp(String windowID, String timeout) {
		getCurrent().waitForPopUp(windowID, timeout);
	}

	@Override
	public void windowFocus() {
		getCurrent().windowFocus();
	}

	@Override
	public void windowMaximize() {
		getCurrent().windowMaximize();
	}

	@Override
	public void stop() {
		selenium.stop();
		_selenium.remove();	
	}

	@Override
	public void addCustomRequestHeader(String arg0, String arg1) {
		getCurrent().addCustomRequestHeader(arg0, arg1);
	}

	@Override
	public String captureNetworkTraffic(String arg0) {
		return getCurrent().captureNetworkTraffic(arg0);
	}

	@Override
	public void deselectPopUp() {
		getCurrent().deselectPopUp();
	}

	@Override
	public void selectPopUp(String arg0) {
		getCurrent().selectPopUp(arg0);
	}

	@Override
	public void verifyEquals(Object obj1, Object obj2){
		if(obj1 == obj2)return;
		
		if( obj1!=null && obj2!=null && (obj1 instanceof Number) && (obj2 instanceof Number)){
			super.verifyEquals(""+obj1, ""+obj2);
		} else super.verifyEquals(obj1, obj2);
	}
	
	public void verifyContains(String word1,String contains){
    	String msg = "["+word1+"] didn't contains string ["+contains+"]";
    	super.verifyContains(msg, word1, contains);
	}
	
	public void verifyNotContains(String word1,String contains){
    	String msg = "["+word1+"] didn't contains string ["+contains+"]";
    	super.verifyNotContains(msg, word1, contains);
	}

    /**
     * Causes the currently executing thread to sleep for the specified number
     * of milliseconds, subject to the precision and accuracy of system timers
     * and schedulers. The thread does not lose ownership of any monitors.
     * @param millis the length of time to sleep in milliseconds.
     */
	protected void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}
	
	/**
	 * 
	 * @param comparator an image comparator.
	 */
	public void verifyImage(Comparator comparator) {
		ZKSelenium zkSelenium = (ZKSelenium) getCurrent();
        String browserName = zkSelenium.getBrowserName();
        ConfigHelper configHelper = ConfigHelper.getInstance();
        String resultDirStr = configHelper.getImageDest() + File.separator +  format.format(new java.util.Date());
        String baseDirStr = configHelper.getImageSrc();
        
        if (resultDirStr == null || resultDirStr.isEmpty() ||
            baseDirStr == null || baseDirStr.isEmpty()) {
            verifyTrue("Incorrect setting of images' outputing path. Please check config.properties.", false);
            return;
        }
        
        try {
            File resultDir = new File(resultDirStr);
            File baseDir = new File(baseDirStr);
            
            if (!baseDir.exists()) {
                baseDir.mkdir();
            }
            
            if (!resultDir.exists()) {
                resultDir.mkdir();
            }

            String title = this.getEval("document.title");
            byte[] imgByteArr = Base64.decode(((TakesScreenshot)((ZKSelenium)getCurrent()).getWrappedDriver()).getScreenshotAs(OutputType.BASE64));
            BufferedImage testBuffImg = ImageIO.read(new ByteArrayInputStream(imgByteArr));
            
            final String postfix = "_" + recordCount++ + ".png";
            if (configHelper.isComparable()) {
            	File basef = new File(baseDir + File.separator + caseID, caseID + "_" + browserName + postfix);
            	if (!basef.isFile()) {
            		super.assertTrue("The image source is not found. - " + basef, false);
            		return;
            	}
                BufferedImage baseBuffImg = ImageIO.read(basef);
                if (baseBuffImg.getWidth() != testBuffImg.getWidth() || baseBuffImg.getHeight() != testBuffImg.getHeight()) {
                	File subDir = new File(resultDirStr + File.separator + caseID);
                	if (!subDir.isDirectory())
                		subDir.mkdir();
                	subDir = new File(subDir, caseID + "_" + browserName + "_result" + postfix);
                	ImageIO.write(testBuffImg, "png", subDir);
                	super.verifyTrue("The size of images are not the same. Please check result. - " + subDir, false);
                	return;
                }

                Comparator ic = comparator == null ? new DefaultComparator(baseBuffImg.getWidth()/configHelper.getGranularity(),
                		baseBuffImg.getHeight()/configHelper.getGranularity(), configHelper.getLeniency()):
                    						comparator;
                BufferedImage imgc = ic.compare(baseBuffImg, testBuffImg);
                if (imgc != null) {
                	File subDir = new File(resultDirStr + File.separator + caseID);
                	if (!subDir.isDirectory())
                		subDir.mkdir();
                	subDir = new File(subDir, caseID + "_" + browserName + "_result" + postfix);
                	ImageIO.write(imgc, "png", subDir);
                    super.verifyTrue("Images are mismatch. Please check result. - " + subDir, false);
                } else {
                	File f = new File(resultDirStr + File.separator + caseID + File.separator + caseID + "_" + browserName + "_result" + postfix);
                	if (f.isFile()) {
                		f.delete();
                	}
                }
            } else {
            	File subDir = new File(baseDir, caseID);
            	if (!subDir.isDirectory())
            		subDir.mkdir();
                ImageIO.write(testBuffImg, "png", new File(subDir, caseID + "_" + browserName + postfix));
            }
        } catch (Exception e) {
        	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        	PrintStream ps = new PrintStream(baos);
        	e.printStackTrace(ps);
            super.fail(baos.toString());
        }
	}
	/**
	 * Compares the snapshot of the testing result.
	 * It is decided by the config.properties <i>comparable</i>.
	 * <p> If true, it will load base image from the specified path, and compare
	 * the current screen shot of the testing result. Otherwise, it just captures
	 * the current screen shot and put into the base image path.
	 * <p> The default comparator is to use {@link Defaultcomparator}. You can
	 * also use {@link #verifyImage(Comparator)} to specify your own comparator.
	 * @see DefaultComparator
	 */
	public void verifyImage() {
		verifyImage(null);
	}

	@Override
	public Number getCssCount(String arg0) {
		// TODO Auto-generated method stub
		return getCurrent().getCssCount(arg0);
	}

	@Override
	public String getLog() {
		// TODO Auto-generated method stub
		return getCurrent().getLog();
	}

	@Override
	public void open(String arg0, String arg1) {
		// TODO Auto-generated method stub
		getCurrent().open(arg0, arg1);
	}
}
