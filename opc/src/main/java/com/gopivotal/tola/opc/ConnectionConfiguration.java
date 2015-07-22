/*=========================================================================
 * Copyright (c) 2010-2014 Pivotal Software, Inc. All rights reserved.
 * This product is protected by U.S. and international copyright
 * and intellectual property laws. Pivotal products are covered by
 * one or more patents listed at http://www.gopivotal.com/patents.
 *=========================================================================
 */
package com.gopivotal.tola.opc;

import org.hibernate.validator.constraints.NotEmpty;

public class ConnectionConfiguration {

	private String name;

	private String clsId;
	private String progId;
	
	@NotEmpty
	private String host;
	private String domain;
	private String user;
	private String password;

	// no args constructor
	public ConnectionConfiguration() {

	}

	// 0-host 1-domain 2-user 3-password [4-CLSID|5-ProgId] 6-tagList 7-[async]
	public ConnectionConfiguration(String[] args) {
		host = args[0];
		domain = args[1];
		user = args[2];
		password = args[3];
		clsId = args[4];
		progId = args[5];
	}
	
	// Copy constructor
	public ConnectionConfiguration copy(String pswd) {
		ConnectionConfiguration config = new ConnectionConfiguration();
		config.host = host;
		config.domain = domain;
		config.user = user;
		config.password = pswd;
		config.clsId = clsId;
		config.progId = progId;
		return config;
	}

	// //////////////////////////////////////
	// accessor methods
	// //////////////////////////////////////

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getHost() {
		return host;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getDomain() {
		return domain;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getUser() {
		return user;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public String getClsId() {
		return clsId;
	}

	public void setClsId(String clsId) {
		this.clsId = clsId;
	}

	public void setProgId(String progId) {
		this.progId = progId;
	}

	public String getProgId() {
		// Matrikon.OPC.OPCSniffer
		// Matrikon.OPC.Simulation.1
		return progId;
	}

	// /////////////////////////

	@Override
	public String toString() {
		return "[clsId=" + clsId
				+ ", progId=" + progId + ", host=" + host + ", domain="
				+ domain + ", user=" + user + ", password=" + password + "]";
	}

}
