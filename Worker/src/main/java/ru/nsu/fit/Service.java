package ru.nsu.fit;

import org.paukov.combinatorics3.Generator;
import org.springframework.web.reactive.function.client.WebClient;
import ru.nsu.fit.dto.WorkerResponse;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@org.springframework.stereotype.Service
public class Service {

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

    public Service() {
        this.symbols = new Character[26 + 10];
        for (int i = 0; i < 36; i++) {
            symbols[i] = i < 26 ? (char) ('a' + i) : (char) ('0' + i - 26);
        }

        this.webClient = WebClient.builder()
                .baseUrl("http://manager:8080/internal/api/manager/hash/crack/request")
                .build();
    }

    public void processManagerTask(UUID requestId, String hash, int maxLength, int partNumber, int partCount) {
        int arraySize = 36 / partCount;
        Character[] slicedArray = Arrays.copyOfRange(
                symbols,
                (partNumber - 1) * arraySize,
                partNumber * arraySize
        );
        Set<String> wordsSet = new HashSet<>();

        for (int length = 1; length <= maxLength; length++) {
            Iterator<List<Character>> iterator = Generator.permutation(slicedArray)
                    .withRepetitions(length)
                    .iterator();

            iterator.forEachRemaining(list -> {
                StringBuilder sb = new StringBuilder();
                list.forEach(sb::append);

                byte[] plainWordBytes = sb.toString().getBytes();
                BigInteger bi = new BigInteger(1, md.digest(plainWordBytes));
                String wordHash = String.format("%0" + (plainWordBytes.length << 1) + "x", bi);

                if (wordHash.equals(hash)) {
                    wordsSet.add(sb.toString());
                }
            });
        }

        webClient.patch()
                .bodyValue(new WorkerResponse(requestId, wordsSet))
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}