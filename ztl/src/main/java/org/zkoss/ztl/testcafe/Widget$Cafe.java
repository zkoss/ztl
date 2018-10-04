/* Widget$Cafe.java
	Purpose:

	Description:

	History:
		Wed Jun 13 10:19:16 CST 2018, Created by jameschu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.ztl.testcafe;

import org.openqa.selenium.By;
import org.zkoss.ztl.unit.Element;
import org.zkoss.ztl.unit.JQuery;
import org.zkoss.ztl.unit.Widget;
import org.zkoss.ztl.util.Scripts;

import java.util.List;

/**
 * @author jameschu
 */
public class Widget$Cafe extends Widget {
	private List<CafeTestStep> _testSteps;
	private static String WIDGET = "zk.Widget.$('%1')";

	private String _uuid;

	public Widget$Cafe(String uuid, List<CafeTestStep> testSteps) {
		super(uuid);
		_testSteps = testSteps;
	}

	public Widget$Cafe(JQuery jquery, List<CafeTestStep> testSteps) {
		super(jquery);
		_testSteps = testSteps;
	}

	public Widget$Cafe(StringBuffer out, List<CafeTestStep> testSteps) {
		this(out, null, testSteps);
	}

	public Widget$Cafe(StringBuffer out, String script, List<CafeTestStep> testSteps) {
		super(out, script);
		_out = new StringBuffer(out);
		if (script != null)
			_out.append(script);
		_testSteps = testSteps;
	}

	@Override
	public void set(String name, boolean value) {
		Scripts.doCafeEval("!!" + _out.toString() + toUpperCase(".set", name) + "(" + value + ")", _testSteps);
	}

	@Override
	public void set(String name, int value) {
		Scripts.doCafeEval("!!" + _out.toString() + toUpperCase(".set", name) + "(" + value + ")", _testSteps);
	}

	@Override
	public void set(String name, String value) {
		Scripts.doCafeEval("!!" + _out.toString() + toUpperCase(".set", name) + "('" + value + "')", _testSteps);
	}

	@Override
	public String uuid() {
		if (_uuid == null)
			return _uuid = eval("uuid");
		return _uuid;
	}

	@Override
	public String id() {
		return eval("id");
	}

	@Override
	public String attr(String name) {
		return Scripts.getCafeClientFunction(_out.toString() + toUpperCase(".get", name) + "()");
	}

	@Override
	public boolean is(String name) {
		return false;
	}

	@Override
	public Widget getChild(String name) {
		return new Widget$Cafe(_out, "." + name, _testSteps);
	}

	@Override
	public int nChildren() {
		return 0;
	}

	@Override
	public Widget lastChild() {
		return new Widget$Cafe(_out, ".lastChild", _testSteps);
	}

	@Override
	public Widget firstChild() {
		return new Widget$Cafe(_out, ".firstChild", _testSteps);
	}

	@Override
	public Widget nextSibling() {
		return new Widget$Cafe(_out, ".nextSibling", _testSteps);
	}

	@Override
	public Widget previousSibling() {
		return new Widget$Cafe(_out, ".previousSibling", _testSteps);
	}

	@Override
	public Widget $f(String id) {
		return new Widget$Cafe(_out, ".$f('" + id + "', true)", _testSteps);
	}

	@Override
	public Widget $o() {
		return new Widget$Cafe(_out, ".$o()", _testSteps);
	}

	@Override
	public Element $n() {
		return new Element$Cafe(_out + ".$n()", _testSteps);
	}

	@Override
	public Element $n(String subname) {
		return new Element$Cafe(_out + ".$n('" + subname + "')", _testSteps);
	}

	public Element toElement() {
		return $n();
	}

	@Override
	public void detach() {
		Scripts.doCafeEval(_out.toString() + ".detach()", _testSteps);
	}

	@Override
	public boolean exists() {
		return false;
	}

	public By toBy() {
		return $n();
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
