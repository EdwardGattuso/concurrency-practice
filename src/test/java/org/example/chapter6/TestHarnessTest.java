package org.example.chapter6;

import org.example.chapter4.ImprovedMap;
import org.example.chapter5.TestHarness;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class TestHarnessTest {

    @ParameterizedTest
    @ValueSource(ints = {10000, 50000, 100000})
    void testMapPerformance(int iterations) {
        Map<String, Long> result = Stream.of(
                new ConcurrentHashMap(),
                new ImprovedMap<>(),
                Collections.synchronizedMap(new HashMap<>())
        ).collect(Collectors.toMap(map -> map.getClass().getSimpleName(),
                map -> testHarnessWithMultipleThread(10, iterations, map)));

        result.forEach((key, value) -> System.out.println(String.format("%s - %d, Performance: %s", key, iterations, value)));
    }

    private long testHarnessWithMultipleThread(int nThreads, int iterations, Map<Integer, Integer> map) {
        try {
            return TestHarness.timeTasks(nThreads, () -> IntStream.range(0, iterations).forEach(i -> map.put(i, i)));
        } catch (InterruptedException ignored) {
            throw new RuntimeException(ignored);
        }
    }
}
