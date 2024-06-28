package accounts;

import java.nio.charset.StandardCharsets;
import java.security.*;

public class Security {
    public static String hexToString(byte[] bytes) {
        StringBuffer buff = new StringBuffer();
        for (int i=0; i<bytes.length; i++) {
            int val = bytes[i];
            val = val & 0xff;  // remove higher bits, sign
            if (val<16) buff.append('0'); // leading 0
            buff.append(Integer.toString(val, 16));
        }
        return buff.toString();
    }
    public static String getHash(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            return hexToString(md.digest(str.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
    public static boolean isStrong(String password) {
        return password.length() >= 8 && containsInRange(password,'A','Z') && containsInRange(password,'a','z') && containsInRange(password,'0','9');
    }
    private static boolean containsInRange(String str, char l, char r) {
        for(int i = 0; i < str.length(); i++) {
            if(str.charAt(i) >= l && str.charAt(i) <= r) return true;
        }
        return false;
    }
}
