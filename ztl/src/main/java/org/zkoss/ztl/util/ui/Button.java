package org.zkoss.ztl.util.ui;

import org.zkoss.ztl.ClientWidget;
import org.zkoss.ztl.JQuery;

public class Button{
	
	public static ClientWidget byLabel(String label) {
		return new JQuery("@button[label=\""+label+"\"]");
	}
}
