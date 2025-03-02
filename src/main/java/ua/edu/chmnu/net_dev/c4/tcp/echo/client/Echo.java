package ua.edu.chmnu.net_dev.c4.tcp.echo.client;

import ua.edu.chmnu.net_dev.c4.tcp.core.client.EndPoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Echo {
    private final static int DEFAULT_PORT = 8080;

    public static void main(String[] args) throws IOException {

        EndPoint endPoint;

        if (args.length > 0) {
            endPoint = new EndPoint(args[0]);
        } else {
            endPoint = new EndPoint("localhost", DEFAULT_PORT);
        }

        try (Socket clientSocket = new Socket(endPoint.getHost(), endPoint.getPort())) {

            System.out.println("Establish connection to " + endPoint.getHost() + ":" + endPoint.getPort());

            try (
                    var scanner = new Scanner(System.in);
                    var writer = new PrintWriter(clientSocket.getOutputStream(), true);
                    var reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
            ) {
                String promptNick = reader.readLine();

                System.out.print(promptNick);

                var nick = scanner.nextLine();

                writer.println(nick);

                while (!clientSocket.isClosed()) {
                    String promptData = reader.readLine();

                    System.out.print(promptData);

                    var line = scanner.nextLine();

                    if (line.equalsIgnoreCase("Q")) {
                        System.out.println("Done client!");
                        break;
                    }

                    writer.println(line);
                }
            }
        }
    }
}
