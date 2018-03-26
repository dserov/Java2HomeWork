package chat;

import java.util.ArrayList;
import java.util.Collections;

public class User {
    private int id;
    private String login;
    private String password;
    private String nickname;
    private boolean admin;
    private ArrayList<Integer> banned = new ArrayList<>();

    public User(int id, String login, String password, String nickname, boolean admin) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.nickname = nickname;
        this.admin = admin;
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

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public ArrayList<Integer> getBanned() {
        return banned;
    }

    public void setBanned(ArrayList<Integer> banned) {
        this.banned = banned;
    }

    @Override
    public String toString() {
        return getClass() + " {id: " + id + ", login: " + login + ", password: " + password + ", nickname: " + nickname + "}";
    }
}
