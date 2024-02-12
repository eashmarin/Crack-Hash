package ru.nsu.fit.domain;

import org.paukov.combinatorics3.Generator;
import org.springframework.web.reactive.function.client.WebClient;
import ru.nsu.fit.dto.WorkerResponse;
import ru.nsu.fit.port.WorkerUrl;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@org.springframework.stereotype.Service
public class Worker {

    private final Character[] symbols;
    private final MessageDigest md;
    private final WebClient webClient;

    {
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public Worker() {
        this.symbols = new Character[26 + 10];
        for (int i = 0; i < 36; i++) {
            symbols[i] = i < 26 ? (char) ('a' + i) : (char) ('0' + i - 26);
        }

        this.webClient = WebClient.builder()
                .baseUrl(WorkerUrl.WORKER_RESPONSE)
                .build();
    }

    public void processManagerTask(UUID requestId, String desiredHash, int maxLength, int partNumber, int partCount) {
        Character[] alphabet = sliceAlphabetArray(partNumber, partCount);
        Set<String> hashEqualWords = findHashEqualWords(desiredHash, maxLength, alphabet);
        sendWordsToManager(requestId, hashEqualWords);
    }

    private Set<String> findHashEqualWords(String desiredHash, int maxLength, Character[] alphabet) {
        Set<String> hashEqualWords = new HashSet<>();

        for (int length = 1; length <= maxLength; length++) {
            Iterator<List<Character>> iterator = Generator.permutation(alphabet)
                    .withRepetitions(length)
                    .iterator();

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

    private Character[] sliceAlphabetArray(int partNumber, int partCount) {
        int arraySize = symbols.length / partCount;
        return Arrays.copyOfRange(
                symbols,
                (partNumber - 1) * arraySize,
                partNumber * arraySize
        );
    }

    private String getMD5Hash(String source) {
        byte[] plainWordBytes = source.getBytes();
        BigInteger bi = new BigInteger(1, md.digest(plainWordBytes));
        return String.format("%0" + (plainWordBytes.length << 1) + "x", bi);
    }

    private void sendWordsToManager(UUID requestId, Set<String> words) {
        webClient.patch()
                .bodyValue(new WorkerResponse(requestId, words))
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}