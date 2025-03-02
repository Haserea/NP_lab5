package ua.edu.chmnu.net_dev.c4.tcp.echo.client;

import ua.edu.chmnu.net_dev.c4.tcp.core.client.EndPoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class EchoLab6  {
    private final static int DEFAULT_PORT = 6666;

    public static void main(String[] args) throws IOException {

        EndPoint endPoint;

        if (args.length > 0) {
            endPoint = new EndPoint(args[0]);
        } else {
            endPoint = new EndPoint("localhost", DEFAULT_PORT);
        }

        try (Socket clientSocket = new Socket(endPoint.getHost(), endPoint.getPort())) {

            System.out.println("Connected to " + endPoint.getHost() + ":" + endPoint.getPort());

            try (
                    var scanner = new Scanner(System.in);
                    var writer = new PrintWriter(clientSocket.getOutputStream(), true);
                    var reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
            ) {
                System.out.println("Enter the name of the application to execute:");

                while (true) {
                    System.out.print("App Name: ");
                    var appName = scanner.nextLine();

                    if (appName.equalsIgnoreCase("Q")) {
                        System.out.println("Exiting client.");
                        break;
                    }

                    writer.println(appName);

                    String serverResponse = reader.readLine();

                    if (serverResponse != null) {
                        System.out.println("Server Response: " + serverResponse);
                    }
                }
            }
        }
    }
}
