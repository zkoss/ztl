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
package org.zkoss.ztl;

import org.openqa.selenium.By;

/**
 * A simulator of ZK client side object, which wraps the ZK client API.
 * @author jumperchen
 */
public class ZK extends ClientWidget {
	/**
	 * The script of get jq by UUID
	 */
	private static String ZK = "zk('%1')";
	public final static String VERSION = ZKClientTestCaseCafe.callEval("zk.version");

	public ZK(String selector) {
		if (isEmpty(selector))
			throw new NullPointerException("selector cannot be null!");
		_out = new StringBuffer(ZK.replace("%1", selector));
	}
	public ZK(ClientWidget el) {
		_out = new StringBuffer(ZK.replace("'%1'", el.toString()));
	}
	public ZK(StringBuffer out, String script) {
		_out = new StringBuffer(out).append(script);
	}
	public ZK(StringBuffer out) {
		_out = new StringBuffer(out);
	}
	
	/**
	 * Returns the revised offset array.
	 */
	public int[] revisedOffset() {
		String[] s = ZKClientTestCaseCafe.callEval(_out.toString() + ".revisedOffset()").split(",");
		return new int[] {ZKClientTestCase.parseInt(s[0]), ZKClientTestCase.parseInt(s[1])};
	}
	/**
	 * Returns the revised width.
	 * @param size the original size.
	 */
	public int revisedWidth(int size) {
		return Integer.parseInt(ZKClientTestCaseCafe.callEval(_out.toString() + ".revisedWidth("+ size +")"));
	}
	/**
	 * Returns the revised height.
	 * @param size the original size.
	 */
	public int revisedHeight(int size) {
		return Integer.parseInt(ZKClientTestCaseCafe.callEval(_out.toString() + ".revisedHeight("+ size +")"));
	}
	
	/**
	 * focus the current element
	 */
	public void focus() {
		ZKClientTestCaseCafe.callEval(_out.toString() + ".focus()");
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
}
