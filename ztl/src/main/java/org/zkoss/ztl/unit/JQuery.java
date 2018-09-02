/* JQuery.java

{{IS_NOTE
	Purpose:

	Description:

	History:
		Dec 4, 2009 10:45:32 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
 */
package org.zkoss.ztl.unit;

import org.openqa.selenium.By;
import org.zkoss.ztl.util.Scripts;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A simulator of JQuery client side object, which wraps the JQuery client side
 * API.
 *
 * @author jumperchen
 *
 */
public class JQuery extends ClientWidget implements Iterable<JQuery> {
	/**
	 * The script of get jq by UUID
	 */
	private static String JQ = "jq('%1')";

	public JQuery(String uuid) {
		if (isEmpty(uuid))
			throw new NullPointerException("uuid cannot be null!");
		_out = new StringBuffer(JQ.replace("%1", uuid));
	}

	public JQuery(ClientWidget el) {
		_out = new StringBuffer(JQ.replace("'%1'", el.toString()));
	}

	public JQuery(StringBuffer out) {
		this(out, null);
	}

	public JQuery(StringBuffer out, String script) {
		_out = new StringBuffer(out);
		if (script != null)
			_out.append(script);
	}

	/**
	 * Returns the CSS value from the given name.
	 *
	 * @param name
	 *            CSS name.
	 */
	public String css(String name) {
		return Scripts.getEval(_out.toString() + ".css('" + name + "')");
	}

	/**
	 * Returns the attribute value from the given name.
	 *
	 * @param name
	 *            attribute name of the element.
	 */
	public String attr(String name) {
		return Scripts.getEval(_out.toString() + ".attr('" + name + "')");
	}

	/**
	 * Returns whether includes the className.
	 *
	 * @param className
	 *            the CSS class name.
	 */
	public boolean hasClass(String className) {
		return Boolean.valueOf(Scripts.getEval(_out.toString() + ".hasClass('" + className + "')"));
	}

	/**
	 * check the jquery element match the selector. (We port this from jQuery);
	 *
	 * @param selector
	 *            the JQuery allowed
	 * @return
	 */
	public boolean is(String selector) {
		return Boolean.valueOf(Scripts.getEval(_out.toString() + ".is('" + selector + "')"));

	}

	/**
	 * a short cut for visible
	 */
	public boolean isVisible() {
		return is(":visible");
	}

	/**
	 * Finds the element from the given selector.
	 *
	 * @param selector
	 *            the JQuery allowed.
	 */
	public JQuery find(String selector) {
		return new JQuery(_out, ".find('" + selector + "')");
	}

	/**
	 * Returns the first element in JQuery object.
	 */
	public JQuery first() {
		return new JQuery(_out, ".first()");
	}

	/**
	 * Returns the last element in JQuery object.
	 */
	public JQuery last() {
		return new JQuery(_out, ".last()");
	}

	/**
	 * Returns the previous element in JQuery object.
	 */
	public JQuery prev() {
		return new JQuery(_out, ".prev()");
	}

	/**
	 * Returns the next element in JQuery object.
	 */
	public JQuery next() {
		return new JQuery(_out, ".next()");
	}

	/**
	 * Returns the child element in JQuery object.
	 */
	public JQuery children() {
		return new JQuery(_out, ".children()");
	}

	/**
	 * Returns the child element in JQuery object.
	 */
	public JQuery children(String selector) {
		return new JQuery(_out, ".children('" + selector + "')");
	}

	/**
	 * Returns the parent element in JQuery object.
	 */
	public JQuery parent() {
		return new JQuery(_out, ".parent()");
	}

	/**
	 * Returns the parent element in JQuery object.
	 */
	public JQuery parent(String selector) {
		return new JQuery(_out, ".parent('" + selector + "')");
	}

	/**
	 * Returns the parents element in JQuery object.
	 */
	public JQuery parents(String selector) {
		return new JQuery(_out, ".parents('" + selector + "')");
	}

	/**
	 * Returns the text content
	 */
	public String text() {
		return Scripts.getEval(_out.toString() + ".text()");
	}

	/**
	 * Returns the html content(innerHTML)
	 */
	public String html() {
		return Scripts.getEval(_out.toString() + ".html()");
	}

	/**
	 * Returns the current value of the first element in the set of matched
	 * elements.
	 *
	 * @return
	 */
	public String val() {
		return Scripts.getEval(_out.toString() + ".val()");
	}

