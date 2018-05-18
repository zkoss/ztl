/* Keyh.java
	Purpose:

	Description:

	History:
		Thu May 17 12:49:39 CST 2018, Created by jameschu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.ztl.testcafe;

import org.openqa.selenium.Keys;

/**
 * @author jameschu
 */
public class KeyHelper {
	public static String getKey(Object o) {
		String keyString = o.toString();
		if (o instanceof Keys) {
			if (Keys.ENTER.equals(o)) keyString = "enter";
			else if (Keys.BACK_SPACE.equals(o)) keyString = "backspace";
			else if (Keys.TAB.equals(o)) keyString = "tab";
			else if (Keys.RETURN.equals(o)) keyString = "enter";
			else if (Keys.ENTER.equals(o)) keyString = "enter";
			else if (Keys.ESCAPE.equals(o)) keyString = "esc";
			else if (Keys.SPACE.equals(o)) keyString = "space";
			else if (Keys.PAGE_UP.equals(o)) keyString = "pageup";
			else if (Keys.PAGE_DOWN.equals(o)) keyString = "pagedown";
			else if (Keys.END.equals(o)) keyString = "end";
			else if (Keys.HOME.equals(o)) keyString = "home";
			else if (Keys.LEFT.equals(o)) keyString = "left";
			else if (Keys.ARROW_LEFT.equals(o)) keyString = "left";
			else if (Keys.UP.equals(o)) keyString = "up";
			else if (Keys.ARROW_UP.equals(o)) keyString = "up";
			else if (Keys.RIGHT.equals(o)) keyString = "right";
			else if (Keys.ARROW_RIGHT.equals(o)) keyString = "right";
			else if (Keys.DOWN.equals(o)) keyString = "down";
			else if (Keys.ARROW_DOWN.equals(o)) keyString = "down";
			else if (Keys.DELETE.equals(o)) keyString = "delete";
			else if (Keys.NUMPAD0.equals(o)) keyString = "0";
			else if (Keys.NUMPAD1.equals(o)) keyString = "1";
			else if (Keys.NUMPAD2.equals(o)) keyString = "2";
			else if (Keys.NUMPAD3.equals(o)) keyString = "3";
			else if (Keys.NUMPAD4.equals(o)) keyString = "4";
			else if (Keys.NUMPAD5.equals(o)) keyString = "5";
			else if (Keys.NUMPAD6.equals(o)) keyString = "6";
			else if (Keys.NUMPAD7.equals(o)) keyString = "7";
			else if (Keys.NUMPAD8.equals(o)) keyString = "8";
			else if (Keys.NUMPAD9.equals(o)) keyString = "9";
			//Modifier
			else if (Keys.SHIFT.equals(o)) keyString = "shift";
			else if (Keys.LEFT_SHIFT.equals(o)) keyString = "shift";
			else if (Keys.CONTROL.equals(o)) keyString = "ctrl";
			else if (Keys.LEFT_CONTROL.equals(o)) keyString = "ctrl";
			else if (Keys.ALT.equals(o)) keyString = "alt";
			else if (Keys.LEFT_ALT.equals(o)) keyString = "alt";
			else if (Keys.COMMAND.equals(o)) keyString = "meta";
		}
		return keyString;
	}
}
