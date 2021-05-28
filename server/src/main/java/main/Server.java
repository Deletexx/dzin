package main;

import model.ServerResponse;
import model.User;
import org.apache.commons.lang3.SerializationUtils;
import org.dizitart.no2.Cursor;
import org.dizitart.no2.Document;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteCollection;
import org.dizitart.no2.mapper.JacksonMapper;
import org.dizitart.no2.mapper.NitriteMapper;
import org.dizitart.no2.objects.ObjectRepository;
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.dizitart.no2.sync.data.InfoResponse;
import org.eclipse.jetty.websocket.api.Session;
import org.json.simple.JSONObject;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;
import static org.dizitart.no2.Document.createDocument;


import static spark.Spark.*;

public class Server {
    static Map<Session, String> userUsernameMap = new ConcurrentHashMap<>();
    static int nextUserNumber = 1;

    static Nitrite db = Nitrite.builder()
            .filePath("data.db")
            .openOrCreate();
    static NitriteCollection collectionMessages = db.getCollection("messages");
    static NitriteCollection collectionUsers = db.getCollection("users");
    static ObjectRepository<User> repositoryUsers = db.getRepository(User.class);

    private static ServerResponse verifyUser(spark.Request request) {
        User user = repositoryUsers.find(ObjectFilters.eq("username", request.params("username"))).firstOrDefault();
        if (user == null) {
            return ServerResponse.USER_DOES_NOT_EXIST;
        } else if (user.isExpectedPassword(request.params("password"))) {
            return ServerResponse.USER_VERIFIED;
        }
        return ServerResponse.WRONG_PASSWORD;
    }

    public static void main(String[] args) {

        //Cursor cursor = collectionUsers.find();
//        Document doc = createDocument("firstName", "John").put("b", new HashMap());
        //collection.insert(doc);

        port(8080);
        webSocket("/chat", WebSocketHandler.class);
        get("/from/*/to/*", (request, response) -> {
            return  "Hello " + request.splat()[0] + " and " + request.splat()[1];
        });
        get("/verify/:username/:password", (request, response) -> SerializationUtils.serialize(verifyUser(request)));
        get("/create/:username/:password", (request, response) -> {
            if (verifyUser(request) == ServerResponse.USER_DOES_NOT_EXIST) {
                User user = new User();
                user.setUsername(request.params("username"));
                user.setPasswordSalt(UUID.randomUUID().toString());
                user.setSaltedPassword(request.params("password"));
                repositoryUsers.insert(user);
                return SerializationUtils.serialize(ServerResponse.USER_CREATED_SUCCESSFULLY);
            }
            return SerializationUtils.serialize(ServerResponse.USER_ALREADY_EXIST);
        });
    }
    public static void broadcastMessage(String sender, String message) {
        userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
                session.getRemote().sendString(String.valueOf(new JSONObject()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}


