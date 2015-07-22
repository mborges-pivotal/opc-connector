/*=========================================================================
 * Copyright (c) 2010-2014 Pivotal Software, Inc. All rights reserved.
 * This product is protected by U.S. and international copyright
 * and intellectual property laws. Pivotal products are covered by
 * one or more patents listed at http://www.gopivotal.com/patents.
 *=========================================================================
 */
package com.gopivotal.tola.opc.servers;


/**
 * Graybox simulator
 * 
 * {@link http://www.gray-box.net}
 * @author mborges
 *
 */
public class GrayboxServer implements IServerConfiguration
{
	
	public static final IServerConfiguration INSTANCE = new GrayboxServer();
	
	// SINGLETON
	private GrayboxServer() {	
	}

    public String getCLSID ()
    {
        return "2C2E36B7-FE45-4A29-BF89-9BFBA6A40857";
    }

    public String getProgId ()
    {
        return "Graybox.Simulator";
    }

    public String getVersionedProgId ()
    {
        return "Graybox.Simulator.1";
    }


}