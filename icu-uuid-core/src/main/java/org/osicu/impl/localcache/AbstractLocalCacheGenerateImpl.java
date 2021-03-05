package org.osicu.impl.localcache;

import org.osicu.IdGenerateInterface;
import org.osicu.config.IdConfigProperties;
import org.osicu.config.LocalCacheProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author osicu
 * @Description 本地内存+磁盘生成
 * 原理：
 * * 43 位二级制数字
 * 000 0000000-0 00000000 00000000 00000000 00000000
 * 第1位到第10位 共10位用于记录工作机器id 十进制最大1024
 * 第11位到43位 共33位用于记录序列号 十进制最大 8589934592  8.5亿
 * <p>
 * 本地磁盘维护步长表
 * 实现步骤：
 * 1.首次启动和重启后检查是否存在当前系统记录（磁盘里）
 * 2.如果不存在初始化 存在继续使用
 * 3.异步生成新id范围，触发条件为：当剩余量小于阈值时 生成新一批ID
 * 4.获取ID并记录内存中目前ID；内存中维护ID缓存队列
 * @date: 2020/12/22 17:52
 * <p>
 */
public abstract class AbstractLocalCacheGenerateImpl implements IdGenerateInterface {
    private static final Logger log = LoggerFactory.getLogger(AbstractLocalCacheGenerateImpl.class);
    private IdConfigProperties idConfigProperties;

    private ArrayBlockingQueue<Long> arrayBlockingQueue = new ArrayBlockingQueue<Long>(1024);
    /**
     * 文件存储根目录
     * README 检查是否有写权限 没有启动抛异常；可支持自定义路径
     */
    private final String BASE_PATH_TABLE = "/export/Data/id/";

    @Override
    public String nextId() throws Exception {
        if (null == idConfigProperties) {
            idConfigProperties = getLocalCacheProperties();
        }
        try {
            return convertId();
        } catch (Exception e) {
            log.error("获取ID异常 e={}", e);
            throw e;
        }
    }

    /**
     * 获取当前服务器的唯一id
     *
     * @return
     */
    protected abstract long getWorkerId() throws Exception;

    protected abstract IdConfigProperties getLocalCacheProperties();


    private synchronized String convertId() throws Exception {
        if (needNewIds()) {
            log.debug("ID不充足，生成新的一批IDS");
//            next-v 可以异步生成新ID
            generateIdList();
        }
        Long poll = arrayBlockingQueue.poll(3000, TimeUnit.MILLISECONDS);
        if (null == poll) {
            log.error("获取ID超时，超过3秒。");
            throw new RuntimeException("获取ID超时，超过3秒。");
        }
        long workerId = getWorkerId();
        if (workerId < 0) {
            throw new IllegalStateException("获取workerId失败");
        }
        long id = workerId << 33 | poll;
        return id + "";
    }

    private void generateIdList() throws Exception {
//        查询当前系统 最大的id和步长
        IdTableCache idTableCache = fileParse();
        long maxNo = idTableCache.getMaxNo();
        LocalCacheProperties localCacheProperties = idConfigProperties.getLocalCache();
        log.debug("{} maxNo={} stepNum={}", idConfigProperties.getSystemCode(), maxNo, localCacheProperties.getStepNum());
        idTableCache.setMaxNo(maxNo + localCacheProperties.getStepNum());
//        写磁盘
        writeFile(idTableCache);
//        写内存-队列
        for (int i = 0; i < localCacheProperties.getStepNum(); i++) {
            arrayBlockingQueue.put(maxNo + i + 1);
        }
    }

    /**
     * 如果存在文件则解析 否则只创建文件
     * systemCode:x
     * maxNo:x
     */
    private IdTableCache fileParse() throws Exception {
        IdTableCache idTableCache = new IdTableCache();
        File file = new File(BASE_PATH_TABLE + "idTable/" + idConfigProperties.getSystemCode());
        if (!file.exists()) {
//                创建文件
            log.info("初始化 {} idTable文件", idConfigProperties.getSystemCode());
            File fileParent = file.getParentFile();
            String fileParentPath = file.getParent();
            if (!fileParent.exists()) {
                fileParent.mkdirs();
            }
            file.createNewFile();
            idTableCache.setSystemCode(idConfigProperties.getSystemCode());
            idTableCache.setMaxNo(0L);
            log.info("首次启动");
            return idTableCache;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            idTableCache.setSystemCode(br.readLine().split(":")[1]);
            idTableCache.setMaxNo(Long.valueOf(br.readLine().split(":")[1]));
            return idTableCache;
        }
    }

    /**
     * 写磁盘
     * systemCode:x
     * maxNo:x
     *
     * @param idTableCache
     * @throws Exception
     */
    private void writeFile(IdTableCache idTableCache) throws Exception {
        File file = new File(BASE_PATH_TABLE + "idTable/" + idConfigProperties.getSystemCode());
        if (!file.exists()) {
//                创建文件
            log.info("初始化 {} idTable文件", idConfigProperties.getSystemCode());
            File fileParent = file.getParentFile();
            String fileParentPath = file.getParent();
            if (!fileParent.exists()) {
                fileParent.mkdirs();
            }
            file.createNewFile();
        }
        try (BufferedWriter br = new BufferedWriter(new FileWriter(file))) {
            br.write("systemCode:" + idTableCache.getSystemCode() + "\n");
            br.write("maxNo:" + idTableCache.getMaxNo() + "\n");
            br.flush();
        }
    }

    /**
     * 是否需要创建新ID
     *
     * @return
     */
    private boolean needNewIds() {
        int size = arrayBlockingQueue.size();
        LocalCacheProperties localCacheProperties = idConfigProperties.getLocalCache();
        float thresholdValue = localCacheProperties.getThresholdValue();
        return size <= localCacheProperties.getStepNum() * (1 - thresholdValue);
    }
}
