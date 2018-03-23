package chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EntryPoint implements TCPConnectionListener {
    public static void main(String[] args) {
        new EntryPoint();
    }

    private final AuthService authService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final Vector<TCPConnection> connections = new Vector<>();

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
        tcpConnection.sendString("/autherr Требуется авторизация");
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String value) {
        // при тупом разрыве соединения value == null
        if (value == null) {
            tcpConnection.disconnect();
            return;
        }

//        System.out.println(tcpConnection.toString() + "(" + tcpConnection.getName() + "): " + value);
        if (value.equals("")) return;

        String parts[] = value.split(" ");
        String cmd = parts[0];

        // запрос на отключение
        if (cmd.equals("/end")) {
            // отключение от сервера
            tcpConnection.disconnect();
            return;
        }

        if (cmd.equals("/auth")) {
            // попытка авторизации
            tryToAuth(parts, tcpConnection);
            return;
        }

        // Проверка, что пользователь авторизован
        if (tcpConnection.getName().equals("")) {
            tcpConnection.sendString("/autherr Вам необходимо авторизоваться");
            return;
        }

        if (cmd.equals("/w")) {
            // отправка приватного сообщения
            sendPrivateMessage(parts, tcpConnection);
            return;
        }

        sendNicknameBroadcastMessage(value, tcpConnection);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        String nickName = tcpConnection.getName();
        if (nickName.equals("")) {
            System.out.println("Отключился неизвестный клиент");
            return;
        }

        // удалим из списка авторизованных
        connections.remove(tcpConnection);

        // отключился авторизованный клиент
        sendBroadcastMessage("/end " + nickName);
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
    private synchronized void sendNicknameBroadcastMessage(String message, TCPConnection conn) {
        LocalDateTime localDateTime = LocalDateTime.now();
        String dt = localDateTime.format(formatter);
        String nickName = conn.getName();
        if (nickName.equals("")) return;
        for (TCPConnection entry : connections) {
            entry.sendString(dt + " " + nickName + ": " + message);
        }
    }

    /**
     * Посылка всем от неавторизованого соединения
     *
     * @param message
     */
    private synchronized void sendBroadcastMessage(String message) {
        for (TCPConnection entry : connections) entry.sendString(message);
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

            // юзер авторизовался, супер. проверим, чтоб это не была вторая паралельная авторизация
            TCPConnection connection = null;
            for (TCPConnection item : connections) {
                if (item.getName().equals(user.getNickname())) {
                    // вторая авторизация
                    conn.sendString("/autherr Повторная авторизация");
                    return;
                }
            }

            // все классно, первичная авторизация
            conn.setName(user.getNickname());
            connections.add(conn);

            // сообщим клиентам, что появился новый пользователь
            sendBroadcastMessage("/begin " + user.getNickname());
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
        TCPConnection receiver = null;
        for (TCPConnection item : connections) {
            if (item.getName().equals(parts[1])) {
                receiver = item;
                break;
            }
        }

        // уберем первые 2 части из parts
        String newMsg[] = new String[parts.length - 2];
        System.arraycopy(parts, 2, newMsg, 0, newMsg.length);
        if (receiver == null) {
            // не нашелся. Отправим это сообщение отправителю, пусть запомнит его
            connection.sendString(String.join(" ", newMsg));
            return;
        }

        // разошлем сообщение отправителю и адресату
        LocalDateTime localDateTime = LocalDateTime.now();
        String dt = localDateTime.format(formatter);

        // отправителю
        String nickName = connection.getName();
        connection.sendString(dt + " " + nickName + ": " + String.join(" ", newMsg));

        // получателю
        receiver.sendString(dt + " " + nickName + ": " + String.join(" ", newMsg));
    }
}
