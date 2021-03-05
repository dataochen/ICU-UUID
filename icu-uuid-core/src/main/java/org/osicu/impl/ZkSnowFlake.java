package org.osicu.impl;

import org.osicu.config.DataCenterProperties;
import org.osicu.config.IdConfigProperties;
import org.osicu.config.SnowFlakeProperties;
import org.osicu.route.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * @author osicu
 * @Description 通过zk获取机器的唯一id
 * 所有机器必须连接同一zk集群 多数据中心可用  MultiDataCenterSnowFlake
 * @date: 2020/11/3 16:56
 */
public class ZkSnowFlake extends AbstractSnowFlakeImpl {
    private static final Logger log = LoggerFactory.getLogger(ZkSnowFlake.class);
    private long workerId;
    private IdConfigProperties idConfigProperties;


    public ZkSnowFlake(IdConfigProperties idConfigProperties) throws Exception {
        this.idConfigProperties = idConfigProperties;
        init();
    }


    @Override
    protected long getWorkerId() {
        return workerId;
    }

    @Override
    protected SnowFlakeProperties getSnowFlakeProperties() {
        return idConfigProperties.getSnowFlake();
    }

    private void init() throws Exception {
        SnowFlakeProperties snowFlake = idConfigProperties.getSnowFlake();
        long workerId = ZkUtil.getWorkerId(snowFlake.getWorkIdStrategy().getZk(),idConfigProperties.getSystemCode());
        if (workerId > snowFlake.getWorkIdStrategy().getMaxIpNum()) {
            throw new IllegalArgumentException("zk获取的workId超过最大限制");
        }
        DataCenterProperties dataCenterProperties = snowFlake.getDataCenterProperties();
        if (Objects.nonNull(dataCenterProperties)) {
            int dataCenterIndex = dataCenterProperties.getDataCenterIndex();
            log.info("ID生成器启动，数据中心：{}", dataCenterIndex);
            workerId = dataCenterIndex << CommonUtil.bitUp(snowFlake.getWorkIdStrategy().getMaxIpNum()) | workerId;
        }
        this.workerId = workerId;
    }

}
