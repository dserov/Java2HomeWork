package chat;

import java.sql.*;

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
//        connection.setSchema("main");
    }

    public void disconnect() throws SQLException {
        connection.close();
    }

    public User getUserByLogin(String login) throws AuthServiceException {
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
                        resultSet.getString("nickname")
                );
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
                        resultSet.getString("nickname")
                );
        } catch (SQLException e) {
            throw new AuthServiceException("Authservice failed: " + e.getMessage());
        }
        return user;
    }

    public void addUser(User user) throws AuthServiceException {
        User userExist = getUserByNickname(user.getNickname());
        if (userExist != null) {
            throw new AuthServiceException("Nickname is already busy");
        }
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO users (id, login, password, nickname) VALUES (?, ?, ?, ?);");
            statement.setInt(1, user.getId());
            statement.setString(2, user.getLogin());
            statement.setString(3, user.getPassword());
            statement.setString(4, user.getNickname());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new AuthServiceException("Authservice failed: " + e.getMessage());
        }
    }
}

class AuthServiceException extends Exception {
    public AuthServiceException(String message) {
        super(message);
    }
}

