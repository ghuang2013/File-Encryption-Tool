package math;

import utility.Pair;

import java.math.BigInteger;

/**
 * Created by Guan Huang on 10/12/2015.
 */
public class MultInverse {

    public static BigInteger getResult(BigInteger a, BigInteger n) {
        final BigInteger modulus = n;
        Pair<BigInteger, BigInteger> x = new Pair<>(Digit.ZERO, Digit.ONE);
        BigInteger newX = null, newMod = null;

        while (!a.equals(Digit.ZERO)) {
            newX = generateX(x.first, x.second, n.divide(a), modulus);
            x.first = x.second;
            x.second = newX;
            newMod = n.mod(a);
            n = a;
            a = newMod;
        }
        return x.first;
    }

    private static BigInteger generateX(BigInteger x1, BigInteger x2,
                                        BigInteger d, BigInteger modulus) {
        return x1.subtract(x2.multiply(d)).mod(modulus);
    }
}

