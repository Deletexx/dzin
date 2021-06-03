package main;

import model.Message;
import model.ServerResponse;
import static org.apache.commons.lang3.SerializationUtils.*;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Client extends WebSocketClient {

    static public Client webSocket;
    static private Controller controller;
    static private final HttpClient httpClient = HttpClient.newHttpClient();;


    static private String username;
    static private String password;

    static public String IP = "127.0.0.1";
    static public int port = 8080;
    static public String uri = "http://" + IP + ":" + port + "/";

    public Client(String username, String password) throws URISyntaxException {
        super(new URI("ws://" + IP + ":" + port + "/chat"),
                Map.of("username", username, "password", password));
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

    static private <T> T sendRequest(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri + path))
                .build();
        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
        return deserialize(response.body());
    }

    static public ServerResponse verifyCredentials() throws IOException, InterruptedException {
        return sendRequest(String.format("verify/%s/%s", username, password));
    }

    static public ServerResponse createUser() throws IOException, InterruptedException {
        return sendRequest(String.format("create/%s/%s", username, password));
    }

    static public ArrayDeque<Message> getMessagesFromServer() throws IOException, InterruptedException {
        return sendRequest(String.format("messages/%s/%s", username, password));
    }

    static public ConcurrentHashMap<String, String> getNicknamesFromServer() throws IOException, InterruptedException {
        HashMap<String, String> r = sendRequest(String.format("nicknames/%s/%s", username, password));
        return new ConcurrentHashMap<>(r);
    }

    static public void updateNicknames(HashMap<String, String> nicknames)
            throws IOException, InterruptedException {
        httpClient.send(HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofByteArray(serialize(nicknames)))
                .uri(URI.create(uri + String.format("set-nicknames/%s/%s", username, password)))
                .build(), HttpResponse.BodyHandlers.ofString());
    }

    static public void sendMessage(String text, String recipient) {
        webSocket.send(serialize(new Message(text, recipient)));
    }

    public void onMessage(ByteBuffer bytes) {
        controller.addReceivedMessage(deserialize(bytes.array()));
    }

    public void onOpen(ServerHandshake handshake) { }
    public void onMessage(String message) { }
    public void onClose(int code, String reason, boolean remote) { }
    public void onError(Exception ex) { }
}
