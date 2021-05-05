package main;

import afester.javafx.svg.SvgLoader;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.fxmisc.richtext.InlineCssTextArea;

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

    private ChatList chats;
    private double chatListMenuWidth = 350.0;
    private double addChatMenuHeight = 70.0;

    private double xOffset = 0;
    private double yOffset = 0;
    private InlineCssTextArea messageTextArea;
    private IntegerProperty messageLineCounter = new SimpleIntegerProperty();

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
    @FXML
    private void addNewChatPane(ActionEvent event) {

    }

    @FXML
    private void clickMenu(ActionEvent event) {
    }

    private SvgLoader loader = new SvgLoader();
    private Group svgToGroup(String path, double scaleX, double scaleY){
        Group svgImageCloseBtn = loader.loadSvg(getClass().getResourceAsStream(path));
        Group graphic = new Group(svgImageCloseBtn);
        svgImageCloseBtn.setScaleX(scaleX);
        svgImageCloseBtn.setScaleY(scaleY);
        return graphic;
    }
    private void initializeMessageTextArea(){
        messageTextArea = new InlineCssTextArea();
        messageTextArea.getStyleClass().add("message_area_input");
        AnchorPane.setLeftAnchor(messageTextArea, 16.0);
        AnchorPane.setRightAnchor(messageTextArea, 16.0);
        AnchorPane.setTopAnchor(messageTextArea, 13.0);
        AnchorPane.setBottomAnchor(messageTextArea, 11.0);
        messageTextArea.setWrapText(true);
        messageTextArea.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                e.consume(); // otherwise a new line will be added to the textArea after the sendFunction() call
                if (e.isShiftDown()) {
                    messageTextArea.appendText(System.getProperty("line.separator"));
                } else {
                    messageTextArea.deletePreviousChar();
                    if(!messageTextArea.getText().isEmpty()) {
                        int lineSeparatorCounter = 0;
                        for(char s : messageTextArea.getText().toCharArray()){
                            if (s == '\n') lineSeparatorCounter++;
                        }
                        if (messageTextArea.getText().length() != lineSeparatorCounter) {
                            //sendFunction();
                            System.out.println(messageTextArea.getText());
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

    private void setWindowMovable(){
        titleBar.setOnMousePressed(e -> {
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });

        titleBar.setOnMouseDragged(e -> {
            Stage stage = (Stage) titleBar.getScene().getWindow();
            stage.setX(e.getScreenX() - xOffset);
            stage.setY(e.getScreenY() - yOffset);
        });
    }
    private void initializeChatMenu(){
        chatList.prefWidthProperty().bind(chatListScrollPane.prefWidthProperty());
        chatMenuTgl.selectedProperty().addListener((o, oldVal, newVal) -> {
            if (newVal) {
                chatListScrollPane.setPrefWidth(chatListMenuWidth);
                if (addChatPaneTgl.isSelected()) {
                    addChatMenuPane.setPrefWidth(chatListMenuWidth);
                    addChatMenuPane.setPrefHeight(addChatMenuHeight);
                }
            } else {
                chatListScrollPane.setPrefWidth(0.0);
                addChatMenuPane.setPrefWidth(0.0);
                addChatMenuPane.setPrefHeight(0.0);
            }
        });
        addChatPaneTgl.disableProperty().bind(chatMenuTgl.selectedProperty().not());
        addChatPaneTgl.visibleProperty().bind(chatMenuTgl.selectedProperty());
        addChatPaneTgl.selectedProperty().addListener((o, oldVal, newVal) -> {
            if (newVal && chatMenuTgl.isSelected()) {
                addChatMenuPane.setPrefWidth(chatListMenuWidth);
                addChatMenuPane.setPrefHeight(addChatMenuHeight);
                chatListScrollPane.setPrefHeight(chatListScrollPane.getPrefHeight() - addChatMenuHeight);
            } else if (!newVal) {
                addChatMenuPane.setPrefWidth(0.0);
                addChatMenuPane.setPrefHeight(0.0);
                chatListScrollPane.setPrefHeight(chatListScrollPane.getPrefHeight() + addChatMenuHeight);
            }
        });

    }
    @FXML
    public void initialize(){
        closeBtn.setGraphic(svgToGroup("img/x-mark.svg", 0.03, 0.03));
        minimizeBtn.setGraphic(svgToGroup("img/minimize.svg", 0.03, 0.03));
        chatMenuTgl.setGraphic(svgToGroup("img/menu.svg", 0.06, 0.06));
        addChatPaneTgl.setGraphic(svgToGroup("img/add.svg", 0.05, 0.05));
        initializeMessageTextArea();
        setWindowMovable();
        initializeChatMenu();
    }
}
