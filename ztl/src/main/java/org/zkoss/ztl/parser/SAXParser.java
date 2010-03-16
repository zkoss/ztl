/* SAXParser.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mar 8, 2010 2:27:40 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
 */
package org.zkoss.ztl.parser;

import java.io.File;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.zkoss.ztl.util.Test;

/**
 * A simple SAX parser for ZTL file.
 * @author jumperchen
 */
public class SAXParser {
	public static Test parser(File f) {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		javax.xml.parsers.SAXParser sparser = null;
		try {
			sparser = spf.newSAXParser();
			SAXHandler handler = new SAXHandler();
			setSafeProperty(sparser,
					"http://xml.org/sax/properties/lexical-handler",
					"http://xml.org/sax/handlers/LexicalHandler", handler);

			sparser.parse(f, handler);
			return handler.getRoot();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void setSafeProperty(javax.xml.parsers.SAXParser sparser,
			String name, String auxnm, Object value) {
		Throwable ex;
		try {
			sparser.setProperty(name, value);
			return;
		} catch (Throwable t) {
			ex = t;
		}
		if (auxnm != null) {
			try {
				sparser.setProperty(auxnm, value);
				return;
			} catch (Throwable t) {
			}
		}
	}
}
