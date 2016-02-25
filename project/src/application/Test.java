package application;

import crypto.DES;
import crypto.OperationMode;
import crypto.RSA;

import java.math.BigInteger;

/**
 * Created by Guan Huang on 11/26/2015.
 */
public class Test {
    public static void main(String[] args) {
        DES des = new DES();
        des.setInitialKey("1234567890abcdef");
        OperationMode operationMode = new OperationMode(des,
                OperationMode.Types.Cipher_Feedback);

        String cipher = operationMode.encryption("cryptography is an interesting subject!");
        System.out.println(cipher);
        String plainText = operationMode.decryption(cipher);
        System.out.println(plainText);

        DES des2 = new DES();
        des2.setInitialKey("1234567890abcdef");
        System.out.println(des2.decryption(des2.encryption("abdc222")));

        RSA rsa = new RSA();
        BigInteger s = rsa.encryption(new BigInteger("hello".getBytes()));
        System.out.println(new String(rsa.decryption(s).toByteArray()));
    }
}
