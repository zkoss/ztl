/* clickButtonTest.java

	Purpose:
		
	Description:
		
	History:
		Aug, 19, 2011 17:06:27 PM

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

This program is distributed under Apache License Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
 */
package org.sbelei.zktreesort;

import junit.framework.TestCase;

import org.junit.Test;
import org.zkoss.ztl.ZKIntegrationTestCase;
import org.zkoss.ztl.util.ui.Button;

import com.thoughtworks.selenium.Selenium;

public class TestButtonClick extends TestCase {
	private ZKIntegrationTestCase zk;

	public TestButtonClick() {
		zk = new ZKIntegrationTestCase();
		zk.setTarget("http://localhost:8080/ZK-demo");
		zk.setBrowsers(zk.makeListOfBrowsers("iexplore"));
		zk._setTimeout(100);
		zk.setClickTimeOut(60000);
	}

	public void setUp(){
		zk.setUp();
	}
	
	public void tearDown(){
		try {
			zk.tearDown();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testClick() {
		for (Selenium selenium : zk.getBrowsers()) {
			try {
				zk.start(selenium);
				zk.windowFocus();
				zk.windowMaximize();
				zk.clickAndWait(Button.byLabel("click"));
				zk.assertElementPresent(zk.getJq("@label[value=\"hello\"]"));
			} finally {
				zk.stop();
			}
		}
	}
}
