package ru.gb.net;

public interface MessageSocketThreadListener {

    void onSocketReady(MessageSocketThread thread);
    void onSocketClosed(MessageSocketThread thread);
    void onMessageReceived(String msg, MessageSocketThread thread);
    void onException(Throwable throwable, MessageSocketThread thread);
}
