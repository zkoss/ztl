/* ZKGetText.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jul 25, 2011 7:11:22 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
 */
package org.openqa.selenium.internal.seleniumemulation;

import com.thoughtworks.selenium.webdriven.ElementFinder;
import com.thoughtworks.selenium.webdriven.JavascriptLibrary;
import com.thoughtworks.selenium.webdriven.SeleneseCommand;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.zkoss.ztl.util.Scripts;

/**
 * A class to fix OperaDirver issue
 * Ref: Selenium GetText
 * @author jumperchen
 */
public class ZKGetText extends SeleneseCommand<String> {
	private final JavascriptLibrary library;

	private final ElementFinder finder;

	public ZKGetText(JavascriptLibrary library, ElementFinder finder) {
		this.library = library;
		this.finder = finder;
	}

	protected String handleSeleneseCommand(WebDriver driver, String locator,
			String ignored) {
		if (Scripts.isZKScript(locator)) {
			WebElement element = finder.findElement(driver, locator);
			return element.getText();
		}

		String getText = library.getSeleniumScript("getText.js");

	    try {
	      return (String) ((JavascriptExecutor) driver).executeScript(
	          "return (" + getText + ")(arguments[0]);", locator);
	    } catch (WebDriverException e) {
	      // TODO(simon): remove fall back for IE driver
	      // OperaDriver will cause NPE at this line
	      WebElement element = finder.findElement(driver, locator);
	      return element.getText();
	    }
	}
}
