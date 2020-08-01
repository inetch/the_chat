package ru.gb.chat.common;

public class Message {
    public final MessageLibrary.messageType msgType;
    public final MessageLibrary.authType aType;
    public final String login;
    public final String password;
    public final String nickname;
    public final String message;
    public final String millis;

    public Message(String[] lines) {
        MessageLibrary.messageType msgType;
        MessageLibrary.authType aType;

        String login = "";
        String password = "";
        String nickname = "";
        String message = "";
        String millis = "";

        try {
            switch (lines[0]) {
                case "/auth":
                    msgType = MessageLibrary.messageType.AUTH;
                    switch (lines[1]) {
                        case "request":
                            aType = MessageLibrary.authType.REQUEST;
                            break;
                        case "accept":
                            aType = MessageLibrary.authType.ACCEPT;
                            break;
                        case "denied":
                        default:
                            aType = MessageLibrary.authType.DENIED;
                    }
                    break;
                case "/broadcast":
                    msgType = MessageLibrary.messageType.BROADCAST;
                    aType = null;
                    break;
                case "/msg_format_error":
                default:
                    msgType = MessageLibrary.messageType.ERROR;
                    aType = null;
            }

            switch (msgType) {
                case AUTH:
                    switch (aType) {
                        case REQUEST:
                            login = lines[2];
                            password = lines[3];
                            nickname = "";
                            message = "";
                            millis = "";
                            break;
                        case ACCEPT:
                            login = "";
                            password = "";
                            nickname = lines[2];
                            message = "";
                            millis = "";
                            break;
                        case DENIED:
                        default:
                            login = "";
                            password = "";
                            nickname = "";
                            message = "";
                            millis = "";
                    }
                    break;
                case BROADCAST:
                    login = "";
                    password = "";
                    nickname = lines[2];
                    message = lines[3];
                    millis = lines[1];
            }
        } catch (IndexOutOfBoundsException e) {
            msgType = MessageLibrary.messageType.ERROR;
            aType = null;
        }

        this.msgType = msgType;
        this.aType = aType;
        this.login = login;
        this.password = password;
        this.nickname = nickname;
        this.message = message;
        this.millis = millis;
    }
}