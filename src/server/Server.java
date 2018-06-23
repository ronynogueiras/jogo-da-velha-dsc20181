package server;

import sample.Controller;
import util.MessageInterpreter;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private int port;

    public Server(int port) {
        this.port = port;
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
                                Controller.setPosition(pos);
                                break;
                            case "09":
                                Controller.newMatch();
                                break;
                            case "10":
                                Controller.finishGame();
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
