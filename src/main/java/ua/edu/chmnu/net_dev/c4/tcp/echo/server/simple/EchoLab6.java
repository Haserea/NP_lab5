package ua.edu.chmnu.net_dev.c4.tcp.echo.server.simple;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoLab6 {

    private final static int DEFAULT_PORT = 6666;

    private static Integer resolvePort(String src, int defaultPort) {
        try {
            return Integer.parseInt(src);
        } catch (Exception e) {
            return defaultPort;
        }
    }

    private static void processClient(Socket socket) {

        try (var reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             var writer = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("Connection established with: " + socket.getRemoteSocketAddress());

            writer.println("Connected to the server. Enter application name to execute or 'Q' to quit.");

            String appName;
            while ((appName = reader.readLine()) != null) {
                if (appName.equalsIgnoreCase("Q")) {
                    System.out.println("Client disconnected.");
                    break;
                }

                System.out.println("Requested application: " + appName);

                try {
                    Process process = Runtime.getRuntime().exec(appName);
                    writer.println("Application '" + appName + "' started successfully.");
                    System.out.println("Application '" + appName + "' started.");
                } catch (IOException e) {
                    writer.println("Error: Unable to start application '" + appName + "'. It might not be installed.");
                    System.out.println("Error starting application: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        Integer port = DEFAULT_PORT;

        if (args.length > 0) {
            port = resolvePort(args[0], DEFAULT_PORT);
        }

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Server listening on port " + port);
            while (true) {
                System.out.println("Waiting for a connection...");
                try (Socket clientSocket = serverSocket.accept()) {
                    processClient(clientSocket);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
