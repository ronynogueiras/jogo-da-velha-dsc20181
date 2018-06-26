package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));

        Scene scene = new Scene(root, 620, 540);
        primaryStage.setTitle("Jogo da Velha - v1.0");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop(){
    }

    public static void main(String[] args) {
        launch(args);
    }
}
