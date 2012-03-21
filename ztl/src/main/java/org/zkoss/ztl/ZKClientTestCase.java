/* ZKClientTestCase.java

{{IS_NOTE
	Purpose:

	Description:

	History:
		Dec 4, 2009 9:50:12 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.ztl;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.zkoss.ztl.util.ConfigHelper;
import org.zkoss.ztl.util.Scripts;
import org.zkoss.ztl.util.image.Comparator;

import com.thoughtworks.selenium.SeleniumException;

/**
 * A skeleton of ZK client widget.
 * @author jumperchen
 *
 */
public class ZKClientTestCase extends ZKTestCase {
	protected int _timeout;

	/**
	 * Waits for Ajax response. (excluding animation check)
	 * <p>By default the timeout time is specified in config.properties
	 * @see #waitResponse(int)
	 */
	protected void waitResponse() {
		waitResponse(_timeout);
	}

	/**
	 * Waits for Ajax response.
	 * <p>By default the timeout time is specified in config.properties
	 * @param includingAnimation if true, it will include animation check.
	 * @see #waitResponse(int, boolean)
	 * @since 2.0.0
	 */
	protected void waitResponse(boolean includingAnimation) {
		waitResponse(_timeout, includingAnimation);
	}
	
	/**
	 * Verifies the image before response has done. 
	 */
	@Override
	public void verifyImage(Comparator comparator) {
		waitResponse();
		super.verifyImage(comparator);
	}

	/**
	 * Waits for Ajax response according to the timeout attribute.
	 * @param timeout
	 * @param includingAnimation if true, it will include animation check.
	 * @since 2.0.0
	 * 
	 */
	protected void waitResponse(int timeout, boolean includingAnimation) {
		long s = System.currentTimeMillis();
		int i = 0;
		int ms = Integer.parseInt(getSpeed());
		if (isIE())
			ms /= 2;
		
		String scripts = includingAnimation ? "!!zAu.processing() || !!jq.timers.length"
								: "!!zAu.processing()";
		while (i < 2) { // make sure the command is triggered.
			while(Boolean.valueOf(this.getEval(scripts))) {
				if (System.currentTimeMillis() - s > timeout) {
					assertTrue("Test case timeout!", false);
					break;
				}
				i = 0;//reset
				sleep(ms);
			}
			i++;
			sleep(ms);
		}
	}
	/**
	 * Waits for Ajax response according to the timeout attribute.(excluding animation check)
	 * @param timeout the time. (millisecond).
	 * @see #waitResponse(int, boolean)
	 */
	protected void waitResponse(int timeout) {
		waitResponse(timeout, false);
	}

	/**
	 * Remove all of the children of the given widget.(internal use only)
	 */
	protected void removeChildren(Widget w) {
		Widget f = w.firstChild();
		while (f.exists()) {
			f.detach();
		}
	}

	protected void runZscript(String zscript) {
		getEval("zAu.send(new zk.Event(null, 'onZTLService', '"+ zscript + "', 10))");
	}

	/**
	 * Verify image after waitResponse(). (including animation time)
	 * @since 2.0.0
	 */
	public void verifyImage() {
		waitResponse(true);
		super.verifyImage();
	}
	
	/**
	 * Returns the Widget object of the UUID.
	 * @param number the number of the widget ID.
	 * @see #uuid(int)
	 * @see #widget(String)
	 */
	protected Widget widget(int number) {
		return widget(uuid(number));
	}

	/**
	 * Returns the Widget object of the UUID.
	 * @param uuid the widget ID.
	 */
	protected Widget widget(String uuid) {
		return new Widget(uuid);
	}

	/**
	 * Returns the Widget object from the JQuery object.
	 * @param jQuery the JQuery object.
	 */
	protected Widget widget(JQuery jQuery) {
		return new Widget(jQuery);
	}

	/**
	 * Returns the Jquery object of the selector
	 * <p> Default: without "#" sign
	 * @param selector the selector
	 */
	protected JQuery jq(String selector) {
		return new JQuery(selector);
	}

	/**
	 * Returns the Jquery object of the ZKClientObject.
	 * @param el the ZKClientObject
	 */
	protected JQuery jq(ClientWidget el) {
		return new JQuery(el);
	}

	/**
	 * Returns the ZK object of the ZKClientObject.
	 * @param el the ZKClientObject
	 */
	protected ZK zk(ClientWidget el) {
		return new ZK(el);
	}

