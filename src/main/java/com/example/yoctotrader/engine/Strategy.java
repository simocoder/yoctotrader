package com.example.yoctotrader.engine;

public class Strategy {
    private final int fastN;
    private final int slowN;
    private double fastMA = 0, slowMA = 0;
    private int fastCount = 0, slowCount = 0;

    public Strategy(int fastN, int slowN) {
        if (fastN >= slowN) throw new IllegalArgumentException("fast < slow");
        this.fastN = fastN; this.slowN = slowN;
    }

    public enum Signal { BUY, SELL, HOLD }

    public Signal onPrice(double px) {
        // simple incremental average (bounded by window lengths)
        fastCount++; slowCount++;
        fastMA += (px - fastMA) / Math.min(fastCount, fastN);
        slowMA += (px - slowMA) / Math.min(slowCount, slowN);

        if (fastCount < fastN || slowCount < slowN) return Signal.HOLD;
        if (fastMA > slowMA) return Signal.BUY;
        if (fastMA < slowMA) return Signal.SELL;
        return Signal.HOLD;
    }
}
