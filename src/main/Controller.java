package main;

import afester.javafx.svg.SvgLoader;
import javafx.beans.property.IntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.fxmisc.richtext.InlineCssTextArea;
import org.reactfx.inhibeans.property.SimpleIntegerProperty;

import java.util.Arrays;

public class Controller {

    @FXML
    private Button closeBtn;
    @FXML
    private Button minimizeBtn;
    @FXML
    private Button menuBtn;
    @FXML
    private AnchorPane messageBoxContainer;

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

    @FXML
    public void initialize(){
        closeBtn.setGraphic(svgToGroup("img/x-mark.svg", 0.03, 0.03));
        minimizeBtn.setGraphic(svgToGroup("img/minimize.svg", 0.03, 0.03));
        menuBtn.setGraphic(svgToGroup("img/menu.svg", 0.06, 0.06));

        initializeMessageTextArea();
    }
}
