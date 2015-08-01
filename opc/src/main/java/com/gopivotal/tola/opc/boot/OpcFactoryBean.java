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

import javax.annotation.PostConstruct;

import org.jinterop.dcom.common.JIException;
import org.openscada.opc.lib.common.AlreadyConnectedException;
import org.openscada.opc.lib.da.AddFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gopivotal.tola.opc.ConnectionConfiguration;
import com.gopivotal.tola.opc.OpcDaClient;

@Component
public class OpcFactoryBean {
	
	Logger logger = LoggerFactory
			.getLogger("com.gopivotal.tola.opc.boot.OpcFactoryBean");
	
	@Autowired
	private ServerConfigList srvConfigs;
	
	private Map<String, ConnectionConfiguration> servers = new HashMap<String, ConnectionConfiguration>();
	private Map<String, OpcDaClient> connections = new HashMap<String, OpcDaClient>();
	
	// No Args Constructor
	public OpcFactoryBean() {
		logger.info("OpcFactoryBean create");
	}
	
	@PostConstruct
	public void init() {
		for(ConnectionConfiguration config: srvConfigs.getServers()) {
			servers.put(config.getName(), config);
		}		
		listServers();
	}	

	// CREATE CONNECTION
	public boolean createConnection(String name, String server, String pswd) {
		
		ConnectionConfiguration connConfig = servers.get(server);
		if (connConfig == null) {
			logger.info("Could not create connection '{}'. Server '{}' not found.", name, server);	
			return false;
		}
		
		if (pswd != null) {
			connConfig = connConfig.copy(pswd);
		}
		
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
		return true;
	}

	// CREATE SERVER
	public boolean createServer(String name, String parmList, String server) {
		
		Map<String, String> parms = new HashMap<String, String>();
		String[] parmPairs = parmList.split(",");
		for(String pair: parmPairs) {
			String[] parts = pair.split("=");
			parms.put(parts[0], parts[1]);
		}
		
		ConnectionConfiguration connConfig = servers.get(server);
		if (connConfig == null) {
			connConfig = new ConnectionConfiguration();
			connConfig.setPassword(parms.get("password"));
		} else {
			connConfig = connConfig.copy(parms.get("password"));
		}
		
		connConfig.setName(name);
		for(String key: parms.keySet()) {
			if (key.equals("password")) {
				continue; // already set above
			} else if (key.equals("user")) {
				connConfig.setUser(parms.get("user"));
			} else if (key.equals("host")) {
				connConfig.setHost(parms.get("host"));
			} else if (key.equals("domain")) {
				connConfig.setDomain(parms.get("domain"));
			}
		}
		
		servers.put(name, connConfig);
		
		logger.info("Created server '{}'", connConfig);
		return true;
	}
	
	// DUMP ROOT TREE
	public String dumpRootTree(String name, boolean flat, String filter) {
		OpcDaClient opc = connections.get(name);
		if (opc == null) {
			String msg = String.format("connection '%s' not found - NOP", name);
			logger.info(msg);
			return msg;
		}
		return opc.dumpRootTree(filter, flat);
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

	// LIST
	public String list() {
		StringBuffer sb = new StringBuffer();
		sb.append(listServers());
		sb.append(listConnections());
		return sb.toString();
	}
	
	// LIST CONNECTIONS
	private String listConnections() {
		StringBuffer sb = new StringBuffer("*** Connections\n");
		for(String name: connections.keySet()) {
			String line = String.format("%s - %s", name, connections.get(name).getConnConfig());
			sb.append(line + "\n");
			logger.info(line);
		}
		return sb.toString();
	}

	// LIST SERVERS
	private String listServers() {
		StringBuffer sb = new StringBuffer("*** Servers\n");
		for(String name: servers.keySet()) {
			String line = String.format("%s - %s", name, servers.get(name));
			sb.append(line + "\n");
			logger.info(line);
		}
		return sb.toString();
	}

	// LIST OPC SERVERS - list opc servers from a server connection
	public String listOpcServers(String name) {
		StringBuffer sb = new StringBuffer("*** OPC Servers\n");
		OpcDaClient opc = connections.get(name);
		if (opc == null) {
			logger.info("server '{}' not found - NOP", name);
			return "";
		}		
		try {
			sb.append(opc.listOPCServers());
		} catch (IllegalArgumentException | UnknownHostException | JIException e) {
			e.printStackTrace();
		}

		return sb.toString();
	}

	/////////////////////////////////
	// Groups
	/////////////////////////////////
	
	public String addItemToGroup(String name, String gName, String iName) {
		OpcDaClient opc = connections.get(name);
		if (opc == null) {
			logger.info("connection '{}' not found - NOP", name);
			return "";
		}
		return opc.addItemToGroup(gName, iName);
	}
	
	public String removeGroupItem(String name, String gName, String iName) {
		OpcDaClient opc = connections.get(name);
		if (opc == null) {
			logger.info("connection '{}' not found - NOP", name);
			return "";
		}
		return opc.removeGroupItem(gName, iName);		
	}
	public String readGroupItems(String name, String gName) {
		OpcDaClient opc = connections.get(name);
		if (opc == null) {
			logger.info("connection '{}' not found - NOP", name);
			return "";
		}
		return opc.readGroupItems(gName);
	}
	
	public String listGroups(String name) {
		OpcDaClient opc = connections.get(name);
		if (opc == null) {
			logger.info("connection '{}' not found - NOP", name);
			return "";
		}
		return opc.listGroups();
	}

	////////////////////////////////////////////////////////////
	// Helper
	////////////////////////////////////////////////////////////
	
	
}