	/**
	 * Returns the ZK object of the selector
	 * @param selector the selector of the element
	 */
	protected ZK zk(String selector) {
		return new ZK(selector);
	}
	/**
	 * Returns the int value from the given string number.
	 * @param number the string number, if null or empty, 0 is assumed.
	 */
	public static int parseInt(String number) {
		if (number != null) {
			number = number.replaceAll("[^-0-9\\.]", "");
			int decimal = number.indexOf('.');
			if (decimal > 0)
				number = number.substring(0, decimal);

			if(!"".equals(number.trim())){
				return Integer.parseInt(number);
			}else{
				return 0;
			}
		}
		return 0;
	}
	public void addSelection(ClientWidget locator, String optionLocator) {
		super.addSelection(locator.toLocator(), optionLocator);
	}

	public void assignId(ClientWidget locator, String identifier) {
		super.assignId(locator.toLocator(), identifier);
	}

	public void check(ClientWidget locator) {
		super.check(locator.toLocator());
	}

	public void click(ClientWidget locator) {
		if (isOpera() || !"tr".equalsIgnoreCase(locator.toElement().get("tagName")))
			super.click(locator.toLocator());
		else {			
			// bug B30-1575048.ztl and B30-1813055.ztl
			// fixed Selenium 2.3 on Firefox driver issue
			if (isFirefox()) {
				Scripts.triggerMouseEventAt(getWebDriver(), locator, "mouseover", "2,2");
				Scripts.triggerMouseEventAt(getWebDriver(), locator, "click", "2,2");
			} else {
				// bug B30-1575048.ztl, B30-1813055.ztl and B30-1769047.ztl for listitem
				getActions().moveToElement(findElement(locator), 2,2).click().perform();
			}
		}
	}
	
	/**
	 * Close the errorbox for webdriver
	 * @since 2.0.0
	 */
	public void closeErrorBox() {
		// fixed bug for B50-2916148.ztl
		JQuery jq = jq(".z-errbox-close");
		int x = jq.width() - 3;
		x += parseInt(jq.css("padding-right"));
		
		if (isOpera() || ZK.is("ff > 10"))
			Scripts.triggerMouseEventAt(getWebDriver(), jq, "click", x + ",3");
		else {
			WebElement element = findElement(jq);
			getActions().moveToElement(element, x, 3).click().perform();

			// double check again (clicking without padding-right)
			// sometimes on 64 bit OS will need to close again for IE9.
			if (jq.exists()) {
				x -= parseInt(jq.css("padding-right"));
				getActions().moveToElement(element, x, 3).click().perform();
			}
		}
	}

	public void clickAt(ClientWidget locator, String coordString) {
		if (isHtmlUnit()) {
			super.click(locator.toLocator());
			return;
		}
		try {
			super.clickAt(locator.toLocator(), coordString);
		} catch (SeleniumException e) {
			// Opera seems not to support clickAt() fixed for B30-2562880.ztl
			// Firefox3 for B30-1903399.ztl
			try {
				Scripts.triggerMouseEventAt(getWebDriver(), locator, "mousedown", coordString);
				Scripts.triggerMouseEventAt(getWebDriver(), locator, "mouseup", coordString);
				Scripts.triggerMouseEventAt(getWebDriver(), locator, "click", coordString);
			} catch (SeleniumException ee){
				throw e;
			}
		}
	}

	public void contextMenu(ClientWidget locator) {
		Scripts.triggerMouseEventAt(getWebDriver(), locator, "contextmenu", "5,5");
	}

	public void contextMenuAt(ClientWidget locator, String coordString) {
		super.contextMenuAt(locator.toLocator(), coordString);
	}

	public void doubleClick(ClientWidget locator) {
		// don't use doubleClick(), because it fails in IE
		doubleClickAt(locator, "1,1");
	}

	public void doubleClickAt(ClientWidget locator, String coordString) {
		// fixed for webdriver
		//super.doubleClickAt(locator.toLocator(), coordString);
		Scripts.triggerMouseEventAt(getWebDriver(), locator, "dblclick", coordString);
	}

	public void dragAndDrop(ClientWidget locator, String movementsString) {
		super.dragAndDrop(locator.toLocator(), movementsString);
	}

