package org.zkoss.ztl.util.ui;

import org.zkoss.ztl.ClientWidget;
import org.zkoss.ztl.JQuery;

public class Tree {

	public static ClientWidget cell(String label) {
		return new JQuery("@treecell[label=\""+label+"\"]").toWidget().$n("cave");
	}
	
	public static ClientWidget cellPlus(String label) {
		return new JQuery("@treecell[label=\""+label+"\"]").toWidget().$n("cave").firstChild();
	}
}
