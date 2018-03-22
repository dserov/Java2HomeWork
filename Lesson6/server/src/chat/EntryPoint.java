package chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public class EntryPoint implements TCPConnectionListener {
    public static void main(String[] args) {
        new EntryPoint();
    }

    private final AuthService authService;

    // nickname -> tcpConnection pair
    private final Map<String, TCPConnection> connections = new HashMap<>();

    public EntryPoint() {
        System.out.println("Connecting to database...");
        try {
            authService = AuthService.getInstance();
            // add some users
//            authService.addUser(new User(0, "login1", "pass1", "nick1"));
//            authService.addUser(new User(0, "login2", "pass2", "nick2"));
//            authService.addUser(new User(0, "login3", "pass3", "nick3"));
//            authService.addUser(new User(0, "login4", "pass4", "nick4"));
//            authService.addUser(new User(0, "dserov", "dserov", "SystemShock"));
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
        // подключен новый клиент. потребуем от него авторизацию
        tcpConnection.sendString("/autherr");
//        connections.add(tcpConnection);
//        sendBroadcastMessage("Accepted new client, " + tcpConnection);
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String value) {
        if (value.equals("")) return;
        System.out.println(tcpConnection.toString() + ": " + value);

        String parts[] = value.split(" ");
        String cmd = parts[0];
        if (cmd.equals("/auth")) {
            // попытка авторизации
            tryToAuth(parts, tcpConnection);
            return;
        }

        // Проверка, что пользователь авторизован
        if (!connections.containsValue(tcpConnection)) {
            tcpConnection.sendString("/autherr Вам необходимо авторизоваться");
            return;
        }

        if (cmd.equals("/w")) {
            // отправка приватного сообщения
            sendPrivateMessage(parts, tcpConnection);
            return;
        }

        if (cmd.equals("/end")) {
            // отключение от сервера
            tcpConnection.disconnect();
            return;
        }

        sendAuthBroadcastMessage(value, tcpConnection);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        Map.Entry<String, TCPConnection> needleEntry = null;
        for (Map.Entry<String, TCPConnection> entry : connections.entrySet()) {
            if (entry.getValue().equals(tcpConnection)) {
                needleEntry = entry;
                break;
            }
        }
        if (needleEntry == null) {
            System.out.println("Отключился неизвестный клиент");
            return;
        }

        // отключился авторизованный клиент
        sendUnAuthBroadcastMessage("/end " + needleEntry.getKey());
        connections.remove(needleEntry.getKey());
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCPConnection exception: " + e);
    }

    /**
     * Посылка всем только от авторизованного соединения
     *
     * @param message
     * @param conn
     */
    private synchronized void sendAuthBroadcastMessage(String message, TCPConnection conn) {
        if (!connections.containsValue(conn)) {
            // неавторизованный!
            conn.sendString("/autherr Вам необходимо авторизоваться");
            return;
        }

//        System.out.println(message);
        for (Map.Entry<String, TCPConnection> entry : connections.entrySet()) {
            entry.getValue().sendString(message);
        }
    }

    /**
     * Посылка всем от неавторизованого соединения
     *
     * @param message
     */
    private synchronized void sendUnAuthBroadcastMessage(String message) {
//        System.out.println(message);
        for (Map.Entry<String, TCPConnection> entry : connections.entrySet())
            entry.getValue().sendString(message);
    }

    private synchronized void tryToAuth(String[] parts, TCPConnection conn) {
        try {
            // мало строк для работы
            if (parts.length != 3) {
                conn.sendString("/autherr Недостаточно параметров");
                return;
            }

            User user = authService.authorizeUser(parts[1], parts[2]);
            if (user == null) {
                conn.sendString("/autherr Не верный логин или пароль");
                return;
            }

            // юзер авторизовался, супер. проверим, чтбо это не была вторая паралельная авторизация
            if (connections.get(user.getNickname()) != null) {
                // вторая авторизация
                conn.sendString("/autherr Повторная авторизация");
                return;
            }

            // все классно
            connections.put(user.getNickname(), conn);

            // сообщим клиентам, что появился новый пользователь
            sendUnAuthBroadcastMessage("/begin " + user.getNickname());
        } catch (AuthServiceException e) {
            conn.sendString("/autherr На сервере неполадки. Попробуйте подключиться позже.");
        }
    }

    private synchronized void sendPrivateMessage(String parts[], TCPConnection connection) {
        if (parts.length < 3) {
            connection.sendString("Не верный формат приватного сообщения. \"/w <nickname> <сообщение>\"");
            return;
        }

        // ищем поток получателя по никнейму
        TCPConnection receiverConnection = connections.get(parts[1]);
        parts[0] = "";
        parts[1] = "";
        if (receiverConnection == null) {
            // не нашелся. Отправим это сообщение отправителю, пусть запомнит его
            connection.sendString(String.join(" ", parts));
            return;
        }

        // отправка получателю в новом потоке, чтоб избежать блокировок
        new Thread(new Runnable() {
            @Override
            public void run() {
                receiverConnection.sendString(String.join(" ", parts));
            }
        }).start();
    }
}
