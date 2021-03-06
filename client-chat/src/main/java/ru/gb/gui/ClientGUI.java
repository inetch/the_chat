package ru.gb.gui;

import ru.gb.chat.common.Message;
import ru.gb.core.ChatFileSaver;
import ru.gb.net.MessageSocketThread;
import ru.gb.net.MessageSocketThreadListener;
import ru.gb.chat.common.MessageLibrary;
import ru.gb.gui.MessageSender;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;

public class ClientGUI extends JFrame implements ActionListener, Thread.UncaughtExceptionHandler, MessageSocketThreadListener, MessageSender {

    private static final int WIDTH = 400;
    private static final int HEIGHT = 300;

    private int preLoadedLines = 5;

    private final JPanel settingsPanel = new JPanel(new GridLayout(2, 3));
    private final JPanel messagePanel = new JPanel(new BorderLayout());

    private final JTextArea chatArea = new JTextArea();

    private final JTextField ipAddressField = new JTextField("127.0.0.1");
    private final JTextField portField = new JTextField("8181");
    private final JCheckBox cbAlwaysOnTop = new JCheckBox("Always on top", true);
    private final JTextField loginField = new JTextField("user1");
    private final JPasswordField passwordField = new JPasswordField("user1");
    private final JButton buttonLogin = new JButton("Login");

    private final JButton buttonDisconnect = new JButton("<html><b>Disconnect</b></html>");
    private final JTextField messageField = new JTextField();
    private final JButton buttonSend = new JButton("Send");
    private final JButton buttonTestThreads = new JButton("Test synch");

    private String nickName;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    private final JList<String> listUsers = new JList<>();

    private MessageSocketThread socketThread;

    private ChatFileSaver fileChat;

