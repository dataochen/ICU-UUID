package org.osicu.impl.localcache;

import org.osicu.config.DataCenterProperties;
import org.osicu.config.IdConfigProperties;
import org.osicu.config.LocalCacheProperties;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

/**
 * @author osicu
 * @Description 多数据中心 不同网段下使用
 * 多数据中心网络不通或存在同一ip的情况下使用
 * 原理：数据中心id和ip共用workerId字段
 * 此处默认数据中心2位bit 最大4个数据中心；ip还是10-2=8 256台机器ip
 * @date: 2020/11/3 16:57
 */

public class MultiDataCenterLocalCache extends AbstractLocalCacheGenerateImpl {
    public MultiDataCenterLocalCache(IdConfigProperties idConfigProperties) {
        this.idConfigProperties = idConfigProperties;
    }

    private Long workerIdCache;
    private static String localIp;
    private IdConfigProperties idConfigProperties;

    static {
        try {
            localIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new Error("不能够获取到本机ip，启动不成功。e=" + e.getMessage());
        }
    }

    @Override
    protected long getWorkerId() throws Exception {
        if (null != workerIdCache) {
            return workerIdCache;
        }
        LocalCacheProperties localCache = idConfigProperties.getLocalCache();
        DataCenterProperties dataCenter = localCache.getDataCenter();
        Long dataCenterId = (long) dataCenter.getDataCenterIndex();
        if (dataCenterId >= 3) {
            throw new Exception("数据中心超过4个，不支持");
        }
        String ips = dataCenter.getIps();
        String[] split = ips.split(",");
        List<String> collect = Arrays.asList(split);
        for (int i = 0; i < collect.size(); i++) {
            if (localIp.equals(collect.get(i))) {
                workerIdCache = dataCenterId << 8 | (long) i;
                return i;
            }
        }
        throw new Exception("没有找到ip");
    }

    @Override
    protected LocalCacheProperties getLocalCacheProperties() {
        return idConfigProperties.getLocalCache();
    }



}
