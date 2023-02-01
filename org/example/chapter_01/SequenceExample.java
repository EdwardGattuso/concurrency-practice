package org.example.chapter_01;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

interface Sequence {
    int getNext();
}

class UnsafeSequence implements Sequence {
    private int value;

    @Override
    public int getNext() {
        return value++;
    }
}

class SafeSequence implements Sequence {
    private int value;

    @Override
    public synchronized int getNext() {
        return value++;
    }
}


class SequenceExample {

    public static void main(String[] args) throws Exception {
        System.out.println("Test UnsafeSequence - Thread Safe: " + isSafeSequence(new UnsafeSequence()));
        System.out.println("Test SafeSequence - Thread Safe: " + isSafeSequence(new SafeSequence()));
    }

    public static boolean isSafeSequence(Sequence sequence) throws Exception {
        List<Integer> seqList1 = Collections.synchronizedList(new ArrayList<>());
        List<Integer> seqList2 = Collections.synchronizedList(new ArrayList<>());
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch countDownLatch = new CountDownLatch(2);
        executorService.submit(() -> {
            for (int i = 0; i < 100000; i++) {
                seqList1.add(sequence.getNext());
            }
            countDownLatch.countDown();
        });
        executorService.submit(() -> {
            for (int i = 0; i < 100000; i++) {
                seqList2.add(sequence.getNext());
            }
            countDownLatch.countDown();
        });
        countDownLatch.await();
        executorService.shutdown();

        return seqList1.stream().noneMatch(s1 -> seqList2.stream().anyMatch(s1::equals));
    }
}