    private ThreadsTest threadsTest;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientGUI());
    }

    @Override
    public void textMessageSend(String message){
        putMessageToTextArea(nickName, message);
        socketThread.sendMessage(MessageLibrary.getRegularMessage(nickName, message));
    }

    private void sendMessage(){
        String message;
        if(messageField.getText().startsWith(MessageLibrary.TYPE_CHANGENICK + " ")){
            message = MessageLibrary.getChangenickMessage(messageField.getText().substring(MessageLibrary.TYPE_CHANGENICK.length() + 1));
            socketThread.sendMessage(message);
        }else {
            textMessageSend(messageField.getText());
        }
        messageField.setText("");
        messageField.grabFocus();
    }

    private void putMessageToTextArea(String userName, String message){
        String messageToChat = String.format("%s <%s>: %s%n", sdf.format(Calendar.getInstance().getTime()), userName, message);
        chatArea.append(messageToChat);
        fileChat.appendFile(messageToChat);
    }

    private void loadMessagesFromFile(){
        LinkedList<String> lines = fileChat.loadLastLines(preLoadedLines);
        for(String line : lines){
            chatArea.append(line + "\n");
        }
        chatArea.append("==========================\n");
    }

    private void initListeners(){
        cbAlwaysOnTop.addActionListener(this);

        messageField.addActionListener(
                (ActionEvent e) -> sendMessage()
        );

        buttonSend.addActionListener(
                (ActionEvent e) -> sendMessage()
        );

        buttonTestThreads.addActionListener(
                (ActionEvent e) -> threadsTest.start(5)
        );

        buttonLogin.addActionListener(
                (ActionEvent e) -> {
                    Socket socket = null;
                    try{
                        socket = new Socket(ipAddressField.getText(), Integer.parseInt(portField.getText()));
                        nickName = loginField.getText();
                        socketThread = new MessageSocketThread(this, "Client " + nickName, socket);
                        socketThread.start();
                        messagePanel.setVisible(true);
                        settingsPanel.setVisible(false);
                    } catch (IOException ioException){
                        showError(ioException.getMessage());
                    }
                }
        );

        buttonDisconnect.addActionListener(
                (ActionEvent e) -> socketThread.close()
        );
    }

    ClientGUI () {
        fileChat = new ChatFileSaver("chat_file.txt");

        Thread.setDefaultUncaughtExceptionHandler(this);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Chat");
        setSize(WIDTH, HEIGHT);
        setAlwaysOnTop(true);

        listUsers.setListData(new String[]{"user1", "user2", "user3", "user4",
                "user5", "user6", "user7", "user8", "user9", "user-with-too-long-name-in-this-chat"});
        JScrollPane scrollPaneUsers = new JScrollPane(listUsers);
        JScrollPane scrollPaneChatArea = new JScrollPane(chatArea);
        scrollPaneUsers.setPreferredSize(new Dimension(100, 0));

        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setEditable(false);

        settingsPanel.add(ipAddressField);
        settingsPanel.add(portField);
        settingsPanel.add(cbAlwaysOnTop);
        settingsPanel.add(loginField);
        settingsPanel.add(passwordField);
        settingsPanel.add(buttonLogin);
        messagePanel.add(buttonDisconnect, BorderLayout.WEST);
        messagePanel.add(messageField, BorderLayout.CENTER);

        JPanel messageButtonsPanel = new JPanel(new GridLayout(1, 2));
        messageButtonsPanel.add(buttonSend);
        messageButtonsPanel.add(buttonTestThreads);
        messagePanel.add(messageButtonsPanel, BorderLayout.EAST);

        add(scrollPaneChatArea, BorderLayout.CENTER);
        add(scrollPaneUsers, BorderLayout.EAST);
        add(settingsPanel, BorderLayout.NORTH);
        add(messagePanel, BorderLayout.SOUTH);

        initListeners();

        switchGUI(false);

        threadsTest = new ThreadsTest(this);

        setVisible(true);
    }

    private void showError(String errorMsg) {
        JOptionPane.showMessageDialog(this, errorMsg, "Exception!", JOptionPane.ERROR_MESSAGE);
    }

    private void switchGUI(boolean isConnected){
        messagePanel.setVisible(isConnected);
        settingsPanel.setVisible(!isConnected);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == cbAlwaysOnTop) {
            setAlwaysOnTop(cbAlwaysOnTop.isSelected());
        } else {
            throw new RuntimeException("Unsupported action: " + src);
        }
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace();
        StackTraceElement[] ste = e.getStackTrace();
        String msg = String.format("Exception in \"%s\": %s %s%n\t %s",
                t.getName(), e.getClass().getCanonicalName(), e.getMessage(), ste[0]);
        JOptionPane.showMessageDialog(this, msg, "Exception!", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void onMessageReceived(String msg, MessageSocketThread thread) {
        if(MessageLibrary.isFormattedMessage(msg)){
            Message message = MessageLibrary.parseMessage(msg);
            switch (message.msgType){
                case AUTH:
                    if(message.aType == MessageLibrary.authType.ACCEPT){
                        nickName = message.nickname;
                        loadMessagesFromFile();
                        putMessageToTextArea("System", "Welcome, " + nickName);
                    } else if (message.aType == MessageLibrary.authType.DENIED){
                        putMessageToTextArea("System", "Wrong credentials!");
                    } else {
                        putMessageToTextArea("System", "Incorrect auth message");
                    }
                    break;
                case BROADCAST:
                    putMessageToTextArea(message.nickname, "[BC] " + message.message);
                    break;
                case REGULAR:
                    putMessageToTextArea(message.nickname, "[" + message.millis + "]" + message.message);
                    break;
                case ERROR:
                default:
                    putMessageToTextArea("System", "Incorrect message");
            }
        } else {
            putMessageToTextArea("raw message", msg);
        }

    }

    @Override
    public void onSocketReady(MessageSocketThread thread){
        switchGUI(true);
        socketThread.sendMessage(MessageLibrary.getAuthRequestMessage(loginField.getText(), new String(passwordField.getPassword())));
    }

    @Override
    public void onSocketClosed(MessageSocketThread thread){
        switchGUI(false);
    }

    @Override
    public void onException(Throwable throwable, MessageSocketThread thread) {
        throwable.printStackTrace();
        showError(throwable.getMessage());
    }

}
