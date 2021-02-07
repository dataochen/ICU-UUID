package org.osicu.config;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * @author osicu
 * @Description 配置
 * @date: 2020/12/1 17:25
 */
public class LocalCacheProperties {
    /**
     * 如果有此配置 则使用
     * <p>
     * 例子：127.0.0.2,127.0.0.3,127.0.0.1,10.13.145.149 逗号分隔
     */
    private String ips;
    /**
     * 如果有此配置 则使用
     */
    @NestedConfigurationProperty
    private DataCenterProperties dataCenter;
    /**
     * 如果有此配置 则使用
     */
    @NestedConfigurationProperty
    private ZkProperties zk;
    /**
     * 阈值
     * 剩余ID低于此阈值数量 生成新的一批ID
     * 默认低于50个ID时 继续获取
     */
    private int ThresholdValue = 50;
    /**
     * 系统唯一标示 隔离不通业务系统 单个系统内保证id不重复
     */
    @NotBlank
    private String systemCode;
    /**
     * 步长
     * 默认100
     */
    @Max(value = 1000)
    @Min(value = 100)
    private int stepNum = 100;

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


    public int getThresholdValue() {
        return ThresholdValue;
    }

    public void setThresholdValue(int thresholdValue) {
        ThresholdValue = thresholdValue;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public int getStepNum() {
        return stepNum;
    }

    public void setStepNum(int stepNum) {
        this.stepNum = stepNum;
    }
}