	public void dragdropTo(ClientWidget locatorOfObjectToBeDragged, String from, String to) {
		if (ZK.is("ie9")) {
			String[] froms = from.split(",");
			String[] tos = to.split(",");
			int x0 = (int) Double.parseDouble(froms[0]);
			int y0 = (int) Double.parseDouble(froms[1]);
			int x1 = (int) Double.parseDouble(tos[0]);
			int y1 = (int) Double.parseDouble(tos[1]);
			WebElement element = findElement(locatorOfObjectToBeDragged);
			getActions().moveToElement(element, x0, y0).clickAndHold(element)
				.moveByOffset(x1-x0, y1-y0).release(null).perform();
		} else {
			// fixed for Selenium 2.5.0 issue
			Scripts.triggerMouseEventAt(getWebDriver(), locatorOfObjectToBeDragged, "mousemove", to);
			Scripts.triggerMouseEventAt(getWebDriver(), locatorOfObjectToBeDragged, "mousedown", from);
			if (isChrome())
				sleep(Integer.parseInt(ConfigHelper.getInstance().getDelay()));
			Scripts.triggerMouseEventAt(getWebDriver(), locatorOfObjectToBeDragged, "mousemove", to);
			Scripts.triggerMouseEventAt(getWebDriver(), locatorOfObjectToBeDragged, "mouseup", to);
		}
	}

	public void dragdropToObject(ClientWidget locatorOfObjectToBeDragged,
			ClientWidget locatorOfDragDestinationObject, String from, String to) {
			
		// fixed for Selenium 2.5.0
		//super.dragdropToObject(locatorOfObjectToBeDragged.toLocator(),
		//	locatorOfDragDestinationObject.toLocator(), from, to);
		mouseMoveAt(locatorOfObjectToBeDragged, from);
		mouseDownAt(locatorOfObjectToBeDragged, from);
		mouseMoveAt(locatorOfDragDestinationObject, to);
		mouseUpAt(locatorOfDragDestinationObject, to);
	}

	public void dragAndDropToObject(ClientWidget locatorOfObjectToBeDragged,
			ClientWidget locatorOfDragDestinationObject) {
		super.dragAndDropToObject(locatorOfObjectToBeDragged.toLocator(), locatorOfDragDestinationObject.toLocator());
	}

	public void dragdrop(ClientWidget locator, String movementsString) {
		super.dragdrop(locator.toLocator(), movementsString);
	}

	public void fireEvent(ClientWidget locator, String eventName) {
		super.fireEvent(locator.toLocator(), eventName);
	}

	/**
	 * @param locator
	 */
	public void focus(ClientWidget locator) {
		// fixed for IE9 on Webdriver
		//TODO : Click scrolls page !!!
		if (isIE())
			click(locator);
		else
			super.focus(locator.toLocator());
	}
	/**
	 * @param locator
	 */
	public void blur(ClientWidget locator) {
		// fixed for IE9 on Webdriver
		// very tricky way to fire the blur event. In this case we cannot send Keys.Tab,
		// because it may affect the scrollbar to move.
		if (ZK.is("ie"))
			Scripts.triggerMouseEventAt(getWebDriver(), locator, "blur", "2,2");
		else
			super.fireEvent(locator.toLocator(), "blur");
	}

	public String getAttribute(ClientWidget attributeLocator) {
		return super.getAttribute(attributeLocator.toLocator());
	}

	public Number getCursorPosition(ClientWidget locator) {
		return super.getCursorPosition(locator.toLocator());
	}

	public Number getElementHeight(ClientWidget locator) {
		return super.getElementHeight(locator.toLocator());
	}

	public Number getElementIndex(ClientWidget locator) {
		return super.getElementIndex(locator.toLocator());
	}

	public Number getElementPositionLeft(ClientWidget locator) {
		return super.getElementPositionLeft(locator.toLocator());
	}

	public Number getElementPositionTop(ClientWidget locator) {
		return super.getElementPositionTop(locator.toLocator());
	}

	public Number getElementWidth(ClientWidget locator) {
		return super.getElementWidth(locator.toLocator());
	}

	public String[] getSelectOptions(ClientWidget selectLocator) {
		return super.getSelectOptions(selectLocator.toLocator());
	}

	public String getSelectedId(ClientWidget selectLocator) {
		return super.getSelectedId(selectLocator.toLocator());
	}

	public String[] getSelectedIds(ClientWidget selectLocator) {
		return super.getSelectedIds(selectLocator.toLocator());
	}

	public String getSelectedIndex(ClientWidget selectLocator) {
		return getCurrent().getSelectedIndex(selectLocator.toLocator());
	}

	public String[] getSelectedIndexes(ClientWidget selectLocator) {
		return super.getSelectedIndexes(selectLocator.toLocator());
	}

	public String getSelectedLabel(ClientWidget selectLocator) {
		return super.getSelectedLabel(selectLocator.toLocator());
	}

	public String[] getSelectedLabels(ClientWidget selectLocator) {
		return super.getSelectedLabels(selectLocator.toLocator());
	}

	public String getSelectedValue(ClientWidget selectLocator) {
		return super.getSelectedValue(selectLocator.toLocator());
	}

