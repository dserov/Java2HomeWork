package chat;

public class User {
    private int id;
    private String login;
    private String password;
    private String nickname;

    public User(int id, String login, String password, String nickname) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.nickname = nickname;
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    @Override
    public String toString() {
        return getClass() + " {id: " + id + ", login: " + login + ", password: " + password + ", nickname: " + nickname + "}";
    }
}
