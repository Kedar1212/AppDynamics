package com.appdynamics.extensions.redis_enterprise.metrics;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrometheusTextParser {

    /**
     * Parses a Prometheus text exposition format response.
     * Returns a Map where the key is the metric name and the value is a list of metric samples.
     */
    public static Map<String, List<Double>> parse(String response) {
        Map<String, List<Double>> metricMap = new HashMap<>();
        String[] lines = response.split("\\r?\\n");
        // Regex captures: metricName{optional_labels} value OR metricName value
        Pattern pattern = Pattern.compile("^(\\w+)(\\{[^}]+\\})?\\s+([\\d\\.eE+-]+)$");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                String metricName = matcher.group(1);
                String valueStr = matcher.group(3);
                try {
                    double value = Double.parseDouble(valueStr);
                    metricMap.computeIfAbsent(metricName, k -> new ArrayList<>()).add(value);
                } catch (NumberFormatException e) {
                    // Log or ignore invalid numbers
                }
            }
        }
        return metricMap;
    }
}
