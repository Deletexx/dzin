package main;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ChatList {
    public ChatList() {}
    public Chat getChat(String username) {
        return map.getOrDefault(username, null);
    }
    public void add(Chat chat) {
        list.addFirst(chat);
        map.put(chat.getUsername(), chat);
        nicknameMap.put(chat.getUsername(), chat.getNickname());
    }

    public ConcurrentHashMap<String, String> nicknameMap = new ConcurrentHashMap<>();
    public ConcurrentHashMap<String, Chat> map = new ConcurrentHashMap<>();
    public ConcurrentLinkedDeque<Chat> list = new ConcurrentLinkedDeque<>();
}
