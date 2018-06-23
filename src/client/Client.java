package client;

import sample.Main;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;

public class Client {

    private static DatagramSocket socket = null;
    private String address;
    private int port;

    public Client(String address, int port) {
        this.port = port;
        this.address = address;
    }

    public static void main(String[] args) {
//        (new Client("127.0.0.1", 8899)).init();
        try {
            String message = "01007RONYN";
            socket = new DatagramSocket();
            socket.setBroadcast(true);
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("192.168.1.5"), Main.PORT);
            socket.send(packet);
            socket.close();
            System.out.println("Send: " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init() {
        ObjectOutputStream output;
        ObjectInputStream input;
        Socket conn;
        String message = "";

        try {
            conn = new Socket(this.address, this.port);
            output =  new ObjectOutputStream(conn.getOutputStream());
            output.flush();
            input =  new ObjectInputStream(conn.getInputStream());
            message = "MENSAGEM DE TESTE";

            do {
                output.writeObject(message);
                output.flush();
                try {
                    message = (String) input.readObject();
                    System.out.println("Server: " + message);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } while (true);

//            output.close();
//            input.close();
//            conn.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
