package crypto;

import java.util.Arrays;

import static utility.Conversion.*;

/**
 * Created by Guan Huang on 11/24/2015.
 */
abstract class SymmetricKey<T, R> {

    public abstract boolean[] encryption(boolean[] plainText);

    public abstract R encryption(T plainText);

    public abstract R decryption(T cipherText);

    /**
     * @precondition input1.length() == input2.length()
     */
    public static boolean[] XOR(boolean[] input1, boolean[] input2) {
        boolean[] output = new boolean[input1.length];
        for (int i = 0; i < input1.length; ++i) {
            output[i] = input1[i] ^ input2[i];
        }
        return output;
    }

    public static boolean[] leftShiftBits(boolean[] original, int numOfBits) {
        boolean[] shiftedArray = new boolean[original.length];
        boolean[] tmp = Arrays.copyOfRange(original, 0, numOfBits);

        for (int i = numOfBits; i < original.length; i++) {
            shiftedArray[i - numOfBits] = original[i];
        }
        int i = numOfBits;
        for (boolean value : tmp) {
            shiftedArray[original.length - i] = value;
            i--;
        }
        return shiftedArray;
    }

    public static boolean[] rightShiftBits(boolean[] original, int numOfBits) {
        boolean[] shiftedArray = new boolean[original.length];
        boolean[] tmp = Arrays.copyOfRange(original, original.length - numOfBits,
                original.length);

        for (int i = numOfBits; i < original.length; i++) {
            shiftedArray[i] = original[i - numOfBits];
        }

        for (int i = 0; i < tmp.length; ++i) {
            shiftedArray[i] = tmp[i];
        }
        return shiftedArray;
    }

    public abstract void setInitialKey(T key);

    enum Mode {
        ENCRYPTION, DECRYPTION
    }
}

public class DES extends SymmetricKey<String, String> {
    private boolean[] initialKey = new boolean[64];
    private boolean[][] subKeys = new boolean[16][48];
    private String padding;

    /**
     * Overloaded functions for transferring keys to boolean array
     *
     * @param initialKey
     */
    public void setInitialKey(boolean[] initialKey) {
        this.initialKey = initialKey;
    }

    public void setInitialKey(String key) {
        this.initialKey = toBooleanArray(toBinaryString(key));
    }

    /**
     * DES Key Scheduler
     * Generating 16 sub-keys
     */
    private void keyGeneration(Mode mode) {
        boolean[] pc1 = permutate(initialKey, PERMUTATION_TABLE_56);
        boolean[] left = Arrays.copyOfRange(pc1, 0, pc1.length / 2);
        boolean[] right = Arrays.copyOfRange(pc1, pc1.length / 2, pc1.length);

        for (int i = 0; i < NUMBER_OF_ROUNDS; ++i) {
            if (mode.equals(Mode.ENCRYPTION)) {
                left = leftShiftBits(left, SHIFT[i]);
                right = leftShiftBits(right, SHIFT[i]);
            } else if (mode.equals(Mode.DECRYPTION)) {
                //no shift first round
                if (i != 0) {
                    left = rightShiftBits(left, SHIFT[i]);
                    right = rightShiftBits(right, SHIFT[i]);
                }
            }
            subKeys[i] = permutate(combine(left, right), PERMUTATION_TABLE_48);
        }
    }

    /**
     * @param plainText in hex format
     */
    @Override
    public String encryption(String plainText) {
        int blockSize = plainText.length();
        //less than 64 bits pad 0
        if (blockSize < 16) {
            padding = multiplyStr("0", 16 - blockSize);
            plainText = plainText + padding;
        }
        //generate 16 sub-keys
        keyGeneration(Mode.ENCRYPTION);
        boolean[] input = toBooleanArray(toBinaryString(plainText));
        return toHexString(processFeistel(input));
    }

    /**
     * @param cipherText in hex format
     */
    @Override
    public String decryption(String cipherText) {
        if (cipherText.length() < 16) {
            throw new IllegalArgumentException("Internal Error");
        }
        keyGeneration(Mode.DECRYPTION);
        //remove padded 0s
        boolean[] input = toBooleanArray(toBinaryString(cipherText));
        return toHexString(processFeistel(input)).replaceAll(String.format("%s$", padding), "");
    }

    public boolean[] encryption(boolean[] plainText) {
        keyGeneration(Mode.ENCRYPTION);
        return processFeistel(plainText);
    }

    /**
     * Same operation for encryption and decryption
     *
     * @param input 64 bits cipher or plaintext
     * @return result of the F-function
     */
    private boolean[] processFeistel(boolean[] input) {
        boolean[] cipher = permutate(input, INITIAL_PERMUTATION);
        boolean[] left = Arrays.copyOfRange(cipher, 0, cipher.length / 2);
        boolean[] right = Arrays.copyOfRange(cipher, cipher.length / 2, cipher.length);

        for (int i = 0; i < 16; ++i) {
            boolean[] previousLeft = Arrays.copyOf(left, left.length);
            left = Arrays.copyOf(right, right.length);
            //Expansion function: from 32 bits to 48 bits
            boolean[] XOROutput = XOR(subKeys[i], permutate(right, EXPANSION));
            //S-box look up table
            String sBoxOutput = "";
            for (int j = 6; j <= XOROutput.length; j += 6) {
                //grab 6 binary digits each time
                String partialOutput = sBoxSubstitution(
                        Arrays.copyOfRange(XOROutput, j - 6, j), j / 6 - 1);
                //pad 0 in front of binary string
                sBoxOutput += multiplyStr("0", 4 - partialOutput.length()) + partialOutput;
            }
            right = XOR(previousLeft, permutate(toBooleanArray(sBoxOutput), PERMUTATION_TABLE_32));
        }
        return permutate(combine(right, left), FINAL_PERMUTATION);
    }

