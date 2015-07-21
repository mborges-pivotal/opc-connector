/*=========================================================================
 * Copyright (c) 2010-2014 Pivotal Software, Inc. All rights reserved.
 * This product is protected by U.S. and international copyright
 * and intellectual property laws. Pivotal products are covered by
 * one or more patents listed at http://www.gopivotal.com/patents.
 *=========================================================================
 */
package com.gopivotal.tola.opc;

import java.util.Arrays;

public class ConnectionConfigurationImpl implements IConnectionConfiguration {

	private String[] args;
	
	// 0-host 1-domain 2-user 3-password [4-CLSID|5-ProgId] 6-tagList 7-[async]
	public ConnectionConfigurationImpl(String[] args) {
		this.args = args;
	}
	
	@Override
	public String toString() {
		return "ConnectionConfigurationImpl [args=" + Arrays.toString(args)
				+ "]";
	}

	public String getCLSID() {
		return args[4];
	}

	public String getProgId() {
		// Matrikon.OPC.OPCSniffer
		// Matrikon.OPC.Simulation.1
		return args[5];
	}

	@Override
	public String getHost() {
		return args[0];
	}

	@Override
	public String getDomain() {
		return args[1];
	}

	@Override
	public String getUser() {
		return args[2];
	}

	@Override
	public String getPassword() {
		return args[3];
	}

}
