package crypto;

import static utility.Conversion.*;

/**
 * Mode of operation for private key scheme
 */
public class OperationMode {
    private SymmetricKey cryptoScheme;
    private boolean[] initializationVector = new boolean[64];
    private Types type;

    public enum Types {
        Electronic_Codebook, Cipher_Feedback, Output_Feedback, Counter
    }

    public OperationMode(SymmetricKey cryptoScheme, Types types) {
        this.cryptoScheme = cryptoScheme;
        this.type = types;
    }

    public void setType(Types types) {
        this.type = types;
    }

    public void setKey(String key) {
        this.cryptoScheme.setInitialKey(key);
    }

    public String randomHexKey(int length) {
        return toHexString(randomBoolArray(length * 4));
    }

    public String encryption(String message) {
        String cipherText = "";
        message = hexEncode(message);
        int messageSize = message.length();

        switch (type) {
            case Electronic_Codebook:
                for (int i = 0; i < messageSize; i++) {
                    if (i != 0 && i % 16 == 0) {
                        cipherText += cryptoScheme.encryption(message.substring(i - 16, i));
                    }
                }
                if (messageSize % 16 != 0) {
                    cipherText += cryptoScheme.encryption(message.substring(messageSize / 16 * 16));
                }
                return cipherText;
            case Cipher_Feedback:
                int blocks = (int) Math.ceil(messageSize / 16.0);
                if (blocks <= 1) {
                    setType(Types.Electronic_Codebook);
                    return (String) cryptoScheme.encryption(message);
                }

                //this.initializationVector = randomBoolArray(64);

                boolean[][] ciphers = new boolean[blocks][];
                ciphers[0] = cryptoScheme.encryption(initializationVector);

                int index = 0;
                for (int i = 16; i <= messageSize; i += 16) {
                    if (index != 0) {
                        ciphers[index] = cryptoScheme.encryption(ciphers[index - 1]);
                    }
                    String messagePart = message.substring(i - 16, i);
                    String binary = toBinaryString(messagePart);
                    ciphers[index] = SymmetricKey.XOR(ciphers[index],
                            toBooleanArray(binary));
                    cipherText += toHexString(ciphers[index]);
                    index++;
                }
                //grab remaining bits
                if (messageSize % 16 != 0) {
                    ciphers[index] = cryptoScheme.encryption(ciphers[index - 1]);
                    String remaining = message.substring(messageSize / 16 * 16);
                    String binary = toBinaryString(remaining);
                    String padding = DES.multiplyStr("0", 64 - binary.length());

                    ciphers[index] = SymmetricKey.XOR(ciphers[index],
                            toBooleanArray(binary + padding));
                    cipherText += toHexString(ciphers[index]);
                }
                return cipherText;
            default:
                throw new IllegalArgumentException("Invalid operation mode");
        }
    }

    public String decryption(String cipher) {
        String plainText = "";
        switch (type) {
            case Electronic_Codebook:
                for (int i = 16; i <= cipher.length(); i += 16) {
                    plainText += cryptoScheme.decryption(cipher.substring(i - 16, i));
                }
                return hexDecode(plainText);
            case Cipher_Feedback:
                int blockLength = (int) Math.ceil(cipher.length() / 16.0);
                boolean[][] messages = new boolean[blockLength][];
                boolean[] cipherBlock = new boolean[64];
                for (int i = 16; i <= cipher.length(); i += 16) {
                    int index = i / 16 - 1;
                    if (index == 0) {
                        messages[index] = cryptoScheme.encryption(initializationVector);
                    } else {
                        messages[index] = cryptoScheme.encryption(cipherBlock);
                    }
                    //hex cipher to binary cipher
                    String binaryCipher = toBinaryString(cipher.substring(i - 16, i));
                    cipherBlock = toBooleanArray(binaryCipher);
                    messages[index] = SymmetricKey.XOR(messages[index], cipherBlock);
                    plainText += toHexString(messages[index]);
                }
                return hexDecode(plainText);
            default:
                throw new IllegalArgumentException("Invalid operation mode");
        }
    }

    private boolean[] randomBoolArray(int length) {
        boolean[] random = new boolean[length];
        for (int i = 0; i < length; ++i) {
            random[i] = (Math.random() < 0.5);
        }
        return random;
    }
}
