package com.example.yoctotrader.feed;

import java.util.concurrent.ThreadLocalRandom;

public class RandomWalkFeed {
    private double px = 100.00;
    public double nextPrice() {
        double step = ThreadLocalRandom.current().nextGaussian() * 0.01;
        px = Math.max(0.01, px + step);
        return px;
    }
}
