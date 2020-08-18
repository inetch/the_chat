package ru.gb.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.gb.chat.common.Message;
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

    private final Logger logger = LogManager.getLogger(ChatServer.class);

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
        //System.out.println("Client connected");
        //logMessage("Client connected");
        logger.info("Client connected");
    }

    @Override
    public void onSocketAccepted(Socket socket) {
        ClientSessionThread session = new ClientSessionThread(this, "ClientSessionThread", socket);
        clients.add(session);
    }

    @Override
    public void onException(Throwable throwable) {
        logger.error(throwable);
    }

    @Override
    public void onException(Throwable throwable, MessageSocketThread thread) {
        logger.error("Exception in thread {}", thread,  throwable);
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
        logger.trace(msg);
        Message message = new Message(msg.split(MessageLibrary.DELIMITER));
        switch (message.msgType) {
            case REGULAR:
                for (ClientSessionThread client : clients) {
                    if (client.isAuthorized() && !client.equals(thread)) {
                        thread.sendMessage(MessageLibrary.getRegularMessage(thread.getUser().getNickname(), message.message, message.millis));
                    }
                }
                break;
            case CHANGENICK:
                authController.changeNickname(thread.getUser().getLogin(), message.nickname);
            default:
                logger.error("Unknown message: {}", message);
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
        thread.authAccept(authController.getUser(login));
    }

    @Override
    public void onSocketReady(MessageSocketThread thread){
        logger.info("Socket ready!");
    }

    @Override
    public void onSocketClosed(MessageSocketThread thread){
        logger.info("Socket closed");
        clients.remove(thread);
    }

    @Override
    public void onChatServerMessage(String message){
        logger.info(message);
    }

}
