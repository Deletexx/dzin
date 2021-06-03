package main;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Chat {
    public Chat(String username, String nickname) {
        messageList = new ConcurrentLinkedDeque<>();
        this.username = username;
        this.nickname = nickname;
        chatPane = new AnchorPane();
        paneText = new Label(nickname.equals(username) ? "@" + username : nickname);
        paneText.setAlignment(Pos.CENTER);
        paneText.setTextFill(Color.WHITE);
        paneText.setPadding(new Insets(6, 0,0,3));
        AnchorPane.setLeftAnchor(paneText, 0.0);
        chatPane.getChildren().add(paneText);
        chatPane.getStyleClass().add("chat_menu_pane");
    }
    public Chat() {
        messageList = new ConcurrentLinkedDeque<>();
        chatPane = new AnchorPane();
        this.nickname = "";
        paneText = new Label(this.nickname);
        this.username = "";
    }
    public ConcurrentLinkedDeque<MessageBlock> messageList;
    private Pane chatPane;
    private Label paneText;
    private String username;
    private String nickname;

    public Pane getChatPane() {
        return chatPane;
    }
    public void addMessage(MessageBlock message) {
        messageList.add(message);
    }

    public void setSelectedStyle(boolean b) {
        chatPane.getStyleClass().clear();
        if (b) {
            chatPane.getStyleClass().add("chat_menu_pane_selected");
            paneText.setTextFill(Color.BLACK);
            return;
        }
        chatPane.getStyleClass().add("chat_menu_pane");
        paneText.setTextFill(Color.WHITE);
    }
    public String getUsername() {
        return username;
    }
    public String getNickname() {
        return nickname;
    }
}