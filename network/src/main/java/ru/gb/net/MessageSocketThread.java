package ru.gb.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MessageSocketThread extends Thread {

    private Socket socket;
    private MessageSocketThreadListener listener;
    private DataOutputStream out;
    private DataInputStream in;

    public MessageSocketThread(MessageSocketThreadListener listener, String name, Socket socket) {
        super(name);
        this.socket = socket;
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            in  = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            listener.onSocketReady(this);

            while (!isInterrupted()) {
                listener.onMessageReceived(in.readUTF(), this);
            }

        } catch (IOException e) {
            close();
            System.out.println(e);
        } finally {
            close();
        }
    }

    public void sendMessage(String message) {
        //System.out.println("sendMessage: " + message);
        try {
            if (!socket.isConnected() || socket.isClosed()) {
                listener.onException(new RuntimeException("Socked closed or not initialized"), this);
                return;
            }

            out.writeUTF(message);

        } catch (IOException e) {
            close();
            listener.onException(e, this);
        }
    }

    public synchronized void close() {
        interrupt();
        try {
            if (out != null) {
                out.close();
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        listener.onSocketClosed(this);
    }
}
