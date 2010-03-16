/* Server.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mar 4, 2010 6:23:41 PM , Created by jumperchen
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
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The server entity for ZTL file.
 * @author jumperchen
 *
 */
public class Server implements Source {
	private String _content;
	private Map<String, String> _attrs;
	private Case _case;
	private Server _ref;
	/**
	 * Bug fix for skip EL in ID definition 
	 */
	private static String[] ID_GROUP = {"id=( \"[^${][^{}]*?\"|\"[^${][^{}]*?\"|\'[^${][^{}]*?\')",
				"id =( \"[^${][^{}]*?\"|\"[^${][^{}]*?\"|\'[^${][^{}]*?\')",
				"setId\\((\"[^${][^{}]*?\")"};
	public Server(Map<String,String> attrs) {
		_attrs = attrs;
	}
	
	public String getContent() {
		return _content;
	}

	public String getId() {
		return _attrs.get("id");
	}
	public Server getRef() {
		if (_ref != null) return _ref;
		String ref = _attrs.get("ref");
		if (ref != null) {
			Test test = getTest();
			for (Case c : test.getChildren()) {
				for (Source s : c.getChildren()) {
					if ("Server".equals(s.getType())
							&& ref.equals(((Server) s).getId())) {
						_ref = (Server) s;
						return _ref;
					}
				}
			}
		}
		return null;
	}
	@Override
	public void setContent(String content) {
		if (_content != null) {
			_content += content;
		} else {
			_content = content;
		}
	}
	public String[] getIdArray() {
		Server ref = getRef();
		if (ref != null)
			return ref.getIdArray();
		if (_content != null) {
			List<String> list = new LinkedList<String>();
			for (String ptn : ID_GROUP) {
				Pattern pattern = Pattern.compile(ptn);
				Matcher matcher = pattern.matcher(_content);
				while (matcher.find()) {
					String id = matcher.group(1).trim();
					list.add(id.substring(1, id.length() - 1));
				}
			}
			return list.toArray(new String[0]);
		}
		return new String[0];
	}
	@Override
	public String[] getContentArray() {
		Server ref = getRef();
		if (ref != null)
			return ref.getContentArray();
		if (_content != null) {
			BufferedReader reader = new BufferedReader(
					  new StringReader(_content));
			String str;
			LinkedList<String> ary = new LinkedList<String>();
			try {
				String lang = getLang();
				if (!"zul".equals(lang))
					ary.add("\"<zscript lang=\\\"" + lang + "\\\"><![CDATA[\"");
				while ((str = reader.readLine()) != null) {
					if (str.length() > 0) {
						str = str.replace("\\", "\\\\\\\\");
						str = str.replace("'", "\\\\'");
						str = str.replace("\"", "\\\"");
						String s = str.trim();
						if (s.indexOf('<') < 0)
							str = " " + str;
						if (str.indexOf("//") < 0 || str.indexOf("://") < 0)
							ary.add('"' + str + '"');
						else
							ary.add('"' + str + "\\\\n\"");
					}
				}

				if (!"zul".equals(lang))
					ary.add("\"]]></zscript>\"");
			} catch (IOException e) {
				e.printStackTrace();
			}
			return ary.toArray(new String[0]);
		}
		return new String[0];
	}
	
	public String getLang() {
		String lang = _attrs.get("lang");
		return lang == null ? "zul" : lang;
	}
	@Override
	public String getType() {
		return "Server";
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
