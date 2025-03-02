package ua.edu.chmnu.net_dev.c4.udp.shared;

import java.net.InetSocketAddress;

public record ReceiverHost(String host, int port) {

    public InetSocketAddress inetSocketAddress() {
        return new InetSocketAddress(host, port);
    }
}
