/*=========================================================================
 * Copyright (c) 2010-2014 Pivotal Software, Inc. All rights reserved.
 * This product is protected by U.S. and international copyright
 * and intellectual property laws. Pivotal products are covered by
 * one or more patents listed at http://www.gopivotal.com/patents.
 *=========================================================================
 */
package com.gopivotal.tola.opc.type;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Timestamp {
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS a");
	
	private final Calendar timestamp;
	
	public Timestamp(Calendar timestamp) {
		this.timestamp = timestamp;
	}
	
	public static String format(Calendar timestamp) {
		return sdf.format(timestamp.getTime());
	}

	@Override
	public String toString() {
		return "Timestamp [timestamp=" + sdf.format(timestamp) + "]";
	}

}
