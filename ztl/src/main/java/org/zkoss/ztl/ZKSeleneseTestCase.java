/* ZKSeleneseTestCase.java

{{IS_NOTE
	Purpose:

	Description:

	History:
		Aug 18, 2010 3:39:50 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.ztl;

import java.util.Date;

import junit.framework.TestCase;

import com.thoughtworks.selenium.SeleneseTestBase;
import com.thoughtworks.selenium.Selenium;

/**
 * This is a copy of SeleneseTestCase.java and improve the log mechanism.
 * @author jumperchen
 *
 */
public class ZKSeleneseTestCase extends TestCase {

	// static tag used for debugging parallel test
	protected static long timeUUID = new Date().getTime();
	public long getTimeUUID() {return timeUUID;}
	
    private ZKSeleneseTestBase stb = new ZKSeleneseTestBase();

    /** Use this object to run all of your selenium tests */
    protected ThreadLocal<Selenium> selenium = new ThreadLocal<Selenium>();

    public ZKSeleneseTestCase() {
        super();
    }


    public ZKSeleneseTestCase(String name) {
        super(name);
    }

    /** Asserts that there were no verification errors during the current test, failing immediately if any are found */
    public void checkForVerificationErrors() {
        stb.checkForVerificationErrors();
    }

    /** Clears out the list of verification errors */
    public void clearVerificationErrors() {
        stb.clearVerificationErrors();
    }

    /** Returns the body text of the current page */
    public String getText() {
        return stb.getText();
    }

    /** Sleeps for the specified number of milliseconds */
    public void pause(int millisecs) {
        stb.pause(millisecs);
    }

    /** Calls this.setUp(null)
     * @see #setUp(String)
     */
    public void setUp() throws Exception {
        stb.setUp();
        selenium.set(stb.selenium.get());
    }

    /**
     * Calls this.setUp with the specified url and a default browser.  On Windows, the default browser is *iexplore; otherwise, the default browser is *firefox.
     * @see #setUp(String, String)
     * @param url the baseUrl to use for your Selenium tests
     * @throws Exception
     *
     */
    public void setUp(String url) throws Exception {
        stb.setUp(url);
        selenium.set(stb.selenium.get());
    }

    /**
     * Creates a new DefaultSelenium object and starts it using the specified baseUrl and browser string
     * @param url the baseUrl for your tests
     * @param browserString the browser to use, e.g. *firefox
     * @throws Exception
     */
    public void setUp(String url, String browserString) throws Exception {
        stb.setUp(url, browserString);
        selenium.set(stb.selenium.get());
    }

    /** checks for verification errors and stops the browser */
    public void tearDown() throws Exception {
        stb.tearDown();
    }

    /** Like assertEquals, but fails at the end of the test (during tearDown) */
    public void verifyEquals(boolean arg1, boolean arg2) {
        stb.verifyEquals(arg1, arg2, selenium.get());
    }

    /** Like assertEquals, but fails at the end of the test (during tearDown) */
    public void verifyEquals(String message, boolean arg1, boolean arg2) {
        stb.verifyEquals(message, arg1, arg2, selenium.get());
    }

    /** Like assertEquals, but fails at the end of the test (during tearDown) */
    public void verifyEquals(Object s1, Object s2) {
        stb.verifyEquals(s1, s2, selenium.get());
    }

    
    public void verifyContains(String message,String s1,String s2){
		stb.verifyContains(message, s1, s2, selenium.get());
    }
    
    public void verifyNotContains(String message,String s1,String s2){
		stb.verifyNotContains(message, s1, s2, selenium.get());
    }
    
    /** Like assertEquals, but fails at the end of the test (during tearDown) */
    public void verifyEquals(String message, Object s1, Object s2) {
        stb.verifyEquals(message, s1, s2, selenium.get());
    }

    /** Like assertEquals, but fails at the end of the test (during tearDown) */
    public void verifyEquals(String[] s1, String[] s2) {
        stb.verifyEquals(s1, s2, selenium.get());
    }

    /** Like assertEquals, but fails at the end of the test (during tearDown) */
    public void verifyEquals(String message, String[] s1, String[] s2) {
        stb.verifyEquals(message, s1, s2, selenium.get());
    }

    /** Like assertFalse, but fails at the end of the test (during tearDown) */
    public void verifyFalse(boolean b) {
        stb.verifyFalse("verification should be false", b, selenium.get());
    }
    /** Like assertFalse, but fails at the end of the test (during tearDown) */
    public void verifyFalse(String message, boolean b) {
        stb.verifyFalse(message, b, selenium.get());
    }

    /** Like assertNotEquals, but fails at the end of the test (during tearDown) */
    public void verifyNotEquals(boolean s1, boolean s2) {
        stb.verifyNotEquals(s1, s2, selenium.get());
    }

    /** Like assertNotEquals, but fails at the end of the test (during tearDown) */
    public void verifyNotEquals(String message, boolean s1, boolean s2) {
        stb.verifyNotEquals(message, s1, s2, selenium.get());
    }

