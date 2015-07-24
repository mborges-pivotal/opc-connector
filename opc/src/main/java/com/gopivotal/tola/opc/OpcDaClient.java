package com.gopivotal.tola.opc;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.jinterop.dcom.common.JIException;
import org.openscada.opc.lib.common.AlreadyConnectedException;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.opc.lib.common.NotConnectedException;
import org.openscada.opc.lib.da.AccessBase;
import org.openscada.opc.lib.da.AddFailedException;
import org.openscada.opc.lib.da.Async20Access;
import org.openscada.opc.lib.da.DataCallback;
import org.openscada.opc.lib.da.DuplicateGroupException;
import org.openscada.opc.lib.da.Item;
import org.openscada.opc.lib.da.ItemState;
import org.openscada.opc.lib.da.Server;
import org.openscada.opc.lib.da.SyncAccess;
import org.openscada.opc.lib.da.browser.Branch;
import org.openscada.opc.lib.da.browser.Leaf;
import org.openscada.opc.lib.da.browser.TreeBrowser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gopivotal.tola.opc.type.Quality;
import com.gopivotal.tola.opc.type.Timestamp;

public class OpcDaClient {

	Logger logger = LoggerFactory
			.getLogger("com.gopivotal.tola.opc.OpcDaClient");

	private int PERIOD_SYNC = 500;
	private int PERIOD_ASYNC = 100;
	private boolean INITIAL_REFRESH = false;

	private boolean async;

	private ConnectionConfiguration connConfig;

	public String[] tags;
	
	public DataCallback dcb;

	private ScheduledExecutorService scheduler = Executors
			.newSingleThreadScheduledExecutor();

	private Server server;
	private AccessBase access;

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

	public String dumpRootTree() {

		if (!connected) {
			logger.info("not connected");
			return "not connected";
		}

		try {
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
	public void asyncRead() throws JIException, AddFailedException,
			IllegalArgumentException, UnknownHostException,
			NotConnectedException, DuplicateGroupException {
		access = new Async20Access(server, PERIOD_ASYNC, INITIAL_REFRESH);

	}

	public static String dumpItemState(final Item item, final ItemState state) {
		return String.format("Item: %s, Value: %s, Timestamp: %s, Quality: %s",
				item.getId(), state.getValue(),
				Timestamp.format(state.getTimestamp()),
				Quality.format(state.getQuality()));
	}

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

	// InnerClass DataCallBack
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