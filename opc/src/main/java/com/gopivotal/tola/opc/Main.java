/*=========================================================================
 * Copyright (c) 2010-2014 Pivotal Software, Inc. All rights reserved.
 * This product is protected by U.S. and international copyright
 * and intellectual property laws. Pivotal products are covered by
 * one or more patents listed at http://www.gopivotal.com/patents.
 *=========================================================================
 */
package com.gopivotal.tola.opc;

import java.io.File;

import org.springframework.beans.DirectFieldAccessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.FileWritingMessageHandler;

/**
 * Main - the purpose is to test the OPC inbound adapter
 * 
 * @author mborges
 *
 */
public class Main {
	
	
	@SuppressWarnings("resource")
	public static void  main(String[] args) throws Exception{
		ApplicationContext context = new ClassPathXmlApplicationContext("/WEB-INF/file-context.xml");
		Main.displayDirectories(context);
		Thread.sleep(5000);
	}	

	public static void displayDirectories(ApplicationContext context) {
		File inDirAlerts = (File) new DirectFieldAccessor(context.getBean("FileAlertsIn.adapter.source",FileReadingMessageSource.class)).getPropertyValue("directory");
		File inDirDiags = (File) new DirectFieldAccessor(context.getBean("DiagsIn.adapter.source",FileReadingMessageSource.class)).getPropertyValue("directory");
		LiteralExpression expression = (LiteralExpression) new DirectFieldAccessor(context.getBean(FileWritingMessageHandler.class)).getPropertyValue("destinationDirectoryExpression");
		File outDir = new File(expression.getValue());
		
		System.out.println("java.version:" + System.getProperty("java.version"));

		System.out.println("===================================================");
		System.out.println("Input directory for alerts is: " + inDirAlerts.getAbsolutePath());
		System.out.println("Input directory for Diags is: " + inDirDiags.getAbsolutePath());
		System.out.println("Output directory for Diags is: " + outDir.getAbsolutePath());
		System.out.println("===================================================");
	}

	

}
