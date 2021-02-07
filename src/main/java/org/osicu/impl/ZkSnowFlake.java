package org.osicu.impl;

import org.osicu.config.IdConfigProperties;
import org.osicu.config.SnowFlakeProperties;
import org.osicu.config.ZkProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

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
    private ZkProperties zkProperties;


    public ZkSnowFlake(IdConfigProperties idConfigProperties) throws Exception {
        this.idConfigProperties = idConfigProperties;
        SnowFlakeProperties snowFlake = idConfigProperties.getSnowFlake();
        this.zkProperties = snowFlake.getZk();
        init();
    }

    private void init() throws Exception {
        workerId = ZkUtil.getWorkerId(zkProperties);
    }

    @Override
    protected long getWorkerId() {
        return workerId;
    }

    @Override
    protected SnowFlakeProperties getSnowFlakeProperties() {
        return idConfigProperties.getSnowFlake();
    }

    /**
     * 路由
     * 获取连接那台zk服务器
     * 可根据zk集群中的所有ip进行ping 那个速度快 用哪个
     * 也可以其他路由策略
     *
     * @param zkIndex
     * @return
     * @deprecated 直接使用 curator
     */
    @Deprecated
    private String getIndex4Route(Set<String> zkIndex) {
        ZkRoute zkRoute = new ZkRouteDefaultImpl();
        return zkRoute.getIndex4Route(zkIndex);
    }

    /**
     * 默认 直接返回第一个
     *
     * @deprecated 直接使用 curator
     */
    @Deprecated
    class ZkRouteDefaultImpl implements ZkRoute {

        @Override
        public String getIndex4Route(Set<String> zkIndex) {
            return zkIndex.iterator().next();
        }
    }

    /**
     * 根据zk集群中的所有ip进行ping 那个速度快 用哪个
     *
     * @deprecated 直接使用 curator
     */
    @Deprecated
    class ZkRouteFastImpl implements ZkRoute {

        @Override
        public String getIndex4Route(Set<String> zkIndex) {
            return null;
        }
    }
}
