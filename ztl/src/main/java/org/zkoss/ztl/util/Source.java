/* Item.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mar 4, 2010 6:22:01 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.ztl.util;

/**
 * The interface of the entity that is enclosed by the case entity.
 * @author jumperchen
 *
 */
public interface Source {
	public String getContent();
	public void setContent(String content);
	public String[] getContentArray();
	public String getType();
	public Case getCase();
	public Test getTest();
}
