package ru.gb.core;

import ru.gb.chat.common.MessageLibrary;
import ru.gb.net.MessageSocketThread;
import ru.gb.net.MessageSocketThreadListener;
import ru.gb.net.ServerSocketThread;
import ru.gb.net.ServerSocketThreadListener;

import java.net.Socket;
import java.util.Vector;

public class ChatServer implements ServerSocketThreadListener, MessageSocketThreadListener, ChatServerListener {

    private ServerSocketThread serverSocketThread;
    private ChatServerListener listener;
    private AuthController authController;
    private Vector<ClientSessionThread> clients = new Vector<>();

    public ChatServer(ChatServerListener listener) {
        this.listener = listener;
    }

    public void start(int port) {
        if (serverSocketThread != null && serverSocketThread.isAlive()) {
            return;
        }
        serverSocketThread = new ServerSocketThread(this,"Chat-Server-Socket-Thread", port, 2000);
        serverSocketThread.start();
        authController = new AuthController();
        authController.init();
    }

    public void stop() {
        if (serverSocketThread == null || !serverSocketThread.isAlive()) {
            return;
        }
        serverSocketThread.interrupt();
        authController.closeDBConnection();
    }

    private void logMessage(String msg) {
        listener.onChatServerMessage(msg);
    }

    @Override
    public void onClientConnected() {
        System.out.println("Client connected");
    }

    @Override
    public void onSocketAccepted(Socket socket) {
        clients.add(new ClientSessionThread(this, "ClientSessionThread", socket));
    }

    @Override
    public void onException(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void onException(Throwable throwable, MessageSocketThread thread) {
        throwable.printStackTrace();
    }

    @Override
    public void onClientTimeout(Throwable throwable) {

    }

    @Override
    public void onMessageReceived(String msg, MessageSocketThread thread) {
        if (((ClientSessionThread)thread).isAuthorized()) {
            processAuthorizedUserMessage(msg, (ClientSessionThread)thread);
        } else {
            processUnauthorizedUserMessage(msg, (ClientSessionThread)thread);
        }
    }

    private void processAuthorizedUserMessage(String msg, ClientSessionThread thread) {
        logMessage(msg);
        thread.sendMessage("echo: " + msg);
        for(ClientSessionThread client: clients){
            if(client.isAuthorized() && !client.equals(thread)) {
                thread.sendMessage(MessageLibrary.getBroadcastMessage(client.getNickname(), msg));
            }
        }
    }

    private void processUnauthorizedUserMessage(String msg, ClientSessionThread thread) {
        String[] arr = msg.split(MessageLibrary.DELIMITER);
        if (arr.length < 4 ||
                !arr[0].equals(MessageLibrary.AUTH_METHOD) ||
                !arr[1].equals(MessageLibrary.AUTH_REQUEST)) {
            thread.authError("Incorrect request: " + msg);
            return;
        }
        String login = arr[2];
        String password = arr[3];
        DBMain.actionResult res = authController.logIn(login, password);
        if(res != DBMain.actionResult.SUCCESS){
            System.out.println("user \"" + login + "\" failed to log in: " + res);
            thread.authDeny();
            return;
        }
        String nickname = authController.getNickname(login);
        thread.authAccept(authController.getUser(login));
    }

    @Override
    public void onSocketReady(MessageSocketThread thread){
        logMessage("Socket ready!");
    }

    @Override
    public void onSocketClosed(MessageSocketThread thread){
        logMessage("Socket closed");
        //thread.
        clients.remove(thread);
    }

    @Override
    public void onChatServerMessage(String message){
        System.out.println(message);
    }

}
