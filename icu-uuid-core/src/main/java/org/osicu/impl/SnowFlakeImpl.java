package org.osicu.impl;

import org.osicu.IdGenerateInterface;
import org.osicu.config.IdPropertiesImpl;
import org.osicu.config.SnowFlakeProperties;

/**
 * @author chendatao
 */
public class SnowFlakeImpl extends AbstractWorkerId implements IdGenerateInterface {
    private SnowFlakeImpl() {

    }
    public SnowFlakeImpl(SnowFlakeProperties snowFlakeProperties,String systemCode) {
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
    public String nextId() throws Exception {
        return convertId();
    }

    @Override
    public void printRange() {
        // TODO: 2021/3/5  

    }

    /**
     * 或运算 |
     * 0|1=1 通过左移在或运算 实现二级制加算法
     * <p>
     * <p>
     * 位数 4~12位
     *
     * @return
     */
    private synchronized String convertId() throws Exception {
        long currentTimeMillis = System.currentTimeMillis();
        if (lastTimeStamp > currentTimeMillis) {
            long maxRollBackTime = snowFlakeProperties.getMaxRollBackTime();
            if (lastTimeStamp - currentTimeMillis > maxRollBackTime) {
                throw new Exception("时钟回拨了");
            }
            Thread.sleep(maxRollBackTime);
        }
        long workerId = getWorkerId();
        if (workerId >= 512) {
            throw new Exception("workerId 超限了");
        }

        if (lastTimeStamp != currentTimeMillis) {
            sequence = 0L;
            lastTimeStamp = currentTimeMillis;
        } else {
            ++sequence;
        }
        if (sequence >= 2) {
//            等待下一秒
            while (true) {
                long nextTimeMillis = System.currentTimeMillis();
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
        if (l > Math.pow(2, 37)) {
            throw new IllegalArgumentException("时间超限了,只能用17年，注意你配置的开始时间");
        }
        long id = (currentTimeMillis - startTime) << (1 + 9) | workerId << 1 | sequence;
        return Long.toHexString(id);

    }

    @Override
    IdPropertiesImpl convertIdPropertiesImpl() {
        return snowFlakeProperties;
    }

    @Override
    String convertSystemCode() {
        return systemCode;
    }
}
