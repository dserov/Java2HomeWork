package chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class EntryPoint implements TCPConnectionListener {
    public static void main(String[] args) {
        new EntryPoint();
    }

    private final ArrayList<TCPConnection> connections = new ArrayList<>();
    private final AuthService authService;

    public EntryPoint() {
        System.out.println("Connecting to database...");
        try {
            authService = AuthService.getInstance();
            // add some users
//            User user = authService.getUserByNickname("nick1");
//            if (user == null)
//                System.out.println("user not found");
//            else
//                System.out.println(user);
//            authService.addUser(new User(1, "login1", "pass1", "nick1"));
//            authService.addUser(new User(2, "login2", "pass2", "nick2"));
//            authService.addUser(new User(3, "login3", "pass3", "nick3"));
//            authService.addUser(new User(4, "login4", "pass4", "nick4"));
//            authService.addUser(new User(5, "login5", "pass5", "nick5"));
        } catch (AuthServiceException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Server running...");
        try (ServerSocket serverSocket = new ServerSocket(8189)) {
            while (true) {
                try {
                    new TCPConnection(this, serverSocket.accept());
                } catch (IOException e) {
                    System.out.println("TCPConnection exception: " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        sendBroadcastMessage("Accepted new client, " + tcpConnection);
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String value) {
        sendBroadcastMessage(value);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        sendBroadcastMessage("Отключен клиент");
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCPConnection exception: " + e);
    }

    private void sendBroadcastMessage(String message) {
        System.out.println(message);
        for (TCPConnection o : connections) o.sendString(message);
    }
}
