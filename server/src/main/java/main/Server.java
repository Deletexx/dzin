package main;

import model.Message;
import model.ServerResponse;
import model.User;
import static org.apache.commons.lang3.SerializationUtils.*;
import org.dizitart.no2.Cursor;
import org.dizitart.no2.Document;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteCollection;
import org.dizitart.no2.mapper.JacksonMapper;
import org.dizitart.no2.mapper.NitriteMapper;
import org.dizitart.no2.objects.ObjectFilter;
import org.dizitart.no2.objects.ObjectRepository;
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.dizitart.no2.sync.data.InfoResponse;
import org.eclipse.jetty.util.HostMap;
import org.eclipse.jetty.websocket.api.Session;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;
import static org.dizitart.no2.Document.createDocument;


import static spark.Spark.*;

public class Server {
    static Map<String, Session> userUsernameMap = new ConcurrentHashMap<>();
    static Map<Session, String> userSessionMap = new ConcurrentHashMap<>();

    static Nitrite db = Nitrite.builder()
            .filePath("data.db")
            .openOrCreate();

    static ObjectRepository<Message> repositoryMessages = db.getRepository(Message.class);
    static ObjectRepository<User> repositoryUsers = db.getRepository(User.class);

    private static ServerResponse verifyUser(spark.Request request) {
        User user = repositoryUsers.find(ObjectFilters
                .eq("username", request.params("username"))).firstOrDefault();
        if (user == null) {
            return ServerResponse.USER_DOES_NOT_EXIST;
        } else if (user.isExpectedPassword(request.params("password"))) {
            return ServerResponse.USER_VERIFIED;
        }
        return ServerResponse.WRONG_PASSWORD;
    }

    public static void main(String[] args) {

        port(8080);
        webSocket("/chat", WebSocketHandler.class);
        get("/verify/:username/:password", (request, response) -> serialize(verifyUser(request)));
        get("/messages/:username/:password", (request, response) -> {
            if (verifyUser(request) == ServerResponse.USER_VERIFIED) {
                String username = request.params("username");
                ArrayDeque<Message> messages = new ArrayDeque<>(repositoryMessages.find(ObjectFilters.or(
                        ObjectFilters.eq("sender", username),
                        ObjectFilters.eq("recipient", username))).toList());
                return serialize(messages);
            }
            return null;
        });
        get("/create/:username/:password", (request, response) -> {
            if (verifyUser(request) == ServerResponse.USER_DOES_NOT_EXIST) {
                User user = new User();
                user.setUsername(request.params("username"));
                user.setPasswordSalt(UUID.randomUUID().toString());
                user.setSaltedPassword(request.params("password"));
                repositoryUsers.insert(user);
                return serialize(ServerResponse.USER_CREATED_SUCCESSFULLY);
            }
            return serialize(ServerResponse.USER_ALREADY_EXIST);
        });
        get("/nicknames/:username/:password", (request, response) -> {
            if (verifyUser(request) == ServerResponse.USER_VERIFIED) {
                String username = request.params("username");
                User user = repositoryUsers.find(ObjectFilters.eq("username", username)).firstOrDefault();
                return serialize(user.getUserAliases() != null ? user.getUserAliases() : new HashMap<>());
            }
            return null;
        });
        put("/set-nicknames/:username/:password", (request, response) -> {
            if (verifyUser(request) == ServerResponse.USER_VERIFIED) {
                String username = request.params("username");
                User user = repositoryUsers.find(ObjectFilters.eq("username", username)).firstOrDefault();
                user.setUserAliases(deserialize(request.bodyAsBytes()));
                repositoryUsers.update(user);
            }
            return null;
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            stop();
            db.close();
            System.out.println("Server was stopped");
        }));
    }
}


