package ua.edu.chmnu.net_dev.c4.udp.sender;

public interface UpdExchanger extends AutoCloseable {

    void send();

    void receive();

    @Override
    void close();

}
