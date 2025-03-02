package ua.edu.chmnu.net_dev.c4.tcp.core.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class TcpServer implements Runnable {

    private final static Logger LOGGER = Logger.getLogger(TcpServer.class.getName());

    private final ServerSocket serverSocket;

    private boolean active = true;

    private ClientSessionProvider clientSessionProvider;

    private ClientSessionSubmitter clientSessionSubmitter;

    public TcpServer(int port) throws IOException {
        this(port, 100);
    }

    public TcpServer(int port, int backlog) throws IOException {
        serverSocket = new ServerSocket(port, backlog);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;

        if (!active && serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                throw new ServerException(e);
            }
        }
    }

    public TcpServer with(ClientSessionProvider clientSessionProvider) {
        this.clientSessionProvider = clientSessionProvider;
        return this;
    }

    public TcpServer with(ClientSessionSubmitter clientSessionSubmitter) {
        this.clientSessionSubmitter = clientSessionSubmitter;
        return this;
    }

    @Override
    public void run() {
        if (clientSessionProvider == null || clientSessionSubmitter == null) {
            LOGGER.warning("Cant process client sessions");
            return;
        }

        try (var serverSocket = this.serverSocket) {
            System.out.println("Listening on port " + serverSocket.getLocalPort());

            while (active) {
                System.out.println("Waiting for connection...");

                Socket clientSocket = serverSocket.accept();

                var session = clientSessionProvider.create(clientSocket);

                clientSessionSubmitter.submit(session);
            }
        } catch (IOException e) {
            throw new ServerException(e);
        }
    }
}
