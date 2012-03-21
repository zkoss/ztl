package org.zkoss.ztl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.zkoss.ztl.util.ConfigHelper;
import org.zkoss.ztl.util.ui.Tree;

import com.thoughtworks.selenium.Selenium;

/**
 * Extend this class for integration testing. For component testing please use
 * extension from ZKClientTestCase
 * 
 * @author sbelei
 *
 */
public class ZKFunctionalTestCase extends ZKClientTestCase {
	private int clickTimeOut;
	private int waitTimeout;
	private String screenShotsDir;
	
	/**
	 * Set target URL
	 * @param tgt
	 */
	public void setTarget(String tgt){
		target = tgt;
	}
	
	/**
	 * Create list of selenium brousers by string:
	 * iexplore - Internet Explorer
	 * opera - Opera
	 * firefox - Mozilla Firefox
	 * chrome - Google Chrome
	 * @param browsers
	 * @return
	 */
	public List<Selenium> makeListOfBrowsers(String browsers) {
		return super.getBrowsers(browsers);
	}
	
	/**
	 * Sets the list of Selenium browsers to test.
	 * You may generate this list by {@linkplain ZKFunctionalTestCase#makeListOfBrowsers(String)}}
	 * @param brwsrs
	 */
	public void setBrowsers(List<Selenium> brwsrs){
		browsers = brwsrs;
	}
	
	/**
	 * Get list of Selenium browsers to test.
	 * @return
	 */
	public List<Selenium> getBrowsers(){
		return browsers;
	}
	
	/**
	 * Start test with selected browser
	 */
	public void start(Selenium selenium) {
		super.start(selenium);
	}
	/**
	 * This class used for element location with timeout.
	 * It's throws exception if element found.
	 * 
	 * @author sbelei
	 */
	private class ErrorIfElementFound implements ExpectedCondition {
		 
		By findCondition;
		String errMsg;
	 
		ErrorIfElementFound(By by, String msg) {
			this.findCondition = by;
			this.errMsg = msg;
		}
	 
		@Override
		public Object apply(Object input) {		
			getWebDriver().findElement(this.findCondition);
			throw new AssertionError(errMsg);
		}
	}
	
	/**
	 * This class used for element location with timeout.
	 * It's throws exception if element found. 
	 * 
	 * @author sbelei
	 */
	@SuppressWarnings("rawtypes")
	private class ErrorIfElementNotFound implements ExpectedCondition {
		 
		By findCondition;
		String errMsg;
	 
		ErrorIfElementNotFound(By by, String msg) {
			this.findCondition = by;
			this.errMsg = msg;
		}
	 
		@Override
		public Object apply(Object input) {
			if (getWebDriver().findElement(this.findCondition) == null) {
				throw new AssertionError(errMsg);
			}
			return true;
		}
	}
	
	/**
	 * Returns timeout for waiting of new screen after click.
	 * @return timeout (in ms)
	 * @author sbelei
	 */
	public int getClickTimeOut() {
		return clickTimeOut;
	}

	/**
	 * Sets timeout for waiting of new screen after click.
	 * @param clickTimeOut timeout (in ms)
	 * @author sbelei
	 */	
	public void setClickTimeOut(int clickTimeOut) {
		this.clickTimeOut = clickTimeOut;
	}

	/**
	 * Returns timeout for searching element.
	 * @return timeout (in seconds)
	 * @author sbelei
	 */
	public int getWaitTimeout() {
		return waitTimeout;
	}

	/**
	 * Sets timeout for searching element.
	 * @param waitTimeout timeout (in seconds)
	 * @author sbelei
	 */
	public void setWaitTimeout(int waitTimeout) {
		this.waitTimeout = waitTimeout;
	}

