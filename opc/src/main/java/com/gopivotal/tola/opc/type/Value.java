/*=========================================================================
 * Copyright (c) 2010-2014 Pivotal Software, Inc. All rights reserved.
 * This product is protected by U.S. and international copyright
 * and intellectual property laws. Pivotal products are covered by
 * one or more patents listed at http://www.gopivotal.com/patents.
 *=========================================================================
 */
package com.gopivotal.tola.opc.type;

import java.io.IOException;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIVariant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class Value {
	
	Logger logger = LoggerFactory
			.getLogger("com.gopivotal.tola.opc.type.Value");


	private final JIVariant variant;

	public Value(JIVariant variant) {
		this.variant = variant;		
	}

	public Object getVariantValue() {
		
		try {
			if (!variant.isArray()) {

				int type = variant.getType();
				switch(type) {
				case JIVariant.VT_BSTR:
					return variant.getObjectAsString2();
				case JIVariant.VT_I1:
					return (int)variant.getObjectAsChar();
				default:
					return variant.getObject();
				}
				
			} else {
				int typeOfArray = variant.getType() & 0x0FFF;
				switch(typeOfArray) {
				case JIVariant.VT_BSTR: 
					logger.debug("String array");
					JIString[] array = (JIString[]) variant.getObjectAsArray().getArrayInstance();
					StringBuffer sb = new StringBuffer();
					for(int i = 0; i < array.length; i++) {
						logger.debug("element = {}",array[i]);
						if (i > 0) {
							sb.append(',');
						}
						sb.append(array[i].getString());
					}
					return sb.toString();
				case JIVariant.VT_BOOL: 
					logger.debug("Boolean array");
					break;
				case JIVariant.VT_I1: 
					logger.debug("Int1 array");
					break;
				}
				logger.debug("Not handling JVariant array yet");
			}
		} catch (JIException e) {
			e.printStackTrace();
		}

		// return null since I probably got an JIException
		return variant.toString();

	}

	public String toValueString() {
		return "Value [variant=" + variant + "]";
	}
	
	/////////////////////////////////////////////////
	
	@Override
	public String toString() {
		return getVariantValue().toString();
	}

	// JSON Serializer
	@SuppressWarnings("serial")
	public static class Serializer extends StdSerializer<Value> {
		public Serializer(Class<Value> t) {
			super(t);
		}

		public void serialize(Value value, JsonGenerator jgen,
				SerializerProvider provider) throws IOException,
				JsonProcessingException {
			jgen.writeString(value.toString());
		}
	}
}
