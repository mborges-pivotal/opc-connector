/*=========================================================================
 * Copyright (c) 2010-2014 Pivotal Software, Inc. All rights reserved.
 * This product is protected by U.S. and international copyright
 * and intellectual property laws. Pivotal products are covered by
 * one or more patents listed at http://www.gopivotal.com/patents.
 *=========================================================================
 */
package com.gopivotal.tola.opc.servers;


/**
 * MatrikonServer Information
 * 
 * {@link http://www.matrikonopc.com/}
 * 
 * @author mborges
 *
 */
public class MatrikonServer implements IServerConfiguration
{
	
	public static final IServerConfiguration INSTANCE = new MatrikonServer();
	
	// SINGLETON
	private MatrikonServer() {	
	}

    public String getCLSID ()
    {
        return "F8582CF2-88FB-11D0-B850-00C0F0104305";
    }

    public String getProgId ()
    {
        return "Matrikon.OPC.Simulation";
    }

    public String getVersionedProgId ()
    {
        return "Matrikon.OPC.Simulation.1";
    }


}