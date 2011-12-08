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
	
	private boolean _openonce;
	private int _cyclecount = 0;
	private int _maxsize = 5;

	public ZKSelenium(CommandProcessor processor) {
		super(processor);
	}
	public ZKSelenium(CommandProcessor processor,String openonce) {
		super(processor);
		this._openonce= openonce != null;
		if (_openonce) {
			try {
				_maxsize= Integer.parseInt(openonce);
			} catch (Exception e) {}
		}
	}
	public ZKSelenium(CommandProcessor processor, String browsername, String openonce) {
		super(processor);
		this._openonce= openonce != null;
		if (_openonce) {
			try {
				_maxsize= Integer.parseInt(openonce);
			} catch (Exception e) {}
		}
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
	public void open(String url) {
		if(_openonce)
			_cyclecount++;
		
		super.open(url);
	}
	@Override
	public void close() {
		if(!_openonce || _cyclecount % _maxsize == 0){
			if (_openonce)
				ConfigHelper.getInstance().clearCache(this);
			getWrappedDriver().close();
			isBrowserOpened = false;
		}
	}
	public void shutdown() {
		getWrappedDriver().quit();
	}
	@Override
	public void stop() {
		if(!_openonce  || _cyclecount % _maxsize == 0){
			if (_openonce)
				ConfigHelper.getInstance().clearCache(this);
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
