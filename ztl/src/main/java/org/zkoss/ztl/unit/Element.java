/* Element.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Dec 4, 2009 12:34:01 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.ztl.unit;

import org.openqa.selenium.By;
import org.zkoss.ztl.util.Scripts;

/**
 * A simulator of DOM element object.
 * @author jumperchen
 *
 */
public class Element extends ClientWidget {
	public Element(String script) {
		_out = new StringBuffer(script);
	}
	public Element(StringBuffer out) {
		_out = new StringBuffer(out);
	}
	/**
	 * Sets the string value to the evaluated name.
	 * <p>For example,
	 * <p><code>ele.set("style.display", "none");</code>
	 * @param name any attribute of the element.
	 */
	public void set(String name, String value) {
		Scripts.getEval(_out.toString() + "." + name + " = '" + value + "'");
	}
	/**
	 * Sets the boolean value to the evaluated name.
	 * <p>For example,
	 * <p><code>ele.set("checked", false);</code>
	 * @param name any attribute of the element.
	 * @param value true or false.
	 */
	public void set(String name, boolean value) {
		Scripts.getEval(_out.toString() + "." + name + " = " + value + "");
	}
	/**
	 * Sets the number value to the evaluated name.
	 * <p>For example,
	 * <p><code>ele.set("style.top", 12);</code>
	 * @param name any attribute of the element.
	 * @param value any number.
	 */
	public void set(String name, int value) {
		Scripts.getEval(_out.toString() + "." + name + " = " + value + "");
	}
	/**
	 * Returns the result of the evaluated name.
	 * <p>For example,
	 * <p><code>ele.attr("name");</code> if the ele.name is "myname",
	 * the result is "myname" being returned.
	 * @param name any attribute of the element.
	 */
	public String attr(String name) {
		return eval(name);
	}
	/**
	 * Returns the boolean value of the evaluated name.
	 * <p>For example,
	 * <p><code>ele.is("name");</code> if the ele.name is "true",
	 * the result is true. Otherwise, false is assumed.
	 * @param name any attribute of the element.
	 * @return if true, the return value is the same as "true".
	 */
	public boolean is(String name) {
		return Boolean.valueOf(eval(name));
	}
	/**
	 * Returns the parentNode of the element.
	 */
	public Element parentNode() {
		return new Element(_out + ".parentNode");
	}
	/**
	 * Returns the firstChild of the element.
	 */
	public Element firstChild() {
		return new Element(_out + ".firstChild");
	}
	/**
	 * Returns the nextSibling of the element.
	 * @since 2.0.0
	 */
	public Element nextSibling() {
		return new Element(_out + ".nextSibling");
	}
	/**
	 * Returns whether the element exists or not.
	 */
	public boolean exists() {
		return Boolean.valueOf(eval(" != null", false));
	}
	public Element toElement() {
		return this;
	}
	
	public By toBy() {
		String id = attr("id");
		if (!isEmpty(id))
			return By.id(id);
		else
			throw new UnsupportedOperationException("Please use By.id(), By.className(), and By.cssSelector() instead!");
	}

	/**
	 * Used in test cafe
	 */
	public String is_cafeStr(String name) {
		return Scripts.getCafeClientFunction(_out.toString() + "." + name);
	}
	public String exists_cafeStr() {
		return Scripts.getCafeClientFunction(_out.toString() + " != null");
	}
}
