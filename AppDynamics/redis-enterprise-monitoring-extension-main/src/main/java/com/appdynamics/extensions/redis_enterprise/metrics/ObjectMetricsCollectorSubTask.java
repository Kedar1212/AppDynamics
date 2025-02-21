package com.appdynamics.extensions.redis_enterprise.metrics;

import com.appdynamics.extensions.MetricWriteHelper;
import com.appdynamics.extensions.conf.MonitorContextConfiguration;
import com.appdynamics.extensions.logging.ExtensionsLoggerFactory;
import com.appdynamics.extensions.metrics.Metric;
import com.appdynamics.extensions.redis_enterprise.config.Stat;
import org.slf4j.Logger;
import java.util.List;
import java.util.concurrent.Phaser;

public class ObjectMetricsCollectorSubTask implements Runnable {

    private static final Logger LOGGER = ExtensionsLoggerFactory.getLogger(ObjectMetricsCollectorSubTask.class);
    private final MonitorContextConfiguration monitorContextConfiguration;
    private final String uid;
    private final String objectName;
    private final String statsEndpointUrl;
    private final MetricWriteHelper metricWriteHelper;
    private final String serverName;
    private Stat parentStat;
    private Phaser phaser;

    public ObjectMetricsCollectorSubTask(String displayName,
                                           String statsEndpointUrl,
                                           String uid,
                                           String objectName,
                                           MonitorContextConfiguration monitorContextConfiguration,
                                           MetricWriteHelper metricWriteHelper,
                                           Stat parentStat,
                                           Phaser phaser) {
        this.uid = uid;
        this.objectName = objectName;
        this.statsEndpointUrl = statsEndpointUrl;
        this.monitorContextConfiguration = monitorContextConfiguration;
        this.metricWriteHelper = metricWriteHelper;
        this.serverName = displayName;
        this.parentStat = parentStat;
        this.phaser = phaser;
        this.phaser.register();
    }

    @Override
    public void run() {
        try {
            collectMetrics(parentStat);
        } catch (Exception e) {
            LOGGER.info("Exception while collecting object metrics {}", objectName, e);
        } finally {
            phaser.arriveAndDeregister();
        }
    }

    private void collectMetrics(Stat stat) {
        LOGGER.debug("Extracting metrics for [{}] objectName [{}] from endpoint with uid [{}]", statsEndpointUrl, objectName, uid);
        // Retrieve raw Prometheus text for this object's metrics.
        // Adjust the URL formation as needed (this example appends "/" + uid).
        String responseText = com.appdynamics.extensions.http.HttpClientUtils.getResponseAsStr(
                monitorContextConfiguration.getContext().getHttpClient(),
                statsEndpointUrl + "/" + uid);
        // Use the Prometheus parser
        ParseApiResponsePrometheus parser = new ParseApiResponsePrometheus(
                responseText,
                monitorContextConfiguration.getMetricPrefix() + "|" + serverName + "|" + stat.getType() + "|" + objectName);
        List<Metric> metricsList = parser.extractMetrics(stat);
        metricWriteHelper.transformAndPrintMetrics(metricsList);
    }
}
