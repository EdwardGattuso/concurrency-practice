package org.example.chapter_05;

import org.example.chapter_04.ImprovedMap;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.IntStream;

@State(Scope.Benchmark)
@Measurement(iterations = 10)
public class MapPerformanceTest {

    @Fork(value = 2)
    @Benchmark
    @OutputTimeUnit(TimeUnit.SECONDS)
    @BenchmarkMode(Mode.Throughput)
    public static void testSynchronizedMap() throws InterruptedException {
        writeValuesToMapWithMultipleThread(Collections.synchronizedMap(new HashMap<>()));
    }

    @Fork(value = 2)
    @Benchmark
    @OutputTimeUnit(TimeUnit.SECONDS)
    @BenchmarkMode(Mode.Throughput)
    public static void testConcurrentHashMap() throws InterruptedException {
        writeValuesToMapWithMultipleThread(new ConcurrentHashMap<>());
    }

    @Fork(value = 2)
    @Benchmark
    @OutputTimeUnit(TimeUnit.SECONDS)
    @BenchmarkMode(Mode.Throughput)
    public static void testImprovedMap() throws InterruptedException {
        writeValuesToMapWithMultipleThread(new ImprovedMap<>());
    }

    private static void writeValuesToMapWithMultipleThread(Map<Integer, Integer> map) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(10);
        IntStream.range(0, 10).forEach(i -> executorService.submit(() -> {
            IntStream.range(0, 100000).forEach(v -> map.put(v, v));
            countDownLatch.countDown();
        }));
        countDownLatch.await();
        executorService.shutdown();
    }

    public static void main(String[] args) throws IOException {
        org.openjdk.jmh.Main.main(args);

        // # Fork: 2
        // # Warmup: 5 iterations, 10 s each
        // # Measurement: 10 iterations, 10 s each
        // # 1. Each of the 10 threads inserts 10,000 elements into a map at the same time
        // Benchmark                                  Mode  Cnt    Score    Error  Units
        // MapPerformanceTest.testConcurrentHashMap  thrpt   20  427.674 ± 26.927  ops/s
        // MapPerformanceTest.testImprovedMap        thrpt   20  201.436 ± 12.822  ops/s
        // MapPerformanceTest.testSynchronizedMap    thrpt   20  190.711 ±  6.979  ops/s
        // # 2. Each of the 10 threads inserts 50,000 elements into a map at the same time
        // Benchmark                                  Mode  Cnt    Score   Error  Units
        // MapPerformanceTest.testConcurrentHashMap  thrpt   20  131.385 ± 7.448  ops/s
        // MapPerformanceTest.testImprovedMap        thrpt   20   49.413 ± 0.831  ops/s
        // MapPerformanceTest.testSynchronizedMap    thrpt   20   48.580 ± 1.662  ops/s
        // # 3. Each of the 10 threads inserts 100,000 elements into a map at the same time
        // Benchmark                                  Mode  Cnt   Score   Error  Units
        // MapPerformanceTest.testConcurrentHashMap  thrpt   20  54.768 ± 3.913  ops/s
        // MapPerformanceTest.testImprovedMap        thrpt   20  19.843 ± 2.383  ops/s
        // MapPerformanceTest.testSynchronizedMap    thrpt   20  20.817 ± 1.041  ops/s

        // # Fork 5
        // # Warmup: 5 iterations, 10 s each
        // # Measurement: 10 iterations, 10 s each
        // # 3. Each of the 10 threads inserts 100,000 elements into a map at the same time
        // Benchmark                                  Mode  Cnt   Score   Error  Units
        // MapPerformanceTest.testConcurrentHashMap  thrpt   50  60.069 ± 1.333  ops/s
        // MapPerformanceTest.testImprovedMap        thrpt   50  22.930 ± 0.513  ops/s
        // MapPerformanceTest.testSynchronizedMap    thrpt   50  22.669 ± 0.667  ops/s
    }
}
