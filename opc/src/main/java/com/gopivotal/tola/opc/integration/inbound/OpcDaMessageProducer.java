/*=========================================================================
 * Copyright (c) 2010-2014 Pivotal Software, Inc. All rights reserved.
 * This product is protected by U.S. and international copyright
 * and intellectual property laws. Pivotal products are covered by
 * one or more patents listed at http://www.gopivotal.com/patents.
 *=========================================================================
 */
package com.gopivotal.tola.opc.integration.inbound;

import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;

import org.jinterop.dcom.common.JIException;
import org.openscada.opc.lib.common.AlreadyConnectedException;
import org.openscada.opc.lib.common.NotConnectedException;
import org.openscada.opc.lib.da.AddFailedException;
import org.openscada.opc.lib.da.DataCallback;
import org.openscada.opc.lib.da.DuplicateGroupException;
import org.openscada.opc.lib.da.Item;
import org.openscada.opc.lib.da.ItemState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.integration.context.OrderlyShutdownCapable;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.ErrorMessage;
import org.springframework.xd.tuple.Tuple;

import com.gopivotal.tola.opc.IOpcDaClient;
import com.gopivotal.tola.opc.Timestamp;
import com.gopivotal.tola.opc.Value;

import static org.springframework.xd.tuple.TupleBuilder.tuple;

public class OpcDaMessageProducer extends MessageProducerSupport implements
		OrderlyShutdownCapable, ApplicationListener<ContextRefreshedEvent> {

	Logger logger = LoggerFactory
			.getLogger("com.gopivotal.tola.opc.integration.inbound.OpcDaMessageProducer");

	private boolean active;
	private volatile boolean shuttingDown;
	private final AtomicInteger activeCount = new AtomicInteger();
	
	private DataCallback dcb;

	private IOpcDaClient opc;
	
	// Constructor
	public OpcDaMessageProducer() {
		dcb = new DataCallbackImpl(this);
	}

	// onMessage
	public boolean onMessage(Message<?> message) {
		if (this.shuttingDown) {
			if (logger.isInfoEnabled()) {
				logger.info("Inbound message ignored; shutting down; "
						+ message.toString());
			}
		} else {
			if (message instanceof ErrorMessage) {
				/*
				 * TODO: Look into OPC errors???? Socket errors are sent here so
				 * they can be conveyed to any waiting thread. There's not one
				 * here; simply ignore.
				 */
				return false;
			}
			this.activeCount.incrementAndGet();
			try {
				sendMessage(message);
			} finally {
				this.activeCount.decrementAndGet();
			}
		}
		return false;
	}
	
	//////////////////////////
	// MessageProducerSupport
	//////////////////////////

	@Override
	public void onInit() {
		super.onInit();
	}

	@Override
	protected void doStart() {
		try {
			opc.connect();
			opc.setDataCallback(dcb);
			active = true;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (AlreadyConnectedException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doStop() {
		opc.disconnect();
		active = false;
	}


	//////////////////////////////
	// InnerClass DataCallBack
	//////////////////////////////
	private static class DataCallbackImpl implements DataCallback {
		
		private OpcDaMessageProducer channel;
		
		private DataCallbackImpl(OpcDaMessageProducer channel) {
			this.channel = channel;
		}

		public void changed(Item item, ItemState state) {
			Tuple tuple = tuple().of("tagId", item.getId(), "value",
					new Value(state.getValue()).getVariantValue(), "timestamp", state.getTimestamp(),
					"quality", state.getQuality());
			
			Message<Tuple> message = MessageBuilder.withPayload(tuple).build();
			
			channel.onMessage(message);

		}

	}

	// /////////////////////////////
	// ApplicationListener interface
	// /////////////////////////////

	// That method will be called after the context is fully wired
	public void onApplicationEvent(ContextRefreshedEvent event) {
		try {
			if (opc.isAsync()) {
				opc.asyncRead();
			} else {
				opc.asyncRead();
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (JIException e) {
			e.printStackTrace();
		} catch (AddFailedException e) {
			e.printStackTrace();
		} catch (NotConnectedException e) {
			e.printStackTrace();
		} catch (DuplicateGroupException e) {
			e.printStackTrace();
		}

	}

	// /////////////////////////////
	// OrderlyShutdownCapable interface
	// /////////////////////////////

	public int afterShutdown() {
		logger.debug("afterShutdown");
		this.stop();
		return 0; // activeItems
	}

	public int beforeShutdown() {
		logger.debug("beforeShutdown");
		shuttingDown = true;
		return 0; // activeItems
	}

}