	/**
	 * Reads properties from property file
	 * For reading of additional properties, please override loadAdditionallProperties
	 * timeout - timeout for zk operations [default "100"]
	 * click-time-out - timeout for XxxAndWait operations [default "1000"]
	 * browsers - list of browsers to test (at the moment only one browser in list is supported
	 * target - URL under testing 
	 * screenshots-dir - directory for screenshots [default "screenshots"]
	 * @param propertyFile name of property file for tests
	 */
	public void loadProperties(String propertyFile) {
		InputStream testIn = ClassLoader.getSystemResourceAsStream(propertyFile);
		if (testIn != null) {
			try {
				Properties testProp = new Properties();
				testProp.load(testIn);
				_timeout = Integer.parseInt(testProp.getProperty("timeout","100"));
				setClickTimeOut(Integer.parseInt(testProp.getProperty("click-time-out","1000")));
				setWaitTimeout(Integer.parseInt(testProp.getProperty("wait-time-out","3")));
				browsers = getBrowsers(testProp.getProperty("browsers",ConfigHelper.getInstance().getBrowser()));
				target  = testProp.getProperty("target","");
				screenShotsDir = testProp.getProperty("screenshots-dir","screenshots");
				loadAdditionallProperties(testProp);
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					testIn.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	/**
	 * This method is empty and used to load additional configs from config file.
	 * You can override it if necessary
	 * @author sbelei
	 */
	public void loadAdditionallProperties(Properties testProp) {
		// TODO Auto-generated method stub
	}
	

	public void saveScreenShot(String nameSufix) { 
		(new File(screenShotsDir)).mkdir(); 
		String filename = screenShotsDir+ "//" + getClass().getSimpleName() + "_" + getName()+ nameSufix + ".png";
		captureScreenshot(filename);
	}
	
	/**
	 * Clicks and waits for action.
	 * @param locator widget locator (i.e. jq(".. )
	 * @author sbelei
	 */
	public void clickAndWait(ClientWidget locator){
		click(locator);
		waitNSeconds(waitTimeout);
		waitResponse(clickTimeOut, true);
	}

	/**
	 * Double clicks and waits for action.
	 * @param locator widget locator (i.e. jq(".. )
	 * @author sbelei
	 */
	public void doubleClickAndWait(ClientWidget locator){
		doubleClick(locator);
		waitNSeconds(waitTimeout);
		waitResponse(clickTimeOut, true);
	}
	
	/**
	 * Invokes context menu and waits for action
	 * @param locator widget locator (i.e. jq(".. )
	 * @author sbelei
	 */
	public void contextMenuAndWait(ClientWidget locator){
		contextMenu(locator);
		waitNSeconds(waitTimeout);
		waitResponse(clickTimeOut, true);
	}
	
	/**
	 * Checks if element absent.
	 * If element found method will throw an exception. 
	 * @param locator element locator
	 * @param timeout
	 * @author sbelei
	 */
	@SuppressWarnings("unchecked")
	private void assertElementAbsent(ClientWidget locator, int timeOut) {
		try {
			WebDriverWait wait = new WebDriverWait(getWebDriver(), timeOut);
			wait.until(new ErrorIfElementFound(locator, "Element present"));
		} catch (org.openqa.selenium.TimeoutException e) {
			return;	//Thats fine if we're here
		}		
	}

	/**
	 * Checks if element absent.
	 * If element found method will throw an exception. 
	 * @param locator element locator
	 * @author sbelei
	 * @throws AssertionError
	 */	
	public void assertElementAbsent(ClientWidget locator) {
		assertElementAbsent(locator, waitTimeout);
	}		
	
	/**
	 * Checks if element present.
	 * If element not found method will throw an exception.
	 * @param locator element locator
	 * @param timeout
	 * @author sbelei
	 */
	@SuppressWarnings("unchecked")
	private void assertElementPresent(ClientWidget locator, int timeOut) {
		try {
			WebDriverWait wait = new WebDriverWait(getWebDriver(), timeOut);
			wait.until(new ErrorIfElementNotFound(locator, "Element absent"));
			return;
		} catch (org.openqa.selenium.TimeoutException e) {
			throw new AssertionError("Element absent");
		}		
	}
	
	/**
	 * Checks if element present.
	 * If element not found method will throw an exception.
	 * @param locator element locator
	 * @author sbelei
	 * @throws AssertionError
	 */	
	public void assertElementPresent(ClientWidget locator) {
		assertElementPresent(locator, waitTimeout);
	}	
	
	/**
	 * Checks if error box absent.
	 * If error box present raises AssertionException
	 * @author sbelei
	 * @deprecated
	 */
	public void assertErrorBoxAbsent() {
		assertElementAbsent(errorBox(), waitTimeout);
	}
	
	
	/**
	 * Returns the locator of error box.
	 * (Modal window with title "ZK")
	 * @return locator of error box
	 * @author sbelei
	 * @deprecated
	 */
 	public ClientWidget errorBox() {
 		return jq("@window[mode=\"modal\"][title=\"ZK\"]");
	}

	/**
	 * Checks if each element of path is collapsed and opens it.
	 * You should specify at least two nodes.
	 * @param nodes path, i.e. node, subnode, subsubnode
	 * @author sbelei
	 */
	public void walkByPath(String... nodes) {
		for (int i=0;i<nodes.length-1;i++) {
			try {
				assertElementPresent(Tree.cell(nodes[i+1]));
			} catch (AssertionError e){
				clickAndWait(Tree.cellPlus(nodes[i]));
			}
		}
		clickAndWait(Tree.cellPlus(nodes[nodes.length-1]));
	}
	
	private void wait1second()
	// post: pause for one second
	{
		long now = System.currentTimeMillis();
		long then = now + 1000; // one second of milliseconds
		while (System.currentTimeMillis() < then) { 
			double garbage = Math.PI*Math.PI; 
		} 
	} 

	/**
	 * Make pause for n seconds
	 * @param n number of seconds to wait
	 */
	public void waitNSeconds(int n) 
	// pre: number >= 0
	// post: pause for number seconds
	{
		int i;
		for (i = 0; i < n; i++){ 
			wait1second(); 
		} 
	}

	/**
	 * Getter for <code>jq</code>  object in ZTL test case
	 * @param string
	 * @return
	 */
	public JQuery jq(String string) {
		return jq(string);
	}

	/**
	 * Sets <code>_timeout</code> variable for ZTL tests
	 * @param timeout in miliseconds
	 */
	public void _setTimeout(int timeout) {
		_timeout = timeout;
	} 
}
