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
import server.TCPServer;
import server.UDPServer;
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
    @FXML
    private GridPane gridBoard;
    @FXML
    private Label p1;
    @FXML
    private Label p2;
    @FXML
    private Label p3;
    @FXML
    private Label p4;
    @FXML
    private Label p5;
    @FXML
    private Label p6;
    @FXML
    private Label p7;
    @FXML
    private Label p8;
    @FXML
    private Label p9;


    private static String playerName;

    private boolean isPlaying = false;

    private boolean isInvited = false;

    private String playerIp = null;
    private int playerPort = 0;

    private UDPServer udpServer;
    private TCPServer tcpServer;
    private Thread threadUDP;
    private Thread threadTCP;

    private ObservableList<Player> connectedUsers = FXCollections.observableArrayList();

    private char playerOne = 'X';
    private char playerTwo = 'O';

    private char current = 'X';

    private char[] board = new char[10];

    public synchronized void addNewConnectedUser(Player player) {
        boolean found = false;
        for (Player p: connectedUsers) {
            if (p.getIp().equals(player.getIp())) {
                found = true;
                break;
            }
        }
        if (!found) {
            Platform.runLater(() -> connectedUsers.add(player));
        }
    }
    public synchronized void removeOfflineUser(String ip) {
        for (Player p: connectedUsers) {
            if (!p.getIp().equals(ip)) {
                continue;
            }
            Platform.runLater(() -> connectedUsers.remove(p));
            break;
        }
    }
    public void closeGame() {
        try {
            if (playerName != null) {
                udpServer.sendBroadcast(MessageFormatter.format("03", playerName));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void broadcastLogin() {
        try {
            udpServer.sendBroadcast(MessageFormatter.format("01", playerName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void responseOnline() {
        try {
            if (playerName != null) {
                udpServer.sendBroadcast(MessageFormatter.format("02", playerName));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void responseInvitation(String ip) {
        try {
            int port;
            if (!isPlaying) {
                port = new Random().nextInt((65535 - 50000) + 1) + 50000;
                tcpServer = new TCPServer(port).setController(this);
                threadTCP = new Thread(() -> tcpServer.init());
                threadTCP.start();
                isPlaying = true;
                playerIp = ip;
                isInvited = true;
            } else {
                port = 0;
            }
            udpServer.sendMessage(MessageFormatter.format("05", playerName + "|" + port), ip);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void responseConfirmation(String ip, int port) {
        try {
            if (port > 0) {
                playerIp = ip;
                udpServer.sendMessage(MessageFormatter.format("06", "OK"), playerIp);
                tcpServer = new TCPServer(port);
                threadTCP = new Thread(() -> {
                    try {
                        tcpServer.listener(playerIp, port);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                threadTCP.start();
                playerPort = port;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void newMatch() {
        System.out.println("NEW MATCH");
    }
    public void startGame() {
        try {
            System.out.println("START GAME! ");
            String type = isInvited ? "1" : "2";
            if (tcpServer != null) {
                tcpServer.send(MessageFormatter.format("07", type));
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void finishGame() {
        System.out.println("FINISH GAME");
    }
    public void setPosition(int pos) {
        if (board[pos] == ' ') {
            board[pos] = 'X';
            switch (pos) {
                case 1:
                    p1.setText("X");
                    break;
                case 2:
                    p2.setText("X");
                    break;
                case 3:
                    p3.setText("X");
                    break;
                case 4:
                    p4.setText("X");
                    break;
                case 5:
                    p5.setText("X");
                    break;
                case 6:
                    p6.setText("X");
                    break;
                case 7:
                    p7.setText("X");
                    break;
                case 8:
                    p8.setText("X");
                    break;
                case 9:
                    p9.setText("X");
                    break;
            }
            if (isWinner()) {
                System.out.println("Winner!");
            }
        } else {
            System.out.println("HAS SELECTED!");
        }
    }

    @FXML
    private void initialize() {
        udpServer = new UDPServer().setController(this);
        threadUDP = new Thread(() -> {
            try {
                udpServer.broadcastListener();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        threadUDP.start();
        for(int i=0;i<board.length;i++) {
            board[i] = ' ';
        }
        this.currentPlayer.setText(String.valueOf(this.current));
        this.pointsPlayerOne.setText("0");
        this.pointsPlayerTwo.setText("0");
        this.listOnlinePlayers.setItems(connectedUsers);
        this.listOnlinePlayers.setOnMousePressed(event -> {
            if(event.getClickCount() == 2) {
                Player p = (Player) listOnlinePlayers.getSelectionModel().getSelectedItem();
                try {
                    udpServer.sendMessage(MessageFormatter.format("04", playerName), p.getIp());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
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
        int pos = 0;
        Label label = (Label) event.getSource();
        char[] position = label.getId().toCharArray();
        pos = Integer.valueOf(Character.toString(position[1]));
        setPosition(pos);

//        if (board[row][col] == ' ') {
//            board[row][col] = this.current;
//            label.setText(String.valueOf(this.current));
//            if (this.isWinner()) {
//                this.addPoint();
//
//                Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                alert.setTitle("Vencedor!");
//                alert.setHeaderText(null);
//                alert.setContentText(this.currentPlayer.getText() + ", você venceu !! Parabéns !!");
//                alert.showAndWait();
//                ((Button) label.getParent().getParent().lookup("#resetBoard")).fire();
//            }
//            if (this.isDrew()) {
//                Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                alert.setTitle("Empate!");
//                alert.setHeaderText(null);
//                alert.setContentText("O jogo terminou empatado.");
//                alert.showAndWait();
//                ((Button) label.getParent().getParent().lookup("#resetBoard")).fire();
//            }
//            this.current = this.current == 'X' ? 'O' : 'X';
//            this.currentPlayer.setText(this.current == 'X' ? "Jogador 1" : "Jogador 2");
//        } else {
//            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//            alert.setTitle("Alerta!");
//            alert.setHeaderText(null);
//            alert.setContentText("Esta posição já foi selecionada");
//            alert.showAndWait();
//        }
    }
    private void addPoint() {
        if (this.current == 'X') {
            this.pointsPlayerOne.setText(String.valueOf(Integer.valueOf(this.pointsPlayerOne.getText()) + 1));
        } else {
            this.pointsPlayerTwo.setText(String.valueOf(Integer.valueOf(this.pointsPlayerTwo.getText()) + 1));
        }
    }
    private boolean isWinner() {

        if (board[1] == 'X' && board[2] == 'X' && board[3] == 'X') {
            return true;
        }
        if (board[4] == 'X' && board[5] == 'X' && board[6] == 'X') {
            return true;
        }
        if (board[7] == 'X' && board[8] == 'X' && board[9] == 'X') {
            return true;
        }
        if (board[1] == 'X' && board[4] == 'X' && board[7] == 'X') {
            return true;
        }
        if (board[2] == 'X' && board[5] == 'X' && board[8] == 'X') {
            return true;
        }
        if (board[3] == 'X' && board[6] == 'X' && board[9] == 'X') {
            return true;
        }
        if (board[1] == 'X' && board[5] == 'X' && board[9] == 'X') {
            return true;
        }
        if (board[3] == 'X' && board[5] == 'X' && board[7] == 'X') {
            return true;
        }
        return false;
    }
    private boolean isDrew() {
        return false;
    }
    @FXML
    private void resetBoard(ActionEvent event) {
        Button btn =  (Button) event.getSource();
        GridPane gridBoard = (GridPane) btn.getParent().lookup("#gridBoard");

        this.current = 'X';
        this.currentPlayer.setText("Jogador 1");
    }
}
