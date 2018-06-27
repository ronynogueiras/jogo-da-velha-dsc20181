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
    public synchronized void send(String message) throws IOException {
        this.message = message;
//        ObjectOutputStream outToServer = new ObjectOutputStream(socket.getOutputStream());
//        outToServer.writeObject(message);
    }
    public void listener(String ip, int port) throws IOException {
        System.out.println("LISTENER");
        this.socket = new Socket(InetAddress.getByName(ip), port);
        ObjectInputStream input;
        ObjectOutputStream output;
        while(true) {
            System.out.println("WHILE");
            input = new ObjectInputStream(this.socket.getInputStream());
            output = new ObjectOutputStream(this.socket.getOutputStream());
            if (message != null) {
                output.writeObject(message);
                message = null;
            }
            String receive = null;
            try {
                receive = (String) input.readObject();
                System.out.println("CLIENT: " + receive);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

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
            while(true) {
                System.out.println("SERVER SOCKET");
                conn = server.accept();
                while(true) {
                    System.out.println("ACCEPT");
                    output = new ObjectOutputStream(conn.getOutputStream());
                    input = new ObjectInputStream(conn.getInputStream());

                    output.writeObject("Connection stable...");
                    try {
                        message = (String) input.readObject();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    System.out.println("SERVER:  " + message);
                }
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
