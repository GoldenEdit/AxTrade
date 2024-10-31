package com.artillexstudios.axtrade.hooks.unifiedmetrics;

import dev.cubxity.plugins.metrics.api.metric.collector.Collector;
import dev.cubxity.plugins.metrics.api.metric.collector.CollectorCollection;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TradeCollectorCollection implements CollectorCollection {
    private final List<Collector> collectors;

    public TradeCollectorCollection() {
        collectors = Stream.of(MetricType.values())
                .map(BaseCollector::new)
                .collect(Collectors.toList());
    }

    @NotNull
    @Override
    public List<Collector> getCollectors() {
        return this.collectors;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void initialize() {
        // Do nothing
    }

    @Override
    public void dispose() {
        // Do nothing
    }
}
