package chat;

import com.sun.istack.internal.NotNull;

import java.io.IOException;
import java.net.ServerSocket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server implements TCPConnectionListener {
    public static void main(String[] args) {
        new Server();
    }

    private final AuthService authService;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm:ss");

    private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    private final Vector<TCPConnection> connections = new Vector<>();

    public Server() {
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
        // подключен новый клиент
        // Если на авторизуется за 120 секунд - отключим нафиг
        service.schedule(new TimeoutUnAuthConnection(tcpConnection), 120, TimeUnit.SECONDS);
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String value) {
        // при тупом разрыве соединения value == null
//        if (value == null) {
//            System.out.println("onReceiveString: value == null");
//            tcpConnection.disconnect();
//            return;
//        }

        System.out.println(tcpConnection.toString() + "(" + tcpConnection.getName() + "): " + value);
        if (value.equals("")) return;

        String parts[] = value.split(" ");
        String cmd = parts[0];

        // запрос на деавторизацию
        if (cmd.equals("/end")) {
            tcpConnection.sendString("/autherr Сброс авторизации");
            tcpConnection.setName("");
            connections.remove(tcpConnection);
            // Если на авторизуется за 120 секунд - отключим нафиг
            service.schedule(new TimeoutUnAuthConnection(tcpConnection), 120, TimeUnit.SECONDS);
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

        if (cmd.equals("/ban")) {
            // бан юзера
            banUser(parts, tcpConnection);
            // обновим список клиентов
            sendPrivateClientList(tcpConnection);
            return;
        }

        if (cmd.equals("/unban")) {
            // бан юзера
            unbanUser(parts, tcpConnection);
            // обновим список клиентов
            sendPrivateClientList(tcpConnection);
            return;
        }

        sendNicknameBroadcastMessage(value, tcpConnection);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        System.out.println("onDisconnect: " + tcpConnection.getName());
        String nickName = tcpConnection.getName();
        if (nickName.equals("")) {
            System.out.println("Отключился неизвестный клиент");
            return;
        }

        // удалим из списка авторизованных
        connections.remove(tcpConnection);

        // отключился авторизованный клиент
        sendNewClientList();

        System.out.println("onDisconnect:" + tcpConnection);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCPConnection exception: " + e);
    }

    /**
     * поместить в бан указанного пользователя
     *
     * @param parts
     * @param tcpConnection
     */
    private void banUser(String[] parts, TCPConnection tcpConnection) {
        if (parts.length != 2) {
            tcpConnection.sendString("Недостаточно параметров: " + String.join(" ", parts));
            return;
        }

        try {
            authService.addUserToBanList(tcpConnection.getName(), parts[1]);
        } catch (AuthServiceException e) {
            tcpConnection.sendString("Не удалось выполнить команду");
            System.out.println("Ошибка доступа к БД: " + e);
        }
    }

    /**
     * разбанить указанного пользователя
     *
     * @param parts
     * @param tcpConnection
     */
    private void unbanUser(String[] parts, TCPConnection tcpConnection) {
        if (parts.length != 2) {
            tcpConnection.sendString("Недостаточно параметров: " + String.join(" ", parts));
            return;
        }

        try {
            authService.deleteUserFromBanList(tcpConnection.getName(), parts[1]);
        } catch (AuthServiceException e) {
            tcpConnection.sendString("Не удалось выполнить команду");
            System.out.println("Ошибка доступа к БД: " + e);
        }
    }

    /**
     * Посылка всем только от авторизованного соединения
     *
     * @param message
     * @param conn
     */
    private synchronized void sendNicknameBroadcastMessage(String message, TCPConnection conn) {
        String nickName = conn.getName();
        if (nickName.isEmpty()) return;

        // может случиться, что получатель забанил отправителя
        try {
            String dt = LocalDateTime.now().format(dateTimeFormatter);
            for (TCPConnection entry : connections) {
                // кто-то может кого-то забанить
                Boolean isBanned = authService.checkUserInBanList(entry.getName(), conn.getName());
                if (isBanned) continue;
                entry.sendString(dt + " " + nickName + ": " + message);
            }
        } catch (AuthServiceException e) {
            System.out.println("Ошибка работы сервиса авторизации");
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

            // обновим список клиентов
            sendNewClientList();

            // а самому потоку скажем, что его ник такой-то
            conn.sendString("/authok " + user.getNickname());
        } catch (AuthServiceException e) {
            conn.sendString("/autherr На сервере неполадки. Попробуйте подключиться позже.");
        }
    }

    /**
     * Отправляем оновленные списки юзеров каждому клиенту по каждому
     */
    private synchronized void sendNewClientList() {
        for (TCPConnection c : connections) sendPrivateClientList(c);
    }

    /**
     * Отправка обновленного списка только для ЭТОГО соединения
     *
     * @param connection
     */
    private synchronized void sendPrivateClientList(@NotNull TCPConnection connection) {
        try {
            ArrayList<String> arrayList = new ArrayList<>();
            // формируем список только по текущему клиенту
            for (TCPConnection c : connections) {
                if (connection == c) continue;
                String checkNick = c.getName();
                Boolean isBanned = authService.checkUserInBanList(connection.getName(), c.getName());
                if (isBanned)
                    arrayList.add("!" + checkNick);
                else
                    arrayList.add(checkNick);
            }
            connection.sendString("/clientlist " + String.join(",", arrayList));
        } catch (AuthServiceException e) {
            connection.sendString("/autherr На сервере неполадки. Попробуйте подключиться позже.");
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
        String message = String.join(" ", newMsg);
        if (receiver == null) {
            // не нашелся. Отправим это сообщение отправителю, пусть запомнит его
            connection.sendString(message);
            return;
        }

        // разошлем сообщение отправителю и адресату
        String dt = LocalDateTime.now().format(dateTimeFormatter);

        // отправителю
        String nickName = connection.getName();
        connection.sendString(dt + " " + nickName + ": " + message);

        // если адресат и отправитель совпадают, то все.
        if (connection == receiver) return;

        // может случиться, что получатель забанил отправителя
        try {
            Boolean isBanned = authService.checkUserInBanList(receiver.getName(), connection.getName());
            if (isBanned) {
                connection.sendString(dt + " " + receiver.getName() + " поместил Вас в черный список.");
                return;
            }
        } catch (AuthServiceException e) {
            System.out.println("Ошибка работы сервиса авторизации");
        }

        // отправим получателю
        receiver.sendString(dt + " " + nickName + ": " + message);
    }
}
