package main;

import afester.javafx.svg.SvgLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.Console;
import java.io.InputStream;

public class Controller {

    @FXML
    private Button closeBtn;
    @FXML
    private Button minimizeBtn;

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
    public void initialize(){
        SvgLoader loader = new SvgLoader();
        Group svgImageCloseBtn = loader.loadSvg(getClass().getResourceAsStream("img/x-mark.svg"));
        Group graphic = new Group(svgImageCloseBtn);
        svgImageCloseBtn.setScaleX(0.03);
        svgImageCloseBtn.setScaleY(0.03);
        closeBtn.setGraphic(graphic);

        Group svgImageMinimizeBtn = loader.loadSvg(getClass().getResourceAsStream("img/minimize.svg"));
        graphic = new Group(svgImageMinimizeBtn);
        svgImageMinimizeBtn.setScaleX(0.03);
        svgImageMinimizeBtn.setScaleY(0.03);
        minimizeBtn.setGraphic(graphic);
    }
}
