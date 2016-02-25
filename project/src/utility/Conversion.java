package utility;

import crypto.DES;

import java.util.Arrays;

/**
 * Created by Guan Huang on 11/25/2015.
 */
public class Conversion {

    /**
     * Convert message to its hex presentation
     *
     * @param message message
     * @return hex string
     */
    public static String hexEncode(String message) {
        String hexStr = "";
        for (int i = 0; i < message.length(); ++i) {
            String hex = String.format("%04x", (int) message.charAt(i));
            hexStr += hex;
        }
        return hexStr;
    }

    /**
     * Convert hex string to message
     *
     * @param hexStr hex representation
     * @return regular message
     */
    public static String hexDecode(String hexStr) {
        String message = "";
        for (int i = 4; i <= hexStr.length(); i += 4) {
            int extractedHex = Integer.parseInt(hexStr.substring(i - 4, i), 16);
            message += (char) extractedHex;
        }
        return message;
    }

    /**
     * From boolean array to hex string
     *
     * @param input boolean array
     * @return hex representation
     */
    public static String toHexString(boolean[] input) {
        String result = "";
        for (int i = 4; i <= input.length; i += 4) {
            String binaryString = toBinaryString(Arrays.copyOfRange(input, i - 4, i));
            int binaryInt = Integer.parseInt(binaryString, 2);
            result += Integer.toString(binaryInt, 16);
        }
        return result;
    }

    /**
     * From boolean array to binary string
     *
     * @param input boolean array
     * @return binary string representation
     */
    public static String toBinaryString(boolean[] input) {
        String result = "";
        for (boolean value : input) {
            result += (value) ? '1' : '0';
        }
        return result;
    }

    /**
     * From hex string to binary string
     *
     * @param hexString string in hex
     * @return string in binary
     */
    public static String toBinaryString(String hexString) {
        String binaryString = "";
        for (int i = 0; i < hexString.length(); ++i) {
            String binary = Integer.toBinaryString(Character.getNumericValue(hexString.charAt(i)));
            if (binary.length() < 4) {
                binaryString += DES.multiplyStr("0", 4 - binary.length()) + binary;
            } else {
                binaryString += binary;
            }
        }
        return binaryString;
    }

    /**
     * From binary string to boolean array
     *
     * @param binary string in binary
     * @return boolean array
     */
    public static boolean[] toBooleanArray(String binary) {
        boolean[] booleans = new boolean[binary.length()];
        for (int i = 0; i < binary.length(); ++i) {
            booleans[i] = binary.charAt(i) == '1';
        }
        return booleans;
    }


    private Conversion(){
        //do not create object
    }
}
