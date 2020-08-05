package ru.gb.core;

import ru.gb.data.User;

import java.util.ArrayList;
import java.util.HashMap;

public class AuthController {

    HashMap<String, User> users = new HashMap<>();
    DBMain db = new DBMain("chat-server/chatserver.db");

    public void init() {
        try {
            db.connect();
        }catch (ClassNotFoundException e){
            throw new RuntimeException(e.getMessage());
        }
        for (User user : receiveUsers()) {
            users.put(user.getLogin(), user);
        }
    }

    public DBMain.actionResult logIn(String login, String password){
        DBMain.actionResult res = db.userLogIn(login, password);
        if(res == DBMain.actionResult.SUCCESS){
            users.get(login).setLastActionId(DBMain.loginAction);
        }
        return res;
    }

    public String getNickname(String login) {
        User user = users.get(login);
        if (user != null && user.getLastActionId() == DBMain.loginAction) {
            return user.getNickname();
        }
        return null;
    }

    public DBMain.actionResult changeNickname(String login, String newNickname){
        User user = users.get(login);
        if(user == null) {
            return DBMain.actionResult.INVALID_USER;
        }

        DBMain.actionResult res = db.changeNick(user.getId(), newNickname);
        if(res == DBMain.actionResult.SUCCESS){
            user.changeNick(newNickname);
        }

        return res;
    }

    private ArrayList<User> receiveUsers() {
        ArrayList<User> usersArr = db.receiveUsers();
        return usersArr;
    }

    public void closeDBConnection(){
        db.disconnect();
    }

    public User getUser(String login){
        return users.get(login);
    }

}
