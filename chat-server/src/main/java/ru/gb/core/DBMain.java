package ru.gb.core;

import ru.gb.data.User;

import java.sql.*;
import java.util.ArrayList;

public class DBMain {
    private Connection connection;
    private final String databaseFilename;

    private PreparedStatement getUserPassword;
    private PreparedStatement checkUserAction;
    private PreparedStatement logAction;
    private PreparedStatement getUserId;
    private PreparedStatement updateLastAction;
    private PreparedStatement getNicknameByUserName;
    private PreparedStatement getUsers;
    private PreparedStatement changeNickname;

    public static final int loginAction = 1;
    public static final int logoutAction = 0;
    public static final int changeNickAction = 2;

    public enum actionResult{
        SUCCESS
      , INVALID_USER
      , INVALID_PASSWORD
      , INVALID_ACTION
      , SQL_EXCEPTION
    }

    private void prepareStatements() throws SQLException {
        getUserPassword     = connection.prepareStatement("select id, password from usr_credential where user_name = ?");
        checkUserAction     = connection.prepareStatement("select u.access_level_id from usr_user u join usr_available_action aa on (aa.access_level_id = u.access_level_id) where u.id = ? and aa.action_id = ?");
        logAction           = connection.prepareStatement("insert into usr_action_log (user_id, access_level_id, action_id, is_successful) values (?, (select access_level_id from usr_user where id = ?), ?, ?)");
        getUserId           = connection.prepareStatement("select id from usr_credential where user_name = ?");
        updateLastAction    = connection.prepareStatement("update usr_user set last_successful_action_id = ? where id = ?");
        getNicknameByUserName   = connection.prepareStatement("select u.nickname from usr_user u join usr_credential c on (c.id = u.id) where c.user_name = ?");
        getUsers            = connection.prepareStatement("select c.id, c.user_name, u.nickname, u.last_successful_action_id from usr_credential c join usr_user u on (u.id = c.id)");
        changeNickname      = connection.prepareStatement("update usr_user set nickname = ? where id = ?");
    }

    public DBMain(String databaseFilename){
        this.databaseFilename = databaseFilename;
        try{
            connect();
        } catch (Exception e){
            e.printStackTrace();
            disconnect();
        }
    }

    public void connect() throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFilename);
            prepareStatements();
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
    }

    public void disconnect(){
        try {
            connection.close();
        }catch (SQLException throwable){
            throwable.printStackTrace();
        }
    }

    private void logUserAction(int userId, int actionId, boolean isSuccessful) throws SQLException{
        logAction.setInt(1, userId);
        logAction.setInt(2, userId);
        logAction.setInt(3, actionId);
        logAction.setInt(4, isSuccessful ? 1 : 0);
        logAction.executeUpdate();

        if(isSuccessful){
            updateLastAction.setInt(1, actionId);
            updateLastAction.setInt(2, userId);
            updateLastAction.executeUpdate();
        }
    }

    public actionResult userLogIn (String userName, String password){
        actionResult res = actionResult.SUCCESS;

        try {
            getUserPassword.setString(1, userName);
            ResultSet rs = getUserPassword.executeQuery();
            if(!rs.next()){
                rs.close();
                return actionResult.INVALID_USER;
            }

            String dbPass = rs.getString(2);
            int userId = rs.getInt(1);
            rs.close();

            if(!dbPass.equals(password)){
                logUserAction(userId, loginAction, false);
                return actionResult.INVALID_PASSWORD;
            }

            checkUserAction.setInt(1, userId);
            checkUserAction.setInt(2, loginAction);

            rs = checkUserAction.executeQuery();

            if(!rs.next()){
                logUserAction(userId, loginAction, false);
                rs.close();
                return actionResult.INVALID_ACTION;
            }else{
                logUserAction(userId, loginAction, true);
            }

            if (!rs.isClosed()) {
                rs.close();
            }

        } catch (SQLException sqle){
            sqle.printStackTrace();
            res = actionResult.SQL_EXCEPTION;
        }

        return res;
    }

    public actionResult userLogOut(String userName){
        actionResult res = actionResult.SUCCESS;
        try{
            getUserId.setString(1, userName);
            ResultSet rs = getUserId.executeQuery();
            if(!rs.first()){
                res = actionResult.INVALID_USER;
            }else{
                res = userLogOut(rs.getInt(1));
            }

            if(!rs.isClosed()){
                rs.close();
            }
        } catch (SQLException sqle){
            res = actionResult.SQL_EXCEPTION;
        }

        return res;
    }

    public String getNickname (String userName){
        String nick = "";
        try {
            getNicknameByUserName.setString(1, userName);
            ResultSet rs = getNicknameByUserName.executeQuery();
            if(rs.first()){
                nick = rs.getString(1);
            }
            rs.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return nick;
    }

    public actionResult userLogOut(int userId){
        actionResult res = actionResult.SUCCESS;
        try{
            logUserAction(userId, logoutAction, true);
        } catch (SQLException sqle){
            res = actionResult.SQL_EXCEPTION;
        }
        return res;
    }

    public actionResult changeNick(int userId, String newNickname){
        actionResult res = actionResult.SUCCESS;
        try{
            changeNickname.setString(1, newNickname);
            changeNickname.setInt(2, userId);
            changeNickname.executeUpdate();
            logUserAction(userId, changeNickAction, true);
        } catch (SQLException sqle){
            res = actionResult.SQL_EXCEPTION;
            try{
                logUserAction(userId, changeNickAction, false);
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        return res;
    }

    public ArrayList<User> receiveUsers() {
        ArrayList<User> usersArr = new ArrayList<>();
        //"select c.id, c.user_name, u.nickname, u.last_successful_action_id from usr_credential c join usr_user u on (u.id = c.id)");
        try {
            ResultSet rs = getUsers.executeQuery();
            while (rs.next()) {
                usersArr.add(new User(rs.getString(2), "", rs.getString(3), rs.getInt(1), rs.getInt(4)));
            }
            rs.close();
        }catch (SQLException e){
            e.printStackTrace();
        }

        return usersArr;
    }
}
