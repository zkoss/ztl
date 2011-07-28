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

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.seleniumemulation.ElementFinder;
import org.openqa.selenium.internal.seleniumemulation.JavascriptLibrary;
import org.zkoss.ztl.util.ConfigHelper;
import org.zkoss.ztl.util.Scripts;

/**
 * An element finder for ZK's client widget.
 * <p> Note: due to the relative classpath for {@ElementFinder}, we have to put
 * this class under the package of <code>org.openqa.selenium.internal.seleniumemulation</code>
 * @author jumperchen
 */
public class ZKElementFinder extends ElementFinder {
	private static String js = ConfigHelper.getInstance().isDebuggable() ? 
			Scripts.DEBUGGER_FIND_ELEMENT_SCRIPTS : Scripts.FIND_ELEMENT_SCRIPTS;
	public ZKElementFinder(JavascriptLibrary javascriptLibrary) {
		super(javascriptLibrary);
	}
	
	public static WebElement findElementX(WebDriver driver, String locator) {
		Object result = ((JavascriptExecutor) driver).executeScript(
				"return (" + js + ")(arguments[0]);",
				locator.replaceAll("\"", "\\\\\"").replaceAll("'", "\"")); // fixed for Operadriver
		if (result instanceof WebElement)
			return (WebElement) result;
		else
			throw new NoSuchElementException("Element not found: [" + locator + "]");
	}
	
	public WebElement findElement(WebDriver driver, String locator) {
		if (Scripts.isZKScript(locator)) {
			return findElementX(driver, locator);
		}
		return super.findElement(driver, locator);
	}
}
