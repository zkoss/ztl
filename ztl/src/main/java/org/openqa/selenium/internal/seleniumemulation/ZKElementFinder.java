/* ZKElementFinder.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jul 21, 2011 7:17:05 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.openqa.selenium.internal.seleniumemulation;

import com.thoughtworks.selenium.webdriven.ElementFinder;
import com.thoughtworks.selenium.webdriven.JavascriptLibrary;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.zkoss.ztl.util.ConfigHelper;
import org.zkoss.ztl.util.Scripts;
import org.zkoss.ztl.webdriver.ZKRemoteWebDriver;

/**
 * An element finder for ZK's client widget.
 * <p> Note: due to the relative classpath for selenium ElementFinder, we have to put
 * this class under the package of <code>org.openqa.selenium.internal.seleniumemulation</code>
 * @author jumperchen
 */
public class ZKElementFinder extends ElementFinder {
	private static String _finderJS = ConfigHelper.getInstance().isDebuggable() ? 
			Scripts.DEBUGGER_FIND_ELEMENT_SCRIPTS : Scripts.FIND_ELEMENT_SCRIPTS;
	private static String _childFinderJS = ConfigHelper.getInstance().isDebuggable() ? 
			Scripts.DEBUGGER_FIND_CHILD_ELEMENT_SCRIPTS : Scripts.FIND_CHILD_ELEMENT_SCRIPTS;
	public ZKElementFinder(JavascriptLibrary javascriptLibrary) {
		super(javascriptLibrary);
	}
	
	public static WebElement findChildElementX(WebDriver driver, String id, String locator) {
		try {
			String replaced = (driver instanceof ZKRemoteWebDriver && ((ZKRemoteWebDriver) driver)
							.getCapabilities().getBrowserName().contains("opera")) ?
									"\\\\\\\\\"" : "\\\\\""; 
			Object result = ((JavascriptExecutor) driver).executeScript(
					"return (" + _childFinderJS + ")(arguments[0],arguments[1]);",
					id,	locator.replaceAll("\"", replaced).replaceAll("'", "\"")); // fixed for Operadriver
			if (result instanceof WebElement)
				return (WebElement) result;
			} catch (WebDriverException e) {
			}
			throw new NoSuchElementException("Element not found: [" + locator + "] from [#"+id+"], Driver: [" + ((RemoteWebDriver)driver).getCapabilities() + "]");
	}
	
	public static WebElement findElementX(WebDriver driver, String locator) {
		try {
		// fixed B30-1615919.ztl for OperaDriver
		String replaced = ((driver instanceof ZKRemoteWebDriver && ((ZKRemoteWebDriver) driver)
						.getCapabilities().getBrowserName().contains("opera"))) ?
								"\\\\\\\\\"" : "\\\\\""; 
		Object result = ((JavascriptExecutor) driver).executeScript(
				"return (" + _finderJS + ")(arguments[0]);",
				locator.replaceAll("\"", replaced).replaceAll("'", "\"")); // fixed for Operadriver
		if (result instanceof WebElement)
			return (WebElement) result;
		} catch (WebDriverException e) {
		}
		throw new NoSuchElementException("Element not found: [" + locator + "], Driver: [" + ((RemoteWebDriver)driver).getCapabilities() + "]");
	}
	
	public WebElement findElement(WebDriver driver, String locator) {
		if (Scripts.isZKScript(locator)) {
			return findElementX(driver, locator);
		}
		return super.findElement(driver, locator);
	}
}
