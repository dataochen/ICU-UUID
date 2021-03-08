package org.osicu.impl;

import org.osicu.IdGenerateInterface;
import org.osicu.config.SnowFlakeProperties;
import org.osicu.route.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chendatao
 */
public class SnowFlakeImpl extends AbstractWorkerId implements IdGenerateInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(SnowFlakeImpl.class);

    private SnowFlakeImpl() {

    }

    public SnowFlakeImpl(SnowFlakeProperties snowFlakeProperties, String systemCode) {
        this.snowFlakeProperties = snowFlakeProperties;
        this.systemCode = systemCode;
    }

    private SnowFlakeProperties snowFlakeProperties;
    private String systemCode;
    /**
     * 序列号
     * 支持并发
     */
    private long sequence = 0L;
    private long lastTimeStamp = -1L;

    @Override
    public long nextId() throws Exception {
        return convertId();
    }

    @Override
    public void printRange() {
//        打印 20年内的每年ID的范围：最小ID~最大ID 长度x~y 可调节并发数和最大workerId
        int i1 = CommonUtil.bitUp(snowFlakeProperties.getMaxTpsNum());
        int i2 = CommonUtil.bitUp(snowFlakeProperties.getWorkIdStrategy().getMaxWorkerIdNum());
        int maxWorkerIdNum = snowFlakeProperties.getWorkIdStrategy().getMaxWorkerIdNum();
        int maxTpsNum = snowFlakeProperties.getMaxTpsNum();
        LOGGER.info("可通过调节org.osicu.config.SnowFlakeProperties.maxTpsNum 和 org.osicu.config.WorkIdStrategy.maxWorkerIdNum 来控制ID范围。");
        LOGGER.info("当单台机器的每秒最大并发数为{}，最大机器数{}时", maxTpsNum, maxWorkerIdNum);
        for (int i = 0; i < 20; i++) {
            int min = i * 365 * 24 * 3600 << (i1 + i2);
            int max = (i + 1) * 365 * 24 * 3600 << (i1 + i2) | maxWorkerIdNum << i1 | maxTpsNum - 1;
            LOGGER.info("第{}年 ID范围是{}~{} 长度范围是{}~{} ", i + 1, min, max, String.valueOf(min).length(), String.valueOf(max).length());
        }

    }

    /**
     * NOTE:关于时间回拨：
     * 1.如果系统运行中 这时候时间回拨 代码是可以支持的
     * 2.如果系统重启后时间回拨了 由于无法获取到重启之前的时间 代码是不支持的 有可能会ID重复
     *
     * @return
     */
    private synchronized long convertId() throws Exception {
        long currentTimeMillis = System.currentTimeMillis() / 1000;
        if (lastTimeStamp > currentTimeMillis) {
            long maxRollBackTime = snowFlakeProperties.getMaxRollBackTime();
            if (lastTimeStamp - currentTimeMillis > maxRollBackTime) {
                throw new RuntimeException("时钟回拨了 上次的时间戳：" + lastTimeStamp + " 当前时间戳：" + currentTimeMillis);
            }
            Thread.sleep(maxRollBackTime);
        }
        long workerId = getWorkerId(snowFlakeProperties, systemCode);
        if (lastTimeStamp != currentTimeMillis) {
            sequence = 0L;
            lastTimeStamp = currentTimeMillis;
        } else {
            ++sequence;
        }
        //  2021/3/8 单位时间内 支持多少个ID序列号
        if (sequence >= snowFlakeProperties.getMaxTpsNum()) {
//            等待下一秒
            while (true) {
                long nextTimeMillis = System.currentTimeMillis() / 1000;
                if (nextTimeMillis > currentTimeMillis) {
                    currentTimeMillis = nextTimeMillis;
                    lastTimeStamp = currentTimeMillis;
                    sequence = 0L;
                    break;
                }
            }
        }
        long startTime = snowFlakeProperties.getStartTime();
        long l = currentTimeMillis - startTime;
        return l << (CommonUtil.bitUp(snowFlakeProperties.getMaxTpsNum()) + CommonUtil.bitUp(snowFlakeProperties.getWorkIdStrategy().getMaxWorkerIdNum()))
                | workerId << CommonUtil.bitUp(snowFlakeProperties.getMaxTpsNum())
                | sequence;
    }
}
