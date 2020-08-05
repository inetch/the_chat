package ru.gb.data;

public class User {
    private String login;
    private String password;
    private String nickname;
    private int id;
    private int lastActionId;

    public User(String login, String password, String nickname, int id, int lastActionId) {
        this.login = login;
        this.password = password;
        this.nickname = nickname;
        this.id = id;
        this.lastActionId = lastActionId;
    }

    public String getLogin() {
        return login;
    }

    public String getNickname() {
        return nickname;
    }

    public boolean isPasswordCorrect(String password) {
        return this.password.equals(password);
    }

    public void setLastActionId(int lastActionId){
        this.lastActionId = lastActionId;
    }

    public int getLastActionId(){
        return this.lastActionId;
    }
}
