package ua.edu.chmnu.net_dev.c4.udp.sender;

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Senderlab8 {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 6666;

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket();
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("UDP Client is running. Type your messages:");

            while (true) {
                System.out.print("Enter message: ");
                String message = scanner.nextLine();

                if (message.equalsIgnoreCase("exit")) {
                    System.out.println("Exiting client...");
                    break;
                }

                byte[] buffer = message.getBytes();
                InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);

                // Відправка повідомлення серверу
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, SERVER_PORT);
                socket.send(packet);

                // Прийняття відповіді від сервера
                byte[] responseBuffer = new byte[1024];
                DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
                socket.receive(responsePacket);

                String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
                System.out.println("Server response: " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
