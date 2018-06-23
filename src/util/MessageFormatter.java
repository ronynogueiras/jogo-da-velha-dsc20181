package util;

public class MessageFormatter {

    public static String format(String code, String data) {
        return code + String.format("%03d", data.length() + 2) + data;
    }

}