	public String[] getSelectedValues(ClientWidget selectLocator) {
		return super.getSelectedValues(selectLocator.toLocator());
	}


	public String getTable(ClientWidget tableCellAddress) {
		return super.getTable(tableCellAddress.toLocator());
	}

	public String getText(ClientWidget locator) {
		return super.getText(locator.toLocator());
	}

	public String getValue(ClientWidget locator) {
		return super.getValue(locator.toLocator());
	}

	public void highlight(ClientWidget locator) {
		super.highlight(locator.toLocator());
	}

	public boolean isChecked(ClientWidget locator) {
		return super.isChecked(locator.toLocator());
	}

	public boolean isEditable(ClientWidget locator) {
		return super.isEditable(locator.toLocator());
	}

	public boolean isElementPresent(ClientWidget locator) {
		return super.isElementPresent(locator.toLocator());
	}

	public boolean isOrdered(ClientWidget locator1, ClientWidget locator2) {
		return super.isOrdered(locator1.toLocator(), locator2.toLocator());
	}

	public boolean isSomethingSelected(ClientWidget selectLocator) {
		return super.isSomethingSelected(selectLocator.toLocator());
	}

	@Override
	public boolean isTextPresent(String pattern) {
		return super.isTextPresent(pattern);
	}

	public boolean isVisible(ClientWidget locator) {
		return super.isVisible(locator.toLocator());
	}

	public void keyDown(ClientWidget locator, String keySequence) {
		super.keyDown(locator.toLocator(), keySequence);
	}
	
	/**
	 * 2010/10/29 TonyQ:note: when typing number in chrome , it failed 
	 * @param locator
	 * @param keySequence
	 */
	public void keyPress(ClientWidget locator, String keySequence) {
		super.keyPress(locator.toLocator(), keySequence);
	}

	/**
	 * Use this method to simulate typing into an element, which may set its value.
	 * @param by The locating mechanism to use
	 * @param keysToSend
	 * @since 2.0.0
	 */
	public void sendKeys(By by, CharSequence... keysToSend) {
		// fixed firefox Keys.ENTER is 14, not 13
		if (isFirefox()) {
			// fixed B30-1943783.ztl
			if (keysToSend.length == 1 && keysToSend[0] == Keys.ENTER) {
				try {
					Scripts.callEmbeddedSelenium(getWebDriver(), "triggerKeyEvent", by, "keydown", 13);
					Scripts.callEmbeddedSelenium(getWebDriver(), "triggerKeyEvent", by, "keyup", 13);
				} catch (SeleniumException e) {}
				return;
			}
			for (int i = 0; i < keysToSend.length; i++)
				if (keysToSend[i] == Keys.ENTER)
					keysToSend[i] = Keys.RETURN;
		}
		getWebDriver().findElement(by).sendKeys(keysToSend);
	}
	/**
	 * <pre>
	 * 2010/10/27 TonyQ:
	 * because there exist a lot of problem to press enter for ENTER key ,
	 * so we build the method for it.
	 *
	 * <b>NOTICE:</b>Because we use the keyPressNative , so you need to
	 * 	let computer focus on browser when you run the test case  which use this method,
	 *  or the native key press will NOT work anyway.
	 *
	 * If you want modify this ,please make sure that browser compatibility is ok .
	 * This is a hard method anyway.
	 *
	 * It is a issue for selenium.
	 * 
	 * @note it will trigger onChanging that we unexpect.
	 * </pre>
	 * @browsers firefox,safari402,chrome,ie8,ie7,ie6 .
	 */
	public void keyPressEnter(ClientWidget locator){
		focus(locator);
		keyDown(locator,"13");
	}

	public void keyUp(ClientWidget locator, String keySequence) {
		super.keyUp(locator.toLocator(), keySequence);
	}

	public void mouseDown(ClientWidget locator) {
		super.mouseDown(locator.toLocator());
	}

	public void mouseDownAt(ClientWidget locator, String coordString) {
		if (ZK.is("ie9")) {
			//bug for Form.ztl 
			//Scripts.triggerMouseEventAt(getWebDriver(), locator, "mousedown", coordString);
			String[] froms = coordString.split(",");
			int x0 = Integer.parseInt(froms[0]);
			int y0 = Integer.parseInt(froms[1]);
			WebElement element = findElement(locator);
			getActions().moveToElement(element, x0, y0).clickAndHold(element).perform();
		} else {
			// fixed for Selenium 2.5.0
			// super.mouseDownAt(locator.toLocator(), coordString);
			Scripts.triggerMouseEventAt(getWebDriver(), locator, "mousedown", coordString);
		}
	}

