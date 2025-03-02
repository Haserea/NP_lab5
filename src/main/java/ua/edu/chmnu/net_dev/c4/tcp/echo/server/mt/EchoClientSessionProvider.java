package ua.edu.chmnu.net_dev.c4.tcp.echo.server.mt;

import ua.edu.chmnu.net_dev.c4.tcp.core.server.ClientSession;
import ua.edu.chmnu.net_dev.c4.tcp.core.server.ClientSessionProvider;

import java.net.Socket;

public class EchoClientSessionProvider implements ClientSessionProvider {
    @Override
    public ClientSession create(Socket clientSocket) {
        return new EchoClientSession(clientSocket);
    }
}
