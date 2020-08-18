package ru.gb.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerSocketThread extends Thread {

    private final int port;
    private final int timeout;
    private final ServerSocketThreadListener listener;

    private final Logger logger = LogManager.getLogger(ServerSocketThread.class);


    public ServerSocketThread(ServerSocketThreadListener listener, String name, int port, int timeout) {
        super(name);
        this.port = port;
        this.listener = listener;
        this.timeout = timeout;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setSoTimeout(timeout);
            logger.info("{} running on port {}", getName(), port);
            while (!isInterrupted()) {
                System.out.println("Waiting for connect");
                try {
                    Socket socket = serverSocket.accept();
                    listener.onSocketAccepted(socket);
                } catch (SocketTimeoutException e) {
                    listener.onClientTimeout(e);
                    continue;
                }
                listener.onClientConnected();
            }
        } catch (IOException e) {
            logger.fatal(e);
            listener.onException(e);
        }
    }
}