    public static boolean[] combine(boolean[] left, boolean[] right) {
        int leftLen = left.length;
        int rightLen = right.length;
        boolean[] combined = new boolean[leftLen + rightLen];
        System.arraycopy(left, 0, combined, 0, leftLen);
        System.arraycopy(right, 0, combined, leftLen, rightLen);
        return combined;
    }

    private boolean[] generate56bitsKey(final boolean[] booleanArray) {
        int keyIdx = 0;
        boolean[] booleans = new boolean[56];
        for (int i = 0; i < booleanArray.length; ++i) {
            if ((i + 1) % 8 != 0) {
                booleans[keyIdx] = booleanArray[i];
                keyIdx++;
            }
        }
        return booleans;
    }

    private static String sBoxSubstitution(boolean[] input, int tableIndex) {
        int col = Integer.parseInt(toBinaryString(Arrays.copyOfRange(input, 1, 5)), 2);
        boolean[] temp = new boolean[]{input[0], input[5]};
        int row = Integer.parseInt(toBinaryString(temp), 2);
        return Integer.toBinaryString(S_BOXES[tableIndex][col + 16 * row]);
    }

    private static boolean[] permutate(boolean[] original, final int[] permutationTable) {
        boolean[] permutedKeys = new boolean[permutationTable.length];
        for (int i = 0; i < permutationTable.length; ++i) {
            permutedKeys[i] = original[permutationTable[i] - 1];
        }
        return permutedKeys;
    }

    /**
     * Multiple a substring in a specified number of times
     *
     * @param content sub string to be repeated
     * @param repeat  number of times
     * @return result
     */
    public static String multiplyStr(String content, int repeat) {
        String result = "";
        for (int j = 0; j < repeat; ++j) {
            result = result.concat(content);
        }
        return result;
    }

    final static private int NUMBER_OF_ROUNDS = 16;

    final static private int[] SHIFT = {
            1, 1, 2, 2, 2, 2, 2, 2,
            1, 2, 2, 2, 2, 2, 2, 1
    };

    final static private int[] PERMUTATION_TABLE_56 = {
            57, 49, 41, 33, 25, 17, 9,
            1, 58, 50, 42, 34, 26, 18,
            10, 2, 59, 51, 43, 35, 27,
            19, 11, 3, 60, 52, 44, 36,
            63, 55, 47, 39, 31, 23, 15,
            7, 62, 54, 46, 38, 30, 22,
            14, 6, 61, 53, 45, 37, 29,
            21, 13, 5, 28, 20, 12, 4
    };

    final static private int[] PERMUTATION_TABLE_48 = {
            14, 17, 11, 24, 1, 5,
            3, 28, 15, 6, 21, 10,
            23, 19, 12, 4, 26, 8,
            16, 7, 27, 20, 13, 2,
            41, 52, 31, 37, 47, 55,
            30, 40, 51, 45, 33, 48,
            44, 49, 39, 56, 34, 53,
            46, 42, 50, 36, 29, 32
    };

    final static private int[] PERMUTATION_TABLE_32 = {
            16, 7, 20, 21,
            29, 12, 28, 17,
            1, 15, 23, 26,
            5, 18, 31, 10,
            2, 8, 24, 14,
            32, 27, 3, 9,
            19, 13, 30, 6,
            22, 11, 4, 25
    };

    final static private int[] INITIAL_PERMUTATION = {
            58, 50, 42, 34, 26, 18, 10, 2,
            60, 52, 44, 36, 28, 20, 12, 4,
            62, 54, 46, 38, 30, 22, 14, 6,
            64, 56, 48, 40, 32, 24, 16, 8,
            57, 49, 41, 33, 25, 17, 9, 1,
            59, 51, 43, 35, 27, 19, 11, 3,
            61, 53, 45, 37, 29, 21, 13, 5,
            63, 55, 47, 39, 31, 23, 15, 7
    };

    final static private int[] FINAL_PERMUTATION = {
            40, 8, 48, 16, 56, 24, 64, 32,
            39, 7, 47, 15, 55, 23, 63, 31,
            38, 6, 46, 14, 54, 22, 62, 30,
            37, 5, 45, 13, 53, 21, 61, 29,
            36, 4, 44, 12, 52, 20, 60, 28,
            35, 3, 43, 11, 51, 19, 59, 27,
            34, 2, 42, 10, 50, 18, 58, 26,
            33, 1, 41, 9, 49, 17, 57, 25
    };

    final static private int[] EXPANSION = {
            32, 1, 2, 3, 4, 5,
            4, 5, 6, 7, 8, 9,
            8, 9, 10, 11, 12, 13,
            12, 13, 14, 15, 16, 17,
            16, 17, 18, 19, 20, 21,
            20, 21, 22, 23, 24, 25,
            24, 25, 26, 27, 28, 29,
            28, 29, 30, 31, 32, 1
    };

    final static private int[][] S_BOXES = {
            {14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7,
                    0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8,
                    4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0,
                    15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13
            },
            {15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10,
                    3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5,
                    0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15,
                    13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9
            },
            {10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8,
                    13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1,
                    13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7,
                    1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12
            },
            {7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15,
                    13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9,
                    10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4,
                    3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14
            },
            {2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9,
                    14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6,
                    4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14,
                    11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3
            },
            {12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11,
                    10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8,
                    9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6,
                    4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13
            },
            {4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1,
                    13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6,
                    1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2,
                    6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12
            },
            {13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7,
                    1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2,
                    7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8,
                    2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11
            }
    };
}