package server;

import sample.Controller;
import util.MessageInterpreter;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {

    private int port;
    private String ip;
    private Controller controller;

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
        Socket socket = new Socket(ip, this.port);
        DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
        outToServer.writeBytes(message);
        socket.close();
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
                System.out.println("Port Listener " + this.port);
                conn = server.accept();
                output = new ObjectOutputStream(conn.getOutputStream());
                input = new ObjectInputStream(conn.getInputStream());

                output.writeObject("Connection stable...");
                while (true) {
                    try {
                        message = (String) input.readObject();
                        String code = MessageInterpreter.getCode(message);
                        switch (code) {
                            case "07":

                                break;
                            case "08":
                                int pos = Integer.valueOf(MessageInterpreter.getData(message));
                                controller.setPosition(pos);
                                break;
                            case "09":
                                controller.newMatch();
                                break;
                            case "10":
                                controller.finishGame();
                                break;
                        }
                        output.writeObject(message);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
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
