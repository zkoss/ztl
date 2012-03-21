package org.zkoss.ztl.util.ui;

import org.zkoss.ztl.ClientWidget;
import org.zkoss.ztl.JQuery;

public class MessageBox {

	/**
	 * Locates error message box by title
	 * You can fetch error message in next way
	 * <code>
	 * MessageBox.byTitle("Title").text()
	 * </code>
	 * @param title - window title (could be empty)
	 * @return locator of label on the message window
	 */
	public static  JQuery byTitle(String title) {
		if ("".equals(title)) {
			return new JQuery("@window @label");
		} else {
			return new JQuery("@window[title=\""+title+"\"] @label");
		}
	}
}
