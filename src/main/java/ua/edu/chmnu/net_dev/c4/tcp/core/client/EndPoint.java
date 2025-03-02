package ua.edu.chmnu.net_dev.c4.tcp.core.client;

public class EndPoint {
    private String host = "localhost";

    private int port = 8080;

    public EndPoint() {
    }

    public EndPoint(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public EndPoint(String endPoint) {
        try {
            String[] parts = endPoint.split(":");
            try {
                this.host = parts[0];
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                this.port = Integer.parseInt(parts[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
