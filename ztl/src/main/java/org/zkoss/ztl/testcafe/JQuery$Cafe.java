/* JQuery$Cafe.java
	Purpose:

	Description:

	History:
		Wed Jun 13 10:19:05 CST 2018, Created by jameschu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.ztl.testcafe;

import org.openqa.selenium.By;
import org.zkoss.ztl.unit.*;
import org.zkoss.ztl.util.Scripts;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author jameschu
 */
public class JQuery$Cafe extends JQuery {
	private List<CafeTestStep> _testSteps;
	private static String JQ = "jq('%1')";

	public JQuery$Cafe(String uuid, List<CafeTestStep> testSteps) {
		super(uuid);
		_testSteps = testSteps;
	}

	public JQuery$Cafe(ClientWidget el, List<CafeTestStep> testSteps) {
		super(el);
		_testSteps = testSteps;
	}

	public JQuery$Cafe(StringBuffer out, String script, List<CafeTestStep> testSteps) {
		super(out, script);
		_testSteps = testSteps;
	}

	@Override
	public String css(String name) {
		return Scripts.getCafeClientFunction(_out.toString() + ".css('" + name + "')");
	}

	@Override
	public String attr(String name) {
		return Scripts.getCafeClientFunction(_out.toString() + ".attr('" + name + "')");
	}

	@Override
	public boolean hasClass(String className) {
		return false;
	}

	@Override
	public boolean is(String selector) {
		return false;
	}

	@Override
	public boolean isVisible() {
		return is(":visible");
	}

	@Override
	public JQuery find(String selector) {
		return new JQuery$Cafe(_out, ".find('" + selector + "')", _testSteps);
	}

	@Override
	public JQuery first() {
		return new JQuery$Cafe(_out, ".first()", _testSteps);
	}

	@Override
	public JQuery last() {
		return new JQuery$Cafe(_out, ".last()", _testSteps);
	}

	@Override
	public JQuery prev() {
		return new JQuery$Cafe(_out, ".prev()", _testSteps);
	}

	@Override
	public JQuery next() {
		return new JQuery$Cafe(_out, ".next()", _testSteps);
	}

	@Override
	public JQuery children() {
		return new JQuery$Cafe(_out, ".children()", _testSteps);
	}

	@Override
	public JQuery children(String selector) {
		return new JQuery$Cafe(_out, ".children('" + selector + "')", _testSteps);
	}

	@Override
	public JQuery parent() {
		return new JQuery$Cafe(_out, ".parent()", _testSteps);
	}

	@Override
	public JQuery parent(String selector) {
		return new JQuery$Cafe(_out, ".parent('" + selector + "')", _testSteps);
	}

	@Override
	public JQuery parents(String selector) {
		return new JQuery$Cafe(_out, ".parents('" + selector + "')", _testSteps);
	}

	@Override
	public String text() {
		return Scripts.getCafeClientFunction(_out.toString() + ".text()");
	}

	@Override
	public String html() {
		return Scripts.getCafeClientFunction(_out.toString() + ".html()");
	}

	@Override
	public String val() {
		return Scripts.getCafeClientFunction(_out.toString() + ".val()");
	}

	@Override
	public int height() {
		return 0;
	}

	@Override
	public int width() {
		return 0;
	}

	@Override
	public int innerHeight() {
		return 0;
	}

	@Override
	public int innerWidth() {
		return 0;
	}

	@Override
	public int outerWidth() {
		return 0;
	}

	@Override
	public int outerWidth(boolean includeMargin) {
		return 0;
	}

	@Override
	public int outerHeight() {
		return 0;
	}

	@Override
	public int outerHeight(boolean includeMargin) {
		return 0;
	}

	@Override
	public int length() {
		return 0;
	}

	@Override
	public int scrollbarWidth() {
		return 0;
	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public int offsetLeft() {
		return 0;
	}
	@Override
	public int offsetTop() {
		return 0;
	}

	@Override
	public int positionLeft() {
		return 0;
	}

	@Override
	public int positionTop() {
		return 0;
	}

	@Override
	public int scrollTop() {
		return 0;
	}

	@Override
	public void scrollTop(int value) {
		Scripts.doCafeEval(_out.toString() + ".scrollTop(" + value + ")", _testSteps);
	}

	@Override
	public int scrollLeft() {
		return 0;
	}

	@Override
	public void scrollLeft(int value) {
		Scripts.doCafeEval(_out.toString() + ".scrollLeft(" + value + ")", _testSteps);
	}

	@Override
	public int scrollHeight() {
		return 0;
	}

	@Override
	public int scrollWidth() {
		return 0;
	}

	@Override
	public Element get(int index) {
		return new Element$Cafe(_out.toString() + "[" + index + "]", _testSteps);
	}

	public Element toElement() {
		return get(0);
	}

	@Override
	public JQuery eq(int index) {
		return new JQuery$Cafe(_out, ".eq(" + index + ")", _testSteps);
	}

	@Override
	public Iterator<JQuery> iterator() {
		return new JQuery$Cafe.JQueryIerator(this);
	}

	/**
	 * translate to widget. (a shortcut for  new Widget($obj) );
	 * @return
	 */
	public Widget toWidget() {
		return new Widget$Cafe(this, _testSteps);
	}

	@Override
	public ZK zk() {
		return new ZK$Cafe(_out, ".zk", _testSteps);
	}

	public By toBy() {
		return get(0);
	}

	private class JQueryIerator implements Iterator<JQuery> {
		private JQuery _context;
		private int _count;
		private int _index = 0;

		public JQueryIerator(JQuery context) {
			_context = context;
			_count = _context.length();
		}

		@Override
		public boolean hasNext() {
			return _index != _count;
		}

		@Override
		public JQuery next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			JQuery result = _context.eq(_index);
			++_index;
			return result;
		}

		/**
		 * why we don't support remove in this time?
		 * Because we don't really got a jQuery instance in this time.
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
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
