package com.artillexstudios.axtrade.hooks.unifiedmetrics;

public enum MetricType {
    INITIALIZED_TRADES("trade_initialized_trades"),
    COMPLETED_TRADES("trade_completed_trades"),
    FAILED_TRADES("trade_failed_trades"),
    TOKENS_TRADED("trade_legend_tokens_traded"),
    TOTAL_ITEMS_TRADED("trade_total_items_traded"),
    INVENTORY_INTERACTIONS("trade_inventory_interaction");


    private final String metricName;

    MetricType(String metricName) {
        this.metricName = metricName;
    }

    public String getMetricName() {
        return metricName;
    }
}
