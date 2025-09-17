package com.example.yoctotrader.engine;

public class RiskGate {
    private final int maxPosition;
    private int position = 0;
    private long lastOrderNs = 0;
    private final long minGapNs;

    public RiskGate(int maxPosition, long minGapNs) {
        this.maxPosition = maxPosition;
        this.minGapNs = minGapNs;
    }

    public boolean allow(Strategy.Signal sig, long nowNs) {
        if (nowNs - lastOrderNs < minGapNs) return false;
        int nextPos = switch (sig) {
            case BUY -> position + 1;
            case SELL -> position - 1;
            default -> position;
        };
        boolean ok = Math.abs(nextPos) <= maxPosition;
        if (ok && (sig == Strategy.Signal.BUY || sig == Strategy.Signal.SELL)) {
            position = nextPos;
            lastOrderNs = nowNs;
        }
        return ok;
    }

    public int position() { return position; }
}
