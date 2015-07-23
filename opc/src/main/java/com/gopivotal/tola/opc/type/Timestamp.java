/*=========================================================================
 * Copyright (c) 2010-2014 Pivotal Software, Inc. All rights reserved.
 * This product is protected by U.S. and international copyright
 * and intellectual property laws. Pivotal products are covered by
 * one or more patents listed at http://www.gopivotal.com/patents.
 *=========================================================================
 */
package com.gopivotal.tola.opc.type;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class Timestamp {

	private static final SimpleDateFormat sdf = new SimpleDateFormat(
			"MM/dd/yyyy HH:mm:ss.SSS a");

	private final Calendar timestamp;

	public Timestamp(Calendar timestamp) {
		this.timestamp = timestamp;
	}

	public static String format(Calendar timestamp) {
		return sdf.format(timestamp.getTime());
	}

	public String toValueString() {
		return "Timestamp [timestamp=" + sdf.format(timestamp) + "]";
	}

	@Override
	public String toString() {
		return sdf.format(timestamp.getTime());
	}

	// JSON Serializer
	@SuppressWarnings("serial")
	public static class Serializer extends StdSerializer<Timestamp> {
		public Serializer(Class<Timestamp> t) {
			super(t);
		}

		public void serialize(Timestamp value, JsonGenerator jgen,
				SerializerProvider provider) throws IOException,
				JsonProcessingException {
			jgen.writeString(value.toString());
		}
	}

}
