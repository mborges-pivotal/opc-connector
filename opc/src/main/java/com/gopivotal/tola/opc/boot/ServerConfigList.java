/*=========================================================================
 * Copyright (c) 2010-2014 Pivotal Software, Inc. All rights reserved.
 * This product is protected by U.S. and international copyright
 * and intellectual property laws. Pivotal products are covered by
 * one or more patents listed at http://www.gopivotal.com/patents.
 *=========================================================================
 */
package com.gopivotal.tola.opc.boot;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.gopivotal.tola.opc.ConnectionConfiguration;

@Configuration
@ConfigurationProperties(prefix = "opc")
public class ServerConfigList {

	@Valid
	private List<ConnectionConfiguration> servers = new ArrayList<ConnectionConfiguration>();

	public List<ConnectionConfiguration> getServers() {
		return this.servers;
	}

}
