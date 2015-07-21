/*=========================================================================
 * Copyright (c) 2010-2014 Pivotal Software, Inc. All rights reserved.
 * This product is protected by U.S. and international copyright
 * and intellectual property laws. Pivotal products are covered by
 * one or more patents listed at http://www.gopivotal.com/patents.
 *=========================================================================
 */
package com.gopivotal.tola.opc.boot;

import org.openscada.opc.lib.da.DataCallback;
import org.openscada.opc.lib.da.Item;
import org.openscada.opc.lib.da.ItemState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gopivotal.tola.opc.type.Quality;
import com.gopivotal.tola.opc.type.Timestamp;

public class DataCallbackImpl implements DataCallback {

	Logger logger = LoggerFactory
			.getLogger("com.gopivotal.tola.opc.boot.OpcFactoryBean.DataCallbackImpl");
	
	private String name;
	
	public DataCallbackImpl(String name) {
		this.name = name;
	}

	public void changed(Item item, ItemState state) {
		logger.info(dumpItemState(item, state));
	}

	// //// Helper

	public String dumpItemState(final Item item, final ItemState state) {
		return String.format("Connection: %s - Item: %s, Value: %s, Timestamp: %s, Quality: %s", 
				name,
				item.getId(), state.getValue(),
				Timestamp.format(state.getTimestamp()),
				Quality.format(state.getQuality()));
	}
}
