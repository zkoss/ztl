package org.zkoss.ztl.util.ui;

import org.zkoss.ztl.ClientWidget;
import org.zkoss.ztl.JQuery;

/**
 * This class helps to locate menu items
 * @author sbelei
 *
 */
public class Menu {
	
	public static ClientWidget byLabel(String label) {
		return new JQuery("@menu[label=\""+label+"\"]");
	}
}
