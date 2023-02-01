package org.example.chapter_01;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

class SafeSequence {
    private int value;

    public synchronized int getNext() {
        return value++;
    }
}

class SafeSequenceTest {

    public static void main(String[] args) throws Exception {
        SafeSequence safeSequence = new SafeSequence();
        List<Integer> seqList1 = Collections.synchronizedList(new ArrayList<>());
        List<Integer> seqList2 = Collections.synchronizedList(new ArrayList<>());
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch countDownLatch = new CountDownLatch(2);
        executorService.submit(() -> {
            for (int i = 0; i < 100000; i++) {
                seqList1.add(safeSequence.getNext());
            }
            countDownLatch.countDown();
        });
        executorService.submit(() -> {
            for (int i = 0; i < 100000; i++) {
                seqList2.add(safeSequence.getNext());
            }
            countDownLatch.countDown();
        });
        countDownLatch.await();
        executorService.shutdown();
        List<Integer> sameSeqList = seqList1.stream().filter(s1 -> seqList2.stream().anyMatch(s1::equals)).collect(Collectors.toList());
        if (sameSeqList.isEmpty()) {
            System.out.println("Not found the same value!");
        }
    }
}
