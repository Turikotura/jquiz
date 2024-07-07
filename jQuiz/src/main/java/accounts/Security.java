package accounts;

import java.nio.charset.StandardCharsets;
import java.security.*;

public class Security {
    /**
     * Turns array of bytes into a string
     * @param bytes Array of bytes
     * @return String that corresponds to bytes passed
     */
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

    /**
     * Function used to hash password for storing it safely
     * @param str Password we are hashing
     * @return Hash value corresponding to string passed
     */
    public static String getHash(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            return hexToString(md.digest(str.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Checks if password is at least 8 symbols long, and contains at least one lowercase letter, uppercase letter and a digit
     * @param password Potential password user entered
     * @return true if password is strong, false otherwise
     */
    public static boolean isStrong(String password) {
        return password.length() >= 8 && containsInRange(password,'A','Z') && containsInRange(password,'a','z') && containsInRange(password,'0','9');
    }

    /**
     * Checks if passed string contains at least one char from [l,r] interval
     * @param str String of characters
     * @param l left bound
     * @param r right bound
     * @return true if str contains at least one char from [l,r], false otherwise
     */
    private static boolean containsInRange(String str, char l, char r) {
        for(int i = 0; i < str.length(); i++) {
            if(str.charAt(i) >= l && str.charAt(i) <= r) return true;
        }
        return false;
    }
}
