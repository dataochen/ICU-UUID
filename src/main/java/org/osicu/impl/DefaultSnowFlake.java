package org.osicu.impl;

import org.osicu.config.IdConfigProperties;
import org.osicu.config.SnowFlakeProperties;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

/**
 * @author osicu
 * @Description 默认的雪花算法 通过配置文件来获取机器的id
 * ip必须唯一
 * @date: 2020/11/3 16:54
 */
public class DefaultSnowFlake extends AbstractSnowFlakeImpl {
    public DefaultSnowFlake(IdConfigProperties idConfigProperties) {
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
        SnowFlakeProperties snowFlake = idConfigProperties.getSnowFlake();
        String ips1 = snowFlake.getIps();
        String[] split = ips1.split(",");
        List<String> collect = Arrays.asList(split);
        for (int i = 0; i < collect.size(); i++) {
            if (localIp.equals(collect.get(i))) {
                workerIdCache = (long) i;
                return i;
            }
        }
        throw new Exception("没有找到ip");
    }

    @Override
    protected SnowFlakeProperties getSnowFlakeProperties() {
        return idConfigProperties.getSnowFlake();
    }

}
