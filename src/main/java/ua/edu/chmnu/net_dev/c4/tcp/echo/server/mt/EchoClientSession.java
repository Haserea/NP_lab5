package ua.edu.chmnu.net_dev.c4.tcp.echo.server.mt;

import ua.edu.chmnu.net_dev.c4.tcp.core.server.ClientSession;
import ua.edu.chmnu.net_dev.c4.tcp.core.server.ServerException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class EchoClientSession implements ClientSession {

    private final Socket socket;

    public EchoClientSession(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void process() {
        try (var socket = this.socket) {
            try (var ir = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 var writer = new PrintWriter(socket.getOutputStream(), true)) {

                System.out.println("Establishing connection from: " + socket.getRemoteSocketAddress());

                String promptNick = "Enter your nick:";

                String promptData = "Enter message (Q to quit):";

                writer.println(promptNick);

                String nick = ir.readLine();

                System.out.println("Client nick: " + nick);

                String inPrefix = "[" + nick + "] < ";

                String outPrefix = "[" + nick + "] > ";

                while (!socket.isClosed()) {
                    try {
                        System.out.println("Waiting text from: " + nick);

                        writer.println(promptData);

                        String inLine = ir.readLine();

                        if (inLine == null || inLine.isBlank()) {
                            break;
                        }

                        System.out.println(inPrefix + inLine);

                        String outLine = inverse(inLine);

                        writer.println(outLine);

                        System.out.println(outPrefix + outLine);
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }
                }

                System.out.println("Client " + nick + " disconnected");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            throw new ServerException(e);
        }
    }

    private String inverse(String source) {
        return new StringBuilder(source).reverse().toString();
    }
}
