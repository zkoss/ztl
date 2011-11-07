package org.zkoss.ztl.util.ui;

import org.zkoss.ztl.ClientWidget;
import org.zkoss.ztl.JQuery;

public class Listcell {

	public static ClientWidget byLabel(String label) {
		return new JQuery("@listcell[title=\""+label+"\"]");
	}
}