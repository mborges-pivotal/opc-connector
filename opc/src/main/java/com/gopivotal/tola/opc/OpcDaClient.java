package com.gopivotal.tola.opc;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.jinterop.dcom.common.JIException;
import org.openscada.opc.dcom.list.ClassDetails;
import org.openscada.opc.lib.common.AlreadyConnectedException;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.opc.lib.common.NotConnectedException;
import org.openscada.opc.lib.da.AccessBase;
import org.openscada.opc.lib.da.AddFailedException;
import org.openscada.opc.lib.da.Async20Access;
import org.openscada.opc.lib.da.DataCallback;
import org.openscada.opc.lib.da.DuplicateGroupException;
import org.openscada.opc.lib.da.Group;
import org.openscada.opc.lib.da.Item;
import org.openscada.opc.lib.da.ItemState;
import org.openscada.opc.lib.da.Server;
import org.openscada.opc.lib.da.SyncAccess;
import org.openscada.opc.lib.da.UnknownGroupException;
import org.openscada.opc.lib.da.browser.BaseBrowser;
import org.openscada.opc.lib.da.browser.Branch;
import org.openscada.opc.lib.da.browser.Leaf;
import org.openscada.opc.lib.da.browser.TreeBrowser;
import org.openscada.opc.lib.list.Categories;
import org.openscada.opc.lib.list.Category;
import org.openscada.opc.lib.list.ServerList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gopivotal.tola.opc.type.Quality;
import com.gopivotal.tola.opc.type.Timestamp;

public class OpcDaClient {

	Logger logger = LoggerFactory
			.getLogger("com.gopivotal.tola.opc.OpcDaClient");

	private int PERIOD_SYNC = 1000;
	private int PERIOD_ASYNC = 1000;
	private boolean INITIAL_REFRESH = false;

	private boolean async;

	private ConnectionConfiguration connConfig;

	public String[] tags;
	
	public DataCallback dcb;

	private ScheduledExecutorService scheduler = Executors
			.newSingleThreadScheduledExecutor();

	private Server server;
	private AccessBase access;
	private Map<String, Map<String, Item>> groups = new HashMap<String, Map<String, Item>>();

	private volatile boolean connected = false;

	// ///////////////////////////////////////////
	// Accessor methods
	// ///////////////////////////////////////////

	public boolean isAsync() {
		return async;
	}

	public void setAsync(boolean async) {
		this.async = async;
	}

	public void setConnConfig(ConnectionConfiguration connConfig) {
		this.connConfig = connConfig;
	}

	public ConnectionConfiguration getConnConfig() {
		return connConfig;
	}

	// CONNECT
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gopivotal.tola.opc.xd.IOpcDaClient#connect()
	 */
	public void connect() throws IllegalArgumentException,
			UnknownHostException, AlreadyConnectedException {
		ConnectionInformation ci = new ConnectionInformation();

		ci.setHost(connConfig.getHost());
		ci.setDomain(connConfig.getDomain());
		ci.setUser(connConfig.getUser());
		ci.setPassword(connConfig.getPassword());
		ci.setProgId(connConfig.getProgId());

		server = new Server(ci, scheduler);

		logger.info("Connecting...");
		try { 
			server.connect();
			createAccessBase();
		} catch (JIException e) {
			// TODO: handle error
			// C000006D: Unknown error (C000006D) - invalid user/password?
			String error = String.format("%08X: %s", e.getErrorCode(),server.getErrorMessage(e.getErrorCode()));
			throw new IllegalStateException(error, e);
		} catch (NotConnectedException e) {
			e.printStackTrace();
		} catch (DuplicateGroupException e) {
			e.printStackTrace();
		} catch (AddFailedException e) {
			e.printStackTrace();
		}
		logger.info("Connected.");

		connected = true;
	}

	/**
	 * ADD_ITEM
	 * 
	 * @param tag
	 */
	public void addItem(String tag) {
		if (connected) {
			try {
				access.addItem(tag, dcb);
			} catch (JIException | AddFailedException e) {
				e.printStackTrace();
				throw new IllegalArgumentException(e);
			}
		}
	}

