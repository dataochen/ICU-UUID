package org.osicu.route;

import org.osicu.IdGenerateInterface;
import org.osicu.config.IdConfigProperties;
import org.osicu.config.LocalCacheProperties;
import org.osicu.config.SnowFlakeProperties;
import org.osicu.config.WorkIdStrategy;
import org.osicu.impl.DefaultSnowFlake;
import org.osicu.impl.ZkSnowFlake;
import org.osicu.impl.localcache.DefaultLocalCache;
import org.osicu.impl.localcache.ZkLocalCache;

import java.util.Objects;

/**
 * @author osicu
 * @Description
 * @date: 2020/11/3 15:22
 */
public class IdGenerateFactory {

    /**
     * 路由
     * 1.如果有snowflake配置 会使用雪花算法
     * 1.1如果有snowflake.datacenter 配置 则使用 MultiDataCenterSnowFlake 生产id
     * 1.2如果有zk.index 配置 则使用 ZkSnowFlake 生产id
     * README 优化 策略模式
     *
     * @return
     */
    public IdGenerateInterface getBean(IdConfigProperties idConfigProperties) throws Exception {
        SnowFlakeProperties snowFlake = idConfigProperties.getSnowFlake();
        LocalCacheProperties localCache = idConfigProperties.getLocalCache();
        if (null != snowFlake) {
            WorkIdStrategy workIdStrategy = snowFlake.getWorkIdStrategy();
            if (Objects.nonNull(workIdStrategy.getIps())) {
                return new DefaultSnowFlake(idConfigProperties);
            }
            if (Objects.nonNull(workIdStrategy.getZk())) {
                return new ZkSnowFlake(idConfigProperties);
            }
            throw new IllegalStateException("不支持的workId策略");
        } else if (null != localCache) {
            WorkIdStrategy workIdStrategy = localCache.getWorkIdStrategy();
            if (Objects.nonNull(workIdStrategy.getIps())) {
                return new DefaultLocalCache(idConfigProperties);
            }
            if (Objects.nonNull(workIdStrategy.getZk())) {
                return new ZkLocalCache(idConfigProperties);
            }
            throw new IllegalStateException("不支持的workId策略");
        }
        throw new IllegalArgumentException("没有路由到相应实现,本期只支持本地缓存算法和雪花算法生成ID。");
    }
}