    /** Like assertNotEquals, but fails at the end of the test (during tearDown) */
    public void verifyNotEquals(Object s1, Object s2) {
        stb.verifyNotEquals(s1, s2, selenium.get());
    }

    /** Like assertNotEquals, but fails at the end of the test (during tearDown) */
    public void verifyNotEquals(String message, Object s1, Object s2) {
        stb.verifyNotEquals(message, s1, s2, selenium.get());
    }

    /** Like assertTrue, but fails at the end of the test (during tearDown) */
    public void verifyTrue(boolean b) {
        stb.verifyTrue("verification should be true", b, selenium.get());
    }

    /** Like assertTrue, but fails at the end of the test (during tearDown) */
    public void verifyTrue(String message, boolean b) {
        stb.verifyTrue(message, b, selenium.get());
    }

	/**
	 *
	 * Compare number1 and number2 with a tolearant .
	 *
	 * sometimes we will have 1~2px mistake when browser compute the numbers ,
	 * but it's ok in actully , so we add this test method.
	 *
	 * as other verify method , it fails at the end of the test (during tearDown)
	 * @param number1
	 * @param number2
	 * @param tolerant
	 * @author TonyQ
	 */
	public void verifyTolerant(int number1,int number2,int tolerant){
		if(number1 == number2) return;

		if(Math.abs(number1 -  number2) > tolerant){
			verifyEquals(number1,number2);
		}


	}

    /** Like JUnit's Assert.assertEquals, but knows how to compare string arrays */
    public static void assertEquals(Object s1, Object s2) {
        SeleneseTestBase.assertEquals(s1, s2);
    }

    /** Like JUnit's Assert.assertEquals, but handles "regexp:" strings like HTML Selenese */
    public static void assertEquals(String s1, String s2) {
        SeleneseTestBase.assertEquals(s1, s2);
    }

    /** Like JUnit's Assert.assertEquals, but joins the string array with commas, and
     * handles "regexp:" strings like HTML Selenese
     */
    public static void assertEquals(String s1, String[] s2) {
        SeleneseTestBase.assertEquals(s1, s2);
    }

    /** Asserts that two string arrays have identical string contents */
    public static void assertEquals(String[] s1, String[] s2) {
        SeleneseTestBase.assertEquals(s1, s2);
    }

    /** Asserts that two booleans are not the same */
    public static void assertNotEquals(boolean b1, boolean b2) {
        SeleneseTestBase.assertNotEquals(b1, b2);
    }

    /** Asserts that two objects are not the same (compares using .equals()) */
    public static void assertNotEquals(Object obj1, Object obj2) {
        SeleneseTestBase.assertNotEquals(obj1, obj2);
    }

    /** Compares two objects, but handles "regexp:" strings like HTML Selenese
     * @see #seleniumEquals(String, String)
     * @return true if actual matches the expectedPattern, or false otherwise
     */
    public static boolean seleniumEquals(Object expected, Object actual) {
        return SeleneseTestBase.seleniumEquals(expected, actual);
    }

    /** Compares two strings, but handles "regexp:" strings like HTML Selenese
     *
     * @param expectedPattern
     * @param actual
     * @return true if actual matches the expectedPattern, or false otherwise
     */
    public static boolean seleniumEquals(String expected, String actual) {
        return SeleneseTestBase.seleniumEquals(expected, actual);
    }


    /**
     * @deprecated Use {@link #isCaptureScreenShotOnFailure()} instead
     */
    protected boolean isCaptureScreetShotOnFailure() {
        return isCaptureScreenShotOnFailure();
    }


    protected boolean isCaptureScreenShotOnFailure() {
        return stb.isCaptureScreenShotOnFailure();
    }

    protected String runtimeBrowserString() {
        return stb.runtimeBrowserString();
    }

    /**
     * @deprecated Use {@link #setCaptureScreenShotOnFailure(boolean)} instead
     */
    protected void setCaptureScreetShotOnFailure(boolean b) {
        setCaptureScreenShotOnFailure(b);
    }


    protected void setCaptureScreenShotOnFailure(boolean b) {
        stb.setCaptureScreenShotOnFailure(b);
    }

    protected void setTestContext() {
        selenium.get().setContext(this.getClass().getSimpleName() + "." + getName());
    }

    /**
     * Runs the bare test sequence, capturing a screenshot if a test fails
     * @exception Throwable if any exception is thrown
     */
    // @Override
    public void runBare() throws Throwable {
        if (!isCaptureScreenShotOnFailure()) {
            super.runBare();
            return;
        }
        setUp();
        try {
            runTest();
        } catch (Throwable t) {
            if (selenium != null) {
                String filename = getName() + ".png";
                try {
                    selenium.get().captureScreenshot(filename);
                    System.err.println("Saved screenshot " + filename);
                } catch (Exception e) {
                    System.err.println("Couldn't save screenshot " + filename + ": " + e.getMessage());
                    e.printStackTrace();
                }
                throw t;
            }
        }
        finally {
            tearDown();
        }
    }

    public String join(String[] array, char c) {
        return stb.join(array, c);
    }
}
