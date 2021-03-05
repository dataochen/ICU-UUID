package org.osicu.config;

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
     */
    private Ips ips;
    /**
     * zookeeper的配置
     */
    private ZkProperties zk;
    /**
     * 影响ID的长度 请合理赋值
     * 越大 ID长度越大 建议根据自己的业务量来评估 同一个{@link IdConfigProperties#systemCode }下 一台机器占一个
     * 默认最大 256 台
     * 推荐2的幂次方
     */
    @Min(1)
    private int maxIpNum = 1 << 8;

    @Override
    public void checkProperties() throws IllegalArgumentException {
        if (StringUtils.isEmpty(ips) == Objects.isNull(zk)) {
            throw new IllegalArgumentException("WorkIdStrategy.ips 和WorkIdStrategy.zk 有且只能有一个 ");
        }
        if (Objects.nonNull(ips)) {
            String[] split = ips.getValue().split(Ips.SEPARATOR);
            if (split.length > maxIpNum) {
                throw new IllegalArgumentException("ip串长度超限，最大" + maxIpNum);
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

    public int getMaxIpNum() {
        return maxIpNum;
    }

    public void setMaxIpNum(int maxIpNum) {
        this.maxIpNum = maxIpNum;
    }
}
