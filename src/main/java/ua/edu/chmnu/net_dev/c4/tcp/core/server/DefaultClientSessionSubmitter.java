package ua.edu.chmnu.net_dev.c4.tcp.core.server;

import java.util.concurrent.ExecutorService;

public class DefaultClientSessionSubmitter implements ClientSessionSubmitter {

    public DefaultClientSessionSubmitter(ExecutorService executor) {
        this.executor = executor;
    }

    private final ExecutorService executor;

    @Override
    public void submit(ClientSession clientSession) {
        executor.submit(clientSession::process);
    }
}
