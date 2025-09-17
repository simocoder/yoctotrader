package com.example.yoctotrader.events;

public class MarketDataEvent {
    public long seq;          // sequence number from feed
    public long tsNanos;      // producer timestamp (nanoTime)
    public double price;      // last trade/quote mid

    public void set(long seq, long tsNanos, double price) {
        this.seq = seq;
        this.tsNanos = tsNanos;
        this.price = price;
    }

    public void clear() {
        seq = 0; tsNanos = 0; price = 0.0;
    }
}
