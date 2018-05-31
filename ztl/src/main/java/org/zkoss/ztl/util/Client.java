/* Client.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mar 4, 2010 6:23:54 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
 */
package org.zkoss.ztl.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.Map;

/**
 * The client entity for ZTL file.
 * @author jumperchen
 * 
 */
public class Client implements Source {

	private String _content = "";
	private Case _case;
	private boolean _javaToScala; //support java to scala conversion

	private Map<String, String> _attrs;

	public Client(Map<String, String> attrs, boolean javaToScala) {
		_attrs = attrs;
		_javaToScala = javaToScala;
	}

	public String getContent() {
		return _content;
	}

	public String getWait() {
		String wait = _attrs.get("wait");
		return wait == null ? "" : wait;
	}
	@Override
	public void setContent(String content) {
		//support java to scala conversion
		if (content != null && _javaToScala) {
			content = content.replaceAll("String ", "var ")
					.replaceAll("String\\[\\] ", "var ")
					.replaceAll("int ", "var ")
					.replaceAll("int\\[\\] ", "var ")
					.replaceAll("Element ", "var ")
					.replaceAll("Element\\[\\] ", "var ")
					.replaceAll(".val\\(\\)", ".`val`()")
					.replaceAll("type\\(", "typeKeys(")
					.replaceAll(";\n", "\n");
			// TODO: for loop
			// TODO: xxx ? OOO : zzz (if else)
			// TODO: parseInt
			// TODO: array
			// TODO: type
		}
		if (_content != null) {
			_content += content;
		} else {
			_content = content;
		}
	}
	@Override
	public String[] getContentArray() {
		if (_content != null) {
			BufferedReader reader = new BufferedReader(
					  new StringReader(_content));
			String str;
			LinkedList<String> ary = new LinkedList<String>();
			try {
				while ((str = reader.readLine()) != null) {
					if (str.trim().length() > 0) {
						ary.add(str.trim());
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
			return ary.toArray(new String[0]);
		}
		return new String[0];
	}

	@Override
	public String getType() {
		return "Client";
	}
	
	/**package*/ void setCase(Case c) {
		_case = c;
	}
	@Override
	public Case getCase() {
		return _case;
	}

	@Override
	public Test getTest() {
		return _case != null ? _case.getTest() : null;
	}
}
