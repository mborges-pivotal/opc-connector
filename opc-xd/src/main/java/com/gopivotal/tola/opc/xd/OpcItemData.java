/*=========================================================================
 * Copyright (c) 2010-2014 Pivotal Software, Inc. All rights reserved.
 * This product is protected by U.S. and international copyright
 * and intellectual property laws. Pivotal products are covered by
 * one or more patents listed at http://www.gopivotal.com/patents.
 *=========================================================================
 */
package com.gopivotal.tola.opc.xd;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ser.StdSerializerProvider;
import org.openscada.opc.lib.da.Item;
import org.openscada.opc.lib.da.ItemState;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.gopivotal.tola.opc.type.Quality;
import com.gopivotal.tola.opc.type.Timestamp;
import com.gopivotal.tola.opc.type.Value;

/**
 * OpcItemData 
 * 
 * @author mborges
 *
 */
public class OpcItemData {
	
	private static final String JSON_ERROR = "{\"ERROR\":\"JSON CONVERSION\"}";
	
	private String id;
	private Value value;
	private Timestamp timestamp;
	private Quality quality;
	
	// no args contructor
	public OpcItemData() {
		
	}
	
	public OpcItemData(final Item item, final ItemState state) {
		id = item.getId();
		value = new Value(state.getValue());
		timestamp = new Timestamp(state.getTimestamp());
		quality = new Quality(state.getQuality());
	}

	/////////////////////////////////////////////
	// Accessor methods
	//////////////////////////////////////////
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Value getValue() {
		return value;
	}

	public void setValue(Value value) {
		this.value = value;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public Quality getQuality() {
		return quality;
	}

	public void setQuality(Quality quality) {
		this.quality = quality;
	}
	
	/////////////////////////////////////////////////

	@Override
	public String toString() {
		return "OpcItemData [id=" + id + ", value='" + value + "', timestamp="
				+ timestamp + ", quality=" + quality + "]";
	}
	
	public String toJsonString() {
		ObjectMapper mapper = new ObjectMapper();		
		SimpleModule testModule = new SimpleModule("MyModule");
		testModule.addSerializer(new Timestamp.Serializer(Timestamp.class)); // assuming serializer declares correct class to bind to
		testModule.addSerializer(new Value.Serializer(Value.class)); // assuming serializer declares correct class to bind to
		testModule.addSerializer(new Quality.Serializer(Quality.class)); // assuming serializer declares correct class to bind to
		mapper.registerModule(testModule);

		try {
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return JSON_ERROR;
	}
	

}
