package main;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.util.Date;

public class MessageBlock {
    public MessageBlock(String text, Date date, boolean isClient) {
        this.text = text;
        this.date = date;
        this.isClient = isClient;
        anchorPane = new AnchorPane();
        Label textLabel = new Label(text);
        if (isClient) {
            anchorPane.getChildren().add(textLabel);
            textLabel.setPadding(new Insets(8, 50, 8, 15));
        } else {
            AnchorPane.setRightAnchor(textLabel, 0.0);
            textLabel.setPadding(new Insets(8, 15, 8, 50));
        }
        anchorPane.getChildren().add(textLabel);
    }
    private String text;
    private Date date;
    private AnchorPane anchorPane;
    private boolean isClient;
    public AnchorPane getRootPane() {
        return anchorPane;
    }
}
