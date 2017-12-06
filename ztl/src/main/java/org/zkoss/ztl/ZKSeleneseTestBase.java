/* ZKSeleneseTestBase.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 18, 2010 3:44:13 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.ztl;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.regex.Pattern;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import junit.framework.AssertionFailedError;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import org.zkoss.ztl.util.ZKSelenium;

/**
 * This is a copy of SeleneseTestCaseBase.java and improve the log mechanism.
 * @author jumperchen
 *
 */
public class ZKSeleneseTestBase {
	 private static final boolean THIS_IS_WINDOWS = File.pathSeparator.equals(";");
	    
	    private boolean captureScreenShotOnFailure = false;
	    
	    /** Use this object to run all of your selenium tests */
	    protected ThreadLocal<Selenium> selenium;
	    
		HashSet browsers = new HashSet();
	    StringBuffer verificationErrors = new StringBuffer();
	    
	    public ZKSeleneseTestBase() {
	        super();
	    }

	    /** Calls this.setUp(null)
		 * @see #setUp(String)
		 */
	    public void setUp() throws Exception {
	        this.setUp(null);
	    }
	    
	    
	    /**
	     * Calls this.setUp with the specified url and a default browser.  On Windows, the default browser is *iexplore; otherwise, the default browser is *firefox.
	     * @see #setUp(String, String)
	     * @param url the baseUrl to use for your Selenium tests
	     * @throws Exception
	     * 
	     */
	    public void setUp(String url) throws Exception {
	        setUp(url, runtimeBrowserString());
	    }

	    protected String runtimeBrowserString() {
	        String defaultBrowser = System.getProperty("selenium.defaultBrowser");
	        if (null != defaultBrowser && defaultBrowser.startsWith("${")) {
	            defaultBrowser = null;
	        }
	        if (defaultBrowser == null) {
	            if(THIS_IS_WINDOWS){
	                defaultBrowser = "*iexplore";
	            } else{
	                 defaultBrowser = "*firefox";
	            }
	        }
	        return defaultBrowser;
	    }

	    /**
	     * Creates a new DefaultSelenium object and starts it using the specified
	     * baseUrl and browser string. The port is selected as follows: if the
	     * server package's RemoteControlConfiguration class is on the classpath,
	     * that class' default port is used. Otherwise, if the "server.port" system
	     * property is specified, that is used - failing that, the default of 4444
	     * is used.
	     *
	     * @see #setUp(String, String, int)
	     * @param url the baseUrl for your tests
	     * @param browserString the browser to use, e.g. *firefox
	     * @throws Exception
	     */
	    public void setUp(String url, String browserString) throws Exception {
	        setUp(url, browserString, getDefaultPort());
	    }
	    
	    private int getDefaultPort() {
	        try {
	            Class c = Class.forName("org.openqa.selenium.server.RemoteControlConfiguration");
	            Method getDefaultPort = c.getMethod("getDefaultPort", new Class[0]);
	            Integer portNumber = (Integer)getDefaultPort.invoke(null, new Object[0]);
	            return portNumber.intValue();
	        } catch (Exception e) {
	            return Integer.getInteger("selenium.port", 4444).intValue();
	        }
	    }

	    public void setUp(String url, String browserString, int port) {
	        if (url == null) {
	            url = "http://localhost:" + port;
	        }
	        Selenium s = new DefaultSelenium("localhost", port, browserString, url);
	        selenium.set(s);
	        s.start();
	    }

	    /** Like assertTrue, but fails at the end of the test (during tearDown) */
	    public void verifyTrue(String message, boolean b, Selenium selenium) {
	        try {
	            assertTrue(message, b);
	        } catch (Error e) {
	            error(throwableToString(e), selenium);
	        }
	    }
	    
	    /** Like assertFalse, but fails at the end of the test (during tearDown) */
	    public void verifyFalse(String message, boolean b, Selenium selenium) {
	        try {
	            assertFalse(message, b);
	        } catch (Error e) {
	        	error(throwableToString(e), selenium);
	        }
	    }
	    
	    /** Returns the body text of the current page */
	    public String getText() {
	        return selenium.get().getEval("this.page().bodyText()");
	    }

	    /** Like assertEquals, but fails at the end of the test (during tearDown) */
	    public void verifyEquals(Object s1, Object s2, Selenium selenium) {
	        try {
	            assertEquals(null, s1, s2);
	        } catch (Error e) {
	        	error(throwableToString(e), selenium);
	        }
	    }

	    /** Like assertEquals, but fails at the end of the test (during tearDown) */
	    public void verifyEquals(String message, Object s1, Object s2, Selenium selenium) {
	        try {
	            assertEquals(message, s1, s2);
	        } catch (Error e) {
	        	error(throwableToString(e), selenium);
	        }

	    }
	    
