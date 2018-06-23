package util;

public class MessageInterpreter {

    public static String getCode(String message) {
        return message.substring(0, 2);
    }
    public static int getLength(String message) {
        return Integer.valueOf(message.substring(2, 5));
    }
    public static String getData(String message) {
        return message.substring(5, message.length());
    }

}
