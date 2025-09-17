package com.example.yoctotrader;

import com.example.yoctotrader.engine.*;
import com.example.yoctotrader.events.MarketDataEvent;
import com.example.yoctotrader.feed.RandomWalkFeed;
import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;  
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class Main {

    public static void main(String[] args) throws Exception {
        final int ringSize = 1 << 16; // 65,536 entries
        final ThreadFactory tf = r -> {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setPriority(Thread.NORM_PRIORITY + 1);
            t.setName("yoctotrader-" + t.getId());
            return t;
        };

        Disruptor<MarketDataEvent> disruptor = new Disruptor<>(
            MarketDataEvent::new,
            ringSize,
            tf,
            ProducerType.SINGLE,
            new BusySpinWaitStrategy()
        );

        Strategy strat = new Strategy(8, 32);
        RiskGate risk = new RiskGate(5, 1_000_000); // 1 ms minimum gap
        OrderPublisher oms = new OrderPublisher();
        LatencyRecorder rec = new LatencyRecorder();

        // Consumer handler: strategy -> risk -> order
        EventHandler<MarketDataEvent> handler = (evt, seq, endOfBatch) -> {
            long now = System.nanoTime();

            var sig = strat.onPrice(evt.price);
            if (sig != Strategy.Signal.HOLD && risk.allow(sig, now)) {
                Order o = new Order(now, sig, evt.price, 1);
                oms.publish(o);
            }

            rec.record(evt.tsNanos, now);
            evt.clear();

            if (endOfBatch && (seq & 0xFFFF) == 0) { // print occasionally
                rec.printAndReset("e2e");
            }
        };

        disruptor.handleEventsWith(handler);
        RingBuffer<MarketDataEvent> rb = disruptor.start();

        // Producer: simulated market data at target rate
        RandomWalkFeed feed = new RandomWalkFeed();
        long seqNo = 0;
        final long targetRatePerSec = 200_000; // 200k ticks/s (simulate pressure)
        final long intervalNs = 1_000_000_000L / targetRatePerSec;
        long next = System.nanoTime();

        System.out.println("YoctoTrader started: ~" + targetRatePerSec + " ticks/sec");
        while (true) {
            long now = System.nanoTime();
            if (now < next) continue;
            next += intervalNs;

            long s = rb.next();
            try {
                MarketDataEvent e = rb.get(s);
                e.set(++seqNo, now, feed.nextPrice());
            } finally {
                rb.publish(s);
            }
        }
    }
}
