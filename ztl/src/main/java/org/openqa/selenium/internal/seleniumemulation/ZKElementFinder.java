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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.seleniumemulation.ElementFinder;
import org.openqa.selenium.internal.seleniumemulation.JavascriptLibrary;
import org.zkoss.ztl.util.ConfigHelper;

/**
 * An element finder for ZK's client widget.
 * <p> Note: due to the relative classpath for {@ElementFinder}, we have to put
 * this class under the package of <code>org.openqa.selenium.internal.seleniumemulation</code>
 * @author jumperchen
 */
public class ZKElementFinder extends ElementFinder {
	private static String scripts = "function(e){e=jq.evalJSON(e);if(e){if(e.length)return e[0];else if(e.$n)return e.$n();return(e);}}";
	private static String debugScripts = "function(e){e=jq.evalJSON(e);if(e){if(e.length){zk.log(\"jq\",e[0].id);return e[0];}else if(e.$n){zk.log(\"zk widget\",e.$n().id);return e.$n();}return(e);}}";
	private static String js = ConfigHelper.getInstance().isDebuggable() ? 
										debugScripts : scripts;
	public ZKElementFinder(JavascriptLibrary javascriptLibrary) {
		super(javascriptLibrary);
	}
	
	public static WebElement findElementX(WebDriver driver, String locator) {
		return (WebElement) ((JavascriptExecutor) driver).executeScript(
				"return (" + js + ")(arguments[0]);",
				locator.replaceAll("\"", "\\\\\"").replaceAll("'", "\"")); // fixed for Operadriver
	}
	
	public static boolean isZKElement(String locator) {
		return (locator.indexOf("zk.") == 0 || locator.indexOf("zk(") == 0
				|| locator.indexOf("jq") == 0);
	}
	public WebElement findElement(WebDriver driver, String locator) {
		if (isZKElement(locator)) {
			return findElementX(driver, locator);
		}
		return super.findElement(driver, locator);
	}
}
