package crypto;

import math.*;
import utility.Pair;

import java.math.BigInteger;

import static java.lang.String.format;

/**
 * Created by Guan Huang on 10/9/2015.
 */
abstract class AsymmetricKey<T, R> {
    protected CryptoSecureRandom randomEngine;

    protected int numOfBits = 128;

    public abstract R encryption(T plainText);

    public abstract R decryption(T cipherText);

    public void setNumOfBits(int numOfBits) {
        this.numOfBits = numOfBits;
    }
}

public class RSA extends AsymmetricKey<BigInteger, BigInteger> {
    private Pair<BigInteger, BigInteger> publicKey = null;
    private BigInteger privateKey = null;
    final private static BigInteger standardE = BigInteger.valueOf(65537);
    final private static int securityIndex = 100;

    private BigInteger p = null, q = null;

    public RSA() {
        randomEngine = new CryptoSecureRandom();
    }

    private void keyGeneration() {
        do {
            p = randomEngine.nextPrimeCandidate(numOfBits / 2);
        } while (!PrimeTester.isPrime(p, securityIndex));

        do {
            q = randomEngine.nextPrimeCandidate(numOfBits / 2);
        } while (!PrimeTester.isPrime(q, securityIndex));

        BigInteger n = p.multiply(q);

        BigInteger e = standardE;
        BigInteger toitient = p.subtract(Digit.ONE).multiply(q.subtract(Digit.ONE));
        //toitient = (p-1)(q-1)
        while (!GCD.hasMultiplicativeInverse(e, toitient)) {
            e = e.add(Digit.TWO);
        }
        this.privateKey = MultInverse.getResult(e, toitient);
        this.publicKey = new Pair<>(e, n); //return public key
    }

    public BigInteger encryption(BigInteger plainText) throws IllegalArgumentException {
        int messageSize = plainText.bitLength();
        if (messageSize >= (numOfBits - 8)) {
            throw new IllegalArgumentException(format("Maximal message size is %d", numOfBits - 8));
        }
        keyGeneration();
        return SquareAndMultiply.getResult(plainText, publicKey.first, publicKey.second);
    }

    public BigInteger decryption(BigInteger cipherText) {
        return SquareAndMultiply.getResult(cipherText, privateKey, publicKey.second);
    }

    public String getD() {
        return String.valueOf(privateKey);
    }

    public String getP() {
        return String.valueOf(p);
    }

    public String getQ() {
        return String.valueOf(q);
    }
}
