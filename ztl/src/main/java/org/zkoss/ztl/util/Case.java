/* Case.java

{{IS_NOTE
	Purpose:

	Description:

	History:
		Mar 4, 2010 12:03:53 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.ztl.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * The case entity for ZTL file.
 * @author jumperchen
 */
public class Case {
	private LinkedList<Source> _children;
	private Map<String, String> _attrs;
	private Test _test;
	public Case(Map<String,String> attrs) {
		_children = new LinkedList<Source>();
		_attrs = attrs;
	}
	/**package*/ void setTest(Test t) {
		_test = t;
	}

	public void add(Source c) {
		if (c instanceof Client)
			((Client) c).setCase(this);
		else if (c instanceof Server)
			((Server) c).setCase(this);
		_children.add(c);
	}
	public List<Source> getChildren() {
		return _children;
	}
	public Source get(int index) {
		return _children.get(index);
	}
	public String getId() {
		return _attrs.get("id");
	}
	public Test getTest() {
		return _test;
	}
}
