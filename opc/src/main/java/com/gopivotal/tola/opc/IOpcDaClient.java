/*=========================================================================
 * Copyright (c) 2010-2014 Pivotal Software, Inc. All rights reserved.
 * This product is protected by U.S. and international copyright
 * and intellectual property laws. Pivotal products are covered by
 * one or more patents listed at http://www.gopivotal.com/patents.
 *=========================================================================
 */
package com.gopivotal.tola.opc;

import java.net.UnknownHostException;

import org.jinterop.dcom.common.JIException;
import org.openscada.opc.lib.common.AlreadyConnectedException;
import org.openscada.opc.lib.common.NotConnectedException;
import org.openscada.opc.lib.da.AddFailedException;
import org.openscada.opc.lib.da.DataCallback;
import org.openscada.opc.lib.da.DuplicateGroupException;

public interface IOpcDaClient {

	// CONNECT
	public abstract void connect() throws IllegalArgumentException,
			UnknownHostException, AlreadyConnectedException;

	// SYNC READ
	public abstract void syncRead() throws IllegalArgumentException,
			UnknownHostException, NotConnectedException, JIException,
			DuplicateGroupException, AddFailedException;

	// ASYNC READ
	public abstract void asyncRead() throws JIException, AddFailedException,
			IllegalArgumentException, UnknownHostException,
			NotConnectedException, DuplicateGroupException;

	// DISCONNECT
	public abstract void disconnect();

	public abstract boolean isAsync();

	public abstract void setAsync(boolean async);

	public abstract void setDataCallback(DataCallback dcb);

}