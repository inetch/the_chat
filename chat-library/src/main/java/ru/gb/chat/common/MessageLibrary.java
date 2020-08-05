package ru.gb.chat.common;

public class MessageLibrary {

    /*
     * /auth|request|login|password
     * /auth|accept|nickname
     * /auth|denied
     * /broadcast|msg
     *
     * /msg_format_error|msg
     * */

    public enum messageType{
        AUTH
      , BROADCAST
      , ERROR
      , CHANGENICK
      , REGULAR
    }

    public enum authType{
        REQUEST
      , ACCEPT
      , DENIED
    }

    public static final String DELIMITER = "##";
    public static final String AUTH_METHOD = "/auth";
    public static final String AUTH_REQUEST = "request";
    public static final String AUTH_ACCEPT = "accept";
    public static final String AUTH_DENIED = "denied";
    /* если мы вдруг не поняли, что за сообщение и не смогли разобрать */
    public static final String TYPE_BROADCAST = "/broadcast";
    public static final String TYPE_CHANGENICK = "/changenick";
    public static final String TYPE_REGULAR = "/regular";

    /* то есть сообщение, которое будет посылаться всем */
    public static final String MSG_FORMAT_ERROR = "/msg_format_error";

    public static String getAuthRequestMessage(String login, String password) {
        return AUTH_METHOD + DELIMITER + AUTH_REQUEST + DELIMITER + login + DELIMITER + password;
    }

    public static String getAuthAcceptMessage(String nickname) {
        return AUTH_METHOD + DELIMITER + AUTH_ACCEPT + DELIMITER + nickname;
    }

    public static String getChangenickMessage(String newNickname){
        return TYPE_CHANGENICK + DELIMITER + newNickname;
    }

    public static String getAuthDeniedMessage() {
        return AUTH_METHOD + DELIMITER + AUTH_DENIED;
    }

    public static String getMsgFormatErrorMessage(String message) {
        return MSG_FORMAT_ERROR + DELIMITER + message;
    }

    public static String getBroadcastMessage(String src, String message) {
        return TYPE_BROADCAST + DELIMITER + System.currentTimeMillis() +
                DELIMITER + src + DELIMITER + message;
    }

    public static String getRegularMessage(String nickName, String message){
        return TYPE_REGULAR + DELIMITER + System.currentTimeMillis() +
                DELIMITER + nickName + DELIMITER + message;
    }

    public static String getRegularMessage(String nickName, String message, String millis){
        return TYPE_REGULAR + DELIMITER + millis +
                DELIMITER + nickName + DELIMITER + message;
    }

    public static boolean isFormattedMessage(String message){
        return message.startsWith(AUTH_METHOD) || message.startsWith(TYPE_BROADCAST) || message.startsWith(MSG_FORMAT_ERROR);
    }

    public static Message parseMessage(String message){
        return new Message(message.split(DELIMITER));
    }

}
