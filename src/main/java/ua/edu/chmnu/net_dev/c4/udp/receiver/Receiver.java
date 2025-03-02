package ua.edu.chmnu.net_dev.c4.udp.receiver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Receiver {
    private final static int DEFAULT_PORT = 6666;

    public static void main(String[] args) {
        try (DatagramSocket serverSocket = new DatagramSocket(DEFAULT_PORT)) {
            System.out.println("Server listening on port " + DEFAULT_PORT);

            byte[] receiveData = new byte[1024];
            byte[] sendData;

            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                String appName = new String(receivePacket.getData(), 0, receivePacket.getLength());
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                System.out.println("Requested application: " + appName);

                String response;
                try {
                    Process process = Runtime.getRuntime().exec(appName);
                    response = "Application '" + appName + "' started successfully.";
                    System.out.println("Application '" + appName + "' started.");
                } catch (IOException e) {
                    response = "Error: Unable to start application '" + appName + "'. It might not be installed.";
                    System.out.println("Error starting application: " + e.getMessage());
                }

                sendData = response.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
                serverSocket.send(sendPacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
