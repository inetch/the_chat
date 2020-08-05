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

    public void setLastActionId(int lastActionId){
        this.lastActionId = lastActionId;
    }

    public int getLastActionId(){
        return this.lastActionId;
    }

    public void changeNick(String newNick){
        this.nickname = newNick;
    }

    public int getId(){
        return this.id;
    }

    @Override
    public String toString() {
        return this.nickname + "[" + this.login + "]";
    }
}
