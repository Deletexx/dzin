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
import model.Message;
import org.fxmisc.richtext.InlineCssTextArea;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
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
    @FXML
    private Label clientLabel;
    @FXML
    private ScrollPane messageScrollPane;

    private ChatList chats = new ChatList();;
    private Chat selectedChat = new Chat();
    private double chatListMenuWidth = 250.0;
    private double addChatMenuHeight = 70.0;

    private InlineCssTextArea messageTextArea;

    @FXML
    private void clickClose(ActionEvent event) {
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        Client.webSocket.close();
        try {
            Client.updateNicknames(new HashMap<>(chats.nicknameMap));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        stage.close();
    }

    @FXML
    private void clickMinimize(ActionEvent event) {
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.setIconified(true);
    }

    private Chat getChatOrElseCreate(String username) {
        return Optional.ofNullable(chats.getChat(username)).orElseGet(() -> {
            Chat chat = new Chat(username, chats.nicknameMap.getOrDefault(username, username));
            chats.add(chat);
            setChatSelectable(chat);
            updateChatList();
            return chat;
        });
    }

    public void addReceivedMessage(Message message) {
        Platform.runLater(() -> {
            MessageBlock messageBlock = MessageBlock.messageToMessageBlock(message, false);
            Chat destinationChat = getChatOrElseCreate(message.getSender());
            destinationChat.addMessage(messageBlock);
            if (selectedChat == destinationChat) {
                addMessageToVBox(messageBlock);
            }
        });
    }

    private void addMessageToVBox(MessageBlock message) {
        messageStack.getChildren().add(message.getRootPane());
        messageScrollPane.setVvalue(1.0);
    }

    private void sendMessage(MessageBlock message) {
        selectedChat.messageList.addLast(message);
        addMessageToVBox(message);
        if (!selectedChat.getUsername().isEmpty()) {
            Client.sendMessage(message.getText(), selectedChat.getUsername());
        }
    }

    private void initializeMessageTextArea() {
        messageTextArea = new InlineCssTextArea();
        IntegerProperty messageLineCounter = new SimpleIntegerProperty();
        messageTextArea.getStyleClass().add("message_area_input");
        AnchorPane.setLeftAnchor(messageTextArea, 16.0);
        AnchorPane.setRightAnchor(messageTextArea, 16.0);
        AnchorPane.setTopAnchor(messageTextArea, 13.0);
        AnchorPane.setBottomAnchor(messageTextArea, 11.0);
        messageTextArea.setWrapText(true);
        messageTextArea.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                e.consume();
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
            messageLineCounter.set(messageTextArea
                    .getParagraphLinesCount(0) + messageTextArea.getParagraphs().size() - 1)
        );
        double messageBoxContainerHeight = messageBoxContainer.getPrefHeight();
        messageLineCounter.addListener((o, oldVal, newVal) -> {
            double heightEstimate = messageTextArea.getTotalHeightEstimate();
            messageBoxContainer
                    .setPrefHeight((messageBoxContainerHeight - (heightEstimate / newVal.intValue())) + heightEstimate);
        });
        messageBoxContainer.getChildren().add(messageTextArea);
    }

    private void setWindowMovable(Node nodeForScene) {
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
        for (Chat chat : chats.list) {
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
        chatLabel.setText("@" + selectedChat.getUsername());
        messageStack.getChildren().clear();
        for(MessageBlock message: selectedChat.messageList) {
            messageStack.getChildren().add(message.getRootPane());
        }
        messageScrollPane.setVvalue(1.0);
    }

    void setChatSelectable(Chat chat) {
        chat.getChatPane().setOnMouseClicked(e -> onChatSelected(chat));
    }

    private void confirmAddChatMenuFields() {
        if (!usernameInput.getText().isEmpty()) {
            Chat chat;
            if (nicknameInput.getText().isEmpty()) {
                chat = new Chat(usernameInput.getText(), usernameInput.getText());
            } else {
                chat = new Chat(usernameInput.getText(), nicknameInput.getText());
            }
            setChatSelectable(chat);
            chats.add(chat);
            usernameInput.clear();
            nicknameInput.clear();
            updateChatList();
        }
    }

    private void initializeChatMenu() {
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

    private void setDialogStyle(Dialog dialog) {
        setWindowMovable(dialog.getDialogPane());
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.getDialogPane().getStylesheets()
                .add(getClass().getResource("css/dialog.css").toExternalForm());
        dialog.getDialogPane().getScene().setFill(Color.TRANSPARENT);
        dialog.getDialogPane().setGraphic(new Pane());
        ((Stage)dialog.getDialogPane().getScene().getWindow()).initStyle(StageStyle.TRANSPARENT);
    }

    private boolean confirmUserCreation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm user creation");
        alert.setHeaderText("Confirm user creation");
        alert.setContentText("Create new user?");
        setDialogStyle(alert);
        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }

    private void callAlertDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(message);
        alert.setHeaderText("");
        alert.setContentText(message);
        setDialogStyle(alert);
        alert.showAndWait();
    }

    private void initializeLoginDialog() {
        AtomicReference<Boolean> userIsVerified = new AtomicReference<>(false);
        do {
            Dialog<Client> dialog = new Dialog<>();
            setDialogStyle(dialog);
            dialog.setTitle("Login");
            dialog.setHeaderText("Enter your credentials");
            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            TextField usernameField = new TextField();
            usernameField.setPromptText("Username");
            TextField passwordField = new TextField();
            passwordField.setPromptText("Password");
            dialogPane.setContent(new VBox(8, usernameField, passwordField));
            Platform.runLater(usernameField::requestFocus);
            dialog.setResultConverter((ButtonType button) -> {
                if (button == ButtonType.OK) {
                    if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                        callAlertDialog("Some fields are empty");
                        return null;
                    }
                    try {
                        return new Client(usernameField.getText(), passwordField.getText());
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
                Platform.exit();
                System.exit(0);
                return null;
            });
            Optional<Client> optionalResult = dialog.showAndWait();
            optionalResult.ifPresent((Client result) -> {
                try {
                    switch(Client.verifyCredentials()) {
                        case USER_VERIFIED:
                            userIsVerified.set(true);
                            break;
                        case WRONG_PASSWORD:
                            callAlertDialog("Wrong username or password");
                            break;
                        case USER_DOES_NOT_EXIST:
                            if (confirmUserCreation()) {
                                Client.createUser();
                                userIsVerified.set(true);
                            }
                            break;
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
        } while (!userIsVerified.get());
    }

    private final SvgLoader loader = new SvgLoader();
    private Group svgToGroup(String path, double scaleX, double scaleY){
        Group svgImageCloseBtn = loader.loadSvg(getClass().getResourceAsStream(path));
        Group graphic = new Group(svgImageCloseBtn);
        svgImageCloseBtn.setScaleX(scaleX);
        svgImageCloseBtn.setScaleY(scaleY);
        return graphic;
    }

    private void initializeMiscUIElements() {
        closeBtn.setGraphic(svgToGroup("img/x-mark.svg", 0.03, 0.03));
        minimizeBtn.setGraphic(svgToGroup("img/minimize.svg", 0.03, 0.03));
        chatMenuTgl.setGraphic(svgToGroup("img/menu.svg", 0.06, 0.06));
        addChatPaneTgl.setGraphic(svgToGroup("img/add.svg", 0.05, 0.05));
        clientLabel.setText("@" + Client.getUsername());
        clientLabel.setStyle("-fx-effect: dropshadow(one-pass-box, black, 4, 1, 0.1, 0.1)");
    }

    private void initializeChatsFromServer() {
        try {
            chats.nicknameMap = Client.getNicknamesFromServer();
            ArrayList<Message> messages = new ArrayList<>(Client.getMessagesFromServer());
            messages.sort(Comparator.comparing(Message::getDate));
            for (Message message : messages) {
                MessageBlock messageBlock;
                if (message.getSender().equals(Client.getUsername())) {
                    messageBlock = MessageBlock.messageToMessageBlock(message, true);
                    getChatOrElseCreate(message.getRecipient()).addMessage(messageBlock);
                } else {
                    messageBlock = MessageBlock.messageToMessageBlock(message, false);
                    getChatOrElseCreate(message.getSender()).addMessage(messageBlock);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        initializeLoginDialog();
        initializeMiscUIElements();
        initializeMessageTextArea();
        setWindowMovable(titleBar);
        initializeChatMenu();
        Client.setController(this);
        initializeChatsFromServer();
        Client.webSocket.connect();
    }
}
