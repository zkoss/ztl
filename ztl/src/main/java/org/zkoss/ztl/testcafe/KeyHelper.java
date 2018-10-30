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
	public static String getKeys(String o) {
		String keyString = o;
		if (Keys.ENTER.toString().equals(o)) keyString = "enter";
		else if (Keys.BACK_SPACE.toString().equals(o)) keyString = "backspace";
		else if (Keys.TAB.toString().equals(o)) keyString = "tab";
		else if (Keys.RETURN.toString().equals(o)) keyString = "enter";
		else if (Keys.ENTER.toString().equals(o)) keyString = "enter";
		else if (Keys.ESCAPE.toString().equals(o)) keyString = "esc";
		else if (Keys.SPACE.toString().equals(o)) keyString = "space";
		else if (Keys.PAGE_UP.toString().equals(o)) keyString = "pageup";
		else if (Keys.PAGE_DOWN.toString().equals(o)) keyString = "pagedown";
		else if (Keys.END.toString().equals(o)) keyString = "end";
		else if (Keys.HOME.toString().equals(o)) keyString = "home";
		else if (Keys.LEFT.toString().equals(o)) keyString = "left";
		else if (Keys.ARROW_LEFT.toString().equals(o)) keyString = "left";
		else if (Keys.UP.toString().equals(o)) keyString = "up";
		else if (Keys.ARROW_UP.toString().equals(o)) keyString = "up";
		else if (Keys.RIGHT.toString().equals(o)) keyString = "right";
		else if (Keys.ARROW_RIGHT.toString().equals(o)) keyString = "right";
		else if (Keys.DOWN.toString().equals(o)) keyString = "down";
		else if (Keys.ARROW_DOWN.toString().equals(o)) keyString = "down";
		else if (Keys.DELETE.toString().equals(o)) keyString = "delete";
		else if (Keys.NUMPAD0.toString().equals(o)) keyString = "0";
		else if (Keys.NUMPAD1.toString().equals(o)) keyString = "1";
		else if (Keys.NUMPAD2.toString().equals(o)) keyString = "2";
		else if (Keys.NUMPAD3.toString().equals(o)) keyString = "3";
		else if (Keys.NUMPAD4.toString().equals(o)) keyString = "4";
		else if (Keys.NUMPAD5.toString().equals(o)) keyString = "5";
		else if (Keys.NUMPAD6.toString().equals(o)) keyString = "6";
		else if (Keys.NUMPAD7.toString().equals(o)) keyString = "7";
		else if (Keys.NUMPAD8.toString().equals(o)) keyString = "8";
		else if (Keys.NUMPAD9.toString().equals(o)) keyString = "9";
			//Modifier
		else if (Keys.SHIFT.toString().equals(o)) keyString = "shift";
		else if (Keys.LEFT_SHIFT.toString().equals(o)) keyString = "shift";
		else if (Keys.CONTROL.toString().equals(o)) keyString = "ctrl";
		else if (Keys.LEFT_CONTROL.toString().equals(o)) keyString = "ctrl";
		else if (Keys.ALT.toString().equals(o)) keyString = "alt";
		else if (Keys.LEFT_ALT.toString().equals(o)) keyString = "alt";
		else if (Keys.COMMAND.toString().equals(o)) keyString = "meta";
		return keyString;
	}
}
