/* Browsers.java

	Purpose:
		
	Description:
		
	History:
		Oct 21, 2016 15:36:42 , Created by jameschu

Copyright (C) 2016 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.ztl;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface IgnoreBrowsers {
	public String value();
}
