/* ZKSelenium.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 18, 2010 3:54:01 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.ztl.util;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.internal.WrapsDriver;

import com.thoughtworks.selenium.CommandProcessor;
import com.thoughtworks.selenium.DefaultSelenium;

/**
 * @author jumperchen
 *
 */
public class ZKSelenium extends DefaultSelenium  implements
WrapsDriver {
	private String _browsername;
	private String _speed = "200";
	
	private boolean _openonce = false;
	private int _cyclecount = 0;

	public ZKSelenium(CommandProcessor processor) {
		super(processor);
	}
	public ZKSelenium(CommandProcessor processor,boolean openonce) {
		super(processor);
		this._openonce=openonce;
	}
	public ZKSelenium(CommandProcessor processor, String browsername, boolean openonce) {
		super(processor);
		this._openonce=openonce;
		_browsername = browsername;
	}
	
	private boolean isBrowserOpened = false;
	@Override
	public void start() {
		if(_openonce){
			_cyclecount++;
				
			if(!isBrowserOpened){
				super.start();
				isBrowserOpened = true;
			}
		}else{
			super.start();
		}
	}
	@Override
	public void close() {
		if(!_openonce || _cyclecount % 20 == 0){
			getWrappedDriver().close();
			isBrowserOpened = false;
		}
	}
	@Override
	public void stop() {
		if(!_openonce  || _cyclecount % 20 == 0){
			getWrappedDriver().quit();
			isBrowserOpened = false;
		}
	}
	
	public String getBrowserName() {
		return _browsername;
	}
		
	public CommandProcessor getCmdProcessor() {
	    return commandProcessor;
	}
	
	@Override
	public WebDriver getWrappedDriver() {
		return ((WrapsDriver) commandProcessor).getWrappedDriver();
	}
	@Override
	public void setSpeed(String value) {
		super.setSpeed(value);
		_speed = value;
	}
	@Override
	/**
	 * Returns the command speed, if any.
	 * <p> Default is 200 ms.
	 */
	public String getSpeed() {
		return _speed;
	}
	
}
