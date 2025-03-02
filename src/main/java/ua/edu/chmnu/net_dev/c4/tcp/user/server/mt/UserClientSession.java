package ua.edu.chmnu.net_dev.c4.tcp.user.server.mt;

import ua.edu.chmnu.net_dev.c4.tcp.core.server.ClientSession;
import ua.edu.chmnu.net_dev.c4.tcp.core.server.ServerException;
import ua.edu.chmnu.net_dev.c4.tcp.user.shared.Authorization;
import ua.edu.chmnu.net_dev.c4.tcp.user.shared.UserLogin;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.MessageDigest;
import java.time.LocalDateTime;

public class UserClientSession implements ClientSession {

    private final Socket socket;

    public UserClientSession(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void process() {
        try (var socket = this.socket) {
            try (var is = new ObjectInputStream(socket.getInputStream());
                 var os = new ObjectOutputStream(socket.getOutputStream())) {

                System.out.println("Establishing connection from: " + socket.getRemoteSocketAddress());

                UserLogin user = (UserLogin) is.readObject();

                System.out.println("Check user: " + user.getUsername());

                MessageDigest instance = MessageDigest.getInstance("SHA-256");

                byte[] digest = instance.digest((user.getUsername() + ":" + user.getPassword() + ":" + LocalDateTime.now()).getBytes());

                String token = bytesToHex(digest);

                var auth = new Authorization(token);

                os.writeObject(auth);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            throw new ServerException(e);
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
