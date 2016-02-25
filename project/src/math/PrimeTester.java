package math;

import utility.Pair;

import java.math.BigInteger;


/**
 * Created by Guan Huang on 10/9/2015.
 */
public class PrimeTester {
    private static Pair factor(BigInteger dividend, BigInteger divisor) {
        int s = 0;
        while (dividend.mod(divisor).equals(Digit.ZERO)) {
            dividend = dividend.divide(divisor);
            s++;
        }
        return new Pair<>(dividend, s);
    }

    public static Boolean isPrime(BigInteger number, int securityParam) {
        if (number.equals(Digit.TWO) || number.equals(Digit.THREE)) {
            return true;
        }
        BigInteger r = number.subtract(Digit.ONE);

        Pair resultPair = factor(r, Digit.TWO);
        r = (BigInteger) resultPair.first;
        int s = (Integer) resultPair.second;

        if (s == 0) {
            return false;
        }//even number

        for (int i = 0; i < securityParam; ++i) {
            if (!runMillerRabin(number, r, s)) {
                return false;
            }
        }
        return true;
    }

    private static boolean runMillerRabin(BigInteger number, BigInteger r, int s) {
        CryptoSecureRandom cryptoSecureRandom = new CryptoSecureRandom();
        BigInteger a = cryptoSecureRandom.nextBigInteger(Digit.TWO, number.subtract(Digit.TWO));

        BigInteger y = SquareAndMultiply.getResult(a, r, number);
        if (y.equals(Digit.ONE) || y.equals(number.subtract(Digit.ONE))) {
            return true;
        } else {
            for (int j = 1; j <= s - 1; ++j) {
                y = SquareAndMultiply.getResult(y, Digit.TWO, number);
                if (y.equals(Digit.ONE)) return false;
                if (y.equals(number.subtract(Digit.ONE))) return true;
            }
        }
        return false;
    }
}

