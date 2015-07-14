/*=========================================================================
 * Copyright (c) 2010-2014 Pivotal Software, Inc. All rights reserved.
 * This product is protected by U.S. and international copyright
 * and intellectual property laws. Pivotal products are covered by
 * one or more patents listed at http://www.gopivotal.com/patents.
 *=========================================================================
 */
package com.gopivotal.tola.opc.xd;

import org.springframework.xd.module.options.spi.ModuleOption;

/**
 * TimeGeneratorOptionsMetadata - I'M USING THE PROPERTIES FILE
 * 
 * @author mborges
 *
 */
public class OpcDaOptionsMetadata {
	
	private String host;
	private String domain;
	private String user;
	private String password;

	//private ServerConfiguration serverConfiguration;

	////// HOST
	public String getHost() {
		return host;
	}
	
	@ModuleOption("OPC Server hostname (resolvable) or IP")
	public void setHost(String host) {
		this.host = host;
	}

	////// DOMAIN
	public String getDomain() {
		return domain;
	}

	@ModuleOption("OPC Server windows domain")
	public void setDomain(String domain) {
		this.domain = domain;
	}

	////// USER
	public String getUser() {
		return user;
	}

	@ModuleOption("OPC Server user")
	public void setUser(String user) {
		this.user = user;
	}

	////// PASSWORD
	public String getPassword() {
		return password;
	}

	@ModuleOption("OPC Server password")
	public void setPassword(String password) {
		this.password = password;
	}

}
