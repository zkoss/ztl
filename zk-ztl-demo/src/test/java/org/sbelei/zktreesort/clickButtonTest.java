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

import org.junit.Test;
import org.zkoss.ztl.Tags;
import org.zkoss.ztl.Widget;
import org.zkoss.ztl.ZKClientTestCase;
import com.thoughtworks.selenium.Selenium;

@Tags(tags = "label")
public class clickButtonTest extends ZKClientTestCase {

	public clickButtonTest() {
		target = "http://localhost:8080/ZKTreeSort/index.zul";
		browsers = getBrowsers("all");
		_timeout = 60000;
		caseID = getClass().getSimpleName();
	}

	@Test(expected = AssertionError.class)
	public void testUntitled() {
		for (Selenium selenium : browsers) {
			try {
				start(selenium);
				windowFocus();
				windowMaximize();
				Widget sampleTree = widget(jq("sampleTree"));
				click(jq("$colName div.z-treecol-cnt"));
			} finally {
				stop();
			}
		}
	}
}
