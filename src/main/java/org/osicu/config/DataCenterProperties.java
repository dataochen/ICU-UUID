package org.osicu.config;

import javax.validation.constraints.Max;

/**
 * @author osicu
 * @Description
 * @date: 2020/12/1 17:41
 */
public class DataCenterProperties {
    /**
     * 127.0.0.2,127.0.0.3,127.0.0.1,10.13.145.149 逗号分隔
     * 最大256台ip
     */
    private String ips;
    /**
     * 如果有此配置 则使用 MultiDataCenterSnowFlake 生产id ;dataCenterIndex从0开始
     * 最大4个数据中心
     */
    @Max(3)
    private int dataCenterIndex;

    public String getIps() {
        return ips;
    }

    public void setIps(String ips) {
        this.ips = ips;
    }

    public int getDataCenterIndex() {
        return dataCenterIndex;
    }

    public void setDataCenterIndex(int dataCenterIndex) {
        this.dataCenterIndex = dataCenterIndex;
    }
}
