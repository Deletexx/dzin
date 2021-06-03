package main;

import model.Message;
import model.User;
import static org.apache.commons.lang3.SerializationUtils.*;
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;

@WebSocket
public class WebSocketHandler {

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
        String username = user.getUpgradeRequest().getHeader("username");
        String password = user.getUpgradeRequest().getHeader("password");

        User u = Server.repositoryUsers.find(ObjectFilters.eq("username",
                username)).firstOrDefault();
        if (u == null || !u.isExpectedPassword(password) ||
                Server.userUsernameMap.containsKey(username)) {
            user.close(403, "Wrong credentials");
        } else {
            Server.userUsernameMap.put(username, user);
            Server.userSessionMap.put(user, username);
            System.out.println("User " + username + " joined the chat");
        }
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        String username = Server.userSessionMap.get(user);
        Server.userUsernameMap.remove(username);
        Server.userSessionMap.remove(user);
        System.out.println("User " + username + " left the chat");
    }

    @OnWebSocketMessage
    public void onMessage(Session user, byte[] buf, int offset, int length) {
        Message message = deserialize(buf);
        message.setSender(Server.userSessionMap.get(user));
        System.out.printf("Message \"%s\" is sent from %s to %s%n", message.getMessage(),
                message.getSender(), message.getRecipient());
        message.setDate(new Date());
        Server.repositoryMessages.insert(message);
        if (Server.userUsernameMap.containsKey(message.getRecipient())) {
            try {
                Server.userUsernameMap.get(message.getRecipient()).getRemote()
                        .sendBytes(ByteBuffer.wrap(serialize(message)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
