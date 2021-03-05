package org.osicu.impl;

import org.osicu.config.ZkProperties;
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
    private static final int MAX_ORDER = 1024;

    /**
     * zk 获取WorkerId
     *
     * @param zkProperties
     * @return
     */
    public static long getWorkerId(ZkProperties zkProperties,String systemCode) throws Exception {
        String zkAddress = zkProperties.getZkAddress();
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(zkAddress, retryPolicy);
        client.start();
        long w = buildWorkId(client, zkProperties,systemCode);
        Runtime.getRuntime().addShutdownHook(new Thread(client::close));
        return w;
    }

    /***
     * 获取workId
     */
    private static long buildWorkId(CuratorFramework client, ZkProperties zkProperties,String systemCode) throws Exception {
        long workerId;
        String pathBase = BASE_PATH.replaceAll("\\{systemCode}", systemCode);
// lockPath,用于加锁，注意要与nodePath区分开
        final String lockPath = pathBase;
// nodePath 用于存放集群各节点初始路径
        final String nodePath = pathBase + NODE_PATH;

        // InterProcessMutex 分布式锁（加锁过程中lockPath会自动创建）
        InterProcessLock interProcessLock = new InterProcessMutex(client, lockPath);
        try {
            long lockTimeOut = zkProperties.getLockTimeOut();
            // 加锁 此处逻辑非常重要
            if (!interProcessLock.acquire(lockTimeOut, TimeUnit.MILLISECONDS)) {
                throw new TimeoutException("ZK分布式锁 加锁超时，超时时间: " + lockTimeOut);
            }

            // nodePath 第一次需初始化，永久保存, 或者节点路径为临时节点，则设置为永久节点
            if (null == client.checkExists().forPath(nodePath)) {
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(nodePath);
            }
            //  2020/12/28 获取是否存在当前ip的目录 如果有 则用 否则 新增
            String localIp = InetAddress.getLocalHost().getHostAddress();
            String ip = localIp.replaceAll("\\.", "_");
            // 获取nodePath下已经创建的子节点

            String ipPath = nodePath + "/" + ip;
            if (null != client.checkExists().forPath(ipPath)) {
                byte[] bytes = client.getData().forPath(ipPath);
                String orderInfo = new String(bytes, "UTF-8");
                workerId = Long.valueOf(orderInfo);
                log.info("已存在 workerId={}", workerId);
                return workerId;
            }
            List<String> childPath = client.getChildren().forPath(nodePath);
            if (CollectionUtils.isEmpty(childPath) || childPath.size() == 0) {
                client.create().withMode(CreateMode.PERSISTENT).forPath(ipPath, ("0").getBytes("UTF-8"));
                workerId = 0;
            } else {
                if (childPath.size() >= MAX_ORDER) {
                    throw new RuntimeException("获取WorkId失败，共[" + MAX_ORDER + "]个可用WorkId, 已全部用完。 ");
                }
                int order = childPath.size();

                client.create().withMode(CreateMode.PERSISTENT).forPath(ipPath, (order + "").getBytes("UTF-8"));
                workerId = order;
            }
            log.info("基于ZK成功构建 workerId={}", workerId);
            return workerId;
        } catch (Exception e) {
            log.error("获取分布式WorkId异常", e);
            throw e;
        } finally {
            // 构建成功后释放锁
            try {
                interProcessLock.release();
            } catch (Exception e) {
                log.warn("释放锁失败");
            }
        }
    }

}
