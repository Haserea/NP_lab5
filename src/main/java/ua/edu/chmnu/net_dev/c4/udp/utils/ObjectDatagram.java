package ua.edu.chmnu.net_dev.c4.udp.utils;

import lombok.experimental.UtilityClass;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.SocketAddress;

@UtilityClass
public class ObjectDatagram {

    public static <T extends java.io.Serializable> DatagramPacket to(T source, SocketAddress target) throws IOException {
        try (var bos = new ByteArrayOutputStream();
             var oos = new ObjectOutputStream(bos)) {
            oos.writeObject(source);
            byte[] buffer = bos.toByteArray();

            return new DatagramPacket(buffer, buffer.length, target);
        }
    }

    public static <T extends java.io.Serializable> T from(DatagramPacket source) throws IOException, ClassNotFoundException {
        try (var bis = new ByteArrayInputStream(source.getData());
             var ois = new ObjectInputStream(bis)) {

            return (T) ois.readObject();
        }
    }
}
