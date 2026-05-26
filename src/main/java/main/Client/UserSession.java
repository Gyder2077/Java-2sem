package main.Client;

import main.Utils.*;

public class UserSession {
    private static UserSession instance;
    private String login;
    private String passwordHash;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) instance = new UserSession();
        return instance;
    }

    public void setCredentials(String login, String password) {
        this.login = login;
        this.passwordHash = PasswordHasher.hash(password);
    }

    public String getLogin() { return login; }
    public String getPasswordHash() { return passwordHash; }
    public boolean isAuthenticated() { return login != null && passwordHash != null; }
    public void clear() { login = null; passwordHash = null; }
}