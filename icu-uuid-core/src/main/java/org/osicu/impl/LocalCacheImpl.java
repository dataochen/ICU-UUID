package org.osicu.impl;

import org.osicu.IdGenerateInterface;
import org.osicu.config.LocalCacheProperties;
import org.osicu.impl.localcache.IdTableCache;
import org.osicu.route.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author chendatao
 * @since 1.0
 */
public class LocalCacheImpl extends AbstractWorkerId implements IdGenerateInterface {
    private final static Logger log = LoggerFactory.getLogger(LocalCacheImpl.class);

    public LocalCacheImpl(LocalCacheProperties localCache, String systemCode) {
        this.localCacheProperties = localCache;
        this.systemCode = systemCode;
    }

    private LocalCacheImpl() {

    }

    private LocalCacheProperties localCacheProperties;
    private String systemCode;
    /**
     * 存储自增ID
     * 默认步长的2倍
     */
    private ArrayBlockingQueue<Long> arrayBlockingQueue = new ArrayBlockingQueue<Long>(localCacheProperties.getStepNum() << 1);

    @Override
    public long nextId() throws Exception {
        return convertId();
    }

    @Override
    public void printRange() {
        long everyMaxNo = localCacheProperties.getEveryMaxNo();
        int maxWorkerIdNum = localCacheProperties.getWorkIdStrategy().getMaxWorkerIdNum();
        log.info("可通过调节org.osicu.config.LocalCacheProperties.everyMaxNo 和 org.osicu.config.WorkIdStrategy.maxWorkerIdNum 来控制ID范围。");
        log.info("当每台机器支持的最大ID数量为{}，最大机器数{}时", everyMaxNo, maxWorkerIdNum);
        //  everyMaxNo  maxWorkerIdNum ID范围 长度范围
        long max = maxWorkerIdNum << CommonUtil.bitUp(everyMaxNo) | everyMaxNo;
        log.info("ID范围是0~{} 长度范围是0~{}", max, String.valueOf(max).length());
    }

    /**
     * synchronized 线性安全的
     *
     * @return
     * @throws Exception
     */
    private synchronized long convertId() throws Exception {
        if (needNewIds()) {
            log.debug("ID不充足，生成新的一批IDS");
//            next-v 可以异步生成新ID
            ThreadUtil.executeAsync(() -> {
                try {
                    generateIdList();
                } catch (Exception e) {
                    log.error("异步生成ID失败，请检查配置，e={}", e);
                }
            });
        }
        Long poll = arrayBlockingQueue.poll(3000, TimeUnit.MILLISECONDS);
        if (null == poll) {
            log.error("获取ID超时，超过3秒。");
            throw new RuntimeException("获取ID超时，超过3秒。");
        }
        long workerId = getWorkerId(localCacheProperties, systemCode);
        ;
        if (workerId < 0) {
            throw new IllegalStateException("获取workerId失败");
        }
        long everyMaxNo = localCacheProperties.getEveryMaxNo();
        if (poll > everyMaxNo) {
            throw new IllegalArgumentException("每台机器上生产的ID已超限，请调大最大限制，当前最大限制" + everyMaxNo);
        }
        return workerId << CommonUtil.bitUp(everyMaxNo) | poll;
    }

    private void generateIdList() throws IOException, InterruptedException {
//        查询当前系统 最大的id和步长
        IdTableCache idTableCache = fileParse();
        long maxNo = idTableCache.getMaxNo();
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
    private IdTableCache fileParse() throws IOException {
        IdTableCache idTableCache = new IdTableCache();
        File file = new File(localCacheProperties.getBasePath() + "idTable/" + systemCode);
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
    private void writeFile(IdTableCache idTableCache) throws IOException {
        File file = new File(localCacheProperties.getBasePath() + "idTable/" + systemCode);
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
        return arrayBlockingQueue.size() <= localCacheProperties.getStepNum() * (1 - localCacheProperties.getThresholdValue());
    }

    public static void main(String[] args) {
        String s = Long.toHexString(1100);
        System.out.println(s);
    }

}
