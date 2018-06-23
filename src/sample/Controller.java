package sample;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import server.BroadcastServer;
import server.Server;
import util.MessageFormatter;

import java.io.IOException;
import java.util.Random;

public class Controller {
    @FXML
    private Label pointsPlayerOne;
    @FXML
    private Label pointsPlayerTwo;
    @FXML
    private Label currentPlayer;
    @FXML
    private ListView listOnlinePlayers;
    @FXML
    private TextField inputLogin;

    private static String playerName;

    private static boolean isPlaying = false;

    public static boolean isInvited = false;

    private static ObservableList<Player> connectedUsers = FXCollections.observableArrayList();

    private char playerOne = 'X';
    private char playerTwo = 'O';

    private char current = 'X';

    private char[][] board = new char[][]{
            {' ', ' ', ' '},
            {' ', ' ', ' '},
            {' ', ' ', ' '}
    };

    public synchronized static void addNewConnectedUser(Player player) {
        if(!connectedUsers.contains(player))
            connectedUsers.add(player);
    }
    public synchronized static void removeOfflineUser(String ip) {
        for (Player p: connectedUsers) {
            if (!p.getIp().equals(ip)) {
                continue;
            }
            Platform.runLater(() -> connectedUsers.remove(p));
            break;
        }
    }
    public static void closeGame() {
        try {
            if (playerName != null) {
                BroadcastServer.send(MessageFormatter.format("03", playerName));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static void broadcastLogin() {
        try {
            BroadcastServer.send(MessageFormatter.format("01", playerName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void responseOnline() {
        try {
            if (playerName != null) {
                BroadcastServer.send(MessageFormatter.format("02", playerName));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static  void responseInvitation() {
        try {
            int port;
            if (isPlaying) {
                port = new Random().nextInt((50000 - 65535) + 1) + 50000 ;
                new Thread(() -> new Server(port).init()).start();
            } else {
                port = 0;
            }
            BroadcastServer.send(MessageFormatter.format("05", playerName + "|" + port));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void responseConfirmation() {
        try {
            BroadcastServer.send(MessageFormatter.format("06", "OK"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void setPosition(int pos) {
        switch (pos) {
            case 1:
                break;
            case 2:
                break;

        }
    }
    public static void newMatch() {
        // new match
    }
    public static void startGame() {
        // start
    }
    public static void finishGame() {
        // finish game
    }
    @FXML
    private void initialize() {
        this.currentPlayer.setText(String.valueOf(this.current));
        this.pointsPlayerOne.setText("0");
        this.pointsPlayerTwo.setText("0");
        this.listOnlinePlayers.setItems(connectedUsers);

    }
    @FXML
    private void login(ActionEvent event) {
        Button btn = (Button) event.getSource();
        this.inputLogin = (TextField) btn.getParent().lookup("#inputLogin");
        Label statusConnection = (Label) btn.getParent().lookup("#statusConnection");
        if (this.inputLogin.getText().length() > 0) {
            statusConnection.setText("Conectando...");
            statusConnection.setTextFill(Paint.valueOf("#8ECAE9"));
            playerName = this.inputLogin.getText();
            broadcastLogin();
            statusConnection.setText("Conectado: " + playerName);
            statusConnection.setTextFill(Paint.valueOf("#0DB18D"));
            this.inputLogin.setText("");
            this.inputLogin.setDisable(true);
            btn.setDisable(true);
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Aviso");
            alert.setHeaderText(null);
            alert.setContentText("Você deve informar um login");
            alert.showAndWait();
        }
        System.out.println(inputLogin.getText());
    }
    @FXML
    private void boardItemSelected(MouseEvent event) {
        Label label = (Label) event.getSource();
        char[] position = label.getId().toCharArray();
        int row = Integer.valueOf(Character.toString(position[1]));
        int col = Integer.valueOf(Character.toString(position[2]));
        if (board[row][col] == ' ') {
            board[row][col] = this.current;
            label.setText(String.valueOf(this.current));
            if (this.isWinner()) {
                this.addPoint();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Vencedor!");
                alert.setHeaderText(null);
                alert.setContentText(this.currentPlayer.getText() + ", você venceu !! Parabéns !!");
                alert.showAndWait();
                ((Button) label.getParent().getParent().lookup("#resetBoard")).fire();
            }
            if (this.isDrew()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Empate!");
                alert.setHeaderText(null);
                alert.setContentText("O jogo terminou empatado.");
                alert.showAndWait();
                ((Button) label.getParent().getParent().lookup("#resetBoard")).fire();
            }
            this.current = this.current == 'X' ? 'O' : 'X';
            this.currentPlayer.setText(this.current == 'X' ? "Jogador 1" : "Jogador 2");
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Alerta!");
            alert.setHeaderText(null);
            alert.setContentText("Esta posição já foi selecionada");
            alert.showAndWait();
        }
    }
    private void addPoint() {
        if (this.current == 'X') {
            this.pointsPlayerOne.setText(String.valueOf(Integer.valueOf(this.pointsPlayerOne.getText()) + 1));
        } else {
            this.pointsPlayerTwo.setText(String.valueOf(Integer.valueOf(this.pointsPlayerTwo.getText()) + 1));
        }
    }
    private boolean isWinner() {
        for (int i = 0; i < this.board.length; i++) {
            int rowSelecteds = 0;

            if (this.board[0][i] == this.current && this.board[1][i] == this.current && this.board[2][i] == this.current) {
                return true;
            }
            if (this.board[0][0] == this.current && this.board[1][1] == this.current && this.board[2][2] == this.current) {
                return true;
            }
            if (this.board[2][0] == this.current && this.board[1][1] == this.current && this.board[0][2] == this.current) {
                return true;
            }

            for (int j = 0; j < this.board[i].length; j++) {
                if (this.board[i][j] == this.current) {
                    rowSelecteds++;
                }
            }

            if (rowSelecteds == 3 ) {
                return true;
            }
        }
        return false;
    }
    private boolean isDrew() {
        for (int i = 0; i < this.board.length; i++) {
            for (int j = 0; j < this.board[i].length; j++) {
                if (this.board[i][j] == ' ') {
                    return false;
                }
            }
        }
        return !this.isWinner();
    }
    @FXML
    private void resetBoard(ActionEvent event) {
        Button btn =  (Button) event.getSource();
        GridPane gridBoard = (GridPane) btn.getParent().lookup("#gridBoard");

        for (int i = 0; i < this.board.length; i++) {
            for (int j = 0; j < this.board[i].length; j++) {
                ((Label) gridBoard.lookup("#p" + i + "" + j)).setText(" ");
                this.board[i][j] = ' ';
            }
        }
        this.current = 'X';
        this.currentPlayer.setText("Jogador 1");
    }
}
