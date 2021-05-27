package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("fxml/layout.fxml"));
        Scene mainScene = new Scene(root);
        mainScene.setFill(Color.TRANSPARENT);
        primaryStage.setTitle("DZIN");
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
