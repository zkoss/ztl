/* ZKRemoteWebDriver.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jul 20, 2011 4:58:25 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
 */
package org.zkoss.ztl.webdriver;

import java.net.URL;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * An extended RemoteWebDriver for screenshot function
 * @author jumperchen
 * 
 */
public class ZKRemoteWebDriver extends RemoteWebDriver implements
		TakesScreenshot {
	protected ZKRemoteWebDriver() {
		super();
	}

	public ZKRemoteWebDriver(CommandExecutor executor,
			Capabilities desiredCapabilities) {
		super(executor, desiredCapabilities);
	}
	
	public ZKRemoteWebDriver(URL remoteAddress, Capabilities desiredCapabilities) {
		this(new HttpCommandExecutor(remoteAddress), desiredCapabilities);
	}

	public <X> X getScreenshotAs(OutputType<X> target)
			throws WebDriverException {
		if ((Boolean) getCapabilities().getCapability(
				CapabilityType.TAKES_SCREENSHOT)) {
			return target
					.convertFromBase64Png(execute(DriverCommand.SCREENSHOT)
							.getValue().toString());
		}
		return null;
	}

}
