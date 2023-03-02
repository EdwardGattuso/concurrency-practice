package org.example.chapter_01;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

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

    private static final int THREAD_COUNT = 10;

    public static void main(String[] args) throws Exception {
        System.out.println("Test UnsafeSequence - Thread Safe: " + isSafeSequence(new UnsafeSequence()));
        System.out.println("Test SafeSequence - Thread Safe: " + isSafeSequence(new SafeSequence()));
    }

    public static boolean isSafeSequence(Sequence sequence) throws InterruptedException {
        List<Integer> seqList = Collections.synchronizedList(new ArrayList<>());
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch countDownLatch = new CountDownLatch(THREAD_COUNT);
        IntStream.range(0, THREAD_COUNT).forEach(t -> executorService.submit(() -> {
            IntStream.range(0, 10000).forEach(i -> seqList.add(sequence.getNext()));
            countDownLatch.countDown();
        }));
        countDownLatch.await();
        executorService.shutdown();

        return new HashSet<>(seqList).size() == 10000 * THREAD_COUNT;
    }
}
