package com.example.yoctotrader.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderPublisher {
    private static final Logger log = LoggerFactory.getLogger(OrderPublisher.class);

    public void publish(Order o) {
        // real world: binary FIX/SBE over kernel-bypass NIC, ack handling, etc.
        log.info("ORDER {} qty={} px={} ts={}", o.side(), o.qty(), o.price(), o.tsNanos());
    }
}
