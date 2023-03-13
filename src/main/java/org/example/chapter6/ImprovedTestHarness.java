package org.example.chapter6;

import java.util.concurrent.*;
import java.util.stream.IntStream;

public class ImprovedTestHarness {

    public static long timeTasks(int nThreads, final Runnable task) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        FutureTask<Long> startFutureTask = createFutureTaskWithCompletedTime(nThreads, "reached");
        FutureTask<Long> endFutureTask = createFutureTaskWithCompletedTime(nThreads, "exit");
        final CyclicBarrier startGate = new CyclicBarrier(nThreads, startFutureTask);
        final CyclicBarrier endGate = new CyclicBarrier(nThreads, endFutureTask);
        IntStream.range(0, nThreads).forEach(i -> executorService.submit(() -> {
            try {
                System.out.println(String.format("%s reached. [%s/%s] (Number of waiting / Parties)",
                        Thread.currentThread().getName(), startGate.getNumberWaiting(), startGate.getParties()));
                startGate.await();
                try {
                    task.run();
                } finally {
                    System.out.println(String.format("%s exit. [%s/%s] (Number of waiting / Parties)",
                            Thread.currentThread().getName(), endGate.getNumberWaiting(), endGate.getParties()));
                    endGate.await();
                }
            } catch (InterruptedException | BrokenBarrierException ignored) {
                // Ignored
            }
        }));
        executorService.shutdown();
        return endFutureTask.get() - startFutureTask.get();
    }

    private static FutureTask<Long> createFutureTaskWithCompletedTime(int nThreads, String name) {
        return new FutureTask<>(() -> {
            long nanoTime = System.nanoTime();
            System.out.println(String.format("All of test thread [%s] %s, and nanoTime: [%s]", nThreads, name, nanoTime));
            return nanoTime;
        });
    }
}
