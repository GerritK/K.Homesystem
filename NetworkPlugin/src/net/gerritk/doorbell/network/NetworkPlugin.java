package net.gerritk.doorbell.network;

import net.gerritk.doorbell.interfaces.DoorbellPlugin;
import net.gerritk.doorbell.network.servlets.JsonRpcServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;

public class NetworkPlugin implements DoorbellPlugin {
	private Server server;

	@Override
	public boolean initialize() {
		Log.setLog(new NoLogging());

		System.out.println("[NetworkPlugin] Initializing...");
		boolean success = true;

		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");

		context.addServlet(new ServletHolder(new JsonRpcServlet()), "/jsonrpc/*");
		// TODO add websocket servlet
		// TODO add file servlet

		server = new Server(81);
		server.setStopAtShutdown(true);
		server.setHandler(context);

		try {
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
			success = false;
		}

		if (success) {
			System.out.println("[NetworkPlugin] Finished initializing.");
		} else {
			System.err.println("[NetworkPlugin] Initializing failed.");
		}
		return success;
	}

	@Override
	public void dispose() {
		System.out.println("[NetworkPlugin] Disposing...");

		try {
			server.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("[NetworkPlugin] Finished disposing.");
	}

	@Override
	public String toString() {
		return "NetworkPlugin";
	}
}