	    /** Like assertEquals, but fails at the end of the test (during tearDown) */
	    public void verifyContains(String message, String s1, String s2, Selenium selenium) {
	        try {
	
	        	assertFalse(message, s1 == null || s2 == null);
	        	assertNotEquals(message, s1.indexOf(s2), -1);
	        } catch (Error e) {
	        	error(throwableToString(e), selenium);
	        }
	    }
	    
	    /** Like assertNotEquals, but fails at the end of the test (during tearDown) */
	    public void verifyNotContains(String message, String s1, String s2, Selenium selenium) {
	        try {
	
	        	assertFalse(message, s1 == null || s2 == null);
	        	assertEquals(message, s1.indexOf(s2), -1);
	        } catch (Error e) {
	        	error(throwableToString(e), selenium);
	        }
	    }
	    
	    /** Like assertEquals, but fails at the end of the test (during tearDown) */
	    public void verifyEquals(boolean s1, boolean s2, Selenium selenium) {
	        try {
	            assertEquals(null, new Boolean(s1), new Boolean(s2));
	        } catch (Error e) {
	        	error(throwableToString(e), selenium);
	        }
	    }
	    
	    /** Like assertEquals, but fails at the end of the test (during tearDown) */
	    public void verifyEquals(String message, boolean s1, boolean s2, Selenium selenium) {
	        try {
	            assertEquals(message, new Boolean(s1), new Boolean(s2));
	        } catch (Error e) {
	        	error(throwableToString(e), selenium);
	        }
	    }

	    /** Like JUnit's Assert.assertEquals, but knows how to compare string arrays */
	    public static void assertEquals(String message, Object s1, Object s2) {
	        if (s1 == s2) return;
	        if (s1 instanceof String && s2 instanceof String) {
	            assertEquals((String)s1, (String)s2);
	        } else if (s1 instanceof String && s2 instanceof String[]) {
	            assertEquals((String)s1, (String[])s2);
	        } else if (s1 instanceof String && s2 instanceof Number) {
	            assertEquals((String)s1, ((Number)s2).toString());
	        }
	        else {
	            if (s1 instanceof String[] && s2 instanceof String[]) {
	                
	                String[] sa1 = (String[]) s1;
	                String[] sa2 = (String[]) s2;
	                if (sa1.length!=sa2.length) {
	                	if (message != null)
	                		throw new Error(message + "\nExpected " + sa1 + " but saw " + sa2);
	                	else
	                		throw new Error("Expected " + sa1 + " but saw " + sa2);
	                }
	                for (int j = 0; j < sa1.length; j++) {
	                    assertEquals(sa1[j], sa2[j]);
	                }
	            } else {
		            assertEquals(message, String.valueOf(s1), String.valueOf(s2));
	            }
	        }
	    }

	    /** Like JUnit's Assert.assertEquals, but handles "regexp:" strings like HTML Selenese */ 
	    public static void assertEquals(String s1, String s2) {
	        assertTrue("Expected \"" + s1 + "\" but saw \"" + s2 + "\" instead", seleniumEquals(s1, s2));
	    }
	    /** Like JUnit's Assert.assertEquals, but handles "regexp:" strings like HTML Selenese */ 
	    public static void assertEquals(String message, String s1, String s2) {
	        assertTrue(message, seleniumEquals(s1, s2));
	    }
	    
	    /** Like JUnit's Assert.assertEquals, but joins the string array with commas, and 
	     * handles "regexp:" strings like HTML Selenese
	     */
	    public static void assertEquals(String s1, String[] s2) {
	    	assertEquals(s1, join(s2, ','));
	    }
	    
