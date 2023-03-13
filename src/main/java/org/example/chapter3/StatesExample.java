package org.example.chapter3;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

interface States {
    String[] getStates();
}

class UnsafeStates implements States {
    private String[] states = new String[]{
            "AK", "AL", "SB", "SC", "SD", "SP", "ST", "OO", "PP", "GG"
    };

    @Override
    public String[] getStates() {
        return this.states;
    }
}

class FinalStates implements States {
    private final String[] states = new String[]{
            "AK", "AL", "SB", "SC", "SD", "SP", "ST", "OO", "PP", "GG"
    };

    @Override
    public String[] getStates() {
        return this.states;
    }
}

class SafeStates implements States {
    private final String[] states = new String[]{
            "AK", "AL", "SB", "SC", "SD", "SP", "ST", "OO", "PP", "GG"
    };


    @Override
    public String[] getStates() {
        return Arrays.copyOf(this.states, this.states.length);
    }
}

public class StatesExample {
    private static final int THREAD_COUNT = 10;

    public static void main(String[] args) throws Exception {
        UnsafeStates unsafeStates = new UnsafeStates();
        FinalStates finalStates = new FinalStates();
        SafeStates safeStates = new SafeStates();
        String[] originalStates = Arrays.copyOf(safeStates.getStates(), safeStates.getStates().length);
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch countDownLatch = new CountDownLatch(THREAD_COUNT);
        IntStream.range(0, THREAD_COUNT).forEach(i -> executorService.submit(() -> {
            String[] uStates = unsafeStates.getStates();
            uStates[i] = "Hacking - " + i;
            String[] fStates = finalStates.getStates();
            fStates[i] = "Hacking - " + i;
            String[] sStates = safeStates.getStates();
            sStates[i] = "Hacking - " + i;
            countDownLatch.countDown();
        }));
        countDownLatch.await();
        executorService.shutdown();
        System.out.println("UnsafeStates instance that modified by Multi-Thread: " + Arrays.toString(unsafeStates.getStates()) + "\nUnsafeStates - is the same as the original states: " + Arrays.toString(originalStates).equals(Arrays.toString(unsafeStates.getStates())));
        System.out.println("FinalStates instance that after modified by Multi-Thread: " + Arrays.toString(finalStates.getStates()) + "\nFinalStates - is the same as the original states: " + Arrays.toString(originalStates).equals(Arrays.toString(finalStates.getStates())));
        System.out.println("SafeStates instance that after modified by Multi-Thread: " + Arrays.toString(safeStates.getStates()) + "\nSafeStates - is the same as the original states: " + Arrays.toString(originalStates).equals(Arrays.toString(safeStates.getStates())));
        System.out.println();
        // results
        //UnsafeStates instance that modified by Multi-Thread: [Hacking - 0, Hacking - 1, Hacking - 2, Hacking - 3, Hacking - 4, Hacking - 5, Hacking - 6, Hacking - 7, Hacking - 8, Hacking - 9]
        //UnsafeStates - is the same as the original states: false
        //FinalStates instance that after modified by Multi-Thread: [Hacking - 0, Hacking - 1, Hacking - 2, Hacking - 3, Hacking - 4, Hacking - 5, Hacking - 6, Hacking - 7, Hacking - 8, Hacking - 9]
        //FinalStates - is the same as the original states: false
        //SafeStates instance that after modified by Multi-Thread: [AK, AL, SB, SC, SD, SP, ST, OO, PP, GG]
        //SafeStates - is the same as the original states: true
    }
}
