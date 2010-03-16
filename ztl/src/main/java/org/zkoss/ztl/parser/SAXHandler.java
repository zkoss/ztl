/* SAXHandler.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mar 8, 2010 2:26:29 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.ztl.parser;

import java.util.LinkedHashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;
import org.zkoss.ztl.util.Case;
import org.zkoss.ztl.util.Client;
import org.zkoss.ztl.util.Server;
import org.zkoss.ztl.util.Test;

/**
 * An implementation of SAX handler for ZTL file to generate the Java class at
 * runtime.
 * @author jumperchen
 */
public class SAXHandler extends DefaultHandler implements LexicalHandler {
	private Test _test;
	private Case _case;
	private int _deep = -1;
	private boolean _content;
	public Test getRoot() {
		return _test;
	}
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (!_content  || length == 0) return; //ignore zero length

		final String data = new String(ch, start, length);
		if (data.trim().length() == 0) return;
		_case.get(_deep).setContent(data);
	}
	public void comment(char[] ch, int start, int length) {
		if (!_content  || length == 0) return; //ignore zero length
		final String data = new String(ch, start, length);
		if (data.trim().length() == 0) return;
		_case.get(_deep).setContent(data);
		_case.getChildren();
	}
	@Override
	public void endDocument() throws SAXException {
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		_content = false;
	}

	@Override
	public void startDocument() throws SAXException {
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attrs) throws SAXException {
		if ("test".equals(qName)) {
			if (_test != null)
				throw new SAXException("<test> has already existed!");
			_test = new Test(attrsToMap(attrs));
		} else if ("case".equals(qName)) {
			_case = new Case(attrsToMap(attrs));
			_test.add(_case);
			_deep = -1;
		} else if ("server".equals(qName)) {
			_content = true;
			_case.add(new Server(attrsToMap(attrs)));
			_deep++;
		} else if ("client".equals(qName)) {
			_content = true;
			_case.add(new Client(attrsToMap(attrs)));
			_deep++;
		} else {
			throw new SAXException("Unsupported tag [" + qName + "]");
		}
	}
	private Map<String,String> attrsToMap(Attributes attrs) {
		Map<String,String> map = new LinkedHashMap<String,String>();
		for (int j=0, len=attrs.getLength(); j<len; j++) {
			String attQname = attrs.getQName(j);
			String attLname = attrs.getLocalName(j);

			if (attQname == null || attQname.length() == 0) //just in case
				attQname = attLname;
			map.put(attQname, attrs.getValue(j));
		}
		return map;
	}
	@Override
	public void endCDATA() throws SAXException {}
	@Override
	public void endDTD() throws SAXException {}
	@Override
	public void endEntity(String name) throws SAXException {}
	@Override
	public void startCDATA() throws SAXException {}
	@Override
	public void startDTD(String name, String publicId, String systemId)	throws SAXException {}
	@Override
	public void startEntity(String name) throws SAXException {}
	
}
