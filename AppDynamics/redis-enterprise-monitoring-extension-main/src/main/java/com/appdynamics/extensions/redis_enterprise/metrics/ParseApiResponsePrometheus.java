package com.appdynamics.extensions.redis_enterprise.metrics;

import com.appdynamics.extensions.metrics.Metric;
import com.appdynamics.extensions.redis_enterprise.config.Stat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.appdynamics.extensions.logging.ExtensionsLoggerFactory;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParseApiResponsePrometheus {

    private static final Logger LOGGER = ExtensionsLoggerFactory.getLogger(ParseApiResponsePrometheus.class);
    private final Map<String, List<Double>> metricsMap;
    private final String metricPrefix;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public ParseApiResponsePrometheus(String responseText, String metricPrefix) {
        this.metricsMap = PrometheusTextParser.parse(responseText);
        this.metricPrefix = metricPrefix;
    }

    /**
     * Extracts metrics based on the configuration defined in the given Stat object.
     * For each metric defined in metrics.xml, it looks up the attribute (attr) in the parsed Prometheus data.
     */
    public List<Metric> extractMetrics(Stat stat) {
        List<Metric> metricList = new ArrayList<>();
        // Process nested stats recursively if present
        if (stat.getStats() != null) {
            for (Stat childStat : stat.getStats()) {
                metricList.addAll(extractMetrics(childStat));
            }
        }
        for (com.appdynamics.extensions.redis_enterprise.config.Metric metricConfig : stat.getMetric()) {
            String attr = metricConfig.getAttr();
            List<Double> values = metricsMap.get(attr);
            if (values != null && !values.isEmpty()) {
                // Example aggregation: sum all values. You may choose average, max, etc.
                double aggregatedValue = values.stream().mapToDouble(Double::doubleValue).sum();
                String aggregatedValueStr = Double.toString(aggregatedValue);
                String[] metricPathTokens = attr.split("\\|");
                Map<String, String> propertiesMap = objectMapper.convertValue(metricConfig, Map.class);
                Metric metric = new Metric(attr, aggregatedValueStr, propertiesMap, metricPrefix, metricPathTokens);
                metricList.add(metric);
                LOGGER.debug("Processed metric [{}] with aggregated value: {}", attr, aggregatedValueStr);
            } else {
                LOGGER.debug("Metric [{}] not found in Prometheus response", attr);
            }
        }
        return metricList;
    }
}
