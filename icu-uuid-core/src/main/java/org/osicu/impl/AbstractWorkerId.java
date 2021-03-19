package org.osicu.impl;

import org.osicu.config.DataCenterProperties;
import org.osicu.config.IdPropertiesBean;
import org.osicu.config.Ips;
import org.osicu.config.WorkIdStrategy;
import org.osicu.spi.SpiFactory;
import org.osicu.spi.WorkeStrategyLegalInterface;
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
 * @since 1.0
 */
public abstract class AbstractWorkerId implements WorkerIdInterface {
    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractWorkerId.class);
    /**
     * 缓存workerId
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


    @Override
    public long getWorkerId(IdPropertiesBean idProperties, String systemCode) throws Exception {
        //  2021/3/8 扩展接口 自定义获取workerId方式
//        如果有自定义的实现类 优先使用自定义实现类
        WorkerIdInterface workerIdInterface = SpiFactory.getObject(WorkerIdInterface.class);
        if (Objects.nonNull(workerIdInterface)) {
            return workerIdInterface.getWorkerId(idProperties, systemCode);
        }
        if (Objects.nonNull(workerIdCache)) {
            return workerIdCache;
        }
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
                        workerIdCache = dataCenterIndex << CommonUtil.bitUp(workIdStrategy.getMaxWorkerIdNum()) | workerIdCache;
                    }
                    return workerIdCache;
                }
            }
            throw new IllegalArgumentException("没有在配置中找到ip" + localIp + "，请检查配置是否正确。");
        } else if (Objects.nonNull(workIdStrategy.getZk())) {
            workerIdCache = ZkUtil.newInstance(workIdStrategy.getZk().getZkAddress()).buildWorkId(workIdStrategy.getZk().getLockTimeOut(), systemCode, workIdStrategy.getMaxWorkerIdNum());
            if (workerIdCache > workIdStrategy.getMaxWorkerIdNum()) {
                throw new IllegalArgumentException("zk获取的workId超过最大限制");
            }
            DataCenterProperties dataCenterProperties = idProperties.getDataCenterProperties();
            if (Objects.nonNull(dataCenterProperties)) {
                int dataCenterIndex = dataCenterProperties.getDataCenterIndex();
                LOGGER.info("ID生成器启动，数据中心：{}", dataCenterIndex);
                workerIdCache = dataCenterIndex << CommonUtil.bitUp(workIdStrategy.getMaxWorkerIdNum()) | workerIdCache;
            }

        }
        //  2021/3/8 自定义 workerId 实现逻辑
        WorkeStrategyLegalInterface workeStrategyLegalInterface = SpiFactory.getObject(WorkeStrategyLegalInterface.class);
        if (Objects.nonNull(workeStrategyLegalInterface)) {
            workeStrategyLegalInterface.callBack4iiLegalWorkIdStrategy();
        }
        throw new IllegalArgumentException("不合法的workerId策略");
    }
}
