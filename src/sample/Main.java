package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import server.BroadcastServer;

import java.io.IOException;


public class Main extends Application {

    public static int PORT = 20181;
    private Thread udpServer;
    
    @Override
    public void start(Stage primaryStage) throws Exception {

        udpServer = new Thread(() -> {
            try {
                BroadcastServer.listener();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        udpServer.start();
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));

        Scene scene = new Scene(root, 620, 540);
        primaryStage.setTitle("Jogo da Velha - v1.0");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop(){
        Controller.closeGame();
        System.out.println("Stage is closing");
        // Save file
    }

    public static void main(String[] args) {
        launch(args);
    }
}
