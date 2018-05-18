/* CafeTestStep.java
	Purpose:

	Description:

	History:
		Tue May 15 12:55:39 CST 2018, Created by jameschu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.ztl.testcafe;

/**
 * @author jameschu
 */
public class CafeTestStep {
	public static String ACTION = "action";
	public static String ASSERTION = "assertion";
	public static String EVAL = "eval";

	private String _type;
	private String _content;

	public CafeTestStep(String type, String content) {
		this._type = type;
		this._content = content;
	}

	public String getType() {
		return _type;
	}

	public String getContent() {
		return _content;
	}
}
