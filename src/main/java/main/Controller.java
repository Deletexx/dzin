package main;

import afester.javafx.svg.SvgLoader;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.fxmisc.richtext.InlineCssTextArea;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class Controller {

    @FXML
    private Button closeBtn;
    @FXML
    private Button minimizeBtn;
    @FXML
    private ToggleButton chatMenuTgl;
    @FXML
    private ToggleButton addChatPaneTgl;
    @FXML
    private AnchorPane messageBoxContainer;
    @FXML
    private HBox titleBar;
    @FXML
    private VBox chatList;
    @FXML
    private VBox messageStack;
    @FXML
    private ScrollPane chatListScrollPane;
    @FXML
    private AnchorPane addChatMenuPane;
    @FXML
    private TextField usernameInput;
    @FXML
    private TextField nicknameInput;
    @FXML
    private Label chatLabel;

    private ChatList chats;
    private Chat selectedChat = new Chat();
    private double chatListMenuWidth = 250.0;
    private double addChatMenuHeight = 70.0;

    private InlineCssTextArea messageTextArea;

    @FXML
    private void clickClose(ActionEvent event) {
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void clickMinimize(ActionEvent event) {
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.setIconified(true);
    }

    private SvgLoader loader = new SvgLoader();
    private Group svgToGroup(String path, double scaleX, double scaleY){
        Group svgImageCloseBtn = loader.loadSvg(getClass().getResourceAsStream(path));
        Group graphic = new Group(svgImageCloseBtn);
        svgImageCloseBtn.setScaleX(scaleX);
        svgImageCloseBtn.setScaleY(scaleY);
        return graphic;
    }

    private void addMessageToVBox(MessageBlock message) {
        messageStack.getChildren().add(message.getRootPane());
    }

    private void sendMessage(MessageBlock message) {
        selectedChat.messageList.addLast(message);
        addMessageToVBox(message);
    }

    private void initializeMessageTextArea(){
        messageTextArea = new InlineCssTextArea();
        IntegerProperty messageLineCounter = new SimpleIntegerProperty();
        messageTextArea.getStyleClass().add("message_area_input");
        AnchorPane.setLeftAnchor(messageTextArea, 16.0);
        AnchorPane.setRightAnchor(messageTextArea, 16.0);
        AnchorPane.setTopAnchor(messageTextArea, 13.0);
        AnchorPane.setBottomAnchor(messageTextArea, 11.0);
        //Text placeholderText = new Text("Write a message...");
        //messageTextArea.setPlaceholder(placeholderText);
        messageTextArea.setWrapText(true);
        messageTextArea.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                e.consume(); // otherwise a new line will be added to the textArea after the sendFunction() call
                if (e.isShiftDown()) {
                    messageTextArea.appendText(System.getProperty("line.separator"));
                } else {
                    String messageText = messageTextArea.getText();
                    messageTextArea.deletePreviousChar();
                    if(!messageText.isEmpty()) {
                        int lineSeparatorCounter = 0;
                        for(char s : messageText.toCharArray()){
                            if (s == '\n') lineSeparatorCounter++;
                        }
                        if (messageText.length() != lineSeparatorCounter) {
                            if (messageText.endsWith("\n")) {
                                messageText = messageText.substring(0, messageText.length() - 1);
                            }
                            if (messageText.startsWith("\n")) {
                                messageText = messageText.substring(1);
                            }
                            if (e.isControlDown()) {
                                sendMessage(new MessageBlock(messageText, new Date(), false));
                            } else {
                                sendMessage(new MessageBlock(messageText, new Date(), true));
                            }
                            messageTextArea.clear();
                        }
                    }
                }
            }
        });
        messageTextArea.setOnKeyTyped(e ->
            messageLineCounter.set(messageTextArea.getParagraphLinesCount(0) + messageTextArea.getParagraphs().size() - 1)
        );
        double messageBoxContainerHeight = messageBoxContainer.getPrefHeight();
        messageLineCounter.addListener((o, oldVal, newVal) -> {
            double heightEstimate = messageTextArea.getTotalHeightEstimate();
            messageBoxContainer.setPrefHeight((messageBoxContainerHeight - (heightEstimate / newVal.intValue())) + heightEstimate);
        });
        messageBoxContainer.getChildren().add(messageTextArea);
    }

    private void setWindowMovable(Node nodeForScene){
        AtomicReference<Double> xOffset = new AtomicReference<>((double) 0);
        AtomicReference<Double> yOffset = new AtomicReference<>((double) 0);
        nodeForScene.setOnMousePressed(e -> {
            xOffset.set(e.getSceneX());
            yOffset.set(e.getSceneY());
        });

        nodeForScene.setOnMouseDragged(e -> {
            Stage stage = (Stage) nodeForScene.getScene().getWindow();
            stage.setX(e.getScreenX() - xOffset.get());
            stage.setY(e.getScreenY() - yOffset.get());
        });
    }

    private void updateChatList() {
        chatList.getChildren().clear();
        for (Chat chat : chats.chatList) {
            chatList.getChildren().add(chat.getChatPane());
        }
    }

    private void setEnabledAddChatMenu(boolean b) {
        if (b) {
            addChatMenuPane.setPrefWidth(chatListMenuWidth);
            addChatMenuPane.setPrefHeight(addChatMenuHeight);
            addChatPaneTgl.setRotate(45.0);
        } else {
            addChatMenuPane.setPrefWidth(0.0);
            addChatMenuPane.setPrefHeight(0.0);
            addChatPaneTgl.setRotate(0.0);
        }
        usernameInput.setDisable(!b);
        usernameInput.setVisible(b);
        nicknameInput.setDisable(!b);
        nicknameInput.setVisible(b);
    }

    void onChatSelected(Chat chat) {
        selectedChat.setSelectedStyle(false);
        selectedChat = chat;
        selectedChat.setSelectedStyle(true);
        messageStack.getChildren().clear();
        chatLabel.setText("@" + selectedChat.getUsername());
        for(MessageBlock message: selectedChat.messageList) {
            messageStack.getChildren().add(message.getRootPane());
        }
    }

    private void confirmAddChatMenuFields() {
        if (!usernameInput.getText().isEmpty()) {
            Chat chat;
            if (nicknameInput.getText().isEmpty()) {
                chat = new Chat(usernameInput.getText(), usernameInput.getText());
            } else {
                chat = new Chat(usernameInput.getText(), nicknameInput.getText());
            }
            chat.getChatPane().setOnMouseClicked(e -> onChatSelected(chat));
            chats.add(chat);
            usernameInput.clear();
            nicknameInput.clear();
            updateChatList();
        }
    }

    private void initializeChatMenu(){
        chatList.prefWidthProperty().bind(chatListScrollPane.prefWidthProperty());
        chatList.setSpacing(6.0);
        chatMenuTgl.selectedProperty().addListener((o, oldVal, newVal) -> {
            if (newVal) {
                chatListScrollPane.setPrefWidth(chatListMenuWidth);
                if (addChatPaneTgl.isSelected()) {
                    setEnabledAddChatMenu(true);
                }
            } else {
                chatListScrollPane.setPrefWidth(0.0);
                setEnabledAddChatMenu(false);
            }
        });
        addChatPaneTgl.disableProperty().bind(chatMenuTgl.selectedProperty().not());
        addChatPaneTgl.visibleProperty().bind(chatMenuTgl.selectedProperty());
        addChatPaneTgl.selectedProperty().addListener((o, oldVal, newVal) -> {
            if (newVal && chatMenuTgl.isSelected()) {
                setEnabledAddChatMenu(true);
                chatListScrollPane.setPrefHeight(chatListScrollPane.getPrefHeight() - addChatMenuHeight);
            } else if (!newVal) {
                setEnabledAddChatMenu(false);
                chatListScrollPane.setPrefHeight(chatListScrollPane.getPrefHeight() + addChatMenuHeight);
            }
        });
        usernameInput.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                e.consume();
                confirmAddChatMenuFields();
            }
        });
        nicknameInput.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                e.consume();
                confirmAddChatMenuFields();
            }
        });
    }
    private void initializeLoginDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("css/dialog.css").toExternalForm());
        dialog.setTitle("Login");
        dialog.setContentText("Enter your username:");
        dialog.setHeaderText("Login");
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.getDialogPane().setGraphic(new Pane());
        setWindowMovable(dialog.getDialogPane());
        dialog.getDialogPane().getScene().setFill(Color.TRANSPARENT);
        ((Stage)dialog.getDialogPane().getScene().getWindow()).initStyle(StageStyle.TRANSPARENT);
        Optional<String> result = dialog.showAndWait();
        result.ifPresentOrElse(name -> {
            if (!name.isEmpty()) {
                chats = new ChatList(name);
            } else Platform.exit();
        }, Platform::exit);
    }

    @FXML
    public void initialize(){
        closeBtn.setGraphic(svgToGroup("img/x-mark.svg", 0.03, 0.03));
        minimizeBtn.setGraphic(svgToGroup("img/minimize.svg", 0.03, 0.03));
        chatMenuTgl.setGraphic(svgToGroup("img/menu.svg", 0.06, 0.06));
        addChatPaneTgl.setGraphic(svgToGroup("img/add.svg", 0.05, 0.05));
        initializeMessageTextArea();
        setWindowMovable(titleBar);
        initializeChatMenu();
        initializeLoginDialog();
    }
}