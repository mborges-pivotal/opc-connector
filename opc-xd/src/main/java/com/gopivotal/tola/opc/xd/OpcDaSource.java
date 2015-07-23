/*=========================================================================
 * Copyright (c) 2010-2014 Pivotal Software, Inc. All rights reserved.
 * This product is protected by U.S. and international copyright
 * and intellectual property laws. Pivotal products are covered by
 * one or more patents listed at http://www.gopivotal.com/patents.
 *=========================================================================
 */
package com.gopivotal.tola.opc.xd;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.gopivotal.tola.opc.ConnectionConfiguration;
import com.gopivotal.tola.opc.OpcDaClient;
import com.gopivotal.tola.opc.xd.DataCallbackImpl;

public class OpcDaSource implements ApplicationListener<ContextRefreshedEvent>, InitializingBean, DisposableBean {
	
	Logger logger = LoggerFactory
			.getLogger("com.gopivotal.tola.opc.xd.OpcDaSource");

	// no args constructor
	public OpcDaSource() {
		
	}
	
	// Configuration
	private String clsId;
	private String progId;
	
	private String host;
	private String domain;
	private String user;
	private String password;
	
	private String tags;

	private OpcDaClient opc;
	private DataCallbackImpl dcb;
	
	private List<String> events = new ArrayList<String>(100);

	/**
	 * generate - Either an usage_event or dynamic_basket depending on the ratio
	 * @return
	 */
	public String generate() {
				
		if (events.size() <= 0) {
			dcb.drainQueue(events);			
		}
		
		if (events.size() <=0) {
			return null;
		}
		
		String result = events.remove(0);
		
		return result;
	}

	//////////////////////////////////////////
	// Accessor methods
	//////////////////////////////////////////
	

	@Override
	public String toString() {
		return "OpcDaSource [clsId=" + clsId + ", progId=" + progId + ", host="
				+ host + ", domain=" + domain + ", user=" + user
				+ ", password=" + password + ", tags=" + tags + "]";
	}

	public String getClsId() {
		return clsId;
	}

	public void setClsId(String clsId) {
		this.clsId = clsId;
	}

	public String getProgId() {
		return progId;
	}

	public void setProgId(String progId) {
		this.progId = progId;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}
	
	// That method will be called after the context is fully wired
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		logger.debug("onApplicationEvent: {}", event.toString());
	}
	

	@Override
	public void afterPropertiesSet() throws Exception {


		// Connect
		opc = new OpcDaClient();
		ConnectionConfiguration connConfig = new ConnectionConfiguration();
		connConfig.setProgId(progId);
		connConfig.setClsId(clsId);
		connConfig.setDomain(domain);
		connConfig.setHost(host);
		connConfig.setUser(user);
		connConfig.setPassword(password);
		opc.setConnConfig(connConfig);
		dcb = new DataCallbackImpl("OPC_XD");
		opc.dcb = dcb;

		logger.debug("Setting properties - {}",connConfig);

		opc.connect();
		
		// AddTags
		opc.tags = tags.split(",");
		for (String tag : opc.tags) {
			opc.addItem(tag);
		}
		
		// Listen
		opc.start();
	}

	@Override
	public void destroy() throws Exception {
		// Disconnect
		logger.debug("Disconnecting....");
		opc.disconnect();				
		logger.debug("Disconnected");
	}


	
}

