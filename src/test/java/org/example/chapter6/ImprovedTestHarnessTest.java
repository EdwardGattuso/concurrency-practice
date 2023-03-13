package org.example.chapter6;

import org.example.chapter4.ImprovedMap;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class ImprovedTestHarnessTest {

    @ParameterizedTest
    @ValueSource(ints = {10000, 50000, 100000})
    void testMapPerformance(int iterations) {
        Map<String, Long> result = Stream.of(
                new ConcurrentHashMap(),
                new ImprovedMap<>(),
                Collections.synchronizedMap(new HashMap<>())
        ).collect(Collectors.toMap(map -> map.getClass().getSimpleName(),
                map -> testImprovedHarnessWithMultipleThread(10, iterations, map)));

        result.forEach((key, value) -> System.out.println(String.format("%s - %d, Performance: %s", key, iterations, value)));
    }

    private long testImprovedHarnessWithMultipleThread(int nThreads, int iterations, Map<Integer, Integer> map) {
        try {
            return ImprovedTestHarness.timeTasks(nThreads, () -> IntStream.range(0, iterations).forEach(i -> map.put(i, i)));
        } catch (InterruptedException | ExecutionException ignored) {
            throw new RuntimeException(ignored);
        }
    }

    // TestHarness with 10 threads

    // ImprovedMap - 10000, Performance: 19647469
    // SynchronizedMap - 10000, Performance: 12913318
    // ConcurrentHashMap - 10000, Performance: 20203982

    // ImprovedMap - 50000, Performance: 24228148
    // SynchronizedMap - 50000, Performance: 26572347
    // ConcurrentHashMap - 50000, Performance: 51466931

    // ImprovedMap - 100000, Performance: 50162685
    // SynchronizedMap - 100000, Performance: 62445033
    // ConcurrentHashMap - 100000, Performance: 51045225

    // ImprovedTestHarness with 10 threads
    //
    // ImprovedMap - 10000, Performance: 18507595
    // SynchronizedMap - 10000, Performance: 10326267
    // ConcurrentHashMap - 10000, Performance: 20741551
    //
    // ImprovedMap - 50000, Performance: 26384160
    // SynchronizedMap - 50000, Performance: 23469202
    // ConcurrentHashMap - 50000, Performance: 56592096
    //
    // ImprovedMap - 100000, Performance: 65524655
    // SynchronizedMap - 100000, Performance: 69773281
    // ConcurrentHashMap - 100000, Performance: 28951043
}
