package ua.edu.chmnu.net_dev.c4.udp.receiver;

import lombok.SneakyThrows;
import ua.edu.chmnu.net_dev.c4.udp.shared.FileFragment;
import ua.edu.chmnu.net_dev.c4.udp.shared.FileMeta;
import ua.edu.chmnu.net_dev.c4.udp.utils.ObjectDatagram;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

public class FileReceiver {

    private final static int PACKET_SIZE = 4 * 1024;

    private final int port;

    private final DatagramSocket socket;

    @SneakyThrows
    public FileReceiver(int port) {
        this.port = port;
        this.socket = new DatagramSocket(port);
    }

    @SneakyThrows
    public void receive() {
        System.out.println("Waiting for receive packet on port: " + port + "...");
        while (true) {
            byte[] buffer = new byte[PACKET_SIZE];
            var packet = new DatagramPacket(buffer, buffer.length);

            socket.receive(packet);

            FileMeta fileMeta = ObjectDatagram.from(packet);

            System.out.println("========>");
            System.out.println(fileMeta);

            sendResponse(packet.getSocketAddress(), "OK");
        }
    }

    @SneakyThrows
    private void sendResponse(SocketAddress to, String response) {
        var fragment = new FileFragment(response);

        var datagram = ObjectDatagram.to(fragment, to);

        socket.send(datagram);
    }

    public static void main(String[] args) {
        new FileReceiver(5559).receive();
    }
}
