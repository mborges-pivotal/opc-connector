/*=========================================================================
 * Copyright (c) 2010-2014 Pivotal Software, Inc. All rights reserved.
 * This product is protected by U.S. and international copyright
 * and intellectual property laws. Pivotal products are covered by
 * one or more patents listed at http://www.gopivotal.com/patents.
 *=========================================================================
 */
package com.gopivotal.tola.opc.boot;

import java.net.UnknownHostException;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIClsid;
import org.jinterop.dcom.core.JIComServer;
import org.jinterop.dcom.core.JISession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gopivotal.tola.opc.ConnectionConfiguration;

@Component
public class OpcDCOMClient {
	
    private static Logger logger = LoggerFactory.getLogger ( OpcDCOMClient.class );

    private JISession session;
    private JIComServer comServer;
    
    private volatile boolean connected = false;
    
    public void test(ConnectionConfiguration conn, String password) {
    	
    	try {
			connect(conn, password);
		} catch (UnknownHostException | JIException e) {
			e.printStackTrace();
		}
    	
    	logger.info("Waiting 10 seconds before disconnecting");
    	
    	try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	
    	disconnect();
    	
    }
    
    // CONNECT
    private void connect(ConnectionConfiguration conn, String password) throws UnknownHostException, JIException {
    	
    	if (connected) {
    		return;
    	}
    	
        final int socketTimeout = Integer.getInteger ( "rpc.socketTimeout", 0 );
        logger.info ("Socket timeout: {} ", socketTimeout);
    	
        session = JISession.createSession ( conn.getDomain (), conn.getUser (), password );
        session.setGlobalSocketTimeout ( socketTimeout );
        comServer = new JIComServer ( JIClsid.valueOf ( conn.getClsId() ), conn.getHost (), this.session );
        //comServer = new JIComServer ( JIProgId.valueOf ( conn.getProgId () ), conn.getHost (), this.session );
        
        connected = true;

    }
    
    // DISCONNECT
    /**
     * cleanup after the connection is closed
     */
    protected void disconnect ()
    {
    	
    	if (!connected) {
    		return;
    	}
    	
        logger.info ( "Destroying DCOM session..." );
        final JISession destructSession = this.session;
        final Thread destructor = new Thread ( new Runnable () {

            @Override
            public void run ()
            {
                final long ts = System.currentTimeMillis ();
                try
                {
                    logger.debug ( "Starting destruction of DCOM session" );
                    JISession.destroySession ( destructSession );
                    logger.info ( "Destructed DCOM session" );
                }
                catch ( final Throwable e )
                {
                    logger.warn ( "Failed to destruct DCOM session", e );
                }
                finally
                {
                    logger.info ( "Session destruction took {} ms", System.currentTimeMillis () - ts);
                }
            }
        }, "DCOMSessionDestructor" );
        destructor.setName ( "OPCSessionDestructor" );
        destructor.setDaemon ( true );
        destructor.start ();
        logger.info ( "Destroying DCOM session... forked" );

        session = null;
        comServer = null;
        
        connected = false;

    }

}
