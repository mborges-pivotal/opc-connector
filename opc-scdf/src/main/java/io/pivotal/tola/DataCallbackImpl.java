/*=========================================================================
 * Copyright (c) 2010-2014 Pivotal Software, Inc. All rights reserved.
 * This product is protected by U.S. and international copyright
 * and intellectual property laws. Pivotal products are covered by
 * one or more patents listed at http://www.gopivotal.com/patents.
 *=========================================================================
 */
package io.pivotal.tola;

import com.gopivotal.tola.opc.type.Quality;
import com.gopivotal.tola.opc.type.Timestamp;
import org.openscada.opc.lib.da.DataCallback;
import org.openscada.opc.lib.da.Item;
import org.openscada.opc.lib.da.ItemState;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class DataCallbackImpl implements DataCallback {

	// buffering OPC events
	private int BUFFER_COUNT = 1000;
	private BlockingQueue<OpcItemData> bq = new ArrayBlockingQueue<OpcItemData>(BUFFER_COUNT);
		
	private String name;
	
	public DataCallbackImpl(String name) {
		this.name = name;
	}

	public void changed(Item item, ItemState state) {
		OpcItemData event = new OpcItemData(item, state);
		System.out.println( event.toString() );
		bq.offer(event);
	}
	
	public void drainQueue(List<OpcItemData> events) {
		bq.drainTo(events, 100);
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
