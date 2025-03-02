package ua.edu.chmnu.net_dev.c4.tcp.user.server.mt;

import ua.edu.chmnu.net_dev.c4.tcp.core.CmdLineOptions;
import ua.edu.chmnu.net_dev.c4.tcp.core.server.DefaultClientSessionSubmitter;
import ua.edu.chmnu.net_dev.c4.tcp.core.server.TcpServer;
import ua.edu.chmnu.net_dev.c4.tcp.core.server.TcpServerApp;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserServerApp extends TcpServerApp {

    private final static int DEFAULT_PORT = 6720;

    private final static int DEFAULT_BACK_LOG = 20;

    private final static int DEFAULT_POOL_SIZE = 20;

    public UserServerApp(TcpServer server, ExecutorService executor) {
        super(server, executor);
    }

    public static void main(String[] args) throws IOException {
        var cmdLineOptions = new CmdLineOptions(args);

        var executor = createExecutor(cmdLineOptions);

        var tcpServer = createTcpServer(cmdLineOptions, executor);

        new UserServerApp(tcpServer, executor).start();

        manage(tcpServer, executor);
    }

    private static ExecutorService createExecutor(CmdLineOptions options) {
        var poolSize = options
                .getLongOption("pool-size", Integer::valueOf, DEFAULT_POOL_SIZE)
                .orElse(DEFAULT_POOL_SIZE);

        return Executors.newFixedThreadPool(poolSize);
    }

    private static TcpServer createTcpServer(CmdLineOptions options, ExecutorService executorService) throws IOException {
        var port = options
                .getLongOption("port", Integer::valueOf, DEFAULT_PORT)
                .orElse(DEFAULT_PORT);

        var backlog = options
                .getLongOption("backlog", Integer::valueOf, DEFAULT_BACK_LOG)
                .orElse(DEFAULT_BACK_LOG);

        return new TcpServer(port, backlog)
                .with(new UserClientSessionProvider())
                .with(new DefaultClientSessionSubmitter(executorService));

    }

    private static void manage(TcpServer tcpServer, ExecutorService executorService) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("To stop server type Q/Quit====");

            while (true) {
                var line = scanner.nextLine();

                if (line.equalsIgnoreCase("Q") || line.equalsIgnoreCase("QUIT")) {
                    break;
                }
            }

            executorService.shutdownNow();

            System.out.println("Waiting for server to shutdown");

            while (!executorService.isShutdown()) {
                System.out.print(".");
            }

            tcpServer.setActive(false);

        }
    }
}
