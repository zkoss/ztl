/* Element$Cafe.java
	Purpose:

	Description:

	History:
		Wed Jun 13 10:19:40 CST 2018, Created by jameschu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.ztl.testcafe;

import org.openqa.selenium.By;
import org.zkoss.ztl.unit.Element;
import org.zkoss.ztl.util.Scripts;

import java.util.List;

/**
 * @author jameschu
 */
public class Element$Cafe extends Element {
	private List<CafeTestStep> _testSteps;
	public Element$Cafe(String script, List<CafeTestStep> testSteps) {
		super(script);
		_testSteps = testSteps;
	}
	public Element$Cafe(StringBuffer out, List<CafeTestStep> testSteps) {
		super(out);
		_testSteps = testSteps;
	}
	@Override
	public void set(String name, String value) {
		Scripts.getEval(_out.toString() + "." + name + " = '" + value + "'", _testSteps, true);
	}
	@Override
	public void set(String name, boolean value) {
		Scripts.getEval(_out.toString() + "." + name + " = " + value + "", _testSteps, true);
	}
	@Override
	public void set(String name, int value) {
		Scripts.getEval(_out.toString() + "." + name + " = " + value + "", _testSteps, true);
	}
	@Override
	public String attr(String name) {
		return Scripts.getCafeEval(_out.toString() + "." + name);
	}
	@Override
	public boolean is(String name) {
		return false;
	}

	@Override
	public Element parentNode() {
		return new Element$Cafe(_out + ".parentNode", _testSteps);
	}
	@Override
	public Element firstChild() {
		return new Element$Cafe(_out + ".firstChild", _testSteps);
	}
	@Override
	public Element nextSibling() {
		return new Element$Cafe(_out + ".nextSibling", _testSteps);
	}
	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public Element toElement() {
		return this;
	}
	@Override
	public By toBy() {
		String id = attr("id");
		if (!isEmpty(id))
			return this;
		else
			throw new UnsupportedOperationException("Please use Jquery instead!");
	}
	@Override
	public String eval(String script) {
		return eval(script, true);
	}

	@Override
	public String eval(String script, boolean withDot) {
		return Scripts.getCafeEval(_out.toString() + (withDot ? "." : "") + script);
	}
}
