package server;

import sample.Controller;
import sample.Main;
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
    public static void listener() throws IOException {
        System.out.printf("Listening on udp:%s:%d%n",
                InetAddress.getByName("0.0.0.0"), Main.PORT);
        DatagramSocket  socket = new DatagramSocket(Main.PORT, InetAddress.getByName("0.0.0.0"));
        byte[] receiveData = new byte[128];
        DatagramPacket receivePacket = new DatagramPacket(receiveData,
                receiveData.length);
        System.out.println("Listen " + Main.PORT);
        while(true)
        {
            socket.receive(receivePacket);
            if (!receivePacket.getAddress().getHostAddress().equals(InetAddress.getLocalHost().getHostAddress())) {
                String message = new String( receivePacket.getData(), 0,
                        receivePacket.getLength() );
                String code = MessageInterpreter.getCode(message);
                String name;
                switch (code) {
                    case "01":
                        name = MessageInterpreter.getData(message);
                        Controller.addNewConnectedUser(name + "_" +receivePacket.getAddress().toString());
                        Controller.responseOnline();
                        break;
                    case "02":
                        name = MessageInterpreter.getData(message);
                        Controller.addNewConnectedUser(name + "_" +receivePacket.getAddress().toString());
                        break;
                    case "03":
                        name = MessageInterpreter.getData(message);
                        Controller.removeOfflineUser(name + "_" +receivePacket.getAddress().toString());
                        break;
                    case "04":
                        Controller.responseInvitation();
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
    }
}
