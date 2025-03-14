package com.example.just;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

public class CaffeineSyncVsAtomicLongVsLongAdderTest {

    private static final int THREAD_COUNT = 4_000;  // 동시 실행할 스레드 개수
    private static final int ITERATIONS = 1; // 스레드당 실행 횟수 (총 5,000개 실행)
    private Cache<Long, Long> caffeineCache;
    private Cache<Long, AtomicLong> atomicCache;
    private Cache<Long, LongAdder> longAdderCache;

    @BeforeEach
    void setUp() {
        // Caffeine 캐시 초기화 (단순 캐싱 목적)
        caffeineCache = Caffeine.newBuilder()
                .maximumSize(10_000)  // 최대 10,000개 키 저장
                .build();

        atomicCache = Caffeine.newBuilder()
                .maximumSize(10_000)
                .build();

        longAdderCache = Caffeine.newBuilder()
                .maximumSize(10_000)
                .build();

        // 초기 값 설정 (캐시에서 가져온 후 값 업데이트)
        caffeineCache.put(1L, 1L);
        atomicCache.put(1L, new AtomicLong(1L));
        longAdderCache.put(1L, new LongAdder());
    }

    @Test
    void testCaffeineComputePerformance() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        long startTime = System.nanoTime();

        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.submit(() -> {
                for (int j = 0; j < ITERATIONS; j++) {
                    // compute()를 사용하여 원자적 업데이트 보장
                    caffeineCache.asMap().compute(1L, (key, value) -> (value == null ? 1 : value + 1));
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
        long endTime = System.nanoTime();

        System.out.println("Caffeine + compute() Execution Time: " + (endTime - startTime) / 1_000_000 + " ms");
        System.out.println("Caffeine + compute() Final Value: " + caffeineCache.getIfPresent(1L));
    }


    @Test
    void testAtomicLongPerformance() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        long startTime = System.nanoTime();

        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.submit(() -> {
                for (int j = 0; j < ITERATIONS; j++) {
                    // 캐시에서 AtomicLong을 가져온 후 incrementAndGet() 호출
                    atomicCache.get(1L, k -> new AtomicLong(1L)).incrementAndGet();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
        long endTime = System.nanoTime();

        System.out.println("Caffeine + AtomicLong Execution Time: " + (endTime - startTime) / 1_000_000 + " ms");
        System.out.println("Caffeine + AtomicLong Final Value: " + atomicCache.getIfPresent(1L).get());
    }

    @Test
    void testLongAdderPerformance() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        long startTime = System.nanoTime();

        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.submit(() -> {
                for (int j = 0; j < ITERATIONS; j++) {
                    // 캐시에서 LongAdder를 가져온 후 increment() 호출
                    longAdderCache.get(1L, k -> new LongAdder()).increment();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
        long endTime = System.nanoTime();

        System.out.println("Caffeine + LongAdder Execution Time: " + (endTime - startTime) / 1_000_000 + " ms");
        System.out.println("Caffeine + LongAdder Final Value: " + longAdderCache.getIfPresent(1L).sum());
    }
}
