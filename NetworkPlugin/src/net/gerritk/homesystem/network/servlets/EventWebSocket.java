package net.gerritk.homesystem.network.servlets;

import net.gerritk.homesystem.events.DoorbellListener;
import net.gerritk.homesystem.events.DoorbellEvent;
import net.gerritk.homesystem.elements.DoorbellService;
import net.gerritk.homesystem.services.ServiceContainer;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import java.io.IOException;

public class EventWebSocket extends WebSocketAdapter implements DoorbellListener {
	@Override
	public void onWebSocketConnect(Session session) {
		System.out.println("SOCKET_CONNECT::" + session.getRemoteAddress());
		super.onWebSocketConnect(session);

		DoorbellService doorbellService = ServiceContainer.getService(DoorbellService.class);
		doorbellService.registerListener(this);
	}

	@Override
	public void onWebSocketError(Throwable cause) {
		cause.printStackTrace(System.err);
		super.onWebSocketError(cause);
	}

	@Override
	public void onWebSocketText(String message) {
		System.out.println("SOCKET_MESSAGE::" + message);
		super.onWebSocketText(message);
	}

	@Override
	public void onWebSocketClose(int statusCode, String reason) {
		System.out.println("SOCKET_CLOSED::[" + statusCode + "] " + reason);
		super.onWebSocketClose(statusCode, reason);

		DoorbellService doorbellService = ServiceContainer.getService(DoorbellService.class);
		doorbellService.unregisterListener(this);
	}

	@Override
	public void onRinging(DoorbellEvent event) {
		if(isConnected()) {
			try {
				RemoteEndpoint remote = getRemote();
				remote.sendString(event.toJSONString());
				remote.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
