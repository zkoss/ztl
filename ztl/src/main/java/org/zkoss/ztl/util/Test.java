/* Test.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mar 4, 2010 4:34:44 PM , Created by jumperchen
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
 * The test entity for ZTL file.
 * @author jumperchen
 */
public class Test {
	private LinkedList<Case> _children;
	private Map<String, String> _attrs;
	private String _fileName;
	private String _action;
	private String _server;
	private String _contextPath;
	private String _package;
	private String _delay;
	private String _timeout;
	private String _browser;
	private long _lastModified;
	public Test(Map<String,String> attrs) {
		_children = new LinkedList<Case>();
		_attrs = attrs;
	}
	public void add(Case c) {
		c.setTest(this);
		_children.add(c);
	}
	public List<Case> getChildren() {
		return _children;
	}
	public Case get(int index) {
		return _children.get(index);
	}
	/**package*/ void setLastModified(long time) {
		_lastModified = time;
	}
	public long lastModified() {
		return _lastModified;
	}
	public String[] getImport() {
		String imports = _attrs.get("import");
		if (imports != null)
			return imports.split(",");
		return new String[0];
	}

	public boolean containsTag(String[] check){
		if(check == null || check.length==0) return false;

		String tags = getTags();
		if("".equals(tags)) return false;

		String[] items = tags.split(",");

		for(String tag:items){
			for(String str : check){
				if( tag.trim().equalsIgnoreCase(str.trim())) return true;
			}
		}
		return false;

	}
	public String getTags() {
		String tags = _attrs.get("tags");
		if (tags != null)
			return tags;
		return "";
	}
	/**package*/ void setContextPath(String contextPath) {
		_contextPath = (contextPath.startsWith("/") ? "" : '/') + contextPath;
	}
	public String getContextPath() {
		String contextPath = _attrs.get("context-path");
		if (contextPath != null) {
			return (contextPath.startsWith("/") ? "" : '/') + contextPath;
		}
		return _contextPath;
	}
	/**package*/ void setServer(String server) {
		_server = server;
	}
	public String getServer() {
		String serverURL = _attrs.get("server");
		if (serverURL != null)
			return serverURL;
		return _server;
	}
	/**package*/ void setAction(String action) {
		_action = action;
	}
	public String getAction() {
		String pkg = _attrs.get("action");
		if (pkg != null)
			return pkg;
		return _action;
	}
	/**package*/ void setPackage(String pkg) {
		_package = pkg;
	}
	public String getPackage() {
		return _package;
	}
	
	/**package*/ void setFileName(String fileName) {
		_fileName = fileName;
	}
	public String getFileName() {
		if (!_fileName.endsWith("Test"))
			_fileName += "Test";
		return _fileName;
	}
	
	/**package*/ void setDelay(String delay) {
		_delay = delay;
	}
	public String getDelay() {
		String delay = _attrs.get("delay");
		if (delay != null)
			return delay;
		return _delay;
	}
	
	/**package*/ void setTimeout(String timeout) {
		_timeout = timeout;
	}
	public String getTimeout() {
		String timeout = _attrs.get("timeout");
		if (timeout != null)
			return timeout;
		return _timeout;
	}
	/**package*/ void setBrowser(String browser) {
		_browser = browser;
	}
	public String getBrowser() {
		String browser = _attrs.get("browser");
		if (browser != null)
			return browser;
		return _browser;
	}
}