	    /** Compares two strings, but handles "regexp:" strings like HTML Selenese
	     * 
	     * @param expectedPattern
	     * @param actual
	     * @return true if actual matches the expectedPattern, or false otherwise
	     */
	    public static boolean seleniumEquals(String actual, String expectedPattern) {
	        if (actual.startsWith("regexp:") || actual.startsWith("regex:") || actual.startsWith("regexpi:") || actual.startsWith("regexi:")) {
	            // swap 'em
	        	String tmp = actual;
	            actual = expectedPattern;
	            expectedPattern = tmp;
	        }
	        Boolean b;
	        b = handleRegex("regexp:", expectedPattern, actual, 0);
	        if (b != null) { return b.booleanValue(); }
	        b = handleRegex("regex:", expectedPattern, actual, 0);
	        if (b != null) { return b.booleanValue(); }
	        b = handleRegex("regexpi:", expectedPattern, actual, Pattern.CASE_INSENSITIVE);
	        if (b != null) { return b.booleanValue(); }
	        b = handleRegex("regexi:", expectedPattern, actual, Pattern.CASE_INSENSITIVE);
	        if (b != null) { return b.booleanValue(); }
	        
	        if (expectedPattern.startsWith("exact:")) {
	            String expectedExact = expectedPattern.replaceFirst("exact:", "");
	            if (!expectedExact.equals(actual)) {
	                System.out.println("expected " + actual + " to match " + expectedPattern);
	                return false;
	            }
	            return true;
	        }
	        
	        String expectedGlob = expectedPattern.replaceFirst("glob:", "");
	        expectedGlob = expectedGlob.replaceAll("([\\]\\[\\\\{\\}$\\(\\)\\|\\^\\+.])", "\\\\$1");

	        expectedGlob = expectedGlob.replaceAll("\\*", ".*");
	        expectedGlob = expectedGlob.replaceAll("\\?", ".");
	        if (!Pattern.compile(expectedGlob, Pattern.DOTALL).matcher(actual).matches()) {
	            System.out.println("expected \"" + actual + "\" to match glob \"" + expectedPattern + "\" (had transformed the glob into regexp \"" + expectedGlob + "\")");
	            return false;
	        }
	        return true;
	    }

	    private static Boolean handleRegex(String prefix, String expectedPattern, String actual, int flags) {
	        if (expectedPattern.startsWith(prefix)) {
	            String expectedRegEx = expectedPattern.replaceFirst(prefix, ".*") + ".*";
	            Pattern p = Pattern.compile(expectedRegEx, flags);
	            if (!p.matcher(actual).matches()) {
	                System.out.println("expected " + actual + " to match regexp " + expectedPattern);
	                return Boolean.FALSE;                    
	            }
	            return Boolean.TRUE;
	        }
	        return null;
	    }
	    
	    /** Compares two objects, but handles "regexp:" strings like HTML Selenese
	     * @see #seleniumEquals(String, String)
	     * @return true if actual matches the expectedPattern, or false otherwise
	     */
	    public static boolean seleniumEquals(Object expected, Object actual) {
	        if (expected instanceof String && actual instanceof String) {
	            return seleniumEquals((String)actual, (String)expected);
	        }
	        return expected.equals(actual);
	    }
	    
	    /** Asserts that two string arrays have identical string contents */
	    public static void assertEquals(String[] s1, String[] s2) {
	        String comparisonDumpIfNotEqual = verifyEqualsAndReturnComparisonDumpIfNot(s1, s2);
	        if (comparisonDumpIfNotEqual!=null) {
	            throw new AssertionError(comparisonDumpIfNotEqual);
	        }
	    }
	    
	    /** Asserts that two string arrays have identical string contents (fails at the end of the test, during tearDown) */
	    public void verifyEquals(String[] s1, String[] s2, Selenium selenium) {
	        String comparisonDumpIfNotEqual = verifyEqualsAndReturnComparisonDumpIfNot(s1, s2);
	        if (comparisonDumpIfNotEqual!=null) {
	        	error(comparisonDumpIfNotEqual, selenium);
	        }
	    }
	    
	    private static String verifyEqualsAndReturnComparisonDumpIfNot(String[] s1, String[] s2) {
	        boolean misMatch = false;
	        if (s1.length != s2.length) {
	            misMatch = true;
	        }
	        for (int j = 0; j < s1.length; j++) {
	            if (!seleniumEquals(s1[j], s2[j])) {
	                misMatch = true;
	                break;
	            }
	        }
	        if (misMatch) {
	            return "Expected " + stringArrayToString(s1) + " but saw " + stringArrayToString(s2);
	        }
	        return null;
	    }
	    
	    private static String stringArrayToString(String[] sa) {
	        StringBuffer sb = new StringBuffer("{");
	        for (int j = 0; j < sa.length; j++) {
	            sb.append(" ")
	            .append("\"")
	            .append(sa[j])
	            .append("\"");            
	        }
	        sb.append(" }");
	        return sb.toString();
	    }
	    
	    private static String throwableToString(Throwable t) {
	        StringWriter sw = new StringWriter();
	        PrintWriter pw = new PrintWriter(sw);
	        t.printStackTrace(pw);
	        return sw.toString();
	    }
	    
	    public static String join(String[] sa, char c) {
	        StringBuffer sb = new StringBuffer();
	        for (int j = 0; j < sa.length; j++) {
	            sb.append(sa[j]);
	            if (j < sa.length -1) {
	            	sb.append(c);
	            }          
	        }
	        return sb.toString();
	    }

