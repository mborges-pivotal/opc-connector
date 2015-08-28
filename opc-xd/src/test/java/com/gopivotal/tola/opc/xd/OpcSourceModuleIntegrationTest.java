package com.gopivotal.tola.opc.xd;

import static org.junit.Assert.*;
import static org.springframework.xd.dirt.test.process.SingleNodeProcessingChainSupport.*;

import org.junit.BeforeClass;
import org.junit.Test;

import org.springframework.xd.dirt.server.singlenode.SingleNodeApplication;
import org.springframework.xd.dirt.test.SingleNodeIntegrationTestSupport;
import org.springframework.xd.dirt.test.SingletonModuleRegistry;
import org.springframework.xd.dirt.test.process.SingleNodeProcessingChainConsumer;
import org.springframework.xd.module.ModuleType;
import org.springframework.xd.test.RandomConfigurationSupport;

/**
 * 
 * @author mborges
 *
 */
public class OpcSourceModuleIntegrationTest {
	private static SingleNodeApplication application;

	private static int RECEIVE_TIMEOUT = 6000;

	/**
	 * Start the single node container, binding random unused ports, etc. to not conflict with any other instances
	 * running on this host. Configure the ModuleRegistry to include the project module.
	 */
	@BeforeClass
	public static void setUp() {
		RandomConfigurationSupport randomConfigSupport = new RandomConfigurationSupport();
		application = new SingleNodeApplication().run();
		SingleNodeIntegrationTestSupport singleNodeIntegrationTestSupport = new SingleNodeIntegrationTestSupport
				(application);
		singleNodeIntegrationTestSupport.addModuleRegistry(new SingletonModuleRegistry(ModuleType.source,
				"opc"));
	}

	@Test
	public void test() {
		String password = "YOUR_PASSWORD";
		SingleNodeProcessingChainConsumer chain = chainConsumer(application, "opcStream",
				String.format("opc --password='%s'", password));

		Object payload = chain.receivePayload(RECEIVE_TIMEOUT);
		assertTrue(payload instanceof String);

		chain.destroy();
	}

}
