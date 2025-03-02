package ua.edu.chmnu.net_dev.c4.udp.receiver;

import java.net.*;
import java.io.*;

public class Receiverlab8 {
    private static final int PORT = 6666;

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            System.out.println("UDP Server is running on port " + PORT);

            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                // Отримання повідомлення від клієнта
                socket.receive(packet);

                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received: " + received);

                // Створюємо новий потік для обробки клієнта
                new Thread(() -> {
                    try {
                        String response = "Echo: " + received;
                        byte[] responseBytes = response.getBytes();

                        // Відправка відповіді клієнту
                        DatagramPacket responsePacket = new DatagramPacket(
                                responseBytes, responseBytes.length,
                                packet.getAddress(), packet.getPort()
                        );
                        socket.send(responsePacket);
                        System.out.println("Response sent to " + packet.getAddress() + ":" + packet.getPort());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
