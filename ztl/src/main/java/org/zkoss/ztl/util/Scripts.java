/* Scripts.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jul 28, 2011 12:59:40 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.ztl.util;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.Resources;
import com.thoughtworks.selenium.webdriven.JavascriptLibrary;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.zkoss.ztl.ZKTestCase;
import org.zkoss.ztl.testcafe.CafeTestStep;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A util class to collect the source of Javascript for Selenium 2.
 * 
 * @author jumperchen
 * @since 2.0.0
 */
public class Scripts {
	public static final String ZTL_DEBUGGER_SCRIPTS = "(function(){var log=function(evt){var w=zk.Widget.$(evt.target);if(evt.target.id!='zk_log'&&w&&w.widgetName!='page'){var md=evt.mouseData(),kd=evt.keyData(),log='';if(kd.shiftKey){log+='shiftKey,';}if(kd.altKey){log+='altKey,';}if(kd.ctrlKey){log+'ctrlKey,';}if(md.pageX!=undefined){log+='x='+md.pageX+',';}if(md.pageY!=undefined){log+='y='+md.pageY+',';}if(log){zk.log(evt.type,evt.target,log);}else{zk.log(evt.type,evt.target);}}};jq(document).keydown(log).keyup(log).keypress(log).bind('zcontextmenu',log).bind('zmousedown',log).bind('zmouseup',log).bind('zmousemove',log).mouseover(log).mouseout(log).click(log).bind('zdblclick',log);})();";

	public static final String FIND_ELEMENT_SCRIPTS = "function(e){e=jq.evalJSON(e);if(e){if(e.length)return e[0];else if(e.$n)return e.$n();return(e);}}";

	public static final String DEBUGGER_FIND_ELEMENT_SCRIPTS = "function(e){e=jq.evalJSON(e);if(e){if(e.length){zk.log(\"jq\",jq.nodeName(e[0]),e[0],e[0].id);return e[0];}else if(e.$n){zk.log(\"zk widget\",e.widgetName,e.$n().id);return e.$n();}zk.log(\"elem\", e);return(e);}}";

	public static final String FIND_CHILD_ELEMENT_SCRIPTS = "function(id,e){e=jq.evalJSON(e);if(e){if(e.length){if(!id){return e[0];}return e.filter(function(){return $(this).parents(\"#\"+id).length>0;})[0];}else if(e.$n){return e.$n();}return(e);}}";
	
	public static final String DEBUGGER_FIND_CHILD_ELEMENT_SCRIPTS = "function(id,e){e=jq.evalJSON(e);if(e){if(e.length){if(!id){zk.log(\"jq\",jq.nodeName(e[0]),e[0],e[0].id);return e[0];}e=e.filter(function(){return $(this).parents(\"#\"+id).length>0;});zk.log(\"jq-child\",jq.nodeName(e[0]),e[0],e[0].id);return e[0];}else if(e.$n){zk.log(\"zk widget\",e.widgetName,e.$n().id);return e.$n();}zk.log(\"elem\", e);return(e);}}";
	public static final JavascriptLibrary JS = new JavascriptLibrary();

	private static final String SELENIUM_PREFIX = "/" + JavascriptLibrary.class.getPackage()
    .getName().replace(".", "/") + "/";
	private static final String PREFIX = "/" + Scripts.class.getPackage()
    .getName().replace(".", "/") + "/";
	
	// Fixed IE9 issue and use our own hack, the bug is related to 
	// http://code.google.com/p/selenium/issues/detail?id=2218
	//private static final String injectableSelenium = readScript(SELENIUM_PREFIX + "injectableSelenium.js");
	private static final String injectableSelenium = readScript("/injectableSelenium.js");
	
	public static final String ZK_FIXED_SCRIPTS = readScript("/zkfixed.js");;

	/**
	 * Returns whether the locator can be recognized via ZK engine.
	 */
	public static boolean isZKScript(String locator) {
		return (locator.indexOf("zk.") == 0 || locator.indexOf("zk(") == 0 || locator
				.indexOf("jq") == 0);
	}

	/**
	 * Calls the embeddedSelenium function.
	 * <p>
	 * This is an enhancement for
	 * {@link JavascriptLibrary#callEmbeddedSelenium(WebDriver, String, WebElement, Object...)}
	 */
	public static Object callEmbeddedSelenium(WebDriver driver,
			String functionName, By by, Object... values) {
		StringBuilder builder = new StringBuilder(injectableSelenium);
		builder.append("return browserbot.").append(functionName)
				.append(".apply(browserbot, arguments);");

		List<Object> args = new ArrayList<Object>();
		args.add(driver.findElement(by));
		args.addAll(Arrays.asList(values));

		return ((JavascriptExecutor) driver).executeScript(builder.toString(),
				args.toArray());
	}
	
	/**
	 * Calls the embeddedSelenium triggerMouseEventAt() function
	 */
	public static void triggerMouseEventAt(WebDriver driver, By by, String eventName, String coordString) {
		Scripts.callEmbeddedSelenium(driver, "triggerMouseEventAt", by, eventName, coordString);
	}
	
	private static String readScript(String script) {
		URL url = Scripts.class.getResource(script);

		if (url == null) {
			throw new RuntimeException("Cannot locate " + script);
		}

		try {
			return Resources.toString(url, Charsets.UTF_8);
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

	public static String getEval(String script) {
		return getEval(script, null , false);
	}

	//internal use
	public static String getEval(String script, List<CafeTestStep> testSteps) {
		return getEval(script, testSteps , false);
	}
	public static String getEval(String script, List<CafeTestStep> testSteps, boolean doRecord) {
		if (testSteps == null) {
			script = ZKTestCase.getCurrent().getEval(script);
		} else {
			if (doRecord)
				testSteps.add(new CafeTestStep(CafeTestStep.EVAL, "await ClientFunction(() => " + script + ")()"));
		}
		return script;
	}

	public static void doCafeEval(String script, List<CafeTestStep> testCodeList) {
		if (!script.trim().startsWith("await ")) {
			script = "await ClientFunction(() => {" + script + "})()";
		}
		testCodeList.add(new CafeTestStep(CafeTestStep.EVAL, script));
	}

	public static String getCafeClientFunction(String script) {
		if (!script.trim().startsWith("await ")) {
			script = "await ClientFunction(() => " + script + ")()";
		}
		return script;
	}

	public static void doCafeScript(String script, List<CafeTestStep> testCodeList) {
		testCodeList.add(new CafeTestStep(CafeTestStep.EVAL, script));
	}
}
