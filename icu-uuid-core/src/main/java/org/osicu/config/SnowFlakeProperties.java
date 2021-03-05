package org.osicu.config;

import javax.validation.constraints.Min;
import java.util.concurrent.TimeUnit;

/**
 * @author osicu
 * @Description 雪花算法 配置
 * @date: 2020/12/1 17:25
 */
public class SnowFlakeProperties extends IdPropertiesImpl {
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
     */
    @Min(0)
    private long maxRollBackTime = 2000L;

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
}
