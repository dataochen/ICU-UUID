package org.osicu.impl;

import org.osicu.IdGenerateInterface;
import org.osicu.config.SnowFlakeProperties;

/**
 * @author osicu
 * @Description 推特的雪花算法 适用于大部分系统
 * 64位二级制数字
 * 0-00000000 00000000 00000000 00000000 00000000 0-00000000 00 -00000000 0000
 * 第一位不用 代表为正数
 * 第二位到第42位 共41位用于时间戳
 * 第43位到52位 共10位用于记录工作机器id
 * 第53位到64位 共12位用于记录序列号
 * <p>
 * 63位二级制转换为long的最大值为java.lang.Long#MAX_VALUE 9223372036854775807L 19位数字
 * 41位二级制时间戳最大可记录4.398046511103E12毫秒 如果遵循unix时间戳定义从1970.1.1 08:00:00算起可到2109/5/15 15:35:11
 * 10位二级制工作机器id 可以支持2的10次方 1024台机器
 * 12位序列号 4096个序列号 如果一毫秒内超过4096个 阻塞等待下一毫秒
 * <p>
 * 如果极限的话 一毫秒2个 一秒2000单号已经可以满足大量系统了 时间戳37位 17年 工作机器id 9位 512个 序列号1位 2个  那么最低需要37+9+1=47位 最大值2^47=1.40737488355328E14 转换成16进制 12位
 * @date: 2020/11/3 15:23
 * @since 2020年12月22日17:22:42 已支持时间回拨 短时间回拨 直接阻塞等待
 * <p>
 * todo
 * 1.各位置位数可配置化
 * 2.时钟回拨优化方案：（需要持久化机器时间，可以本地磁盘或zk记录）
 * 2.1 短时间回拨 直接阻塞等待
 * 2.2 短时间回拨 继续追加毫秒级别下的序列号，如果没有可用的阻塞下一毫秒
 * 2.3 长时间回拨 继续追加毫秒级别下的序列号，如果没有可用的阻塞下一毫秒
 * 2.4 长时间回拨 报警并自动下线
 * <p>
 * 其他实现方式
 * 1.10进制 20位长度
 * 2.10进制 12位短单号 时间控制在天 ，本地atomoicLong自增（每天限制总数），重启应用时（atomoicLong=0） 加上天前缀
 * 3.
 */
public abstract class AbstractSnowFlakeImpl implements IdGenerateInterface {
    private SnowFlakeProperties snowFlakeProperties;


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

    /**
     * 获取当前服务器的唯一id
     *
     * @return
     */
    protected abstract long getWorkerId() throws Exception;

    /**
     * 传参
     *
     * @return
     */
    protected abstract SnowFlakeProperties getSnowFlakeProperties();


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
        SnowFlakeProperties snowFlakeProperties = getSnowFlakeProperties();
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
        if (0 == startTime) {
            // TODO: 2021/2/7 默认时间优化
            startTime = 1606216408000L;
        }
        long l = currentTimeMillis - startTime;
        if (l > Math.pow(2, 37)) {
            throw new IllegalArgumentException("时间超限了,只能用17年，注意你配置的开始时间");
        }
        long id = (currentTimeMillis - startTime) << (1 + 9) | workerId << 1 | sequence;
        return Long.toHexString(id);

    }

    private void checkTimeBack() {

    }
}
