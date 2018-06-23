package server;

import sample.Controller;
import sample.Main;
import sample.Player;
import util.MessageInterpreter;

import java.io.IOException;
import java.net.*;

public class BroadcastServer {

    public static void send(String message) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        socket.setBroadcast(true);
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("255.255.255.255"), Main.PORT);
        socket.send(packet);
        socket.close();
        System.out.println("Send: " + message);
    }
    public static void send(String message, String ip) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ip), Main.PORT);
        socket.send(packet);
        socket.close();
        System.out.println("Send: " + message + ", HOST: " + ip );
    }
    public static void listener() throws IOException {
        System.out.printf("Listening on udp:%s:%d%n",
                InetAddress.getByName("0.0.0.0"), Main.PORT);
        DatagramSocket  socket = new DatagramSocket(Main.PORT, InetAddress.getByName("0.0.0.0"));
        byte[] receiveData = new byte[128];
        DatagramPacket receivePacket = new DatagramPacket(receiveData,
                receiveData.length);
        System.out.println("Listen " + Main.PORT);
        boolean run = true;
        while(run) {
            socket.receive(receivePacket);
            String message = new String( receivePacket.getData(), 0,
                    receivePacket.getLength() );
            String code = MessageInterpreter.getCode(message);
            if (!receivePacket.getAddress().getHostAddress().equals(InetAddress.getLocalHost().getHostAddress())) {
                String name;
                switch (code) {
                    case "01":
                        name = MessageInterpreter.getData(message);
                        Controller.addNewConnectedUser(new Player(name, receivePacket.getAddress().getHostAddress(), true));
                        Controller.responseOnline();
                        break;
                    case "02":
                        name = MessageInterpreter.getData(message);
                        Controller.addNewConnectedUser(new Player(name, receivePacket.getAddress().getHostAddress(), true));
                        break;
                    case "03":
                        Controller.removeOfflineUser(receivePacket.getAddress().getHostAddress());
                        break;
                    case "04":
                        Controller.responseInvitation(receivePacket.getAddress().getHostAddress());
                        break;
                    case "05":
                        Controller.responseConfirmation();
                        break;
                    case "06":
                        Controller.startGame();
                        break;
                }
                System.out.println("RECEIVED: " + message);
            }
        }
        socket.close();
    }
}
