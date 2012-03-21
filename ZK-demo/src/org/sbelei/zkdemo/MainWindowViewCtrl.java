package org.sbelei.zkdemo;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

public class MainWindowViewCtrl extends GenericForwardComposer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Window mainWindow;
	private Button actButton;
	private Label msgLabel;

	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);
		System.out.println("after compose");
		// TODO Auto-generated method stub
		actButton.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event event) throws Exception {
				System.out.println("Jo");
			}
		});
	}
	
	public void onClick$actButton(Event event) {
		System.out.println("Hello world");
		msgLabel.setValue("hello");
	}
}
