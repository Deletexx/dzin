package main;

import java.util.ArrayList;

public class ChatList {
    public ChatList(String username) {
        this.username = username;
        chatList = new ArrayList();
    }
    private String username;
    public ArrayList<Chat> chatList;
}
