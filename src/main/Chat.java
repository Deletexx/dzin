package main;

import java.util.ArrayList;

public class Chat {
    public Chat(String username, String nickname) {
        messageList = new ArrayList();
        this.username = username;
        this.nickname = nickname;
    }
    public ArrayList<MessageBlock> messageList;
    private String username;
    private String nickname;
}