package main;

import model.ServerResponse;
import org.apache.commons.lang3.SerializationUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;

public class Client extends WebSocketClient {

    static public Client webSocket;
    static private Controller controller;
    static private HttpClient httpClient = HttpClient.newHttpClient();;


    static private String username;
    static private String password;

    static public String uri = "http://127.0.0.1:8080/";

    public Client(String username, String password) throws URISyntaxException {
        super(new URI("ws://localhost:8080/chat/"));
        Client.username = username;
        Client.password = password;
        webSocket = this;
    }

    static public String getUsername() {
        return username;
    }

    static public String getPassword() {
        return password;
    }

    static public void setController(Controller controller) {
        Client.controller = controller;
    }

    static private ServerResponse sendRequest(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri + path))
                .build();
        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
        return SerializationUtils.deserialize(response.body());
    }

    static public ServerResponse verifyCredentials() throws IOException, InterruptedException {
        return sendRequest(String.format("verify/%s/%s", username, password));
    }

    static public ServerResponse createUser() throws IOException, InterruptedException {
        return sendRequest(String.format("create/%s/%s", username, password));
    }

    public void onMessage(ByteBuffer bytes) { }

    public void onOpen(ServerHandshake handshake) { System.out.println("Hello client");}
    public void onMessage(String message) { }
    public void onClose(int code, String reason, boolean remote) { }
    public void onError(Exception ex) { }
}
