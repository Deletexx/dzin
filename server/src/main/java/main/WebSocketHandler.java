package main;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import spark.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@WebSocket
public class WebSocketHandler {

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
        String username = "User" + Server.nextUserNumber++;
        Server.userUsernameMap.put(user, username);
        System.out.println("Server " + username + " joined the chat");
        user.getRemote().sendString("hello client");
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        String username = Server.userUsernameMap.get(user);
        Server.userUsernameMap.remove(user);
        System.out.println("Server " + username + " left the chat");
    }

    @OnWebSocketMessage
    public void onMessage(Session user, InputStream stream) {
        System.out.println(Server.userUsernameMap.get(user));
    }
}
