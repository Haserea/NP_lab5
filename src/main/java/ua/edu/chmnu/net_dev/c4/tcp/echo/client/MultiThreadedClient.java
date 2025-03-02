package ua.edu.chmnu.net_dev.c4.tcp.echo.client.multithread;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadedClient {

    private final static String HOST = "localhost";
    private final static int PORT = 8080;
    private final static int NUM_CLIENTS = 1000;

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(100);

        for (int i = 0; i < NUM_CLIENTS; i++) {
            executorService.submit(() -> startClientSession());
        }

        executorService.shutdown();
    }

    private static void startClientSession() {
        try (Socket socket = new Socket(HOST, PORT);
             var writer = new PrintWriter(socket.getOutputStream(), true);
             var reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Generate a random string
            String randomMessage = generateRandomString(10);
            System.out.println("Sending: " + randomMessage);

            // Send the message to the server
            writer.println(randomMessage);

            // Read the response from the server
            String response = reader.readLine();
            System.out.println("Received: " + response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }
}
