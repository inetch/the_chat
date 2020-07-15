package ru.gb.core;

import ru.gb.net.MessageSocketThread;
import ru.gb.net.MessageSocketThreadListener;
import ru.gb.net.ServerSocketThread;
import ru.gb.net.ServerSocketThreadListener;

import java.net.Socket;

public class ChatServer implements ServerSocketThreadListener, MessageSocketThreadListener {

    private ServerSocketThread serverSocketThread;
    private MessageSocketThread socket;

    public void start(int port) {
        if (serverSocketThread != null && serverSocketThread.isAlive()) {
            return;
        }
        serverSocketThread = new ServerSocketThread(this,"Chat-Server-Socket-Thread", port, 2000);
        serverSocketThread.start();
    }

    public void stop() {
        if (serverSocketThread == null || !serverSocketThread.isAlive()) {
            return;
        }
        serverSocketThread.interrupt();
    }

    @Override
    public void onClientConnected() {
        System.out.println("Client connected");
    }

    @Override
    public void onSocketAccepted(Socket socket) {
        this.socket = new MessageSocketThread(this, "ServerSocket", socket);
    }

    @Override
    public void onException(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void onClientTimeout(Throwable throwable) {

    }

    @Override
    public void onMessageReceived(String msg) {
        System.out.println(msg);
        socket.sendMessage("echo: " + msg);
    }


    /*public static void main(String[] args) {
        serverSocketThread = new ServerSocketThread();
        serverSocketThread.start();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + ": running");
            }
        });
       t.start();
        try {
            Thread.currentThread().sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        serverSocketThread.interrupt();
        System.out.println(Thread.currentThread().getName() + ": Main");
    }*/
}