	/**
	 * Returns the current computed height for the first element.
	 */
	public int height() {
		return parseInt(Scripts.getEval(_out.toString() + ".height()"));
	}

	/**
	 * Returns the current computed width for the first element.
	 */
	public int width() {
		return parseInt(Scripts.getEval(_out.toString() + ".width()"));
	}

	/**
	 * Returns the current computed height for the first element, including
	 * padding but not border.
	 */
	public int innerHeight() {
		return parseInt(Scripts.getEval(_out.toString() + ".innerHeight()"));
	}

	/**
	 * Returns the current computed width for the first element, including
	 * padding but not border.
	 */
	public int innerWidth() {
		return parseInt(Scripts.getEval(_out.toString() + ".innerWidth()"));
	}

	/**
	 * Returns the current computed width for the first element, including
	 * padding and border.
	 */
	public int outerWidth() {
		return parseInt(Scripts.getEval(_out.toString() + ".outerWidth()"));
	}

	/**
	 * Returns the current computed width for the first element, including
	 * padding and border, it will including margin, if true
	 *
	 * @param includeMargin
	 */
	public int outerWidth(boolean includeMargin) {
		return parseInt(Scripts.getEval(_out.toString() + ".outerWidth(" + includeMargin + ")"));
	}

	/**
	 * Returns the current computed height for the first element, including
	 * padding and border.
	 */
	public int outerHeight() {
		return parseInt(Scripts.getEval(_out.toString() + ".outerHeight()"));
	}

	/**
	 * Returns the current computed height for the first element, including
	 * padding and border, it will including margin, if true
	 *
	 * @param includeMargin
	 */
	public int outerHeight(boolean includeMargin) {
		return parseInt(Scripts.getEval(_out.toString() + ".outerHeight(" + includeMargin + ")"));
	}

	/**
	 * Returns the length of the array from the jQuery object.
	 */
	public int length() {
		return parseInt(Scripts.getEval(_out.toString() + ".length"));
	}

	/**
	 * Switches to the ZK object.
	 */
	public ZK zk() {
		return new ZK(_out, ".zk");
	}

	public By toBy() {
		String id = attr("id");
		if (!isEmpty(id))
			return By.id(id);
		return By.className(attr("className"));
	}

	/**
	 * Returns the scrollbar width.
	 */
	public int scrollbarWidth() {
		return parseInt(Scripts.getEval("jq.scrollbarWidth()"));
	}

	/**
	 * Returns whether the widget exists or not.
	 */
	public boolean exists() {
		return Boolean.valueOf(Scripts.getEval("!!" + _out.toString() + "[0]"));
	}

	/**
	 * Returns the current computed offsetLeft for the first element
	 */
	public int offsetLeft() {
		return parseInt(Scripts.getEval(_out.toString() + ".offset().left"));
	}

	/**
	 * Returns the current computed offsetTop for the first element
	 */
	public int offsetTop() {
		return parseInt(Scripts.getEval(_out.toString() + ".offset().top"));
	}

	/**
	 * Returns the current computed positionLeft (the offsetLeft relative to the
	 * parent) for the first element
	 *
	 * @return
	 */
	public int positionLeft() {
		return parseInt(Scripts.getEval(_out.toString() + ".position().left"));
	}

	/**
	 * Returns the current computed positionTop (the offsetTop relative to the
	 * parent) for the first element
	 *
	 * @return
	 */
	public int positionTop() {
		return parseInt(Scripts.getEval(_out.toString() + ".position().top"));
	}

	/**
	 * getter for scrollTop
	 * if multiple result , will receive first value.
	 * @return
	 */
	public int scrollTop() {
		return parseInt(Scripts.getEval(_out.toString() + ".scrollTop()"));
	}

	/**
	 * setter for scrollTop
	 * @param value
	 */
	@Deprecated //please use scroll method in ZKClientTestCase
	public void scrollTop(int value) {
		Scripts.getEval(_out.toString() + ".scrollTop(" + value + ");1;");
	}

	/**
	 * getter for scrollLeft
	 * if multiple result , will receive first value.
	 * @return
	 */
	public int scrollLeft() {
		return parseInt(Scripts.getEval(_out.toString() + ".scrollLeft()"));
	}

