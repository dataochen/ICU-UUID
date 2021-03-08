package org.osicu.config;

import org.osicu.impl.SnowFlakeImpl;

import javax.validation.constraints.Min;
import java.util.concurrent.TimeUnit;

/**
 * @author osicu
 * @Description 雪花算法 配置
 * @date: 2020/12/1 17:25
 */
public class SnowFlakeProperties extends IdPropertiesBean {
    /**
     * 默认2021/01/01 00:00:00
     */
    @Min(1)
    private long startTime = 1609430400000L;
    /**
     * 最大时间回拨时间
     * 如果超过此时间会报异常 否则阻塞 {@code maxRollBackTime} 时间
     * 单位：毫秒 {@link TimeUnit#MILLISECONDS}
     * 默认2秒
     *
     * 请慎重修改此值 如果过大 可能会阻塞很长时间
     */
    @Min(0)
    private long maxRollBackTime = 2000L;

    /**
     * 单台机器的每秒最大并发数
     * 如果一秒内生成的ID超过此值 将会阻塞 直到下一秒
     * 请合理设置此值 如果过大 ID将会很长
     * {@link SnowFlakeImpl#convertId()}
     * 默认 128
     */
    @Min(1)
    private int maxTpsNum = 1 << 7;

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getMaxRollBackTime() {
        return maxRollBackTime;
    }

    public void setMaxRollBackTime(long maxRollBackTime) {
        this.maxRollBackTime = maxRollBackTime;
    }

    public int getMaxTpsNum() {
        return maxTpsNum;
    }

    public void setMaxTpsNum(int maxTpsNum) {
        this.maxTpsNum = maxTpsNum;
    }
}
