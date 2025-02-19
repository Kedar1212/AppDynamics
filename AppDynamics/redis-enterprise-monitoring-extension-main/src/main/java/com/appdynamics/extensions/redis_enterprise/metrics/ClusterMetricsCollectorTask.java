package com.appdynamics.extensions.redis_enterprise.metrics;

import com.appdynamics.extensions.MetricWriteHelper;
import com.appdynamics.extensions.conf.MonitorContextConfiguration;
import com.appdynamics.extensions.http.HttpClientUtils;
import com.appdynamics.extensions.logging.ExtensionsLoggerFactory;
import com.appdynamics.extensions.metrics.Metric;
import com.appdynamics.extensions.redis_enterprise.config.Stat;
import com.appdynamics.extensions.redis_enterprise.config.Stats;
import org.slf4j.Logger;
import java.util.List;
import java.util.concurrent.Phaser;

public class ClusterMetricsCollectorTask implements Runnable {
    private static final Logger LOGGER = ExtensionsLoggerFactory.getLogger(ClusterMetricsCollectorTask.class);
    private String displayName;
    private String uri;
    private Phaser phaser;
    private MonitorContextConfiguration configuration;
    private MetricWriteHelper metricWriteHelper;

    public ClusterMetricsCollectorTask(String displayName, String uri, MonitorContextConfiguration configuration, MetricWriteHelper metricWriteHelper, Phaser phaser) {
        this.displayName = displayName;
        this.uri = uri;
        this.phaser = phaser;
        this.configuration = configuration;
        this.metricWriteHelper = metricWriteHelper;
        phaser.register();
    }

    @Override
    public void run() {
        try {
            Stats stats = (Stats) configuration.getMetricsXml();
            Stat[] statArray = stats.getStat();
            collectMetrics(statArray);
        } catch (Exception e) {
            LOGGER.info("Exception in collecting cluster level stats", e);
        } finally {
            phaser.arriveAndDeregister();
        }
        LOGGER.info("Completed cluster metric collection for - {}", displayName);
    }

    private void collectMetrics(Stat[] stats) {
        for (Stat parentStat : stats) {
            if (parentStat.getType().equals("cluster")) {
                // Get raw Prometheus text response instead of JSON
                String responseText = HttpClientUtils.getResponseAsStr(configuration.getContext().getHttpClient(),
                        uri + parentStat.getStatsUrl());
                // Use the new Prometheus parser
                ParseApiResponsePrometheus parser = new ParseApiResponsePrometheus(responseText,
                        configuration.getMetricPrefix() + "|" + displayName);
                List<Metric> metrics = parser.extractMetrics(parentStat);
                metricWriteHelper.transformAndPrintMetrics(metrics);
            }
        }
    }
}
