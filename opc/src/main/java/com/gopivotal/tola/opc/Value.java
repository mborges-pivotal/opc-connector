/*=========================================================================
 * Copyright (c) 2010-2014 Pivotal Software, Inc. All rights reserved.
 * This product is protected by U.S. and international copyright
 * and intellectual property laws. Pivotal products are covered by
 * one or more patents listed at http://www.gopivotal.com/patents.
 *=========================================================================
 */
package com.gopivotal.tola.opc;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIVariant;

public class Value {

	private final JIVariant variant;

	public Value(JIVariant variant) {
		this.variant = variant;
	}

	public Object getVariantValue() {

		try {
			if (!variant.isArray()) {
				Object obj = variant.getObject();
				if (obj instanceof JIString) {
					return ((JIString) obj).getString();
				} else {
					return obj;
				}
			} else {
				throw new UnsupportedOperationException(
						"Not handling JVariant array yet");
			}
		} catch (JIException e) {
			e.printStackTrace();
		}

		// return null since I probably got an JIException
		return null;

	}

}
