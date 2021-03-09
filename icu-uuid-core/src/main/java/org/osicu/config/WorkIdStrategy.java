package org.osicu.config;

import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.util.StringUtils;

import javax.validation.constraints.Min;
import java.util.Objects;

/**
 * @author chendatao
 * {@code ips} 和{@code zk } 互斥，有且只能有一个
 */
public class WorkIdStrategy implements IdProperties {
    /**
     * ip串
     * 例子：127.0.0.1,127.0.0.2,#,127.0.0.4
     * note:如果历史用过某个ip 后期下架 需要代替为# 否则 后面的ip对应的workerId会变 最终导致ID有可能重复
     */
    private Ips ips;
    /**
     * zookeeper的配置
     */
    @NestedConfigurationProperty
    private ZkProperties zk;
    /**
     * 最大机器数
     * 影响ID的长度 请合理赋值
     * 越大 ID长度越大 建议根据自己的业务量来评估 同一个{@link IdConfigProperties#systemCode }下 一台机器占一个
     * 默认最大 256 台
     * 推荐2的幂次方
     */
    @Min(1)
    private int maxWorkerIdNum = 1 << 8;

    @Override
    public void checkProperties() throws IllegalArgumentException {
        if (StringUtils.isEmpty(ips) == Objects.isNull(zk)) {
            throw new IllegalArgumentException("WorkIdStrategy.ips 和WorkIdStrategy.zk 有且只能有一个 ");
        }
        if (Objects.nonNull(ips)) {
            String[] split = ips.getValue().split(Ips.SEPARATOR);
            if (split.length > maxWorkerIdNum) {
                throw new IllegalArgumentException("ip串长度超限，最大" + maxWorkerIdNum);
            }
            ips.checkProperties();
        }
    }

    public Ips getIps() {
        return ips;
    }

    public void setIps(Ips ips) {
        this.ips = ips;
    }

    public ZkProperties getZk() {
        return zk;
    }

    public void setZk(ZkProperties zk) {
        this.zk = zk;
    }

    public int getMaxWorkerIdNum() {
        return maxWorkerIdNum;
    }

    public void setMaxWorkerIdNum(int maxWorkerIdNum) {
        this.maxWorkerIdNum = maxWorkerIdNum;
    }
}
