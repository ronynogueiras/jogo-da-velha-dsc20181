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
        System.out.println(ip + ", " + message);
        Socket socket = new Socket(InetAddress.getByName(ip), this.port);
        DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
        outToServer.writeUTF(message);
        socket.close();
    }
    public void send(String message) throws IOException {
        this.message = message;
        System.out.println(ip + ", " + message);
//        Socket socket = new Socket(InetAddress.getByName(ip), port);
//        DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
//        outToServer.writeUTF(message);
//        socket.close();
    }
    public void listener(String ip, int port) throws IOException {
        try {
            Socket socket = new Socket(InetAddress.getByName(ip), port);
            while(true) {
                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                String receive = input.readUTF();
                if (!receive.equals("")) {
                    System.out.println(receive);
                }
                if (message != null) {
                    output.writeUTF(message);
                    message = null;
                }
            }
        }
        catch(Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
        System.out.println("LISTENER");
    }
    public void init() {
        System.out.println("INIT TCP SERVER!");
        try {
            ServerSocket server = new ServerSocket(this.port);;
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

                output.writeUTF("Connection stable...");
                while (true) {
                    message = input.readUTF();
                    System.out.println(message);
                    String code = MessageInterpreter.getCode(message);
                    switch (code) {
                        case "07":
                            System.out.println("07");
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
