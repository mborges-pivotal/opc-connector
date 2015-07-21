/*=========================================================================
 * Copyright (c) 2010-2014 Pivotal Software, Inc. All rights reserved.
 * This product is protected by U.S. and international copyright
 * and intellectual property laws. Pivotal products are covered by
 * one or more patents listed at http://www.gopivotal.com/patents.
 *=========================================================================
 */
package com.gopivotal.tola.opc.boot;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.jinterop.dcom.common.JIException;
import org.openscada.opc.lib.common.AlreadyConnectedException;
import org.openscada.opc.lib.da.AddFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.gopivotal.tola.opc.IConnectionConfiguration;
import com.gopivotal.tola.opc.OpcDaClient;


@Component
public class OpcFactoryBean {
	
	Logger logger = LoggerFactory
			.getLogger("com.gopivotal.tola.opc.boot.OpcFactoryBean");
	
	private Map<String, OpcDaClient> connections = new HashMap<String, OpcDaClient>();
	
	// No Args Constructor
	public OpcFactoryBean() {
		logger.info("OpcFactoryBean create");
	}

	// CREATE CONNECTION
	public void createConnection(String name, IConnectionConfiguration connConfig) {
		OpcDaClient opc = new OpcDaClient();
		opc.setConnConfig(connConfig);
		opc.dcb = new DataCallbackImpl(name);
		try {
			opc.connect();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (AlreadyConnectedException e) {
			e.printStackTrace();
		}
		connections.put(name, opc);
		logger.info("Created connection '{}'", name);
	}
	
	// DUMP ROOT TREE
	public void dumpRootTree(String name) {
		OpcDaClient opc = connections.get(name);
		if (opc == null) {
			logger.info("connection '{}' not found - NOP", name);
			return;
		}
		opc.dumpRootTree();
	}
	
	// ADD TAG
	public void addTag(String name, String tag) throws JIException, AddFailedException {
		OpcDaClient opc = connections.get(name);
		if (opc == null) {
			logger.info("connection '{}' not found - NOP", name);
			return;
		}
		opc.addItem(tag);
	}

	// REMOVE TAG
	public void removeTag(String name, String tag) throws JIException, AddFailedException {
		OpcDaClient opc = connections.get(name);
		if (opc == null) {
			logger.info("connection '{}' not found - NOP", name);
			return;
		}
		opc.removeItem(tag);
	}

	// LISTEN
	public void listen(String name) {
		OpcDaClient opc = connections.get(name);
		if (opc == null) {
			logger.info("connection '{}' not found - NOP", name);
			return;
		}
		opc.start();
	}

	// QUIESCE
	public void quiesce(String name) {
		OpcDaClient opc = connections.get(name);
		if (opc == null) {
			logger.info("connection '{}' not found - NOP", name);
			return;
		}
		opc.stop();
	}	
	// DESTROY CONNECTION
	public void destroyConnection(String name) {
		OpcDaClient opc = connections.get(name);
		if (opc == null) {
			logger.info("connection '{}' not found - NOP", name);
			return;
		}
		opc.disconnect();
		connections.remove(name);
		logger.info("Destroyed connection '{}'", name);
	}
	
	// LIST CONNECTIONS
	public String listConnections() {
		StringBuffer sb = new StringBuffer();
		for(String name: connections.keySet()) {
			String line = String.format("%s - %s", name, connections.get(name).getConnConfig());
			sb.append(line + "\n");
			logger.info(line);
		}
		return sb.toString();
	}

	////////////////////////////////////////////////////////////
	// Helper
	////////////////////////////////////////////////////////////
	
	
}