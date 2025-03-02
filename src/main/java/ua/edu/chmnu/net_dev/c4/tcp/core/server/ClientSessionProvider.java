package ua.edu.chmnu.net_dev.c4.tcp.core.server;

import java.net.Socket;

public interface ClientSessionProvider {

    ClientSession create(Socket clientSocket);
}