	/**
	 * setter for scrollLeft
	 * @param value
	 */
	@Deprecated //please use scroll method in ZKClientTestCase
	public void scrollLeft(int value) {
		Scripts.getEval(_out.toString() + ".scrollLeft(" + value + ");1;");
	}

	/**
	 * Note:This not a jQuery base method.
	 * just a short cut ,in javascript's world equals get(0).scrollHeight
	 * @return
	 */
	public int scrollHeight() {
		return parseInt(get(0).attr("scrollHeight"));
	}

	/**
	 * Note:This not a jQuery base method.
	 * just a short cut ,in javascript's world equals get(0).scrollWidth
	 * @return
	 */
	public int scrollWidth() {
		return parseInt(get(0).attr("scrollWidth"));
	}

	/**
	 * proxy for jQuery get method
	 * @param index
	 * @return Element  the dom element
	 */
	public Element get(int index) {
		return new Element(_out.toString() + "[" + index + "]");
	}

	public Element toElement() {
		return get(0);
	}

	/**
	 * proxy for jQuery eq method
	 * @param index
	 * @return
	 */
	public JQuery eq(int index) {
		return new JQuery(_out, ".eq(" + index + ")");
	}

	@Override
	public Iterator<JQuery> iterator() {
		return new JQueryIerator(this);
	}

	/**
	 * translate to widget. (a shortcut for  new Widget($obj) );
	 * @return
	 */
	public Widget toWidget() {
		return new Widget(this);
	}

	/**
	 * i use private class to prevent more complexly code in util.
	 * no body should know how it works ,
	 * just know that it return the jquery object in order.
	 * in fact , this is useful i think.
	 * @author Tony
	 *
	 */
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

	/**
	 * Used in test cafe
	 */
	public String hasClass_cafeStr(String className) {
		return Scripts.getCafeEval(_out.toString() + ".hasClass('" + className + "')");
	}
	public String is_cafeStr(String selector) {
		return Scripts.getCafeEval(_out.toString() + ".is('" + selector + "')");
	}
	public String isVisible_cafeStr() {
		return is_cafeStr(":visible");
	}
	public String height_cafeStr() {
		return Scripts.getCafeEval(_out.toString() + ".height()");
	}
	public String width_cafeStr() {
		return Scripts.getCafeEval(_out.toString() + ".width()");
	}
	public String innerHeight_cafeStr() {
		return Scripts.getCafeEval(_out.toString() + ".innerHeight()");
	}
	public String innerWidth_cafeStr() {
		return Scripts.getCafeEval(_out.toString() + ".innerWidth()");
	}
	public String outerWidth_cafeStr() {
		return Scripts.getCafeEval(_out.toString() + ".outerWidth()");
	}
	public String outerWidth_cafeStr(boolean includeMargin) {
		return Scripts.getCafeEval(_out.toString() + ".outerWidth(" + includeMargin + ")");
	}
	public String outerHeight_cafeStr() {
		return Scripts.getCafeEval(_out.toString() + ".outerHeight()");
	}
	public String outerHeight_cafeStr(boolean includeMargin) {
		return Scripts.getCafeEval(_out.toString() + ".outerHeight(" + includeMargin + ")");
	}
	public String length_cafeStr() {
		return Scripts.getCafeEval(_out.toString() + ".length");
	}
	public String exists_cafeStr() {
		return Scripts.getCafeEval("!!" + _out.toString() + "[0]");
	}
	public String offsetLeft_cafeStr() {
		return Scripts.getCafeEval(_out.toString() + ".offset().left");
	}
	public String offsetTop_cafeStr() {
		return Scripts.getCafeEval(_out.toString() + ".offset().left");
	}
	public String positionLeft_cafeStr() {
		return Scripts.getCafeEval(_out.toString() + ".position().left");
	}
	public String positionTop_cafeStr() {
		return Scripts.getCafeEval(_out.toString() + ".position().top");
	}
	public String scrollTop_cafeStr() {
		return Scripts.getCafeEval(_out.toString() + ".scrollTop()");
	}
	public String scrollLeft_cafeStr() {
		return Scripts.getCafeEval(_out.toString() + ".scrollLeft()");
	}
	public String scrollHeight_cafeStr() {
		return get(0).attr("scrollHeight");
	}
	public String scrollWidth_cafeStr() {
		return get(0).attr("scrollWidth");
	}

}
