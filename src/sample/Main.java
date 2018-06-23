package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import server.BroadcastServer;
import server.Server;

import java.io.IOException;


public class Main extends Application {

    public static int PORT = 20181;

    @Override
    public void start(Stage primaryStage) throws Exception {

//        new Thread(() -> (new Server(20181)).init()).start();
        new Thread(() -> {
            try {
                BroadcastServer.listener();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));

        Scene scene = new Scene(root, 620, 540);
        primaryStage.setTitle("Jogo da Velha - v1.0");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
