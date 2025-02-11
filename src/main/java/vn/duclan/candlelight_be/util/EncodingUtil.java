package vn.duclan.candlelight_be.util;

public class EncodingUtil {

    public static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            // Each byte need 2 characters (00 - FF)
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}