	    /** Like assertNotEquals, but fails at the end of the test (during tearDown) */
	    public void verifyNotEquals(Object s1, Object s2, Selenium selenium) {
	        try {
	            assertNotEquals(s1, s2);
	        } catch (Error e) {
	        	error(throwableToString(e), selenium);
	        }
	    }

	    /** Like assertNotEquals, but fails at the end of the test (during tearDown) */
	    public void verifyNotEquals(String message, Object s1, Object s2, Selenium selenium) {
	        try {
	            assertNotEquals(message, s1, s2);
	        } catch (AssertionFailedError e) {
	        	error(throwableToString(e), selenium);
	        }
	    }
	    
	    /** Like assertNotEquals, but fails at the end of the test (during tearDown) */
	    public void verifyNotEquals(boolean s1, boolean s2, Selenium selenium) {
	        try {
	            assertNotEquals(new Boolean(s1), new Boolean(s2));
	        } catch (AssertionFailedError e) {
	        	error(throwableToString(e), selenium);
	        }
	    }
	    
	    /** Like assertNotEquals, but fails at the end of the test (during tearDown) */
	    public void verifyNotEquals(String message, boolean s1, boolean s2, Selenium selenium) {
	        try {
	            assertNotEquals(message, new Boolean(s1), new Boolean(s2));
	        } catch (AssertionFailedError e) {
	        	error(throwableToString(e), selenium);
	        }
	    }
	    
	    private void error(String s, Selenium selenium) {
	    	if (selenium instanceof ZKSelenium){
	    		
	    		ZKSelenium zselenium = ((ZKSelenium)selenium);
	    		browsers.add("#{{"+zselenium.getBrowserName()+"}}");
	    		WebDriver driver = zselenium.getWrappedDriver();
	    		String brand = driver instanceof RemoteWebDriver ? ((RemoteWebDriver) driver).getCapabilities().toString() : "";
	    		verificationErrors.append("[Browser Brand]: ").append(zselenium.getBrowserName()).append(" "+brand+"\n").append(s);
	    	}else
	    		verificationErrors.append(s);
	    }
	    
	    /** Asserts that two objects are not the same (compares using .equals()) */
	    public static void assertNotEquals(Object obj1, Object obj2) {
	        if (obj1.equals(obj2)) {
	            fail("did not expect values to be equal (" + obj1.toString() + ")");
	        }
	    }
	    
	    /** Asserts that two objects are not the same (compares using .equals()) */
	    public static void assertNotEquals(String message, Object obj1, Object obj2) {
	        if (obj1.equals(obj2)) {
	            fail(message + "\ndid not expect values to be equal (" + obj1.toString() + ")");
	        }
	    }
	    
	    public static void fail(String message) {
	        throw new AssertionError(message);
	    }
	    
	    static public void assertTrue(String message, boolean condition) {
	        if (!condition)
	            fail(message);
	    }
	    
	    static public void assertTrue(boolean condition) {
	        assertTrue(null, condition);
	    }
	    
	    static public void assertFalse(String message, boolean condition) {
	        assertTrue(message, !condition);
	    }
	    
	    static public void assertFalse(boolean condition) {
	        assertTrue(null, !condition);
	    }
	    
	    /** Asserts that two booleans are not the same */
	    public static void assertNotEquals(boolean b1, boolean b2) {
	        assertNotEquals(new Boolean(b1), new Boolean(b2));
	    }
	    
	    /** Sleeps for the specified number of milliseconds */
	    public void pause(int millisecs) {
	        try {
	            Thread.sleep(millisecs);
	        } catch (InterruptedException e) {
	        }
	    }
	    
	    /** Asserts that there were no verification errors during the current test, failing immediately if any are found */
	    public void checkForVerificationErrors() {
	        String verificationErrorString = verificationErrors.toString();
	        String browserMsg = browsers.toString() ;
	        clearVerificationErrors();
	        browsers.clear();
	        if (!"".equals(verificationErrorString)) {
	            fail(verificationErrorString+"\n==Failed browser:"+browserMsg);
	        }
	    }

	    /** Clears out the list of verification errors */
	    public void clearVerificationErrors() {
	        verificationErrors = new StringBuffer();
	    }
	    
	    /** checks for verification errors and stops the browser */
	    public void tearDown() throws Exception {
	    	try {
	    		checkForVerificationErrors();
	    	} finally {
	    	    if (selenium != null) {
	    	        selenium.get().stop();
	    	        selenium = null;
	    	    }
	    	}
	    }

	    protected boolean isCaptureScreenShotOnFailure() {
	        return captureScreenShotOnFailure;
	    }

	    protected void setCaptureScreenShotOnFailure(boolean captureScreetShotOnFailure) {
	        this.captureScreenShotOnFailure = captureScreetShotOnFailure;
	    }
}