	public void mouseDownRight(ClientWidget locator) {
		super.mouseDownRight(locator.toLocator());
	}

	public void mouseDownRightAt(ClientWidget locator, String coordString) {
		super.mouseDownRightAt(locator.toLocator(), coordString);
	}

	public void mouseMove(ClientWidget locator) {
		super.mouseMove(locator.toLocator());
	}

	public void mouseMoveAt(ClientWidget locator, String coordString) {
		if (ZK.is("ie9")) {
			String[] froms = coordString.split(",");
			int x0 = Integer.parseInt(froms[0]);
			int y0 = Integer.parseInt(froms[1]);
			WebElement element = findElement(locator);
			getActions().moveToElement(element, x0, y0).perform();
		} else {
			// fixed for Selenium 2.5.0
			// super.mouseMoveAt(locator.toLocator(), coordString);
			Scripts.triggerMouseEventAt(getWebDriver(), locator, "mousemove", coordString);
		}
	}

	public void mouseOut(ClientWidget locator) {
		Scripts.triggerMouseEventAt(getWebDriver(), locator, "mouseout", "2,2");
	}

	public void mouseOver(By locator) {
		Scripts.triggerMouseEventAt(getWebDriver(), locator, "mouseover", "2,2");
	}
	
	public void mouseUp(ClientWidget locator) {
		if (ZK.is("ie"))
			Scripts.triggerMouseEventAt(getWebDriver(), locator, "mouseup", "1,1");
		else
			super.mouseUp(locator.toLocator());
	}

	public void mouseUpAt(ClientWidget locator, String coordString) {
		// fixed for Selenium 2.5.0
		Scripts.triggerMouseEventAt(getWebDriver(), locator, "mouseup", coordString);
	}

	public void mouseUpRight(ClientWidget locator) {
		super.mouseUpRight(locator.toLocator());
	}

	public void mouseUpRightAt(ClientWidget locator, String coordString) {
		super.mouseUpRightAt(locator.toLocator(), coordString);
	}

	public void removeAllSelections(ClientWidget locator) {
		super.removeAllSelections(locator.toLocator());
	}

	public void removeSelection(ClientWidget locator, String optionLocator) {
		super.removeSelection(locator.toLocator(), optionLocator);
	}

	public void rollup(ClientWidget rollupName, String kwargs) {
		super.rollup(rollupName.toLocator(), kwargs);
	}

	public void select(ClientWidget selectLocator, String optionLocator) {
		// fixed Opera to fire unnecessary onchange event.
		if (isOpera())
			click(selectLocator);
		// In IE, it will fail on B30-1819318.ztl, we may wait for the latest version
		// of selenium 2.2+ to fix the following API.
		// new Select(findElement(selectLocator)).selectByVisibleText(optionLocator);
		findElement(jq(selectLocator).find("option:contains(\""+ optionLocator + "\")")).click();
		
		// force to fire onChange event for IE
		if (isIE())
			blur(selectLocator);
		else if (isOpera()) // close the dropdown list and fire onchange
			sendKeys(selectLocator, Keys.ENTER);
	}

	public void selectFrame(ClientWidget locator) {
		if (locator instanceof Widget)
			getWebDriver().switchTo().frame(((Widget)locator).uuid());
		else
			getWebDriver().switchTo().frame(findElement(locator));
	}

	public void setContext(ClientWidget context) {
		super.setContext(context.toLocator());
	}

	public void setCursorPosition(ClientWidget locator, String position) {
		super.setCursorPosition(locator.toLocator(), position);
	}

	public void submit(ClientWidget formLocator) {
		super.submit(formLocator.toLocator());
	}

	/**
	 * Types the value to the locator.
	 * <p> The method will call focus() before typing and blur() after typed.
	 */
	public void type(ClientWidget locator, String value) {
		focus(locator);
		super.type(locator.toLocator(), value);
		blur(locator);
	}
	
	/**
	 * a shortcur for getting alert message.
	 * (Take care about the dom class and model will be different 
	 * 	when event-thread is enable/disable , so we use title .)  
	 * @return
	 */
	public String getAlertMessage(){
		return jq("@window[title=\"ZK Test\"] @label").text();
	}
	
	public void clickAlert(){
		click(jq("@window[title=\"ZK Test\"] @button"));
	}

	/**
	 * Types the value to the locator.
	 * <p> The method will call focus() before typing and blur() after typed.
	 */
	public void typeKeys(ClientWidget locator, String value) {
		focus(locator);
		super.typeKeys(locator.toLocator(), value);
		blur(locator);
	}

	public void uncheck(ClientWidget locator) {
		super.uncheck(locator.toLocator());
	}

}
