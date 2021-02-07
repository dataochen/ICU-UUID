package org.osicu.route;

import org.osicu.IdGenerateInterface;
import org.osicu.config.IdConfigProperties;
import org.osicu.config.LocalCacheProperties;
import org.osicu.config.SnowFlakeProperties;
import org.osicu.impl.DefaultSnowFlake;
import org.osicu.impl.MultiDataCenterSnowFlake;
import org.osicu.impl.ZkSnowFlake;
import org.osicu.impl.localcache.DefaultLocalCache;
import org.osicu.impl.localcache.MultiDataCenterLocalCache;
import org.osicu.impl.localcache.ZkLocalCache;

/**
 * @author osicu
 * @Description
 * @date: 2020/11/3 15:22
 */
public class RouteImpl {

    /**
     * 路由
     * 1.如果有snowflake配置 会使用雪花算法
     * 1.1如果有snowflake.datacenter 配置 则使用 MultiDataCenterSnowFlake 生产id
     * 1.2如果有zk.index 配置 则使用 ZkSnowFlake 生产id
     *todo 优化 策略模式
     * @return
     */
    public IdGenerateInterface route(IdConfigProperties idConfigProperties) {
        SnowFlakeProperties snowFlake = idConfigProperties.getSnowFlake();
        LocalCacheProperties localCache = idConfigProperties.getLocalCache();
        if (null != snowFlake) {
            if (null != snowFlake.getDataCenter()) {
                return new MultiDataCenterSnowFlake(idConfigProperties);
            }
            if (null != snowFlake.getZk()) {
                return new ZkSnowFlake(idConfigProperties);
            }
            return new DefaultSnowFlake(idConfigProperties);
        } else if (null != localCache) {
            if (null != localCache.getZk()) {
                return new ZkLocalCache(idConfigProperties);
            }
            if (null != localCache.getDataCenter()) {
                return new MultiDataCenterLocalCache(idConfigProperties);
            }
            return new DefaultLocalCache(idConfigProperties);
        }
        throw new IllegalArgumentException("没有路由到相应实现,本期只支持雪花算法生成ID。有可能你没有配置snowFlake的参数！");
    }
}
