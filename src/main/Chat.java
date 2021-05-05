package main;

import java.util.ArrayList;

public class Chat {
    public Chat(String interlocutorUserName) {
        messageList = new ArrayList<MessageBlock>();
        this.interlocutorUserName = interlocutorUserName;
    }
    public ArrayList<MessageBlock> messageList;
    private String interlocutorUserName;
}