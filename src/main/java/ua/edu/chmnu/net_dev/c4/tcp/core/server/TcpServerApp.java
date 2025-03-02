package ua.edu.chmnu.net_dev.c4.tcp.core.server;

import java.util.concurrent.ExecutorService;

public class TcpServerApp {

    private final TcpServer server;

    private final ExecutorService executor;

    public TcpServerApp(TcpServer server, ExecutorService executor) {
        this.server = server;
        this.executor = executor;
    }

    public void start() {
        executor.submit(server);
    }
}
