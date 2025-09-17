# YoctoTrader

*A tiny, educational trading engine.* It simulates a market data feed, pushes ticks through a **LMAX Disruptor** ring buffer, runs a toy **moving-average strategy**, passes a simple **risk gate**, and logs orders. It also records **end-to-end latency** percentiles using **HdrHistogram**.

## Quick start (Build and run)
```bash
mvn -q package
java -XX:+AlwaysActAsServerClassMachine      -XX:+UseNUMA -XX:+UseStringDeduplication      -jar target/yoctotrader-1.0.0.jar
```
You’ll see order logs and periodic latency stats (in nanoseconds).

### Docker Hub 
docker run --rm -it simocoder/yoctotrader:latest

### Or GHCR 
docker run --rm -it ghcr.io/simocoder/yoctotrader:latest


## What to learn here
- Lock-free handoff via **Disruptor**
- Allocation-free hot path (event reuse)
- Strategy → Risk → OMS separation
- Latency measurement and back-pressure experiments

## Next steps
- Replace the random-walk feed with real UDP/Aeron
- Encode with SBE/Chronicle instead of POJOs
- Pin threads to CPUs, profile with JFR/async-profiler
