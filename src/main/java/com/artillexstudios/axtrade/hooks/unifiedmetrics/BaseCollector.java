package com.artillexstudios.axtrade.hooks.unifiedmetrics;

import dev.cubxity.plugins.metrics.api.metric.collector.Collector;
import dev.cubxity.plugins.metrics.api.metric.data.GaugeMetric;
import dev.cubxity.plugins.metrics.api.metric.data.Metric;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseCollector implements Collector {
    private final MetricType metricType;
    private final Map<String, String> EMPTY_LABELS = new HashMap<>();

    public BaseCollector(MetricType metricType) {
        this.metricType = metricType;
    }

    @NotNull
    @Override
    public List<Metric> collect() {
        int metricValue = MetricsManager.getInstance().getAndResetMetric(metricType);
        GaugeMetric metric = new GaugeMetric(metricType.getMetricName(), EMPTY_LABELS, metricValue);
        return Collections.singletonList(metric);
    }
}

