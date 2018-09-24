/* ZK.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Dec 4, 2009 6:25:17 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.ztl.unit;

import org.openqa.selenium.By;
import org.zkoss.ztl.util.Scripts;

/**
 * A simulator of ZK client side object, which wraps the ZK client API.
 * @author jumperchen
 */
public class ZK extends ClientWidget {
	/**
	 * The script of get jq by UUID
	 */
	private static String ZK = "zk('%1')";
	public String getVersion() {
		return Scripts.getEval("zk.version");
	}

	public ZK(String selector) {
		if (isEmpty(selector))
			throw new NullPointerException("selector cannot be null!");
		_out = new StringBuffer(ZK.replace("%1", selector));
	}
	public ZK(ClientWidget el) {
		_out = new StringBuffer(ZK.replace("'%1'", el.toString()));
	}
	public ZK(StringBuffer out) {
		this(out, null);
	}
	public ZK(StringBuffer out, String script) {
		_out = new StringBuffer(out);
		if (script != null)
			_out.append(script);
	}

	/**
	 * Returns the revised offset array.
	 */
	public int[] revisedOffset() {
		String[] s = Scripts.getEval(_out.toString() + ".revisedOffset()").split(",");
		return new int[]{parseInt(s[0]), parseInt(s[1])};
	}

	/**
	 * focus the current element
	 */
	public void focus() {
		Scripts.getEval(_out.toString() + ".focus()");
	}
	
	/**
	 * Switches to the JQuery object.
	 */
	public JQuery jq() {
		return new JQuery(_out, ".jq");
	}
	
	/**
	 * Returns whether the widget exists or not.
	 */
	public boolean exists() {
		return jq().exists();
	}
	
	public Element toElement() {
		return jq().toElement();
	}
	
	@Override
	public By toBy() {
		throw new UnsupportedOperationException("Please use By.id(), By.className(), and By.cssSelector() instead!");
	}

	/**
	 * Used in test cafe
	 */
	public String revisedOffset_cafeStr(String name) {
		return Scripts.getCafeEval(_out.toString() + ".revisedOffset()", true);
	}
	public String exists_cafeStr() {
		return Scripts.getCafeEval(_out.toString() + ".jq != null", true);
	}
}
