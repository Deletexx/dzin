package main;

import java.util.LinkedList;

public class ChatList {
    public ChatList() {
        chatList = new LinkedList();
    }
    public void add(String username, String nickname) {
        chatList.addFirst(new Chat(username, nickname));
    }
    public void add(Chat chat) {
        chatList.addFirst(chat);
    }
    public LinkedList<Chat> chatList;
}
