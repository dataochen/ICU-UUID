package org.osicu.impl;

import org.osicu.config.DataCenterProperties;
import org.osicu.config.IdPropertiesImpl;
import org.osicu.config.Ips;
import org.osicu.config.WorkIdStrategy;
import org.osicu.route.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * workerId 获取策略
 *
 * @author chendatao
 */
public abstract class AbstractWorkerId implements WorkerIdInterface {
    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractWorkerId.class);
//    -------------- 私有 -------------
    /**
     * todo
     */
    private Long workerIdCache;
    private static String localIp;

    static {
        try {
            localIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new Error("不能够获取到本机ip，启动不成功。e=" + e.getMessage());
        }
    }

    /**
     * 上下文
     */
    abstract IdPropertiesImpl convertIdPropertiesImpl();
    abstract String convertSystemCode();

    @Override
    public long getWorkerId() throws Exception {
        if (Objects.nonNull(workerIdCache)) {
            return workerIdCache;
        }
        IdPropertiesImpl   idProperties = convertIdPropertiesImpl();
        String  systemCode = convertSystemCode();
        WorkIdStrategy workIdStrategy = idProperties.getWorkIdStrategy();
        if (Objects.nonNull(workIdStrategy.getIps())) {
            Ips ips = workIdStrategy.getIps();
            String[] split = ips.getValue().split(",");
            List<String> collect = Arrays.asList(split);
            DataCenterProperties dataCenterProperties = idProperties.getDataCenterProperties();
            for (int i = 0; i < collect.size(); i++) {
                if (localIp.equals(collect.get(i))) {
                    workerIdCache = (long) i;
                    if (Objects.nonNull(dataCenterProperties)) {
                        int dataCenterIndex = dataCenterProperties.getDataCenterIndex();
                        LOGGER.info("ID生成器启动，数据中心：{}", dataCenterIndex);
                        workerIdCache = dataCenterIndex << CommonUtil.bitUp(workIdStrategy.getMaxIpNum()) | workerIdCache;
                    }
                    return workerIdCache;
                }
            }
            throw new IllegalArgumentException("没有找到ip");
        } else if (Objects.nonNull(workIdStrategy.getZk())) {
            workerIdCache = ZkUtil.getWorkerId(workIdStrategy.getZk(), systemCode);
            if (workerIdCache > workIdStrategy.getMaxIpNum()) {
                throw new IllegalArgumentException("zk获取的workId超过最大限制");
            }
            DataCenterProperties dataCenterProperties = idProperties.getDataCenterProperties();
            if (Objects.nonNull(dataCenterProperties)) {
                int dataCenterIndex = dataCenterProperties.getDataCenterIndex();
                LOGGER.info("ID生成器启动，数据中心：{}", dataCenterIndex);
                workerIdCache = dataCenterIndex << CommonUtil.bitUp(workIdStrategy.getMaxIpNum()) | workerIdCache;
            }

        }
        throw new IllegalArgumentException("不合法的workerId策略");
    }
}