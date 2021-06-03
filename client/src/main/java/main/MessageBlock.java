package main;


import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import model.Message;

import java.util.Date;

public class MessageBlock {
    public MessageBlock(String text, Date date, boolean isClient) {
        this.text = text;
        this.date = date;
        this.isClient = isClient;
        anchorPane = new AnchorPane();
        Text messageText = new Text('\n' + text);
        messageText.setWrappingWidth(560.0);
        messageText.wrappingWidthProperty().bind(anchorPane.widthProperty().subtract(45.0));
        messageText.setLineSpacing(2.0);
        messageText.setStyle("-fx-font-size: 13");
        Pane textPane = new Pane();
        AnchorPane.setLeftAnchor(textPane, 23.0);
        AnchorPane.setTopAnchor(textPane, 5.0);
        textPane.getChildren().add(messageText);
        if (isClient) {
            anchorPane.setStyle("-fx-background-color: white");
        } else {
            messageText.setFill(Color.WHITE);
            anchorPane.setStyle("-fx-background-color: #414141");
        }
        anchorPane.getStyleClass().add("message_container");
        anchorPane.prefHeightProperty().bind(textPane.prefHeightProperty());
        anchorPane.getChildren().add(textPane);
    }
    private String text;
    private Date date;
    private AnchorPane anchorPane;
    private boolean isClient;
    public String getText() {
        return text;
    }
    public AnchorPane getRootPane() {
        return anchorPane;
    }
    static public MessageBlock messageToMessageBlock(Message message, boolean isClient) {
        return new MessageBlock(message.getMessage(), message.getDate(), isClient);
    }
}
