package ua.edu.chmnu.net_dev.c4.tcp.echo.server.simple;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Echo {

    private final static int DEFAULT_PORT = 6666;
    private static final int[][] grid = new int[4][4];

    private static Integer resolvePort(String src, int defaultPort) {
        try {
            return Integer.parseInt(src);
        } catch (Exception e) {
            return defaultPort;
        }
    }

    static {
        Random rand = new Random();
        int shipsPlaced = 0;
        while (shipsPlaced < 6) {
            int x = rand.nextInt(4);
            int y = rand.nextInt(4);
            if (grid[x][y] == 0) {
                grid[x][y] = 1;
                shipsPlaced++;
            }
        }
    }

    private static boolean checkHit(int x, int y) {
        if (x < 0 || x >= 4 || y < 0 || y >= 4) {
            return false;
        }
        return grid[x][y] == 1;
    }

    private static void printGrid() {
        System.out.println("Grid:");
        for (int[] row : grid) {
            for (int cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }

    private static void processClient(Socket socket) {
        try (var ir = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             var writer = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("Establishing connection from: " + socket.getRemoteSocketAddress());

            int hits = 0;
            while (!socket.isClosed() && hits < 6) {
                try {
                    System.out.println("Waiting for coordinates...");
                    writer.println("(x y) - ");
                    String inLine = ir.readLine();

                    if (inLine == null || inLine.isBlank() || inLine.equalsIgnoreCase("Q")) {
                        break;
                    }

                    String[] parts = inLine.split(" ");
                    int x = Integer.parseInt(parts[0]) - 1;
                    int y = Integer.parseInt(parts[1]) - 1;

                    if (checkHit(x, y)) {
                        writer.println("hit");
                        grid[x][y] = 0;
                        hits++;
                    } else {
                        writer.println("miss!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
            if (hits == 6) {
                writer.println("All ships.");
            }

            socket.close();
            System.out.println("Client disconnected");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Integer port = DEFAULT_PORT;
        if (args.length > 0) {
            port = resolvePort(args[0], DEFAULT_PORT);
        }
        printGrid();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Listening on port " + port);
            while (!serverSocket.isClosed()) {
                System.out.println("Waiting for connection...");
                try (Socket socket = serverSocket.accept()) {
                    processClient(socket);
                    if (isAllShipsDestroyed()) {
                        System.out.println("All ships destroyed. Shutting down server...");
                        serverSocket.close();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static boolean isAllShipsDestroyed() {
        for (int[] row : grid) {
            for (int cell : row) {
                if (cell == 1) {
                    return false;
                }
            }
        }
        return true;
    }
}
