package org.osicu.impl;

import org.osicu.IdGenerateInterface;
import org.osicu.config.IdPropertiesImpl;
import org.osicu.config.LocalCacheProperties;
import org.osicu.impl.localcache.IdTableCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author chendatao
 */
public class LocalCacheImpl extends AbstractWorkerId implements IdGenerateInterface {
    private final static Logger log = LoggerFactory.getLogger(LocalCacheImpl.class);

    public LocalCacheImpl(LocalCacheProperties localCache,String systemCode) {
        this.localCacheProperties = localCache;
        this.systemCode = systemCode;
    }

    private LocalCacheImpl() {

    }

    private LocalCacheProperties localCacheProperties;
    private String systemCode;

    private ArrayBlockingQueue<Long> arrayBlockingQueue = new ArrayBlockingQueue<Long>(1024);
    /**
     * 文件存储根目录
     * README 检查是否有写权限 没有启动抛异常；可支持自定义路径
     */
    private final String BASE_PATH_TABLE = "/export/Data/id/";

    @Override
    public String nextId() throws Exception {
        return convertId();
    }

    @Override
    public void printRange() {

    }

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
//        LocalCacheProperties localCacheProperties = idConfigProperties.getLocalCache();
        log.debug("{} maxNo={} stepNum={}", systemCode, maxNo, localCacheProperties.getStepNum());
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
        File file = new File(BASE_PATH_TABLE + "idTable/" + systemCode);
        if (!file.exists()) {
//                创建文件
            log.info("初始化 {} idTable文件", systemCode);
            File fileParent = file.getParentFile();
            String fileParentPath = file.getParent();
            if (!fileParent.exists()) {
                fileParent.mkdirs();
            }
            file.createNewFile();
            idTableCache.setSystemCode(systemCode);
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
        File file = new File(BASE_PATH_TABLE + "idTable/" + systemCode);
        if (!file.exists()) {
//                创建文件
            log.info("初始化 {} idTable文件", systemCode);
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
        float thresholdValue = localCacheProperties.getThresholdValue();
        return size <= localCacheProperties.getStepNum() * (1 - thresholdValue);
    }

    @Override
    IdPropertiesImpl convertIdPropertiesImpl() {
        return localCacheProperties;
    }

    @Override
    String convertSystemCode() {
        return systemCode;
    }
}
