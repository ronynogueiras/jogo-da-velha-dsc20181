package server;

import sample.Controller;
import util.MessageInterpreter;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPServer {

    private int port;
    private String ip;
    private Controller controller;
    private String message = null;
    private Socket socket;

    public TCPServer(int port) {
        this.port = port;
    }
    public TCPServer setController(Controller controller) {
        this.controller = controller;
        return this;
    }
    public TCPServer setIp(String ip) {
        this.ip = ip;
        return this;
    }
    public void send(String ip, String message) throws IOException {
        Socket socket = new Socket(InetAddress.getByName(ip), this.port);
        DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
        outToServer.writeUTF(message);
        socket.close();
    }
    public void send(String message) throws IOException {
        DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
        outToServer.writeUTF(message);
    }
    public void listener(String ip, int port) throws IOException {
        this.socket = new Socket(InetAddress.getByName(ip), port);
        ObjectInputStream input;
        ObjectOutputStream output;
        while(true) {
            input = new ObjectInputStream(this.socket.getInputStream());
            output = new ObjectOutputStream(this.socket.getOutputStream());
            String receive = input.readUTF();
            System.out.println("CLIENT: " + receive);
            output.writeUTF("TESTE");
        }
    }
    public void init() {
        try {
            ServerSocket server = new ServerSocket(this.port);
            Socket conn;
            ObjectOutputStream output;
            ObjectInputStream input;
            String message = "";
            boolean finish = false;
            while(!finish) {
                conn = server.accept();
                output = new ObjectOutputStream(conn.getOutputStream());
                input = new ObjectInputStream(conn.getInputStream());

                output.writeUTF("Connection stable...");
                message = input.readUTF();
                System.out.println("SERVER:  " + message);
//                String code = MessageInterpreter.getCode(message);
//                switch (code) {
//                    case "07":
//                        break;
//                    case "08":
//                        int pos = Integer.valueOf(MessageInterpreter.getData(message));
//                        controller.setPosition(pos);
//                        break;
//                    case "09":
//                        controller.newMatch();
//                        break;
//                    case "10":
//                        controller.finishGame();
//                        break;
//                }
//                finish = true;
//                output.close();
//                input.close();
//                conn.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
