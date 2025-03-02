package ua.edu.chmnu.net_dev.c4.udp.sender;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Sender {
    private final static int DEFAULT_PORT = 6666;

    public static void main(String[] args) throws IOException {
        InetAddress serverAddress = InetAddress.getByName("localhost");
        int serverPort = DEFAULT_PORT;

        try (DatagramSocket clientSocket = new DatagramSocket()) {
            System.out.println("Connected to server " + serverAddress + ":" + serverPort);

            try (Scanner scanner = new Scanner(System.in)) {
                System.out.println("Enter the name of the application to execute:");

                while (true) {
                    System.out.print("App Name: ");
                    String appName = scanner.nextLine();

                    if (appName.equalsIgnoreCase("Q")) {
                        System.out.println("Exiting client.");
                        break;
                    }

                    byte[] sendData = appName.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
                    clientSocket.send(sendPacket);

                    byte[] receiveData = new byte[1024];
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    clientSocket.receive(receivePacket);

                    String serverResponse = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    System.out.println("Server Response: " + serverResponse);
                }
            }
        }
    }
}