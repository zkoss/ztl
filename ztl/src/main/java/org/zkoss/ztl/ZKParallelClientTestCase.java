/* ZKParallelClientTestCase.java

{{IS_NOTE
	Purpose:

	Description:

	History:
		June 5 , 2014 18:50:12 AM , Created by JerryChen
}}IS_NOTE

Copyright (C) 2014 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.ztl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.zkoss.ztl.util.ConfigHelper;

import com.thoughtworks.selenium.SeleniumException;

public class ZKParallelClientTestCase extends ZKClientTestCase {
	// used to decide if we need to restart remote VM
	private static Map<String, Integer> timeoutCount = new HashMap<String, Integer>();
	private final int maxTimeoutCount = ConfigHelper.getInstance().getMaxTimeoutCount();
	// save the name of timeout browser, which can't get connection before timeout
	private final Set<String> emptyUrlSet = new HashSet<String>();
	
	/**
	 * Wait for restarting VM and release connection in the case of VM set to restart
	 */
	public void waitAndRelease(Set<String> browserSet) {
		if(browserSet.size() > 0) {
			try {
				Thread.sleep(ConfigHelper.getInstance().getRestartSleep());
			} catch (InterruptedException e) {}
			
			for (String b : browserSet)
				ConnectionManager.getInstance().releaseRemote(b);
			
			String message = "Case timeout for browser:" + Arrays.toString(browserSet.toArray());
			
			if(emptyUrlSet.size() > 0)
				message += "\nCase timeout for connection acquirement, browser:" + Arrays.toString(emptyUrlSet.toArray()); 
			throw new SeleniumException(message);
		}
	}
	
	/**
	 * Detect if there is a 
	 */
	public void handleTimeout(Set<String> browserSet, long luuid) {
		Iterator<String> iter = browserSet.iterator();
		while (iter.hasNext()) {
			String browser = iter.next();
			System.out.println("kill thread belong to browser:" + browser);
			
			String url = ConnectionManager.getInstance().getOpenedRemote(browser);
			
			// get URL means it got block ....
			if(url != null && isTimeoutToRestart(browser)) {
				System.out.println(getTimeUUID() + "-" + luuid + ":restart browser-" + url);
				restartVM(browser, url);
			} else {
				System.out.println(getTimeUUID() + "-" + luuid + "Can't wait for connection. timeout.");
				emptyUrlSet.add(browser);
				iter.remove();
			}
		}
	}
	
	private void restartVM(String browser, String url) {
		CommandLine cl = new CommandLine("/bin/bash");
		cl.addArgument(ClassLoader.getSystemResource("restartVm.sh").getFile());
		cl.addArgument(browser);
		cl.addArgument(url.replaceAll(".*//", "").replaceAll(":.*", ""));
		try {
			new DefaultExecutor().execute(cl);
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	/**
	 *  We need to restart VM if the times of timeout is greater than maximum.
	 *  @since 2.0.1
	 */
	private boolean isTimeoutToRestart(String browser) {
		final int count = timeoutCount.get(browser) == null ? 1 : timeoutCount.get(browser) + 1;
		
		if(count >= maxTimeoutCount){
			timeoutCount.remove(browser);
			return true;
		} else {
			timeoutCount.put(browser, count);			
			return false;
		}
	}
}
