package org.osicu.config;

import org.osicu.impl.ZkSnowFlake;
import org.osicu.impl.DefaultSnowFlake;
import org.osicu.impl.MultiDataCenterSnowFlake;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @author osicu
 * @Description 雪花算法 配置
 * @date: 2020/12/1 17:25
 */
public class SnowFlakeProperties {
    /**
     * //            默认 2020/11/24 16:26:48
     */
    private long startTime = 1606206408000L;
    /**
     * 如果有此配置 则使用
     *
     * @see DefaultSnowFlake 生产id
     * 例子：127.0.0.2,127.0.0.3,127.0.0.1,10.13.145.149 逗号分隔
     */
    private String ips;
    /**
     * 如果有此配置 则使用
     *
     * @see MultiDataCenterSnowFlake  生产id
     */
    @NestedConfigurationProperty
    private DataCenterProperties dataCenter;
    /**
     * 如果有此配置 则使用
     *
     * @see ZkSnowFlake  生产id;
     */
    @NestedConfigurationProperty
    private ZkProperties zk;
    /**
     * 最大时间回拨时间
     * 如果超过此时间会报异常 否则阻塞
     * 默认2秒
     */
    private long maxRollBackTime=2000L;

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getIps() {
        return ips;
    }

    public void setIps(String ips) {
        this.ips = ips;
    }

    public DataCenterProperties getDataCenter() {
        return dataCenter;
    }

    public void setDataCenter(DataCenterProperties dataCenter) {
        this.dataCenter = dataCenter;
    }

    public ZkProperties getZk() {
        return zk;
    }

    public void setZk(ZkProperties zk) {
        this.zk = zk;
    }

    public long getMaxRollBackTime() {
        return maxRollBackTime;
    }

    public void setMaxRollBackTime(long maxRollBackTime) {
        this.maxRollBackTime = maxRollBackTime;
    }
}
