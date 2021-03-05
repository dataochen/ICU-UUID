package org.osicu.impl.localcache;

import org.osicu.config.IdConfigProperties;
import org.osicu.config.LocalCacheProperties;
import org.osicu.config.ZkProperties;
import org.osicu.impl.ZkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author osicu
 * @Description 通过zk获取机器的唯一id
 * @date: 2020/11/3 16:56
 */
public class ZkLocalCache extends AbstractLocalCacheGenerateImpl {
    private static final Logger log = LoggerFactory.getLogger(ZkLocalCache.class);
    private long workerId;
    private IdConfigProperties idConfigProperties;
    private ZkProperties zkProperties;


    public ZkLocalCache(IdConfigProperties idConfigProperties) throws Exception {
        this.idConfigProperties = idConfigProperties;
        LocalCacheProperties localCache = idConfigProperties.getLocalCache();
        this.zkProperties = localCache.getWorkIdStrategy().getZk();
        init();
    }

    private void init() throws Exception {
        workerId = ZkUtil.getWorkerId(zkProperties, idConfigProperties.getSystemCode());
    }

    @Override
    protected long getWorkerId() {
        return workerId;
    }

    @Override
    protected IdConfigProperties getLocalCacheProperties() {
        return idConfigProperties;
    }

    @Override
    public void printRange() {
        // TODO: 2021/3/5
    }
}
