package math;

import java.math.BigInteger;

/**
 * Created by Guan Huang on 10/9/2015.
 */
public class SquareAndMultiply {

    public static BigInteger getResult(BigInteger base, BigInteger exponent, BigInteger mod) {
        BigInteger result = Digit.ONE;
        if (exponent.equals(Digit.ZERO)) return result;
        //write exponent in terms of binary
        String expBinary = exponent.toString(2);

        BigInteger basePowered = base;
        for (int i = expBinary.length() - 1; i >= 0; --i) {
            if (expBinary.charAt(i) == '1') {
                result = result.multiply(basePowered).mod(mod);
            }
            basePowered = basePowered.pow(2).mod(mod);
        }
        return result;
    }

    private static BigInteger getResultRecur(int i, BigInteger basePowered,
                                             String expBinary, BigInteger mod, BigInteger result) {
        if (i < 0) return result;
        if (expBinary.charAt(i) == '1') {
            result = result.multiply(basePowered).mod(mod);
        }
        basePowered = basePowered.pow(2).mod(mod);
        return getResultRecur(--i, basePowered, expBinary, mod, result);
    }

    public static BigInteger getResultRecur(BigInteger base, BigInteger exp, BigInteger mod) {
        if (exp.equals(Digit.ZERO)) return Digit.ONE;
        String expBinary = exp.toString(2);
        BigInteger result = Digit.ONE;
        return getResultRecur(expBinary.length() - 1, base, expBinary, mod, result);
    }
}
