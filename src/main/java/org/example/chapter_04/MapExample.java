package org.example.chapter_04;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class MapExample {

    public static void main(String[] args) throws Exception {
        ImprovedMap<Integer, Integer> improvedMap = new ImprovedMap<>();
        Map<Integer, Integer> hashMap = new HashMap<>();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(10000);
        IntStream.range(0, 10000).forEach(i -> executorService.submit(() -> {
            improvedMap.put(i, i);
            hashMap.put(i, i);
            countDownLatch.countDown();
        }));
        countDownLatch.await();
        executorService.shutdown();
        System.out.println(improvedMap.size());
        System.out.println(hashMap.size());
        // results output:
        // 10000
        // 9980
    }
}
