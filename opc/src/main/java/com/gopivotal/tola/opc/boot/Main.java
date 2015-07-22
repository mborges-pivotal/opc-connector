/*=========================================================================
 * Copyright (c) 2010-2014 Pivotal Software, Inc. All rights reserved.
 * This product is protected by U.S. and international copyright
 * and intellectual property laws. Pivotal products are covered by
 * one or more patents listed at http://www.gopivotal.com/patents.
 *=========================================================================
 */
package com.gopivotal.tola.opc.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main - the spring boot application
 * @author mborges
 *
 */
@ComponentScan({"com.gopivotal.tola.opc","com.gopivotal.tola.opc.boot"}) 
@SpringBootApplication
public class Main {
	
    public static void main(String[] args) throws Exception {
        SpringApplication spa = new SpringApplication(Main.class);

        spa.setWebEnvironment(false);
        spa.run(args);
    }

}
