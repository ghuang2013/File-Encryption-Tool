package math;

import java.math.BigInteger;

/**
 * Created by Guan Huang on 10/10/2015.
 */
public class GCD {
    public static boolean hasMultiplicativeInverse(BigInteger num1, BigInteger num2) {
        BigInteger remainder = Digit.ZERO;
        do {
            remainder = num1.mod(num2);
            num1 = num2;
            num2 = remainder;
        } while (remainder.compareTo(Digit.ZERO) == 1);
        return num1.equals(Digit.ONE);
    }
}
