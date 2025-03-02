package ua.edu.chmnu.net_dev.c4.tcp.echo.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class EchoLab6  {

    private final static int DEFAULT_PORT = 8080;

    public static void main(String[] args) {
        String serverHost = "localhost";
        int serverPort = DEFAULT_PORT;

        if (args.length > 0) {
            serverHost = args[0];
        }
        if (args.length > 1) {
            try {
                serverPort = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number. Using default port: " + DEFAULT_PORT);
            }
        }

        try (DatagramSocket clientSocket = new DatagramSocket()) {
            InetAddress serverAddress = InetAddress.getByName(serverHost);

            // Prepare the request (can be an empty message)
            byte[] sendData = "TIME_REQUEST".getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);

            // Send the request to the server
            clientSocket.send(sendPacket);

            // Prepare to receive the response
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            // Receive the response from the server
            clientSocket.receive(receivePacket);

            // Extract the server's response
            String serverTime = new String(receivePacket.getData(), 0, receivePacket.getLength());
            System.out.println("Received time from server: " + serverTime);

        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + serverHost);
        } catch (IOException e) {
            System.err.println("Error in client: " + e.getMessage());
        }
    }
}
