/*=========================================================================
 * Copyright (c) 2010-2014 Pivotal Software, Inc. All rights reserved.
 * This product is protected by U.S. and international copyright
 * and intellectual property laws. Pivotal products are covered by
 * one or more patents listed at http://www.gopivotal.com/patents.
 *=========================================================================
 */
package com.gopivotal.tola.opc.xd;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.gopivotal.tola.opc.OpcDaClient;

public class OpcPollingSource implements ApplicationListener<ContextRefreshedEvent> {
	
	// Reusing StringBuilders
	private String CLASS_ID = ""; // we're relying on progid 

	private volatile boolean ready = false;

	private String host;
	private String domain;
	private String username;
	private String password;
	private String progid;
	private String tags;
	
	private OpcDaClient opc;
	
	// no args constructor
	public OpcPollingSource() {
		opc = new OpcDaClient();
	}
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
		opc.host = host;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
		opc.domain = domain;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
		opc.user = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
		opc.password = password;
	}

	public String getProgid() {
		return progid;
	}

	public void setProgid(String progid) {
		this.progid = progid;
		opc.progId = progid;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
		opc.tags = tags.split(",");		
	}

	// //////////////////
	// main - test
	// ////////////////
	public static void main(String[] args) {

		long startTime = System.currentTimeMillis();
		
		long runInMillis = 20000; // 20 secs
		
		OpcPollingSource rg = new OpcPollingSource();
		rg.ready = true;
		
		System.out.println("Generator thread started - " + startTime);
		while(true) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			System.out.println(rg.generate());
			
			if (System.currentTimeMillis() - startTime > runInMillis) {
				break;
			}
		}
	}

	/**
	 * generate - Either an usage_event or dynamic_basket depending on the ratio
	 * @return
	 */
	private String generate() {
		
		while(!ready) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		String result = null;
		
		
		
		return result;
	}
	
	// That method will be called after the context is fully wired
	public void onApplicationEvent(ContextRefreshedEvent event) {
		System.out.println(event);
		ready = true;	
	}
	
}

