package main;

import java.util.ArrayList;

public class ChatList {
    public ChatList(String username) {
        this.username = username;
        chatList = new ArrayList();
    }
    public void add(String username, String nickname) {
        chatList.add(new Chat(username, nickname));
    }
    private String username;
    public ArrayList<Chat> chatList;
}
