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

    private static char[][] board = new char[][]{
            {' ', ' ', ' '},
            {' ', ' ', ' '},
            {' ', ' ', ' '}
    };

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
                tcpServer = new TCPServer(port).setIp(playerIp);
                threadTCP = new Thread(() -> tcpServer.init());
                threadTCP.start();
                playerPort = port;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setPosition(int pos) {
        switch (pos) {
            case 1:
                board[0][0] = 'X';
                break;
            case 2:
                board[0][1] = 'X';
                break;
            case 3:
                board[0][2] = 'X';
                break;
            case 4:
                board[1][0] = 'X';
                break;
            case 5:
                board[1][1] = 'X';
                break;
            case 6:
                board[1][2] = 'X';
                break;
            case 7:
                board[2][0] = 'X';
                break;
            case 8:
                board[2][1] = 'X';
                break;
            case 9:
                board[2][2] = 'X';
                break;
        }
    }
    public void newMatch() {
        System.out.println("NEW MATCH");
    }
    public void startGame() {
        try {
            System.out.println("START GAME! ");
            if (playerIp != null) {
                System.out.println("START GAME! 2");
                String type = isInvited ? "1" : "2";

                tcpServer.send(playerIp, MessageFormatter.format("07", type), playerPort);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void finishGame() {
        System.out.println("FINISH GAME");
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
