package org.osicu.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author osicu
 * @Description
 * @date: 2020/12/29 10:14
 */
public class ZkUtil {
    private static final Logger log = LoggerFactory.getLogger(ZkUtil.class);
    private static final String BASE_PATH = "/org/osicu/idGeneration/workId/{systemCode}";
    private static final String NODE_PATH = "/nodes";

    private static ZkUtil instance;
    private CuratorFramework client;

    private ZkUtil() {
    }

    public static ZkUtil newInstance(String zkAddress) {
        if (Objects.isNull(instance)) {
            synchronized (ZkUtil.class) {
                if (Objects.isNull(instance)) {
                    RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
                    CuratorFramework client = CuratorFrameworkFactory.newClient(zkAddress, retryPolicy);
                    client.start();
                    Runtime.getRuntime().addShutdownHook(new Thread(client::close));
                    instance = new ZkUtil();
                }
            }
        }
        return instance;
    }


    /***
     * 获取workId
     * 例子：
     * path:/org/osicu/idGeneration/workId/{systemCode}/nodes/127_0_0_1
     * data:0
     * path:/org/osicu/idGeneration/workId/{systemCode}/nodes/127_0_0_2
     * data:1
     *
     * @Futrue 未来可以设计下架的IP 进行回收
     * 比如刚开始有ip1,ip2,ip3对应的workerId分表是0,1,2 后来业务改变或设备故障 回收了ip2 那么当新增ip4时 无法使用ip2的workerId 造成资源浪费
     * 需要处理当ip4使用老workerId时 可能造成的ID重复问题
     */
    public long buildWorkId(long lockTimeOut, String systemCode, int maxWorkerIdNum) throws Exception {
        long workerId;
        String pathBase = BASE_PATH.replaceAll("\\{systemCode}", systemCode);
// lockPath,用于加锁，注意要与nodePath区分开
//        final String lockPath = pathBase;
// nodePath 用于存放集群各节点初始路径
        final String nodePath = pathBase + NODE_PATH;

        // InterProcessMutex 分布式锁（加锁过程中lockPath会自动创建） 锁最细在 systemCode 维度下
        InterProcessLock interProcessLock = new InterProcessMutex(client, pathBase);
        try {
            // 加锁 此处逻辑非常重要 分布式应用 用于抢占资源
            if (!interProcessLock.acquire(lockTimeOut, TimeUnit.MILLISECONDS)) {
                throw new TimeoutException("ZK分布式锁 加锁超时，超时时间: " + lockTimeOut);
            }

            // nodePath 第一次需初始化，永久保存, 或者节点路径为临时节点，则设置为永久节点
            if (Objects.isNull(client.checkExists().forPath(nodePath))) {
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(nodePath);
            }
            //  2020/12/28 获取是否存在当前ip的目录 如果有 则用 否则 新增
            String localIp = InetAddress.getLocalHost().getHostAddress();
            String ip = localIp.replaceAll("\\.", "_");
            // 获取nodePath下已经创建的子节点
            String ipPath = nodePath + "/" + ip;
            if (Objects.nonNull(client.checkExists().forPath(ipPath))) {
                byte[] bytes = client.getData().forPath(ipPath);
                String orderInfo = new String(bytes, "UTF-8");
                workerId = Long.parseLong(orderInfo);
                log.info("已存在 workerId={}", workerId);
                return workerId;
            }
            log.debug("不存在目录{}，说明此ip{}首次创建", ipPath, ip);
            List<String> childPath = client.getChildren().forPath(nodePath);
            if (CollectionUtils.isEmpty(childPath)) {
                client.create().withMode(CreateMode.PERSISTENT).forPath(ipPath, ("0").getBytes("UTF-8"));
                workerId = 0;
            } else {
                if (childPath.size() >= maxWorkerIdNum) {
                    throw new RuntimeException("获取WorkId失败，共[" + maxWorkerIdNum + "]个可用WorkId, 已全部用完。请扩展 maxWorkerIdNum 属性 ");
                }
                int order = childPath.size();
                client.create().withMode(CreateMode.PERSISTENT).forPath(ipPath, (order + "").getBytes("UTF-8"));
                workerId = order;
            }
            log.info("基于ZK成功构建 workerId={}", workerId);
            return workerId;
        } catch (Exception e) {
            log.error("获取分布式WorkId异常e={}", e);
            throw e;
        } finally {
            // 释放锁
            try {
                interProcessLock.release();
            } catch (Exception e) {
                log.warn("释放锁失败");
            }
        }
    }

}
