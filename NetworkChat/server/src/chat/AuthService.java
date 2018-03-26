package chat;

import com.sun.istack.internal.NotNull;

import java.nio.charset.Charset;
import java.security.CryptoPrimitive;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Arrays;
import java.util.Base64;

public class AuthService {
    private final String connString = "jdbc:sqlite:server\\src\\chat\\chat.sqlite";
    private static AuthService instance;
    private Connection connection;

    public static synchronized AuthService getInstance() throws AuthServiceException {
        if (instance == null) {
            try {
                instance = new AuthService();
            } catch (SQLException e) {
                throw new AuthServiceException("Authservice failed: " + e.getMessage());
            } catch (ClassNotFoundException e) {
                throw new AuthServiceException("Authservice failed: " + e.getMessage());
            }
        }
        return instance;
    }

    private AuthService() throws ClassNotFoundException, SQLException {
        // подключимся к БД
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection(connString);
    }

    public void disconnect() throws SQLException {
        connection.close();
    }

    public User authorizeUser(String login, String password) throws AuthServiceException {
        User user = null;
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE login = ? LIMIT 1");
            statement.setString(1, login);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                user = new User(
                        resultSet.getInt("id"),
                        resultSet.getString("login"),
                        resultSet.getString("password"),
                        resultSet.getString("nickname"),
                        resultSet.getBoolean("admin")
                );

                // проверим соответствие пароля
                String encodedPassword = encodePassword(password);
                if (!encodedPassword.equals(user.getPassword()))
                    return null; // а пароль не совпал
            }
        } catch (SQLException e) {
            throw new AuthServiceException("Authservice failed: " + e.getMessage());
        }
        return user;
    }

    public User getUserByNickname(String nickname) throws AuthServiceException {
        User user = null;
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement("SELECT * FROM users WHERE nickname = ? LIMIT 1");
            statement.setString(1, nickname);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
                user = new User(
                        resultSet.getInt("id"),
                        resultSet.getString("login"),
                        resultSet.getString("password"),
                        resultSet.getString("nickname"),
                        resultSet.getBoolean("admin")
                );
        } catch (SQLException e) {
            throw new AuthServiceException("Authservice failed: " + e.getMessage());
        }
        return user;
    }

    public void addUser(@NotNull User user) throws AuthServiceException {
        User userExist = getUserByNickname(user.getNickname());
        if (userExist != null) {
            throw new AuthServiceException("Nickname is already busy");
        }
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO users (login, password, nickname) VALUES (?, ?, ?);");
            statement.setString(1, user.getLogin());
            statement.setString(2, encodePassword(user.getPassword()));
            statement.setString(3, user.getNickname());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new AuthServiceException("Authservice failed: " + e.getMessage());
        }
    }

    private String encodePassword(@NotNull String clearPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte arrClear[] = clearPassword.getBytes(Charset.forName("UTF8"));
            byte arrEncoded[] = md.digest(arrClear);
            return Base64.getEncoder().encodeToString(arrEncoded);
        } catch (NoSuchAlgorithmException e) {
            // нет алгоритма. печаль
            throw new RuntimeException(e);
        }
    }

    /**
     * Добавить юзера в бан лист. Проверку на дублирование записи о бане делать не будем, но помним, что это возможно.
     *
     * @param userNick
     * @param tobanNick
     * @throws AuthServiceException
     */
    public void addUserToBanList(String userNick, String tobanNick) throws AuthServiceException{
        User user = getUserByNickname(userNick);
        User toban = getUserByNickname(tobanNick);
        if (user == null || toban == null) throw new AuthServiceException("Ошибки в никах пользователей");
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO banlist (user_id, banned_id) VALUES (?, ?);");
            statement.setInt(1, user.getId());
            statement.setInt(2, toban.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new AuthServiceException("Authservice failed: " + e.getMessage());
        }

    }

    /**
     * Удалить юзера из списка забаненых
     *
     * @param userNick
     * @param unbanNick
     * @throws AuthServiceException
     */
    public void deleteUserFromBanList(String userNick, String unbanNick) throws AuthServiceException{
        User user = getUserByNickname(userNick);
        User unban = getUserByNickname(unbanNick);
        if (user == null || unban == null) throw new AuthServiceException("Ошибки в никах пользователей");
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM banlist WHERE user_id=? AND banned_id=?;");
            statement.setInt(1, user.getId());
            statement.setInt(2, unban.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new AuthServiceException("Authservice failed: " + e.getMessage());
        }
    }

    /**
     * Проверяем, checkList находится ли в бан-листе для userNick
     *
     * @param userNick
     * @param checkNick
     * @throws AuthServiceException
     */
    public boolean checkUserInBanList(String userNick, String checkNick) throws AuthServiceException {
        User user = getUserByNickname(userNick);
        User check = getUserByNickname(checkNick);
        if (user == null || check == null) throw new AuthServiceException("Ошибки в никах пользователей");
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT count(*) as is_banned FROM banlist WHERE user_id=? AND banned_id=?;");
            statement.setInt(1, user.getId());
            statement.setInt(2, check.getId());
            ResultSet resultSet = statement.executeQuery();
            boolean isBanned = false;
            if (resultSet.next()) {
                isBanned = (resultSet.getInt("is_banned") != 0);
            }
            return isBanned;
        } catch (SQLException e) {
            throw new AuthServiceException("Authservice failed: " + e.getMessage());
        }
    }
}
