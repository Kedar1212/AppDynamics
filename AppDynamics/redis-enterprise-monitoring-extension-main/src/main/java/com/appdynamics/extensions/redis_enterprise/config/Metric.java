package com.appdynamics.extensions.redis_enterprise.config;
import com.google.common.collect.Maps;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.math.BigDecimal;
import java.util.Map;

/**
 * @author: Kedar Kanase on 2/18/24
 */

 

@XmlAccessorType(XmlAccessType.FIELD)
public class Metric {

    @XmlAttribute
    private String attr;

    @XmlAttribute
    private String alias;

    @XmlAttribute
    private String delta;

    @XmlAttribute
    private String aggregationType;

    @XmlAttribute
    private String timeRollUpType;

    @XmlAttribute
    private String clusterRollUpType;

    @XmlAttribute
    private BigDecimal multiplier;

    @XmlElement(name = "isBoolean")
    private String isBoolean = "false";

    @XmlElement(name = "convert")
    private MetricConverter[] convert;

    public String getAttr () {
        return attr;
    }

    public void setAttr (String attr) {
        this.attr = attr;
    }

    public String getAlias () {
        return alias;
    }

    public void setAlias (String alias) {
        this.alias = alias;
    }

    public BigDecimal getMultiplier () {
        return multiplier;
    }

    public void setMultiplier (BigDecimal multiplier) {
        this.multiplier = multiplier;
    }

    public String isBoolean () {
        return isBoolean;
    }

    public void setBoolean (String isBoolean) {
        this.isBoolean = isBoolean;
    }

    public String getDelta () {
        return delta;
    }

    public void setDelta (String delta) {
        this.delta = delta;
    }

    public String getAggregationType () {
        return aggregationType;
    }

    public void setAggregationType (String aggregationType) {
        this.aggregationType = aggregationType;
    }

    public String getTimeRollUpType () {
        return timeRollUpType;
    }

    public void setTimeRollUpType (String timeRollUpType) {
        this.timeRollUpType = timeRollUpType;
    }

    public String getClusterRollUpType () {
        return clusterRollUpType;
    }

    public void setClusterRollUpType (String clusterRollUpType) {
        this.clusterRollUpType = clusterRollUpType;
    }

    public Map<String, String> getConvert () {
        Map<String, String> converterMap = Maps.newHashMap();
        if (convert != null && convert.length > 0) {
            return generateConverterMap(converterMap);
        }
        return converterMap;
    }

    private Map<String, String> generateConverterMap (Map<String, String> converterMap) {
        for (MetricConverter converter : convert) {
            converterMap.put(converter.getLabel(), converter.getValue());
        }
        return converterMap;
    }

    public void setConvert (MetricConverter[] convert) {
        this.convert = convert;
    }
}