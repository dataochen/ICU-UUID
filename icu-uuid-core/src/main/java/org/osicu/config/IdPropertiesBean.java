package org.osicu.config;

import org.osicu.impl.AbstractWorkerId;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * @author chendatao
 */
public class IdPropertiesBean implements IdProperties {
    /**
     * 获取workId的策略
     * 参考{@link AbstractWorkerId#getWorkerId(org.osicu.config.IdPropertiesBean, java.lang.String)}
     * <ul>
     *     <li>ip:配置所有ip,在系统启动时获取ip的下标作为全局唯一workId </li>
     *     <li>zk:通过zk锁获取全局唯一workId </li>
     * </ul>
     */
    @NotNull
    private WorkIdStrategy workIdStrategy;
    /**
     * 数据中心
     * 可空
     *
     */
    private DataCenterProperties dataCenterProperties;


    @Override
    public void checkProperties() throws IllegalArgumentException {
        workIdStrategy.checkProperties();
        if (Objects.nonNull(dataCenterProperties)) {
            dataCenterProperties.checkProperties();
        }
    }

    public WorkIdStrategy getWorkIdStrategy() {
        return workIdStrategy;
    }

    public void setWorkIdStrategy(WorkIdStrategy workIdStrategy) {
        this.workIdStrategy = workIdStrategy;
    }

    public DataCenterProperties getDataCenterProperties() {
        return dataCenterProperties;
    }

    public void setDataCenterProperties(DataCenterProperties dataCenterProperties) {
        this.dataCenterProperties = dataCenterProperties;
    }
}
