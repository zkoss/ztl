/* Comparator.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mar 15, 2011 9:37:11 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.ztl.util.image;

import java.awt.image.BufferedImage;

/**
 * An image comparator to compare two images.
 * @author jumperchen
 */
public interface Comparator {
	/**
	 * Return whether the two images are the same. If null, the images are the same,
	 * otherwise, the returned image is the change indicator.
	 * @param b1 the source of the base image
	 * @param b2 the compared image.
	 */
	public BufferedImage compare(BufferedImage b1, BufferedImage b2);
}
