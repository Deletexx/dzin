package main;

import model.ServerResponse;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Client {
    static private Socket socket;
    static private ObjectOutputStream output;
    static private ObjectInputStream input;
    static private PrintWriter writer;

    static private Controller controller;
    static private HttpClient httpClient = HttpClient.newHttpClient();;


    static private String username;
    static private String password;

    static public String uri = "http://127.0.0.1:8080/";

    static public void connect(String hostname, int port) throws IOException {
        socket = new Socket(hostname, port);
        writer = new PrintWriter(socket.getOutputStream());
//        output = new ObjectOutputStream(socket.getOutputStream());
//        input = new ObjectInputStream(socket.getInputStream());
    }

    static public String getUsername() {
        return username;
    }

    static public String getPassword() {
        return password;
    }

    public static void startSocketClient(Controller controller) {
        Client.controller = controller;
        new Thread(Client::run).start();
    }

    public Client(String username, String password) {
        Client.username = username;
        Client.password = password;
    }

    private static ServerResponse sendRequest(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri + path))
                .build();
        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
        return SerializationUtils.deserialize(response.body());
    }

    public static ServerResponse verifyCredentials() throws IOException, InterruptedException {
        return sendRequest(String.format("verify/%s/%s", username, password));
    }

    public static ServerResponse createUser() throws IOException, InterruptedException {
        return sendRequest(String.format("create/%s/%s", username, password));
    }

    static public void run() {
        while (socket.isConnected()) {
            writer.println("hello server");
            writer.flush();
        }
    }
}
