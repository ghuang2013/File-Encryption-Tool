package math;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by Guan Huang on 10/10/2015.
 */
public class CryptoSecureRandom {
    private SecureRandom randomEngine;

    public CryptoSecureRandom() {
        randomEngine = new SecureRandom();
    }

    /**
     * @return a big integer within the specified range (inclusive)
     */
    public BigInteger nextBigInteger(BigInteger start, BigInteger stop) {
        BigInteger randomNum;
        do {
            randomNum = new BigInteger(stop.bitLength(), randomEngine);
        } while (randomNum.compareTo(start) == -1 || randomNum.compareTo(stop) == 1);
        return randomNum;
    }

    public BigInteger nextPrimeCandidate(int bitLength) {
        BigInteger p = new BigInteger(bitLength, randomEngine);
        if (p.and(Digit.ONE).equals(Digit.ZERO)) {
            p = p.add(Digit.ONE);
        }
        return p;
    }
}
