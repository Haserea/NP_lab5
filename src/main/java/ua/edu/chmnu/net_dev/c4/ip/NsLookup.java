package ua.edu.chmnu.net_dev.c4.ip;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;

public class NsLookup {

    private static void usage() {
        System.out.println("Usage: " + NsLookup.class.getName() + " <dns-name>");
    }

    public static void main(String[] args) throws UnknownHostException {

        if (args == null || args.length < 1) {
            usage();
            return;
        }

        var dnsName = args[0];

        InetAddress[] ips = InetAddress.getAllByName(dnsName);

        for (var ip: ips) {
            System.out.println("---------------");

            System.out.println(ip.getHostAddress());
            System.out.println(ip.getHostName());

            System.out.println("---------------");
        }

    }
}
