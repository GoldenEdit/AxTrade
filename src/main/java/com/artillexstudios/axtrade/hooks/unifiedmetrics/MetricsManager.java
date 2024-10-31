package com.artillexstudios.axtrade.hooks.unifiedmetrics;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;

public class MetricsManager {
    private static final MetricsManager instance = new MetricsManager();

    private final Map<String, AtomicInteger> metrics = new ConcurrentHashMap<>();

    private MetricsManager() {}

    public static MetricsManager getInstance() {
        return instance;
    }

    public void incrementMetric(MetricType metric) {
        metrics.computeIfAbsent(metric.getMetricName(), k -> new AtomicInteger(0)).incrementAndGet();
    }

    // New method to increment a metric by a specific amount
    public void incrementMetricBy(MetricType metric, int amount) {
        metrics.computeIfAbsent(metric.getMetricName(), k -> new AtomicInteger(0)).addAndGet(amount);
    }

    public int getAndResetMetric(MetricType metric) {
        return metrics.computeIfAbsent(metric.getMetricName(), k -> new AtomicInteger(0)).getAndSet(0);
    }
}




