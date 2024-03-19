package ru.nsu.fit.core.impl.domain;

import org.paukov.combinatorics3.Generator;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Component
public class HashCracker {

    private final Character[] symbols;
    private final MessageDigest md;

    {
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public HashCracker() {
        this.symbols = new Character[26 + 10];
        for (int i = 0; i < 36; i++) {
            symbols[i] = i < 26 ? (char) ('a' + i) : (char) ('0' + i - 26);
        }
    }

    private record IndexRange(long from, long to) {
    }

    public Set<String> findHashEqualWords(String desiredHash, int maxLength, int partNumber, int partCount) {
        Set<String> hashEqualWords = new HashSet<>();

        for (int length = 1; length <= maxLength; length++) {
            IndexRange bounds = calculatePermutationBounds(partNumber, partCount, length);
            Iterator<List<Character>> iterator = constructPermutationsIterator(length, bounds);
            iterator.forEachRemaining(list -> {
                StringBuilder sb = new StringBuilder();
                list.forEach(sb::append);

                String hash = getMD5Hash(sb.toString());
                if (hash.equals(desiredHash)) {
                    hashEqualWords.add(sb.toString());
                }
            });
        }

        return hashEqualWords;
    }

    private Iterator<List<Character>> constructPermutationsIterator(int length, IndexRange bounds) {
        return Generator.permutation(symbols)
                .withRepetitions(length)
                .stream()
                .skip(bounds.from())
                .limit(bounds.to())
                .iterator();
    }

    private IndexRange calculatePermutationBounds(int partNumber, double partCount, int permutationLength) {
        long partitionSize = (long) Math.max(0, Math.pow(symbols.length, permutationLength) / partCount);
        long from = (partNumber - 1) * partitionSize;
        long to = from + partitionSize;
        return new IndexRange(from, to);
    }

    private String getMD5Hash(String source) {
        byte[] plainWordBytes = source.getBytes();
        BigInteger bi = new BigInteger(1, md.digest(plainWordBytes));
        return String.format("%032x", bi);
    }
}
