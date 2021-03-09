package org.osicu.route;

import org.osicu.IdGenerateInterface;
import org.osicu.config.IdConfigProperties;
import org.osicu.config.LocalCacheProperties;
import org.osicu.config.SnowFlakeProperties;
import org.osicu.impl.LocalCacheImpl;
import org.osicu.impl.SnowFlakeImpl;

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
        // TODO: 2021/3/8 路由扩展接口 扩展自定义的序列号生成方式
        if (Objects.nonNull(snowFlake)) {
            return new SnowFlakeImpl(snowFlake, idConfigProperties.getSystemCode());
        } else if (Objects.nonNull(localCache)) {
            return new LocalCacheImpl(localCache, idConfigProperties.getSystemCode());
        }
        throw new IllegalArgumentException("没有路由到相应实现,本期只支持本地缓存算法和雪花算法生成ID。");
    }

    /**
     * 工厂
     * @return
     */
    public static IdGenerateFactory newInstance() {
        return new IdGenerateFactory();
    }
}
