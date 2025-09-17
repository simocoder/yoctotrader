package com.example.yoctotrader.engine;

public record Order(long tsNanos, Strategy.Signal side, double price, int qty) {}
