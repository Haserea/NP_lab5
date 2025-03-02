package ua.edu.chmnu.net_dev.c4.tcp.user.client;

import ua.edu.chmnu.net_dev.c4.tcp.core.client.EndPoint;
import ua.edu.chmnu.net_dev.c4.tcp.user.shared.Authorization;
import ua.edu.chmnu.net_dev.c4.tcp.user.shared.UserLogin;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class UserClient {
    private final static int DEFAULT_PORT = 6720;

    public static void main(String[] args) throws IOException {

        EndPoint endPoint;

        if (args.length > 0) {
            endPoint = new EndPoint(args[0]);
        } else {
            endPoint = new EndPoint("localhost", DEFAULT_PORT);
        }

        try(Socket clientSocket = new Socket(endPoint.getHost(), endPoint.getPort())) {

            System.out.println("Establish connection to " + endPoint.getHost() + ":" + endPoint.getPort());

            try (
                    var scanner = new Scanner(System.in);
                    var os = new ObjectOutputStream(clientSocket.getOutputStream());
                    var is = new ObjectInputStream(clientSocket.getInputStream())
            ) {
                System.out.print("Enter username: ");
                String username = scanner.nextLine();

                System.out.print("Enter password: ");
                String password = scanner.nextLine();

                var user = new UserLogin(username, password);

                os.writeObject(user);

                var auth = (Authorization)is.readObject();

                System.out.println("Token: " + auth.getToken());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