	public void removeItem(String tag) throws JIException,
			AddFailedException {
		if (connected) {
			access.removeItem(tag);
		}
	}
	
	public void start() {
		if (connected) {
			access.bind();
		}
	}

	public void stop() {
		if (connected) {
			try {
				access.unbind();
			} catch (JIException e) {
				e.printStackTrace();
			}
		}
	}

	// DISCONNECT
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gopivotal.tola.opc.xd.IOpcDaClient#disconnect()
	 */
	public void disconnect() {
		if (connected) {

			//logger.info("server state start time {}", server.getServerState().getStartTime());
			//logger.info("server state group count {}", server.getServerState().getGroupCount());
			if (access != null) {
				try {
					access.unbind();
				} catch (JIException e) {
					e.printStackTrace();
				}
			}

			server.dispose();
			
			// give enough time to cleanup
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		connected = false;
		logger.info("Disconnected");
	}

	/**
	 * DUMP_ROOT_TREE
	 * 
	 * @parma filter - "" or a name
	 * @param flat - browse option flat or hierarchical
	 * 
	 * @return
	 */
	public String dumpRootTree(String filter, boolean flat) {

		if (!connected) {
			logger.info("not connected");
			return "not connected";
		}

		try {
			
            // browse flat
			if (flat) {
				StringBuffer sb = new StringBuffer();
	            final BaseBrowser flatBrowser = server.getFlatBrowser ();
	            if ( flatBrowser != null )
	            {
	                for ( final String item : server.getFlatBrowser ().browse ( filter ) )
	                {
	                	sb.append(item);
	                	sb.append("\n");
	                }
	            }
	            return sb.toString();
			}
		
			
			TreeBrowser tb = server.getTreeBrowser();
			return dumpTree(tb.browse(), 0);

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (JIException e) {
			e.printStackTrace();
		}
		
		return "check logs for error";

	}


	/**
	 * DUMP_ITEM_STATE
	 * 
	 * @param item
	 * @param state
	 * @return
	 */
	public static String dumpItemState(final Item item, final ItemState state) {
		return String.format("Item: %s, Value: %s, Timestamp: %s, Quality: %s",
				item.getId(), state.getValue(),
				Timestamp.format(state.getTimestamp()),
				Quality.format(state.getQuality()));
	}

	/**
	 * DUMP_TREE
	 * 
	 * @param branch
	 * @param level
	 * @return
	 */
	public static String dumpTree(final Branch branch, final int level) {
		
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < level; i++) {
			sb.append("  ");
		}
		final String indent = sb.toString();
		sb.setLength(0);

		for (final Leaf leaf : branch.getLeaves()) {
			sb.append(indent + "Leaf: " + leaf.getName() + " ["
					+ leaf.getItemId() + "]\n");
		}
		for (final Branch subBranch : branch.getBranches()) {
			sb.append(indent + "Branch: " + subBranch.getName());
			sb.append(dumpTree(subBranch, level + 1));
		}
		return sb.toString();
	}

	public String listOPCServers() throws IllegalArgumentException, UnknownHostException, JIException {
		
		StringBuffer sb = new StringBuffer();
		
		//final String host, final String user, final String password, final String domain
        final ServerList serverList = new ServerList ( connConfig.getHost(), connConfig.getUser(), connConfig.getPassword(), connConfig.getDomain() );

        // Getting an individual server detail
        //final String cls = serverList.getClsIdFromProgId ( "Matrikon.OPC.Simulation.1" );
        //ClassDetails cd = serverList.getDetails ( clsid );

        Collection<ClassDetails> detailsList = serverList.listServersWithDetails ( new Category[] { Categories.OPCDAServer20 }, new Category[] {} );
		
		if (detailsList == null) {
			return "No OPC servers found ";
		}

        for ( final ClassDetails details : detailsList )
        {
        	logger.debug(details.toString());
        	
            sb.append ( String.format ( "Found: %s\n", details.getClsId () ) );
            sb.append ( String.format ( "\tProgID: %s\n", details.getProgId () ) );
            sb.append ( String.format ( "\tDescription: %s\n", details.getDescription () ) );
        }
        
        return sb.toString();
	}
	
	
	// ////////////////////////////////////////////////
	// InnerClass DataCallBack
	// /////////////////////////////////////////////////
	private static class DataCallbackImpl implements DataCallback {

		public void changed(Item item, ItemState state) {
			System.out.println(dumpItemState(item, state));

			/*
			 * try { VariantDumper.dumpValue("\t", state.getValue()); } catch
			 * (final JIException e) { e.printStackTrace(); }
			 */
		}
	}
	
	// ////////////////////////////////////////////////
	// helper methods
	// /////////////////////////////////////////////////

	private void createAccessBase() throws IllegalArgumentException,
			UnknownHostException, NotConnectedException, JIException,
			DuplicateGroupException, AddFailedException {
		if (async) {
			asyncRead();
		} else {
			syncRead();
		}
	}

	// SYNC READ
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gopivotal.tola.opc.xd.IOpcDaClient#syncRead()
	 */
	private void syncRead() throws IllegalArgumentException,
			UnknownHostException, NotConnectedException, JIException,
			DuplicateGroupException, AddFailedException {
		access = new SyncAccess(server, PERIOD_SYNC);

		// addItems(dcb);

		// start reading
		// access.bind();

	}

	// ASYNC READ
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gopivotal.tola.opc.xd.IOpcDaClient#asyncRead()
	 */
	private void asyncRead() throws JIException, AddFailedException,
			IllegalArgumentException, UnknownHostException,
			NotConnectedException, DuplicateGroupException {
		access = new Async20Access(server, PERIOD_ASYNC, INITIAL_REFRESH);

	}

	// ////////////////////////////////////////////////
	// Bypass AccessBase - Groups
	// /////////////////////////////////////////////////
	
	public String listGroups() {
		
		StringBuffer sb = new StringBuffer();
		
		for(String group: groups.keySet()) {			
			sb.append(String.format("Group : %s\n", group));
			Map<String, Item> items = groups.get(group);
			for(String item: items.keySet()) {
				sb.append(String.format("\t%s\n", item));
			}
		}
		
		return sb.toString();
	}
	
	public String readGroupItems(String gName) {

		if (!connected) {
			logger.info("not connected");
			return "not connected";
		}
		
		if (!groups.containsKey(gName)) {
			return String.format("Group '%s' not found - NOP", gName);			
		}

		StringBuffer sb = new StringBuffer();
		Map<String, Item> items = groups.get(gName);
		for(Entry<String, Item> entrySet: items.entrySet()) {
			Item item = entrySet.getValue();
			try {
				sb.append(dumpItemState(item, item.read(false)) + "\n");
			} catch (JIException e) {
				e.printStackTrace();
			}
		}
		
		return sb.toString();		
	}
	
	// REMOVE_GROUP_ITEM
	public String removeGroupItem(String gName, String iName) {
		
		if (!connected) {
			logger.info("not connected");
			return "not connected";
		}
		
		Group group = null;
		try {
			group = server.findGroup(gName);
		} catch (IllegalArgumentException | UnknownHostException | JIException | NotConnectedException e1) {
			e1.printStackTrace();
			throw new IllegalStateException(e1);
		} catch (UnknownGroupException e1) {
			e1.printStackTrace();
			return String.format("Group '%s' not found - NOP", gName);
		}
		
		try {
			group.removeItem(iName);
			groups.get(gName).remove(iName);	
			
			if (groups.size() <= 0) {
				server.removeGroup(group, true);
				groups.remove(gName);
				logger.debug("Removing group {}", gName);
			}
			
		} catch (IllegalArgumentException | UnknownHostException | JIException e) {
			e.printStackTrace();
		}
		
		return String.format("Item '%s' removed from group '%s'", iName, gName);
	}
	
	// ADD_ITEM_TO_GROUP
	public String addItemToGroup(String gName, String iName) {

		if (!connected) {
			logger.info("not connected");
			return "not connected";
		}

		boolean groupExist = groups.containsKey(gName);
		
		Group group = null;
		Map<String, Item> items = null;
		
		if (!groupExist) {
			items = new HashMap<String, Item>();
			groups.put(gName, items);
			
			try {
				group = server.addGroup(gName);
				group.setActive(true);
			} catch (IllegalArgumentException | UnknownHostException
					| NotConnectedException | JIException
					| DuplicateGroupException e) {
				e.printStackTrace();
				throw new IllegalStateException(String.format("Could not add group '%s' - %s", gName, e.getMessage()));
			}
		} else {
			items = groups.get(gName);
		} // Groups
				
		try {
			Item item = group.addItem(iName);
			item.setActive(true);
			items.put(iName, item);
		} catch (IllegalArgumentException | JIException e) {
			e.printStackTrace();
		} catch (AddFailedException e) {
			e.printStackTrace();
		} // Item
		
		return String.format("Item '%s' added to group '%s'", iName, gName);

	}
	
	

	// ////////////////////////////////////////////////
	// Main
	// /////////////////////////////////////////////////

	public static void main(final String[] args) throws Exception {

		if (args.length < 6) {
			System.out
					.println("Syntax: OpcDaClient host domain user password [CLSID|ProgId] tagList [async]");
			System.out
					.println(" Where: you can pass either CLSID or ProgID and");
			System.out.println("        tagList is an comma delimited string");
			System.out
					.println("        async communication, defaults to async");
			System.exit(0);
		}

		System.out.println("Args: " + Arrays.toString(args));

		OpcDaClient opc = new OpcDaClient();
		
		opc.connConfig = new ConnectionConfiguration(args);

		System.out.println(opc.listOPCServers());

		if (args.length > 7) {
			opc.async = true;
		}

		// opc.tags = new String[] { ".Temperature","testGroup.t2"};
		opc.dcb = new DataCallbackImpl();

		opc.connect();

		opc.tags = args[6].split(",");
		for (String tag : opc.tags) {
			opc.addItem(tag);
		}
		opc.start();

		// OpcDaClient.dumpTree(opc.server.getTreeBrowser().browse(), 0);

		Thread.sleep(10 * 1000);

		opc.disconnect();

	}

	/*
	 * public static void main2(String[] args) throws Exception {
	 * 
	 * // create connection information final ConnectionInformation ci = new
	 * ConnectionInformation(); ci.setHost("192.168.9.164");
	 * ci.setDomain("CORP"); ci.setUser("borgem"); ci.setPassword("PASSWORD");
	 * ci.setProgId("Matrikon.OPC.Simulation.1"); //
	 * ci.setClsid("680DFBF7-C92D-484D-84BE-06DC3DECCD68"); // if ProgId is //
	 * not working, try it using the Clsid instead // final String itemId =
	 * "_System._Time_Second"; // final String itemId = ".Temperature"; final
	 * String itemId = "testGroup.t2"; // create a new server final Server
	 * server = new Server(ci, Executors.newSingleThreadScheduledExecutor());
	 * 
	 * // final AutoReconnectController autoReconnectController = new //
	 * AutoReconnectController ( server );
	 * 
	 * try {
	 * 
	 * // connect to server System.out.println("Connecting...");
	 * server.connect(); System.out.println("Connectied");
	 * 
	 * // autoReconnectController.connect();
	 * 
	 * // browse dumpTree(server.getTreeBrowser().browse(), 0);
	 * 
	 * // add sync access, poll every 500 ms // final AccessBase access = new
	 * SyncAccess(server, 500);
	 * 
	 * final AccessBase access = new Async20Access(server, 100, false);
	 * 
	 * access.addItem(itemId, new DataCallback() { public void changed(Item
	 * item, ItemState state) { System.out.println(String.format(
	 * "Item: %s, Value: %s, Timestamp: %tc, Quality: %d", item.getId(),
	 * state.getValue(), state.getTimestamp(), state.getQuality()));
	 * 
	 * // USE LOG DEBUG try { VariantDumper.dumpValue("\t", state.getValue()); }
	 * catch (final JIException e) { e.printStackTrace(); } } });
	 * 
	 * // start reading access.bind(); // wait a little bit Thread.sleep(10 *
	 * 1000); // stop reading access.unbind(); } catch (final JIException e) {
	 * System.out.println(String.format("%08X: %s", e.getErrorCode(),
	 * server.getErrorMessage(e.getErrorCode()))); } }
	 */
}