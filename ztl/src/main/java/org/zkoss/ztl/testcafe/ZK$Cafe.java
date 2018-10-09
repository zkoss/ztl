/* CafeZK.java
	Purpose:

	Description:

	History:
		Wed Jun 13 10:17:44 CST 2018, Created by jameschu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.ztl.testcafe;

import org.openqa.selenium.By;
import org.zkoss.ztl.unit.ClientWidget;
import org.zkoss.ztl.unit.Element;
import org.zkoss.ztl.unit.JQuery;
import org.zkoss.ztl.util.Scripts;

import java.util.List;

/**
 * @author jameschu
 */
public class ZK$Cafe extends org.zkoss.ztl.unit.ZK {
	private static String ZK = "zk('%1')";
	private List<CafeTestStep> _testSteps;

	@Override
	public String getVersion() {
		return Scripts.getCafeClientFunction("zk.version");
	}

	public ZK$Cafe(String selector, List<CafeTestStep> testSteps) {
		super(selector);
		_testSteps = testSteps;
	}

	public ZK$Cafe(ClientWidget el, List<CafeTestStep> testSteps) {
		super(el);
		_testSteps = testSteps;
	}

	public ZK$Cafe(StringBuffer out, String script, List<CafeTestStep> testSteps) {
		super(out, script);
		_testSteps = testSteps;
	}

	@Override
	public void focus() {
		Scripts.doCafeEval(_out.toString() + ".focus()", _testSteps);
	}

	@Override
	public JQuery jq() {
		return new JQuery$Cafe(_out, ".jq", _testSteps);
	}

	@Override
	public boolean exists() {
		return jq().exists();
	}

	public Element toElement() {
		return jq().toElement();
	}

	@Override
	public By toBy() {
		throw new UnsupportedOperationException("Please use jQuery instead!");
	}

	@Override
	public String eval(String script) {
		return eval(script, true);
	}

	@Override
	public String eval(String script, boolean withDot) {
		return Scripts.getCafeClientFunction(_out.toString() + (withDot ? "." : "") + script);
	}

}
