package ru.gb.core;

import ru.gb.chat.common.MessageLibrary;
import ru.gb.data.User;
import ru.gb.net.MessageSocketThread;
import ru.gb.net.MessageSocketThreadListener;

import java.net.Socket;

public class ClientSessionThread extends MessageSocketThread {

    private boolean isAuthorized = false;
    private User user;

    public ClientSessionThread(MessageSocketThreadListener listener, String name, Socket socket) {
        super(listener, name, socket);
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public void authAccept(User user) {
        this.user = user;
        this.isAuthorized = true;
        sendMessage(MessageLibrary.getAuthAcceptMessage(user.getNickname()));
        System.out.println(this.user);
    }

    public void authDeny() {
        sendMessage(MessageLibrary.getAuthDeniedMessage());
        close();
    }

    public void authError(String msg) {
        sendMessage(MessageLibrary.getMsgFormatErrorMessage(msg));
        close();
    }

    public User getUser(){
        return user;
    }

}