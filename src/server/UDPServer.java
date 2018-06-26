package server;

import sample.Controller;
import sample.Main;
import sample.Player;
import util.MessageInterpreter;

import java.io.IOException;
import java.net.*;

public class UDPServer{

    private Controller controller;
    private String broadcastSender = "255.255.255.255";
    private String broadcastListener = "0.0.0.0";
    private int port = 20181;

    public UDPServer setController(Controller controller) {
        this.controller = controller;
        return this;
    }

    public UDPServer sendBroadcast(String message) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        socket.setBroadcast(true);
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(broadcastSender), port);
        socket.send(packet);
        socket.close();
        System.out.println("Send: " + message);
        return this;
    }
    public UDPServer sendMessage(String message, String ip) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ip), port);
        socket.send(packet);
        socket.close();
        System.out.println("Send: " + message + ", HOST: " + ip );
        return this;
    }
    public void broadcastListener() throws IOException {
        System.out.printf("Listening on udp:%s:%d%n",
                InetAddress.getByName("0.0.0.0"), port);
        DatagramSocket  socket = new DatagramSocket(port, InetAddress.getByName(broadcastListener));
        byte[] receiveData = new byte[256];
        DatagramPacket receivePacket = new DatagramPacket(receiveData,
                receiveData.length);
        System.out.println("Listen " + port);
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
                        controller.addNewConnectedUser(new Player(name, receivePacket.getAddress().getHostAddress(), true));
                        controller.responseOnline();
                        break;
                    case "02":
                        name = MessageInterpreter.getData(message);
                        controller.addNewConnectedUser(new Player(name, receivePacket.getAddress().getHostAddress(), true));
                        break;
                    case "03":
                        controller.removeOfflineUser(receivePacket.getAddress().getHostAddress());
                        break;
                    case "04":
                        controller.responseInvitation(receivePacket.getAddress().getHostAddress());
                        break;
                    case "05":
                        System.out.println(message);
                        String[] parts = MessageInterpreter.getData(message).split("\\|");
                        int port = Integer.valueOf(parts[1]);
                        controller.responseConfirmation(receivePacket.getAddress().getHostAddress(), port);
                        break;
                    case "06":
                        controller.startGame();
                        break;
                }
                System.out.println("RECEIVED: " + message);
            }
        }
        socket.close();
    }
}
