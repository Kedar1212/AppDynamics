package com.appdynamics.extensions.redis_enterprise.config;
import com.appdynamics.extensions.redis_enterprise.utils.Constants;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author: Kedar Kanase on 2/18/24
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Stats {

    @XmlElement(name = Constants.STAT)
    private Stat[] stat;

    public Stat[] getStat () {
        return stat;
    }

    public void setStat (Stat[] stat) {
        this.stat = stat;
    }
}
