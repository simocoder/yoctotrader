package com.example.yoctotrader.engine;

import org.HdrHistogram.Histogram;

public class LatencyRecorder implements AutoCloseable {
    private final Histogram hist = new Histogram(1, 100_000_000L, 3); // 1ns..100ms

    public void record(long startNs, long endNs) {
        long d = endNs - startNs;
        if (d > 0) hist.recordValue(d);
    }

    public void printAndReset(String label) {
        System.out.printf(
            "%s latency ns: p50=%d p90=%d p99=%d max=%d (count=%d)%n",
            label,
            hist.getValueAtPercentile(50),
            hist.getValueAtPercentile(90),
            hist.getValueAtPercentile(99),
            hist.getMaxValue(),
            hist.getTotalCount()
        );
        hist.reset();
    }

    @Override public void close() { /* nothing */ }
}
