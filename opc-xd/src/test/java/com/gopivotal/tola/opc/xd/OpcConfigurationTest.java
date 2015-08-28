/*
 * Copyright 2015 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gopivotal.tola.opc.xd;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.LogManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.PollableChannel;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Receiver
 * 
 * @author mborges
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class OpcConfigurationTest {
	@Autowired
	PollableChannel output;

	@Autowired
	ConfigurableApplicationContext applicationContext;
	
	Logger logger = LoggerFactory
			.getLogger("com.gopivotal.tola.opc.xd.OpcConfigurationTest");

	// trying to remove the java.util.logging
	/*
	private static final LogManager logManager = LogManager.getLogManager();
	static {
		try {
			logManager.readConfiguration(new FileInputStream("src/main/resources/logging.properties"));
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
	*/

	@Test
	public void test() {

		applicationContext.start();
		Message<?> message = output.receive(10000);
		assertNotNull(message);
		assertTrue(message.getPayload() instanceof String);
		logger.info("message: {}", message);
	}
}
