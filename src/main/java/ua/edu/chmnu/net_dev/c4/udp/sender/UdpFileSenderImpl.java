package ua.edu.chmnu.net_dev.c4.udp.sender;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import ua.edu.chmnu.net_dev.c4.udp.shared.FileMeta;
import ua.edu.chmnu.net_dev.c4.udp.shared.ReceiverHost;
import ua.edu.chmnu.net_dev.c4.udp.utils.ObjectDatagram;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

@AllArgsConstructor
public class UdpFileSenderImpl implements UpdExchanger {

    private final DatagramSocket socket;

    private final ReceiverHost receiverHost;

    private final String fileName;

    public UdpFileSenderImpl(ReceiverHost receiverHost, String fileName) throws SocketException {
        this(new DatagramSocket(), receiverHost, fileName);
    }

    @Override
    @SneakyThrows
    public void send() {

        var fileSize = 12402412L;

        var fileMeta = new FileMeta(fileName, fileSize);

        var datagram = ObjectDatagram.to(fileMeta, receiverHost.inetSocketAddress());

        socket.send(datagram);
    }

    @SneakyThrows
    @Override
    public void receive() {
        var buffer = new byte[8 * 1024];

        var datagram = new DatagramPacket(buffer, buffer.length);

        socket.receive(datagram);

        var response = ObjectDatagram.from(datagram);

        System.out.println("====>   Received: " + response);
    }

    @Override
    public void close() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    public static void main(String[] args) throws SocketException {
        try(var sender = new UdpFileSenderImpl(new ReceiverHost("localhost", 5559), "file1.txt")) {
            sender.send();

            sender.receive();
        }
    }
}
